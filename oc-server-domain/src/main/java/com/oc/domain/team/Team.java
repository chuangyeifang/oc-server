package com.oc.domain.team;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class Team {

	private Integer teamCode;

    private String tenantCode;

    private String teamName;

    private String briefName;

    private String needLogin;
    
    private String loginUrl;
    
    private String assignRule;
    
    private String beginTime;
    
    private String endTime;
    
    private String offlineMessage;
    
    private String autoReply;
    
    private String replyMsg;
    
    private String flag;

    private String createTime;
}