package com.oc.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class CustomerInfo {

    /**
     * 访客名称
     */
    private String cn;
    /**
     * 访客编码
     */
    private String cc;
    /**
     * 租户编码
     */
    private String ttc;
    /**
     * 团队
     */
    private Integer tmc;
    /**
     * 团队简称
     */
    private String tmb;
    /**
     * 技能
     */
    private Integer skc;
    /**
     * 技能
     */
    private String skn;
    /**
     * 业务编码
     */
    private String buc;
    /**
     * 设备
     * 1 pc
     * 2 wap
     * 3 app
     * 4 web chat
     * 5 其他
     */
    private String device;

    private String gc;
    /**
     * 是否登录（0:未登录，1:登录）
     */
    private String real;

    public String getBuc() {
        return null == buc ? "" : buc;
    }

    public String getGc() {
        return null == gc ? "" : gc;
    }
}
