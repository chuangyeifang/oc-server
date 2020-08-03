package com.oc.mapper.skill;

import com.oc.domain.skill.SkillBusiness;
import org.apache.ibatis.annotations.Param;

/**
 * @author chuangyeifang
 */
public interface SkillBusinessMapper {
    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 保存
     * @param record
     * @return
     */
    int insertSelective(SkillBusiness record);

    /**
     * 获取技能与业务 关系
     * @param id
     * @return
     */
    SkillBusiness selectByPrimaryKey(Long id);

    /**
     * 获取技能与业务 关系
     * @param tenantCode
     * @param businessCode
     * @return
     */
    SkillBusiness obtainSkillBusiness(
            @Param("tenantCode") String tenantCode,
            @Param("businessCode") String businessCode);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(SkillBusiness record);
}