package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.dto.ShoppingDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
import com.hoomoomoo.im.utils.ComponentUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.WAIT_APPRAISE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/13
 */
public class ShoppingBaseController extends BaseController{

    @FXML
    public Label orderNum;

    @FXML
    public Label userName;

    @FXML
    public TableView orderGoodsList;

    @FXML
    public TableView log;

    @FXML
    public Button execute;

    @FXML
    public Button query;

    @FXML
    public Button pause;

    public List<GoodsDto> goodsDtoList;

    public Integer orderNumValue = 0;

    private Object instance;

    private Object instanceService;

    public Boolean pauseStatus;

    protected void executePause(Class clazz, FunctionConfig functionConfig) {
        pauseStatus = true;
        ShoppingCommonUtil.info(log, NAME_PAUSE_START);
        schedule.requestFocus();
        ComponentUtils.setButtonDisabled(pause);
    }

    protected void executeQuery(Class clazz, FunctionConfig functionConfig) {
        new Thread(() -> {
            try {
                setProgress(0);
                updateProgress(0.01);
                ComponentUtils.setButtonDisabled(execute, query, pause);
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    query(appConfigDto, clazz, true);
                }
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                ShoppingCommonUtil.info(log, e.getMessage());
            } finally {
                ComponentUtils.setButtonEnabled(execute, query, pause);
            }
        }).start();
    }

    protected void execute(Class clazz, FunctionConfig functionConfig) {
        new Thread(() -> {
            try {
                OutputUtils.clearLog(log);
                LoggerUtils.info(String.format(BaseConst.MSG_USE, functionConfig.getName()));
                if (!ShoppingCommonUtil.checkConfig(log, functionConfig.getCode())) {
                    return;
                }
                setProgress(0);
                this.doExecute(clazz, functionConfig);
            } catch (Exception e) {
                LoggerUtils.info(e);
                ShoppingCommonUtil.info(log, e.getMessage());
            }
        }).start();
    }

    protected void doExecute(Class clazz, FunctionConfig functionConfig) {
        AppConfigDto appConfigDto = null;
        try {
            pauseStatus = false;
            int currentNum = orderNumValue;
            appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            appConfigDto.setExecuteType(functionConfig.getCode());
            if(!ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                return;
            }
            Method goodsAppraise = clazz.getMethod(METHOD_GOODS_APPRAISE, AppConfigDto.class, GoodsDto.class);
            Method serviceAppraise = ServiceAppraiseController.class.getMethod(METHOD_GOODS_APPRAISE, AppConfigDto.class, GoodsDto.class);
            ComponentUtils.setButtonDisabled(execute, query);
            List<String> logs = new ArrayList<>();
            Date currentDate = new Date();
            if (CollectionUtils.isNotEmpty(goodsDtoList)) {
                for (GoodsDto goodsDto : goodsDtoList) {
                    if (pauseStatus) {
                        break;
                    }
                    ShoppingCommonUtil.appraiseStart(appConfigDto, log, logs, goodsDto);
                    if (StringUtils.isBlank(goodsDto.getGoodsId())) {
                        ShoppingCommonUtil.goodsNotExists(appConfigDto, log, logs, (GoodsDto) BeanUtils.cloneBean(goodsDto));
                        continue;
                    }
                    goodsAppraise.invoke(instance, appConfigDto, goodsDto);
                    if (WAIT_APPRAISE.getCode().equals(functionConfig.getCode())) {
                        serviceAppraise.invoke(instance, appConfigDto, goodsDto);
                    }
                    ShoppingCommonUtil.appraiseComplete(appConfigDto, log, logs, (GoodsDto) BeanUtils.cloneBean(goodsDto));
                    orderNumValue--;
                    OutputUtils.info(orderNum, String.valueOf(orderNumValue));
                    setProgress(new BigDecimal(currentNum - orderNumValue).divide(new BigDecimal(currentNum), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    ShoppingCommonUtil.restMoment(appConfigDto, log);
                }
            } else {
                ShoppingCommonUtil.noAppraiseGoods(appConfigDto, log);
            }
            LoggerUtils.writeLogInfo(functionConfig.getCode(), currentDate, logs);
            query(appConfigDto, clazz, false);
            if (pauseStatus) {
                ShoppingCommonUtil.info(log, NAME_PAUSE_COMPLETE);
                ComponentUtils.setButtonEnabled(pause);
                return;
            }
            if (orderNumValue > 0 && currentNum != orderNumValue) {
                doExecute(clazz, functionConfig);
            } else {
               if(CollectionUtils.isNotEmpty(goodsDtoList)) {
                    ShoppingCommonUtil.appraiseComplete(appConfigDto, log);
                }
                setProgress(1);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            ShoppingCommonUtil.info(log, e.getMessage());
        } finally {
            if (appConfigDto != null) {
                appConfigDto.setExecuteType(SYMBOL_EMPTY);
            }
            ComponentUtils.setButtonEnabled(execute, query);
        }
    }

    protected void init(Class clazz, FunctionConfig functionConfig) {
        try {
            instance = clazz.newInstance();
            instanceService = ServiceAppraiseController.class.newInstance();
            pauseStatus = false;
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (appConfigDto.getJdInitQuery()) {
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    query(appConfigDto, clazz, true);
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    protected void query(AppConfigDto appConfigDto, Class clazz, Boolean initLog) throws Exception {
        Method queryData = clazz.getMethod(METHOD_QUERY_DATA, AppConfigDto.class, Boolean.class, TableView.class, TableView.class, Label.class);
        ShoppingDto shoppingDto = (ShoppingDto)queryData.invoke(instance, appConfigDto, initLog, orderGoodsList, log, orderNum);
        orderNumValue = Integer.valueOf(shoppingDto.getOrderNumValue());
        goodsDtoList = shoppingDto.getGoodsDtoList();
    }

    protected static Document getQueryData(AppConfigDto appConfigDto, String requestUrl, int page) throws IOException {
        Connection connection = Jsoup.connect(requestUrl + "&page=" + page);
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        return connection.get();
    }
}
