/**
 * 
 */
package com.oc.core.cs.initializer;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.oc.core.coder.JsonSupport;
import com.oc.core.coder.PacketDecoder;
import com.oc.core.coder.PacketEncoder;
import com.oc.core.cs.config.CsConfiguration;
import com.oc.core.cs.handler.AuthorizationHandler;
import com.oc.core.cs.handler.DecoderHandler;
import com.oc.core.cs.handler.EncoderHandler;
import com.oc.core.cs.handler.SocketHandler;
import com.oc.core.listener.ExceptionListener;
import com.oc.sasl.SASLAuthentication;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月11日
 * @version v 1.0
 */
public class CsChannelInitializer extends ChannelInitializer<Channel>{

	private static String SSL_HANDLER = "ssl";
	private static String AUTHORIZE_HANDLER = "authorizationHandler";
	private static String MESSAGE_HANDLER = "messageHandle";
	private static String ENCODER_HANDLER = "encoderHandler";
	private static String DECODER_HANDLER = "decoderHandler";
	private static String HEART = "heart";
	
	private SSLContext sslContext;
	private SASLAuthentication saslAuthentication;
	
	private CsConfiguration serverConfig;
	
	private SocketHandler serverSocketHandler;
	private AuthorizationHandler authorizationHandler;
	private EncoderHandler encoderHandler;
	
	private JsonSupport jsonSupport;
	
	private ExceptionListener exceptionListener;
	
	private PacketEncoder encoder;
	private PacketDecoder decoder;
	
	public CsChannelInitializer(CsConfiguration serverConfig) {
		this.serverConfig = serverConfig;
		init();
	}
	
	private void init() {
		
		jsonSupport = serverConfig.getJsonSupport();
		encoder = new PacketEncoder(true, jsonSupport);
		decoder = new PacketDecoder(jsonSupport);
		encoderHandler = new EncoderHandler(encoder);
		sslContext = null;
		saslAuthentication = new SASLAuthentication(serverConfig);
		exceptionListener = serverConfig.getExceptionListener();
		serverSocketHandler = new SocketHandler(exceptionListener);
		authorizationHandler = new AuthorizationHandler(saslAuthentication, jsonSupport, exceptionListener);
	}

	/**
	 * @param arg0
	 * @throws Exception
	 */
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		addSslHandler(pipeline);
		addSocketIoHandlers(pipeline);
		
	}
	
	protected void addSslHandler(ChannelPipeline pipeline) {
		if (sslContext != null) {
			SSLEngine engine = sslContext.createSSLEngine();
			engine.setUseClientMode(false);
			pipeline.addLast(SSL_HANDLER, new SslHandler(engine));
		}
	}
	
	protected void addSocketIoHandlers(ChannelPipeline pipeline) {
		pipeline.addLast(HEART, new IdleStateHandler(90, 0, 0, TimeUnit.SECONDS));
		pipeline.addLast(DECODER_HANDLER, new DecoderHandler(decoder));
		pipeline.addLast(AUTHORIZE_HANDLER, authorizationHandler);
		pipeline.addLast(MESSAGE_HANDLER, serverSocketHandler);
		pipeline.addLast(ENCODER_HANDLER, encoderHandler);
	}
}
