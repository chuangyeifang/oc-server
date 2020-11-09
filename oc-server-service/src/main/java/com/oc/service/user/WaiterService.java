/**
 * 
 */
package com.oc.service.user;

import com.oc.domain.waiter.Waiter;
import com.oc.dto.waiter.WaiterOnlines;

import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月24日
 * @version v 1.0
 */
public interface WaiterService {

	/**
	 * 客服登录
	 * @param tenantCode
	 * @param waiterName
	 * @param password
	 * @return
	 */
	Waiter login(String tenantCode, String waiterName, String password);

	/**
	 * 获取客服
	 * @param tenantCode
	 * @param waiterName
	 * @return
	 */
	Waiter obtainWaiter(String tenantCode, String waiterName);

	/**
	 * 获取客服
	 * @param tenantCode
	 * @return
	 */
	List<WaiterOnlines> obtainWaiterOnlines(String tenantCode);

	/**
	 * 根据id更新当前接待量
	 * @param id
	 * @param reception
	 * @return
	 */
	int updateCurReception(Long id, Integer reception);

	/**
	 * 根据工号更新当前接待量
	 * @param waiterCode
	 * @param reception
	 * @return
	 */
	int updateCurReceptionByCode(String waiterCode, Integer reception);

	/**
	 * 更新工作状态
	 * @param id
	 * @param status
	 * @return
	 */
	int updateStatus(Long id, String status);


	/**
	 * 根据code 获取客服信息
	 * @param waiterCode
	 * @return
	 */
    Waiter obtainWaiterByCode(String waiterCode);

	/**
	 * 客服登出
	 * @param id
	 * @return
	 */
    int waiterLogout(Long id);

	/**
	 * 客服退出登录
	 * @param waiterCode
	 */
	void logout(String waiterCode);
}
