package com.oc.core.cs.handler;

import com.oc.core.chain.ChainFactory;
import com.oc.core.heart.HeartDetector;
import com.oc.core.listener.ExceptionListener;
import com.oc.message.Packet;
import com.oc.message.type.PacketType;
import com.oc.session.LocalCustomerSession;
import com.oc.session.LocalWaiterSession;
import com.oc.session.Session;
import com.oc.session.WaiterSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 处理消息
 * @author chuangyeifang
 * @createDate 2020年7月11日
 * @version v 1.0
 */
@Sharable
public class SocketHandler extends SimpleChannelInboundHandler<Packet>{
	
	private static Logger log = LoggerFactory.getLogger(SocketHandler.class);

	private ExceptionListener exceptionListener;
	
	public SocketHandler(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
		try {
			if (packet.getType() == PacketType.PING) {
				HeartDetector.pongCs(ctx.channel());
				return;
			}
			Session session = ctx.channel().attr(Session.CLIENT_SESSION).get();
			if (session instanceof WaiterSession) {
				ChainFactory.getInst().dispatcherWaiter((WaiterSession)session, packet);
			}
		} finally {
			ReferenceCountUtil.release(packet);
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Session session = channel.attr(Session.CLIENT_SESSION).get();
		if (null != session) {
			session.disconnect();
			channel.close();
			if (session instanceof LocalCustomerSession) {
				log.info("客户：{}， 登出成功。", session.getUid());
			} else if (session instanceof LocalWaiterSession) {
				log.info("客服：{}， 登出成功。", session.getUid());
			} else {
				log.info("未知用户：{}， 登出异常", session.getUid());
			}
		} else {
			log.warn("当前用户状态有误。");
		}
		super.channelInactive(ctx);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (!exceptionListener.exceptionCaught(ctx, cause)) {
			super.exceptionCaught(ctx, cause);
		}
	}
}
