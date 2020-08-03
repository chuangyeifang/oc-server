/**
 * 
 */
package com.oc.service.skill;

import com.oc.domain.skill.SkillBusiness;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月8日
 * @version v 1.0
 */
public interface SkillBusinessService {

	/**
	 * 根据buc org 获取
	 * @param tenantCode
	 * @param businessCode
	 * @return
	 */
	SkillBusiness obtainSkillBusiness(String tenantCode, String businessCode);
}
