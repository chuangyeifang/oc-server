/**
 * 
 */
package com.oc.service.chat.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.chat.Chat;
import com.oc.mapper.chat.ChatMapper;
import com.oc.service.chat.ChatService;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月24日
 * @version v 1.0
 */
@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	private ChatMapper chatMapper;

	@Override
	public int insert(Chat record) {
		return chatMapper.insert(record);
	}

	@Override
	public int updateChatClose(Chat record) {
		return chatMapper.updateChat(record);
	}
}
