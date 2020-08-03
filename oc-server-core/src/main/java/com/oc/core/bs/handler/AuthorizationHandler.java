package com.oc.core.bs.handler;

import com.oc.exception.BsAuthorizeException;
import com.oc.core.OcImServer;
import com.oc.core.bs.config.Configuration;
import com.oc.core.bs.oauth.CertificationDispatch;
import com.oc.core.coder.PacketDecoder;
import com.oc.message.Packet;
import com.oc.message.type.PacketType;
import com.oc.message.type.Transport;
import com.oc.scheduler.SchedulerKey;
import com.oc.scheduler.SchedulerKey.SchedulerType;
import com.oc.util.http.HttpRequests;
import com.oc.util.http.HttpResponses;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月31日
 * @version v 1.0
 */
@Sharable
public class AuthorizationHandler extends ChannelInboundHandlerAdapter{
	
	private final static Logger log = LoggerFactory.getLogger(AuthorizationHandler.class);
	public final static String WEBSOCKET_IO = "/ws.io";
	public final static String POLLING_IO = "/poll.io";

	private final static String PARAM_PACKET = "packet";
	
	private final Configuration config;
	private final PacketDecoder decoder;
	private CertificationDispatch certificationDispatch;

	public AuthorizationHandler(Configuration config, PacketDecoder decoder) {
		this.config = config;
		this.decoder = decoder;
		certificationDispatch = new CertificationDispatch(config);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) {
		// 处理HTTP poll 和 Web Socket 请求
		Channel channel = ctx.channel();
		SchedulerKey key = new SchedulerKey(SchedulerType.PING_TIMEOUT, channel);
		OcImServer.getInst().getScheduler().cancel(key);
		
		if (object instanceof FullHttpRequest) {
			Packet packet = null;
			FullHttpRequest req = (FullHttpRequest)object;

			// 保证成功序列化
			if (!req.decoderResult().isSuccess()) {
				HttpResponses.sendError(channel, HttpResponseStatus.BAD_REQUEST);
				releaseFullHttpRequest(req);
				return;
			}
			// 只接受 GET/POST 请求
			if (req.method() != HttpMethod.GET && req.method() != HttpMethod.POST) {
				HttpResponses.sendError(channel, HttpResponseStatus.METHOD_NOT_ALLOWED);
				releaseFullHttpRequest(req);
				return;
			}

			String ip = req.headers().get("X-Real-IP");
			QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri());
			String origin = req.headers().get(HttpHeaderNames.ORIGIN);
			channel.attr(EncoderHandler.ORIGIN).set(origin);
			String userAgent = req.headers().get(HttpHeaderNames.USER_AGENT);
			channel.attr(EncoderHandler.USER_AGENT).set(userAgent);
			Map<String, List<String>> parameters = queryDecoder.parameters();
			String path = queryDecoder.path();
			// 是否允许访问
			if(!config.isAllowCustomRequests()) {
				HttpResponses.sendBadRequestError(channel);
				releaseFullHttpRequest(req);
				return;
			}
			// 是否有可读参数 packet
			String msg = HttpRequests.getRequestParam(parameters, PARAM_PACKET);
			if (null == msg || msg.trim().length() == 0) {
				log.warn("无法解析当前请求 ip：{}, path: {}, parameters : {}", ip, path, parameters);
				HttpResponses.sendBadRequestError(channel);
				releaseFullHttpRequest(req);
				return;
			}
			// 处理消息 packet
			try {
				packet = decoder.decodePackets(msg, Packet.class);
				Transport ts = packet.getTs();
				if (path.startsWith(WEBSOCKET_IO) && ts == Transport.WEBSOCKET) {
					certificationDispatch.handlerWebSocket(ctx, packet, req);
					return;
				}
				if (path.startsWith(POLLING_IO) && ts == Transport.POLLING) {
					// 用户认证
					if (packet.getType() == PacketType.AUTH) {
						certificationDispatch.handlerHttp(ctx, packet, req);
					} else {
						ctx.fireChannelRead(packet);
					}
					releaseFullHttpRequest(req);
					return;
				}
				log.warn("无法解析请求协议 ip：{}, path: {}, parameters : {}", ip, path, parameters);
				HttpResponses.sendBadRequestError(channel);
				releaseFullHttpRequest(req);
			} catch (BsAuthorizeException e) {
				log.warn("ip：{} 认证失败：{} ", ip, e.getMessage());
				HttpResponses.sendUnauthorizedError(channel);
				releaseFullHttpRequest(req);
			} catch(Exception e) {
				log.error("ip：{} Packet: {} B/S处理消息失败：{}", ip, packet, e.getMessage());
				HttpResponses.sendUnauthorizedError(channel);
				releaseFullHttpRequest(req);
			}
		} else {
			//transport websocket
			ctx.fireChannelRead(object);
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		// 如果处理事件超过 FirstDataTimeout，则关闭Channel
		Channel channel = ctx.channel();
		SchedulerKey key = new SchedulerKey(SchedulerType.PING_TIMEOUT, channel);
		OcImServer.getInst().getScheduler().scheduler(key, () -> {
			channel.close();
			log.error("当前请求处理业务逻辑 5秒内未完成，关闭通讯");
		}, config.getFirstDataTimeout(), TimeUnit.MILLISECONDS);
	}

	private void releaseFullHttpRequest(FullHttpRequest req) {
		if (null != req) {
			ReferenceCountUtil.release(req);
		}
	}

}
