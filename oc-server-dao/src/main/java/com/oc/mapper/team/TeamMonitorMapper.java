package com.oc.mapper.team;

import com.oc.domain.team.TeamMonitor;
import org.apache.ibatis.annotations.Param;

/**
 * @author chuangyeifang
 */
public interface TeamMonitorMapper {

    /**
     * 获取昨天团队监控信息
     * @param tenantCode
     * @param teamCode
     * @return
     */
    TeamMonitor obtainYesterdayTeamMonitor(
            @Param("tenantCode")String tenantCode,
            @Param("teamCode")Integer teamCode);

    /**
     * 按天保存团队监控
     * @param record 监控记录
     * @return
     */
    int insertSelective(TeamMonitor record);

    /**
     * 增加排队数
     * @param tenantCode
     * @param teamCode
     * @param waitCount
     * @return
     */
    int updateWaitCount(
            @Param("tenantCode")String tenantCode,
            @Param("teamCode")Integer teamCode,
            @Param("waitCount")Integer waitCount);

}