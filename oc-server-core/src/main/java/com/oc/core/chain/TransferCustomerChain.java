package com.oc.core.chain;

import com.oc.core.OcImServer;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import com.oc.transfer.TransferTeam;
import com.oc.transfer.TransferWaiter;
import com.oc.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月17日
 * @version v 1.0
 */
@Slf4j
class TransferCustomerChain implements IChain{

	/**
	 * 分发客服消息
	 * @param session 客服Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherWaiterPacket(WaiterSession session, Packet packet) {
		Body body = packet.getBody();
		switch (body.getType()) {
			case TRANSFER_WAITER:
				transferByWaiter(packet);
				break;
			case TRANSFER_TEAM:
				transferByTeam(packet);
				break;
			default:
				log.error("不支持的转接类型: {}", body.getType());
				break;
		}
	}

	/**
	 * 分发客户消息
	 *
	 * @param session 客户Session
	 * @param packet  消息内容
	 */
	@Override
	public void dispatcherCustomerPacket(CustomerSession session, Packet packet) {
		throw new IllegalArgumentException("暂不支持");
	}

	/**
	 * 按照客服转接客户
	 * @param packet
	 */
	private void transferByWaiter(Packet packet) {
		String transferJsonStr = packet.getBody().getContent();
		TransferWaiter transferWaiter = null;
		try {
			transferWaiter = JsonUtils.getJson().readClass(transferJsonStr, TransferWaiter.class);
		} catch (IOException e) {
			log.error("转接序列化失败， packet： {}", packet);
		}
		if (null == transferWaiter) {
			errorParam(packet);
		} else {
			OcImServer.getInst().getRoutingTable().routeTransferByWaiter(transferWaiter);
		}
	}

	private void transferByTeam(Packet packet) {
		String transferJsonStr = packet.getBody().getContent();
		TransferTeam transferTeam = null;
		try {
			transferTeam = JsonUtils.getJson().readClass(transferJsonStr, TransferTeam.class);
		} catch (IOException e) {
			log.error("转接序列化失败， packet： {}", packet);
		}
		if (null == transferTeam) {
			errorParam(packet);
		} else {
			OcImServer.getInst().getRoutingTable().routeTransferByTeam(transferTeam);
		}
	}

	private void errorParam(Packet packet) {
		AddressTo to = new AddressTo(packet.getFrom().getUid(), Identity.WAITER);
		AddressFrom from = new AddressFrom(packet.getTo().getUid(),
				packet.getTo().getName(), packet.getTo().getIdy());
		Body body = new Body(BodyType.FAIL, "转接客户失败，请重试。");
		packet.setTo(to);
		packet.setFrom(from);
		packet.setBody(body);
		OcImServer.getInst().getRoutingTable().routePacket(packet);
	}
}
