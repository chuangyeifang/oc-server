package com.oc.session;

import com.oc.common.utils.UUIDUtils;
import com.oc.core.OcImServer;
import com.oc.core.bs.message.OutPacketMessage;
import com.oc.domain.waiter.Waiter;
import com.oc.message.Packet;
import com.oc.message.type.Identity;
import com.oc.message.type.Transport;
import com.oc.provider.db.WaiterProvider;
import com.oc.session.store.LocalTransportStore;
import com.oc.session.store.TransportStore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @Description: 用户Session
 * @author chuangyeifang
 * @createDate 2020年1月18日
 * @version v 1.0
 */
public class LocalWaiterSession implements WaiterSession{
	
	private String uid;
	private Channel channel;
	private Transport transport;
	private TransportStore store;
	private Identity idy;
	private Waiter waiter;
	private String version;

	private volatile boolean closed = false;
	
	public LocalWaiterSession(String uid, Channel channel, Transport transport, Identity idy, Waiter waiter) {
		this.uid = uid;
		this.channel = channel;
		this.transport = transport;
		this.idy = idy;
		this.waiter = waiter;
		this.store = new LocalTransportStore(transport);
		this.version = UUIDUtils.getUUID();
	}
	
	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public String getName() {
		return waiter.getWaiterName();
	}

	@Override
	public Identity getIdy() {
		return idy;
	}

	@Override
	public Waiter getWaiter() {
		return waiter;
	}
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void bindRoute() {
		OcImServer.getInst().getRoutingTable().registerLocalWaiterSession(this);
	}
	
	@Override
	public void bindChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public Transport getTransport() {
		return transport;
	}

	@Override
	public TransportStore transportStore() {
		return store;
	}

	@Override
	public void cachePacket(Packet packet) {
		store.getFuturePackets().add(packet);
	}

	@Override
	public ChannelFuture sendPacket(Packet packet) {
		if (transport == Transport.SOCKET) {
			return channel.writeAndFlush(packet);
		}
		store.getPacketsQueue().add(packet);
		return channel.writeAndFlush(new OutPacketMessage(this));
	}
	
	@Override
	public synchronized void disconnect() {
		if (closed) {
			return;
		}
		closed = true;
		OcImServer.getInst().getRoutingTable().removeLocalWaiterSession(this);
		// 记录登出日志
		waiter.setStatus("4");
		WaiterProvider.getInst().insertWaiterLog(waiter, "3", null);
	}

	@Override
	public void closeChannel() {
		if (null != this.channel) {
			this.channel.close();
		}
	}

	@Override
	public String toString() {
		return "WaiterSessionImpl [uid=" + uid + ", version=" + version + ", waiter=" + waiter + "]";
	}
}
