package com.oc.routing;

import com.oc.core.OcImServer;
import com.oc.cluster.collection.cache.Cache;
import com.oc.cluster.collection.cache.CacheFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月23日
 * @version v 1.0
 */
@SuppressWarnings("unused")
public class ClientRouteManager<T> {
	
	private Cache<String, T> routeCaches;
	
	public ClientRouteManager(String cacheName) {
		routeCaches = CacheFactory.createCache(cacheName);
	}
	
	public void put(String key, T value) {
		routeCaches.put(key, value);
	}
	
	public T get(String key) {
		return routeCaches.get(key);
	}
	
	public T remove(String key) {
		return routeCaches.remove(key);
	}
	
	public boolean containsKey(String key) {
		return routeCaches.containsKey(key);
	}

	public List<T> getRoutes() {
		return new ArrayList<>(routeCaches.values());
	}
	
	public boolean isLocal(T route) {
		if (null != route) {
			if (route instanceof WaiterRoute) {
				return OcImServer.getInst().getNodeId().equals(((WaiterRoute)route).getNodeID());
			} else if (route instanceof CustomerRoute) {
				return OcImServer.getInst().getNodeId().equals(((CustomerRoute)route).getNodeID());
			}
		}
		return false;
	}
	
	public void lock(String key, long timeout) {
		routeCaches.lock(key, timeout);
	}
	
	public void unlock(String key) {
		routeCaches.unlock(key);
	}
}
