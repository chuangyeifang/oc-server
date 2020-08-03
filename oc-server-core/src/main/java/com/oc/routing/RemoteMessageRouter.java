package com.oc.routing;

import com.oc.cluster.collection.RemoteTaskResult;
import com.oc.cluster.node.NodeID;
import com.oc.cluster.task.hazelcast.PacketExecution.RemotePacketType;
import com.oc.dispatcher.register.Event;
import com.oc.message.Packet;
import com.oc.transfer.TransferWaiter;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public interface RemoteMessageRouter {

	/**
	 * 路由分发消息
	 * @param nodeId
	 * @param packet
	 * @param type
	 * @return
	 */
	RemoteTaskResult routePacket(NodeID nodeId, Packet packet, RemotePacketType type);

	/**
	 * 删除集群中已经存在的客户Session
	 * @param nodeId
	 * @param customerRoute
	 */
	void routeRemoveCustomerSession(NodeID nodeId, CustomerRoute customerRoute);

	/**
	 * 删除集群中已经存在的客服Session
	 * @param nodeId
	 * @param waiterRoute
	 */
	void routeRemoveWaiterSession(NodeID nodeId, WaiterRoute waiterRoute);

	/**
	 * 路由分发转接
	 * @param nodeId
	 * @param transferWaiter
	 */
	void routeTransferByWaiter(NodeID nodeId, TransferWaiter transferWaiter);

	/**
	 * 路由分发分配事件
	 * @param nodeId
	 * @param event
	 */
	void routeEvent(NodeID nodeId, Event event);
}
