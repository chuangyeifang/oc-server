package com.oc.service.team.impl;

import com.oc.domain.team.TeamMonitor;
import com.oc.mapper.team.TeamMonitorMapper;
import com.oc.service.team.TeamMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 团队监控
 * @author chuangyeifang
 */
@Service
public class TeamMonitorServiceImpl implements TeamMonitorService {

    @Autowired
    private TeamMonitorMapper teamMonitorMapper;

    @Override
    public TeamMonitor obtainYesterdayTeamMonitor(String tenantCode, Integer teamCode) {
        return teamMonitorMapper.obtainYesterdayTeamMonitor(tenantCode, teamCode);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int updateWaitCount(String tenantCode, Integer teamCode, Integer waitCount) {
        int ret = teamMonitorMapper.updateWaitCount(tenantCode, teamCode, waitCount);
        if (0 == ret) {
            TeamMonitor tm = new TeamMonitor();
            tm.setTenantCode(tenantCode);
            tm.setTeamCode(teamCode);
            tm.setWaitCount(waitCount);
            teamMonitorMapper.insertSelective(tm);
        }
        return ret;
    }
}
