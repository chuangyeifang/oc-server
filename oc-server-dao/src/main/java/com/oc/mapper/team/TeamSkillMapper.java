package com.oc.mapper.team;

import org.apache.ibatis.annotations.Param;

import com.oc.domain.team.TeamSkill;

/**
 * @author chuangyeifang
 */
public interface TeamSkillMapper {

	/**
	 * 获取
	 * @param tenantCode
	 * @param skillCode
	 * @return
	 */
	TeamSkill obtainTeamSkill(
			@Param("tenantCode")String tenantCode,
			@Param("skillCode")Integer skillCode);
}