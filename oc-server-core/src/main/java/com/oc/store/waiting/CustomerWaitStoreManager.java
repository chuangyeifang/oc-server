package com.oc.store.waiting;

import com.oc.session.Customer;
import com.oc.store.waiting.listener.CustomerWaitStoreListener;
import com.oc.store.waiting.listener.DbCustomerWaitStoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户排队统计
 * @author chuangyeifang
 */
public class CustomerWaitStoreManager {

    private static List<CustomerWaitStoreListener> listeners = new ArrayList<>();

    private CustomerWaitStoreManager() {
        //TODO 暂时放弃MQ registerListener(new KafkaCustomerWaitStoreListener());
        registerListener(new DbCustomerWaitStoreListener());
    }

    /**
     * 注册消息监听
     * @param listener
     */
    public void registerListener(CustomerWaitStoreListener listener) {
        listeners.add(listener);
    }

    public void enterQueue(Customer customer, int queueSize) {
        //同步消息到监听者
        for (CustomerWaitStoreListener customerWaitStoreListener : listeners) {
            customerWaitStoreListener.enterQueue(customer, queueSize);
        }
    }

    public void leaveQueue(Customer customer, int queueSize) {
        //同步消息到监听者
        for (CustomerWaitStoreListener customerWaitStoreListener : listeners) {
            customerWaitStoreListener.leaveQueue(customer, queueSize);
        }
    }

    public static CustomerWaitStoreManager getInst() {
        return Single.inst;
    }

    private static class Single {
        private static CustomerWaitStoreManager inst = new CustomerWaitStoreManager();
    }
}
