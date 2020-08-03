package com.oc.core.chain;

import com.oc.message.Packet;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;

/**
 * 消息分发链
 * @author chuangyeifang
 */
public interface IChain {
    /**
     * 分发客服消息
     * @param session 客服Session
     * @param packet 消息内容
     */
    void dispatcherWaiterPacket(WaiterSession session, Packet packet);

    /**
     * 分发客户消息
     * @param session 客户Session
     * @param packet 消息内容
     */
    void dispatcherCustomerPacket(CustomerSession session, Packet packet);
}
