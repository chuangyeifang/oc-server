package com.oc.dispatcher;

import com.oc.cluster.collection.cache.Cache;
import com.oc.cluster.collection.cache.CacheFactory;
import com.oc.cluster.collection.queue.CustomQueue;
import com.oc.cluster.collection.queue.CustomQueueFactory;

/**
 * @Description: 管理缓存
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@SuppressWarnings({"unchecked", "unused"})
public class NameFactory {
	private final static String SEPARATOR = ":";
	
	private String fitName(String prefix, String... ids) {
		StringBuilder prefixBuilder = new StringBuilder(prefix);
		for (String id : ids) {
			prefixBuilder.append(SEPARATOR).append(id);
		}
		prefix = prefixBuilder.toString();
		return prefix;
	}
	
	public <T extends Cache<?, ?>> T getCache(String prefix, String...ids) {
		String cacheName = fitName(prefix, ids);
		Cache<?, ?> createCache = CacheFactory.createCache(cacheName, -1, -1);
		return (T)createCache;
	}
	
	public <T extends Cache<?, ?>> T getCache(String name) {
		Cache<?, ?> createCache = CacheFactory.createCache(name, -1, -1);
		return (T)createCache;
	}
	
	public <T extends CustomQueue<?>> T getQueue(String prefix, String...ids) {
		String queueName = fitName(prefix, ids);
		CustomQueue<?> createQueue = CustomQueueFactory.createQueue(queueName, -1);
		return (T)createQueue;
	}
	
	public <T extends CustomQueue<?>> T getQueue(String name) {
		CustomQueue<?> createQueue = CustomQueueFactory.createQueue(name, -1);
		return (T)createQueue;
	}
}
