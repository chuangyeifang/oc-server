package com.oc.core.bs.handler;

import com.oc.exception.BsAuthorizeException;
import com.oc.core.OcImServer;
import com.oc.core.bs.message.OutPacketMessage;
import com.oc.core.bs.message.PollOkMessage;
import com.oc.core.bs.message.PongPacketMessage;
import com.oc.core.chain.ChainFactory;
import com.oc.message.AddressFrom;
import com.oc.message.Packet;
import com.oc.message.type.PacketType;
import com.oc.message.type.Transport;
import com.oc.scheduler.SchedulerKey;
import com.oc.scheduler.SchedulerKey.SchedulerType;
import com.oc.session.CustomerSession;
import com.oc.util.http.HttpResponses;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@Sharable
public class PollingTransport extends ChannelInboundHandlerAdapter {

	private Logger log = LoggerFactory.getLogger(PollingTransport.class);

	public final static Transport TRANSPORT = Transport.POLLING;

	private final static Long POLL_CONNECT_MILLISECOND = 60 * 1000L;

	public PollingTransport() { }

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		Channel channel = ctx.channel();
		if (msg instanceof Packet) {
 			Packet packet = (Packet) msg;
			Transport ts = packet.getTs();
			if (TRANSPORT == ts) {
				try {
					switch (packet.getType()) {
						case POLL:
							onPoller(channel, packet);
							break;
						case MESSAGE:
							onSender(channel, packet);
							break;
						case PING:
							pong(channel, packet);
							break;
						case CLOSE:
							close(channel, packet);
							break;
						default:
							HttpResponses.sendBadRequestError(channel);
							log.warn("客户发送的消息，不符合规范。 packet : {}", packet);
					}
				} catch (Exception e) {
					log.error("处理POLLING packet: {} 消息错误。", packet, e);
					HttpResponses.sendBadRequestError(channel);
				} finally {
					ReferenceCountUtil.release(msg);
				}
			} else {
				ctx.fireChannelRead(msg);
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private void onSender(Channel channel, Packet packet) {
		AddressFrom from = packet.getFrom();
		CustomerSession customerSession = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(from.getUid());
		if (customerSession == null) {
			sendError(channel);
			log.error("检测用户：{}， 会话不存在", packet.getCid());
			return;
		}
		channel.writeAndFlush(new PollOkMessage());
		ChainFactory.getInst().dispatcherCustomer(customerSession, packet);
	}
	
	private void onPoller(Channel channel, Packet packet) throws BsAuthorizeException {
		AddressFrom from = packet.getFrom();
		CustomerSession session = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(from.getUid());
		if (null == session) {
			throw new BsAuthorizeException("未获取到当前用户状态.");
		}
		session.bindChannel(channel);
		//保证在重连过程中，消息有缓存
		channel.writeAndFlush(new OutPacketMessage(session));
		SchedulerKey key = new SchedulerKey(SchedulerType.PING_TIMEOUT, channel);
		OcImServer.getInst().getScheduler().cancel(key);
		OcImServer.getInst().getScheduler().scheduler(key, () -> channel.writeAndFlush(new PollOkMessage()), POLL_CONNECT_MILLISECOND, TimeUnit.MILLISECONDS);
	}
	
	private void close(Channel channel, Packet packet) {
		AddressFrom from = packet.getFrom();
		CustomerSession customerSession = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(from.getUid());
		if (null == customerSession) {
			log.info("客户:{}, 已经离开不需要再次处理", from.getUid());
		} else {
			customerSession.disconnect();
			channel.close();
			log.info("客户: {}, Http-close 直接关闭会话", from.getUid());
		}
	}

	private void sendError(Channel channel) {
		HttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		channel.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
	}

	private void pong(Channel channel, Packet packet) {
		Packet pongPacket = new Packet(PacketType.PONG);
		pongPacket.setFrom(packet.getFrom());
		channel.writeAndFlush(new PongPacketMessage(pongPacket));
	}
}
