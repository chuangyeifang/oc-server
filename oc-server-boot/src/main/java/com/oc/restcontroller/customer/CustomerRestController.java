package com.oc.restcontroller.customer;

import com.oc.auth.CustomerAuthCoder;
import com.oc.auth.CustomerInfo;
import com.oc.common.utils.DateUtils;
import com.oc.common.utils.UUIDUtils;
import com.oc.domain.skill.SkillBusiness;
import com.oc.domain.team.Team;
import com.oc.domain.team.TeamSkill;
import com.oc.provider.cache.LocalSkillBusinessStore;
import com.oc.provider.cache.LocalTeamSkillStore;
import com.oc.provider.cache.LocalTeamStore;
import com.oc.restcontroller.AbstractBasicRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuangyeifang
 */
@RestController
@RequestMapping(value = "customer")
@Slf4j
public class CustomerRestController extends AbstractBasicRestController {

    @RequestMapping(value = "auth", method = {RequestMethod.GET, RequestMethod.POST})
    public Object auth(CustomerInfo customerInfo) {
        String ttc = customerInfo.getTtc();
        Integer skc = customerInfo.getSkc();
        String buc = customerInfo.getBuc();
        if (existEmpty(ttc)) {
            return failed(1, "请求参数错误");
        }
        if (!existEmpty(buc)) {
            SkillBusiness skillBusiness = LocalSkillBusinessStore.getInst().get(ttc, buc);
            skc = skillBusiness.getSkillCode();
        }
        if (null == skc) {
            return failed(2, "请求参数错误");
        }
        TeamSkill teamSkill = LocalTeamSkillStore.getInst().get(ttc, skc);
        if (null == teamSkill) {
            return failed(3, "无法匹配的路由信息");
        }

        Team team = LocalTeamStore.getInst().getTeam(teamSkill.getTeamCode());
        customerInfo.setTmc(team.getTeamCode());
        customerInfo.setTmb(team.getBriefName());
        customerInfo.setSkc(teamSkill.getSkillCode());
        customerInfo.setSkn(teamSkill.getSkillName());

        String cc = UUIDUtils.getID(8);
        customerInfo.setCc(cc);
        customerInfo.setCn(cc);
        customerInfo.setReal("0");

        boolean isWorkingTime = DateUtils.isTimeInZone(team.getBeginTime(), team.getEndTime());
        if (!isWorkingTime) {
          String offlineMsg = team.getOfflineMessage()
                    .replace("{0}", team.getBeginTime())
                    .replace("{1}", team.getEndTime());
            return failed(4, offlineMsg);
        }

        setCookies(CustomerAuthCoder.CUSTOMER_COOKIE_NAME, CustomerAuthCoder.encode(customerInfo));
        return success(customerInfo);
    }
}
