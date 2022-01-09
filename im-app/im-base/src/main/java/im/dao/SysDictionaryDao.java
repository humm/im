package im.dao;

import im.model.SysDictionaryModel;
import im.model.SysDictionaryQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 字典信息Dto
 * @package com.hoomoomoo.im.dao
 * @date 2019/08/11
 */

@Mapper
public interface SysDictionaryDao {

    /**
     * 分页查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    List<SysDictionaryModel> selectPage(SysDictionaryQueryModel sysDictionaryQueryModel);

    /**
     * 查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    List<SysDictionaryModel> selectSysDictionary(SysDictionaryQueryModel sysDictionaryQueryModel);

    /**
     * 查询所有字典查询条件
     *
     * @return
     */
    List<SysDictionaryModel> selectSysDictionaryCondition();

    /**
     * 删除字典信息
     *
     * @param sysDictionaryModel
     */
    void delete(SysDictionaryModel sysDictionaryModel);

    /**
     * 修改字典信息
     *
     * @param sysDictionaryModel
     */
    void update(SysDictionaryModel sysDictionaryModel);

    /**
     * 保存字典信息
     *
     * @param sysDictionaryModel
     */
    void save(SysDictionaryModel sysDictionaryModel);

}
