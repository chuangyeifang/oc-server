/**
 * 
 */
package com.oc.service.team;

import com.oc.domain.team.TeamSkill;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月21日
 * @version v 1.0
 */
public interface TeamSkillService {
	
	TeamSkill obtainTeamSkill(String tenantCode, Integer skillCode);
}
