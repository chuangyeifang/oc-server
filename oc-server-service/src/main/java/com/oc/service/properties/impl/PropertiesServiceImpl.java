/**
 * 
 */
package com.oc.service.properties.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.properties.Properties;
import com.oc.mapper.properties.PropertiesMapper;
import com.oc.service.properties.PropertiesService;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月4日
 * @version v 1.0
 */
@Service
public class PropertiesServiceImpl implements PropertiesService {

	@Autowired
	private PropertiesMapper propertiesMapper;
	
	/**
	 * @param tenantCode
	 * @return
	 */
	@Override
	public Properties obtainProperties(String tenantCode) {
		return propertiesMapper.obtainProperties(tenantCode);
	}

}
