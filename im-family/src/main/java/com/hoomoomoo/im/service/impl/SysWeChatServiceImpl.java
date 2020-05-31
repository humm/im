package com.hoomoomoo.im.service.impl;

import com.hoomoomoo.im.dao.SysConsoleDao;
import com.hoomoomoo.im.dao.SysGiftDao;
import com.hoomoomoo.im.model.*;
import com.hoomoomoo.im.model.BaseModel;
import com.hoomoomoo.im.model.SessionBean;
import com.hoomoomoo.im.service.*;
import com.hoomoomoo.im.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hoomoomoo.im.config.RunDataConfig.*;
import static com.hoomoomoo.im.consts.BusinessConst.*;
import static com.hoomoomoo.im.consts.CueConst.*;
import static com.hoomoomoo.im.consts.DictionaryConst.D000;
import static com.hoomoomoo.im.consts.DictionaryConst.D013;
import static com.hoomoomoo.im.consts.ParameterConst.*;
import static com.hoomoomoo.im.consts.WeChatConst.*;

/**
 * @author hoomoomoo
 * @description 微信消息服务实现类
 * @package com.hoomoomoo.im.service.impl
 * @date 2020/02/27
 */

@Service
@Transactional
public class SysWeChatServiceImpl implements SysWeChatService {

    private static final Logger logger = LoggerFactory.getLogger(SysWeChatServiceImpl.class);

    @Autowired
    private SysParameterService sysParameterService;

    @Autowired
    private SysWeChatUserService sysWeChatUserService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysInterfaceService sysInterfaceService;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private SysConsoleDao sysConsoleDao;

    @Autowired
    private SysGiftDao sysGiftDao;

    @Autowired
    private SysIncomeService sysIncomeService;

    @Autowired
    private SysGiftService sysGiftService;



    /**
     * 微信消息处理
     *
     * @param request
     * @return
     */
    @Override
    public String message(HttpServletRequest request, HttpServletResponse response) {
        SysLogUtils.serviceStart(logger, LOG_BUSINESS_TYPE_WECHAT, LOG_OPERATE_TYPE_HANDLE);
        try {
            request.setCharacterEncoding(UTF8);
            response.setCharacterEncoding(UTF8);
        } catch (UnsupportedEncodingException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_WECHAT, e);
        }
        String responseMessage;
        if (REQUEST_TYPE_GET.equals(request.getMethod())) {
            responseMessage = checkToken(request);
        } else {
            responseMessage = handleMessage(request);
        }
        SysLogUtils.serviceEnd(logger, LOG_BUSINESS_TYPE_WECHAT, LOG_OPERATE_TYPE_HANDLE);
        return responseMessage;
    }

    /**
     * 更新微信操作信息
     */
    @Override
    public void updateOperateFlow() {
        Iterator<String> iterator = WECHAT_FLOW_OPERATE.keySet().iterator();
        Integer operateTime = sysParameterService.getParameterInteger(WECHAT_OPERATE_TIME);
        while (iterator.hasNext()) {
            String userKey = iterator.next();
            SysWeChatOperateModel sysWeChatOperateModel = WECHAT_FLOW_OPERATE.get(userKey);
            Long lastOperateTime = sysWeChatOperateModel.getOperateTime();
            Long currentTime = System.currentTimeMillis();
            SysLogUtils.info(logger, sysWeChatOperateModel.getOperateFlow());
            if (System.currentTimeMillis() > (lastOperateTime + operateTime * 1000)) {
                if (isAuthBind(userKey)) {
                    sysWeChatOperateModel.setOperateFlow(FLOW_CODE_SELECT);
                    sysWeChatOperateModel.setOperateTime(currentTime);
                }
            }
        }
    }

    /**
     * 是否完成身份验证 用户信息绑定
     * @param userKey
     * @return
     */
    private boolean isAuthBind(String userKey) {
        boolean flag = false;
        if (StringUtils.isNotBlank(userKey)) {
            String[] userInfo = userKey.split(MINUS);
            if (userInfo.length == 2) {
                SysWeChatUserQueryModel sysWeChatUserQueryModel = new SysWeChatUserQueryModel();
                sysWeChatUserQueryModel.setWeChatPublicId(userInfo[0]);
                sysWeChatUserQueryModel.setWeChatUserId(userInfo[1]);
                SysWeChatUserModel sysWeChatUserModel = sysWeChatUserService.selectOne(sysWeChatUserQueryModel);
                if (sysWeChatUserModel != null && (D013 + MINUS + STR_1).equals(sysWeChatUserModel.getIsAuth()) && StringUtils.isNotBlank(sysWeChatUserModel.getUserId())) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * token 校验
     *
     * @param request
     * @return
     */
    private String checkToken(HttpServletRequest request) {
        String signature = request.getParameter(PARAMETER_SIGNATURE);
        String timestamp = request.getParameter(PARAMETER_TIMESTAMP);
        String nonce = request.getParameter(PARAMETER_NONCE);
        String echostr = request.getParameter(PARAMETER_ECHOSTR);
        ArrayList<String> list = new ArrayList<>();
        list.add(TOKEN);
        list.add(timestamp);
        list.add(nonce);
        Collections.sort(list);
        StringBuffer content = new StringBuffer();
        for(String str:list){
            content.append(str);
        }
        if (signature.equals(hash(content.toString(), ALGORITHM_SHA1))) {
            return echostr;
        } else {
            return STR_EMPTY;
        }
    }

    /**
     * 消息处理
     *
     * @param request
     * @return
     */
    private String handleMessage(HttpServletRequest request) {
        Map<String, String> requestParameter = SysWeChatUtil.parseXml(request);
        SysLogUtils.parameter(logger, requestParameter);
        String msgType = requestParameter.get(PARAMETER_MSGTYPE);
        String responseMsg;
        switch (msgType) {
            case MESSAGE_TYPE_TEXT:
                SysWeChatTextModel sysWeChatTextModel = new SysWeChatTextModel();
                setRequestSysWeChatBaseModelInfo(requestParameter, sysWeChatTextModel);
                sysWeChatTextModel.setContent(requestParameter.get(PARAMETER_CONTENT));
                responseMsg = textMessage(sysWeChatTextModel);
                break;
            case MESSAGE_TYPE_EVENT:
                SysWeChatEventModel sysWeChatEventModel = new SysWeChatEventModel();
                setRequestSysWeChatBaseModelInfo(requestParameter, sysWeChatEventModel);
                sysWeChatEventModel.setEvent(requestParameter.get(PARAMETER_EVENT));
                sysWeChatEventModel.setEventKey(requestParameter.get(PARAMETER_EVENTKEY));
                responseMsg = eventMessage(sysWeChatEventModel);
                break;
            default:
                SysWeChatTextModel weChatTextModel = new SysWeChatTextModel();
                setRequestSysWeChatBaseModelInfo(requestParameter, weChatTextModel);
                weChatTextModel.setContent(WECHAT_UNSUPPORT_EVENT);
                responseMsg = textMessage(weChatTextModel);
                break;
        }
        return responseMsg;
    }

    /**
     * 设置用户session信息
     * 
     * @param sysWeChatBaseModel
     */
    private SessionBean getUserSessionInfo(SysWeChatBaseModel sysWeChatBaseModel) {
        SessionBean sessionBean = new SessionBean();
        SysWeChatUserQueryModel sysWeChatUserQueryModel = new SysWeChatUserQueryModel();
        sysWeChatUserQueryModel.setWeChatPublicId(sysWeChatBaseModel.getToUserName());
        sysWeChatUserQueryModel.setWeChatUserId(sysWeChatBaseModel.getFromUserName());
        SysWeChatUserModel sysWeChatUserModel = sysWeChatUserService.selectOne(sysWeChatUserQueryModel);
        if (sysWeChatUserModel != null) {
            SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
            sysUserQueryModel.setUserId(sysWeChatUserModel.getUserId());
            List<SysUserModel> sysUserModelList = sysUserService.selectSysUser(sysUserQueryModel);
            if (CollectionUtils.isNotEmpty(sysUserModelList)) {
                SysUserModel sysUserModel = sysUserModelList.get(0);
                sessionBean = sysLoginService.setSessionBeanInfo(sysUserModel);
            }
        }
        return sessionBean;
    }
    
    /**
     * 设置请求消息基础信息
     *
     * @param requestParameter
     * @param sysWeChatBaseModel
     */
    private void setRequestSysWeChatBaseModelInfo(Map<String, String> requestParameter,
                                                  SysWeChatBaseModel sysWeChatBaseModel) {
        sysWeChatBaseModel.setFromUserName(requestParameter.get(PARAMETER_FROMUSERNAME));
        sysWeChatBaseModel.setToUserName(requestParameter.get(PARAMETER_TOUSERNAME));
        sysWeChatBaseModel.setMsgType(requestParameter.get(PARAMETER_MSGTYPE));
        sysWeChatBaseModel.setCreateTime(Long.parseLong(requestParameter.get(PARAMETER_CREATETIME)));
        sysWeChatBaseModel.setMsgId(requestParameter.get(PARAMETER_MSGID));
    }

    /**
     * 设置返回消息基础信息
     *
     * @param request
     * @param response
     */
    private void setResponseSysWeChatBaseModelInfo(SysWeChatBaseModel request, SysWeChatBaseModel response){
        response.setToUserName(request.getFromUserName());
        response.setFromUserName(request.getToUserName());
        response.setCreateTime(System.currentTimeMillis());
        response.setFuncFlag(0);
    }

    /**
     * 处理事件消息
     *
     * @param request
     * @return
     */
    private String eventMessage(SysWeChatEventModel request) {
        SysWeChatTextModel response = new SysWeChatTextModel();
        setResponseSysWeChatBaseModelInfo(request, response);
        response.setMsgType(MESSAGE_TYPE_TEXT);
        switch (request.getEvent()) {
            case EVENT_TYPE_SUBSCRIBE:
                subscribeEvent(request, response);
                break;
            case EVENT_TYPE_UNSUBSCRIBE:
                unsubscribeEvent(request, response);
                break;
            default:
                break;
        }
        return SysWeChatUtil.textMessageToXml(response);
    }

    /**
     * 订阅事件处理
     *
     * @param request
     * @param response
     * @return
     */
    private void subscribeEvent(SysWeChatEventModel request, SysWeChatTextModel response) {
        SysWeChatUserModel sysWeChatUserModel = getSysWeChatUserInfo(request);
        if (sysWeChatUserModel == null) {
            insertSysWeChatUserInfo(request);
        } else {
            updateSysWeChatUserInfo(request, new SysWeChatUserModel());
        }
        response.setContent(sysParameterService.getParameterString(WECHAT_WELCOME));
    }

    /**
     * 退订事件处理
     *
     * @param request
     * @param response
     */
    private void unsubscribeEvent(SysWeChatEventModel request, SysWeChatTextModel response) {
        SysWeChatUserModel sysWeChatUserModel = new SysWeChatUserModel();
        sysWeChatUserModel.setWeChatUserId(request.getFromUserName());
        sysWeChatUserModel.setWeChatPublicId(request.getToUserName());
        sysWeChatUserService.delete(sysWeChatUserModel);
        WECHAT_FLOW_OPERATE.remove(request.getToUserName() + MINUS + request.getFromUserName());
        response.setContent(WECHAT_SUBSCRIBE_AGAIN);
    }

    /**
     * 查询微信用户信息
     *
     * @param request
     * @return
     */
    private SysWeChatUserModel getSysWeChatUserInfo(SysWeChatBaseModel request) {
        SysWeChatUserQueryModel sysWeChatUserQueryModel = new SysWeChatUserQueryModel();
        sysWeChatUserQueryModel.setWeChatUserId(request.getFromUserName());
        sysWeChatUserQueryModel.setWeChatPublicId(request.getToUserName());
        return sysWeChatUserService.selectOne(sysWeChatUserQueryModel);
    }

    /**
     * 新增微信用户信息
     *
     * @param request
     */
    private void insertSysWeChatUserInfo(SysWeChatBaseModel request) {
        SysWeChatUserModel sysWeChatUserModel = new SysWeChatUserModel();
        sysWeChatUserModel.setWeChatUserId(request.getFromUserName());
        sysWeChatUserModel.setWeChatPublicId(request.getToUserName());
        sysWeChatUserService.insert(sysWeChatUserModel);
    }

    /**
     * 修改微信用户信息
     *
     * @param request
     */
    private void updateSysWeChatUserInfo(SysWeChatBaseModel request, SysWeChatUserModel sysWeChatUserModel) {
        sysWeChatUserModel.setWeChatUserId(request.getFromUserName());
        sysWeChatUserModel.setWeChatPublicId(request.getToUserName());
        sysWeChatUserService.update(sysWeChatUserModel);
    }

    /**
     * 处理文本消息
     *
     * @param request
     * @return
     */
    private String textMessage(SysWeChatTextModel request) {
        SysWeChatTextModel response = new SysWeChatTextModel();
        setResponseSysWeChatBaseModelInfo(request, response);
        response.setMsgType(MESSAGE_TYPE_TEXT);
        // 查询公众号对外开放状态
        String wechatOpen = sysParameterService.getParameterString(WECHAT_OPEN);
        if (StringUtils.isNotBlank(getOperateInfo(request))) {
            // 业务操作
            replyTextMessage(request, response);
        } else {
            if (STR_2.equals(wechatOpen)) {
                // 微信公众号不对外开放 校验是否进行密钥验证
                SysWeChatUserModel sysWeChatUserModel = getSysWeChatUserInfo(request);
                if (sysWeChatUserModel == null) {
                    insertSysWeChatUserInfo(request);
                    // 提示进行身份信息验证
                    response.setContent(WECHAT_USER_AUTH);
                    setOperateInfo(request, FLOW_CODE_AUTH);
                } else {
                    if (!(D013 + MINUS + STR_1).equals(sysWeChatUserModel.getIsAuth())) {
                        // 提示进行身份信息验证
                        response.setContent(WECHAT_USER_AUTH);
                        setOperateInfo(request, FLOW_CODE_AUTH);
                    } else {
                        // 校验用户信息是否绑定
                        if (StringUtils.isBlank(sysWeChatUserModel.getUserId())) {
                            // 提示绑定系统用户信息
                            response.setContent(WECHAT_USER_BIND);
                            setOperateInfo(request, FLOW_CODE_BIND);
                        } else {
                            // 返回功能清单
                            response.setContent(WECHAT_MAIN_FLOW_LIST);
                            setOperateInfo(request, FLOW_CODE_SELECT);
                        }
                    }
                }
            } else {
                // 微信公众号对外开放 返回功能清单
                response.setContent(WECHAT_MAIN_FLOW_LIST);
                setOperateInfo(request, FLOW_CODE_SELECT);
            }
        }
        return SysWeChatUtil.textMessageToXml(response);
    }

    /**
     * 恢复文本消息
     *
     * @param request
     * @param response
     * @return
     */
    private void replyTextMessage(SysWeChatTextModel request, SysWeChatTextModel response){
        String operateType = getOperateInfo(request);
        if (FLOW_CODE_MAIN.equals(getFlowCode(request))) {
            operateType = FLOW_CODE_SELECT;
        }
        switch (operateType) {
            case FLOW_CODE_SELECT:
                // 服务选择
                selectService(request, response);
                break;
            case FLOW_CODE_AUTH:
                // 身份验证
                authUserInfo(request, response);
                break;
            case FLOW_CODE_BIND:
                // 用户绑定
                bindUserInfo(request, response);
                break;
            case FLOW_CODE_INCOME_ADD$:
                insertIncomeInfo(request, response);
                break;
            case FLOW_CODE_INCOME_DELETE$:
                deleteIncomeInfo(request, response);
                break;
            case FLOW_CODE_GIFT_ADD$:
                insertGiftInfo(request, response);
                break;
            case FLOW_CODE_GIFT_DELETE$:
                deleteGiftInfo(request, response);
                break;
            case FLOW_CODE_GIFT_FREE$:
                freeGiftInfo(request, response);
                break;
            default:
                break;
        }
    }

    /**
     * 新增收入信息
     *
     * @param request
     * @param response
     */
    private void insertIncomeInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        saveBusinessData(request, response, INTERFACE_TYPE_INCOME, FLOW_CODE_INCOME_ADD$);
    }

    /**
     * 删除收入信息
     *
     * @param request
     * @param response
     */
    private void deleteIncomeInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        sysIncomeService.delete(buildBusinessId(request));
        setOperateAfterFlow(request, FLOW_CODE_INCOME_DELETE$);
        response.setContent(LOG_BUSINESS_TYPE_INCOME + DELETE_SUCCESS);
    }

    /**
     * 新增随礼信息
     *
     * @param request
     * @param response
     */
    private void insertGiftInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        saveBusinessData(request, response, INTERFACE_TYPE_GIFT, FLOW_CODE_GIFT_ADD$);
    }

    /**
     * 删除随礼信息
     *
     * @param request
     * @param response
     */
    private void deleteGiftInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        sysGiftService.delete(buildBusinessId(request));
        setOperateAfterFlow(request, FLOW_CODE_GIFT_DELETE$);
        response.setContent(LOG_BUSINESS_TYPE_GIFT + DELETE_SUCCESS);
    }

    /**
     * 自由查询随礼信息
     *
     * @param request
     * @param response
     */
    private void freeGiftInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        String queryCondition = request.getContent();
        StringBuffer responseMsg =
                new StringBuffer(String.format(WECHAT_FREE_TITLE, queryCondition)).append(NEXT_LINE).append(NEXT_LINE);
        SysGiftQueryModel sysGiftQueryModel = new SysGiftQueryModel();
        SessionBean sessionBean = getUserSessionInfo(request);
        if (!sessionBean.getIsAdminData()) {
            sysGiftQueryModel.setGiftId(sessionBean.getUserId());
        }
        // 查询送礼信息
        sysGiftQueryModel.setGiftSender(queryCondition);
        List<SysGiftModel> sendList = sysGiftDao.selectFreeInfo(sysGiftQueryModel);
        if (CollectionUtils.isNotEmpty(sendList)) {
            responseMsg.append(WECHAT_GIFT_SEND_TITLE).append(NEXT_LINE);
            for (SysGiftModel sysGiftModel : sendList) {
                responseMsg.append(sysGiftModel.getGiftType()).append(COLON).append(STR_SPACE)
                           .append(sysGiftModel.getGiftReceiver()).append(STR_SPACE)
                           .append(sysGiftModel.getGiftAmount()).append(NEXT_LINE);
            }
            responseMsg.append(NEXT_LINE);
        }
        // 查询收礼信息
        sysGiftQueryModel.setGiftSender(null);
        sysGiftQueryModel.setGiftReceiver(queryCondition);
        List<SysGiftModel> receivelList = sysGiftDao.selectFreeInfo(sysGiftQueryModel);
        if (CollectionUtils.isNotEmpty(receivelList)) {
            responseMsg.append(WECHAT_GIFT_RECEIVE_TITLE).append(NEXT_LINE);
            for (SysGiftModel sysGiftModel : receivelList) {
                responseMsg.append(sysGiftModel.getGiftType()).append(COLON).append(STR_SPACE)
                        .append(sysGiftModel.getGiftSender()).append(STR_SPACE)
                        .append(sysGiftModel.getGiftAmount()).append(NEXT_LINE);
            }
        }
        if (CollectionUtils.isEmpty(sendList) && CollectionUtils.isEmpty(receivelList)) {
            responseMsg.append(WECHAT_NO_DATA_TITLE);
        }
        setOperateAfterFlow(request, FLOW_CODE_GIFT_FREE$);
        response.setContent(responseMsg.toString());
    }

    /**
     * 构建业务流水号
     *
     * @param request
     * @return
     */
    private String buildBusinessId(SysWeChatTextModel request) {
        String[] condition = getAllCondition(request);
        StringBuffer businessNo = new StringBuffer();
        for (String businessId : condition) {
            businessNo.append(businessId).append(COMMA);
        }
        return condition.toString();
    }

    /**
     * 保存业务数据
     *
     * @param request
     * @param response
     * @param businessType
     * @param flowCode
     * @return
     */
    private void saveBusinessData(SysWeChatTextModel request, SysWeChatTextModel response,
                                  String businessType, String flowCode) {
        String[] condition = getAllCondition(request);
        if (condition.length < 5 || condition.length > 6) {
            response.setContent(WECHAT_PARAMETER_ERROR);
            setOperateInfo(request, flowCode);
        } else {
            List<BaseModel> requestModelList = new ArrayList<>();
            SysInterfaceRequestModel sysInterfaceRequestModel = new SysInterfaceRequestModel();
            sysInterfaceRequestModel.setUser(condition[0]);
            sysInterfaceRequestModel.setTarget(condition[1]);
            sysInterfaceRequestModel.setDate(condition[2]);
            sysInterfaceRequestModel.setType(businessType);
            sysInterfaceRequestModel.setSubType(condition[3]);
            sysInterfaceRequestModel.setAmount(condition[4]);
            if (condition.length == 6) {
                sysInterfaceRequestModel.setMemo(condition[5]);
            }
            requestModelList.add(sysInterfaceRequestModel);
            SysInterfaceResponseModel sysInterfaceResponseModel =
                    sysInterfaceService.handleRequestData(requestModelList, null);
            response.setContent(convertMessage(businessType, sysInterfaceResponseModel.getMessage()));
            if (sysInterfaceResponseModel.getResult()) {
                setOperateAfterFlow(request, flowCode);
            }
        }
    }

    /**
     * 提示消息转换
     *
     * @param message
     * @return
     */
    private String convertMessage(String businessType, String message) {
        // 转换处理提示消息
        if (INTERFACE_TYPE_INCOME.equals(businessType)) {
            return message.replace(INTERFACE_TIPS_USER, INTERFACE_TIPS_INCOME_USER)
                    .replace(INTERFACE_TIPS_TARGET, INTERFACE_TIPS_INCOME_TARGET)
                    .replace(INTERFACE_TIPS_BUSINESS_DATE, INTERFACE_TIPS_INCOME_BUSINESS_DATE)
                    .replace(INTERFACE_TIPS_BUSINESS_SUB_TYPE, INTERFACE_TIPS_INCOME_BUSINESS_SUB_TYPE)
                    .replace(INTERFACE_TIPS_BUSINESS_AMOUNT, INTERFACE_TIPS_INCOME_BUSINESS_AMOUNT)
                    .replace(INTERFACE_TIPS_BUSINESS_MEMO, INTERFACE_TIPS_INCOME_BUSINESS_MEMO);
        } else if (INTERFACE_TYPE_GIFT.equals(businessType)) {
            return message.replace(INTERFACE_TIPS_USER, INTERFACE_TIPS_GIFT_USER)
                    .replace(INTERFACE_TIPS_TARGET, INTERFACE_TIPS_GIFT_TARGET)
                    .replace(INTERFACE_TIPS_BUSINESS_DATE, INTERFACE_TIPS_GIFT_BUSINESS_DATE)
                    .replace(INTERFACE_TIPS_BUSINESS_SUB_TYPE, INTERFACE_TIPS_GIFT_BUSINESS_SUB_TYPE)
                    .replace(INTERFACE_TIPS_BUSINESS_AMOUNT, INTERFACE_TIPS_GIFT_BUSINESS_AMOUNT)
                    .replace(INTERFACE_TIPS_BUSINESS_MEMO, INTERFACE_TIPS_GIFT_BUSINESS_MEMO);
        }
        return message;
    }

    /**
     * 选择服务
     *
     * @param request
     * @param response
     */
    private void selectService(SysWeChatTextModel request, SysWeChatTextModel response) {
        String flowCode = getFlowCode(request);
        if (!isOpenService(flowCode)) {
            response.setContent(new StringBuffer(WECHAT_SERVICE_NOT_EXIST).append(NEXT_LINE).append(WECHAT_MAIN_FLOW_LIST).toString());
            setOperateInfo(request, FLOW_CODE_SELECT);
            return;
        }
        switch (flowCode) {
            case FLOW_CODE_INCOME_MONTH:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_INCOME, STR_1);
                break;
            case FLOW_CODE_INCOME_YEAR:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_INCOME, STR_2);
                break;
            case FLOW_CODE_INCOME_YEAR_CURRENT:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_INCOME, STR_3);
                break;
            case FLOW_CODE_INCOME_ALL:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_INCOME, STR_4);
                break;
            case FLOW_CODE_INCOME_ADD:
                getFlowTip(request, response, FLOW_CODE_INCOME_ADD);
                break;
            case FLOW_CODE_INCOME_UPDATE:
                getFlowTip(request, response, FLOW_CODE_INCOME_UPDATE);
                break;
            case FLOW_CODE_INCOME_DELETE:
                getFlowTip(request, response, FLOW_CODE_INCOME_DELETE);
                break;
            case FLOW_CODE_MORE:
                selectMoreService(request, response, true);
                break;
            case FLOW_CODE_GIFT_FREE:
                getFlowTip(request, response, FLOW_CODE_GIFT_FREE);
                break;
            case FLOW_CODE_GIFT_LAST:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_GIFT, STR_10);
                break;
            case FLOW_CODE_GIFT_YEAR:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_GIFT, STR_11);
                break;
            case FLOW_CODE_GIFT_YEAR_CURRENT:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_GIFT, STR_12);
                break;
            case FLOW_CODE_GIFT_ALL:
                selectBusinessInfo(request, response, LOG_BUSINESS_TYPE_GIFT, STR_13);
                break;
            case FLOW_CODE_GIFT_ADD:
                getFlowTip(request, response, FLOW_CODE_GIFT_ADD);
                break;
            case FLOW_CODE_GIFT_UPDATE:
                getFlowTip(request, response, FLOW_CODE_GIFT_UPDATE);
                break;
            case FLOW_CODE_GIFT_DELETE:
                getFlowTip(request, response, FLOW_CODE_GIFT_DELETE);
                break;
            case FLOW_CODE_MAIN:
                selectMoreService(request, response, false);
                break;
            default:
                defaultReply(request, response);
                break;
        }
    }

    /**
     * 获取操作提示信息
     *
     * @param request
     * @param response
     * @param tipType
     */
    private void getFlowTip(SysWeChatTextModel request, SysWeChatTextModel response, String tipType) {
        response.setContent(WECHAT_FLOW_LIST.get(tipType).getFlowTips());
        setOperateInfo(request, tipType + $);
    }

    /**
     * 查询收入信息
     *
     * @param request
     * @param response
     * @param selectType
     * @param businessType
     */
    private void selectBusinessInfo(SysWeChatTextModel request, SysWeChatTextModel response,
                                    String businessType, String selectType) {
        StringBuffer responseMsg = new StringBuffer(getResponseTitle(request, selectType, businessType));
        boolean isLegal = isLegalCondition(getCondition(request)[0], selectType);
        int index = 0;
        SessionBean sessionBean = getUserSessionInfo(request);
        if (sessionBean != null) {
            String userId = sessionBean.getUserId();
            List<SysDictionaryModel> userList = DICTIONARY_CONDITION.get(userId).get(D000);
            int times = 1;
            if (STR_9.equals(selectType) || STR_10.equals(selectType) || STR_11.equals(selectType)
                    || STR_12.equals(selectType) || STR_13.equals(selectType)) {
                times = 2;
            }
            for (int i=1; i<=times; i++) {
                if (times == 2) {
                    if (i == 1) {
                        responseMsg.append(WECHAT_GIFT_SEND_TITLE).append(NEXT_LINE);
                    } else {
                        responseMsg.append(WECHAT_GIFT_RECEIVE_TITLE).append(NEXT_LINE);
                    }
                }
                if (sessionBean.getIsAdminData()) {
                    // 获取用户信息
                    if (CollectionUtils.isNotEmpty(userList)) {
                        if (userList.size() > 1) {
                            responseMsg.append(getBusinessInfo(null, FAMILY_TITLE, selectType,
                                    getCondition(request)[0], String.valueOf(i)));
                        }
                        index++;
                        for (SysDictionaryModel user : userList) {
                            if (!isLegal && index > 0) {
                                continue;
                            }
                            responseMsg.append(getBusinessInfo(user.getDictionaryItem(), user.getDictionaryCaption(), selectType,
                                    getCondition(request)[0], String.valueOf(i)));
                            index++;
                        }
                    }
                } else {
                    if (CollectionUtils.isNotEmpty(userList)) {
                        SysDictionaryModel user = userList.get(0);
                        responseMsg.append(getBusinessInfo(sessionBean.getUserId(), user.getDictionaryCaption(), selectType,
                                getCondition(request)[0], String.valueOf(i)));
                    }
                }
                responseMsg.append(NEXT_LINE);
            }
        }
        response.setContent(responseMsg.toString());
        setOperateInfo(request, FLOW_CODE_SELECT);
    }

    /**
     * 获取返回消息标题
     *
     * @param request
     * @param selectType
     * @return
     */
    private String getResponseTitle(SysWeChatTextModel request, String selectType, String businessType) {
        String responseMsg = STR_EMPTY;
        if (STR_4.equals(selectType) || STR_13.equals(selectType)) {
            responseMsg = String.format(WECHAT_ALL_TITLE, businessType) + NEXT_LINE + NEXT_LINE;
        } else if (STR_10.equals(selectType)) {
            responseMsg = WECHAT_GIFT_LAST_TITLE + NEXT_LINE + NEXT_LINE;
        } else {
            if (isLegalCondition(getCondition(request)[0], selectType)) {
                String condition = getCondition(request)[0];
                String format = FORMAT_DATE_YYYYMM;
                String formateChinese = FORMAT_DATE_YYYYMM_CHINESE;
                if (STR_2.equals(selectType) || STR_3.equals(selectType) || STR_11.equals(selectType) || STR_12.equals(selectType)) {
                    format = FORMAT_DATE_YYYY;
                    formateChinese = FORMAT_DATE_YYYY_CHINESE;
                }
                if (StringUtils.isBlank(condition)) {
                    condition = new SimpleDateFormat(formateChinese).format(new Date());
                } else {
                    try {
                        Date selectDate = new SimpleDateFormat(format).parse(condition);
                        condition = new SimpleDateFormat(formateChinese).format(selectDate);
                    } catch (ParseException e) {

                    }
                }
                responseMsg = String.format(WECHAT_BUSINESS_TITLE, condition, businessType) +  NEXT_LINE + NEXT_LINE;
            }
        }
        return responseMsg;
    }

    /**
     * 查询业务信息
     *
     * @return
     */
    private String getBusinessInfo(String userId, String userName, String selectType, String condition,
                                   String subType) {
        if (userName.contains(COLON_CHINESE)) {
            userName = userName.split(COLON_CHINESE)[1];
        }
        StringBuffer incomeInfo = new StringBuffer(userName).append(COLON).append(STR_SPACE);
        SysConsoleQueryModel sysConsoleQueryModel = new SysConsoleQueryModel();
        sysConsoleQueryModel.setUserId(userId);
        String amount = STR_0;
        if (StringUtils.isNotBlank(condition)) {
            if (!isLegalCondition(condition, selectType)) {
                incomeInfo = new StringBuffer(WECHAT_CONDITION_YYYYMM).append(NEXT_LINE);
                if (STR_2.equals(selectType) || STR_11.equals(selectType)) {
                    incomeInfo = new StringBuffer(WECHAT_CONDITION_YYYY).append(NEXT_LINE);
                }
                return incomeInfo.toString();
            } else {
                sysConsoleQueryModel.setQueryCondition(condition);
                sysConsoleQueryModel.setYearStartDate(condition);
                amount = executeBusinessSelect(sysConsoleQueryModel, selectType, subType);
            }
        } else {
            amount = executeBusinessSelect(sysConsoleQueryModel, selectType, subType);
        }
        incomeInfo.append(SysCommonUtils.formatValue(amount)).append(NEXT_LINE);
        return incomeInfo.toString();
    }

    /**
     * 执行业务信息查询
     *
     * @param sysConsoleQueryModel
     * @param selectType
     * @return
     */
    private String executeBusinessSelect(SysConsoleQueryModel sysConsoleQueryModel, String selectType, String subType) {
        String amount = STR_0;
        if (STR_1.equals(selectType)) {
            // 月度收入查询
            amount = sysConsoleDao.selectIncomeMonth(sysConsoleQueryModel);
        } else if (STR_2.equals(selectType)) {
            // 年度收入查询
            if (StringUtils.isBlank(sysConsoleQueryModel.getYearStartDate())) {
                String year = SysDateUtils.yyyy();
                sysConsoleQueryModel.setYearStartDate(year + STR_0 + STR_1);
            } else {
                sysConsoleQueryModel.setYearStartDate(sysConsoleQueryModel.getYearStartDate() + STR_0 + STR_1);
            }
            sysConsoleQueryModel.setIsCalc(STR_0);
            amount = sysConsoleDao.selectIncomeYear(sysConsoleQueryModel);
        } else if (STR_3.equals(selectType)) {
            // 本年度收入查询
            sysConsoleQueryModel.setIsCalc(STR_0);
            sysConsoleQueryModel.setYearStartDate(sysParameterService.getParameterString(YEAR_START_DATE));
            amount = sysConsoleDao.selectIncomeYear(sysConsoleQueryModel);
        } else if (STR_4.equals(selectType)) {
            // 总收入查询
            sysConsoleQueryModel.setIsCalc(STR_0);
            amount = sysConsoleDao.selectIncomeTotal(sysConsoleQueryModel);
        } else if (STR_10.equals(selectType)) {
            // 最近一次随礼查询
            if (STR_1.equals(subType)) {
                amount = sysConsoleDao.selectGiftSendLast(sysConsoleQueryModel);
            } else {
                amount = sysConsoleDao.selectGiftReceiveLast(sysConsoleQueryModel);
            }
        } else if (STR_11.equals(selectType)) {
            // 年度随礼查询
            if (StringUtils.isBlank(sysConsoleQueryModel.getYearStartDate())) {
                String year = SysDateUtils.yyyy();
                sysConsoleQueryModel.setYearStartDate(year + STR_0 + STR_1);
            } else {
                sysConsoleQueryModel.setYearStartDate(sysConsoleQueryModel.getYearStartDate() + STR_0 + STR_1);
            }
            sysConsoleQueryModel.setIsCalc(STR_0);
            if (STR_1.equals(subType)) {
                amount = sysConsoleDao.selectGiftSendYear(sysConsoleQueryModel);
            } else {
                amount = sysConsoleDao.selectGiftReceiveYear(sysConsoleQueryModel);
            }
        } else if (STR_12.equals(selectType)) {
            // 本年度随礼查询
            sysConsoleQueryModel.setIsCalc(STR_0);
            sysConsoleQueryModel.setYearStartDate(sysParameterService.getParameterString(YEAR_START_DATE));
            if (STR_1.equals(subType)) {
                amount = sysConsoleDao.selectGiftSendYear(sysConsoleQueryModel);
            } else {
                amount = sysConsoleDao.selectGiftReceiveYear(sysConsoleQueryModel);
            }
        } else if (STR_13.equals(selectType)) {
            // 总随礼查询
            sysConsoleQueryModel.setIsCalc(STR_0);
            if (STR_1.equals(subType)) {
                amount = sysConsoleDao.selectGiftSendTotal(sysConsoleQueryModel);
            } else {
                amount = sysConsoleDao.selectGiftReceiveTotal(sysConsoleQueryModel);
            }
        }
        return StringUtils.isBlank(amount) ? STR_0 : amount;
    }

    /**
     * 是否合法查询条件
     *
     * @param condition
     * @param selectType
     * @return
     */
    private boolean isLegalCondition(String condition, String selectType){
        if (StringUtils.isBlank(condition) || STR_3.equals(selectType) || STR_4.equals(selectType)
                || STR_10.equals(selectType) || STR_12.equals(selectType) || STR_13.equals(selectType)) {
            return true;
        }
        if (STR_9.equals(selectType)) {
            if (StringUtils.isBlank(getCondition(condition)[0])) {
                return false;
            } else {
                return true;
            }
        }
        boolean flag = true;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_YYYYMM);
        if (STR_1.equals(selectType)) {
            if (condition.length() != 6) {
                flag = false;
            }
        } else if (STR_2.equals(selectType) || STR_11.equals(selectType)) {
            simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_YYYY);
            if (condition.length() != 4) {
                flag = false;
            }
        }
        try {
            simpleDateFormat.parse(condition);
        } catch (ParseException e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 服务默认处理
     *
     * @param request
     * @param response
     */
    private void defaultReply(SysWeChatTextModel request, SysWeChatTextModel response) {
        SysWeChatFlowModel sysWeChatFlowModel = WECHAT_FLOW_LIST.get(getFlowCode(request));
        if (sysWeChatFlowModel != null) {
            response.setContent(sysWeChatFlowModel.getFlowTips());
        } else {
            response.setContent(new StringBuffer(WECHAT_SERVICE_NOT_EXIST).append(NEXT_LINE).append(WECHAT_MAIN_FLOW_LIST).toString());
        }
        setOperateInfo(request, FLOW_CODE_SELECT);
    }

    /**
     * 更多服务
     *
     * @param request
     * @param response
     */
    private void selectMoreService(SysWeChatTextModel request, SysWeChatTextModel response, boolean isMore) {
        if (isMore) {
            response.setContent(WECHAT_MAIN_FLOW_MORE_LIST);
        } else {
            response.setContent(WECHAT_MAIN_FLOW_LIST);
        }
        setOperateInfo(request, FLOW_CODE_SELECT);
    }

    /**
     * 绑定用户信息
     *
     * @param request
     * @param response
     */
    private void bindUserInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        String content = request.getContent().trim();
        SysUserQueryModel sysUserQueryModel = new SysUserQueryModel();
        sysUserQueryModel.setUserCode(content);
        List<SysUserModel> sysUserModelList = sysUserService.selectSysUser(sysUserQueryModel);
        if (CollectionUtils.isNotEmpty(sysUserModelList)) {
            SysWeChatUserModel sysWeChatUserModel = new SysWeChatUserModel();
            sysWeChatUserModel.setUserId(sysUserModelList.get(0).getUserId());
            updateSysWeChatUserInfo(request, sysWeChatUserModel);
            response.setContent(new StringBuffer(String.format(WECHAT_USER_BIND_SUCCESS, content)).append(NEXT_LINE).append(NEXT_LINE).append(WECHAT_MAIN_FLOW_LIST).toString());
            setOperateInfo(request, FLOW_CODE_SELECT);
        } else {
            response.setContent(WECHAT_USER_BIND_FAIL);
            setOperateInfo(request, FLOW_CODE_BIND);
        }
    }

    /**
     * 用户身份验证
     *
     * @param request
     * @param response
     */
    private void authUserInfo(SysWeChatTextModel request, SysWeChatTextModel response) {
        String content = request.getContent().trim();
        String weChatKey = sysParameterService.getParameterString(WECHAT_KEY);
        if (StringUtils.equals(content, weChatKey)) {
            SysWeChatUserModel sysWeChatUserModel = new SysWeChatUserModel();
            sysWeChatUserModel.setIsAuth(D013 + MINUS + STR_1);
            updateSysWeChatUserInfo(request, sysWeChatUserModel);
            response.setContent(WECHAT_USER_BIND);
            setOperateInfo(request, FLOW_CODE_BIND);
        } else {
            response.setContent(WECHAT_USER_AUTH_FAIL);
            setOperateInfo(request, FLOW_CODE_AUTH);
        }
    }

    /**
     * 获取当前操作状态
     *
     * @param sysWeChatBaseModel
     * @return
     */
    private String getOperateInfo(SysWeChatBaseModel sysWeChatBaseModel) {
        String userKey = sysWeChatBaseModel.getToUserName() + MINUS + sysWeChatBaseModel.getFromUserName();
        if (WECHAT_FLOW_OPERATE.containsKey(userKey)) {
            return WECHAT_FLOW_OPERATE.get(userKey).getOperateFlow();
        }
        return STR_EMPTY;
    }

    /**
     * 设置操作提示信息
     *
     * @param flowCode
     * @return
     */
    private void setOperateInfo(SysWeChatBaseModel sysWeChatBaseModel, String flowCode) {
        SysWeChatOperateModel sysWeChatOperateModel = new SysWeChatOperateModel();
        sysWeChatOperateModel.setOperateFlow(flowCode);
        sysWeChatOperateModel.setOperateTime(System.currentTimeMillis());
        String userKey = sysWeChatBaseModel.getToUserName() + MINUS + sysWeChatBaseModel.getFromUserName();
        if (StringUtils.isBlank(flowCode)) {
            WECHAT_FLOW_OPERATE.remove(userKey);
        } else {
            WECHAT_FLOW_OPERATE.put(userKey, sysWeChatOperateModel);
        }
    }

    /**
     * 服务是否开放
     *
     * @param flowCode
     * @return
     */
    private boolean isOpenService(String flowCode) {
        SysWeChatFlowModel sysWeChatFlowModel = WECHAT_FLOW_LIST.get(flowCode);
        if (sysWeChatFlowModel == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取流程步骤代码
     *
     * @param request
     * @return
     */
    private String getFlowCode(SysWeChatTextModel request) {
        String flowCode = null;
        String content = request.getContent().trim();
        if (StringUtils.isNotBlank(content)) {
            String[] message = content.split(BACKSLASH_S);
            if (message.length != 0) {
                if (WECHAT_FLOW_NUM_TO_CODE.containsKey(message[0])) {
                    flowCode = WECHAT_FLOW_NUM_TO_CODE.get(message[0]);
                } else {
                    flowCode = message[0];
                }
            }
        }
        return flowCode;
    }

    /**
     * 是否
     * @return
     */
    private void setOperateAfterFlow(SysWeChatBaseModel sysWeChatBaseModel, String flowCode) {
        String userKey = sysWeChatBaseModel.getToUserName() + MINUS + sysWeChatBaseModel.getFromUserName();
        SysWeChatOperateModel sysWeChatOperateModel = new SysWeChatOperateModel();
        sysWeChatOperateModel.setOperateTime(System.currentTimeMillis());
        if (sysParameterService.getParameterBoolean(WECHAT_OPERATE_BACK)) {
            flowCode = FLOW_CODE_SELECT;
        }
        sysWeChatOperateModel.setOperateFlow(flowCode);
        WECHAT_FLOW_OPERATE.put(userKey, sysWeChatOperateModel);
    }

    /**
     * 获取查询条件
     *
     * @param request
     * @return
     */
    private String[] getAllCondition(SysWeChatTextModel request) {
        String content = request.getContent().trim();
        if (StringUtils.isNotBlank(content)) {
            String[] message = content.split(BACKSLASH_S);
            if (message.length > 1) {
                return message;
            }
        }
        return new String[] {STR_EMPTY};
    }

    /**
     * 获取查询条件
     *
     * @param request
     * @return
     */
    private String[] getCondition(SysWeChatTextModel request) {
        String content = request.getContent().trim();
        if (StringUtils.isNotBlank(content)) {
            String[] message = content.split(BACKSLASH_S);
            if (message.length > 1) {
                return Arrays.copyOfRange(message, 1, message.length);
            }
        }
        return new String[] {STR_EMPTY};
    }

    /**
     * 获取查询条件
     *
     * @param content
     * @return
     */
    private String[] getCondition(String content) {
        if (StringUtils.isNotBlank(content)) {
            String[] message = content.split(BACKSLASH_S);
            if (message.length > 1) {
                return Arrays.copyOfRange(message, 1, message.length);
            }
        }
        return new String[] {STR_EMPTY};
    }

    /**
     * hash处理
     *
     * @param content
     * @param algorithm
     * @return
     */
    private String hash(String content, String algorithm) {
        if (content.isEmpty()) {
            return STR_EMPTY;
        }
        StringBuffer result = new StringBuffer();
        try {
            MessageDigest hash = MessageDigest.getInstance(algorithm);
            byte[] bytes = hash.digest(content.getBytes(UTF8));
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp =STR_0 + temp;
                }
                result.append(temp);
            }
        } catch (NoSuchAlgorithmException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_WECHAT, e);
        } catch (UnsupportedEncodingException e) {
            SysLogUtils.exception(logger, LOG_BUSINESS_TYPE_WECHAT, e);
        }
        return result.toString();
    }
}
