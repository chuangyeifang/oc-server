package com.oc.store.waiting.listener;

import com.oc.session.Customer;

/**
 * 客户排队监听器
 * @author chuangyeifang
 */
public interface CustomerWaitStoreListener {

    /**
     * 进入队列
     * @param customer
     * @param queueSize
     */
    void enterQueue(Customer customer, int queueSize);

    /**
     * 离开队列
     * @param customer
     * @param queueSize
     */
    void leaveQueue(Customer customer, int queueSize);
}
