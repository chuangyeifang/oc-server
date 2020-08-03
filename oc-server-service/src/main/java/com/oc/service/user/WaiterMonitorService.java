package com.oc.service.user;

import com.oc.domain.waiter.Waiter;
import com.oc.domain.waiter.WaiterMonitor;

/**
 * 客服监控
 * @author chuangyeifang
 */
public interface WaiterMonitorService {

    /**
     * 客服进入服务状态
     * @param waiter
     * @param status
     * @return
     */
    int waiterEnterService(Waiter waiter, String status);

    /**
     * 更新时间
     * @param waiter
     * @param beforeStatus
     * @param afterStatus
     * @return
     */
    int updateStatus(Waiter waiter, String beforeStatus, String afterStatus);

    /**
     * 更新系统忙碌时间
     * @param teamCode
     * @param waiterCode
     * @param time
     * @return
     */
    int updateSysBusyTime(Integer teamCode, String waiterCode, Long time);

    /**
     * 获取客服当日监控数据
     * @param waiterCode
     * @return
     */
    WaiterMonitor obtainWaiterMonitorOfDay(String waiterCode);
}
