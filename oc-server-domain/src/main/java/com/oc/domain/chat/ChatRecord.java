package com.oc.domain.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class ChatRecord {

    private Long id;
    
    private String tenantCode;
    
    private Integer teamCode;
    
    private String chatId;
    
    private String messageId;

    private String ownerType;

    private String waiterCode;

    private String waiterName;

    private String customerCode;
    
    private String customerName;

    private String messageType;
    
    private String offline;

    private Integer revocation;
    
    private String messages;
    
    private String createTime;
}