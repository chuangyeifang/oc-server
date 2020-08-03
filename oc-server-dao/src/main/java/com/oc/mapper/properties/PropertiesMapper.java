package com.oc.mapper.properties;

import org.apache.ibatis.annotations.Param;

import com.oc.domain.properties.Properties;

/**
 * @author chuangyeifang
 */
public interface PropertiesMapper {

    /**
     * 获取属性信息
     * @param tenantCode
     * @return
     */
    Properties obtainProperties(@Param("tenantCode")String tenantCode);
}