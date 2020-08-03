package com.oc.mapper.team;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oc.domain.team.Team;

/**
 * @author chuangyeifang
 */
public interface TeamMapper {

    /**
     * 获取团队列表
     * @param tenantCode
     * @return
     */
    List<Team> obtainTeams(@Param("tenantCode")String tenantCode);

    /**
     * 获取团队
     * @param teamCode
     * @return
     */
    Team obtainTeam(@Param("teamCode")Integer teamCode);
}