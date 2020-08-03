package com.oc.core.chain;

import com.oc.dispatcher.register.Event;
import com.oc.dispatcher.register.EventType;
import com.oc.domain.waiter.Waiter;
import com.oc.core.OcImServer;
import com.oc.message.Packet;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月4日
 * @version v 1.0
 */
class DirectReceptionChain implements IChain{

	/**
	 * 手动接入排队客户
	 * @param session 客服Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherWaiterPacket(WaiterSession session, Packet packet) {
		Waiter waiter = session.getWaiter();
		String tenantCode = waiter.getTenantCode();
		Integer teamCode = waiter.getTeamCode();
		String waiterCode = waiter.getWaiterCode();
		String waiterName = waiter.getWaiterName();
		Event event = new Event(EventType.WAITER_MANUAL_REQ, null,
				tenantCode, teamCode, waiterName, waiterCode);
		OcImServer.getInst().getDispatcher().registerAllotEvent(event);
	}

	/**
	 * 分发客户消息
	 *
	 * @param session 客户Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherCustomerPacket(CustomerSession session, Packet packet) {

	}
}
