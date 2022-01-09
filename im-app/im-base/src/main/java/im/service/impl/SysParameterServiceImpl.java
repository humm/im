package im.service.impl;

import im.config.WebSocketServerConfig;
import im.dao.SysDictionaryDao;
import im.dao.SysParameterDao;
import im.model.*;
import im.model.base.ResultData;
import im.service.SysParameterService;
import im.util.SysBeanUtils;
import im.util.SysLogUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static im.consts.BaseConst.*;
import static im.consts.BaseCueConst.*;
import static im.consts.DictionaryConst.D010;
import static im.consts.ParameterConst.BACKUP_LOCATION;

/**
 * @author hoomoomoo
 * @description 参数信息服务实现类
 * @package im.service.impl
 * @date 2019/10/20
 */

@Service
@Transactional
public class SysParameterServiceImpl implements SysParameterService {

    private static final Logger logger = LoggerFactory.getLogger(SysParameterServiceImpl.class);

    @Autowired
    private SysParameterDao sysParameterDao;

    @Autowired
    private SysDictionaryDao sysDictionaryDao;


    /**
     * 查询参数信息
     *
     * @param sysParameterQueryModel
     * @return
     */
    @Override
    public ResultData selectList(SysParameterQueryModel sysParameterQueryModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        SysLogUtils.parameter(logger, sysParameterQueryModel);
        List<SysParameterModel> sysParameterModelList = sysParameterDao.selectList(sysParameterQueryModel);
        transfer(sysParameterModelList);
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        return new ResultData(true, SELECT_SUCCESS, sysParameterModelList);
    }

    /**
     * 保存参数信息
     *
     * @param sysParameterModel
     * @return
     */
    @Override
    public ResultData save(SysParameterModel sysParameterModel) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_UPDATE);
        SysLogUtils.parameter(logger, sysParameterModel);
        if (PARAMETER_TYPE_SWITCH.equals(sysParameterModel.getParameterType())) {
            if (sysParameterModel.getParameterValue() != null) {
                sysParameterModel.setParameterValue(STR_1);
            } else {
                sysParameterModel.setParameterValue(STR_2);
            }
        }
        if (StringUtils.isNotBlank(sysParameterModel.getParameterValue())) {
            sysParameterModel.setParameterValue(sysParameterModel.getParameterValue().replaceAll(NBSP, STR_EMPTY));
        }
        sysParameterDao.save(sysParameterModel);

        // 修改dmp备份路径
        try {
            if (BACKUP_LOCATION.equals(sysParameterModel.getParameterCode())) {
                String backupLocation =
                        new StringBuffer(SINGLE_QUOTATION_MARKS).append(sysParameterModel.getParameterValue()).append(SINGLE_QUOTATION_MARKS).toString();
                sysParameterModel.setParameterValue(backupLocation);
                sysParameterDao.updateBackupDir(sysParameterModel);
            }
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_PARAMETER, e);
        }
        WebSocketServerConfig.sendMessageInfo(WEBSOCKET_TOPIC_NAME_CONSOLE, LOG_BUSINESS_TYPE_PARAMETER);
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_UPDATE);
        return new ResultData(true, UPDATE_SUCCESS, null);
    }

    /**
     * 获取系统参数
     *
     * @param parameterCode
     * @return
     */
    @Override
    public Boolean getParameterBoolean(String parameterCode) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        SysParameterQueryModel sysParameterQueryModel = new SysParameterQueryModel();
        sysParameterQueryModel.setParameterCode(parameterCode);
        SysLogUtils.parameter(logger, sysParameterQueryModel);
        boolean flag = false;
        SysParameterModel sysParameterModel = sysParameterDao.selectSysParameter(sysParameterQueryModel);
        if (sysParameterModel != null && STR_1.equals(sysParameterModel.getParameterValue())) {
            flag = true;
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        return flag;
    }

    /**
     * 获取系统参数
     *
     * @param parameterCode
     * @return
     */
    @Override
    public String getParameterString(String parameterCode) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        SysParameterQueryModel sysParameterQueryModel = new SysParameterQueryModel();
        sysParameterQueryModel.setParameterCode(parameterCode);
        SysLogUtils.parameter(logger, sysParameterQueryModel);
        String parameterValue = STR_EMPTY;
        SysParameterModel sysParameterModel = sysParameterDao.selectSysParameter(sysParameterQueryModel);
        if (sysParameterModel != null) {
            parameterValue = sysParameterModel.getParameterValue();
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        return parameterValue;
    }

    /**
     * 获取系统参数
     *
     * @param parameterCode
     * @return
     */
    @Override
    public Integer getParameterInteger(String parameterCode) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        SysParameterQueryModel sysParameterQueryModel = new SysParameterQueryModel();
        sysParameterQueryModel.setParameterCode(parameterCode);
        SysLogUtils.parameter(logger, sysParameterQueryModel);
        int parameterValue = 0;
        SysParameterModel sysParameterModel = sysParameterDao.selectSysParameter(sysParameterQueryModel);
        if (sysParameterModel != null) {
            parameterValue = Integer.valueOf(sysParameterModel.getParameterValue());
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        return parameterValue;
    }

    /**
     * 获取邮件配置参数
     *
     * @return
     */
    @Override
    public MailConfigModel getMailConfig() {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        MailConfigModel mailConfigModel = new MailConfigModel();
        List<SysParameterModel> sysParameterModelList = sysParameterDao.selectMailConfig();
        if (CollectionUtils.isNotEmpty(sysParameterModelList)) {
            Map mailMap = new HashMap(16);
            for (SysParameterModel sysParameterModel : sysParameterModelList) {
                mailMap.put(sysParameterModel.getParameterCode(), sysParameterModel.getParameterValue());
            }
            mailConfigModel = (MailConfigModel) SysBeanUtils.mapToBean(MailConfigModel.class, mailMap);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_PARAMETER, LOG_OPERATE_TYPE_SELECT);
        return mailConfigModel;
    }

    /**
     * 转义
     *
     * @param sysParameterModelList
     */
    private void transfer(List<SysParameterModel> sysParameterModelList) {
        if (CollectionUtils.isNotEmpty(sysParameterModelList)) {
            SysDictionaryQueryModel sysDictionaryQueryModel = new SysDictionaryQueryModel();
            sysDictionaryQueryModel.setDictionaryCode(D010);
            List<SysDictionaryModel> dictionaryModelList =
                    sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
            Map<String, String> dictionaryMap = new HashMap(16);
            for (SysDictionaryModel sysDictionaryModel : dictionaryModelList) {
                dictionaryMap.put(sysDictionaryModel.getDictionaryItem(), sysDictionaryModel.getDictionaryCaption());
            }
            for (SysParameterModel sysParameterModel : sysParameterModelList) {
                if (PARAMETER_TYPE_SWITCH.equals(sysParameterModel.getParameterType())) {
                    if (dictionaryMap.get(sysParameterModel.getParameterValue()) != null) {
                        sysParameterModel.setParameterOldValue(sysParameterModel.getParameterValue());
                        sysParameterModel.setParameterValue(dictionaryMap.get(sysParameterModel.getParameterValue()));
                    }
                }
            }
        }
    }
}
