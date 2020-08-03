package com.oc.auth;

import com.oc.common.utils.MD5Encrypt;
import com.oc.domain.waiter.Waiter;
import com.oc.util.base64.B64;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;

/**
 * @Description: Cookie信息编解码
 *  * @author chuangyeifang
 *  * @createDate 2020年7月30日
 *  * @version v 1.0
 */
@Slf4j
public class WaiterAuthCoder {

	public final static long LIMIT_TIME = 24 * 60 * 60;

	public static AuthResult decode(String waiterCookieValueBase64) {
		try {
			String waiterCookieValue = B64.decoder(URLDecoder.decode(waiterCookieValueBase64, "UTF-8"));
			String[] body = waiterCookieValue.split("=");
			if (body.length != 2) {
				return new AuthResult(3, "无法解析token信息");
			}
			String[] params = body[0].split("\\|");
			if (params.length < 5) {
				return new AuthResult(4, "参数存在问题");
			}
			if (MD5Encrypt.check(body[1], body[0])){
				WaiterInfo authInfo = new WaiterInfo();
				authInfo.setWaiterName(params[0]);
				authInfo.setWaiterCode(params[1]);
				authInfo.setTenantCode(params[2]);
				authInfo.setTeamCode(params[3]);
				long createTime = Long.parseLong(params[4]) / 1000;
				long curTime = System.currentTimeMillis() / 1000;
				if (curTime - createTime > LIMIT_TIME) {
					return new AuthResult(2, "token 已经过期，请重新登录");
				}
				return new AuthResult(0, "验证通过", authInfo);
			}
		} catch (Exception e) {
			log.warn("登录验证失败", e);
		}
		return new AuthResult(2, "验证不通过");
	}
	
	public static String encode(Waiter waiter) {
		StringBuilder sb = new StringBuilder();
		sb.append(waiter.getWaiterName()).append("|");
		sb.append(waiter.getWaiterCode()).append("|");
		sb.append(waiter.getTenantCode()).append("|");
		sb.append(waiter.getTeamCode()).append("|");
		sb.append(System.currentTimeMillis());
		String sign = MD5Encrypt.sign(sb.toString());
		sb.append("=").append(sign);
		return B64.encoder(sb.toString());
	}
	
	public static class AuthResult{
		private WaiterInfo info;
		private int rc;
		private String rm;
		
		public AuthResult(int rc, String rm) {
			this(rc, rm, null);
		}
		
		public AuthResult(int rc, String rm, WaiterInfo info) {
			this.info = info;
			this.rc = rc;
			this.rm = rm;
		}
		public WaiterInfo getInfo() {
			return info;
		}
		public void setInfo(WaiterInfo info) {
			this.info = info;
		}
		public int getRc() {
			return rc;
		}
		public void setRc(int rc) {
			this.rc = rc;
		}
		public String getRm() {
			return rm;
		}
		public void setRm(String rm) {
			this.rm = rm;
		}
	}
}
