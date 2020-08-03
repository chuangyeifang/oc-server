package com.oc.util;

import com.oc.core.coder.JacksonJsonSupport;
import com.oc.core.coder.JsonSupport;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年6月25日
 * @version v 1.0
 */
public class JsonUtils {
	private static JsonSupport jsonSupport = new JacksonJsonSupport();
	
	public static JsonSupport getJson() {
		return jsonSupport;
	}
}
