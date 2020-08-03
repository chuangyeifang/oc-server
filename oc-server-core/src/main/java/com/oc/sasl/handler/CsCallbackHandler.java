/**
 * 
 */
package com.oc.sasl.handler;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.oc.sasl.callback.AuthorizeCallback;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class CsCallbackHandler implements CallbackHandler{

	/**
	 * @param callbacks
	 * @throws IOException
	 * @throws UnsupportedCallbackException
	 */
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		
		for (Callback callback : callbacks) {
			if (callback instanceof AuthorizeCallback) {
			}else {
                throw new UnsupportedCallbackException(callback, "无法识别 Callback");
            }
		}
	}

}
