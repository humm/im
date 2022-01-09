package im.service;

import im.model.SysNoticeModel;
import im.model.SysNoticeQueryModel;
import im.model.base.PageModel;
import im.model.base.ResultData;

/**
 * @author hoomoomoo
 * @description 消息通知服务类
 * @package im.service
 * @date 2019/09/06
 */

public interface SysNoticeService {

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
     * 查询页面初始化相关数据
     *
     * @return
     */
    ResultData selectInitData();

    /**
     * 分页查询消息通知信息
     *
     * @param sysNoticeQueryModel
     * @return
     */
    PageModel<SysNoticeModel> selectPage(SysNoticeQueryModel sysNoticeQueryModel);


    /**
     * 查询消息通知信息
     *
     * @param noticeId
     * @return
     */
    ResultData selectOne(String noticeId, Boolean isTranslate);

    /**
     * 修改消息通知阅读信息
     *
     * @param noticeIds
     * @param isAll
     * @return
     */
    ResultData updateReadStatus(String isAll, String noticeIds);

}
