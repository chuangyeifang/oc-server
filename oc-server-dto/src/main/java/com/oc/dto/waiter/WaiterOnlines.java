package com.oc.dto.waiter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class WaiterOnlines {

    private String teamCode;

    private String teamName;

    private Integer sort;

    private List<WaiterDto> waiters;
}
