package com.oc.mapper.waiter;

import com.oc.domain.waiter.WaiterMonitor;
import org.apache.ibatis.annotations.Param;

/**
 * @author chuangyeifang
 */
public interface WaiterMonitorMapper {

    /**
     * 获取昨天客服建东状态
     * @param teamCode
     * @param waiterCode
     * @return
     */
    WaiterMonitor obtainYesterdayWaiterMonitor(
            @Param("teamCode")Integer teamCode,
            @Param("waiterCode")String waiterCode);

    /**
     * 保存客服监控信息
     * @param record
     * @return
     */
    int insertSelective(WaiterMonitor record);

    /**
     * 按客服工号更新客服监控信息
     * @param teamCode
     * @param waiterCode
     * @param status
     * @return
     */
    int updateStatus(
            @Param("teamCode")Integer teamCode,
            @Param("waiterCode")String waiterCode,
            @Param("status")String status);

    /**
     * 统计客服接待数
     * @param teamCode
     * @param waiterCode
     * @return
     */
    int incrReception(
            @Param("teamCode")Integer teamCode,
            @Param("waiterCode")String waiterCode);

    /**
     * 更新时间
     * @param teamCode
     * @param waiterCode
     * @param beforeStatus
     * @param afterStatus
     * @return
     */
    int updateTime(
            @Param("teamCode") Integer teamCode,
            @Param("waiterCode") String waiterCode,
            @Param("beforeStatus") String beforeStatus,
            @Param("afterStatus") String afterStatus);

    /**
     * 修复时间
     * @param teamCode
     * @param waiterCode
     * @param status
     * @return
     */
    int repairCurDateMonitor(
            @Param("teamCode") Integer teamCode,
            @Param("waiterCode") String waiterCode,
            @Param("status") String status);

    /**
     * 修复昨天数据
     * @param teamCode
     * @param waiterCode
     * @param status
     * @return
     */
    int repairYesterdayMonitor(
            @Param("teamCode") Integer teamCode,
            @Param("waiterCode") String waiterCode,
            @Param("status") String status);

    /**
     * 更新系统判定客服忙碌时间
     * @param teamCode
     * @param waiterCode
     * @param time
     * @return
     */
    int updateSysBusyTime(
            @Param("teamCode") Integer teamCode,
            @Param("waiterCode") String waiterCode,
            @Param("time") Long time);

    /**
     * 获取当日客服监控信息
     * @param waiterCode
     * @return
     */
    WaiterMonitor obtainWaiterMonitorOfDay(String waiterCode);
}