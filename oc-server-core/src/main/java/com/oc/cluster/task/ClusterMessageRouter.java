/**
 * 
 */
package com.oc.cluster.task;

import com.oc.cluster.node.NodeID;
import com.oc.cluster.task.hazelcast.*;
import com.oc.cluster.task.hazelcast.PacketExecution.RemotePacketType;
import com.oc.dispatcher.register.Event;
import com.oc.core.OcImServer;
import com.oc.message.Packet;
import com.oc.cluster.collection.cache.CacheFactory;
import com.oc.cluster.collection.RemoteTaskResult;
import com.oc.routing.CustomerRoute;
import com.oc.routing.RemoteMessageRouter;
import com.oc.routing.WaiterRoute;
import com.oc.transfer.TransferWaiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 服务器间消息路由
 * @author chuangyeifang
 * @createDate 2020年1月16日
 * @version v 1.0
 */
public class ClusterMessageRouter implements RemoteMessageRouter {

	private static final Logger log = LoggerFactory.getLogger(ClusterMessageRouter.class);
	
	public ClusterMessageRouter() {}
	
	/**
	 * 路由消息
	 * @param nodeId
	 * @param packet
	 * @param type
	 * @return
	 */
	@Override
	public RemoteTaskResult routePacket(NodeID nodeId, Packet packet, RemotePacketType type) {
		try {
			PacketExecution task = new PacketExecution(packet, type);
			log.info("[分发消息]-任务 taskId:{} - body:{} - sourceNode:{} - targetNode: {}",
					task.getTaskId(), packet, nodeId,
					OcImServer.getInst().getNodeId());
			return CacheFactory.doSynchronousClusterTask(task, nodeId.getBytes());
		} catch (IllegalStateException  e) {
			log.warn("消息路由到远程节点时出错: " + e);
	    }
		return new RemoteTaskResult(106, "未知错误");
	}

	/**
	 * 路由移除客户
	 * @param nodeId
	 * @param customerRoute
	 */
	@Override
	public void routeRemoveCustomerSession(NodeID nodeId, CustomerRoute customerRoute) {
		try {
			RmCustomerSessionExecution task = new RmCustomerSessionExecution(customerRoute);
			log.info("[移除集群中存在客户信息]-任务 taskId:{} - body:{} - sourceNode:{} - targetNode: {}",
					task.getTaskId(), customerRoute,
					nodeId.toString(),
					OcImServer.getInst().getNodeId());
			CacheFactory.doSynchronousClusterTask(task, nodeId.getBytes());
			return;
		} catch (IllegalStateException  e) {
			log.warn("消息路由到远程节点时出错: " + e);
	    }
		new RemoteTaskResult(106, "未知错误");
	}
	
	/**
	 * 路由移除客服
	 * @param nodeId
	 * @param waiterRoute
	 */
	@Override
	public void routeRemoveWaiterSession(NodeID nodeId, WaiterRoute waiterRoute) {
		try {
			RmWaiterSessionExecution task = new RmWaiterSessionExecution(waiterRoute);
			log.info("[移除集群中存在的客服信息]-任务 taskId:{} - body:{} - sourceNode:{} - targetNode: {}",
					task.getTaskId(), waiterRoute,
					nodeId.toString(),
					OcImServer.getInst().getNodeId());
			CacheFactory.doSynchronousClusterTask(task, nodeId.getBytes());
			return;
		} catch (IllegalStateException  e) {
			log.warn("消息路由到远程节点时出错: " + e);
	    }
		new RemoteTaskResult(106, "未知错误");
	}

	/**
	 * 路由注册事件
	 * @param nodeId
	 * @param event
	 */
	@Override
	public void routeEvent(NodeID nodeId, Event event) {
		try {
			EventExecution task = new EventExecution(event);
			log.info("[分发分配事件]-任务 taskId:{} - body:{} - sourceNode:{} - targetNode: {}",
					task.getTaskId(), event,
					nodeId.toString(),
					OcImServer.getInst().getNodeId());
			CacheFactory.doSynchronousClusterTask(task, nodeId.getBytes());
			return;
		} catch (IllegalStateException  e) {
			log.warn("消息路由到远程节点时出错: {}, 事件 Event：{}", e, event);
	    }
		new RemoteTaskResult(106, "未知错误");
	}

	/**
	 * 路由转接信息
	 * @param nodeId
	 * @param transferWaiter
	 */
	@Override
	public void routeTransferByWaiter(NodeID nodeId, TransferWaiter transferWaiter) {
		try {
			TransferExecution task = new TransferExecution(transferWaiter);
			log.info("[分发转接事件]-任务 taskId:{} - body:{} - sourceNode:{} - targetNode: {}",
					task.getTaskId(), transferWaiter,
					nodeId.toString(),
					OcImServer.getInst().getNodeId());
			CacheFactory.doSynchronousClusterTask(task, nodeId.getBytes());
			return;
		} catch (IllegalStateException  e) {
			log.warn("消息路由到远程节点时出错: " + e);
	    }
		new RemoteTaskResult(106, "未知错误");
	}
}
