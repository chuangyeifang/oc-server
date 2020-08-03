package com.oc.store.packet.listener;

import com.oc.domain.chat.Chat;
import com.oc.domain.chat.ChatRecord;
import com.oc.provider.db.ChatProvider;

/**
 * @Description: Packet 持久化
 * @author chuangyeifang
 * @createDate 2020年2月8日
 * @version v 1.0
 */
public class LocalPacketStoreListener implements PacketStoreListener {

	/**
	 * 会话记录
	 * @param chatRecord
	 */
	@Override
	public void remoteChatRecord(ChatRecord chatRecord) {
		ChatProvider.getInstance().insertChatRecord(chatRecord);
	}

	/**
	 * 创建会话
	 * @param chat
	 */
	@Override
	public void remoteBuildChat(Chat chat) {
		ChatProvider.getInstance().insertChat(chat);
	}

	/**
	 * 结束会话
	 * @param chat
	 */
	@Override
	public void remoteEndChat(Chat chat) {
		ChatProvider.getInstance().updateChatClose(chat);
	}
}
