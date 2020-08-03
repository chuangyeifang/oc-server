/**
 * 
 */
package com.oc.core.server;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月11日
 * @version v 1.0
 */
public interface Server {
	
	public void start();
	
	public void stop();
	
	public int getPort();
	
	public String getHostName();
}
