/**
 * 
 */
package com.oc.restcontroller.dashboard;

import java.util.Collection;
import java.util.List;

import com.oc.routing.CustomerRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.HazelcastInstance;
import com.oc.core.OcImServer;
import com.oc.restcontroller.AbstractBasicRestController;
import com.oc.session.CustomerSession;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月7日
 * @version v 1.0
 */
@RestController
@RequestMapping("dashboard/customer")
public class CustomerViewRestController extends AbstractBasicRestController {

	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	@RequestMapping("local/session/list")
	public Object getLocalCustomer(String ttc) {
		Collection<CustomerSession> list = OcImServer.getInst().getRoutingTable().getLocalCustomerSessions();
		return success(list);
	}

	@RequestMapping("local/route/list")
	public Object getLocalCustomerRoute(String ttc) {
		List<CustomerRoute> list = OcImServer.getInst().getRoutingTable().getCustomerRoutes();
		return success(list);
	}
}
