package im.service.impl;

import im.config.bean.SystemConfigBean;
import im.dao.*;
import im.model.*;
import im.model.base.BaseModel;
import im.service.*;
import im.util.SysCommonUtils;
import im.util.SysJsonUtils;
import im.util.SysLogUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static im.config.RunDataConfig.MAIL_CONFIG;
import static im.config.RunDataConfig.MAIL_HANDLE_FLAG;
import static im.consts.BaseConst.*;
import static im.consts.BaseConst.MINUS;
import static im.consts.BaseCueConst.*;
import static im.consts.DictionaryConst.*;
import static im.consts.ParameterConst.START_MAIL;

/**
 * @author hoomoomoo
 * @description 接口信息服务实现类
 * @package im.service.impl
 * @date 2020/02/09
 */

@Service
@Transactional
public class SysInterfaceServiceImpl implements SysInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(SysInterfaceServiceImpl.class);

    @Autowired
    private SysMailService sysMailService;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysInterfaceDao sysInterfaceDao;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    @Autowired
    private SysIncomeService sysIncomeService;

    @Autowired
    private SysGiftService sysGiftService;

    @Autowired
    private SystemConfigBean systemConfigBean;

    @Autowired
    private SysParameterService sysParameterService;

    /**
     * 处理邮件请求
     */
    @Override
    public void handleMailRequest() {
        if (MAIL_HANDLE_FLAG) {
            SysLogUtils.info(logger, LOG_BUSINESS_MAIL_HANDLEING);
            return;
        }
        // 邮件配置参数判断
        if (sysMailService.checkMailConfig()) {
            SysLogUtils.error(logger, MAIL_NOT_CONFIG);
            return;
        }
        MAIL_HANDLE_FLAG = true;
        try {
            SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_MAIL, LOG_OPERATE_TYPE_HANDLE);
            // 获取邮件读取开始ID
            SysInterfaceQueryModel sysInterfaceQueryModel = new SysInterfaceQueryModel(ASTERISK);
            SysInterfaceModel sysInterfaceModel = sysInterfaceDao.selectOne(sysInterfaceQueryModel);
            if (sysInterfaceModel == null) {
                // 基础数据不存在 存储基础数据
                sysInterfaceModel = new SysInterfaceModel();
                BeanUtils.copyProperties(sysInterfaceQueryModel, sysInterfaceModel);
                sysInterfaceModel.setInterfaceId(sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_INTERFACE));
                String requestId = new StringBuffer(MAIL_CONFIG.getMailReceiveHost()).append(MINUS)
                        .append(MAIL_CONFIG.getMailReceiveUsername()).append(MINUS).append(0).toString();
                sysInterfaceModel.setRequestId(requestId);
                SysCommonUtils.setCreateUserInfo(sysInterfaceModel, sysSystemService.getUserId());
                sysInterfaceDao.save(sysInterfaceModel);
            }
            SysMailModel mailModel = new SysMailModel();
            mailModel.setSubject(MAIL_CONFIG.getMailSubject());
            mailModel.setMailId(sysInterfaceModel.getRequestId());
            List<SysMailModel> sysMailModelList = sysMailService.receiveMail(mailModel);
            if (CollectionUtils.isNotEmpty(sysMailModelList)) {
                for (SysMailModel sysMailModel : sysMailModelList) {
                    List<BaseModel> baseModelList = null;
                    try {
                        baseModelList = SysCommonUtils.getMailXmlToBean(sysMailModel.getContent(), SysInterfaceRequestModel.class);
                    } catch (DocumentException e) {
                        SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL, e);
                    }
                    handleRequestData(baseModelList, sysMailModel);
                }
            }
            SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_MAIL, LOG_OPERATE_TYPE_HANDLE);
        } catch (Exception e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL, e);
        } finally {
            MAIL_HANDLE_FLAG = false;
        }
    }

    /**
     * 处理业务请求数据
     *
     * @param baseModelList
     * @param sysMailModel
     * @return
     */
    @Override
    public SysInterfaceResponseModel handleRequestData(List<BaseModel> baseModelList, SysMailModel sysMailModel) {
        SysInterfaceResponseModel sysInterfaceResponseModel = new SysInterfaceResponseModel();
        if (CollectionUtils.isNotEmpty(baseModelList)) {
            // 数据校验
            // 校验异常信息集合
            List<SysCheckResultModel> sysCheckResultModelList = new ArrayList<>();
            // 转义后业务数据
            List<SysInterfaceRequestModel> sysInterfaceRequestModelList = new ArrayList<>();
            // 业务数据ID集合
            List<String> interfaceIdList = new ArrayList<>();
            boolean check = checkBusinessData(baseModelList, sysInterfaceRequestModelList, sysCheckResultModelList);
            if (check) {
                for (int i=0; i<sysInterfaceRequestModelList.size(); i++) {
                    SysInterfaceRequestModel sysInterfaceRequestModel = sysInterfaceRequestModelList.get(i);
                    if (sysMailModel != null) {
                        sysInterfaceRequestModel.setDataType(new StringBuffer(D008).append(MINUS).append(STR_2).toString());
                    } else {
                        sysInterfaceRequestModel.setDataType(new StringBuffer(D008).append(MINUS).append(STR_3).toString());
                    }
                    // 数据存储 存储业务数据
                    String requestId = saveBusinessData(sysInterfaceRequestModel);
                    // 添加业务Id
                    sysInterfaceResponseModel.getBusinessNo().add(requestId);
                    // 存储接口数据
                    String interfaceId = saveInterfaceData(requestId, (SysInterfaceRequestModel)baseModelList.get(i), null);
                    interfaceIdList.add(interfaceId);
                }
            } else {
                for (int i=0; i<sysInterfaceRequestModelList.size(); i++) {
                    String interfaceId = saveInterfaceData(STR_EMPTY, (SysInterfaceRequestModel)baseModelList.get(i),
                            sysCheckResultModelList.get(i));
                    interfaceIdList.add(interfaceId);
                }
            }

            if (sysMailModel != null) {
                // 邮件反馈处理结果
                SysMailModel mail = new SysMailModel();
                mail.setFrom(MAIL_CONFIG.getMailFrom());
                mail.setTo(sysMailModel.getTo());
                mail.setSubject(systemConfigBean.getAppDescribe() + INTERFACE_FEEDBACK_MAIL);
                mail.setContent(getContent(check, baseModelList, sysCheckResultModelList,
                        sysInterfaceResponseModel, new StringBuffer(D008).append(MINUS).append(STR_2).toString()));
                boolean sendStatus = sysMailService.sendMail(mail);
                String feedbackStatus = sendStatus ? D002 + MINUS + STR_1 : D002 + MINUS + STR_2;
                // 更新接口数据邮件反馈状态
                for (String interfaceId : interfaceIdList) {
                    SysInterfaceModel sysInterface = new SysInterfaceModel();
                    sysInterface.setInterfaceId(interfaceId);
                    sysInterface.setFeedbackStatus(feedbackStatus);
                    sysInterfaceDao.update(sysInterface);
                }
            } else {
                sysInterfaceResponseModel.setMessage(getContent(check, baseModelList, sysCheckResultModelList,
                        sysInterfaceResponseModel, new StringBuffer(D008).append(MINUS).append(STR_3).toString()));
            }
        }
        return sysInterfaceResponseModel;
    }

    /**
     * 获取返回消息内容
     *
     * @param status
     * @param baseModelList
     * @param sysCheckResultModelList
     * @param sysInterfaceResponseModel
     * @return
     */
    private String getContent(boolean status, List<BaseModel> baseModelList,
                                  List<SysCheckResultModel> sysCheckResultModelList,
                                  SysInterfaceResponseModel sysInterfaceResponseModel,
                                  String requestType) {
        StringBuffer content = new StringBuffer(STR_EMPTY);
        String separateType = NEXT_LINE;
        if (new StringBuffer(D008).append(MINUS).append(STR_2).toString().equals(requestType)) {
            separateType = STR_SPACE;
        }
        for (int i=0; i<baseModelList.size(); i++) {
            SysInterfaceRequestModel sysInterfaceRequestModel = (SysInterfaceRequestModel)baseModelList.get(i);
            content.append(sysInterfaceRequestModel.getUser()).append(separateType)
                    .append(sysInterfaceRequestModel.getTarget()).append(separateType)
                    .append(sysInterfaceRequestModel.getDate()).append(separateType)
                    .append(sysInterfaceRequestModel.getType()).append(separateType)
                    .append(sysInterfaceRequestModel.getSubType()).append(separateType)
                    .append(sysInterfaceRequestModel.getAmount()).append(separateType);
            if (StringUtils.isNotBlank(sysInterfaceRequestModel.getMemo())) {
                content.append(sysInterfaceRequestModel.getMemo()).append(separateType);
            }
            if (status) {
                if (new StringBuffer(D008).append(MINUS).append(STR_2).toString().equals(requestType)) {
                    content.append(INTERFACE_MAIL_SUCCESS).append(separateType);
                } else {
                    content.append(separateType).append(INTERFACE_WECHAT_SUCCESS).append(separateType).append(separateType);
                }
                if (CollectionUtils.isNotEmpty(sysInterfaceResponseModel.getBusinessNo())) {
                    StringBuffer businessNo = new StringBuffer(BUSINESS_BUSINESS_NO).append(separateType);
                    for (String businessId : sysInterfaceResponseModel.getBusinessNo()) {
                        businessNo.append(businessId).append(separateType);
                    }
                    content.append(businessNo);
                }
            } else {
                if (new StringBuffer(D008).append(MINUS).append(STR_2).toString().equals(requestType)) {
                    content.append(INTERFACE_MAIL_FAIL).append(separateType).append(getErrorMessage(sysCheckResultModelList.get(i), COMMA));
                    content.append(BR);
                } else {
                    content.append(separateType).append(INTERFACE_WECHAT_FAIL).append(separateType).append(separateType)
                           .append(getErrorMessage(sysCheckResultModelList.get(i), NEXT_LINE));
                    content.append(NEXT_LINE);
                }
            }
        }
        return content.toString();
    }
    /**
     * 获取失败信息
     *
     * @param sysCheckResultModel
     * @return
     */
    private String getErrorMessage(SysCheckResultModel sysCheckResultModel, String separateType) {
        StringBuffer message = new StringBuffer(STR_EMPTY);
        List<String> tips = sysCheckResultModel.getMessage();
        for (String msg : tips) {
            message.append(msg).append(separateType);
        }
        while (message.length() >= 150) {
            message = new StringBuffer(message.toString().substring(0, message.toString().lastIndexOf(separateType)));
        }
        String errorInfo = message.toString();
        if (errorInfo.endsWith(separateType)) {
            errorInfo = errorInfo.substring(0, errorInfo.lastIndexOf(separateType));
        }
        return errorInfo;
    }

    /**
     * 保存接口数据
     * @param requestId
     * @param sysInterfaceRequestModel
     */
    private String saveInterfaceData(String requestId, SysInterfaceRequestModel sysInterfaceRequestModel,
                                   SysCheckResultModel sysCheckResultModel) {
        String interfaceId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_INTERFACE);
        SysInterfaceModel sysInterfaceModel = new SysInterfaceModel();
        sysInterfaceModel.setInterfaceId(interfaceId);
        sysInterfaceModel.setRequestId(requestId);
        sysInterfaceModel.setRequestData(SysJsonUtils.toJson(sysInterfaceRequestModel));
        if (sysCheckResultModel == null) {
            sysInterfaceModel.setRequestResult(D002 + MINUS + STR_1);
            sysInterfaceModel.setRequestMessage(OPERATE_SUCCESS);
        } else {
            sysInterfaceModel.setRequestResult(D002 + MINUS + STR_2);
            sysInterfaceModel.setRequestMessage(getErrorMessage(sysCheckResultModel, COMMA));
        }
        SysCommonUtils.setCreateUserInfo(sysInterfaceModel, sysSystemService.getUserId());
        sysInterfaceDao.save(sysInterfaceModel);
        return interfaceId;
    }

    /**
     * 业务数据存储
     *
     * @param sysInterfaceRequestModel
     */
    private String saveBusinessData(SysInterfaceRequestModel sysInterfaceRequestModel) {
        String requestId = STR_EMPTY;
        if (INTERFACE_TYPE_INCOME.equals(sysInterfaceRequestModel.getType())) {
            requestId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_INCOME);
            SysIncomeModel sysIncomeModel = new SysIncomeModel();
            sysIncomeModel.setDataType(sysInterfaceRequestModel.getDataType());
            sysIncomeModel.setIncomeId(requestId);
            sysIncomeModel.setUserId(sysInterfaceRequestModel.getUser());
            sysIncomeModel.setIncomeType(sysInterfaceRequestModel.getSubType());
            try {
                sysIncomeModel.setIncomeDate(new SimpleDateFormat(FORMAT_DATE_TEMPLATE).parse(sysInterfaceRequestModel.getDate()));
            } catch (ParseException e) {
                SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL, e);
            }
            sysIncomeModel.setIncomeCompany(sysInterfaceRequestModel.getTarget());
            sysIncomeModel.setIncomeAmount(sysInterfaceRequestModel.getAmount());
            sysIncomeModel.setIncomeMemo(sysInterfaceRequestModel.getMemo());
            SysCommonUtils.setCreateUserInfo(sysIncomeModel, sysSystemService.getUserId());
            sysIncomeService.save(sysIncomeModel);
        } else if (INTERFACE_TYPE_GIFT.equals(sysInterfaceRequestModel.getType())) {
            requestId = sysSystemService.getBusinessSerialNo(BUSINESS_TYPE_GIFT);
            SysGiftModel sysGiftModel = new SysGiftModel();
            sysGiftModel.setDataType(sysInterfaceRequestModel.getDataType());
            sysGiftModel.setGiftId(requestId);
            sysGiftModel.setGiftType(sysInterfaceRequestModel.getSubType());
            sysGiftModel.setGiftSender(sysInterfaceRequestModel.getUser());
            sysGiftModel.setGiftReceiver(sysInterfaceRequestModel.getTarget());
            try {
                sysGiftModel.setGiftDate(new SimpleDateFormat(FORMAT_DATE_TEMPLATE).parse(sysInterfaceRequestModel.getDate()));
            } catch (ParseException e) {
                SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL, e);
            }
            sysGiftModel.setGiftAmount(sysInterfaceRequestModel.getAmount());
            sysGiftModel.setGiftMemo(sysInterfaceRequestModel.getMemo());
            SysCommonUtils.setCreateUserInfo(sysGiftModel, sysSystemService.getUserId());
            sysGiftService.save(sysGiftModel);
        }
        return requestId;
    }


    /**
     * 数据校验
     *
     * @param baseModelList
     * @param sysInterfaceRequestModelList
     * @param sysCheckResultModelList
     * @return
     */
    private boolean checkBusinessData(List<BaseModel> baseModelList,
                                      List<SysInterfaceRequestModel> sysInterfaceRequestModelList,
                                      List<SysCheckResultModel> sysCheckResultModelList) {
        boolean checkStatus = true;
        for (BaseModel baseModel : baseModelList) {
            SysInterfaceRequestModel sysInterfaceRequestModel = (SysInterfaceRequestModel)baseModel;
            // 非空校验
            SysCheckResultModel sysCheckResultModel = checkEmpty(sysInterfaceRequestModel);
            if (!sysCheckResultModel.getResult()) {
                checkStatus = false;
                sysCheckResultModelList.add(sysCheckResultModel);
            } else {
                // 业务数据校验
                sysCheckResultModel = checkBusiness(sysInterfaceRequestModel, sysInterfaceRequestModelList);
                if (!sysCheckResultModel.getResult()) {
                    checkStatus = false;
                    sysCheckResultModelList.add(sysCheckResultModel);
                } else {
                    // 校验通过
                    sysCheckResultModel.getMessage().add(INTERFACE_TOGETHER_ERROR);
                    sysCheckResultModelList.add(sysCheckResultModel);
                }
            }
        }
        return checkStatus;
    }

    /**
     * 业务数据校验
     *
     * @param sysInterfaceRequestModel
     * @param sysInterfaceRequestModelList
     * @return
     */
    private SysCheckResultModel checkBusiness(SysInterfaceRequestModel sysInterfaceRequestModel,
                                              List<SysInterfaceRequestModel> sysInterfaceRequestModelList) {
        SysCheckResultModel sysCheckResultModel = new SysCheckResultModel(true);
        SysInterfaceRequestModel sysInterfaceRequest = new SysInterfaceRequestModel();
        BeanUtils.copyProperties(sysInterfaceRequestModel, sysInterfaceRequest);
        sysInterfaceRequestModelList.add(sysInterfaceRequest);
        if (sysInterfaceRequestModel != null) {
            if (INTERFACE_TYPE_INCOME.equals(sysInterfaceRequestModel.getType())) {
                // 收入
                // 校验当前用户
                SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
                sysUserQueryModel.setUserName(sysInterfaceRequestModel.getUser());
                SysUserModel sysUserModel = sysUserDao.selectOne(sysUserQueryModel);
                if (sysUserModel == null) {
                    sysCheckResultModel.setResult(false);
                    sysCheckResultModel.getMessage().add(String.format(INTERFACE_NOT_EXIT_USER, sysInterfaceRequestModel.getUser()));
                } else {
                    sysInterfaceRequest.setUser(sysUserModel.getUserId());
                }
                // 校验目标对象
                SysDictionaryQueryModel sysDictionaryQueryModel = new SysDictionaryQueryModel();
                sysDictionaryQueryModel.setDictionaryCode(D005);
                sysDictionaryQueryModel.setDictionaryCaption(sysInterfaceRequestModel.getTarget());
                List<SysDictionaryModel> sysDictionaryModelList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
                if (CollectionUtils.isEmpty(sysDictionaryModelList)) {
                    sysCheckResultModel.setResult(false);
                    sysCheckResultModel.getMessage().add(String.format(INTERFACE_NOT_EXIT_TARGET, sysInterfaceRequestModel.getTarget()));
                } else {
                    sysInterfaceRequest.setTarget(D005 + MINUS + sysDictionaryModelList.get(0).getDictionaryItem());
                }
                checkCommonBusiness(sysInterfaceRequestModel, sysInterfaceRequest, sysCheckResultModel, D003);
            } else if (INTERFACE_TYPE_GIFT.equals(sysInterfaceRequestModel.getType())) {
                // 随礼
                // 校验当前用户
                SysDictionaryQueryModel sysDictionaryQueryModel = new SysDictionaryQueryModel();
                sysDictionaryQueryModel.setDictionaryCode(D009);
                sysDictionaryQueryModel.setDictionaryCaption(sysInterfaceRequestModel.getUser());
                List<SysDictionaryModel> sysDictionaryModelList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
                if (CollectionUtils.isEmpty(sysDictionaryModelList)) {
                    sysCheckResultModel.setResult(false);
                    sysCheckResultModel.getMessage().add(String.format(INTERFACE_NOT_EXIT_USER, sysInterfaceRequestModel.getUser()));
                } else {
                    sysInterfaceRequest.setUser(D009 + MINUS + sysDictionaryModelList.get(0).getDictionaryItem());
                }
                // 校验目标对象
                sysDictionaryQueryModel.setDictionaryCaption(sysInterfaceRequestModel.getTarget());
                sysDictionaryModelList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
                if (CollectionUtils.isEmpty(sysDictionaryModelList)) {
                    sysCheckResultModel.setResult(false);
                    sysCheckResultModel.getMessage().add(String.format(INTERFACE_NOT_EXIT_TARGET, sysInterfaceRequestModel.getTarget()));
                } else {
                    sysInterfaceRequest.setTarget(D009 + MINUS + sysDictionaryModelList.get(0).getDictionaryItem());
                }
                // 校验送礼人或收礼人必须有一个为系统用户
                SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
                sysUserQueryModel.setUserName(sysInterfaceRequestModel.getUser());
                SysUserModel sysUserModel = sysUserDao.selectOne(sysUserQueryModel);
                sysUserQueryModel.setUserName(sysInterfaceRequestModel.getTarget());
                SysUserModel targetUserModel = sysUserDao.selectOne(sysUserQueryModel);
                if (sysUserModel == null && targetUserModel == null) {
                    sysCheckResultModel.setResult(false);
                    sysCheckResultModel.getMessage().add(String.format(INTERFACE_MUST_ONE_SYSTEM_USER, sysInterfaceRequestModel.getUser(), sysInterfaceRequestModel.getTarget()));
                }
                checkCommonBusiness(sysInterfaceRequestModel, sysInterfaceRequest, sysCheckResultModel, D004);
            } else {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(String.format(INTERFACE_NOT_EXIT_TYPE, sysInterfaceRequestModel.getType()));
            }
        }
        return sysCheckResultModel;
    }

    /**
     * 公共业务数据校验
     *
     * @param sysInterfaceRequestModel
     * @param sysInterfaceRequest
     * @param sysCheckResultModel
     * @param subTypeCode
     */
    private void checkCommonBusiness(SysInterfaceRequestModel sysInterfaceRequestModel,
                                     SysInterfaceRequestModel sysInterfaceRequest,
                                     SysCheckResultModel sysCheckResultModel, String subTypeCode) {
        // 校验业务日期
        String businessDate = sysInterfaceRequestModel.getDate();
        if (businessDate.length() != 8) {
            sysCheckResultModel.setResult(false);
            sysCheckResultModel.getMessage().add(String.format(INTERFACE_FORMAT_DATE, businessDate));
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_TEMPLATE);
            try {
                simpleDateFormat.parse(businessDate);
            } catch (ParseException e) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(String.format(INTERFACE_FORMAT_DATE, businessDate));
                SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL, e);
            }
        }
        // 校验业务子类型
        SysDictionaryQueryModel sysDictionaryQueryModel = new SysDictionaryQueryModel();
        sysDictionaryQueryModel.setDictionaryCode(subTypeCode);
        sysDictionaryQueryModel.setDictionaryCaption(sysInterfaceRequestModel.getSubType());
        List<SysDictionaryModel> sysDictionaryModelList = sysDictionaryDao.selectSysDictionary(sysDictionaryQueryModel);
        if (CollectionUtils.isEmpty(sysDictionaryModelList)) {
            sysCheckResultModel.setResult(false);
            sysCheckResultModel.getMessage().add(String.format(INTERFACE_NOT_EXIT_SUBTYPE, sysInterfaceRequestModel.getSubType()));
        } else {
            sysInterfaceRequest.setSubType(subTypeCode + MINUS + sysDictionaryModelList.get(0).getDictionaryItem());
        }
        // 校验业务金额
        try {
            Double.valueOf(sysInterfaceRequestModel.getAmount());
        } catch (NumberFormatException e) {
            sysCheckResultModel.setResult(false);
            sysCheckResultModel.getMessage().add(String.format(INTERFACE_FORMAT_AMOUNT, sysInterfaceRequestModel.getAmount()));
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_MAIL, e);
        }
        // 校验业务备注
        if (StringUtils.isNotEmpty(sysInterfaceRequestModel.getMemo()) && sysInterfaceRequestModel.getMemo().length() > 150) {
            sysCheckResultModel.setResult(false);
            sysCheckResultModel.getMessage().add(INTERFACE_LENGTH_MEMO);
        }
    }

    /**
     * 非空校验
     * @param sysInterfaceRequestModel
     * @return
     */
    private SysCheckResultModel checkEmpty(SysInterfaceRequestModel sysInterfaceRequestModel) {
        SysCheckResultModel sysCheckResultModel = new SysCheckResultModel(true);
        if (sysInterfaceRequestModel != null) {
            if (StringUtils.isBlank(sysInterfaceRequestModel.getUser())) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(INTERFACE_NOT_EMPTY_USER);
            }
            if (StringUtils.isBlank(sysInterfaceRequestModel.getTarget())) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(INTERFACE_NOT_EMPTY_TARGET);
            }
            if (StringUtils.isBlank(sysInterfaceRequestModel.getDate())) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(INTERFACE_NOT_EMPTY_DATE);
            }
            if (StringUtils.isBlank(sysInterfaceRequestModel.getType())) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(INTERFACE_NOT_EMPTY_TYPE);
            }
            if (StringUtils.isBlank(sysInterfaceRequestModel.getSubType())) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(INTERFACE_NOT_EMPTY_SUBTYPE);
            }
            if (StringUtils.isBlank(sysInterfaceRequestModel.getAmount())) {
                sysCheckResultModel.setResult(false);
                sysCheckResultModel.getMessage().add(INTERFACE_NOT_EMPTY_AMOUNT);
            }
        }
        return sysCheckResultModel;
    }

    /**
     * 系统启动读取邮件
     */
    @Override
    public void startMail() {
        boolean startMail = sysParameterService.getParameterBoolean(START_MAIL);
        if (startMail) {
            handleMailRequest();
        }
    }
}
