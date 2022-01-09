package im.dao;

import im.model.SysNoticeModel;
import im.model.SysNoticeQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 消息通知dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/09/06
 */

@Mapper
public interface SysNoticeDao {

    /**
     * 保存消息通知
     *
     * @param sysNoticeModel
     */
    void save(SysNoticeModel sysNoticeModel);

    /**
     * 更新消息通知
     *
     * @param sysNoticeModel
     */
    void update(SysNoticeModel sysNoticeModel);

    /**
     * 批量更新消息通知
     *
     * @param sysNoticeModel
     */
    void updateBatch(SysNoticeModel sysNoticeModel);

    /**
     * 分页查询消息通知信息
     *
     * @param sysNoticeQueryModel
     * @return
     */
    List<SysNoticeModel> selectPage(SysNoticeQueryModel sysNoticeQueryModel);

    /**
     * 查询消息通知信息
     *
     * @param sysNoticeQueryModel
     * @return
     */
    SysNoticeModel selectOne(SysNoticeQueryModel sysNoticeQueryModel);
}
