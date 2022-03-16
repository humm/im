package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.SysDictionaryModel;
import com.hoomoomoo.im.model.SysDictionaryQueryModel;
import com.hoomoomoo.im.model.base.PageModel;
import com.hoomoomoo.im.model.base.ResultData;

import java.util.List;

/**
 * @author hoomoomoo
 * @description  字典信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/08/11
 */

public interface SysDictionaryService {

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    ResultData selectInitData();

    /**
     * 分页查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    PageModel<SysDictionaryModel> selectPage(SysDictionaryQueryModel sysDictionaryQueryModel);

    /**
     * 查询字典信息
     *
     * @param sysDictionaryQueryModel
     * @return
     */
    List<SysDictionaryModel> selectSysDictionary(SysDictionaryQueryModel sysDictionaryQueryModel);

    /**
     * 查询字典信息
     *
     * @param dictionaryCode
     * @return
     */
    ResultData selectOne(String dictionaryCode, Boolean isTranslate);

    /**
     * 保存字典信息
     *
     * @param sysDictionaryModelList
     * @return
     */
    ResultData save(List<SysDictionaryModel> sysDictionaryModelList);

    /**
     * 刷新字典信息
     *
     * @return
     */
    ResultData refresh();

}
