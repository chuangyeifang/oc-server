package com.oc.core.config;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月25日
 * @version v 1.0
 */
@SuppressWarnings("unused")
public class SocketConfig {
	
	private boolean tcpNoDelay = true;
	
	private int tcpReceiveBufferSize = -1;
	
	private int tcpSendBufferSize = -1;
	
	private boolean tcpKeepAlive = false;
	
	private boolean reuseAddress = false;
	
	private int soLinger = -1;
	
	private int acceptBackLog = 1024;

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public int getTcpReceiveBufferSize() {
		return tcpReceiveBufferSize;
	}

	public void setTcpReceiveBufferSize(int tcpReceiveBufferSize) {
		this.tcpReceiveBufferSize = tcpReceiveBufferSize;
	}

	public int getTcpSendBufferSize() {
		return tcpSendBufferSize;
	}

	public void setTcpSendBufferSize(int tcpSendBufferSize) {
		this.tcpSendBufferSize = tcpSendBufferSize;
	}

	public boolean isTcpKeepAlive() {
		return tcpKeepAlive;
	}

	public void setTcpKeepAlive(boolean tcpKeepAlive) {
		this.tcpKeepAlive = tcpKeepAlive;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public int getSoLinger() {
		return soLinger;
	}

	public void setSoLinger(int soLinger) {
		this.soLinger = soLinger;
	}

	public int getAcceptBackLog() {
		return acceptBackLog;
	}

	public void setAcceptBackLog(int acceptBackLog) {
		this.acceptBackLog = acceptBackLog;
	}
}
