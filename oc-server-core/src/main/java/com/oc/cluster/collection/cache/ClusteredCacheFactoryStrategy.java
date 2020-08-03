/**
 * 
 */
package com.oc.cluster.collection.cache;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.oc.cluster.collection.RemoteTaskResult;
import com.oc.cluster.collection.cache.hazelcast.ClusteredCache;
import com.oc.cluster.node.ClusterNode;
import com.oc.cluster.node.LocalClusterNode;
import com.oc.cluster.task.ClusterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月18日
 * @version v 1.0
 */
public class ClusteredCacheFactoryStrategy implements CacheFactoryStrategy{
	
	private static final Logger log = LoggerFactory.getLogger(ClusteredCacheFactoryStrategy.class);
	
	private static final String HAZELCAST_EXECUTOR_SERVICE_NAME = "oc:cluster:executor";
	private static final long MAX_CLUSTER_EXECUTION_TIME = 5;

	private static final String HZ_MAP_SET_NAME = "default";

	private HazelcastInstance hazelcastInstance;
	private Cluster cluster;
	
	public ClusteredCacheFactoryStrategy(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
		this.cluster = hazelcastInstance.getCluster();
	}
	
	/**
	 * @param name
	 * @return
	 */
	@Override
	public <K, V> Cache<K, V> createCache(String name) {
		//存活时间--毫秒转换为妙 。 说明hazelcast 0 代表永久有效而不是-1，本地缓存-1代表永久有效
		long maxLifeTime = CacheFactory.getMaxLifeTime(name);
		final int hazelcastLifetimeInSeconds = maxLifeTime < 0 ? 0 : (int)maxLifeTime / 1000;
		
		//缓存大小  hazelcast 必须设置， -1表示Integer最大值
		long maxCacheSize = CacheFactory.getMaxCacheSize(name);
		return createCache(name, maxCacheSize, hazelcastLifetimeInSeconds);
	}
	
	/**
	 * @param name
	 * @param maxLifeTime
	 * @param maxCacheSize
	 * @return
	 */
	@Override
	public <K, V> Cache<K, V> createCache(String name, long maxLifeTime, long maxCacheSize) {
		//存活时间--毫秒转换为妙 。 说明hazelcast 0 代表永久有效而不是-1，本地缓存-1代表永久有效
		final int hazelcastLifetimeInSeconds = maxLifeTime < 0 ? 0 : (int)maxLifeTime / 1000;
		
		//缓存大小  hazelcast 必须设置， -1表示Integer最大值
		return createCache(name, maxCacheSize, hazelcastLifetimeInSeconds);
	}

	/**
	 * @param cache
	 */
	@Override
	public void destroyCache(Cache<?, ?> cache) {
		ClusteredCache<?, ?> clustered = (ClusteredCache<?, ?>) cache;
		clustered.destroy();
	}

	/**
	 * @return
	 */
	@Override
	public boolean isMasterClusterMember() {
		return cluster.getLocalMember().isLiteMember();
	}

	/**
	 * @return
	 */
	@Override
	public Collection<ClusterNode> getClusterNodeInfo() {
		return null;
	}

	/**
	 * @return
	 */
	@Override
	public byte[] getClusterMemberID() {
		return hazelcastInstance.getCluster().getLocalMember().getUuid().getBytes();
	}

	/**
	 * @return
	 */
	@Override
	public long getClusterTime() {
		return cluster == null ? System.currentTimeMillis() : cluster.getClusterTime();
	}

	/**
	 * @param task
	 */
	@Override
	public boolean doClusterTask(ClusterTask<?> task) {
		return false;
	}

	/**
	 * @param task
	 * @param nodeID
	 */
	@Override
	public boolean doClusterTask(ClusterTask<?> task, byte[] nodeID) {
		if (null == cluster) {
			return false;
		}
		Member member = getMember(nodeID);
		if (null != member) {
			hazelcastInstance.getExecutorService(HAZELCAST_EXECUTOR_SERVICE_NAME).submitToMember(new CallableTask<>(task), member);
			return true;
		} else {
			log.error("请求节点 {} 在集群中不能找到", new String(nodeID, StandardCharsets.UTF_8));
			return false;
		}
	}
	
	/**
	 * 100 执行成功 101执行缺失目标 102集群不可用 103执行超时 104执行被打断 105 执行发生异常 
	 * @param task
	 * @param nodeID
	 * @return
	 */
	@Override
	public RemoteTaskResult doSynchronousClusterTask(ClusterTask<?> task, byte[] nodeID) {
		RemoteTaskResult result;
		if (null == cluster) {
			result = new RemoteTaskResult(102, "集群不可用");
		} else {
			Member member = getMember(nodeID);
			if (null != member) {
				Future<?> future = hazelcastInstance.getExecutorService(HAZELCAST_EXECUTOR_SERVICE_NAME).submitToMember(new CallableTask<>(task), member);
					try {
						Object object = future.get(MAX_CLUSTER_EXECUTION_TIME, TimeUnit.SECONDS);
						if (future.isDone()) {
							result = new RemoteTaskResult(100, "执行成功", object);
						} else {
							future.cancel(true);
							result = new RemoteTaskResult(106, "执行任务超时", object);
						}
					} catch (TimeoutException e) {
						result = new RemoteTaskResult(103, "任务执行超时");
						log.error("未能执行集群任务在规定 " + MAX_CLUSTER_EXECUTION_TIME + " 秒时间内 :{}", e);
					} catch (InterruptedException e) {
						result = new RemoteTaskResult(104, "任务执行被打断");
						log.error("任务执行被打断异常：{}", e);
					} catch (ExecutionException e) {
						result = new RemoteTaskResult(105, "任务执行中发生异常");
						log.error("任务执行异常：{}", e);
					}
			} else {
				result = new RemoteTaskResult(101, "没有找到服务节点，认为客户/客服已经离开");
				String msg = MessageFormat.format("请求节点 {0} 不能在集群中找到, 判定当前节点已经离开集群", new String(nodeID, StandardCharsets.UTF_8));
				log.warn(msg);
			}
		}
		return result;
	}

	/**
	 * 生成MAP
	 * @param name
	 * @param maxCacheSize
	 * @param hazelcastLifetimeInSeconds
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	private <K, V> Cache<K, V> createCache(String name, long maxCacheSize, int hazelcastLifetimeInSeconds) {
		int hazelcastMaxCacheSize = maxCacheSize < 0 ? Integer.MAX_VALUE : (int)maxCacheSize;
		MapConfig mapConfig = hazelcastInstance.getConfig().getMapConfig(HZ_MAP_SET_NAME);
		mapConfig.setTimeToLiveSeconds(hazelcastLifetimeInSeconds);
		mapConfig.setMaxSizeConfig(new MaxSizeConfig(hazelcastMaxCacheSize, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE));
		return new ClusteredCache<K, V>(name, hazelcastInstance.getMap(name), hazelcastLifetimeInSeconds);
	}
	
	private Member getMember(byte[] nodeID) {
		Member result = null;
		
		Set<Member> members = cluster.getMembers();
		for (Member member : members) {
			if (Arrays.equals(member.getUuid().getBytes(StandardCharsets.UTF_8), nodeID)) {
				result = member;
				break;
			}
		}
		 return result;
	}

	/**
	 * @param cachs
	 */
	@Override
	public void updateClusterStats(Map<String, Cache<?, ?>> cachs) {
		
	}

	/**
	 * @param nodeID
	 * @return
	 */
	@Override
	public ClusterNode getClusterNodeInfo(byte[] nodeID) {
		if (cluster == null) {
            return null;
        }
        ClusterNode result = null;
        Member member = getMember(nodeID);
        if (member != null) {
            result = new LocalClusterNode(member, cluster.getClusterTime());
        }
        return result;
	}

	/**
	 * @param key
	 * @param cache
	 * @return
	 */
	@SuppressWarnings({"rawtypes"})
	@Override
	public Lock getLock(Object key, Cache<?, ?> cache) {
		return new ClusterLock(key, (ClusteredCache)cache);
	}
	
	private static class CallableTask<V> implements Callable<V>, Serializable {
		private static final long serialVersionUID = 1L;
		
		private ClusterTask<V> task;

        CallableTask(ClusterTask<V> task) {
            this.task = task;
        }

        @Override
        public V call() {
            task.run();
            log.debug("CallableTask[" + task.getClass().getName() + "] result: " + task.getResult());
            return task.getResult();
        }
    }
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static class ClusterLock implements Lock {

        private Object key;
		private ClusteredCache cache;

        ClusterLock(Object key, ClusteredCache cache) {
            this.key = key;
            this.cache = cache;
        }

		@Override
        public void lock() {
            cache.lock(key, -1);
        }

        @Override
        public void lockInterruptibly() {
            cache.lock(key, -1);
        }

        @Override
        public boolean tryLock() {
            return cache.lock(key, 0);
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            return cache.lock(key, unit.toMillis(time));
        }

        @Override
        public void unlock() {
            cache.unlock(key);
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }
}
