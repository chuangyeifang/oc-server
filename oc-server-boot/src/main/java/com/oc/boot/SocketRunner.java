/**
 * 
 */
package com.oc.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.oc.core.OcImServer;

/**
 * @Description: Spring boot 启动成功后，启动通讯服务器
 * @author chuangyeifang
 * @createDate 2020年7月19日
 * @version v 1.0
 */
@Component
public class SocketRunner implements ApplicationRunner {

	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	/**
	 * @param args
	 * @throws Exception
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		OcImServer ocimServer = new OcImServer(hazelcastInstance);
		ocimServer.startCluster(true);
	}
}
