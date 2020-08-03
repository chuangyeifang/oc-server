/**
 * 
 */
package com.oc.cluster.collection.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.oc.cluster.collection.RemoteTaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.collection.cache.local.DefaultCacheFactoryStrategy;
import com.oc.cluster.task.ClusterTask;

import io.netty.util.internal.PlatformDependent;

/**
 * 创建缓存对象。 根据不同缓存策略，返回的缓存要么是本地，要么是远程
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月18日
 * @version v 1.0
 */
public class CacheFactory {
	
	private final static Logger log = LoggerFactory.getLogger(CacheFactory.class);
	
	public static final int DEFAULT_MAX_CACHE_SIZE = 1024 * 256;
	public static final int DEFAULT_MAX_CACHE_LIFETIME = -1;
	
	/**
	 * 存储所有创建的缓存
	 */
	private static Map<String, Cache<?, ?>> caches = PlatformDependent.newConcurrentHashMap();
	
	private static CacheFactoryStrategy cacheFactoryStrategy;
	private static CacheFactoryStrategy localCacheFactoryStrategy;
	private static CacheFactoryStrategy clusterCacheFactoryStrategy;
	
	private static List<String> localOnly = Collections.synchronizedList(new ArrayList<String>());
	
	/**
	 * 缓存名称，并与属性文件进行映射配置缓存
	 */
	private static final Map<String, String> cacheNames = PlatformDependent.newConcurrentHashMap();
	/**
	 * 默认缓存配置属性
	 */
	private static final Map<String, Long> cacheProps = PlatformDependent.newConcurrentHashMap();
	
	static {
		localCacheFactoryStrategy = new DefaultCacheFactoryStrategy();
		//默认采用本地缓存策略
		cacheFactoryStrategy = localCacheFactoryStrategy;
	}
	
	public static synchronized void startCluster(HazelcastInstance hi) {
		log.info("CacheFactory 开启Hazelcast集群模式.");
		clusterCacheFactoryStrategy = new ClusteredCacheFactoryStrategy(hi);
		cacheFactoryStrategy = clusterCacheFactoryStrategy;
	}
	
	public static long getMaxCacheSize(String cacheName) {
		return CacheFactory.getCacheProperty(cacheName, ".size", DEFAULT_MAX_CACHE_SIZE);
	}
	
	public static long getMaxLifeTime(String cacheName) {
		return CacheFactory.getCacheProperty(cacheName, ".getMaxLifeTime", DEFAULT_MAX_CACHE_LIFETIME);
	}
	
	private static long getCacheProperty(String cacheName, String suffix, long defaultValue) {
		String propName = "cache." + cacheNames.get(cacheName) + suffix;
		Long defaultSize = cacheProps.get(propName);
		return defaultSize == null ? defaultValue : defaultSize;
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Cache<?, ?>> T createCache(String name) {
		T cache = (T)caches.get(name);
		if (null != cache) {
			return cache;
		}
		cache = (T)cacheFactoryStrategy.createCache(name);
		caches.put(name, cache);
		return cache;
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Cache<?, ?>> T createCache(String name, long maxLifttime, long maxCacheSize) {
		T cache = (T)caches.get(name);
		if (null != cache) {
			return cache;
		}
		cache = (T)cacheFactoryStrategy.createCache(name, maxLifttime, maxCacheSize);
		caches.put(name, cache);
		return cache;
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Cache<?, ?>> T createLocalCache(String name) {
		T cache = (T)caches.get(name);
		if (null != cache) {
			return cache;
		}
		
		cache = (T) localCacheFactoryStrategy.createCache(name);
		caches.put(name, cache);
		localOnly.add(name);
		return cache;
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Cache<?, ?>> T createLocalCache(String name, long maxLifttime, long maxCacheSize) {
		T cache = (T)caches.get(name);
		if (null != cache) {
			return cache;
		}
		
		cache = (T)localCacheFactoryStrategy.createCache(name, maxLifttime, maxCacheSize);
		caches.put(name, cache);
		localOnly.add(name);
		return cache;
	}
	
	public static boolean doClusterTask(ClusterTask<?> task, byte[] nodeID) {
		return cacheFactoryStrategy.doClusterTask(task, nodeID);
	}
	
	public static RemoteTaskResult doSynchronousClusterTask(ClusterTask<?> task, byte[] nodeID) {
		return cacheFactoryStrategy.doSynchronousClusterTask(task, nodeID);
	}
	
	public static synchronized Lock getLock(Object key, Cache<?, ?> cache) {
		if (localOnly.contains(cache.getName())) {
			return localCacheFactoryStrategy.getLock(key, cache);
		} else {
			return cacheFactoryStrategy.getLock(key, cache);
		}
	}
	
	public static boolean isMasterMember() {
		return cacheFactoryStrategy.isMasterClusterMember();
	}
}
