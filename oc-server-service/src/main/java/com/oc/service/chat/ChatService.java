package com.oc.service.chat;

import com.oc.domain.chat.Chat;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月24日
 * @version v 1.0
 */
public interface ChatService {

    /**
     * 保存会话
     * @param record
     * @return
     */
    int insert(Chat record);

    /**
     * 更新、关闭会话
     * @param record
     * @return
     */
    int updateChatClose(Chat record);
}
