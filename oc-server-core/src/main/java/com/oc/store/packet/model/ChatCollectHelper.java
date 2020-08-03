package com.oc.store.packet.model;

import com.oc.dto.chat.ChatRecordStatistics;
import lombok.Getter;

import java.util.List;

/**
 * @Description: 统计消息数量
 * @author chuangyeifang
 * @createDate 2020年2月26日
 * @version v 1.0
 */
@Getter
public class ChatCollectHelper {

	private int waiterMsgCount = 0;
	
	private int customerMsgCount = 0;

	public ChatCollectHelper(List<ChatRecordStatistics> chatRecordStatistics) {
		init(chatRecordStatistics);
	}

	private void init(List<ChatRecordStatistics> chatRecordStatistics) {
		if (null != chatRecordStatistics) {
			for (ChatRecordStatistics crs : chatRecordStatistics) {
				if ("1".equals(crs.getOwnerType())) {
					this.customerMsgCount = crs.getMsgCount();
				} else if ("2".equals(crs.getOwnerType())) {
					this.waiterMsgCount = crs.getMsgCount();
				}
			}
		}
	}

	public String isEffective() {
		if (waiterMsgCount > 0) {
			return "1";
		} else {
			return "0";
		}
	}

	public int getTotalCount() {
		return customerMsgCount + waiterMsgCount;
	}
}
