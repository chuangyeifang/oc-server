/**
 * 
 */
package com.oc.core.contants;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月29日
 * @version v 1.0
 */
public class Constants {
	
	public static String ASSIGNING_MESSAGE;
	public static String INTERRUPTION_MESSAGE;
	public static String DISCONNECT_MESSAGE;
	public static String REPEAT_MESSAGE;
	public static String POLL_TIMEOUT_MESSAGE;
	public static String WS_TIMEOUT_MESSAGE;
	
	static {
		ASSIGNING_MESSAGE = "正在分配客服";
		INTERRUPTION_MESSAGE = "会话被中断";
		DISCONNECT_MESSAGE = "客户结束会话";
		REPEAT_MESSAGE = "会话重复开启";
		POLL_TIMEOUT_MESSAGE = "hp客户连接超时";
		WS_TIMEOUT_MESSAGE = "ws客户连接超时";
	}
}
