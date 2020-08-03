package com.oc.domain.team;

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
public class TeamMonitor {

    private Long id;

    private String tenantCode;

    private Integer teamCode;

    private Date startTime;

    private Integer waitCount;

    private Date createDate;
}