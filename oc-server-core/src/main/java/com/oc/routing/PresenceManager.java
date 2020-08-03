package com.oc.routing;

import com.oc.core.OcImServer;
import com.oc.message.type.Identity;
import com.oc.cluster.collection.cache.Cache;
import com.oc.cluster.collection.cache.CacheFactory;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月30日
 * @version v 1.0
 */
@SuppressWarnings("unused")
public class PresenceManager {
	
	private Cache<String, Identity> caches;
	
	private String cacheName;
	
	public PresenceManager() {
		cacheName = OcImServer.getInst().getNodeId().toString();
		caches = CacheFactory.createCache(cacheName);
	}
	
	public String getCacheName() {
		return cacheName;
	}
	
	public synchronized void add(String key, Identity value) {
		try {
			caches.lock(key, -1);
			caches.put(key, value);
		} finally {
			caches.unlock(key);
		}
	}
	
	public synchronized Identity remove(String key) {
		try {
			caches.lock(key, -1);
			Identity idy = caches.get(key);
			if (null != idy) {
				return caches.remove(key);
			} else {
				return null;
			}
		} finally {
			caches.unlock(key);
		}
	}
	
	public synchronized void removeAll(String key) {
		caches.destroy();
	}
}
