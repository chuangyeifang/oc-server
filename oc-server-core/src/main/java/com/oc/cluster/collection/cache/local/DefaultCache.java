/**
 * 
 */
package com.oc.cluster.collection.cache.local;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.oc.cluster.collection.linked.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oc.exception.CannotCalculateSizeException;
import com.oc.cluster.collection.cache.Cache;
import com.oc.cluster.collection.linked.LinkedListNode;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月23日
 * @version v 1.0
 */
public class DefaultCache<K, V> implements Cache<K, V>{
	
	private final static Logger log = LoggerFactory.getLogger(DefaultCache.class);
	
	private ReentrantLock lock;
	
	private static final String NULL_KEY_IS_NOT_ALLOWED = "主键不能为空!";
	private static final String NULL_VALUE_IS_NOT_ALLOWED = "值不能为空";

	private String name;
	
	private long maxCacheSize;
	
	private int cacheSize;
	
	protected long maxLifttime;
	
	private LinkedList<K> ageList;
	
	protected Map<K, DefaultCache.CacheObject<V>> map;
	
	public DefaultCache(String name, long maxCacheSize, long maxLifttime) {
		this.name = name;
		this.maxCacheSize = maxCacheSize;
		this.maxLifttime = maxLifttime;
		
		map = new HashMap<>(103);
		ageList = new LinkedList<>();
		lock = new ReentrantLock();
	}
	
	/**
	 * @return
	 */
	@Override
	public int size() {
		deleteExpiredEntries();
		return map.size();
	}

	/**
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		deleteExpiredEntries();
		return map.isEmpty();
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		deleteExpiredEntries();
		return map.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 */
	@Override
	public boolean containsValue(Object value) {
		deleteExpiredEntries();
		return map.containsValue(value);
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public V get(Object key) {
		checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
		
		deleteExpiredEntries();
		
		DefaultCache.CacheObject<V> cacheObject = map.get(key);
		
		if (null == cacheObject) {
			return null;
		}
		
		return cacheObject.object;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public synchronized V put(K key, V value) {
		checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
		checkNotNull(value, NULL_VALUE_IS_NOT_ALLOWED);
		
		V answer = remove(key);
		
		int objectSize = 1;
		
		try {
			objectSize = CacheSizes.sizeOfAnything(value);
		} catch (CannotCalculateSizeException e) {
			log.warn(e.getMessage(), e);
		}
		
		//如果增加对象超过整个缓存大小90% 则不要添加他
		if (maxCacheSize > 0 && objectSize > maxCacheSize * .90) {
			log.warn("缓存名称为: " + name + " -- 键值为： " + key +
                    " 的值太大不适合当前缓存. 大小为： " + objectSize);
			
			return value;
		}
		cacheSize += objectSize;
		DefaultCache.CacheObject<V> cacheObject = new DefaultCache.CacheObject<>(value, objectSize);
		map.put(key, cacheObject);
		
		LinkedListNode<K> ageNode = ageList.addFirst(key);
		ageNode.timestamp = System.currentTimeMillis();
		cacheObject.ageListNode = ageNode;
		
		return answer;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public V remove(Object key) {
		checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
		CacheObject<V> cacheObject = map.get(key);
		if (null == cacheObject) {
			return null;
		}
		
		map.remove(key);
		
		cacheObject.ageListNode.remove();
		cacheObject.ageListNode = null;
		
		cacheSize -= cacheObject.size;
		
		return cacheObject.object;
	}

	/**
	 * @param m
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Iterator<? extends K> it = m.keySet().iterator(); 
		while(it.hasNext()) {
			K key = it.next();
			V value = m.get(key);
			put(key, value);
		}
	}

	/**
	 * 
	 */
	@Override
	public void clear() {
		Object[] keys = map.keySet().toArray();
		for (Object object : keys) {
			remove(object);
		}
		
		map.clear();
		cacheSize = 0;
	}

	/**
	 * @return
	 */
	@Override
	public Set<K> keySet() {
		deleteExpiredEntries();
		return map.keySet();
	}

	/**
	 * @return
	 */
	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		deleteExpiredEntries();
		synchronized (this) {
			final Map<K, V> result = new HashMap<>();
			for (final Entry<K, DefaultCache.CacheObject<V>> entry : map.entrySet()) {
				result.put(entry.getKey(), entry.getValue().object);
			}
			return result.entrySet();
		}
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
		return maxCacheSize;
	}

	/**
	 * @param size
	 */
	@Override
	public void setMaxCacheSize(int size) {
		this.maxCacheSize = size;
	}

	/**
	 * @return
	 */
	@Override
	public long getMaxLifetime() {
		return maxLifttime;
	}

	/**
	 * @param maxLifetime
	 */
	@Override
	public void setMaxLifetime(long maxLifetime) {
		this.maxLifttime = maxLifetime;
	}

	/**
	 * @return
	 */
	@Override
	public long getCacheSize() {
		return cacheSize;
	}
	
	protected void deleteExpiredEntries() {
		if (maxLifttime < 0) {
			return;
		}
		
		LinkedListNode<K> node = ageList.getLast();
		if (null == node) {
			return;
		}
		
		long expireTime = System.currentTimeMillis() - maxLifttime;
		while(expireTime > node.timestamp) {
			remove(node.object);
			node = ageList.getLast();
			if (null == node) {
				return;
			}
		}
	}

	private static class CacheObject<V> {
		
		public V object;
		
		public int size;
		
		public LinkedListNode<?> ageListNode;
		
		public CacheObject(V object, int size) {
			this.object = object;
			this.size = size;
		}
	}
	
	private void checkNotNull(final Object argument, final String message) {
		try {
			if (null == argument) {
				throw new NullPointerException(message);
			}
		} catch (NullPointerException e) {
			throw e;
		}
	}

	@SuppressWarnings("AlibabaLockShouldWithTryFinally")
	@Override
	public boolean lock(K key, long timeout) {
		lock.lock();
		return true;
	}

	@Override
	public boolean unlock(K key) {
		lock.unlock();
		return true;
	}

	@Override
	public void destroy() {
		map = null;
	}
	
}
