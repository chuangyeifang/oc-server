package com.oc.cluster.task;

/**
 * Cluster 任务执行状态
 * @author chuangyeifang
 */

public enum TaskExecutionStatus {
    /**
     * 执行成功
     */
    success(100, "成功");

    private Integer rc;

    private String rm;

    TaskExecutionStatus(Integer rc, String rm) {
        this.rc = rc;
        this.rm = rm;
    }

    public Integer getRc() {
        return rc;
    }

    public String getRm() {
        return rm;
    }
}
