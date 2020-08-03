package com.oc.mapper.chat;

import com.oc.domain.chat.Chat;

/**
 * @author chuangyeifang
 */
public interface ChatMapper {
	/**
	 * 保存会话
	 * @param record
	 * @return
	 */
	int insert(Chat record);

	/**
	 * 更新会话
	 * @param record
	 * @return
	 */
	int updateChat(Chat record);
}