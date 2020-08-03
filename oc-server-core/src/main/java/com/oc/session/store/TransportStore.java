package com.oc.session.store;

import com.oc.message.Packet;
import com.oc.message.type.Transport;
import com.oc.cluster.collection.queue.CustomQueue;

/**
 * @author chuangyeifang
 */
public interface TransportStore {

    /**
     * 获取当前消息
     * @return
     */
    CustomQueue<Packet> getPacketsQueue();

    /**
     * 获取缓存消息
     * @return
     */
    CustomQueue<Packet> getFuturePackets();

    /**
     * 获取传输类型
     * @return
     */
    Transport getTransport();
}
