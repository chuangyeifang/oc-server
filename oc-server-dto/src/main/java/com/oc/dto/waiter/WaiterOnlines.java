/**
 * 
 */
package com.oc.dto.waiter;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月15日
 * @version v 1.0
 */
public class WaiterOnlines {
	private String tenantCode;
	private String teamCode;
	private String teamName;
	private String waiterName;
	private String waiterCode;
	private String status;
	private String type;
	private String shunt;
	private String curReception;
	private String maxReception;
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
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getShunt() {
		return shunt;
	}
	public void setShunt(String shunt) {
		this.shunt = shunt;
	}
	public String getCurReception() {
		return curReception;
	}
	public void setCurReception(String curReception) {
		this.curReception = curReception;
	}
	public String getMaxReception() {
		return maxReception;
	}
	public void setMaxReception(String maxReception) {
		this.maxReception = maxReception;
	}
}
