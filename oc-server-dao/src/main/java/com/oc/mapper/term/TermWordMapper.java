package com.oc.mapper.term;

import com.oc.domain.term.TermWord;

/**
 * 常用话术
 * @author chuangyeifang
 */
public interface TermWordMapper {

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 新建
     * @param record
     * @return
     */
    int insertSelective(TermWord record);

    /**
     * 查询
     * @param id
     * @return
     */
    TermWord selectByPrimaryKey(Long id);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(TermWord record);
}