package com.hoomoomoo.im.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hoomoomoo.im.dao.SysLoginLogDao;
import com.hoomoomoo.im.model.SysLoginLogModel;
import com.hoomoomoo.im.model.SysLoginLogQueryModel;
import com.hoomoomoo.im.model.PageModel;
import com.hoomoomoo.im.model.ResultData;
import com.hoomoomoo.im.model.common.ViewData;
import com.hoomoomoo.im.service.SysLoginLogService;
import com.hoomoomoo.im.service.SysSystemService;
import com.hoomoomoo.im.util.SysLogUtils;
import com.hoomoomoo.im.util.SysCommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hoomoomoo.im.consts.BusinessConst.BUSINESS_TYPE_LOGIN_LOG;
import static com.hoomoomoo.im.consts.BusinessCueConst.SELECT_SUCCESS;
import static com.hoomoomoo.im.consts.BusinessCueConst.*;

/**
 * @author hoomoomoo
 * @description
 * @package com.hoomoomoo.im.service.impl
 * @date 2019/10/22
 */

@Service
@Transactional
public class SysLoginLogServiceImpl implements SysLoginLogService {

    private static final Logger logger = LoggerFactory.getLogger(SysLoginLogServiceImpl.class);


    @Autowired
    private SysLoginLogDao sysLoginLogDao;

    @Autowired
    private SysSystemService sysSystemService;
    /**
     * 保存登录日志信息
     *
     * @param sysLoginLogModel
     */
    @Override
    public void save(SysLoginLogModel sysLoginLogModel) {
        sysLoginLogDao.save(sysLoginLogModel);
    }

    /**
     * 更新登录日志信息
     *
     * @param sysLoginLogModel
     */
    @Override
    public void update(SysLoginLogModel sysLoginLogModel) {
        sysLoginLogDao.update(sysLoginLogModel);
    }

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    @Override
    public ResultData selectInitData() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_INIT);
        ViewData viewData = new ViewData();
        // 设置查询条件
        viewData.setViewType(BUSINESS_TYPE_LOGIN_LOG);
        sysSystemService.setCondition(viewData);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_INIT);
        return new ResultData(true, SELECT_SUCCESS, viewData);
    }

    /**
     * 分页查询登入日志信息
     *
     * @param sysLoginLogQueryModel
     * @return
     */
    @Override
    public PageModel<SysLoginLogModel> selectPage(SysLoginLogQueryModel sysLoginLogQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_PAGE);
        SysCommonUtils.setSessionInfo(sysLoginLogQueryModel);
        SysLogUtils.parameter(logger, sysLoginLogQueryModel);
        PageHelper.startPage(sysLoginLogQueryModel.getPage(), sysLoginLogQueryModel.getLimit());
        List<SysLoginLogModel> sysLoginLogModelList = sysLoginLogDao.selectPage(sysLoginLogQueryModel);
        // 创建PageInfo对象前 不能处理数据否则getTotal数据不正确
        PageInfo<SysLoginLogModel> pageInfo = new PageInfo<>(sysLoginLogModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT_PAGE);
        return new PageModel(pageInfo.getTotal(), sysSystemService.transferData(pageInfo.getList(), SysLoginLogModel.class));
    }

    /**
     * 查询登入日志信息
     *
     * @param logId
     * @param isTranslate
     * @return
     */
    @Override
    public ResultData selectOne(String logId, Boolean isTranslate) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT);
        SysLoginLogQueryModel sysLoginLogQueryModel = new SysLoginLogQueryModel();
        sysLoginLogQueryModel.setLogId(logId);
        SysLogUtils.parameter(logger, sysLoginLogQueryModel);
        SysLoginLogModel sysLoginLogModel = sysLoginLogDao.selectOne(sysLoginLogQueryModel);
        if (isTranslate) {
            sysSystemService.transferData(sysLoginLogModel, SysLoginLogModel.class);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_LOGIN_LOG, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysLoginLogModel);
    }
}
