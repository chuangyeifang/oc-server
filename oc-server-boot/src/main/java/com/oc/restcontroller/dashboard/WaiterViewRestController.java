/**
 * 
 */
package com.oc.restcontroller.dashboard;

import java.util.Collection;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oc.core.OcImServer;
import com.oc.restcontroller.AbstractBasicRestController;
import com.oc.session.WaiterSession;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月7日
 * @version v 1.0
 */
@RestController
@RequestMapping("dashboard/waiter")
public class WaiterViewRestController extends AbstractBasicRestController {

	@RequestMapping("local/list")
	public Object getLocalWaiters() {
		Collection<WaiterSession> list = OcImServer.getInst().getRoutingTable().getLocalWaiterSessions();
		return success(list);
	}
}
