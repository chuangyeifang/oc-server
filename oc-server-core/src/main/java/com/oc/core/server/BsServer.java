package com.oc.core.server;

import com.oc.core.bs.config.Configuration;
import com.oc.core.bs.initializer.BsChannelInitializer;
import com.oc.core.config.SocketConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@Slf4j
public class BsServer implements Server{
	
	private Configuration config;
	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

	public BsServer() {
    	this(new Configuration());
	}
    
	public BsServer(Configuration config) {
		this.config = config;
	}
	
	/**
	 * 启动 B/S
	 */
	@Override
	public void start() {
		startAsync().syncUninterruptibly();
	}
	
	/**
	 * 同步开启 B/S
	 * @return
	 */
	private Future<Void> startAsync() {
		initGroups();

		BsChannelInitializer channelInitializer = new BsChannelInitializer(config);
		
		Class<? extends ServerChannel> channelClass = NioServerSocketChannel.class;
		if (config.isUseLinuxNativeEpoll()) {
			channelClass = EpollServerSocketChannel.class;
		}
		
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(bossGroup, workerGroup)
			.channel(channelClass)
			.childHandler(channelInitializer);
		
		applyConnectionOptions(boot);
		return boot.bind(config.getPort()).addListener((FutureListener<Void>) future -> {
			if (future.isSuccess()) {
				log.info("B/S 服务启动成功  监听端口: {}", config.getPort());
			} else {
				log.error("B/S 服务启动失败  监听端口: {}!", config.getPort());
			}
		});
	}
	
	/**
	 * 初始化 团队信息 boss监听连接 worker具体工作者
	 */
	private void initGroups() {
		if (config.isUseLinuxNativeEpoll()) {
			bossGroup = new EpollEventLoopGroup(config.getBossThreads());
			workerGroup = new EpollEventLoopGroup(config.getWorkerThreads());
		} else {
			bossGroup = new NioEventLoopGroup(config.getBossThreads());
			workerGroup = new NioEventLoopGroup(config.getWorkerThreads());
		}
	}
	
	/**
	 * 设置应用连接选项
	 * @param bootstrap
	 */
	protected void applyConnectionOptions(ServerBootstrap bootstrap) {
		SocketConfig socketConfig = config.getSocketConfig();
        bootstrap.childOption(ChannelOption.TCP_NODELAY, socketConfig.isTcpNoDelay());
        if (socketConfig.getTcpSendBufferSize() != -1) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, socketConfig.getTcpSendBufferSize());
        }
        if (socketConfig.getTcpReceiveBufferSize() != -1) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, socketConfig.getTcpReceiveBufferSize());
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(socketConfig.getTcpReceiveBufferSize()));
        }
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, socketConfig.isTcpKeepAlive());
        bootstrap.childOption(ChannelOption.SO_LINGER, socketConfig.getSoLinger());

        bootstrap.option(ChannelOption.SO_REUSEADDR, socketConfig.isReuseAddress());
        bootstrap.option(ChannelOption.SO_BACKLOG, socketConfig.getAcceptBackLog());
    }

	/**
	 * 停止B/S
	 */
	@Override
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		log.info("B/S is stopped.");
	}

	/**
	 * 获取B/S 监听端口
	 * @return
	 */
	@Override
	public int getPort() {
		return config.getPort();
	}

	/**
	 * 获取B/S Host Name
	 * @return
	 */
	@Override
	public String getHostName() {
		return config.getHostname();
	}
}
