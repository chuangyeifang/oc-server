/**
 * 
 */
package com.oc.core.cs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oc.core.listener.ExceptionListener;
import com.oc.session.Session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月20日
 * @version v 1.0
 */
public class DefaultExceptionListener implements ExceptionListener{
	
	private final static Logger log = LoggerFactory.getLogger(DefaultExceptionListener.class);
	
	public DefaultExceptionListener() {}

	@Override
	public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Channel channel = ctx.channel();
		Session session = getCurrentSession(channel);
		if (null == session) {
			log.warn("未知用户强制关闭连接， 异常信息为:{}.", cause);
			return true;
		}
		log.warn("uid:{}, 当前用户强制关闭连接， 异常信息为：{}.", session.getUid(), cause);
		return true;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, String errorMsg) {
		
		Channel channel = ctx.channel();
		Session session = getCurrentSession(channel);
		if (null == session) {
			return;
		}
		
		session.disconnect();
		channel.close();
		
		log.error("uid:{}, 连接发生异常:{}.", session.getUid(), errorMsg);
    }
	
	private Session getCurrentSession(Channel channel) {
		return channel.attr(Session.CLIENT_SESSION).get();
	}
}
