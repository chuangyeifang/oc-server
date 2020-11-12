/**
 * 
 */
package com.oc.service.team;

import com.oc.domain.team.TeamSkill;
import com.oc.dto.team.TeamSkillDto;

import java.util.List;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月21日
 * @version v 1.0
 */
public interface TeamSkillService {
	
	TeamSkill obtainTeamSkill(String tenantCode, Integer skillCode);

	List<TeamSkillDto> obtainTeamSkills(String tenantCode, Integer teamCode);
}
