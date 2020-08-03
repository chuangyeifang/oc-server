package com.oc.domain.waiter;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
public class WaiterMonitor {

    private Long id;

    private String tenantCode;

    private Integer teamCode;

    private String waiterCode;

    private String waiterName;

    private Integer receptionCount;

    private Integer status;

    private Long onlineTime;

    private Long waiterBusyTime;

    private Long waiterHangTime;

    private Long sysBusyTime;

    private Date statusUpdateTime;

    private Date serviceBeginTime;

    private Date serviceEndTime;

    private Date createDate;
}