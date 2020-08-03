package com.oc.provider.db;

import com.oc.domain.waiter.Waiter;
import com.oc.domain.waiter.WaiterLog;
import com.oc.provider.context.SpringContext;
import com.oc.service.user.WaiterLogService;
import com.oc.service.user.WaiterService;
import com.oc.service.user.impl.WaiterLogServiceImpl;
import com.oc.service.user.impl.WaiterServiceImpl;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月27日
 * @version v 1.0
 */
public class WaiterProvider {
	
	private WaiterService waiterService = SpringContext.getBean(WaiterServiceImpl.class);
	private WaiterLogService waiterLogService = SpringContext.getBean(WaiterLogServiceImpl.class);
	
	/**
	 * 客服授权登录
	 * @param tenantCode
	 * @param waiterName
	 * @return
	 */
	public Waiter authentication(String tenantCode, String waiterName) {
		return waiterService.obtainWaiter(tenantCode, waiterName);
	}

	/**
	 * 更新id为当前的客服状态
	 * @param id 客服id
	 * @param status 状态：1在线2忙碌3离线
	 */
	public void updateStatus(Long id, String status) {
		waiterService.updateStatus(id, status);
	}
	
	/**
	 * 按id更新客服当前接待量
	 * @param id 客服id
	 * @param reception 接待数
	 */
	public void updateCurReception(Long id, Integer reception) {
		waiterService.updateCurReception(id, reception);
	}

	/**
	 * 按客服工号更新当前接待量
	 * @param waiterCode 客服工号
	 * @param reception 接待数
	 */
	public void updateCurReceptionByCode(String waiterCode, Integer reception) {
		waiterService.updateCurReceptionByCode(waiterCode, reception);
	}

	/**
	 * 客服退出登录
	 * @param id
	 */
	public void waiterLogout(Long id) {
		waiterService.waiterLogout(id);
	}

	/**
	 * 保存客服登录日志
	 * @param waiter
	 * @param type 1登录 2切换状态 3登出 4异常登出
	 * @param ip
	 */
	public void insertWaiterLog(Waiter waiter, String type, String ip) {
		WaiterLog record = new WaiterLog();
		record.setTenantCode(waiter.getTenantCode());
		record.setTeamCode(waiter.getTeamCode());
		record.setWaiterName(waiter.getWaiterName());
		record.setWaiterCode(waiter.getWaiterCode());
		record.setIp(ip);
		record.setStatus(waiter.getStatus());
		record.setType(type);
		waiterLogService.insert(record);
	}

	public static WaiterProvider getInst() {
		return Single.instance;
	}
	
	private static class Single {
		private static WaiterProvider instance = new WaiterProvider();
	}
}
