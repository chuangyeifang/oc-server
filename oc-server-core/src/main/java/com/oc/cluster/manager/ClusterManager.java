/**
 * 
 */
package com.oc.cluster.manager;

import java.nio.charset.StandardCharsets;

import com.oc.cluster.node.NodeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.collection.cache.CacheFactory;
import com.oc.cluster.collection.queue.CustomQueueFactory;
import com.oc.cluster.collection.set.CustomSetFactory;

/**
 * @Description: 集群管理
 * @author chuangyeifang
 * @createDate 2020年5月30日
 * @version v 1.0
 */
public class ClusterManager {
	
	private static Logger log = LoggerFactory.getLogger(ClusterManager.class);
	
	private HazelcastInstance hazelcastInstance;
	private Cluster cluster;
	private ClusterListener clusterListener;

	private State state = State.stopped;
	
	public ClusterManager(HazelcastInstance hi) {
		hazelcastInstance = hi;
	}
	
	public NodeID startCluster() {
		log.info("正在启动 Hazelcast 集群");
		state = State.starting;

		cluster = hazelcastInstance.getCluster();
		clusterListener = new ClusterListener(cluster);
		
		hazelcastInstance.getLifecycleService().addLifecycleListener(clusterListener);
		cluster.addMembershipListener(clusterListener);
		
		CacheFactory.startCluster(hazelcastInstance);
		CustomQueueFactory.startCluster(hazelcastInstance);
		CustomSetFactory.startCluster(hazelcastInstance);
		
		state = State.started;
		log.info("成功启动 Hazelcast 集群");
		
		return new NodeID(cluster.getLocalMember().getUuid().getBytes(StandardCharsets.UTF_8));
	}
	
	public State getClusterState() {
		return state;
	}
	
	public enum State {
		/**
		 * 已停止
		 */
		stopped,
		/**
		 * 启动中
		 */
		starting,
		/**
		 * 已启动
		 */
		started;
	}
}
