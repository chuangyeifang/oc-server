package com.oc.provider.db;

import com.oc.provider.context.SpringContext;
import com.oc.service.team.TeamMonitorService;
import com.oc.service.team.impl.TeamMonitorServiceImpl;

/**
 * 团队监控
 * @author chuangyeifang
 */
public class TeamMonitorProvider {

    private TeamMonitorService teamMonitorService = SpringContext.getBean(TeamMonitorServiceImpl.class);

    private TeamMonitorProvider() {}

    public void updateWaitCount(String tenantCode, Integer teamCode, Integer waitCount) {
        teamMonitorService.updateWaitCount(tenantCode, teamCode, waitCount);
    }

    public static TeamMonitorProvider getInst() {
        return Single.inst;
    }

    private static class Single {
        private static TeamMonitorProvider inst = new TeamMonitorProvider();
    }
}
