package com.oc.routing;

import com.oc.cluster.node.NodeID;

/**
 * 路由信息
 * @author chuangyeifang
 */
public interface Route {
    /**
     * 获取用户ID
     * @return
     */
    String getUid();

    /**
     * 获取集群节点ID
     * @return
     */
    NodeID getNodeID();

    /**
     * 获取版本信息
     * @return
     */
    String getVersion();
}
