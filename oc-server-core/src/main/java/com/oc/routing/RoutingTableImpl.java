package com.oc.routing;

import com.oc.cluster.collection.RemoteTaskResult;
import com.oc.cluster.task.hazelcast.PacketExecution;
import com.oc.core.OcImServer;
import com.oc.core.chain.ChainFactory;
import com.oc.core.chain.ChainType;
import com.oc.core.contants.Constants;
import com.oc.core.factory.PacketTransferFactory;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.session.CustomerAssignStatus;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import com.oc.store.packet.PacketStoreManager;
import com.oc.store.packet.model.RemoteData;
import com.oc.store.packet.model.RemoteDateType;
import com.oc.transfer.TransferTeam;
import com.oc.transfer.TransferWaiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @Description: 路由管理中心
 * 	1. 管理用户Session
 * 	2. 分发路由消息
 * @author chuangyeifang
 * @createDate 2020年7月19日
 * @version v 1.0
 */
public class RoutingTableImpl implements RoutingTable{
	
	private static Logger log = LoggerFactory.getLogger(RoutingTableImpl.class);
	
	private final static String REMOTE_CUSTOMER_CACHES = "Remote Customer Caches";
	private final static String REMOTE_WAITER_CACHES = "Remote Waiter Caches";

	private ClientRouteManager<WaiterRoute> waiterRoutes;
	private ClientRouteManager<CustomerRoute> customerRoutes;
	
	private LocalRoutingTable<WaiterSession> waiterSessionLocalRoutingTable;
	private LocalRoutingTable<CustomerSession> customerSessionLocalRoutingTable;

	public RoutingTableImpl() {
		waiterRoutes = new ClientRouteManager<>(REMOTE_WAITER_CACHES);
		customerRoutes = new ClientRouteManager<>(REMOTE_CUSTOMER_CACHES);
		waiterSessionLocalRoutingTable = new LocalRoutingTable<>();
		customerSessionLocalRoutingTable = new LocalRoutingTable<>();
	}
	
	@Override
	public Collection<CustomerSession> getLocalCustomerSessions() {
		return customerSessionLocalRoutingTable.getRoutes();
	}
	
	@Override
	public Collection<WaiterSession> getLocalWaiterSessions() {
		return waiterSessionLocalRoutingTable.getRoutes();
	}
	
	@Override
	public CustomerRoute getCustomerRoute(String uid) {
		return customerRoutes.get(uid);
	}

	@Override
	public List<CustomerRoute> getCustomerRoutes() {
		return customerRoutes.getRoutes();
	}

	@Override
	public WaiterRoute removeWaiterRoute(String uid) {
		return waiterRoutes.remove(uid);
	}
	
	@Override
	public CustomerRoute removeCustomerRoute(String uid) {
		return customerRoutes.remove(uid);
	}

	@Override
	public WaiterSession getLocalWaiterSession(String uid) {
		return waiterSessionLocalRoutingTable.getSession(uid);
	}
	
	@Override
	public CustomerSession getLocalCustomerSession(String uid) {
		return customerSessionLocalRoutingTable.getSession(uid);
	}

	@Override
	public void registerLocalCustomerSession(CustomerSession customerSession) {
		String uid = customerSession.getUid();
		if(null == uid || uid.length() == 0) {
			throw new NullPointerException("访客uid不能为空");
		}
		synchronized (customerSession.getUid()) {
			// 判断是否已经存在Session， 如果存在先关闭
			CustomerRoute existCustomerRoute = customerRoutes.get(uid);
			if(null != existCustomerRoute) {
				if (customerRoutes.isLocal(existCustomerRoute)) {
					CustomerSession existCustomerSession = getLocalCustomerSession(uid);
					if (null != existCustomerSession) {
						// 存在的客户可能在排队
						OcImServer.getInst().getDispatcher().removeWait(existCustomerSession.getCustomer());
						ChainFactory.getInst().dispatcherCustomer(customerSession, null, ChainType.CLOSE_REPEAT_CONNECTION);
						removeLocalCustomerSession(existCustomerSession);
						existCustomerSession.closeChannel();
					}
				} else {
					OcImServer.getInst().getClusterMessageRouter().routeRemoveCustomerSession(
							existCustomerRoute.getNodeID(), existCustomerRoute);
				}
			}
			CustomerRoute customerRoute = new CustomerRoute(customerSession);
			customerRoutes.put(uid, customerRoute);
			customerSessionLocalRoutingTable.putSession(uid, customerSession);
		}
	}
	
	@Override
	public void registerLocalWaiterSession(WaiterSession waiterSession) {
		String uid = waiterSession.getUid();
		synchronized (waiterSession.getUid()) {
			WaiterRoute existWaiterRoute = waiterRoutes.get(uid);
			// 如果已经存在，则先清除再保存
			if(null != existWaiterRoute) {
				if (waiterRoutes.isLocal(existWaiterRoute)) {
					WaiterSession existWaiterSession = getLocalWaiterSession(uid);
					if (null != existWaiterSession) {
						closeRepeatWaiterClient(existWaiterSession);
						removeLocalWaiterSession(existWaiterSession);
						existWaiterSession.closeChannel();
					}
				} else {
					OcImServer.getInst().getClusterMessageRouter().routeRemoveWaiterSession(
							existWaiterRoute.getNodeID(), existWaiterRoute);
				}
			}
			// 创建客服信息
			WaiterRoute waiterRoute = new WaiterRoute(waiterSession);
			waiterRoutes.put(uid, waiterRoute);
			waiterSessionLocalRoutingTable.putSession(uid, waiterSession);
		}
	}

	@Override
	public synchronized void removeLocalWaiterSession(WaiterSession waiterSession) {
		String uid = waiterSession.getUid();
		WaiterRoute waiterRoute = waiterRoutes.get(uid);
		if (null != waiterRoute && waiterRoute.getVersion().equals(waiterSession.getVersion())) {
			waiterRoutes.remove(uid);
			waiterSessionLocalRoutingTable.remove(uid);
			OcImServer.getInst().getDispatcher().logout(waiterSession);
		}
	}

	@Override
	public synchronized void removeLocalCustomerSession(CustomerSession customerSession) {
		String uid = customerSession.getUid();
		CustomerRoute customerRoute = customerRoutes.get(uid);
		if (customerRoute != null && customerRoute.getVersion().equals(customerSession.getVersion())) {
			OcImServer.getInst().getDispatcher().closeChat(customerSession);
			customerRoutes.remove(uid);
			customerSessionLocalRoutingTable.remove(uid);
			log.info("移除客户会话：{}", customerSession);
		} else {
			log.warn("客户会话已经被移除：{}", customerSession);
		}
	}

	@Override
	public void removeRemoteWaiterSession(WaiterRoute waiterRoute) {
		String uid = waiterRoute.getUid();
		WaiterSession waiterSession = getLocalWaiterSession(uid);
		if (null != waiterSession && waiterSession.getVersion().equals(waiterRoute.getVersion())) {
			closeRepeatWaiterClient(waiterSession);
			waiterSessionLocalRoutingTable.remove(uid);
		}
	}

	@Override
	public void removeRemoteCustomerSession(CustomerRoute customerRoute) {
		String uid = customerRoute.getUid();
		CustomerSession customerSession = getLocalCustomerSession(uid);
		if (null != customerSession && customerSession.getVersion().equals(customerRoute.getVersion())) {
			// 存在的客户可能在排队
			OcImServer.getInst().getDispatcher().removeWait(customerSession.getCustomer());
			customerSessionLocalRoutingTable.remove(uid);
			ChainFactory.getInst().dispatcherCustomer(customerSession, null, ChainType.CLOSE_REPEAT_CONNECTION);
			customerSession.closeChannel();
		}
	}

	@Override
	public void routePacket(Packet packet) {
		AddressTo to = packet.getTo();
		if (null == to) {
			log.error("请设置消息目的地： {}", packet);
			return;
		}
		switch (to.getIdy()) {
			case CUSTOMER:
				CustomerSession customerSession = customerSessionLocalRoutingTable.getSession(to.getUid());
				if (null != customerSession) {
					if (!isCustomerAssigned(customerSession)) {
						packet.getTo().setName(customerSession.getName());
						customerSession.sendPacket(packet);
						dealOfflineWaiterPacket(packet);
						return;
					}
					boolean valid = validateCustomerChatEffective(customerSession, packet);
					if (valid) {
						packet.setCid(customerSession.getCid());
						packet.getTo().setName(customerSession.getName());
						customerSession.sendPacket(packet);
						resolverToCustomerBody(customerSession, packet.getBody());
						RemoteData remoteData = new RemoteData(RemoteDateType.NORMAL, packet, customerSession);
						PacketStoreManager.getInst().addRemoteData(remoteData);
					}
				} else {
					CustomerRoute customerRoute = customerRoutes.get(to.getUid());
					if(null != customerRoute && !isLocalRoute(customerRoute)) {
						RemoteTaskResult remoteTaskResult = OcImServer.getInst().getClusterMessageRouter().routePacket(customerRoute.getNodeID(), packet, PacketExecution.RemotePacketType.NORMAL);
						if(remoteTaskResult.getCode() != 100) {
							dealOfflineWaiterPacket(packet);
							log.warn("无法路由客户消息  code: {} - msg: {}", remoteTaskResult.getCode(), remoteTaskResult.getMsg());
						}
					} else {
						dealOfflineWaiterPacket(packet);
					}
				}
				break;
			case WAITER:
				WaiterSession waiterSession = waiterSessionLocalRoutingTable.getSession(to.getUid());
				if (null != waiterSession) {
					waiterSession.sendPacket(packet);
					RemoteData remoteData = new RemoteData(RemoteDateType.NORMAL, packet, waiterSession);
					PacketStoreManager.getInst().addRemoteData(remoteData);
				} else {
					WaiterRoute waiterRoute = waiterRoutes.get(to.getUid());
					if(null != waiterRoute && !isLocalRoute(waiterRoute)) {
						RemoteTaskResult remoteTaskResult = OcImServer.getInst().getClusterMessageRouter().routePacket(waiterRoute.getNodeID(), packet, PacketExecution.RemotePacketType.NORMAL);
						if(remoteTaskResult.getCode() != 100) {
							unFoundWaiterToCloseChat(packet);
							log.warn("无法路由客服消息  code: {} - msg: {}", remoteTaskResult.getCode(), remoteTaskResult.getMsg());
						}
					} else {
						unFoundWaiterToCloseChat(packet);
						log.warn("客服列表中没有检测到当前客服，关闭当前会话");
					}
				}
				break;
			case SYS:
				// ignore
				break;
			default:
				log.error("无法路由此类型消息 packet:{}", packet);
				break;
		}
	}
	
	@Override
	public void routeTransferByWaiter(TransferWaiter transferWaiter) {
		// 说明：客服(A) 转接   客户(C) -> 客服(B)
		String uid = transferWaiter.getUid();
		CustomerSession customerSession = getLocalCustomerSession(uid);
		if (null != customerSession) {
			String cid = customerSession.getCid();
			String currWaiterCode = customerSession.getWaiterCode();
			String fromWc = transferWaiter.getFromWc();
			// 判断客户和客服绑定关系 A
			if (!fromWc.equals(currWaiterCode)) {
				PacketTransferFactory.getInst().transferByWaiterFailed(cid, transferWaiter, "提示，客户已经离开，状态有误，拒绝转入");
				return;
			}
			// 获取转接客服 B
			boolean isTransfer = OcImServer.getInst().getDispatcher().transferByWaiter(transferWaiter);
			if (!isTransfer) {
				PacketTransferFactory.getInst().transferByWaiterFailed(cid, transferWaiter, "提示，当前客服处于忙碌状态或者已经离线，拒绝转入");
				return;
			}
			// 释放资源 A
			OcImServer.getInst().getDispatcher().directReleaseRelation(transferWaiter.getFromWc());
			// 给转接客服发送转接成功信息  A
			PacketTransferFactory.getInst().transferByWaiterASuccess(cid, transferWaiter);
			// 给接收转接客服发送转入消息 B
			PacketTransferFactory.getInst().transferByWaiterBSuccess(customerSession, transferWaiter);
			// 给客户发送转接成功消息 C
			PacketTransferFactory.getInst().transferByWaiterCSuccess(customerSession, cid, transferWaiter);
		} else {
			CustomerRoute customerRoute = customerRoutes.get(uid);
			if (null != customerRoute && !isLocalRoute(customerRoute)) {
				OcImServer.getInst().getClusterMessageRouter().routeTransferByWaiter(customerRoute.getNodeID(), transferWaiter);
			} else {
				PacketTransferFactory.getInst().transferByWaiterFailed(null, transferWaiter, "提示，客户已经离开，拒绝转入");
			}
		}
	}

	@Override
	public void routeTransferByTeam(TransferTeam transferTeam) {
		OcImServer.getInst().getDispatcher().transferByTeam(transferTeam);
	}
	
	@Override
	public void routeChatClose(Packet packet) {
		AddressTo to = packet.getTo();
		if (to.getIdy() == Identity.CUSTOMER) {
			CustomerSession customerSession = customerSessionLocalRoutingTable.getSession(to.getUid());
			if (null != customerSession) {
				if (customerSession.getStatus() == CustomerAssignStatus.ASSIGNED
						&& customerSession.getWaiterCode().equals(packet.getFrom().getUid())) {
					packet.setCid(customerSession.getCid());
					customerSession.sendPacket(packet);
					OcImServer.getInst().getDispatcher().closeChat(customerSession);
					//存储消息
					RemoteData remoteData = new RemoteData(RemoteDateType.NORMAL, packet, customerSession);
					PacketStoreManager.getInst().addRemoteData(remoteData);
				}
			} else {
				CustomerRoute customerRoute = customerRoutes.get(to.getUid());
				if(null != customerRoute && !isLocalRoute(customerRoute)) {
					RemoteTaskResult remoteTaskResult = OcImServer.getInst().getClusterMessageRouter().routePacket(customerRoute.getNodeID(), packet, PacketExecution.RemotePacketType.CHAT_CLOSE);
					if(remoteTaskResult.getCode() == 101) {
						log.info("客户已经离开，无需再次关闭");
					}
				} else {
					log.info("客户已经离开，无需再次关闭");
				}
			}
		}
	}

	/**
	 * 判断路由信息是否在本地
	 * @param route 路由信息
	 * @return true 本地 false 远程
	 */
	private boolean isLocalRoute(Route route) {
		return route.getNodeID().equals(OcImServer.getInst().getNodeId());
	}

	/**
	 * 未找到客服 关闭当前会话
	 * @param packet 消息
	 */
	private void unFoundWaiterToCloseChat(Packet packet) {
		Packet closePacket = new Packet(PacketType.CLOSE_CHAT);
		AddressFrom from = new AddressFrom(packet.getTo().getUid(), Identity.WAITER);
		AddressTo to = new AddressTo(packet.getFrom().getUid(),
				packet.getFrom().getName(), Identity.CUSTOMER);
		closePacket.setTo(to);
		closePacket.setFrom(from);
		closePacket.setBody(new Body(BodyType.FAIL, "客服状态有误"));
		routeChatClose(closePacket);
	}

	/**
	 * 判断当前会话是否已经分配客服
	 * @param customerSession 当前客户会话
	 * @return true 已经分配 false 未分配
	 */
	private boolean isCustomerAssigned(CustomerSession customerSession) {
		return customerSession.getStatus() == CustomerAssignStatus.ASSIGNED;
	}

	/**
	 * 判断是否是同一个客服, 如果不是一个客服，则通知后续客服客户正在咨询中
	 * @param customerSession 客户信息
	 * @param packet 消息
	 * @return 效验消息 true通过 false未通过
	 */
	private boolean validateCustomerChatEffective(CustomerSession customerSession, Packet packet) {
		PacketType type = packet.getType();
		AddressFrom from = packet.getFrom();
		// 消息类型不能为空
		if (null == type) {
			return false;
		}
		// 广播消息直接效验通过
		if (type == PacketType.BROADCAST) {
			return true;
		}
		// 效验发送者不能为空
		if (null == from) {
			return false;
		}
		if (type == PacketType.MESSAGE && !customerSession.getWaiterCode().equals(from.getUid())) {
			Body body = new Body(BodyType.FAIL, "客户正在与客服工号: " + customerSession.getWaiterCode() + "咨询中，请稍后再试!");
			AddressTo to = new AddressTo(packet.getFrom().getUid(), Identity.WAITER);
			from = new AddressFrom(customerSession.getUid(), customerSession.getName(),
					customerSession.getIdy());
			packet = new Packet(PacketType.CHATTING, from, to, body);
			OcImServer.getInst().getRoutingTable().routePacket(packet);
			return false;
		}
		return true;
	}

	/**
	 * 解析客户消息类型
	 * @param customerSession 客户信息
	 * @param body 消息内容
	 */
	private void resolverToCustomerBody(CustomerSession customerSession, Body body) {
		if (null == body || null == body.getType()) {
			return;
		}
		if (body.getType() == BodyType.TIMEOUT_CLOSE) {
			timeoutClose(customerSession);
		}
	}

	/**
	 * 处理客户不在线，留言信息
	 * @param packet 消息
	 */
	private void dealOfflineWaiterPacket(Packet packet) {
		if (packet.getType() == PacketType.MESSAGE) {
			Body body = packet.getBody();
			if(null != body && body.getType() != null) {
				switch (packet.getBody().getType()) {
					case TIMEOUT_TIP:
					case TIMEOUT_CLOSE:
					case WAITER_CLOSE:
					case WAITER_GREET:
					case BUILDING_CHAT:
						break;
					default:
						RemoteData remoteData = new RemoteData(RemoteDateType.OFFLINE, packet);
						PacketStoreManager.getInst().addRemoteData(remoteData);
						break;
				}
				log.warn("客户列表中没有查找到客户，判定已经离开，离线留言. packet: {}", packet);
			}
		} else if (packet.getType() == PacketType.REVOCATION) {
			RemoteData remoteData = new RemoteData(RemoteDateType.REVOCATION, packet);
			PacketStoreManager.getInst().addRemoteData(remoteData);
		}
	}

	/**
	 * 处理超时关闭
	 * @param customerSession 客户信息
	 */
	private void timeoutClose(CustomerSession customerSession) {
		OcImServer.getInst().getDispatcher().closeChat(customerSession);
	}

	/**
	 * 通知客服重复登录， 踢掉历史客户端
	 * @param waiterSession 客户信息
	 */
	private void closeRepeatWaiterClient(WaiterSession waiterSession) {
		AddressTo to = new AddressTo(Identity.SYS);
		Body body = new Body(BodyType.WAITER_CLOSE, Constants.REPEAT_MESSAGE);
		Packet packet = new Packet(PacketType.RE_LOGIN, to, body);
		waiterSession.sendPacket(packet);
	}
}