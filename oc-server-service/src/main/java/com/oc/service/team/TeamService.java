/**
 * 
 */
package com.oc.service.team;

import java.util.List;

import com.oc.domain.team.Team;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年3月5日
 * @version v 1.0
 */
public interface TeamService {
	
	List<Team> obtainTeams(String tenantCode);
	
	Team obtainTeam(Integer teamCode);

}
