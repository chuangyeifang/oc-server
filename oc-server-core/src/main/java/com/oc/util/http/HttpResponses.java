package com.oc.util.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月7日
 * @version v 1.0
 */
public final class HttpResponses {
	
	public static void sendBadRequestError(Channel channel) {
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void sendError(Channel channel, HttpResponseStatus httpResponseStatus) {
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
	}
	
	public static void sendUnauthorizedError(Channel channel) {
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
		channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
	}
	
	@SuppressWarnings("unused")
	public static void sendSuccess(Channel channel) {
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
	}
}
