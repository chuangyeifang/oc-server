/**
 * 
 */
package com.oc.service.team.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.team.TeamSkill;
import com.oc.mapper.team.TeamSkillMapper;
import com.oc.service.team.TeamSkillService;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月21日
 * @version v 1.0
 */
@Service
public class TeamSkillServiceImpl implements TeamSkillService{

	@Autowired
	private TeamSkillMapper teamSkillMapper;
	
	@Override
	public TeamSkill obtainTeamSkill(String tenantCode, Integer skillCode) {
		return teamSkillMapper.obtainTeamSkill(tenantCode, skillCode);
	}
}
