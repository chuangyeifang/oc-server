/**
 * 
 */
package com.oc.restcontroller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月8日
 * @version v 1.0
 */
public abstract class AbstractBasicRestController {
	
	private final static String defaultCallback = "defaultCallback";
	private final static String SUCCESS_MSG = "操作成功";

	@Autowired
	protected HttpServletRequest request;
	
	@Autowired
	protected HttpServletResponse  response;
	
	protected void setCookies(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 支持成功状态返回
	 * @return
	 */
	protected Object success() {
		String callback = request.getParameter("callback");
		if (!existEmpty(callback)) {
			return new JSONPObject(callback, new ResponseResult(0, SUCCESS_MSG));
		}

		return new ResponseResult(0, SUCCESS_MSG);
	}

	/**
	 * 成功返回结果 默认支持分页
	 * @param data 可以为 Page对象类型
	 * @return
	 */
	protected Object success(Object data) {
		ResponseResult result;
		if(data instanceof Page) {
			result = new ResponseResult(0, SUCCESS_MSG, data, new PageView((Page<?>) data));
		} else {
			result = new ResponseResult(0, SUCCESS_MSG, data);
		}
		String callback = request.getParameter("callback");
		if (!existEmpty(callback)) {
			return new JSONPObject(callback, result);
		}
		return result;
	}

	/**
	 * 操作失败
	 * @param rc
	 * @param rm
	 * @return
	 */
	protected Object failed(int rc, String rm) {
		String callback = request.getParameter("callback");
		if (!existEmpty(callback)) {
			return new JSONPObject(callback, new ResponseResult(rc, rm));
		}
		return new ResponseResult(rc, rm);
	}

	/**
	 * 自动包装分页信息，并返回分页后结果
	 * 默认不进行count 查询
	 * @return
	 */
	protected Page<Object> startPage() {
		return startPage(false);
	}

	/**
	 * 自动包装分页信息，并返回分页后结果
	 * @return
	 */
	protected Page<Object> startPage(boolean count) {
		String strPageNum = request.getParameter("pageNum");
		String strPageSize = request.getParameter("pageSize");
		int pageNum = 1;
		int pageSize = 10;
		if (!existEmpty(strPageNum)) {
			pageNum = Integer.parseInt(strPageNum);
		}
		if (!existEmpty(strPageSize)) {
			pageSize = Integer.parseInt(strPageSize);
		}
		return PageHelper.startPage(pageNum, pageSize, count);
	}
	
	protected boolean existEmpty(String...params) {
		for (String param : params) {
			if (null == param || "".equals(param.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 响应结果
	 * @Description:
	 * @author chuangyeifang
	 * @createDate 2020年7月26日
	 * @version v 1.0
	 */
	@Getter
	@Setter
	public static class ResponseResult {

		private int rc;

		private String rm;

		private Object data;

		private Object pageView;

		public ResponseResult(int rc, String rm) {
			this(rc, rm, null);
		}

		public ResponseResult(int rc, String rm, Object data) {
			this(rc, rm, data, null);
		}

		public ResponseResult(int rc, String rm, Object data, Object pageView) {
			this.rc = rc;
			this.rm = rm;
			this.data = data;
			this.pageView = pageView;
		}
	}

	/**
	 * 分页结果类
	 * @Description:
	 * @author chuangyeifang
	 * @createDate 2020年1月7日
	 * @version v 1.0
	 */
	@Getter
	@Setter
	public static class PageView{

		private int pageNum;

		private int pageSize;

		private long total;

		public PageView(Page<?> page) {
			this.pageNum = page.getPageNum();
			this.pageSize = page.getPageSize();
			this.total = page.getTotal();
		}
	}
}
