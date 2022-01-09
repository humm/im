package im.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import im.config.WebSocketServerConfig;
import im.dao.SysNoticeDao;
import im.model.SysNoticeModel;
import im.model.SysNoticeQueryModel;
import im.model.base.PageModel;
import im.model.base.ResultData;
import im.model.base.SessionBean;
import im.model.base.ViewData;
import im.service.SysNoticeService;
import im.service.SysSystemService;
import im.util.SysLogUtils;
import im.util.SysSessionUtils;
import im.util.SysCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.OPERATE_SUCCESS;
import static im.consts.BaseCueConst.SELECT_SUCCESS;
import static im.consts.DictionaryConst.D007;
import static im.consts.DictionaryConst.D012;
import static im.consts.BaseCueConst.*;

/**
 * @author hoomoomoo
 * @description 消息通知服务实现类
 * @package im.service.impl
 * @date 2019/09/06
 */

@Service
@Transactional
public class SysNoticeServiceImpl implements SysNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(SysNoticeServiceImpl.class);

    @Autowired
    private SysNoticeDao sysNoticeDao;

    @Autowired
    private SysSystemService sysSystemService;

    /**
     * 保存消息通知
     *
     * @param sysNoticeModel
     */
    @Override
    public void save(SysNoticeModel sysNoticeModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_ADD);
        SysCommonUtils.setCreateUserInfo(sysNoticeModel);
        if (StringUtils.isBlank(sysNoticeModel.getNoticeStatus())) {
            sysNoticeModel.setNoticeStatus(new StringBuffer(D007).append(MINUS).append(STR_1).toString());
        }
        SysLogUtils.parameter(logger, sysNoticeModel);
        sysNoticeDao.save(sysNoticeModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_ADD);

    }

    /**
     * 更新消息通知
     *
     * @param sysNoticeModel
     */
    @Override
    public void update(SysNoticeModel sysNoticeModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_UPDATE);
        SysCommonUtils.setCreateUserInfo(sysNoticeModel);
        if (StringUtils.isBlank(sysNoticeModel.getNoticeStatus())) {
            sysNoticeModel.setNoticeStatus(new StringBuffer(D007).append(MINUS).append(STR_2).toString());
        }
        SysLogUtils.parameter(logger, sysNoticeModel);
        sysNoticeDao.update(sysNoticeModel);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_UPDATE);
    }

    /**
     * 查询页面初始化相关数据
     *
     * @return
     */
    @Override
    public ResultData selectInitData() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_INIT);
        ViewData viewData = new ViewData();
        // 设置查询条件
        viewData.setViewType(BUSINESS_TYPE_NOTICE);
        sysSystemService.setCondition(viewData);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_INIT);
        return new ResultData(true, SELECT_SUCCESS, viewData);
    }

    /**
     * 分页查询消息通知信息
     *
     * @param sysNoticeQueryModel
     * @return
     */
    @Override
    public PageModel<SysNoticeModel> selectPage(SysNoticeQueryModel sysNoticeQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_PAGE);
        SysCommonUtils.setSessionInfo(sysNoticeQueryModel);
        SysLogUtils.parameter(logger, sysNoticeQueryModel);
        PageHelper.startPage(sysNoticeQueryModel.getPage(), sysNoticeQueryModel.getLimit());
        List<SysNoticeModel> sysNoticeModelList = sysNoticeDao.selectPage(sysNoticeQueryModel);
        // 创建PageInfo对象前 不能处理数据否则getTotal数据不正确
        PageInfo<SysNoticeModel> pageInfo = new PageInfo<>(sysNoticeModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT_PAGE);
        return new PageModel(pageInfo.getTotal(), sysSystemService.transferData(pageInfo.getList(), SysNoticeModel.class));
    }

    /**
     * 查询消息通知信息
     *
     * @param noticeId
     * @param isTranslate
     * @return
     */
    @Override
    public ResultData selectOne(String noticeId, Boolean isTranslate) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT);
        SysNoticeQueryModel sysNoticeQueryModel = new SysNoticeQueryModel();
        sysNoticeQueryModel.setNoticeId(noticeId);
        SysLogUtils.parameter(logger, sysNoticeQueryModel);
        SysNoticeModel sysNoticeModel = sysNoticeDao.selectOne(sysNoticeQueryModel);
        SysNoticeModel sysNotice = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeModel, sysNotice);
        if (isTranslate) {
            sysSystemService.transferData(sysNoticeModel, SysNoticeModel.class);
        }
        sysNotice.setReadStatus(new StringBuffer(D012).append(MINUS).append(STR_2).toString());
        SysCommonUtils.setModifyUserInfo(sysNoticeModel);
        SysCommonUtils.setModifyUserInfo(sysNotice);
        // 修改阅读状态
        sysNoticeDao.update(sysNotice);
        WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_CONSOLE, LOG_BUSINESS_TYPE_NOTICE);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysNoticeModel);
    }

    /**
     * 修改消息通知阅读信息
     *
     * @param isAll
     * @param noticeIds
     * @return
     */
    @Override
    public ResultData updateReadStatus(String isAll, String noticeIds) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_UPDATE);
        SysNoticeModel sysNoticeModel = new SysNoticeModel();
        sysNoticeModel.setReadStatus(new StringBuffer(D012).append(MINUS).append(STR_2).toString());
        SysCommonUtils.setModifyUserInfo(sysNoticeModel);
        if (STR_1.equals(isAll)){
            // 更新全部信息为已读
            SessionBean sessionBean = SysSessionUtils.getSession();
            if(sessionBean != null){
                if(!ADMIN_CODE.equals(sessionBean.getUserCode())){
                    sysNoticeModel.setUserId(sessionBean.getUserId());
                }
                sysNoticeDao.updateBatch(sysNoticeModel);
                SysLogUtils.parameter(logger, sysNoticeModel);
            }
        } else {
            if(StringUtils.isNotBlank(noticeIds)){
                String[] noticeId = noticeIds.split(COMMA);
                for (String id : noticeId){
                    sysNoticeModel.setNoticeId(id);
                    sysNoticeDao.update(sysNoticeModel);
                    SysLogUtils.parameter(logger, sysNoticeModel);
                }
            }
        }
        WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_CONSOLE, LOG_BUSINESS_TYPE_NOTICE);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_NOTICE, LOG_OPERATE_TYPE_UPDATE);
        return new ResultData(true, OPERATE_SUCCESS, null);
    }

}
