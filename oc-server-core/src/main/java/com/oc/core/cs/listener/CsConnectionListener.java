/**
 * 
 */
package com.oc.core.cs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oc.core.cs.config.CsConfiguration;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * @Description: Netty Server 监听启动状态
 * @author chuangyeifang
 * @createDate 2020年7月13日
 * @version v 1.0
 */
public class CsConnectionListener implements FutureListener<Void>{

	private Logger log = LoggerFactory.getLogger(CsConnectionListener.class);
	
	private CsConfiguration config;
	
	public CsConnectionListener(CsConfiguration config) {
		this.config = config;
	}
	
	@Override
	public void operationComplete(Future<Void> future) throws Exception {
		if (future.isSuccess()) {
			if (future.isSuccess()) {
				log.info("C/S 服务启动成功  监听端口: {}", config.getPort());
			} else {
				log.info("C/S 服务启动失败  监听端口: {}", config.getPort());
			}
		}
	}
}
