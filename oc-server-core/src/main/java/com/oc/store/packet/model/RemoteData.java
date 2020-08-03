package com.oc.store.packet.model;

import com.oc.message.Packet;
import com.oc.message.type.Identity;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月25日
 * @version v 1.0
 */
public class RemoteData {

	private RemoteDateType type;

	private Packet packet;
	private Identity identity;
	private CustomerSession customerSession;
	private WaiterSession waiterSession;

	public RemoteData(RemoteDateType type, Packet packet) {
		this.type = type;
		this.packet = packet;
	}

	public RemoteData(RemoteDateType type, Packet packet, WaiterSession session) {
		this.type = type;
		this.packet = packet;
		this.identity = session.getIdy();
		this.waiterSession = session;
	}
	
	public RemoteData(RemoteDateType type, Packet packet, CustomerSession session) {
		this.type = type;
		this.packet = packet;
		this.identity = session.getIdy();
		this.customerSession = session;
	}

	public RemoteDateType getType() {
		return type;
	}

	public void setType(RemoteDateType type) {
		this.type = type;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public CustomerSession getCustomerSession() {
		return customerSession;
	}

	public WaiterSession getWaiterSession() {
		return waiterSession;
	}

	@Override
	public String toString() {
		return "RemoteData [packet=" + packet + ", identity=" + identity + ", customerSession=" + customerSession
				+ ", waiterSession=" + waiterSession + "]";
	}
}
