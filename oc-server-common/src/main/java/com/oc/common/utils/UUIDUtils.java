/**
 * 
 */
package com.oc.common.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月6日
 * @version v 1.0
 */
public class UUIDUtils {

	private static SecureRandom random = new SecureRandom();

	public static String getID(int num) {
		return new BigInteger(num * 5, random).toString(32);
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String createPid(int pre) {
		return pre + "-" + UUID.randomUUID().toString();
	}
}
