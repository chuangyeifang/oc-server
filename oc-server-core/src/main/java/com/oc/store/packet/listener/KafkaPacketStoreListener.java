package com.oc.store.packet.listener;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oc.domain.chat.Chat;
import com.oc.domain.chat.ChatRecord;
import com.oc.produce.KafkaClient;
import com.oc.util.JsonUtils;
import com.oc.provider.context.SpringContext;

/**
 * @Description: Packet Produce
 * @author chuangyeifang
 * @createDate 2020年2月8日
 * @version v 1.0
 */
public class KafkaPacketStoreListener implements PacketStoreListener {
	
	private static Logger log = LoggerFactory.getLogger(KafkaPacketStoreListener.class);

	private static KafkaClient kafkaClient = SpringContext.getBean(KafkaClient.class);
	
	public KafkaPacketStoreListener() {
		kafkaClient.start();
	}
	
	/**
	 * 会话记录
	 * @param chatRecord
	 */
	@Override
	public void remoteChatRecord(ChatRecord chatRecord) {
		//ignore
	}

	/**
	 * 创建会话
	 * @param chat
	 */
	@Override
	public void remoteBuildChat(Chat chat) {
		//ignore
	}

	/**
	 * 会话结束
	 * @param chat
	 */
	@Override
	public void remoteEndChat(Chat chat) {
		try {
			String endChatTopic = "ocim-chat-info";
			kafkaClient.sendMsg(endChatTopic, JsonUtils.getJson().writeString(chat));
		} catch (IOException e) {
			log.info("Object to json string error: {}", e.getMessage());
		}
	}

}
