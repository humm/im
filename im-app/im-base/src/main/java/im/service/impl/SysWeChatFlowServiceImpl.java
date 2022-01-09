package im.service.impl;

import im.dao.SysWeChatFlowDao;
import im.model.*;
import im.service.SysWeChatFlowService;
import im.service.SysWeChatUserService;
import im.util.SysLogUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static im.config.RunDataConfig.*;
import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.*;
import static im.consts.DictionaryConst.D013;
import static im.consts.WeChatConst.*;

/**
 * @author hoomoomoo
 * @description 微信流程步骤服务体实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2020/02/29
 */

@Service
@Transactional
public class SysWeChatFlowServiceImpl implements SysWeChatFlowService {

    private static final Logger logger = LoggerFactory.getLogger(SysWeChatFlowServiceImpl.class);


    @Autowired
    private SysWeChatFlowDao sysWeChatFlowDao;

    @Autowired
    private SysWeChatUserService sysWeChatUserService;

    /**
     * 查询流程步骤
     *
     * @param sysWeChatFlowQueryModel
     * @return
     */
    @Override
    public List<SysWeChatFlowModel> selectFlowList(SysWeChatFlowQueryModel sysWeChatFlowQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_WECHAT_FLOW, LOG_OPERATE_TYPE_SELECT);
        SysLogUtils.parameter(logger, sysWeChatFlowQueryModel);
        List<SysWeChatFlowModel> sysWeChatFlowModelList = sysWeChatFlowDao.selectFlowList(sysWeChatFlowQueryModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_WECHAT_FLOW, LOG_OPERATE_TYPE_SELECT);
        return sysWeChatFlowModelList;
    }

    /**
     * 获取微信流程步骤
     *
     * @return
     */
    @Override
    public void getWeChatFlow(boolean isLoadFlowOperate) {
        ConcurrentHashMap<String, String> flowNumToCode = new ConcurrentHashMap<>(16);
        LinkedHashMap<String, SysWeChatFlowModel> mainFlow = new LinkedHashMap<>(16);
        StringBuffer mainFlowList = new StringBuffer(WECHAT_MAIN_FLOW).append(NEXT_LINE).append(NEXT_LINE);
        StringBuffer moreFlowList = new StringBuffer();
        StringBuffer flowTitle;
        SysWeChatFlowQueryModel sysWeChatFlowQueryModel = new SysWeChatFlowQueryModel();
        sysWeChatFlowQueryModel.setFlowType(STR_1 + MINUS + STR_2);
        List<SysWeChatFlowModel> sysWeChatFlowModelList = selectFlowList(sysWeChatFlowQueryModel);
        if (CollectionUtils.isNotEmpty(sysWeChatFlowModelList)) {
            for (SysWeChatFlowModel sysWeChatFlowModel : sysWeChatFlowModelList) {
                flowTitle = new StringBuffer(String.format(STR_NUMBER, sysWeChatFlowModel.getFlowNum())).append(sysWeChatFlowModel.getFlowDescribe()).append(NEXT_LINE);
                if (StringUtils.isNotBlank(sysWeChatFlowModel.getFlowCode())) {
                    mainFlow.put(sysWeChatFlowModel.getFlowCode(), sysWeChatFlowModel);
                    flowNumToCode.put(sysWeChatFlowModel.getFlowNum(), sysWeChatFlowModel.getFlowCode());
                } else {
                    mainFlow.put(sysWeChatFlowModel.getFlowNum(), sysWeChatFlowModel);
                }
                if (STR_1.equals(sysWeChatFlowModel.getFlowType())) {
                    mainFlowList.append(flowTitle);
                } else {
                    moreFlowList.append(flowTitle);
                }
            }
        }
        // 加载序号与代码对应关系
        WECHAT_FLOW_NUM_TO_CODE = flowNumToCode;
        // 加载所有服务信息
        WECHAT_FLOW_LIST = mainFlow;
        // 加载主菜单
        WECHAT_MAIN_FLOW_LIST = mainFlowList.toString();
        // 加载更多菜单
        WECHAT_MAIN_FLOW_MORE_LIST = moreFlowList.toString();
        // 加载关注用户操作模式
        if (isLoadFlowOperate) {
            WECHAT_FLOW_OPERATE.clear();
            List<SysWeChatUserModel> sysWeChatUserModelList = sysWeChatUserService.selectList(new SysWeChatUserQueryModel());
            if (CollectionUtils.isNotEmpty(sysWeChatFlowModelList)) {
                SysWeChatOperateModel sysWeChatOperateModel = new SysWeChatOperateModel();
                sysWeChatOperateModel.setOperateTime(System.currentTimeMillis());
                for (SysWeChatUserModel sysWeChatUserModel : sysWeChatUserModelList) {
                    String userKey = sysWeChatUserModel.getWeChatPublicId() + MINUS + sysWeChatUserModel.getWeChatUserId();
                    if (!(D013 + MINUS + STR_1).equals(sysWeChatUserModel.getIsAuth())) {
                        sysWeChatOperateModel.setOperateFlow(FLOW_CODE_AUTH);
                    } else if (StringUtils.isBlank(sysWeChatUserModel.getUserId())) {
                        sysWeChatOperateModel.setOperateFlow(FLOW_CODE_BIND);
                    } else {
                        sysWeChatOperateModel.setOperateFlow(FLOW_CODE_SELECT);
                    }
                    WECHAT_FLOW_OPERATE.put(userKey, sysWeChatOperateModel);
                }
            }
        }
    }
}
