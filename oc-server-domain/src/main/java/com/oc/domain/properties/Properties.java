package com.oc.domain.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class Properties {

    private Integer id;

    private String tenantCode;

    private Integer teamCode;

    private String timeoutTipMessage;

    private String timeoutCloseMessage;
}