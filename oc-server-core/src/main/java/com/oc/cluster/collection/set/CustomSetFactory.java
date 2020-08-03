/**
 * 
 */
package com.oc.cluster.collection.set;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.collection.set.hazelcast.ClusteredSetFactoryStrategy;
import com.oc.cluster.collection.set.local.DefaultCustomSetFactoryStrategy;

import io.netty.util.internal.PlatformDependent;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月4日
 * @version v 1.0
 */
public class CustomSetFactory {
	
	private static Logger log = LoggerFactory.getLogger(CustomSetFactory.class);
	
	private static Map<String, CustomSet<?>> sets = PlatformDependent.newConcurrentHashMap();
	
	private static CustomSetFactoryStrategy ocQueueFactoryStrategy;
	
	private static CustomSetFactoryStrategy clusteredOCQueueFactoryStrategy;
	
	private static CustomSetFactoryStrategy localOCQueueFactoryStrategy;

	private CustomSetFactory() {}
	
	static {
		localOCQueueFactoryStrategy = new DefaultCustomSetFactoryStrategy();
		ocQueueFactoryStrategy = localOCQueueFactoryStrategy;
	}
	
	public static synchronized void startCluster(HazelcastInstance hi) {
		log.info("SetFactory 开启Hazelcast集群模式.");
		clusteredOCQueueFactoryStrategy = new ClusteredSetFactoryStrategy(hi);
		ocQueueFactoryStrategy = clusteredOCQueueFactoryStrategy;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends CustomSet<?>> T createQueue(String name) {
		T t = (T)sets.get(name);
		
		if (null == t) {
			t = (T)ocQueueFactoryStrategy.createOCSet(name);
			sets.put(name, t);
		}
		
		return t;
	}
}
