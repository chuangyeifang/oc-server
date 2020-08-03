package com.oc.provider.db;

import com.oc.domain.chat.Chat;
import com.oc.domain.chat.ChatRecord;
import com.oc.dto.chat.ChatRecordStatistics;
import com.oc.provider.context.SpringContext;
import com.oc.service.chat.ChatRecordService;
import com.oc.service.chat.ChatService;
import com.oc.service.chat.impl.ChatRecordServiceImpl;
import com.oc.service.chat.impl.ChatServiceImpl;

import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月24日
 * @version v 1.0
 */
public class ChatProvider {

	private ChatService chatService = SpringContext.getBean(ChatServiceImpl.class);
	private ChatRecordService chatRecordService = SpringContext.getBean(ChatRecordServiceImpl.class);

	/**
	 * 获取客服、客户、Robot消息数量
	 * @param chatId
	 * @return
	 */
	public List<ChatRecordStatistics> obtainChatRecordStatistics(String chatId) {
		 return  chatRecordService.obtainChatRecordStatistics(chatId);
	}

	/**
	 * 创建会话
	 * @param record
	 */
	public void insertChat(Chat record) {
		chatService.insert(record);
	}

	/**
	 * 保存聊天记录
	 * @param record
	 */
	public void insertChatRecord(ChatRecord record) {
		chatRecordService.insert(record);
	}

	/**
	 * 聊天内容撤回
	 * @param record
	 */
	public void revocationChatRecord(ChatRecord record) {
		chatRecordService.revocationChatRecord(record);
	}

	/**
	 * 结束会话
	 * @param record
	 */
	public void updateChatClose(Chat record) {
		chatService.updateChatClose(record);
	}
	
	public static ChatProvider getInstance() {
		return Single.instance;
	}
	
	private static class Single {
		private static ChatProvider instance = new ChatProvider();
	}
}
