package com.oc.session;

import io.netty.util.AttributeKey;

/**
 * @Description: 用户登录状态
 * @author chuangyeifang
 * @createDate 2020年7月20日
 * @version v 1.0
 */
public class LoginStatus{
	
	public final static AttributeKey<Status> LOGIN_STATUS = AttributeKey.valueOf("LOGIN_STATUS");
	
	public enum Status {

		// 已经登录
		LOGINED,
		// 未登录
		UN_LOGIN
	}
}
