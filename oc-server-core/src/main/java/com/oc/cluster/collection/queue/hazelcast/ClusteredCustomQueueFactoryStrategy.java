/**
 * 
 */
package com.oc.cluster.collection.queue.hazelcast;

import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.collection.queue.CustomQueue;
import com.oc.cluster.collection.queue.CustomQueueFactoryStrategy;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月31日
 * @version v 1.0
 */
public class ClusteredCustomQueueFactoryStrategy implements CustomQueueFactoryStrategy{

	private final static String HZ_QUEUE_CONFIG_NAME = "default";

	private HazelcastInstance hazelcastInstance;
	
	public ClusteredCustomQueueFactoryStrategy(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}
	
	/**
	 * @param name
	 * @return
	 */
	@SuppressWarnings({"deprecation"})
	@Override
	public <E> CustomQueue<E> createOCQueue(String name, int emptyQueueTtl) {
		QueueConfig queueConfig = hazelcastInstance.getConfig().getQueueConfig(HZ_QUEUE_CONFIG_NAME);
		queueConfig.setEmptyQueueTtl(emptyQueueTtl);
		return new ClusteredCustomQueue<>(name, hazelcastInstance.getQueue(name), hazelcastInstance.getLock(name));
	}

}
