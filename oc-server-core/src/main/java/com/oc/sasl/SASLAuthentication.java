/**
 * 
 */
package com.oc.sasl;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

import com.oc.domain.waiter.Waiter;
import com.oc.exception.SaslFailureException;
import com.oc.core.cs.config.CsConfiguration;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.PacketType;
import com.oc.sasl.handler.CsCallbackHandler;
import com.oc.sasl.provider.CsSaslProvider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class SASLAuthentication {
	
	public static AttributeKey<SaslServer> SaslServer = AttributeKey.valueOf("SaslServer");
	public static AttributeKey<Waiter> WAITER = AttributeKey.valueOf("waiter");
	
	public final static String MECHANISM = "mechanism";
	public final static String AUTHENTICATION_VALUE = "authenticationValue";
	
	private static CsSaslProvider ocSaslProvider;
	
	private CsConfiguration serverConfig;
	
	public SASLAuthentication(CsConfiguration serverConfig) {
		this.serverConfig = serverConfig;
	}

	static {
		ocSaslProvider = new CsSaslProvider();
		
		Security.addProvider(ocSaslProvider);
	}
	
	public SSLStatus handler(ChannelHandlerContext ctx, Packet packet) {
		try {
			String mechanism = null;
			
			if ("ANONYMOUS".equals(mechanism)) {
				throw new SaslException("ANONYMOUS: 暂不支持");
			}
			
			switch(packet.getBody().getType()) {
				case ABORT:
					throw new SaslFailureException("完成中断.", "停止授权");
				case LOGIN:
					final Map<String, Object> props = new HashMap<>(4);
					props.put(ChannelHandlerContext.class.getCanonicalName(), ctx);
					props.put(Packet.class.getCanonicalName(), packet);
					SaslServer saslServer = Sasl.createSaslServer(mechanism, "json", serverConfig.getHostName(), props, new CsCallbackHandler());
					if (null == saslServer) {
						throw new SaslFailureException("没有找到合适的 SASLServce.", "生成鉴权服务失败");
					}
					ctx.channel().attr(SaslServer).set(saslServer);
					
					if (mechanism.equals(MECHANISM)) {
						packet.getBody().setContent("");
					}
				case RESPONSE:
					saslServer = (SaslServer)ctx.channel().attr(SaslServer).get();
					if (null == saslServer) {
						throw new SaslFailureException("没有找到合适的 SASLServce.", "没有找到鉴权服务");
					}
					
					final String encoded = packet.getBody().getContent();
					
					byte[] challenge = saslServer.evaluateResponse(encoded.getBytes(StandardCharsets.UTF_8));
				
					if (!saslServer.isComplete()) {
						Body body = new Body(BodyType.RESPONSE, new String(challenge, StandardCharsets.UTF_8));
						Packet challengeMessage = new Packet(PacketType.AUTH, body);
						ctx.write(challengeMessage);
						return SSLStatus.NEED_RESPONSE;
					}
					afterLoginSuccessed(ctx);
					return SSLStatus.SUCCESSED;
				default:
					throw new IllegalStateException("不支持此授权机制");
			}
		} catch (Exception e) {
			String msg = "鉴权失败";
			if (e instanceof SaslFailureException) {
				msg = ((SaslFailureException)e).msg;
			}
			afterLoginFailed(ctx, msg);
			return SSLStatus.FAILED;
		}
	}
	
	private void afterLoginSuccessed(ChannelHandlerContext ctx) {
		Body body = new Body(BodyType.SUCCESS, "验证成功");
		Packet packet = new Packet(PacketType.AUTH, body);
		ctx.channel().attr(SaslServer).set(null);
		ctx.channel().writeAndFlush(packet);
	}
	
	private void afterLoginFailed(ChannelHandlerContext ctx, String msg) {
		Body body = new Body(BodyType.FAIL, msg);
		Packet packet = new Packet(PacketType.AUTH, body);
		ctx.channel().attr(SaslServer).set(null);
		ctx.channel().writeAndFlush(packet);
	}
	
	public enum SSLStatus {
		NEED_RESPONSE,
		FAILED,
		SUCCESSED;
	}
}
