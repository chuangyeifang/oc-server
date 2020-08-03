package com.oc.store.waiting.listener;

import com.oc.provider.db.TeamMonitorProvider;
import com.oc.session.Customer;

/**
 * 同步数据库
 * @author chuangyeifang
 */
public class DbCustomerWaitStoreListener implements CustomerWaitStoreListener {

    @Override
    public void enterQueue(Customer customer, int queueSize) {
        TeamMonitorProvider.getInst().updateWaitCount(customer.getTenantCode(), customer.getTeamCode(), queueSize);
    }

    @Override
    public void leaveQueue(Customer customer, int queueSize) {
        TeamMonitorProvider.getInst().updateWaitCount(customer.getTenantCode(), customer.getTeamCode(), queueSize);
    }
}
