package com.oc.filter;

import com.oc.auth.UserStore;
import com.oc.auth.WaiterAuthCoder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月10日
 * @version v 1.0
 */
@Order(1)
@WebFilter(filterName = "authenticationFilter", urlPatterns = "/*")
@Component
public class AuthenticationFilter implements Filter{

	private static final String COOKIE_NAME = "OMW-Passport";

	private static final String[] NON_FILTERS = {"/waiter/login", "/customer/auth", "/front"};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;

		String uri = req.getRequestURI();
		for (String path :NON_FILTERS) {
			if(uri.startsWith(path)) {
				chain.doFilter(req, response);
				return;
			}
		}

		Cookie[] cookies = req.getCookies();
		if (null == cookies) {
			authFailed(resp, "请重启登录");
			return;
		}
		String cookieValue = null;
		for (Cookie cookie : cookies) {
			if(COOKIE_NAME.equals(cookie.getName())) {
				cookieValue = cookie.getValue();
				break;
			}
		}
		if (null != cookieValue) {
			WaiterAuthCoder.AuthResult authResult = WaiterAuthCoder.decode(cookieValue);
			if (authResult.getRc() == 0) {
				UserStore.set(authResult.getInfo());
				chain.doFilter(req, response);
			} else {
				authFailed(resp, authResult.getRm());
			}
		} else {
			authFailed(resp, "无效认证");
		}
	}

	private void authFailed(HttpServletResponse resp, String msg) {
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json; charset=utf-8");
			try (PrintWriter writer = resp.getWriter()) {
				writer.write("{\"rc\": 1, \"rm\": \"" + msg + "\"}");
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
