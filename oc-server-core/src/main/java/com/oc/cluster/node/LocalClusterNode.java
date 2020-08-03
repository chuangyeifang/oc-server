/**
 * 
 */
package com.oc.cluster.node;

import java.nio.charset.StandardCharsets;

import com.hazelcast.core.Member;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月30日
 * @version v 1.0
 */
public class LocalClusterNode implements ClusterNode {

	private String hostName;
	
	private NodeID nodeID;
	
	private long joinTime;
	
	private boolean masterMember;
	
	public LocalClusterNode(Member member, Long joinTime) {
		hostName = member.getSocketAddress(EndpointQualifier.MEMBER).getHostName();
		nodeID = new NodeID(member.getUuid().getBytes(StandardCharsets.UTF_8));
		this.joinTime = joinTime;
	}
	
	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public NodeID getNodeID() {
		return nodeID;
	}

	@Override
	public long getJoinTime() {
		return joinTime;
	}

	@Override
	public boolean isMasterMember() {
		return masterMember;
	}
}
