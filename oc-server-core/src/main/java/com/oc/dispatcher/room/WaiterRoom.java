package com.oc.dispatcher.room;

import com.oc.domain.waiter.Waiter;
import com.oc.transfer.TransferWaiter;

import java.util.Collection;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月25日
 * @version v 1.0
 */
public interface WaiterRoom {

	/**
	 * 客服登录
	 * @param waiter
	 */
	void login(Waiter waiter);

	/**
	 * 客服登出
	 * @param teamCode
	 * @param waiterCode
	 */
	void logout(Integer teamCode, String waiterCode);

	/**
	 * 获取客服
	 * @param teamCode
	 * @return
	 */
	Waiter acquire(Integer teamCode);

	/**
	 * 获取客服
	 * @param teamCode
	 * @param waiterCode
	 * @return
	 */
	Waiter acquire(Integer teamCode, String waiterCode);

	/**
	 * 直接获取获取客服
	 * @param teamCode
	 * @param waiterCode
	 * @return
	 */
	Waiter directAcquire(Integer teamCode, String waiterCode);

	/**
	 * 按团队获取所有客服
	 * @param teamCode
	 * @return
	 */
	Collection<Waiter> getWaiters(Integer teamCode);

	/**
	 * 客服变更服务状态
	 * @param teamCode
	 * @param waiterCode
	 * @param status
	 * @return
	 */
	boolean changeStatus(Integer teamCode, String waiterCode, String status);

	/**
	 * 尝试按客服转接客户
	 * @param transferWaiter
	 * @return
	 */
	boolean tryTransferByWaiter(TransferWaiter transferWaiter);

	/**
	 * 释放客服资源
	 * @param teamCode
	 * @param waiterCode
	 */
	void release(Integer teamCode, String waiterCode);

	/**
	 * 获取客服
	 * @param teamCode
	 * @param waiterCode
	 * @return
	 */
	Waiter getWaiter(Integer teamCode, String waiterCode);
}
