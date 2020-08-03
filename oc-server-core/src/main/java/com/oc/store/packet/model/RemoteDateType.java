package com.oc.store.packet.model;

/**
 * 存储消息类型
 * @author chuangyeifang
 */
public enum RemoteDateType {
    /**
     * 正常消息
     */
    NORMAL,
    /**
     * 离线消息
     */
    OFFLINE,
    /**
     * 撤回消息
     */
    REVOCATION
}
