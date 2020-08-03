package com.oc.core.factory;

import com.oc.core.OcImServer;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.provider.redis.IDProvider;
import com.oc.session.Customer;
import com.oc.session.CustomerSession;
import com.oc.transfer.TransferTeam;
import com.oc.transfer.TransferWaiter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuangyeifang
 */
@Slf4j
public class PacketTransferFactory {

	public void transferByWaiterFailed(String cid, TransferWaiter transferWaiter, String message) {
		Body body = new Body(BodyType.FAIL, message);
		getCreateTransferPacket(cid, transferWaiter, body);
	}
	
	public void transferByWaiterASuccess(String cid, TransferWaiter transferWaiter) {
		Body body = new Body(BodyType.SUCCESS, "提示，客户成功转入。");
		getCreateTransferPacket(cid, transferWaiter, body);
	}


	public void transferByTeamSuccess(String cid, TransferTeam transferTeam, String message) {
		Body body = new Body(BodyType.SUCCESS, message);
		Packet createTransferPacket = getCreateTransferPacket(cid, transferTeam, body);
		OcImServer.getInst().getRoutingTable().routePacket(createTransferPacket);
	}

	public void transferByWaiterBSuccess(CustomerSession customerSession, TransferWaiter transferWaiter) {

		// 重新分配cid 初始化客户基本信息 C
		String cid = IDProvider.getInstance().getChatId();
		customerSession.setCid(cid);
		customerSession.setWaiter(transferWaiter.getToWc(), transferWaiter.getToWn());
		Customer customer = customerSession.getCustomer();
		customer.setTenantCode(transferWaiter.getTtc());
		customer.setTeamCode(transferWaiter.getTmc());
		customer.setSkillCode(transferWaiter.getSkc());
		customer.setSkillName(transferWaiter.getSkn());

		String transferReason = "提示，客户来自工号【 " +
				transferWaiter.getFromWc() +
				" 】转入， 备注信息：" +
				transferWaiter.getReason();
		String content = BuildChatFactory.createBuildChatToJson(customerSession, transferReason);

		Body body = new Body(BodyType.TRANSFER_WAITER,  content);
		AddressTo to = new AddressTo(transferWaiter.getToWc(), transferWaiter.getToWn(), Identity.WAITER);
		AddressFrom from = new AddressFrom(transferWaiter.getUid(), transferWaiter.getName(),  Identity.CUSTOMER);
		Packet packet = new Packet(PacketType.BUILD_TRANSFER_CHAT, from, to, body);
		packet.setCid(cid);
		packet.setTtc(transferWaiter.getTtc());
		packet.setTmc(transferWaiter.getTmc());
		OcImServer.getInst().getRoutingTable().routePacket(packet);
	}
	
	/**
	 * 客户发送转接成功信息
	 * @param cid
	 * @param transferWaiter
	 */
	public void transferByWaiterCSuccess(CustomerSession customerSession, String cid, TransferWaiter transferWaiter) {
		String content = BuildChatFactory.createBuildChatToJson(customerSession);
		Body body = new Body(BodyType.SUCCESS, content);
		AddressTo to = new AddressTo(transferWaiter.getUid(), transferWaiter.getName(), Identity.CUSTOMER);
		AddressFrom from = new AddressFrom(transferWaiter.getToWc(), Identity.WAITER);
		Packet packet = new Packet(PacketType.TRANSFER, from, to, body);
		packet.setCid(cid);
		OcImServer.getInst().getRoutingTable().routePacket(packet);
	}

	/**
	 * 创建转接成功失败消息
	 * @param cid
	 * @param transferWaiter
	 * @param body
	 */
	private void getCreateTransferPacket(String cid, TransferWaiter transferWaiter, Body body) {
		AddressTo to = new AddressTo(transferWaiter.getFromWc(), Identity.WAITER);
		AddressFrom from = new AddressFrom(transferWaiter.getUid(), transferWaiter.getName(), Identity.CUSTOMER);
		Packet packet = new Packet(PacketType.TRANSFER, from, to, body);
		packet.setCid(cid);
		OcImServer.getInst().getRoutingTable().routePacket(packet);
	}

	/**
	 * 创建转接成功失败消息
	 * @param cid
	 * @param transferTeam
	 * @param body
	 * @return
	 */
	private Packet getCreateTransferPacket(String cid, TransferTeam transferTeam, Body body) {
		AddressFrom from = new AddressFrom(transferTeam.getUid(), transferTeam.getName(), Identity.CUSTOMER);
		AddressTo to = new AddressTo(transferTeam.getFromWc(), Identity.WAITER);
		Packet packet = new Packet(PacketType.TRANSFER, from, to, body);
		packet.setCid(cid);
		return packet;
	}
	
	public static PacketTransferFactory getInst() {
		return Single.inst;
	}

	private static class Single {
		private static PacketTransferFactory inst = new PacketTransferFactory();
	}
}
