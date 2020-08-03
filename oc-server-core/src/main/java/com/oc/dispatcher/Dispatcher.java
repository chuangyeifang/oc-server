package com.oc.dispatcher;

import com.oc.dispatcher.register.Event;
import com.oc.domain.waiter.Waiter;
import com.oc.session.Customer;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import com.oc.transfer.TransferTeam;
import com.oc.transfer.TransferWaiter;

import java.util.Collection;
import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月5日
 * @version v 1.0
 */
public interface Dispatcher {

	/**
	 * 客服登录
	 * @param waiter
	 */
	void login(Waiter waiter);

	/**
	 * 客服登出
	 * @param session
	 */
	void logout(WaiterSession session);

	/**
	 * 客服变更服务状态
	 * @param teamCode
	 * @param waiterCode
	 * @param status
	 */
	void changeWaiterStatus(Integer teamCode, String waiterCode, String status);

	/**
	 * 按团队获取客服列表
	 * @param teamCode
	 * @return
	 */
	Collection<Waiter> getWaiters(Integer teamCode);

	/**
	 * 获取客服
	 * @param teamCode
	 * @return
	 */
	Waiter acquireWaiter(Integer teamCode);

	/**
	 * 获取客服
	 * @param teamCode
	 * @param waiterCode
	 * @return
	 */
	Waiter acquireWaiter(Integer teamCode, String waiterCode);

	/**
	 * 获取客户
	 * @param teamCode
	 * @return
	 */
	Customer acquireCustomer(Integer teamCode);

	/**
	 * 客户加入等待队列
	 * @param customer
	 */
	void addWaiting(Customer customer);

	/**
	 * 获取排队客户列表
	 * @param teamCode
	 * @return
	 */
	@SuppressWarnings("unused")
	List<Customer> getWaits(Integer teamCode);

	/**
	 * 客户移除排队队列
	 * @param customer
	 */
	void removeWait(Customer customer);

	/**
	 * 是否存在排队客户
	 * @param teamCode
	 * @return
	 */
	boolean existWait(Integer teamCode);

	/**
	 * 注册分配事件
	 * @param event
	 */
	void registerAllotEvent(Event event);

	/**
	 * 事件是否存在
	 * @param event
	 * @return
	 */
	boolean hasAllotEvent(Event event);

	/**
	 * 取消事件
	 * @param event
	 * @return
	 */
	@SuppressWarnings("unused")
	boolean cancelAllotRegister(Event event);

	/**
	 * 构建客服、客户服务关系
	 * @param event
	 */
	void buildRelation(Event event);

	/**
	 * 直接释放客服、客户关系
	 * @param waiterCode
	 */
	void directReleaseRelation(String waiterCode);

	/**
	 * 按客服转接
	 * @param transferWaiter
	 * @return
	 */
	boolean transferByWaiter(TransferWaiter transferWaiter);

	/**
	 * 按团队转接
	 * @param transferTeam
	 * @return
	 */
	boolean transferByTeam(TransferTeam transferTeam);

	/**
	 * 关闭会话
	 * @param session
	 */
	void closeChat(CustomerSession session);
}
