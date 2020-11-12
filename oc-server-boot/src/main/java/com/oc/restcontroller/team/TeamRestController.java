package com.oc.restcontroller.team;

import com.oc.auth.UserStore;
import com.oc.auth.WaiterInfo;
import com.oc.dto.team.TeamSkillDto;
import com.oc.restcontroller.AbstractBasicRestController;
import com.oc.service.team.TeamSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chuangyeifang
 */
@RestController
@RequestMapping("team")
public class TeamRestController extends AbstractBasicRestController {

    @Autowired
    private TeamSkillService teamSkillService;

    @RequestMapping("skills")
    public Object getTeamSkills(Integer teamCode) {
        WaiterInfo userInfo = UserStore.get();
        String tenantCode = userInfo.getTenantCode();
        if (teamCode == null) {
            return failed(1, "参数错误");
        }
        List<TeamSkillDto> skills = teamSkillService.obtainTeamSkills(tenantCode, teamCode);
        return success(skills);
    }
}
