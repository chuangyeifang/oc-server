package com.oc.core.chain;

import com.oc.core.OcImServer;
import com.oc.message.AddressFrom;
import com.oc.message.Packet;
import com.oc.message.type.Identity;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import com.oc.store.packet.PacketStoreManager;
import com.oc.store.packet.model.RemoteData;
import com.oc.store.packet.model.RemoteDateType;

/**
 * 撤回消息
 * @author chuangyeifang
 */
class RevocationPacketChain implements IChain{

    /**
     * 客服撤回消息
     *
     * @param session 客服Session
     * @param packet  消息内容
     */
    @Override
    public void dispatcherWaiterPacket(WaiterSession session, Packet packet) {
        packet.setTtc(session.getWaiter().getTenantCode());
        packet.setTmc(session.getWaiter().getTeamCode());
        AddressFrom from = packet.getFrom();
        if (null == from) {
            from = new AddressFrom();
        }
        from.setUid(session.getWaiter().getWaiterCode());
        from.setIdy(Identity.WAITER);
        packet.setFrom(from);

        OcImServer.getInst().getRoutingTable().routePacket(packet);
        RemoteData remoteData = new RemoteData(RemoteDateType.REVOCATION, packet);
        PacketStoreManager.getInst().addRemoteData(remoteData);
    }

    /**
     * 客户撤回消息
     *
     * @param session 客户Session
     * @param packet  消息内容
     */
    @Override
    public void dispatcherCustomerPacket(CustomerSession session, Packet packet) {
        throw new IllegalArgumentException("暂不支持");
    }
}
