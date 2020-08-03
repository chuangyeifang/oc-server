/**
 * 
 */
package com.oc.dto.chat;

/**
 * @Description: 统计消息数量
 * @author chuangyeifang
 * @createDate 2020年2月26日
 * @version v 1.0
 */
public class ChatRecordStatistics {

	private String ownerType;
	
	private Integer msgCount = 0;

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public Integer getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(Integer msgCount) {
		this.msgCount = msgCount;
	}
}
