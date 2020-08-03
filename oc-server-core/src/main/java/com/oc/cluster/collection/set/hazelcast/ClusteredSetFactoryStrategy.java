/**
 * 
 */
package com.oc.cluster.collection.set.hazelcast;

import com.hazelcast.config.SetConfig;
import com.hazelcast.core.HazelcastInstance;
import com.oc.cluster.collection.set.CustomSet;
import com.oc.cluster.collection.set.CustomSetFactoryStrategy;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月4日
 * @version v 1.0
 */
public class ClusteredSetFactoryStrategy implements CustomSetFactoryStrategy{

	private final static String HZ_SET_CONFIG_NAME = "default";

	private HazelcastInstance hazelcastInstance;
	
	public ClusteredSetFactoryStrategy(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}
	
	@Override
	public <E> CustomSet<E> createOCSet(String name) {
		SetConfig setConfig = hazelcastInstance.getConfig().getSetConfig(HZ_SET_CONFIG_NAME);
		setConfig.setStatisticsEnabled(false);
		return new ClusteredSet<>(name, hazelcastInstance.getSet(name));
	}
}
