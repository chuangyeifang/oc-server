/**
 * 
 */
package com.oc.sasl.callback;

import java.io.Serializable;

import javax.security.auth.callback.Callback;

import com.oc.message.type.Identity;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class AuthorizeCallback implements Callback, Serializable{

	private static final long serialVersionUID = -8981179557338523734L;
	
	private Identity identity;
	
	private String userName;
	
	private String password;
	
	private boolean authorized = false;
	
	private Object data;
	
	public AuthorizeCallback(Identity identity, String userName, String password) {
		this.userName = userName;
		this.password = password;
		this.identity = identity;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
}
