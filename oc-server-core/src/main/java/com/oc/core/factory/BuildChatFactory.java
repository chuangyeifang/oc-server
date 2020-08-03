package com.oc.core.factory;

import com.oc.domain.BuildChat;
import com.oc.domain.team.Team;
import com.oc.provider.cache.LocalTeamStore;
import com.oc.session.Customer;
import com.oc.session.CustomerSession;
import com.oc.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author chuangyeifang
 */
@Slf4j
public class BuildChatFactory {

    /**
     * 构建会话信息
     * @param customerSession
     * @return
     */
    public static String createBuildChatToJson(CustomerSession customerSession) {
        BuildChat buildChat = createBuildChat(customerSession);
        return buildCharToJson(buildChat);
    }

    /**
     * 构建会话信息
     * @param customerSession
     * @return
     */
    public static String createBuildChatToJson(CustomerSession customerSession, String reason) {
        BuildChat buildChat = createBuildChat(customerSession);
        buildChat.setReason(reason);
        return buildCharToJson(buildChat);
    }

    private static String buildCharToJson(BuildChat buildChat) {
        String content = null;
        try {
            content = JsonUtils.getJson().writeString(buildChat);
        } catch (IOException e) {
            log.error("序列化失败 BuildChat: {}", buildChat);
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 构建会话信息
     * @param customerSession
     * @return
     */
    private static BuildChat createBuildChat(CustomerSession customerSession) {
        Customer customer = customerSession.getCustomer();
        Team team = LocalTeamStore.getInst().getTeam(customer.getTeamCode());
        return new BuildChat(customer.getTenantCode(), customer.getTeamCode(),
                team.getBriefName(),customer.getSkillCode(), customer.getSkillName(),
                customer.getGoodsCode(), customer.isLogin(), customer.getDevice());
    }
}
