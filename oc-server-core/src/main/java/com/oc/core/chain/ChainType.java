package com.oc.core.chain;

/**
 * 消息链类型
 * @author chuangyeifang
 */

public enum ChainType {
    // 客服开始服务
    BIND,
    // 客服或者客户 结束会话
    FINISHED_CHAT,
    // 关闭重复连接
    CLOSE_REPEAT_CONNECTION,
    // 手动直接接入排队客户
    DIRECT_RECEPTION,
    // 客服和客户 一般通讯消息
    GENERAL_TRANSPORT,
    // 撤回消息
    REVOCATION_PACKET,
    // 转接客户
    TRANSFER_CUSTOMER,
    // 更改客服服务状态
    CHANGE_WAITER_STATUS;
}
