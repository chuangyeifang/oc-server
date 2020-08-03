package com.oc.restcontroller.waiter;

import com.oc.auth.UserStore;
import com.oc.auth.WaiterAuthCoder;
import com.oc.auth.WaiterInfo;
import com.oc.domain.waiter.Waiter;
import com.oc.restcontroller.AbstractBasicRestController;
import com.oc.service.user.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服管理
 * @author chuangyeifang
 */
@RestController
@RequestMapping(value = "waiter")
public class WaiterRestController extends AbstractBasicRestController {

    @Autowired
    private WaiterService waiterService;

    @PostMapping(value = "login")
    public Object login(String tenantCode, String waiterName, String password) {
        // 判断参数是否为空
        if (existEmpty(tenantCode, waiterName, password)) {
            return failed(2, "无效参数");
        }
        // 去除前后空格
        waiterName = waiterName.trim();

        Waiter waiter = waiterService.login(tenantCode, waiterName, password);
        if (waiter == null) {
            return failed(1, "账号或者密码错误");
        }

        String token = WaiterAuthCoder.encode(waiter);
        return success(token);
    }

    @PostMapping(value = "logout")
    public Object logout() {
        WaiterInfo waiterInfo = UserStore.get();
        waiterService.logout(waiterInfo.getWaiterCode());
        return success();
    }
}
