/**
 * 
 */
package com.oc.sasl.provider;

import java.security.Provider;

import com.oc.sasl.factory.CsSaslServerFactory;

/**
 * @Description: Sasl 将自定义的Mechanism加入Provider
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class CsSaslProvider extends Provider{
	
	private static final long serialVersionUID = -5460312101653982457L;

	public CsSaslProvider() {
		super("OnlineCall", 1.01, "OnlineCall SASL provider v1.01" );
		CsSaslServerFactory factory = new CsSaslServerFactory();
		String[] mechanismNames = factory.getMechanismNames(null);
		for (String mechanismName : mechanismNames) {
			put("SaslServerFactory." + mechanismName, factory.getClass().getCanonicalName());
		}
	}
}
