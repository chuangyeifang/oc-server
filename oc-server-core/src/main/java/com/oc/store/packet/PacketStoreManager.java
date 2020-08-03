package com.oc.store.packet;

import com.oc.domain.chat.Chat;
import com.oc.domain.chat.ChatRecord;
import com.oc.domain.waiter.Waiter;
import com.oc.dto.chat.ChatRecordStatistics;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.provider.db.ChatProvider;
import com.oc.session.Customer;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import com.oc.store.packet.listener.LocalPacketStoreListener;
import com.oc.store.packet.listener.PacketStoreListener;
import com.oc.store.packet.model.ChatCollectHelper;
import com.oc.store.packet.model.RemoteData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 消息管理中心
 * @author chuangyeifang
 * @createDate 2020年2月7日
 * @version v 1.0
 */
@Slf4j
public class PacketStoreManager {
	
	private static List<PacketStoreListener> listeners = new ArrayList<>();
	
	private PacketStoreManager() {
		//TODO 暂时放弃MQ registerListener(new KafkaPacketStoreListener());
		registerListener(new LocalPacketStoreListener());
	}
	
	/**
	 * 注册消息监听
	 * @param listener 监听者
	 */
	public void registerListener(PacketStoreListener listener) {
		listeners.add(listener);
	}

	public void addRemoteData(RemoteData remoteData) {
		if (null != remoteData) {
			switch (remoteData.getType()) {
				case NORMAL:
					remotePacket(remoteData);
					break;
				case OFFLINE:
					remoteOfflinePacket(remoteData);
					break;
				case REVOCATION:
					remoteRevocationPacket(remoteData);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * 处理消息
	 * @param remoteData 存储数据
	 */
	private void remotePacket(RemoteData remoteData) {
		Identity identity = remoteData.getIdentity();
		Packet packet = remoteData.getPacket();
		PacketType packetType = packet.getType();

		if (!packetIsPutStore(packet)) {
			return;
		}

		Body body = packet.getBody();
		BodyType bodyType = body.getType();
		if(identity == Identity.WAITER) {
			try {
				switch(packetType) {
					case MESSAGE:
						dispatcherChatRecord(remoteData);
						break;
					case CLOSE_CHAT:
						dispatcherEndChat(remoteData);
						break;
					case TRANSFER:
						if (bodyType == BodyType.SUCCESS) {
							remoteData.setIdentity(Identity.NULL);
							dispatcherEndChat(remoteData);
						}
						break;
					default:
						break;
				}
			} catch (Exception e) {
				log.error("保存客服消息: {}, 发生异常： {}", remoteData, e);
			}
			
		} else if (identity == Identity.CUSTOMER) {
			try {
				switch(packetType) {
					case TRANSFER:
						if (bodyType == BodyType.SUCCESS) {
							dispatcherBuildChat(remoteData, "1");
						}
						break;
					case BUILD_CHAT:
						if (bodyType == BodyType.SUCCESS) {
							dispatcherBuildChat(remoteData, "0");
						} else if(bodyType != BodyType.WAITTING && bodyType != BodyType.WAITTING_NO) {
							dispatcherChatRecord(remoteData);
						}
						break;
					case MESSAGE:
						dispatcherChatRecord(remoteData);
						if (bodyType == BodyType.TIMEOUT_CLOSE) {
							remoteData.setIdentity(Identity.SYS);
							dispatcherEndChat(remoteData);
						}
						break;
					case CLOSE_CHAT:
						dispatcherEndChat(remoteData);
						break;
					default:
						break;
				}
			} catch (Exception e) {
				log.error("保存客户消息: {}, 发生异常： {}", remoteData, e);
			}
		}
	}

	private void remoteOfflinePacket(RemoteData remoteData) {
		Packet packet = remoteData.getPacket();
		if (null == packet.getCid()) {
			return;
		}
		ChatRecord chatRecord = new ChatRecord();
		Body body = packet.getBody();
		String messageType = transMessageType(body.getType());
		
		chatRecord.setChatId(packet.getCid());
		chatRecord.setMessageId(packet.getPid());

		createChatRecord(packet, chatRecord);

		String ownerType = transOwnerType(body.getType(), "2");
		chatRecord.setOwnerType(ownerType);
		chatRecord.setMessageType(messageType);
		chatRecord.setOffline("1");
		chatRecord.setRevocation(1);
		chatRecord.setMessages(body.getContent());
		chatRecord.setCreateTime(packet.getDatetime());
		
		ChatProvider.getInstance().insertChatRecord(chatRecord);
		
	}

	private void remoteRevocationPacket(RemoteData remoteData) {
		Packet packet = remoteData.getPacket();
		ChatRecord record = new ChatRecord();
		record.setChatId(packet.getCid());
		record.setMessageId(packet.getPid());
		record.setRevocation(0);
		ChatProvider.getInstance().revocationChatRecord(record);
	}
	
	private synchronized void dispatcherChatRecord(RemoteData remoteData) {
		ChatRecord chatRecord = new ChatRecord();
		Packet packet = remoteData.getPacket();
		Body body = packet.getBody();
		String messageType = transMessageType(body.getType());
		String ownerType;
		if(packet.getCid() == null || packet.getCid().length() == 0) {
			log.warn("当前消息无chat id, packet: {}", packet);
			return;
		}
		if (remoteData.getIdentity() == Identity.CUSTOMER) {
			chatRecord.setMessageId(packet.getPid());
			chatRecord.setChatId(packet.getCid());
			ownerType = transOwnerType(body.getType(), "2");

			createChatRecord(packet, chatRecord);

			chatRecord.setOwnerType(ownerType);
			chatRecord.setMessageType(messageType);
			chatRecord.setOffline("0");
			chatRecord.setRevocation(1);
			chatRecord.setMessages(body.getContent());
			chatRecord.setCreateTime(packet.getDatetime());
		} else if (remoteData.getIdentity() == Identity.WAITER) {
			ownerType = transOwnerType(body.getType(), "1");
			//客服助手消息，忽略客服发送
			if ("3".equals(ownerType)) {
				return;
			}
			WaiterSession session = remoteData.getWaiterSession();
			Waiter waiter = session.getWaiter();
			String tenantCode = waiter.getTenantCode();
			Integer teamCode = waiter.getTeamCode();
			
			chatRecord.setMessageId(packet.getPid());
			chatRecord.setChatId(packet.getCid());
			chatRecord.setTenantCode(tenantCode);
			chatRecord.setTeamCode(teamCode);
			chatRecord.setWaiterCode(packet.getTo().getUid());
			chatRecord.setWaiterName(packet.getTo().getName());
			chatRecord.setCustomerCode(packet.getFrom().getUid());
			chatRecord.setCustomerName(packet.getFrom().getName());
			chatRecord.setOwnerType(ownerType);
			chatRecord.setMessageType(messageType);
			chatRecord.setOffline("0");
			chatRecord.setRevocation(1);
			chatRecord.setMessages(body.getContent());
			chatRecord.setCreateTime(packet.getDatetime());
		}
		//同步消息到监听者
		for (PacketStoreListener remotePacketStoreListener : listeners) {
			remotePacketStoreListener.remoteChatRecord(chatRecord);
		}
	}

	private void dispatcherBuildChat(RemoteData remoteData, String buildType) {
		Chat chat = new Chat();
		Packet packet = remoteData.getPacket();

		if (remoteData.getIdentity() == Identity.CUSTOMER) {
			CustomerSession customerSession = remoteData.getCustomerSession();
			Customer customer = customerSession.getCustomer();
			String tenantCode = customer.getTenantCode();
			Integer teamCode = customer.getTeamCode();
			Integer skillCode = customer.getSkillCode();

			chat.setChatId(packet.getCid());
			chat.setTenantCode(tenantCode);
			chat.setTeamCode(teamCode);
			chat.setSkillCode(skillCode);
			chat.setGoodsCode(customer.getGoodsCode());

			chat.setDeviceType(customer.getDevice());
			chat.setIsLogin(customer.isLogin() ? "1" : "0");
			chat.setIsTransfer(buildType);
			chat.setCloseType("0");
			chat.setType("1");
			chat.setWaitingTime(customer.getWait());
			chat.setCustomerName(customer.getName());
			chat.setCustomerCode(customer.getUid());
			chat.setWaiterName(customerSession.getWaiterName());
			chat.setWaiterCode(customerSession.getWaiterCode());
			chat.setCreateTime(packet.getDatetime());
		}
		//同步消息到监听者
		for (PacketStoreListener remotePacketStoreListener : listeners) {
			remotePacketStoreListener.remoteBuildChat(chat);
		}
	}
	
	private void dispatcherEndChat(RemoteData remoteData) {
		Chat chat = new Chat();
		Packet packet = remoteData.getPacket();
		
		chat.setChatId(packet.getCid());
		if (remoteData.getIdentity() == Identity.CUSTOMER) {
			chat.setCloseType("2");
		} else if (remoteData.getIdentity() == Identity.WAITER) {
			chat.setCloseType("1");
		} else if (remoteData.getIdentity() == Identity.SYS){
			chat.setCloseType("3");
		} else {
			chat.setCloseType("4");
		}
		List<ChatRecordStatistics> chatRecordStatistics = ChatProvider.getInstance().obtainChatRecordStatistics(packet.getCid());
		ChatCollectHelper helper = new ChatCollectHelper(chatRecordStatistics);
		if (null != helper) {
			chat.setIsEffective(helper.isEffective());
			chat.setMsgTotal(helper.getTotalCount());
			chat.setWaiterMsgTotal(helper.getWaiterMsgCount());
			chat.setCustomerMsgTotal(helper.getCustomerMsgCount());
		} else {
			chat.setIsEffective("0");
			chat.setMsgTotal(0);
			chat.setWaiterMsgTotal(0);
			chat.setCustomerMsgTotal(0);
		}
		//同步消息到监听者
		for (PacketStoreListener remotePacketStoreListener : listeners) {
			remotePacketStoreListener.remoteEndChat(chat);
		}
	}

	private void createChatRecord(Packet packet, ChatRecord chatRecord) {
		chatRecord.setTenantCode(packet.getTtc());
		chatRecord.setTeamCode(packet.getTmc());
		chatRecord.setWaiterCode(packet.getFrom().getUid());
		chatRecord.setWaiterName(packet.getFrom().getName());
		chatRecord.setCustomerCode(packet.getTo().getUid());
		chatRecord.setCustomerName(packet.getTo().getName());
	}

	/**
	 * 触发者：
	 *  1. 客服
	 *  2. 客户
	 *  3. robot
	 * @param type 消息内容类型
	 * @param defaultValue 默认值
	 * @return 返回转换类型
	 */
	private String transOwnerType(BodyType type, String defaultValue) {
		switch (type) {
			case TEAM_GREET:
			case WAITER_GREET:
			case TIMEOUT_TIP:
			case TIMEOUT_CLOSE:
			case ROBOT:
				defaultValue = "3";
				break;
			default:
				break;
		}
		return defaultValue;
	}
	
	/**
	 * 消息类型: 
	 * 	1 普通消息 
	 * 	2 图片消息 
	 * 	3 提示即将关闭 
	 * 	4 提示并关闭
	 * 	5 客服欢迎语 
	 * 	6 系统消息 
	 * 	7 客户关闭
	 * 	8 客服关闭 
	 * 	9 团队欢迎语
	 * 	10 商品信息
	 * 	11 订单信息
	 * 	12 物流信息
	 * 	13 机器人消息
	 * @param type 消息内容类型
	 * @return 转换后类型
	 */
	private String transMessageType(BodyType type) {
		String messageType;
		switch (type) {
			case IMAGE:
				messageType = "2";
				break;
			case WAITER_GREET:
				messageType = "5";
				break;
			case TIMEOUT_TIP:
				messageType = "3";
				break;
			case TIMEOUT_CLOSE:
				messageType = "4";
				break;
			case CUSTOMER_CLOSE:
				messageType = "7";
				break;
			case WAITER_CLOSE:
				messageType = "8";
				break;
			case TEAM_GREET:
				messageType = "9";
				break;
			case GOODS:
				messageType = "10";
				break;
			case ORDER:
				messageType = "11";
				break;
			case LOGISTICS:
				messageType = "12";
				break;
			case ROBOT:
				messageType = "13";
				break;
			default:
				messageType = "1";
		}
		return messageType;
	}

	/**
	 * 是否需要持久化
	 * @param packet 消息
	 * @return true 需要 否则不需要
	 */
	private boolean packetIsPutStore(Packet packet) {
		if (packet.getType() == PacketType.REVOCATION) {
			return false;
		}
		Body body = packet.getBody();
		if (null != body && null != body.getType()) {
			return body.getType() != BodyType.TIP;
		}
		return true;
	}

	/**
	 * @return 返回实例
	 */
	public static PacketStoreManager getInst() {
		return Single.instance;
	}

	/**
	 * 单例模式
	 */
	private static class Single {
		private static PacketStoreManager instance = new PacketStoreManager();
	}
}
