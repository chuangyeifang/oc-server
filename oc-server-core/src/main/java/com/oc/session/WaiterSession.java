package com.oc.session;

import com.oc.domain.waiter.Waiter;

/**
 * @Description: 用户 session
 * @author chuangyeifang
 * @createDate 2020年1月18日
 * @version v 1.0
 */
public interface WaiterSession extends Session{

	/**
	 * 获取客服信息
	 * @return
	 */
	Waiter getWaiter();
}
