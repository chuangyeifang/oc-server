package com.oc.core.server;

import com.oc.core.config.SocketConfig;
import com.oc.core.cs.config.CsConfiguration;
import com.oc.core.cs.initializer.CsChannelInitializer;
import com.oc.core.cs.listener.CsConnectionListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Netty Server
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月11日
 * @version v 1.0
 */
public class CsServer implements Server{
	
	private static Logger log = LoggerFactory.getLogger(CsServer.class);

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private CsConfiguration config;
	
	public CsServer() throws UnknownHostException {
		this(new CsConfiguration(InetAddress.getLocalHost().getCanonicalHostName().toLowerCase()));
	}
	
	public CsServer(CsConfiguration config) {
		this.config = config;
	}
	
	/**
	 * 启动 C/S
	 */
	@Override
	public void start() {
		startAsync().syncUninterruptibly();
	}
	
	/**
	 * 同步开启 C/S
	 * @return
	 */
	private Future<Void> startAsync() {

		CsChannelInitializer pipelineFactory = new CsChannelInitializer(config);

		CsConnectionListener serverConnectionListener = new CsConnectionListener(config);
		
		initGroup();
		
		ServerBootstrap serverBootStrap = new ServerBootstrap();
		Class<? extends ServerChannel> channelClass = NioServerSocketChannel.class;
		if (config.isUseLinuxNativeEpoll()) {
			channelClass = EpollServerSocketChannel.class;
		}
		applyConectionOptions(serverBootStrap);
		serverBootStrap.group(bossGroup, workerGroup)
					   .handler(new LoggingHandler(LogLevel.DEBUG))
					   .channel(channelClass)
					   .childHandler(pipelineFactory);

		return serverBootStrap.bind(config.getPort()).addListener(serverConnectionListener);
	}
	
	/**
	 * 初始化 团队信息 boss监听连接 worker具体工作者
	 */
	private void initGroup() {
		if (config.isUseLinuxNativeEpoll()) {
            bossGroup = new EpollEventLoopGroup(config.getBosserThreads());
            workerGroup = new EpollEventLoopGroup(config.getWorkerThreads());
        } else {
            bossGroup = new NioEventLoopGroup(config.getBosserThreads());
            workerGroup = new NioEventLoopGroup(config.getWorkerThreads());
        }
	}
	
	/**
	 * 设置应用连接选项
	 * @param bootstrap
	 */
	private void applyConectionOptions(ServerBootstrap bootstrap) {
		SocketConfig socketConfig = config.getSocketConfig();
		bootstrap.childOption(ChannelOption.TCP_NODELAY, socketConfig.isTcpNoDelay());
		if (socketConfig.getTcpReceiveBufferSize() != -1) {
			bootstrap.childOption(ChannelOption.SO_RCVBUF, socketConfig.getTcpReceiveBufferSize());
			bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(socketConfig.getTcpReceiveBufferSize()));
		}
		if (socketConfig.getTcpSendBufferSize() != -1) {
			bootstrap.childOption(ChannelOption.SO_SNDBUF, socketConfig.getTcpSendBufferSize());
		}
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, socketConfig.isTcpKeepAlive());
		bootstrap.childOption(ChannelOption.SO_LINGER, socketConfig.getSoLinger());
		
		bootstrap.option(ChannelOption.SO_REUSEADDR, socketConfig.isReuseAddress());
		bootstrap.option(ChannelOption.SO_BACKLOG, socketConfig.getAcceptBackLog());
	}

	/**
	 * 停止C/S
	 */
	@Override
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		log.info("C/S is stopped.");
	}

	/**
	 * 获取C/S 监听端口
	 * @return
	 */
	@Override
	public int getPort() {
		return config.getPort();
	}
	
	/**
	 * 获取 C/S Host Name
	 * @return
	 */
	@Override
	public String getHostName() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
		} catch (UnknownHostException e) {
			log.warn( "Unable to determine local hostname.", e );
            return "localhost";
		}
	}
}
