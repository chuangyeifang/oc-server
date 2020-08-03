package com.oc.restcontroller.waiter;

import com.oc.auth.UserStore;
import com.oc.auth.WaiterInfo;
import com.oc.domain.waiter.WaiterMonitor;
import com.oc.restcontroller.AbstractBasicRestController;
import com.oc.service.user.WaiterMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuangyeifang
 */
@RestController
@RequestMapping(value = "waiterMonitor")
public class WaiterMonitorRestController extends AbstractBasicRestController {

    @Autowired
    private WaiterMonitorService waiterMonitorService;

    @PostMapping("receptions")
    public Object getReceptionCount() {
        WaiterInfo waiterInfo = UserStore.get();

        WaiterMonitor waiterMonitor = waiterMonitorService.obtainWaiterMonitorOfDay(waiterInfo.getWaiterCode());
        if (null != waiterInfo) {
            return success(waiterMonitor.getReceptionCount());
        } else {
            return success(0);
        }
    }
}
