/**
 * 
 */
package com.oc.common.utils;

import org.apache.commons.codec.digest.Md5Crypt;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月10日
 * @version v 1.0
 */
public class PwdEncrypt {
	
	private final static String salt = "set-MM!!@[customer]Oc-MD5-Base64?|}{(pwd)";

	public static boolean check(String mD5Password, String account, String password) {
		int len = account.length();
		String authStr = account.substring(len, len - 1).toUpperCase() + password + salt;
		String newSign = Md5Crypt.apr1Crypt(authStr.getBytes(), salt);
		return mD5Password.equals(newSign);
	}

	public static String createMD5Password(String account, String password) {
		int len = account.length();
		String authStr = account.substring(len - 4, len - 1).toUpperCase() + password + salt;
		return Md5Crypt.apr1Crypt(authStr.getBytes(), salt);
	}
}
