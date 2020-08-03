/**
 * 
 */
package com.oc.service.chat.impl;

import com.oc.dto.chat.ChatRecordStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.chat.ChatRecord;
import com.oc.mapper.chat.ChatRecordMapper;
import com.oc.service.chat.ChatRecordService;

import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月24日
 * @version v 1.0
 */
@Service
public class ChatRecordServiceImpl implements ChatRecordService{
	
	@Autowired
	private ChatRecordMapper chatRecordMapper;

	@Override
	public List<ChatRecordStatistics> obtainChatRecordStatistics(String chatId) {
		return chatRecordMapper.obtainChatRecordStatistics(chatId);
	}

	@Override
	public int insert(ChatRecord record) {
		return chatRecordMapper.insert(record);
	}

	@Override
	public int revocationChatRecord(ChatRecord chatRecord) {
		return chatRecordMapper.revocationChatRecord(chatRecord);
	}

	@Override
	public List<ChatRecord> obtainBefore(String tenantCode, Long id, String customerCode) {
		return chatRecordMapper.obtainBefore(tenantCode, id, customerCode);
	}
}
