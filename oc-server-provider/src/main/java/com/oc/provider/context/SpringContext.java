package com.oc.provider.context;

import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月17日
 * @version v 1.0
 */
final public class SpringContext {
	
	private static ApplicationContext context = null;
	
	@Resource
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContext.context = applicationContext;
	}
	
	public static <T> T getBean(Class<T> type) {
		if (null == context) {
			return null;
		}
		return context.getBean(type);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		if (null == context) {
			return null;
		}
		return (T)context.getBean(name);
	}
}
