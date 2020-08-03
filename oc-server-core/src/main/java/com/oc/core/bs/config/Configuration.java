/**
 * 
 */
package com.oc.core.bs.config;

import com.oc.core.coder.JacksonJsonSupport;
import com.oc.core.coder.JsonSupport;
import com.oc.core.config.SocketConfig;
import com.oc.message.type.Transport;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
public class Configuration {

	private String context = "/socket.io";
	
	private List<Transport> transports = Arrays.asList(Transport.WEBSOCKET, Transport.POLLING);
	/** 0 等价于当前进程数 * 2 */
	private int bossThreads = 0;
	/** 0等价于当前进程数 * 2 */
	private int workerThreads = 0;
	private boolean useLinuxNativeEpoll = false;
	
	private boolean allowCustomRequests = true;
	
	private int upgradeTimeout = 10000;
	private int pingTimeout = 30000;
	private int pingInterval = 25000;
	private int firstDataTimeout = 5000;
	
	private int maxHttpContentLength = 64 * 1024;
	private int maxFramePayloadLength = 64 * 1024;
	
	private String hostname = "";
	private int port = 6066;
	
	private String sslProtocol = "TLSvl";
	
	private String keyStoreFormat = "JKS";
	private InputStream keyStore;
	private String keyStorePassword;
	
	private String trustStoreFormat = "JKS";
	private InputStream trustStore;
	private String trustStorePassword;
	
	private String keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
	
	private SocketConfig socketConfig = new SocketConfig();
	
	private JsonSupport jsonSupport = new JacksonJsonSupport();
	
	private boolean preferDirectBuffer = true;
	
	private boolean httpCompression = true;
	private boolean websocketCompression = true;
	
	private String origin;
	
	public Configuration() {}
	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}


	public int getBossThreads() {
		return bossThreads;
	}

	public void setBossThreads(int bossThreads) {
		this.bossThreads = bossThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public boolean isUseLinuxNativeEpoll() {
		return useLinuxNativeEpoll;
	}

	public void setUseLinuxNativeEpoll(boolean useLinuxNativeEpoll) {
		this.useLinuxNativeEpoll = useLinuxNativeEpoll;
	}

	public boolean isAllowCustomRequests() {
		return allowCustomRequests;
	}

	public void setAllowCustomRequests(boolean allowCustomRequests) {
		this.allowCustomRequests = allowCustomRequests;
	}

	public int getUpgradeTimeout() {
		return upgradeTimeout;
	}

	public void setUpgradeTimeout(int upgradeTimeout) {
		this.upgradeTimeout = upgradeTimeout;
	}

	public int getPingTimeout() {
		return pingTimeout;
	}

	public void setPingTimeout(int pingTimeout) {
		this.pingTimeout = pingTimeout;
	}

	public int getPingInterval() {
		return pingInterval;
	}

	public void setPingInterval(int pingInterval) {
		this.pingInterval = pingInterval;
	}

	public int getFirstDataTimeout() {
		return firstDataTimeout;
	}

	public void setFirstDataTimeout(int firstDataTimeout) {
		this.firstDataTimeout = firstDataTimeout;
	}

	public int getMaxHttpContentLength() {
		return maxHttpContentLength;
	}

	public void setMaxHttpContentLength(int maxHttpContentLength) {
		this.maxHttpContentLength = maxHttpContentLength;
	}

	public int getMaxFramePayloadLength() {
		return maxFramePayloadLength;
	}

	public void setMaxFramePayloadLength(int maxFramePayloadLength) {
		this.maxFramePayloadLength = maxFramePayloadLength;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSslProtocol() {
		return sslProtocol;
	}

	public void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

	public String getKeyStoreFormat() {
		return keyStoreFormat;
	}

	public void setKeyStoreFormat(String keyStoreFormat) {
		this.keyStoreFormat = keyStoreFormat;
	}

	public InputStream getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(InputStream keyStore) {
		this.keyStore = keyStore;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getTrustStoreFormat() {
		return trustStoreFormat;
	}

	public void setTrustStoreFormat(String trustStoreFormat) {
		this.trustStoreFormat = trustStoreFormat;
	}

	public InputStream getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(InputStream trustStore) {
		this.trustStore = trustStore;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getKeyManagerFactoryAlgorithm() {
		return keyManagerFactoryAlgorithm;
	}

	public void setKeyManagerFactoryAlgorithm(String keyManagerFactoryAlgorithm) {
		this.keyManagerFactoryAlgorithm = keyManagerFactoryAlgorithm;
	}

	public SocketConfig getSocketConfig() {
		return socketConfig;
	}

	public void setSocketConfig(SocketConfig socketConfig) {
		this.socketConfig = socketConfig;
	}

	public JsonSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JsonSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}
	
	public boolean isPreferDirectBuffer() {
		return preferDirectBuffer;
	}

	public void setPreferDirectBuffer(boolean preferDirectBuffer) {
		this.preferDirectBuffer = preferDirectBuffer;
	}

	public boolean isHttpCompression() {
		return httpCompression;
	}

	public void setHttpCompression(boolean httpCompression) {
		this.httpCompression = httpCompression;
	}

	public boolean isWebsocketCompression() {
		return websocketCompression;
	}

	public void setWebsocketCompression(boolean websocketCompression) {
		this.websocketCompression = websocketCompression;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
