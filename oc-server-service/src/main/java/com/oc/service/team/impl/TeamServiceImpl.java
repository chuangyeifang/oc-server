/**
 * 
 */
package com.oc.service.team.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.team.Team;
import com.oc.mapper.team.TeamMapper;
import com.oc.service.team.TeamService;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年3月5日
 * @version v 1.0
 */
@Service
public class TeamServiceImpl implements TeamService{

	@Autowired
	private TeamMapper teamMapper;
	
	@Override
	public List<Team> obtainTeams(String tenantCode) {
		return teamMapper.obtainTeams(tenantCode);
	}

	@Override
	public Team obtainTeam(Integer teamCode) {
		return teamMapper.obtainTeam(teamCode);
	}

}
