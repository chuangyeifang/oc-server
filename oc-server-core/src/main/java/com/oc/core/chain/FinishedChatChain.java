/**
 * 
 */
package com.oc.core.chain;

import com.oc.core.OcImServer;
import com.oc.core.contants.Constants;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月6日
 * @version v 1.0
 */
@Slf4j
class FinishedChatChain implements IChain {

	/**
	 * 客服关闭会话
	 * @param session 客服Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherWaiterPacket(WaiterSession session, Packet packet) {
		OcImServer.getInst().getRoutingTable().routeChatClose(packet);
	}

	/**
	 * 客户关闭会话
	 * @param session 客户Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherCustomerPacket(CustomerSession session, Packet packet) {
		Body body = new Body(BodyType.CUSTOMER_CLOSE, Constants.DISCONNECT_MESSAGE);
		AddressFrom from = new AddressFrom(session.getUid(), session.getIdy());
		AddressTo to = new AddressTo(session.getWaiterCode(), session.getWaiterName(), Identity.WAITER);
		Packet closePacket = new Packet(PacketType.CLOSE_CHAT, from, to, body);
		closePacket.setCid(session.getCid());
		OcImServer.getInst().getRoutingTable().routePacket(closePacket);
		if(log.isInfoEnabled()) {
			log.info("客户关闭聊天窗口 packet: {}", closePacket);
		}
	}
}
