package com.oc.exception;

import javax.security.sasl.SaslException;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class SaslFailureException extends SaslException {

	private static final long serialVersionUID = -4965040934998529695L;
	
	public String msg;
	
	public SaslFailureException(String message, String msg) {
		super(message);
		this.msg = msg;
	}
}
