/**
 * 
 */
package com.oc.core.cs.handler;

import com.oc.core.OcImServer;
import com.oc.core.coder.JsonSupport;
import com.oc.core.listener.ExceptionListener;
import com.oc.domain.waiter.Waiter;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.message.type.Transport;
import com.oc.provider.db.WaiterProvider;
import com.oc.sasl.SASLAuthentication;
import com.oc.sasl.SASLAuthentication.SSLStatus;
import com.oc.session.LocalWaiterSession;
import com.oc.session.LoginStatus;
import com.oc.session.LoginStatus.Status;
import com.oc.session.Session;
import com.oc.session.WaiterSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 用户身份认证处理
 * @author chuangyeifang
 * @createDate 2020年7月16日
 * @version v 1.0
 */
@Sharable
public class AuthorizationHandler extends ChannelInboundHandlerAdapter{
	
	private final static Logger log = LoggerFactory.getLogger(AuthorizationHandler.class);

	private SASLAuthentication saslAuthentication;
	private JsonSupport jsonSupport;

	private ExceptionListener exceptionListener;
	
	public AuthorizationHandler(SASLAuthentication saslAuthentication, JsonSupport jsonSupport, ExceptionListener exceptionListener) {
		this.saslAuthentication = saslAuthentication;
		this.jsonSupport = jsonSupport;
		this.exceptionListener = exceptionListener;
	}

	/**
	 * 登录处理器，尚未登录需要登录
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
		if (object instanceof Packet) {
			Channel channel = ctx.channel();
			try {
				Packet packet = (Packet)object;
				Status status = channel.attr(LoginStatus.LOGIN_STATUS).get();
				if (status == Status.UN_LOGIN) {
					authorized(ctx, packet);
				} else {
					ctx.fireChannelRead(packet);
				}
			} catch (Exception e) {
				log.error("处理消息发生异常：" + e);
			} finally {
				ReferenceCountUtil.release(object);
			}
		} else {
			log.warn("仅支持Jackson类型解析：{}", object);
			ReferenceCountUtil.release(object);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(LoginStatus.LOGIN_STATUS).set(LoginStatus.Status.UN_LOGIN);
		super.channelActive(ctx);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		Channel channel = ctx.channel();
		if (evt instanceof IdleStateEvent) {
			
			IdleStateEvent idleEvt = (IdleStateEvent)evt;
			Status stats = channel.attr(LoginStatus.LOGIN_STATUS).get();
			
			if (stats != Status.LOGINED) {
				return;
			}
			
			switch(idleEvt.state()) {
				case READER_IDLE : 
					exceptionListener.exceptionCaught(ctx, "长时间未收到客户端响应, 服务器关闭连接。");
					break;
				default:
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
	/**
	 * 登录相关处理
	 * @param ctx
	 * @param packet
	 * @return
	 */
	private void authorized(ChannelHandlerContext ctx, Packet packet) throws Exception{
		Channel channel = ctx.channel();
		Identity idy = packet.getFrom().getIdy();
		switch (packet.getType()) {
		case AUTH:
			if (idy == Identity.WAITER) {
				SSLStatus status = saslAuthentication.handler(ctx, packet);
				if (status == SSLStatus.FAILED) {
					channel.close();
				}
			} else if (idy == Identity.CUSTOMER) {
				throw new IllegalArgumentException("暂时不支持");
			} else {
				throw new IllegalArgumentException("未知类型");
			}
			break;
		case BIND:
			if (idy == Identity.WAITER) {
				Waiter waiter = channel.attr(SASLAuthentication.WAITER).get();
				if (null == waiter) {
					throw new IllegalArgumentException("绑定用户状态失败，请重试");
				}
				
				String uid = waiter.getWaiterCode();
				WaiterSession session = new LocalWaiterSession(uid, channel, Transport.SOCKET, Identity.WAITER, waiter);
				session.bindRoute();
				//登录成功 回传登录信息
				Body body = new Body(BodyType.SUCCESS, jsonSupport.writeString(waiter));
				Packet bindPacket = new Packet(PacketType.BIND, body);
				
				session.sendPacket(bindPacket).addListener((ChannelFutureListener) future -> {
					if (future.isSuccess()) {
						channel.attr(Session.CLIENT_SESSION).set(session);
						OcImServer.getInst().getDispatcher().login(waiter);
						channel.attr(LoginStatus.LOGIN_STATUS).set(Status.LOGINED);

						// 记录登录日志
						WaiterProvider.getInst().insertWaiterLog(waiter, "1", null);
						log.info("客服：{} 登录成功。", uid);
					}
				});
			}
			break;
		default:
			ReferenceCountUtil.release(packet);
			break;
		}
	}
}
