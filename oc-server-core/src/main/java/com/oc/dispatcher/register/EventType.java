package com.oc.dispatcher.register;

/**
 * @Description: 时间类型
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
public enum EventType {
	/**
	 * 访客请求
	 */
	CUSTOMER_REQ,
	/**
	 * 客服空闲
	 */
	WAITER_IDLE,
	/**
	 * 客服手动请求分配
	 */
	WAITER_MANUAL_REQ,
	/**
	 * 分配成功
	 */
	READY_DONE,
	/**
	 * 团队转接
	 */
	TRANSFER_TEAM;

	EventType() {}
}
