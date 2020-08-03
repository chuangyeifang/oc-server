package com.oc.dispatcher.room;

import java.util.List;

import com.oc.session.Customer;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月25日
 * @version v 1.0
 */
public interface CustomerRoom {

	/**
	 * 添加排队客户
	 * @param customer
	 * @return
	 */
	int addWaiting(Customer customer);

	/**
	 * 获取排队客户
	 * @param teamCode
	 * @return
	 */
	Customer acquire(Integer teamCode);

	/**
	 * 移除排队客户
	 * @param customer
	 */
	void removeWaiting(Customer customer);

	/**
	 * 根据当前团队所有排队客户
	 * @param teamCode
	 * @return
	 */
	List<Customer> getWaits(Integer teamCode);

	/**
	 * 获取当前团队排队人数
	 * @param teamCode
	 * @return
	 */
	int size(Integer teamCode);
}
