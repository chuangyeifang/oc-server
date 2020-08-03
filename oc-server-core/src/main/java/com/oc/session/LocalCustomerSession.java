package com.oc.session;

import com.oc.common.utils.UUIDUtils;
import com.oc.core.OcImServer;
import com.oc.core.bs.message.OutPacketMessage;
import com.oc.core.chain.ChainFactory;
import com.oc.core.chain.ChainType;
import com.oc.message.Packet;
import com.oc.message.type.Identity;
import com.oc.message.type.Transport;
import com.oc.session.moniter.HttpPollCycleMonitor;
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
public class LocalCustomerSession implements CustomerSession {
	
	private String cid;
	private Channel channel;
	private Transport transport;
	private volatile CustomerAssignStatus status = CustomerAssignStatus.UNASSIGNED;
	private String waiterName;
	private String waiterCode;
	private TransportStore store;
	private Identity idy;
	private Customer customer;
	private String version;
	private HttpPollCycleMonitor cycleMonitor;

	private volatile boolean closed = false;
	
	public LocalCustomerSession(Channel channel, Transport transport, Identity idy, Customer customer) {
		this.channel = channel;
		this.transport = transport;
		this.idy = idy;
		this.customer = customer;
		this.store = new LocalTransportStore(transport);
		if (transport == Transport.POLLING) {
			cycleMonitor = new HttpPollCycleMonitor(this);
		}
		this.version = UUIDUtils.getUUID();
	}

	@Override
	public String getUid() {
		return customer.getUid();
	}

	@Override
	public String getName() {
		return customer.getName();
	}

	@Override
	public Identity getIdy() {
		return idy;
	}

	@Override
	public String getTenantCode() {
		return customer.getTenantCode();
	}

	@Override
	public Integer getTeamCode() {
		return customer.getTeamCode();
	}

	@Override
	public Integer getSkillCode() {
		return customer.getSkillCode();
	}

	@Override
	public String getSkillName() {
		return customer.getSkillName();
	}

	@Override
	public String getGoodsCode() {
		return customer.getGoodsCode();
	}

	@Override
	public void setCid(String cid) {
		this.cid = cid;
	}
	
	@Override
	public String getCid() {
		return cid;
	}

	@Override
	public Customer getCustomer() {
		return customer;
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
	public void bindRoute() {
		OcImServer.getInst().getRoutingTable().registerLocalCustomerSession(this);
	}

	@Override
	public void bindChannel(Channel channel) {
		this.channel = channel;
		if (transport == Transport.POLLING) {
			cycleMonitor.reset();
		}
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
	public String getVersion() {
		return version;
	}

	@Override
	public CustomerAssignStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(CustomerAssignStatus status) {
		this.status = status;
		if (CustomerAssignStatus.UNASSIGNED.equals(status)) {
			waiterName = null;
			waiterCode = null;
		}
	}
	
	@Override
	public String getWaiterName() {
		return waiterName;
	}
	
	@Override
	public void setWaiter(String waiterCode, String waiterName) {
		this.waiterCode = waiterCode;
		this.waiterName = waiterName;
	}
	
	@Override
	public String getWaiterCode() {
		return waiterCode;
	}
	
	@Override
	public synchronized void disconnect() {
		if (closed) {
			return;
		}
		closed = true;
		channel.attr(Session.CLIENT_SESSION).set(null);
		OcImServer.getInst().getDispatcher().removeWait(customer);
		if (this.getStatus() == CustomerAssignStatus.ASSIGNED) {
			ChainFactory.getInst().dispatcherCustomer(this, null, ChainType.FINISHED_CHAT);
		}
		OcImServer.getInst().getRoutingTable().removeLocalCustomerSession(this);
	}

	@Override
	public synchronized void closeChannel() {
		if (closed) {
			return;
		}
		closed = true;
		OcImServer.getInst().getDispatcher().closeChat(this);
		if (null != this.channel) {
			this.channel.close();
		}
	}

	@Override
	public String toString() {
		return "CustomerSessionImpl [cid=" + cid + ", status=" + status + ", waiterName=" + waiterName + ", waiterCode="
				+ waiterCode + ", customer=" + customer + ", version=" + version + "]";
	}
}
