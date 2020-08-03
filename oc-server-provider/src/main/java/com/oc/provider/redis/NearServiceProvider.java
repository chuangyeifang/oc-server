package com.oc.provider.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月17日
 * @version v 1.0
 */
@Slf4j
public class NearServiceProvider {

	private boolean enableRedis;
	private Cache<String, String> nearCache;

	private NearServiceProvider(boolean enableRedis) {
		if (!enableRedis) {
			nearCache = CacheBuilder.newBuilder()
					.expireAfterAccess(3, TimeUnit.HOURS)
					.maximumSize(1000)
					.build();
		}
		this.enableRedis = enableRedis;
	}

	public void cacheNearWaiter(Integer teamCode, String customerCode, String waiterCode) {
		String key = createKey(teamCode.toString(), customerCode);
		try {
			if (!enableRedis) {
				nearCache.put(key, waiterCode);
			} else {
				RedisProvider.getInst().getRedisTmp().opsForValue().set(key, waiterCode);
			}
		} catch (Exception e) {
			log.error("更新客户近期服务客服信息失败", e);
		}
	}
	
	public String getNearWaiter(Integer teamCode, String customerCode) {
		try {
			String key = createKey(teamCode.toString(), customerCode);
			if (!enableRedis) {
				return nearCache.getIfPresent(key);
			} else {
				return RedisProvider.getInst().getRedisTmp().opsForValue().get(key);
			}
		} catch (Exception e) {
			log.error("获取近期服务客服信息失败", e);
			return null;
		}
	}

	private String createKey(String teamCode, String customerCode) {
		return RedisSubKey.REDIS_KEY_PREFIX + teamCode + RedisSubKey.SEPARATOR + customerCode;
	}
	
	public static NearServiceProvider getInst() {
		return Single.instance;
	}
	
	private static class Single {
		private static NearServiceProvider instance = new NearServiceProvider(false);
	}
}
