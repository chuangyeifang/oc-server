/**
 * 
 */
package com.oc.service.properties;

import com.oc.domain.properties.Properties;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月4日
 * @version v 1.0
 */
public interface PropertiesService {

	/**
	 * 获取配置属性
	 * @param tenantCode
	 * @return
	 */
	Properties obtainProperties(String tenantCode);
}
