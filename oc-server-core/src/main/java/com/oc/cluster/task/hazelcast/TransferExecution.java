/**
 * 
 */
package com.oc.cluster.task.hazelcast;

import com.oc.cluster.task.ClusterTask;
import com.oc.common.utils.UUIDUtils;
import com.oc.core.OcImServer;
import com.oc.transfer.TransferWaiter;
import com.oc.utils.ExternalizableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @Description: 分发按客服转接任务
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public class TransferExecution implements ClusterTask<Void> {

	private static Logger log = LoggerFactory.getLogger(TransferExecution.class);

	private String taskId;

	private TransferWaiter transferWaiter;
	
	public TransferExecution() {
		this.taskId = UUIDUtils.getUUID();
	}
	
	public TransferExecution(TransferWaiter transferWaiter) {
		this();
		this.transferWaiter = transferWaiter;
	}

	@Override
	public void run() {
		if (null != transferWaiter) {
			OcImServer.getInst().getRoutingTable().routeTransferByWaiter(transferWaiter);
		}
		log.info("[处理按客服转接]-任务 taskId: {} - transferWaiter: {}", taskId, transferWaiter);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, taskId);
		ExternalizableUtil.getInstance().writeSerializable(out, transferWaiter);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		taskId = ExternalizableUtil.getInstance().readSafeUTF(in);
		transferWaiter = (TransferWaiter)ExternalizableUtil.getInstance().readSerializable(in);
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
