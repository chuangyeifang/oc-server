package com.oc.store.packet.listener;

import com.oc.domain.chat.Chat;
import com.oc.domain.chat.ChatRecord;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月7日
 * @version v 1.0
 */
public interface PacketStoreListener {

	/**
	 * 保存聊天详情
	 * @param chatRecord
	 */
	void remoteChatRecord(ChatRecord chatRecord);

	/**
	 * 处理会话创建
	 * @param chat
	 */
	void remoteBuildChat(Chat chat);

	/**
	 * 处理会话结束
	 * @param chat
	 */
	void remoteEndChat(Chat chat);

}
