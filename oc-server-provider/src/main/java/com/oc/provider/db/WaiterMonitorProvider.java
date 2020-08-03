package com.oc.provider.db;

import com.oc.domain.waiter.Waiter;
import com.oc.provider.context.SpringContext;
import com.oc.service.user.WaiterMonitorService;
import com.oc.service.user.impl.WaiterMonitorServiceImpl;

/**
 * 客服监控
 * @author chuangyeifang
 */
public class WaiterMonitorProvider {

    private WaiterMonitorService waiterMonitorService = SpringContext.getBean(WaiterMonitorServiceImpl.class);

    private WaiterMonitorProvider() {}

    public void updateStatus(Waiter waiter, String beforeStatus, String afterStatus) {
        waiterMonitorService.updateStatus(waiter, beforeStatus, afterStatus);
    }

    public void waiterEnterService(Waiter waiter, String status) {
        waiterMonitorService.waiterEnterService(waiter, status);
    }

    public void updateSysBusyTime(Integer teamCode, String waiterCode, Long time) {
        waiterMonitorService.updateSysBusyTime(teamCode, waiterCode, time);
    }

    public static WaiterMonitorProvider getInst() {
        return Single.inst;
    }

    private static class Single {
        private static WaiterMonitorProvider inst = new WaiterMonitorProvider();
    }
}
