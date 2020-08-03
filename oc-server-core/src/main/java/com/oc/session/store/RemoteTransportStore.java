package com.oc.session.store;

import com.oc.dispatcher.NameFactory;
import com.oc.message.Packet;
import com.oc.message.type.Transport;
import com.oc.cluster.collection.queue.CustomQueue;

/**
 * @author chuangyeifang
 */
public class RemoteTransportStore implements TransportStore {

    private final static String PREFIX_CUSTOMER_PACKET = "Customer Packet:";
    private final static String PREFIX_CUSTOMER_FUTURE_PACKET = "Customer Future Packet:";

    private NameFactory nameFactory;

    private CustomQueue<Packet> packetsQueue;
    private CustomQueue<Packet> futurePackets;

    private Transport transport;

    public RemoteTransportStore(Transport transport, String uid) {
        nameFactory = new NameFactory();
        packetsQueue = nameFactory.getQueue(PREFIX_CUSTOMER_PACKET, uid);
        futurePackets = nameFactory.getQueue(PREFIX_CUSTOMER_FUTURE_PACKET, uid);
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

    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
