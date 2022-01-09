package im.service;

import im.model.SysWeChatFlowModel;
import im.model.SysWeChatFlowQueryModel;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 微信流程步骤服务体类
 * @package com.hoomoomoo.im.service
 * @date 2020/02/29
 */
public interface SysWeChatFlowService {

    /**
     * 查询流程步骤
     *
     * @param sysWeChatFlowQueryModel
     * @return
     */
    List<SysWeChatFlowModel> selectFlowList(SysWeChatFlowQueryModel sysWeChatFlowQueryModel);

    /**
     * 获取微信流程步骤
     *
     *
     * @return
     */
    void getWeChatFlow(boolean isLoadFlowOperate);
}
