/**
 * 
 */
package com.oc.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.waiter.WaiterLog;
import com.oc.mapper.waiter.WaiterLogMapper;
import com.oc.service.user.WaiterLogService;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年6月5日
 * @version v 1.0
 */
@Service
public class WaiterLogServiceImpl implements WaiterLogService{

	@Autowired
	private WaiterLogMapper waiterLogMapper;
	
	/**
	 * 记录客服登录日志
	 * @param record
	 * @return
	 */
	@Override
	public int insert(WaiterLog record) {
		return waiterLogMapper.insertSelective(record);
	}

}
