package com.oc.core.listener;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Description: 通信异常监听处理
 * @author chuangyeifang
 * @createDate 2020年7月20日
 * @version v 1.0
 */
public interface ExceptionListener {

	/**
	 * 捕获连接异常
	 * @param ctx
	 * @param cause
	 * @return
	 */
	boolean exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

	/**
	 * 捕获连接异常
	 * @param ctx
	 * @param errorMsg
	 */
	void exceptionCaught(ChannelHandlerContext ctx, String errorMsg);
	
}
