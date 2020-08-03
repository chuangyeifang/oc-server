package com.oc.domain.waiter;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
public class WaiterLog {

    private Long id;

    private String tenantCode;

    private Integer teamCode;

    private String waiterName;

    private String waiterCode;

    private String ip;

    private String status;

    private String type;

    private Date createDate;
}