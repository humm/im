package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysGiftModel;
import com.hoomoomoo.im.model.SysGiftQueryModel;
import com.hoomoomoo.im.model.common.LastType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 随礼信息dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/09/07
 */

@Mapper
public interface SysGiftDao {

    /**
     * 分页查询收入信息
     *
     * @param sysGiftQueryModel
     * @return
     */
    List<SysGiftModel> selectPage(SysGiftQueryModel sysGiftQueryModel);

    /**
     * 删除收入信息
     *
     * @param sysGiftModelList
     * @return
     */
    void delete(List<SysGiftModel> sysGiftModelList);

    /**
     * 查询收入信息
     *
     * @param sysGiftQueryModel
     * @return
     */
    SysGiftModel selectOne(SysGiftQueryModel sysGiftQueryModel);

    /**
     * 查询最后一次随礼类型
     *
     * @param sysGiftQueryModel
     * @return
     */
    LastType selectLastType(SysGiftQueryModel sysGiftQueryModel);

    /**
     * 保存收入信息
     *
     * @param sysGiftModel
     */
    void save(SysGiftModel sysGiftModel);

    /**
     * 自由查询
     *
     * @return
     */
    List<SysGiftModel> selectFreeInfo(SysGiftQueryModel sysGiftQueryModel);

}
