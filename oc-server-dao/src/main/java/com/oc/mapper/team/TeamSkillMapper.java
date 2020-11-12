package com.oc.mapper.team;

import com.oc.dto.team.TeamSkillDto;
import org.apache.ibatis.annotations.Param;

import com.oc.domain.team.TeamSkill;

import java.util.List;

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

    List<TeamSkillDto> obtainTeamSkills(
    		@Param("tenantCode")String tenantCode,
			@Param("teamCode")Integer teamCode);
}