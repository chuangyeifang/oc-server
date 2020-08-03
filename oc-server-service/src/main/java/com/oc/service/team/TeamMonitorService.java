package com.oc.service.team;

import com.oc.domain.team.TeamMonitor;

/**
 * 团队监控
 * @author chuangyeifang
 */
public interface TeamMonitorService {

    /**
     * 获取昨天团队监控信息
     * @param tenantCode
     * @param teamCode
     * @return
     */
    TeamMonitor obtainYesterdayTeamMonitor(String tenantCode, Integer teamCode);

    /**
     * 增加排队数
     * @param tenantCode
     * @param teamCode
     * @param waitCount
     * @return
     */
    int updateWaitCount(String tenantCode, Integer teamCode, Integer waitCount);
}
