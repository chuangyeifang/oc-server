/**
 * 
 */
package com.oc.service.user.impl;

import com.oc.domain.waiter.Waiter;
import com.oc.mapper.waiter.WaiterMapper;
import com.oc.service.user.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 客服操作
 * @author chuangyeifang
 * @createDate 2020年7月24日
 * @version v 1.0
 */
@Service
public class WaiterServiceImpl implements WaiterService {

	@Autowired
	private WaiterMapper waiterMapper;

	@Override
	public Waiter login(String tenantCode, String waiterName, String password) {
		return waiterMapper.login(tenantCode, waiterName, password);
	}

	@Override
	public Waiter obtainWaiter(String tenantCode, String waiterName) {
		return waiterMapper.obtainWaiter(tenantCode, waiterName);
	}

	@Override
	public int updateCurReception(Long id, Integer reception) {
		return waiterMapper.updateCurReception(id, reception);
	}

	@Override
	public int updateCurReceptionByCode(String waiterCode, Integer reception) {
		return waiterMapper.updateCurReceptionByCode(waiterCode, reception);
	}

	@Override
	public int updateStatus(Long id, String status) {
		return waiterMapper.updateStatus(id, status);
	}

	@Override
	public Waiter obtainWaiterByCode(String waiterCode) {
		return waiterMapper.obtainWaiterByCode(waiterCode);
	}

	@Override
	public int waiterLogout(Long id) {
		return waiterMapper.waiterLogout(id);
	}

	@Override
	public void logout(String waiterCode) {
		waiterMapper.logout(waiterCode);
	}
}
