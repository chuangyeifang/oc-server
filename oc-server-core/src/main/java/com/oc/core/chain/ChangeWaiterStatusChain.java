package com.oc.core.chain;

import com.oc.domain.waiter.Waiter;
import com.oc.core.OcImServer;
import com.oc.message.Packet;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月4日
 * @version v 1.0
 */
@Slf4j
class ChangeWaiterStatusChain implements IChain{

	/**
	 * 改变客服服务状态
	 * @param session 客服Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherWaiterPacket(WaiterSession session, Packet packet) {
		Waiter waiter = session.getWaiter();
		Integer teamCode = waiter.getTeamCode();
		String waiterCode = waiter.getWaiterCode();
		String status = packet.getBody().getContent();
		OcImServer.getInst().getDispatcher().changeWaiterStatus(teamCode, waiterCode, status);
	}

	/**
	 * 分发客户消息
	 *
	 * @param session 客户Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherCustomerPacket(CustomerSession session, Packet packet) {
		throw new IllegalStateException("不支持");
	}
}
