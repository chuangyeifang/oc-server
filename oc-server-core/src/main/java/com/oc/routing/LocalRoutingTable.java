package com.oc.routing;

import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月19日
 * @version v 1.0
 */
@SuppressWarnings("unused")
@Slf4j
class LocalRoutingTable<T> {
	
	private Map<String, T> localSessions = PlatformDependent.newConcurrentHashMap();
	
	void putSession(String key, T session) {
		localSessions.put(key, session);
	}
	
	T getSession(String uid) {
		if (null == uid) {
			return null;
		}
		return localSessions.get(uid);
	}
	
	boolean hasSession(String uid) {
		return localSessions.containsKey(uid);
	}
	
	void remove(String uid) {
		localSessions.remove(uid);
	}
	
	Collection<T> getRoutes() {
		return new ArrayList<>(localSessions.values());
	}
	
	Collection<T> getRoutes(String exceptKey) {
		List<T> sessions = new ArrayList<>();
		T session;
		for (String key : localSessions.keySet()) {
			if (!key.equals(exceptKey)) {
				session = localSessions.get(key);
				sessions.add(session);
			}
		}
		return sessions;
	}
	
	/**
	 * 获取除（exceptKeys）之外的本地全部 NettyChannelSession
	 * @param exceptKeys
	 * @return
	 */
	Collection<T> getRoutes(String... exceptKeys) {
		List<T> sessions = new ArrayList<>();
		T session;
		boolean exist;
		for (String key : localSessions.keySet()) {
			exist = false;
			for(String exceptKey : exceptKeys) {
				if (key.equals(exceptKey)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				session = localSessions.get(key);
				sessions.add(session);
			}
		}
		return sessions;
	}
	
	boolean isLocalSession(String uid) {
		return localSessions.containsKey(uid);
	}
}
