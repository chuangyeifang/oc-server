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
public class TeamSkill {
	
    private String tenantCode;

    private Integer teamCode;

    private Integer skillCode;

    private String skillName;

    private String flag;

    private String createTime;
}