/**
 * 
 */
package com.oc.cluster.collection.queue.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.oc.cluster.collection.queue.CustomQueue;
import com.oc.cluster.collection.queue.CustomQueueFactoryStrategy;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月31日
 * @version v 1.0
 */
public class DefaultCustomQueueFactoryStrategy implements CustomQueueFactoryStrategy{

	private static Map<String, CustomQueue<?>> queues = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> CustomQueue<E> createOCQueue(String name, int emptyQueueTtl) {
		CustomQueue<?> queue = queues.get(name);
		
		if (null == queue) {
			queue = new DefaultCustomQueue<>(name);
			queues.put(name, queue);
		}
		
		return (CustomQueue<E>)queue;
	}
}
