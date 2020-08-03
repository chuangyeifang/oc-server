package com.oc.domain.chat;

import lombok.*;

import java.util.Date;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Chat {

    private Long id;

    private String chatId;

    private String tenantCode;
    
    private Integer teamCode;

    private Integer skillCode;
    
    private String goodsCode;
    
    private String customerName;

    private String customerCode;

    private String waiterName;

    private String waiterCode;

    private String isLogin;

    private String isTransfer;

    private String type;

    private String isEffective;

    private Integer waitingTime;

    private Integer msgTotal;

    private Integer waiterMsgTotal;

    private Integer customerMsgTotal;

    private String opinion;

    private String suggest;

    private String deviceType;
    
    private String closeType;

    private String createTime;

    private Date endTime;
}