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
import com.oc.routing.WaiterRoute;
import com.oc.utils.ExternalizableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 删除集群中已存在的客服信息
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public class RmWaiterSessionExecution implements ClusterTask<Void> {

	private static Logger log = LoggerFactory.getLogger(RmWaiterSessionExecution.class);

	private String taskId;

	private WaiterRoute waiterRoute;
	
	public RmWaiterSessionExecution() {
		this.taskId = UUIDUtils.getUUID();
	}
	
	public RmWaiterSessionExecution(WaiterRoute waiterRoute) {
		this();
		this.waiterRoute = waiterRoute;
	}

	@Override
	public void run() {
		OcImServer.getInst().getRoutingTable().removeRemoteWaiterSession(waiterRoute);
		log.info("[处理删除集群中已存在的客服消息]-任务 taskId: {} - waiterRoute: {}", taskId, waiterRoute);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, taskId);
		ExternalizableUtil.getInstance().writeSerializable(out, waiterRoute);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		taskId = ExternalizableUtil.getInstance().readSafeUTF(in);
		waiterRoute = (WaiterRoute)ExternalizableUtil.getInstance().readSerializable(in);
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
