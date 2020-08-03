package com.oc.util.http;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.util.StringUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

/**
 * @Description: Http 处理
 * @author chuangyeifang
 * @createDate 2020年1月10日
 * @version v 1.0
 */
public final class HttpRequests {
	
	public static String getRequestParam(Map<String, List<String>> parameters, String name) {
		List<String> params = parameters.get(name);
		return params == null ? null : StringUtils.isEmpty(params.get(0)) ? null : params.get(0);
	}

	public static String getCookie(HttpHeaders headers, String cookieName) {
		List<String> cookieList = headers.getAll(HttpHeaderNames.COOKIE);
		if (null == cookieList || cookieList.isEmpty()) {
			return null;
		}
		String cookieValue = null;
		Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieList.get(0));
		if (cookies.size() > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.name().equals(cookieName)) {
					cookieValue = cookie.value();
					break;
				}
			}
		}
		return cookieValue;
	}
}
