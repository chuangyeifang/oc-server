package com.oc.provider.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.oc.domain.properties.Properties;
import com.oc.provider.context.SpringContext;
import com.oc.service.properties.PropertiesService;
import com.oc.service.properties.impl.PropertiesServiceImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月4日
 * @version v 1.0
 */
public class LocalPropertiesStore {
	private LoadingCache<String, Properties> properties;
	
	private static PropertiesService propertiesService = SpringContext.getBean(PropertiesServiceImpl.class);

	private LocalPropertiesStore() {
		initCache();
	}
	
	private void initCache() {
		// 缓存过期时间（秒）
		long expireOfSeconds = 60 * 5;
		properties = CacheBuilder.newBuilder()
				.expireAfterWrite(expireOfSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<String, Properties>(){

					@Override
					@ParametersAreNonnullByDefault
					public Properties load(String tenantCode) {
						return propertiesService.obtainProperties(tenantCode);
					}
				});
	}
	
	public Properties getProperties(String tenantCode) {
		try {
			return properties.get(tenantCode);
		} catch (ExecutionException e) {
			return null;
		}
	}
	
	
	public static LocalPropertiesStore getInst() {
		return Single.store;
	}
	
	private static class Single {
		private static LocalPropertiesStore store = new LocalPropertiesStore();
	}
}
