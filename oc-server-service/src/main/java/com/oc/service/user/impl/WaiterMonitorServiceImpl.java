package com.oc.service.user.impl;

import com.oc.domain.waiter.Waiter;
import com.oc.domain.waiter.WaiterMonitor;
import com.oc.mapper.waiter.WaiterMonitorMapper;
import com.oc.service.user.WaiterMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客服监控
 * @author chuangyeifang
 */
@Service
public class WaiterMonitorServiceImpl implements WaiterMonitorService {

    @Autowired
    private WaiterMonitorMapper waiterMonitorMapper;

    @Override
    public int waiterEnterService(Waiter waiter, String status) {
        int ret = waiterMonitorMapper.updateStatus(waiter.getTeamCode(), waiter.getWaiterCode(), status);
        ret = createWaiterMonitor(waiter, ret, 0);
        if (ret == 0) {
            return waiterMonitorMapper.updateStatus(waiter.getTeamCode(), waiter.getWaiterCode(), status);
        }
        return ret;
    }

    @Override
    public int updateStatus(Waiter waiter, String beforeStatus, String afterStatue) {
        int ret = waiterMonitorMapper.updateTime(waiter.getTeamCode(),
                waiter.getWaiterCode(), beforeStatus, afterStatue);
        ret = createWaiterMonitor(waiter, ret, 0);
        if (ret == 0) {
            return waiterMonitorMapper.updateTime(waiter.getTeamCode(), waiter.getWaiterCode(),
                    beforeStatus, afterStatue);
        }
        return ret;
    }


    @Override
    public int updateSysBusyTime(Integer teamCode, String waiterCode, Long time) {
        return waiterMonitorMapper.updateSysBusyTime(teamCode, waiterCode, time);
    }

    @Override
    public WaiterMonitor obtainWaiterMonitorOfDay(String waiterCode) {
        return waiterMonitorMapper.obtainWaiterMonitorOfDay(waiterCode);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    protected int createWaiterMonitor(Waiter waiter, int ret, int reception) {
        if (0 == ret) {
            String waiterCode = waiter.getWaiterCode();
            WaiterMonitor wm = new WaiterMonitor();
            wm.setWaiterCode(waiter.getWaiterCode());
            wm.setWaiterName(waiter.getWaiterName());
            wm.setTenantCode(waiter.getTenantCode());
            wm.setTeamCode(waiter.getTeamCode());
            wm.setStatus(Integer.parseInt(waiter.getStatus()));
            wm.setReceptionCount(reception);
            try {
                ret = waiterMonitorMapper.insertSelective(wm);
            } catch (Exception e) {
                // 已经插入 异常捕获忽略
            }
            if (ret != 0) {
                // 获取跨天状态!4状态 监控信息
                WaiterMonitor waiterMonitor = obtainYesterdayWaiterMonitor(waiter.getTeamCode(), waiterCode);
                if (null != waiterMonitor) {
                    String status = String.valueOf(waiterMonitor.getStatus());
                    // 修复跨天时间统计
                    waiterMonitorMapper.repairYesterdayMonitor(waiter.getTeamCode(), waiterCode, status);
                    waiterMonitorMapper.repairCurDateMonitor(waiter.getTeamCode(), waiterCode, status);
                }
            }
        }
        return ret;
    }

    private WaiterMonitor obtainYesterdayWaiterMonitor(Integer teamCode, String waiterCode) {
        return waiterMonitorMapper.obtainYesterdayWaiterMonitor(teamCode, waiterCode);
    }
}
