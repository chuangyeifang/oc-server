/**
 * 
 */
package com.oc.core.cs.config;

import com.oc.core.coder.JacksonJsonSupport;
import com.oc.core.coder.JsonSupport;
import com.oc.core.config.SocketConfig;
import com.oc.core.cs.listener.DefaultExceptionListener;
import com.oc.core.listener.ExceptionListener;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月25日
 * @version v 1.0
 */
public class CsConfiguration {
	
	private boolean useLinuxNativeEpoll = false;
	
	private int port = 5055;
	
	private SocketConfig socketConfig = new SocketConfig();
	/** 0 = 当前cup核数 * 2 */
	private int bosserThreads = 0;
	/** 0 = 当前cup核数 * 2 */
	private int workerThreads = 0;
	
	private ExceptionListener exceptionListener;
	private JsonSupport jsonSupport;
	private String hostName;
	
	public CsConfiguration(String hostName) {
		exceptionListener = new DefaultExceptionListener();
		jsonSupport = new JacksonJsonSupport();
		this.hostName = hostName;
	}

	public boolean isUseLinuxNativeEpoll() {
		return useLinuxNativeEpoll;
	}

	public void setUseLinuxNativeEpoll(boolean useLinuxNativeEpoll) {
		this.useLinuxNativeEpoll = useLinuxNativeEpoll;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public SocketConfig getSocketConfig() {
		return socketConfig;
	}

	public void setSocketConfig(SocketConfig socketConfig) {
		this.socketConfig = socketConfig;
	}

	public int getBosserThreads() {
		return bosserThreads;
	}

	public void setBosserThreads(int bosserThreads) {
		this.bosserThreads = bosserThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public ExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public void setExceptionListener(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

	public JsonSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JsonSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}
	
	public String getHostName() {
		return hostName;
	}
}
