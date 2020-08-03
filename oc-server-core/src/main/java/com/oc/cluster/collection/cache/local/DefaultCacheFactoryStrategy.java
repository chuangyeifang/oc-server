/**
 * 
 */
package com.oc.cluster.collection.cache.local;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.oc.cluster.node.ClusterNode;
import com.oc.cluster.collection.cache.Cache;
import com.oc.cluster.collection.cache.CacheFactory;
import com.oc.cluster.collection.cache.CacheFactoryStrategy;
import com.oc.cluster.collection.RemoteTaskResult;
import com.oc.cluster.task.ClusterTask;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月23日
 * @version v 1.0
 */
public class DefaultCacheFactoryStrategy implements CacheFactoryStrategy{

	/**
	 * 跟踪锁
	 */
	private Map<Object, LockAndCount> locks = new ConcurrentHashMap<>();
	
	/**
	 * @param name
	 * @return
	 */
	@Override
	public <K, V> Cache<K, V> createCache(String name) {
		long maxCacheSize = CacheFactory.getMaxCacheSize(name);
		long maxLifttime = CacheFactory.getMaxLifeTime(name);
		return new DefaultCache<>(name, maxCacheSize, maxLifttime);
	}
	
	/**
	 * @param name
	 * @param maxLifttime
	 * @param maxCacheSize
	 * @return
	 */
	@Override
	public <K, V> Cache<K, V> createCache(String name, long maxLifttime, long maxCacheSize) {
		return new DefaultCache<>(name, maxCacheSize, maxLifttime);
	}

	/**
	 * @param cache
	 */
	@Override
	public void destroyCache(Cache<?, ?> cache) {
		cache.clear();
	}

	/**
	 * 非集群模式- 失效
	 * @return
	 */
	@Override
	public boolean isMasterClusterMember() {
		throw new IllegalStateException("集群服务不可用.");
	}

	/**
	 * 非集群模式- 失效
	 * @return
	 */
	@Override
	public Collection<ClusterNode> getClusterNodeInfo() {
		throw new IllegalStateException("集群服务不可用.");
	}

	/**
	 * 非集群模式- 失效
	 * @return
	 */
	@Override
	public byte[] getClusterMemberID() {
		throw new IllegalStateException("集群服务不可用.");
	}

	/**
	 * 非集群模式- 失效
	 * @return
	 */
	@Override
	public long getClusterTime() {
		return 0;
	}

	/**
	 * 非集群模式- 失效
	 * @param task
	 */
	@Override
	public boolean doClusterTask(ClusterTask<?> task) {
		throw new IllegalStateException("集群服务不可用.");
	}

	/**
	 * 非集群模式- 失效
	 * @param task
	 * @param nodeID
	 */
	@Override
	public boolean doClusterTask(ClusterTask<?> task, byte[] nodeID) {
		throw new IllegalStateException("集群服务不可用.");
	}

	/**
	 * @param task
	 * @param nodeID
	 */
	@Override
	public RemoteTaskResult doSynchronousClusterTask(ClusterTask<?> task, byte[] nodeID) {
		throw new IllegalStateException("集群服务不可用.");
	}

	@Override
	public void updateClusterStats(Map<String, Cache<?, ?>> cachs) {
		throw new IllegalStateException("集群服务不可用.");
	}

	@Override
	public ClusterNode getClusterNodeInfo(byte[] nodeID) {
		throw new IllegalStateException("集群服务不可用.");
	}
	
	private void acquireLock(Object key) {
        ReentrantLock lock = lookupLockForAcquire(key);
        lock.lock();
    }

    private void releaseLock(Object key) {
        ReentrantLock lock = lookupLockForRelease(key);
        lock.unlock();
    }
	
	private ReentrantLock lookupLockForAcquire(Object key) {
        synchronized(key) {
            LockAndCount lac = locks.get(key);
            if (lac == null) {
                lac = new LockAndCount(new ReentrantLock());
                lac.count = 1;
                locks.put(key, lac);
            } else {
                lac.count++;
            }

            return lac.lock;
        }
    }

    private ReentrantLock lookupLockForRelease(Object key) {
        synchronized(key) {
            LockAndCount lac = locks.get(key);
            if (lac == null) {
                throw new IllegalStateException("没有为对象找到锁: " + key);
            }

            if (lac.count <= 1) {
                locks.remove(key);
            }
            else {
                lac.count--;
            }

            return lac.lock;
        }
    }


    private class LocalLock implements Lock {
        private final Object key;

        LocalLock(Object key) {
            this.key = key;
        }

        @Override
        public void lock(){
            acquireLock(key);
        }

        @Override
        public void	unlock() {
            releaseLock(key);
        }

        @Override
        public void	lockInterruptibly(){
            throw new UnsupportedOperationException();
        }

        @Override
        public Condition newCondition(){
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean 	tryLock() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean 	tryLock(long time, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }

    private static class LockAndCount {
        final ReentrantLock lock;
        int count;

        LockAndCount(ReentrantLock lock) {
            this.lock = lock;
        }
    }

	/**
	 * @param key
	 * @param cache
	 * @return
	 */
	@Override
	public Lock getLock(Object key, Cache<?, ?> cache) {
        return new LocalLock(key);
	}
}
