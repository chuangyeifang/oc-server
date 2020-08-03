/**
 * 
 */
package com.oc.auth;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月29日
 * @version v 1.0
 */
public class WaiterInfo {
	
	private String tenantCode;
	
	private String teamCode;
	
	private String waiterName;
	
	private String waiterCode;
	
	private String sign;

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getTeamCode() {
		return teamCode;
	}

	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}

	public String getWaiterName() {
		return waiterName;
	}

	public void setWaiterName(String waiterName) {
		this.waiterName = waiterName;
	}

	public String getWaiterCode() {
		return waiterCode;
	}

	public void setWaiterCode(String waiterCode) {
		this.waiterCode = waiterCode;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "WaiterInfo{" +
				"tenantCode='" + tenantCode + '\'' +
				", teamCode='" + teamCode + '\'' +
				", waiterName='" + waiterName + '\'' +
				", waiterCode='" + waiterCode + '\'' +
				", sign='" + sign + '\'' +
				'}';
	}
}
