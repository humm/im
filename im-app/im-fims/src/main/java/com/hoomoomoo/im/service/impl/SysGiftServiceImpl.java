package com.hoomoomoo.im.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hoomoomoo.im.config.WebSocketServerConfig;
import com.hoomoomoo.im.dao.SysGiftDao;
import com.hoomoomoo.im.model.*;
import com.hoomoomoo.im.service.SysGiftService;
import com.hoomoomoo.im.dao.SysUserDao;
import com.hoomoomoo.im.model.base.PageModel;
import com.hoomoomoo.im.model.base.ResultData;
import com.hoomoomoo.im.model.base.ViewData;
import com.hoomoomoo.im.service.SysNoticeService;
import com.hoomoomoo.im.service.SysSystemService;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.util.SysCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseCueConst.*;
import static com.hoomoomoo.im.consts.BaseCueConst.UPDATE_SUCCESS;
import static com.hoomoomoo.im.consts.DictionaryConst.*;

/**
 * @author hoomoomoo
 * @description 随礼信息实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/09/07
 */

@Service
@Transactional
public class SysGiftServiceImpl implements SysGiftService {

    private static final Logger logger = LoggerFactory.getLogger(SysGiftServiceImpl.class);

    @Autowired
    private SysGiftDao sysGiftDao;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysNoticeService sysNoticeService;

    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    @Override
    public ResultData selectInitData() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_INIT);
        ViewData viewData = new ViewData();
        // 设置查询条件
        viewData.setViewType(BUSINESS_TYPE_GIFT);
        sysSystemService.setCondition(viewData);
        // 最近一次操作类型
        SysGiftQueryModel sysGiftQueryModel = new SysGiftQueryModel();
        sysGiftQueryModel.setGiftSender(sysSystemService.getUserId());
        LastType lastType = sysGiftDao.selectLastType(sysGiftQueryModel);
        if (lastType != null) {
            viewData.setLastType(lastType);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_INIT);
        return new ResultData(true, SELECT_SUCCESS, viewData);
    }

    /**
     * 分页查询随礼信息
     *
     * @param sysGiftQueryModel
     * @return
     */
    @Override
    public PageModel<SysGiftModel> selectPage(SysGiftQueryModel sysGiftQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_PAGE);
        SysCommonUtils.setSessionInfo(sysGiftQueryModel);
        SysLogUtils.parameter(logger, sysGiftQueryModel);
        PageHelper.startPage(sysGiftQueryModel.getPage(), sysGiftQueryModel.getLimit());
        List<SysGiftModel> sysGiftModelList = sysGiftDao.selectPage(sysGiftQueryModel);
        // 创建PageInfo对象前 不能处理数据否则getTotal数据不正确
        PageInfo<SysGiftModel> pageInfo = new PageInfo<>(sysGiftModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT_PAGE);
        return new PageModel(pageInfo.getTotal(), sysSystemService.transferData(pageInfo.getList(), SysGiftModel.class));
    }

    /**
     * 删除随礼信息
     *
     * @param giftIds
     * @return
     */
    @Override
    public ResultData delete(String giftIds) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_DELETE);
        List<SysGiftModel> list = new ArrayList<>();
        if (StringUtils.isNotBlank(giftIds)) {
            String[] giftId = giftIds.split(COMMA);
            for (String ele : giftId) {
                SysGiftQueryModel sysGiftQueryModel = new SysGiftQueryModel();
                sysGiftQueryModel.setGiftId(ele);
                SysGiftModel sysGiftModel = sysGiftDao.selectOne(sysGiftQueryModel);
                SysNoticeModel sysNoticeModel = setSysNoticeProperties(sysGiftModel);
                sysNoticeModel.setBusinessId(ele);
                sysNoticeService.update(sysNoticeModel);
                list.add(sysGiftModel);
            }
            sysGiftDao.delete(list);
        }
        SysLogUtils.parameter(logger, list);
        sendWebsocketInfo();
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_BUSINESS_TYPE_GIFT);
        return new ResultData(true, DELETE_SUCCESS, null);
    }

    /**
     * 查询随礼信息
     *
     * @param giftId
     * @param isTranslate
     * @return
     */
    @Override
    public ResultData selectOne(String giftId, Boolean isTranslate) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT);
        SysGiftQueryModel sysGiftQueryModel = new SysGiftQueryModel();
        sysGiftQueryModel.setGiftId(giftId);
        SysLogUtils.parameter(logger, sysGiftQueryModel);
        SysGiftModel sysGiftModel = sysGiftDao.selectOne(sysGiftQueryModel);
        if (isTranslate) {
            sysSystemService.transferData(sysGiftModel, SysGiftModel.class);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_GIFT, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysGiftModel);
    }

    /**
     * 保存随礼信息
     *
     * @param sysGiftModel
     * @return
     */
    @Override
    public ResultData save(SysGiftModel sysGiftModel) {
        String operateType = sysGiftModel.getGiftId() == null ? LOG_OPERATE_TYPE_ADD : LOG_OPERATE_TYPE_UPDATE;
        String tipMsg = sysGiftModel.getGiftId() == null ? ADD_SUCCESS : UPDATE_SUCCESS;
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_GIFT, operateType);
        SysCommonUtils.setCreateUserInfo(sysGiftModel);
        String msg = checkSenderAndReceiver(sysGiftModel);
        if (StringUtils.isNotBlank(msg)) {
            return new ResultData(false, msg, null);
        }
        SysNoticeModel sysNoticeModel = setSysNoticeProperties(sysGiftModel);
        if (sysGiftModel.getGiftId() == null || StringUtils.isNotEmpty(sysGiftModel.getDataType())) {
            // 新增
            String giftId = STR_EMPTY;
            if (StringUtils.isNotEmpty(sysGiftModel.getDataType())) {
                giftId = sysGiftModel.getGiftId();
            } else {
                giftId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_GIFT);
            }
            sysGiftModel.setGiftId(giftId);
            sysNoticeModel.setBusinessId(giftId);
        } else {
            // 修改
            sysNoticeModel.setBusinessId(sysGiftModel.getGiftId());
            sysNoticeService.update(sysNoticeModel);
        }
        sysNoticeModel.setNoticeId(sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_NOTICE));
        sysNoticeModel.setNoticeStatus(new StringBuffer(D007).append(MINUS).append(STR_1).toString());
        sysNoticeService.save(sysNoticeModel);
        SysLogUtils.parameter(logger, sysGiftModel);
        sysGiftDao.save(sysGiftModel);
        sendWebsocketInfo();
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_GIFT, operateType);
        return new ResultData(true, tipMsg, null);
    }

    /**
     * 设置消息通知属性
     *
     * @param sysGiftModel
     * @return
     */
    private SysNoticeModel setSysNoticeProperties(SysGiftModel sysGiftModel) {
        SysNoticeModel sysNoticeModel = new SysNoticeModel();
        SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
        sysUserQueryModel.setUserId(changeUserId(sysGiftModel.getGiftSender()));
        SysUserModel giftSender = sysUserDao.selectOne(sysUserQueryModel);
        if (giftSender == null) {
            sysNoticeModel.setUserId(changeUserId(sysGiftModel.getGiftReceiver()));
            sysNoticeModel.setBusinessType(new StringBuffer(D011).append(MINUS).append(STR_3).toString());
        } else {
            sysNoticeModel.setUserId(changeUserId(sysGiftModel.getGiftSender()));
            sysNoticeModel.setBusinessType(new StringBuffer(D011).append(MINUS).append(STR_2).toString());
        }
        sysNoticeModel.setBusinessSubType(sysGiftModel.getGiftType());
        sysNoticeModel.setBusinessDate(sysGiftModel.getGiftDate());
        sysNoticeModel.setBusinessAmount(sysGiftModel.getGiftAmount());
        if (StringUtils.isEmpty(sysGiftModel.getDataType())) {
            sysNoticeModel.setNoticeType(new StringBuffer(D008).append(MINUS).append(STR_1).toString());
        } else {
            sysNoticeModel.setNoticeType(sysGiftModel.getDataType());
        }
        sysNoticeModel.setReadStatus(new StringBuffer(D012).append(MINUS).append(STR_1).toString());
        return sysNoticeModel;
    }

    /**
     * 校验送礼人和收礼人不能同时为系统用户
     *
     * @param sysGiftModel
     * @return
     */
    private String checkSenderAndReceiver(SysGiftModel sysGiftModel) {
        SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
        sysUserQueryModel.setUserId(changeUserId(sysGiftModel.getGiftSender()));
        SysUserModel giftSender = sysUserDao.selectOne(sysUserQueryModel);
        sysUserQueryModel.setUserId(changeUserId(sysGiftModel.getGiftReceiver()));
        SysUserModel giftReceiver = sysUserDao.selectOne(sysUserQueryModel);
        if (giftSender == null && giftReceiver == null) {
            return GIFT_SENDER_RECEIVER_NOT_EXIST;
        }
        if (giftSender != null && giftReceiver != null) {
            return GIFT_SENDER_RECEIVER_EXIST;
        }
        return null;
    }

    /**
     * 用户ID转换
     *
     * @param userId
     * @return
     */
    private String changeUserId(String userId) {
        if (userId != null && userId.contains(MINUS)) {
            if (userId.split(MINUS).length == 2) {
                return userId.split(MINUS)[1];
            } else {
                return userId.split(MINUS)[0];
            }
        }
        return userId;
    }

    /**
     * 发送websocket消息
     */
    private void sendWebsocketInfo() {
        WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_CONSOLE, LOG_BUSINESS_TYPE_GIFT);
        WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_NOTICE, LOG_BUSINESS_TYPE_GIFT);
    }
}
