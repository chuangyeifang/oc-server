package com.oc.core.bs.handler;

import com.oc.core.bs.config.Configuration;
import com.oc.core.bs.initializer.BsChannelInitializer;
import com.oc.core.chain.*;
import com.oc.core.coder.PacketDecoder;
import com.oc.core.coder.PacketEncoder;
import com.oc.core.heart.HeartDetector;
import com.oc.message.Packet;
import com.oc.message.type.PacketType;
import com.oc.message.type.Transport;
import com.oc.session.CustomerSession;
import com.oc.session.LocalCustomerSession;
import com.oc.session.Session;
import com.oc.session.WaiterSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@Sharable
public class WebsocketTransport extends ChannelInboundHandlerAdapter{

	private final static Logger log = LoggerFactory.getLogger(WebsocketTransport.class);

	private static final String HEART = "heart";
	private final static Transport TRANSPORT = Transport.WEBSOCKET;

	private final boolean isSsl;
	private final Configuration config;
	private PacketDecoder decoder;
	private PacketEncoder encoder;

	public WebsocketTransport(Configuration config, boolean isSsl, PacketEncoder encoder, PacketDecoder decoder) {
		this.isSsl = isSsl;
		this.config = config;
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		Channel channel = ctx.channel();
		Session session = getChannelSession(channel);
		if(null == session) {
			ReferenceCountUtil.release(obj);
			ctx.channel().close();
			throw new IllegalAccessException("无效的客户端连接，服务器关闭与当前客户端连接");
		}
		if (obj instanceof CloseWebSocketFrame) {
			if(session instanceof LocalCustomerSession) {
				log.info("客户主动关闭当前会话 Uid: {}", session.getUid());
			} else {
				log.info("客服退出登录 Uid: {}", session.getUid());
			}
			ReferenceCountUtil.release(obj);
		} else if (obj instanceof TextWebSocketFrame) {
			TextWebSocketFrame frame = (TextWebSocketFrame)obj;
			try {
				resolverTextWebSocketFrame(session, channel, frame.text());
			} finally {
				frame.release();
			}
		} else if (obj instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest)obj;
			try {
				if (session.getTransport()== TRANSPORT) {
	                upgradeWebSocket(ctx, session, request);
	            } else {
	                ctx.channel().close();
	                throw new IllegalAccessException("不支持的通讯协议: " + session.getTransport());
				}
			} finally {
				request.release();
			}
		} else {
			ctx.fireChannelRead(obj);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		Session session = getChannelSession(ctx.channel());
        if (session != null) {
            ctx.flush();
        } else {
            super.channelReadComplete(ctx);
        }
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		Session session = getChannelSession(channel);
		if(null != session) {
			if(session instanceof LocalCustomerSession) {
				log.info("执行客户关闭操作 Uid: {}", session.getUid());
			} else {
				log.info("执行客服关闭操作 Uid: {}", session.getUid());
			}
			dealCloseWebSocketFrame(channel, session);
		} else {
			channel.close();
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		Channel channel = ctx.channel();
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleEvt = (IdleStateEvent)evt;
			if (idleEvt.state() == IdleState.READER_IDLE) {
				Session session = getChannelSession(channel);
				if (null != session) {
					ctx.channel().close();
					log.warn("uid:{}, 客户端长时间未响应，服务器关闭当前连接", session.getUid());
				}
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Channel channel = ctx.channel();
		Session session = getChannelSession(channel);
		if (null != session) {
			if(session instanceof LocalCustomerSession) {
				log.info("客户会话被异常关闭 Uid: {}", session.getUid());
			} else {
				log.info("客服退出被异常关闭 Uid: {}", session.getUid());
			}
		}
		ctx.channel().close();
	}

	/**
	 * 关闭当前会话
	 * @param channel
	 * @param session
	 * @return
	 * @throws IllegalAccessException
	 */
	private void dealCloseWebSocketFrame(Channel channel, Session session) {
		synchronized (session.getUid()) {
			session.disconnect();
		}
	}

	/**
	 * 处理消息
	 * @param session
	 * @param channel
	 */
	private void resolverTextWebSocketFrame(Session session, Channel channel, String message) {
		Packet packet = null;
		try {
			packet = decoder.decodePackets(message, Packet.class);
			if (packet.getType() == PacketType.PING) {
				HeartDetector.pongBs(session);
				return;
			}
			//客服消息
			if (session instanceof WaiterSession) {
				ChainFactory.getInst().dispatcherWaiter((WaiterSession) session, packet);
				if (log.isInfoEnabled()) {
					log.info("客服消息 packet：{}", packet);
				}
			} else if (session instanceof CustomerSession) {
				ChainFactory.getInst().dispatcherCustomer((CustomerSession)session, packet);
				if (log.isInfoEnabled()) {
					log.info("客户消息 packet：{}", packet);
				}
			} else {
				log.error("无法分发的消息：{}", packet);
			}
		} catch (Exception e) {
			log.warn("packet:{} 解析数据发生错误", packet);
			e.printStackTrace();
		}
	}

	/**
	 * HTTP 升级  Web socket
	 * @param ctx
	 * @param req
	 */
	private void upgradeWebSocket(ChannelHandlerContext ctx, Session session, FullHttpRequest req) {
		final Channel channel = ctx.channel();
		WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(getWebsocketUrl(req),
				null, true, config.getMaxFramePayloadLength());
		WebSocketServerHandshaker handshake = factory.newHandshaker(req);

		if (handshake != null) {
			handshake.handshake(channel, req, getResponseHeaders(), ctx.newPromise()).addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					channel.pipeline().addFirst(HEART, new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
					channel.pipeline().addBefore(BsChannelInitializer.WEB_SOCKET_TRANSPORT,
							BsChannelInitializer.WEB_SOCKET_AGGREGATOR,
							new WebSocketFrameAggregator(config.getMaxFramePayloadLength()));
				} else {
					log.error("uid: {} 握手失败：{} ", session.getUid(), future.cause());
				}
			});
		} else {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
		}
	}

	/**
	 * 获取当前Session
	 * @param channel
	 * @return
	 */
	private Session getChannelSession(Channel channel) {
		return channel.attr(Session.CLIENT_SESSION).get();
	}

	/**
	 * 获取协议
	 * @param req
	 * @return
	 */
	private String getWebsocketUrl(HttpRequest req) {
		String protocol = "ws://";
		if (isSsl) {
			protocol = "wss://";
		}
		return protocol + req.headers().get(HttpHeaderNames.HOST) + req.uri();
	}
	/**
	 * @return
	 * @throws IOException
	 */
	private HttpHeaders getResponseHeaders() {
        return new DefaultHttpHeaders();
    }
}
