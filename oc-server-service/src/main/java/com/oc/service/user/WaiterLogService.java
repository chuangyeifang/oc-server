/**
 * 
 */
package com.oc.service.user;

import com.oc.domain.waiter.WaiterLog;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年6月5日
 * @version v 1.0
 */
public interface WaiterLogService {
	/**
	 * 保存客服操作日志
	 * @param record
	 * @return
	 */
	int insert(WaiterLog record);
}
