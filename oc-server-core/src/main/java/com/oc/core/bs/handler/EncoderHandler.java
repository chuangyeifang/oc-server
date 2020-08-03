/**
 * 
 */
package com.oc.core.bs.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.oc.core.bs.config.Configuration;
import com.oc.core.bs.message.Message;
import com.oc.core.bs.message.OutPacketMessage;
import com.oc.core.bs.message.PollErrorMessage;
import com.oc.core.bs.message.PollOkMessage;
import com.oc.core.bs.message.PongPacketMessage;
import com.oc.core.coder.PacketEncoder;
import com.oc.message.Packet;
import com.oc.message.type.Transport;
import com.oc.session.Session;

import com.oc.session.store.TransportStore;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@Sharable
public class EncoderHandler extends ChannelOutboundHandlerAdapter{
	
	public static final AttributeKey<String> ORIGIN = AttributeKey.valueOf("origin");
	public static final AttributeKey<String> USER_AGENT = AttributeKey.valueOf("userAgent");
	
	private static final byte[] OK = "{\"ret\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
	private static final byte[] ERROR = "{\"ret\":\"error\"}".getBytes(StandardCharsets.UTF_8);
	
	private final Configuration config;
	private final PacketEncoder encoder;
	
	private String version;
	
	public EncoderHandler(Configuration config, PacketEncoder encoder) {
		this.config = config;
		this.encoder = encoder;
	}
	
	/**
	 * @param ctx
	 * @param msg
	 * @param promise
	 * @throws Exception
	 */
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (!(msg instanceof Message)) {
			super.write(ctx, msg, promise);
			return;
		}
		
		if (msg instanceof OutPacketMessage) {
			OutPacketMessage outMessage = (OutPacketMessage)msg;
			if (outMessage.getTransport() == Transport.WEBSOCKET) {
				handleWebsocket(outMessage, ctx, promise);
			} else if (outMessage.getTransport() == Transport.POLLING) {
				handleHttp(outMessage, ctx, promise);
			}
		} else if (msg instanceof PongPacketMessage) {
			write((PongPacketMessage)msg, ctx, promise);
		} else  if (msg instanceof PollOkMessage) {
			write((PollOkMessage)msg, ctx, promise, HttpResponseStatus.OK);
		} else if (msg instanceof PollErrorMessage) {
			write((PollErrorMessage)msg, ctx, promise);
		}
	}
	
	private void write(PongPacketMessage msg, ChannelHandlerContext ctx, ChannelPromise promise) throws IOException {
		ByteBuf out = encoder.allocateBuffer(ctx.alloc());
		encoder.encodePacket(msg.getPacket(), out, ctx.alloc());
		
		HttpResponse response = bulidHttpResponse(HttpResponseStatus.OK);
		sendPacket(ctx, out, "application/json", promise, response);
	}

	private void handleWebsocket(OutPacketMessage outPacketMessage, ChannelHandlerContext ctx, ChannelPromise promise) throws IOException {
		CustomChannelFutureListener cfl = new CustomChannelFutureListener();
		Session session = outPacketMessage.getSession();
		TransportStore store = session.transportStore();
		
		if (store.getTransport() != Transport.WEBSOCKET) {
			return;
		}
		Queue<Packet> packets = store.getPacketsQueue();
		
		while (true) {
			Packet packet = packets.poll();
			if (packet == null) {
				cfl.setPromise(promise);
				break;
			}
			
			ByteBuf out = encoder.allocateBuffer(ctx.alloc());
			encoder.encodePacket(packet, out, ctx.alloc());
			
			WebSocketFrame res = new TextWebSocketFrame(out);
			if (out.isReadable()) {
				cfl.add(ctx.channel().writeAndFlush(res));
            } else {
                out.release();
            }
		}
	}
	
	private void handleHttp(OutPacketMessage wsMessage, ChannelHandlerContext ctx, ChannelPromise promise) throws IOException {
		Channel channel = ctx.channel();
		Session session = wsMessage.getSession();

		TransportStore store = session.transportStore();
		if (store.getTransport() != Transport.POLLING) {
			return;
		}
		
		Queue<Packet> packetsQueue = store.getPacketsQueue();
		if (packetsQueue.isEmpty() || !channel.isActive()) {
			promise.trySuccess();
			return;
		}
		
		ByteBuf out = encoder.allocateBuffer(ctx.alloc());
		encoder.encodePackets(packetsQueue, out, ctx.alloc(), 50);
		
		HttpResponse response = bulidHttpResponse(HttpResponseStatus.OK);
		sendPacket(ctx, out, "application/json", promise, response);
	}
	
	private void write(PollErrorMessage pollMsg, ChannelHandlerContext ctx, ChannelPromise promise) {
		ByteBuf out = encoder.allocateBuffer(ctx.alloc());
		out.writeBytes(ERROR);
		HttpResponse response = bulidHttpResponse(HttpResponseStatus.OK);
		sendPacket(ctx, out, "application/json", promise, response);
	}
	
	private void write(PollOkMessage okMessage, ChannelHandlerContext ctx, ChannelPromise promise, HttpResponseStatus status) {
		ByteBuf out = encoder.allocateBuffer(ctx.alloc());
		out.writeBytes(OK);
		HttpResponse response = bulidHttpResponse(HttpResponseStatus.OK);
		sendPacket(ctx, out, "application/json", promise, response);
	}
	
	private void sendPacket(ChannelHandlerContext ctx, ByteBuf out, String type,
			ChannelPromise promise, HttpResponse response) {
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, type).add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		
		String origin = ctx.channel().attr(ORIGIN).get();
		
		addOriginHeaders(origin, response);
		
		String userAgent = ctx.channel().attr(EncoderHandler.USER_AGENT).get();
		if (userAgent != null && (userAgent.contains(";MSIE") || userAgent.contains("Trident/"))) {
			response.headers().add("X-XSS-Protection", "0");
		}
		
		HttpUtil.setContentLength(response, out.readableBytes());
		
		sendPacket(ctx.channel(), out, response, promise);
	}

	private void sendPacket(Channel channel, ByteBuf out, HttpResponse resq, ChannelPromise promise) {
		
		channel.write(resq);
		
		if (out.isReadable()) {
			channel.write(new DefaultHttpContent(out));
		} else {
			out.release();
		}
		channel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT, promise).addListener(ChannelFutureListener.CLOSE);
	}
	
	private void addOriginHeaders(String origin, HttpResponse res) {
        if (version != null) {
            res.headers().add(HttpHeaderNames.SERVER, version);
        }

        if (config.getOrigin() != null) {
            res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, config.getOrigin());
            res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE);
        } else {
            if (origin != null) {
                res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE);
            } else {
                res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            }
        }
    }
	
	private HttpResponse bulidHttpResponse(HttpResponseStatus status) {
		return new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
	}

	private class CustomChannelFutureListener implements GenericFutureListener<Future<Void>> {

		private List<ChannelFuture> futureList = new ArrayList<>();
		private ChannelPromise promise = null;
		
		private void clearup() {
			promise = null;
			for (ChannelFuture cf : futureList) {
				cf.removeListener(this);
			}
		}
		
		private void validate() {
			boolean allSuccess = true;
			for (ChannelFuture cf : futureList) {
				if (cf.isDone()) {
					if (!cf.isSuccess()) {
						promise.tryFailure(cf.cause());
						clearup();
						return;
					}
				} else {
					allSuccess = false;
				}
			}
			if (allSuccess) {
				promise.trySuccess();
				clearup();
			}
		}
		
		public void setPromise(ChannelPromise promise) {
			this.promise = promise;
			validate();
		}
		
		public void add(ChannelFuture cf) {
			futureList.add(cf);
			cf.addListener(this);
		}

		@Override
		public void operationComplete(Future<Void> future) throws Exception {
			if (null != promise) {
				validate();
			}
		}
	}
}
