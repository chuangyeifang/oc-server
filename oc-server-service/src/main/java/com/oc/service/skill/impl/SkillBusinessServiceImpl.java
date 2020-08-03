/**
 * 
 */
package com.oc.service.skill.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oc.domain.skill.SkillBusiness;
import com.oc.mapper.skill.SkillBusinessMapper;
import com.oc.service.skill.SkillBusinessService;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月8日
 * @version v 1.0
 */
@Service
public class SkillBusinessServiceImpl implements SkillBusinessService{

	@Autowired
	private SkillBusinessMapper skillBusinessMapper;
	
	@Override
	public SkillBusiness obtainSkillBusiness(String tenantCode, String businessCode) {
		return skillBusinessMapper.obtainSkillBusiness(tenantCode, businessCode);
	}

}
