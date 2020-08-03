/**
 * 
 */
package com.oc.cluster.collection.set.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.oc.cluster.collection.set.CustomSet;
import com.oc.cluster.collection.set.CustomSetFactoryStrategy;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月4日
 * @version v 1.0
 */
public class DefaultCustomSetFactoryStrategy implements CustomSetFactoryStrategy{

	private Map<String, CustomSet<?>> sets = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> CustomSet<E> createOCSet(String name) {
		CustomSet<?> set = sets.get(name);
		if (null == set) {
			set = new DefaultCustomSet<E>(name);
			sets.put(name, set);
		}
		return (CustomSet<E>)set;
	}
}
