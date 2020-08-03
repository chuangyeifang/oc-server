package com.oc.service.chat;

import com.oc.domain.chat.ChatRecord;
import com.oc.dto.chat.ChatRecordStatistics;

import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月24日
 * @version v 1.0
 */
public interface ChatRecordService {

	/**
	 * 获取客服、客户、Robot消息数量
	 * @param chatId
	 * @return
	 */
	List<ChatRecordStatistics> obtainChatRecordStatistics(String chatId);

	/**
	 * 保存聊天记录
	 * @param record
	 * @return
	 */
	int insert(ChatRecord record);

	/**
	 * 撤回聊天记录
	 * @param chatRecord
	 * @return
	 */
	int revocationChatRecord(ChatRecord chatRecord);

	/**
	 * 获取聊天记录
	 * @param tenantCode
	 * @param id
	 * @param customerCode
	 * @return
	 */
    List<ChatRecord> obtainBefore(String tenantCode, Long id, String customerCode);
}
