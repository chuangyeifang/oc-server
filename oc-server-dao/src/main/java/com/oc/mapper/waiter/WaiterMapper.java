package com.oc.mapper.waiter;

import com.oc.domain.waiter.Waiter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客服基础操作
 * @author chuangyeifang
 */
@Mapper
public interface WaiterMapper {

    /**
     * 客服登录
     * @param tenantCode
     * @param waiterName
     * @param password
     * @return
     */
    Waiter login(@Param("tenantCode") String tenantCode,
                 @Param("waiterName") String waiterName,
                 @Param("password") String password);

    /**
     * 获取客服
     * @param tenantCode
     * @param waiterName
     * @return
     */
    Waiter obtainWaiter(@Param("tenantCode")String tenantCode,
                        @Param("waiterName")String waiterName);

    /**
     * 根据id 更新 接待数
     * @param id
     * @param reception
     * @return
     */
    int updateCurReception(@Param("id")Long id,
                           @Param("reception")Integer reception);

    /**
     * 根据客服工号更新接待数
     * @param waiterCode
     * @param reception
     * @return
     */
    int updateCurReceptionByCode(@Param("waiterCode") String waiterCode,
                                 @Param("reception")Integer reception);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(@Param("id")Long id,
                     @Param("status")String status);

    /**
     * 根据账号获取客服
     * @param waiterCode
     * @return
     */
    Waiter obtainWaiterByCode(@Param("waiterCode")String waiterCode);

    /**
     * 客服登出
     * @param id
     * @return
     */
	int waiterLogout(@Param("id")Long id);

    /**
     * 客服退出登录
     * @param waiterCode
     */
    void logout(@Param("waiterCode") String waiterCode);
}