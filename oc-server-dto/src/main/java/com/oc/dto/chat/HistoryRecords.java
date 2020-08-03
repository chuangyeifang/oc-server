/**
 * 
 */
package com.oc.dto.chat;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年4月26日
 * @version v 1.0
 */
public class HistoryRecords {
	
	private String customerName;
	
	private String customerCode;
	
	private String count;
	
	public HistoryRecords() {}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
}
