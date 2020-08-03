/**
 * 
 */
package com.oc.core.chain;

import com.oc.domain.waiter.Waiter;
import com.oc.core.OcImServer;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月6日
 * @version v 1.0
 */
@Slf4j
class BindChain implements IChain {

	/**
	 * 开启客服服务状态
	 * @param session 客服Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherWaiterPacket(WaiterSession session, Packet packet) {
		Waiter waiter = session.getWaiter();
		// 绑定客服信息
		session.bindRoute();
		// 发送登录成功消息
		Body body = new Body(BodyType.SUCCESS);
		AddressFrom from = new AddressFrom(Identity.SYS);
		AddressTo to = new AddressTo(Identity.WAITER);
		Packet bindPacket = new Packet(PacketType.BIND, from, to, body);
		session.sendPacket(bindPacket).addListener((ChannelFutureListener) future -> {
			//客服登录成功后，设置服务信息
			OcImServer.getInst().getDispatcher().login(waiter);
			if (log.isInfoEnabled()) {
				log.info("客服工号：{} 登录成功。", waiter.getWaiterCode());
			}
		});
	}

	/**
	 * 分发客户消息
	 *
	 * @param session 客户Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherCustomerPacket(CustomerSession session, Packet packet) {
		throw new IllegalArgumentException("当前Session 不支持绑定");
	}
}
