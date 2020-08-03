/**
 * 
 */
package com.oc.cluster.manager;

import com.hazelcast.core.*;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.oc.cluster.node.ClusterNode;
import com.oc.cluster.node.LocalClusterNode;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Description: 集群事件监听
 * @author chuangyeifang
 * @createDate 2020年7月30日
 * @version v 1.0
 */
public class ClusterListener implements MembershipListener, LifecycleListener{

	private final static Logger log = LoggerFactory.getLogger(ClusterListener.class);
	
	private final Cluster cluster;
	
	private final Map<String, ClusterNode> clusterNodeInfos = PlatformDependent.newConcurrentHashMap();
	
	public ClusterListener(Cluster cluster) {
		this.cluster = cluster;
		
		for (Member member : cluster.getMembers()) {
			clusterNodeInfos.put(member.getUuid(), new LocalClusterNode(member, cluster.getClusterTime()));
		}
	}

	@Override
	public void stateChanged(LifecycleEvent event) {
		log.info("集群节点状态改变 ：{}" ,event.getState());
		if (event.getState() == LifecycleState.SHUTDOWN) {
			log.info("Hazelcast 关闭");
		}  else if (event.getState() == LifecycleState.STARTED){
			log.info("Hazelcast 启动成功");
		}
	}
	
	@Override
	public void memberAdded(MembershipEvent membershipEvent) {
		log.info("Node ID : {} 节点加入集群.", membershipEvent.getMember().getUuid().toString());
		clusterNodeInfos.put(membershipEvent.getMember().getUuid(), new LocalClusterNode(membershipEvent.getMember(), cluster.getClusterTime()));
	}

	@Override
	public void memberRemoved(MembershipEvent membershipEvent) {
		String nodeIDStr = membershipEvent.getMember().getUuid();
		log.info("Node ID : {} 节点离开集群.", nodeIDStr);
		clusterNodeInfos.remove(nodeIDStr);
	}

	@Override
	public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
		log.info("Node ID : {} 节点属性变化.", memberAttributeEvent.getMember().getUuid());
		ClusterNode priorNodeInfo = clusterNodeInfos.get(memberAttributeEvent.getMember().getUuid());
		clusterNodeInfos.put(memberAttributeEvent.getMember().getUuid(), new LocalClusterNode(memberAttributeEvent.getMember(), priorNodeInfo.getJoinTime()));
	}
}
