/**
 * 
 */
package com.oc.cluster.collection.queue;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.collection.queue.hazelcast.ClusteredCustomQueueFactoryStrategy;
import com.oc.cluster.collection.queue.local.DefaultCustomQueueFactoryStrategy;

import io.netty.util.internal.PlatformDependent;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月31日
 * @version v 1.0
 */
public class CustomQueueFactory {
	
	private static Logger log = LoggerFactory.getLogger(CustomQueueFactory.class);
	
	private static Map<String, CustomQueue<?>> queues = PlatformDependent.newConcurrentHashMap();
	
	private static CustomQueueFactoryStrategy queueFactoryStrategy;
	private static CustomQueueFactoryStrategy clusteredQueueFactoryStrategy;
	private static CustomQueueFactoryStrategy localQueueFactoryStrategy;

	private CustomQueueFactory() {}
	
	static {
		localQueueFactoryStrategy = new DefaultCustomQueueFactoryStrategy();
		queueFactoryStrategy = localQueueFactoryStrategy;
	}
	
	public static synchronized void startCluster(HazelcastInstance hi) {
		log.info("QueueFactory 开启Hazelcast集群模式.");
		clusteredQueueFactoryStrategy = new ClusteredCustomQueueFactoryStrategy(hi);
		queueFactoryStrategy = clusteredQueueFactoryStrategy;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends CustomQueue<?>> T createQueue(String name, int emptyQueueTtl) {
		T t = (T)queues.get(name);
		if (null == t) {
			t = (T)queueFactoryStrategy.createOCQueue(name, emptyQueueTtl);
			queues.put(name, t);
		}
		return t;
	}
}
