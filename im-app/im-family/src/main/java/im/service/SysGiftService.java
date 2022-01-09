package im.service;

import im.model.SysGiftModel;
import im.model.SysGiftQueryModel;
import im.model.base.PageModel;
import im.model.base.ResultData;

/**
 * @author hoomoomoo
 * @description 随礼信息服务类
 * @package com.hoomoomoo.im.service
 * @date 2019/09/07
 */

public interface SysGiftService {

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    ResultData selectInitData();

    /**
     * 分页查询随礼信息
     *
     * @param sysGiftQueryModel
     * @return
     */
    PageModel<SysGiftModel> selectPage(SysGiftQueryModel sysGiftQueryModel);

    /**
     * 删除随礼信息
     *
     * @param giftIds
     * @return
     */
    ResultData delete(String giftIds);

    /**
     * 查询随礼信息
     *
     * @param giftId
     * @return
     */
    ResultData selectOne(String giftId, Boolean isTranslate);

    /**
     * 保存随礼信息
     *
     * @param sysGiftModel
     * @return
     */
    ResultData save(SysGiftModel sysGiftModel);
}
