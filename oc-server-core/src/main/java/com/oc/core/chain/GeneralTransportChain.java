package com.oc.core.chain;

import com.oc.core.OcImServer;
import com.oc.core.contants.Constants;
import com.oc.dispatcher.register.Event;
import com.oc.dispatcher.register.EventType;
import com.oc.domain.properties.Properties;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.provider.cache.LocalPropertiesStore;
import com.oc.session.Customer;
import com.oc.session.CustomerAssignStatus;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 普通消息处理
 * @author chuangyeifang
 */
@Slf4j
class GeneralTransportChain implements IChain {

    /**
     * 分发客服消息包
     * @param waiterSession
     * @param packet
     */
    @Override
    public void dispatcherWaiterPacket(WaiterSession waiterSession, Packet packet) {
        // 处理客服发送客户报文，附加报文来源， 保证报文完整性
        AddressFrom from = packet.getFrom() == null ? new AddressFrom() : packet.getFrom();
        from.setUid(waiterSession.getWaiter().getWaiterCode());
        from.setIdy(Identity.WAITER);
        packet.setFrom(from);
        packet.setTtc(waiterSession.getWaiter().getTenantCode());
        packet.setTmc(waiterSession.getWaiter().getTeamCode());

        resolverWaiterPacketBody(waiterSession, packet);
        OcImServer.getInst().getRoutingTable().routePacket(packet);
    }

    /**
     * 分发客户消息包
     * @param customerSession
     * @param packet
     */
    @Override
    public void dispatcherCustomerPacket(CustomerSession customerSession, Packet packet) {
        packet.setTtc(customerSession.getTenantCode());
        packet.setTmc(customerSession.getTeamCode());
        packet.setCid(customerSession.getCid());
        AddressFrom from = new AddressFrom(customerSession.getUid(), customerSession.getName(), customerSession.getIdy());
        packet.setFrom(from);

        switch (customerSession.getStatus()) {
            case UNASSIGNED:
                customerSession.setStatus(CustomerAssignStatus.ASSIGNING);
                customerSession.cachePacket(packet);
                registerAssignEvent(customerSession);
                break;
            case ASSIGNED:
                packet.getTo().setIdy(Identity.WAITER);
                packet.getTo().setUid(customerSession.getWaiterCode());
                packet.getTo().setName(customerSession.getWaiterName());
                OcImServer.getInst().getRoutingTable().routePacket(packet);
                break;
            case ASSIGNING:
                customerSession.cachePacket(packet);
                break;
            default:
                log.error("不支持的客户状态：{}", customerSession.getStatus());
        }
    }

    /**
     * 为客户注册分配客服事件，并通知客户正在分配客服
     * @param session
     */
    private void registerAssignEvent(CustomerSession session) {
        Customer customer = session.getCustomer();
        String uid = customer.getUid();
        Integer teamCode = customer.getTeamCode();
        //注册分配事件
        Event event = new Event(EventType.CUSTOMER_REQ, uid, customer.getTenantCode(), teamCode);
        OcImServer.getInst().getDispatcher().registerAllotEvent(event);
        //通知客户正在分配客服
        Body body = new Body(BodyType.BUILDING_CHAT, Constants.ASSIGNING_MESSAGE);
        Packet assignPacket = new Packet(PacketType.BUILD_CHAT, body);
        session.sendPacket(assignPacket);
    }

    /**
     * 处理客服消息内容
     * @param session
     * @param packet
     */
    private void resolverWaiterPacketBody(WaiterSession session, Packet packet) {
        Body body = packet.getBody();
        if (null == body || body.getType() == null) {
            return;
        }
        Properties properties;
        switch (body.getType()) {
            case TIMEOUT_TIP:
                properties = LocalPropertiesStore.getInst()
                        .getProperties(session.getWaiter().getTenantCode());
                body.setContent(properties.getTimeoutTipMessage());
                timeoutPacketToWaiter(packet);
                break;
            case TIMEOUT_CLOSE:
                properties = LocalPropertiesStore.getInst()
                        .getProperties(session.getWaiter().getTenantCode());
                body.setContent(properties.getTimeoutCloseMessage());
                timeoutPacketToWaiter(packet);
                break;
            default:
                break;
        }
    }

    /**
     * 超时提示或者关闭
     * @param packet
     */
    private void timeoutPacketToWaiter(Packet packet) {
        AddressTo to = packet.getTo();
        AddressFrom from = packet.getFrom();
        AddressTo reverseTo = new AddressTo(from.getUid(), from.getName(), from.getIdy());
        AddressFrom reverseFrom = new AddressFrom(to.getUid(), to.getName(), to.getIdy());
        packet.setTo(reverseTo);
        packet.setFrom(reverseFrom);
        OcImServer.getInst().getRoutingTable().routePacket(packet);
        packet.setTo(to);
        packet.setFrom(from);
    }
}
