package com.oc.core.chain;

import com.oc.message.Packet;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息链路
 * @author chuangyeifang
 */
public class ChainFactory {

    private Map<ChainType, IChain> factory;

    private ChainFactory() {
        init();
    }

    /**
     * 处理分发客服消息
     * @param session
     * @param packet
     */
    public void dispatcherWaiter(WaiterSession session, Packet packet) {
        switch(packet.getType()) {
            case TRANSFER:
                factory.get(ChainType.TRANSFER_CUSTOMER).dispatcherWaiterPacket(session, packet);
                break;
            case CLOSE_CHAT:
                factory.get(ChainType.FINISHED_CHAT).dispatcherWaiterPacket(session, packet);
                break;
            case RECEPTION:
                factory.get(ChainType.DIRECT_RECEPTION).dispatcherWaiterPacket(session, packet);
                break;
            case CHANGE_STATUS:
                factory.get(ChainType.CHANGE_WAITER_STATUS).dispatcherWaiterPacket(session, packet);
                break;
            case BIND:
                factory.get(ChainType.BIND).dispatcherWaiterPacket(session, packet);
                break;
            case REVOCATION:
                factory.get(ChainType.REVOCATION_PACKET).dispatcherWaiterPacket(session, packet);
                break;
            default:
                factory.get(ChainType.GENERAL_TRANSPORT).dispatcherWaiterPacket(session, packet);
                break;
        }
    }

    /**
     * 处理分发客服消息
     * @param session
     * @param packet
     */
    public void dispatcherWaiter(WaiterSession session, Packet packet, ChainType chainType) {
        factory.get(chainType).dispatcherWaiterPacket(session, packet);
    }

    /**
     * 处理分发客户消息
     * @param session
     * @param packet
     */
    public void dispatcherCustomer(CustomerSession session, Packet packet) {
        factory.get(ChainType.GENERAL_TRANSPORT).dispatcherCustomerPacket(session, packet);
    }

    /**
     * 处理分发客户消息
     * @param session
     * @param packet
     */
    public void dispatcherCustomer(CustomerSession session, Packet packet, ChainType chainType) {
        factory.get(chainType).dispatcherCustomerPacket(session, packet);
    }

    public static ChainFactory getInst() {
        return Single.chainFactory;
    }

    private void init() {
        factory = new HashMap<>(16);
        factory.put(ChainType.BIND, new BindChain());
        factory.put(ChainType.GENERAL_TRANSPORT, new GeneralTransportChain());
        factory.put(ChainType.FINISHED_CHAT, new FinishedChatChain());
        factory.put(ChainType.CLOSE_REPEAT_CONNECTION, new CloseRepeatConnectionChain());
        factory.put(ChainType.REVOCATION_PACKET, new RevocationPacketChain());
        factory.put(ChainType.DIRECT_RECEPTION, new DirectReceptionChain());
        factory.put(ChainType.CHANGE_WAITER_STATUS, new ChangeWaiterStatusChain());
        factory.put(ChainType.TRANSFER_CUSTOMER, new TransferCustomerChain());
    }

    private static class Single {
        private static ChainFactory chainFactory = new ChainFactory();
    }
}
