package com.oc.cluster.collection.cache.hazelcast;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.oc.cluster.collection.cache.Cache;
import com.oc.cluster.collection.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月27日
 * @version v 1.0
 */
public class ClusteredCache<K, V> implements Cache<K, V> {
	
	private static Logger log = LoggerFactory.getLogger(ClusteredCache.class);

	protected IMap<K, V> map;
	
	private String name;
	
	private final int hazelcastLifetimeInSeconds;
	
	public ClusteredCache(String name, IMap<K, V> cache, final int hazelcastLifetimeInSeconds) {
		this.map = cache;
		this.hazelcastLifetimeInSeconds = hazelcastLifetimeInSeconds;
		setName(name);
	}
	
	/**
	 * @return
	 */
	@Override
	public int size() {
		LocalMapStats stats = map.getLocalMapStats();
        return (int) (stats.getOwnedEntryCount() + stats.getBackupEntryCount());
	}

	/**
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 */
	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public V get(Object key) {
		return map.get(key);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public V put(K key, V value) {
		if (value == null) {return null;}
		return map.put(key, value, this.hazelcastLifetimeInSeconds, TimeUnit.SECONDS);
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	/**
	 * @param m
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	/**
	 * 
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * @return
	 */
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	/**
	 * @return
	 */
	@Override
	public Collection<V> values() {
		return map.values();
	}

	/**
	 * @return
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	/**
	 * @return
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	@Override
	public long getMaxCacheSize() {
		return CacheFactory.getMaxCacheSize(getName());
	}

	/**
	 * @param size
	 */
	@Override
	public void setMaxCacheSize(int size) {
	}

	/**
	 * @return
	 */
	@Override
	public long getMaxLifetime() {
		return CacheFactory.getMaxLifeTime(getName());
	}

	/**
	 * @param maxLifetime
	 */
	@Override
	public void setMaxLifetime(long maxLifetime) {
	}

	/**
	 * @return
	 */
	@Override
	public long getCacheSize() {
		LocalMapStats stats = map.getLocalMapStats();
        return (int) (stats.getOwnedEntryMemoryCost() + stats.getBackupEntryMemoryCost());
	}
	
	@Override
	public void destroy() {
		map.destroy();
	}
	
	@Override
	public boolean lock(K key, long timeout) {
		boolean result = true;
		if (timeout  < 0) {
			map.lock(key);
		}
		else if (timeout == 0) {
			result = map.tryLock(key);
		} else {
			try {
				result = map.tryLock(key, timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				log.error("获取集群锁失败");
				result = false;
			}
		}
		return result;
	}
	
	@Override
	public boolean unlock(K key) {
		boolean result = true;
		try {
			map.unlock(key);
		} catch (Exception e) {
			log.error("释放锁失败.");
			result = false;
		}
		return result;
	}
}
