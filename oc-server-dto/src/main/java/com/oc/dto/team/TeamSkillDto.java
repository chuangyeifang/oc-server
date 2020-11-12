package com.oc.dto.team;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class TeamSkillDto {

    private String tenantCode;

    private Integer teamCode;

    private String teamName;

    private Integer skillCode;

    private String skillName;

    private String flag;

    private String createTime;
}
