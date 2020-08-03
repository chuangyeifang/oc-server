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
public class MD5Encrypt {
	
	private final static String salt = "0M-(@=#_+!)Hello World!";
	
	public static String sign(String str) {		
		return Md5Crypt.apr1Crypt(str.getBytes(), salt);
	}
	
	public static boolean check(String sign, String str) {
		String newSign = Md5Crypt.apr1Crypt(str.getBytes(), salt);
		return sign.equals(newSign);
	}
	
	public static String sign(String str, String salt) {
		return Md5Crypt.apr1Crypt(str.getBytes(), salt);
	}
	
	public static boolean check(String sign, String salt, String str) {
		String newSign = Md5Crypt.apr1Crypt(str.getBytes(), salt);
		return sign.equals(newSign);
	}
}
