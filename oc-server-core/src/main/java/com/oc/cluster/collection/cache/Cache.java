/**
 * 
 */
package com.oc.cluster.collection.cache;

import java.util.Map;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月18日
 * @version v 1.0	
 */
public interface Cache<K, V> extends Map<K, V>{

	/**
	 * 返回缓存的名称
	 * @return 缓存的名称
	 */
	String getName();
	
	/**
	 * 设置缓存名称
	 * @param name 缓存名称
	 */
	void setName(String name);
	
	/**
	 * 返回当前缓存字节数。如果返回结果超过设置的最大值，则不常用的将被移除；如果设置为-1，则缓存大小没有界限
	 * @return
	 */
	long getMaxCacheSize();
	
	/**
	 * 设置缓存大小
	 * @param size
	 */
	void setMaxCacheSize(int size);
	
	/**
	 * 获取对象在缓存中生存的最大毫秒数。如果为-1，则永久有效
	 * @return 生存最大毫秒数
	 */
	long getMaxLifetime();
	
	/**
	 * 设置缓存中对象最大存活毫秒数。如果设置为-1，则永久有效
	 * @param maxLifetime 最大毫秒数
	 */
	void setMaxLifetime(long maxLifetime);
	
	/**
	 * 获取当前缓存内容字节大小。该大小为粗略估计大小可能高于实际VM内存。
	 * @return 内容字节数
	 */
	long getCacheSize();
	
	void destroy();
	
	boolean lock(K key, long timeout);
	
	boolean unlock(K key);

}
