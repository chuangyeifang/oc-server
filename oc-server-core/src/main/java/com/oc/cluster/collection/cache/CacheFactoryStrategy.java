/**
 * 
 */
package com.oc.cluster.collection.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.oc.cluster.collection.RemoteTaskResult;
import com.oc.cluster.node.ClusterNode;
import com.oc.cluster.task.ClusterTask;

/**
 * 缓存策略工厂
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月18日
 * @version v 1.0
 */
public interface CacheFactoryStrategy {

	/**
	 * 创建缓存
	 * @param name
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	<K, V> Cache<K, V> createCache(String name);
	
	/**
	 * 根据指定名称、最大存活时间（毫秒）、最大缓存大小创建缓存
	 * @param name
	 * @param maxLifetime
	 * @param maxCacheSize
	 * @return
	 */
	<K, V> Cache<K, V> createCache(String name, long maxLifetime, long maxCacheSize);
	
	/**
	 * 销毁指定的缓存
	 * @param cache
	 */
	void destroyCache(Cache<?, ?> cache);
	
	/**
	 * 该成员如果为Master，则返回true
	 * @return
	 */
	boolean isMasterClusterMember();
	
	/**
	 * 获取集群当前成员的信息
	 * @return
	 */
	Collection<ClusterNode> getClusterNodeInfo();
	
	/**
	 * 获取集群成员ID
	 * @return
	 */
	byte[] getClusterMemberID();

	/**
	 * 获取集群时间
	 * @return
	 */
	long getClusterTime();
	
	/**
	 * 异步集群中执行任务
	 * @param task 具体任务
	 * @return
	 */
	boolean doClusterTask(final ClusterTask<?> task);
	
	/**
	 * 异步指定集群成员执行任务
	 * @param task
	 * @param nodeID
	 * @return
	 */
	boolean doClusterTask(final ClusterTask<?> task, byte[] nodeID);
	
	/**
	 * 同步指定集群成员执行任务
	 * @param task
	 * @param nodeID
	 * @return
	 */
	RemoteTaskResult doSynchronousClusterTask(final ClusterTask<?> task, byte[] nodeID);
	
	/**
	 * 更新集群信息
	 * @param cachs
	 */
	void updateClusterStats(Map<String, Cache<?, ?>> cachs);
	
	/**
	 * 返回指定nodeID的节点信息
	 * @param nodeID
	 * @return
	 */
	ClusterNode getClusterNodeInfo(byte[] nodeID);
	
	/**
	 * 返回当前缓存的锁
	 * @param key
	 * @param cache
	 * @return
	 */
	Lock getLock(Object key, Cache<?, ?> cache);
}
