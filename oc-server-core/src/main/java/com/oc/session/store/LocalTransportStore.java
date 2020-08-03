/**
 * 
 */
package com.oc.session.store;

import com.oc.message.Packet;
import com.oc.message.type.Transport;
import com.oc.cluster.collection.queue.CustomQueue;
import com.oc.cluster.collection.queue.local.DefaultCustomQueue;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月22日
 * @version v 1.0
 */
public class LocalTransportStore implements TransportStore {
	
	private CustomQueue<Packet> packetsQueue = new DefaultCustomQueue<>();
	private CustomQueue<Packet> futurePackets = new DefaultCustomQueue<>();
	
	private Transport transport;
	
	public LocalTransportStore(Transport transport) {
		this.transport = transport;
	}

	@Override
	public CustomQueue<Packet> getPacketsQueue() {
		return packetsQueue;
	}

	@Override
	public CustomQueue<Packet> getFuturePackets() {
		return futurePackets;
	}

	@Override
	public Transport getTransport() {
		return transport;
	}
}
