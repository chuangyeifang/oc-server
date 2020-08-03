package com.oc.session;

/**
 * @author chuangyeifang
 */

public enum CustomerAssignStatus {

    /**
     * 未分配
     */
    UNASSIGNED(0),

    /**
     * 已分配
     */
    ASSIGNED(1),

    /**
     * 正在分配
     */
    ASSIGNING(2);



    private Integer value;

    CustomerAssignStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
