/**
 * 
 */
package com.oc.cluster.task.hazelcast;

import com.oc.cluster.task.ClusterTask;
import com.oc.common.utils.UUIDUtils;
import com.oc.core.OcImServer;
import com.oc.message.Packet;
import com.oc.utils.ExternalizableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @Description: 路由分发消息
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public class PacketExecution implements ClusterTask<Void> {

	private static Logger log = LoggerFactory.getLogger(PacketExecution.class);

	private String taskId;

	private Packet packet;
	
	private RemotePacketType type;
	
	public PacketExecution() {
		this.taskId = UUIDUtils.getUUID();
		this.type = RemotePacketType.NORMAL;
	}
	
	public PacketExecution(Packet packet, RemotePacketType type) {
		this.taskId = UUIDUtils.getUUID();
		this.packet = packet;
		this.type = type;
	}

	@Override
	public void run() {
		if (type == RemotePacketType.NORMAL) {
			OcImServer.getInst().getRoutingTable().routePacket(packet);
		} else if (type == RemotePacketType.CHAT_CLOSE) {
			OcImServer.getInst().getRoutingTable().routeChatClose(packet);
		}
		log.info("[处理分发消息]-任务 taskId: {} - type: {} - packet: {}", taskId, type.name(), packet);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.getInstance().writeSafeUTF(out, type.name());
		ExternalizableUtil.getInstance().writeSafeUTF(out, taskId);
		ExternalizableUtil.getInstance().writeSerializable(out, packet);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		String typeName = ExternalizableUtil.getInstance().readSafeUTF(in);
		if (null != typeName) {
			type = RemotePacketType.valueOf(typeName);
		}
		taskId = ExternalizableUtil.getInstance().readSafeUTF(in);
		packet = (Packet)ExternalizableUtil.getInstance().readSerializable(in);
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public Void getResult() {
		return null;
	}
	
	public enum RemotePacketType {
		/**
		 * 正常消息
		 */
		NORMAL,
		/**
		 * 客服关闭消息
		 */
		CHAT_CLOSE;
	}
}
