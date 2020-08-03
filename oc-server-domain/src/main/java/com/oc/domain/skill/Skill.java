package com.oc.domain.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class Skill {

    private Integer skillCode;

    private String tenantCode;

    private String skillName;

    private String needLogin;

    private String greeting;

    private Date startTime;

    private Date endTime;

    private Date createTime;
}