package com.oc.mapper.waiter;

import com.oc.domain.waiter.WaiterLog;

/**
 * @author chuangyeifang
 */
public interface WaiterLogMapper {
    /**
     * 保存客服操作日志
     * @param record
     * @return
     */
    int insertSelective(WaiterLog record);
}