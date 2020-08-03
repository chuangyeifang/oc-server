package com.oc.provider.redis;

import com.oc.common.utils.UUIDUtils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月24日
 * @version v 1.0
 */
@SuppressWarnings("unused")
public class IDProvider {

	private boolean enableRedis;

	private IDProvider(boolean enableRedis) {
		this.enableRedis = enableRedis;
	}

	public String getChatId() {
		if (enableRedis) {
			Long value = RedisProvider.getInst().getRedisTmp().opsForValue().increment(RedisSubKey.CHAT_ID, 1);
			if (null == value) {
				value = 100000L;
				RedisProvider.getInst().getRedisTmp().opsForValue().set(RedisSubKey.CHAT_ID, value.toString());
			}
			return value.toString();
		} else {
			return UUIDUtils.getID(10);
		}
	}
	
	public static IDProvider getInstance() {
		return Single.instance;
	}
	
	private static class Single {
		private static IDProvider instance = new IDProvider(false);
	}
}
