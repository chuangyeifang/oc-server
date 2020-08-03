package com.oc.mapper.chat;

import com.oc.domain.chat.ChatRecord;
import com.oc.dto.chat.ChatRecordStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chuangyeifang
 */
public interface ChatRecordMapper {

    /**
     * 返回客服、客户、Robot 消息数
     * @param chatId
     * @return
     */
    List<ChatRecordStatistics> obtainChatRecordStatistics(@Param("chatId") String chatId);

    /**
     * 保存聊天记录
     * @param record
     * @return
     */
    int insert(ChatRecord record);

    /**
     * 消息撤回
     * @param record
     * @return
     */
    int revocationChatRecord(ChatRecord record);

    /**
     * 获取聊天记录
     * @param tenantCode
     * @param id
     * @param customerCode
     * @return
     */
    List<ChatRecord> obtainBefore(@Param("tenantCode") String tenantCode,
                                  @Param("id") Long id,
                                  @Param("customerCode") String customerCode);
}