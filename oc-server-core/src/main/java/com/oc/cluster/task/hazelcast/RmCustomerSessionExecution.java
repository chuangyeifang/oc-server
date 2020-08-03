/**
 * 
 */
package com.oc.cluster.task.hazelcast;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.oc.cluster.task.ClusterTask;
import com.oc.common.utils.UUIDUtils;
import com.oc.core.OcImServer;
import com.oc.routing.CustomerRoute;
import com.oc.utils.ExternalizableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 删除集群中已存在的客户信息
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public class RmCustomerSessionExecution implements ClusterTask<Void> {

	private static Logger log = LoggerFactory.getLogger(RmCustomerSessionExecution.class);

	private String taskId;

	private CustomerRoute customerRoute;
	
	public RmCustomerSessionExecution() {
		this.taskId = UUIDUtils.getUUID();
	}
	
	public RmCustomerSessionExecution(CustomerRoute customerRoute) {
		this();
		this.customerRoute = customerRoute;
	}

	@Override
	public void run() {
		OcImServer.getInst().getRoutingTable().removeRemoteCustomerSession(customerRoute);
		log.info("[处理删除集群中已存在的客户消息]-任务 taskId: {} - customerRoute: {}", taskId, customerRoute);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, taskId);
		ExternalizableUtil.getInstance().writeSerializable(out, customerRoute);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		taskId = ExternalizableUtil.getInstance().readSafeUTF(in);
		customerRoute = (CustomerRoute)ExternalizableUtil.getInstance().readSerializable(in);
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public Void getResult() {
		return null;
	}
}
