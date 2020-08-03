/**
 * 
 */
package com.oc.cluster.task.hazelcast;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.oc.cluster.task.ClusterTask;
import com.oc.common.utils.UUIDUtils;
import com.oc.dispatcher.register.Event;
import com.oc.core.OcImServer;
import com.oc.utils.ExternalizableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 路由分发事件
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public class EventExecution implements ClusterTask<Void> {

	private static Logger log = LoggerFactory.getLogger(EventExecution.class);

	private String taskId;

	private Event event;
	
	public EventExecution() {
		this.taskId = UUIDUtils.getUUID();
	}
	
	public EventExecution(Event event) {
		this();
		this.event = event;
	}

	@Override
	public void run() {
		if (null != event) {
			OcImServer.getInst().getDispatcher().buildRelation(event);
		}
		log.info("[处理分配事件]-任务 taskId: {} - event: {}", taskId, event);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, taskId);
		ExternalizableUtil.getInstance().writeSerializable(out, event);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		taskId = ExternalizableUtil.getInstance().readSafeUTF(in);
		event = (Event)ExternalizableUtil.getInstance().readSerializable(in);
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
