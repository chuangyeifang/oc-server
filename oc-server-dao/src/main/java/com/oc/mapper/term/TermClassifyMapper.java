package com.oc.mapper.term;

import com.oc.domain.term.TermClassify;

/**
 * @author chuangyeifang
 */
public interface TermClassifyMapper {

    /**
     * 删除分类
     * @param classifyId
     * @return
     */
    int deleteByPrimaryKey(Long classifyId);

    /**
     * 新建分类
     * @param record
     * @return
     */
    int insertSelective(TermClassify record);

    /**
     * 根据 id 查询分类
     * @param classifyId
     * @return
     */
    TermClassify selectByPrimaryKey(Long classifyId);

    /**
     * 更新 分类
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(TermClassify record);
}