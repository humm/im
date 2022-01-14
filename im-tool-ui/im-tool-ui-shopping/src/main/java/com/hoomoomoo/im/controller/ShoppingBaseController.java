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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

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

    private List<GoodsDto> goodsDtoList;

    private Integer orderNumValue = 0;

    private Object instance;

    private Object instanceService;

    protected void executeQuery(Class clazz, String type) {
        new Thread(() -> {
            try {
                setProgress(0);
                updateProgress(0.01);
                ComponentUtils.setButtonDisabled(execute, query);
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    query(appConfigDto, clazz, true);
                }
                ComponentUtils.setButtonEnabled(execute, query);
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.toString());
            } finally {
                ComponentUtils.setButtonEnabled(execute, query);
            }
        }).start();
    }

    protected void execute(Class clazz, String type, FunctionConfig functionConfig) {
        new Thread(() -> {
            try {
                OutputUtils.clearLog(log);
                LoggerUtils.info(String.format(BaseConst.MSG_USE, functionConfig.getName()));
                if (!ShoppingCommonUtil.checkConfig(log, functionConfig.getCode())) {
                    return;
                }
                setProgress(0);
                this.doExecute(clazz, functionConfig, type);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.toString());
            }
        }).start();
    }

    protected void doExecute(Class clazz, FunctionConfig functionConfig, String type) {
        try {
            int currentNum = orderNumValue;
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
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
                    goodsDto.setStatus(NAME_APPRAISEING);
                    OutputUtils.info(log, goodsDto);
                    ShoppingCommonUtil.initLogs(logs, goodsDto);
                    if (StringUtils.isBlank(goodsDto.getGoodsId())) {
                        GoodsDto goods = (GoodsDto) BeanUtils.cloneBean(goodsDto);
                        goods.setGoodsId(NAME_GOODS_NOT_EXIST);
                        goods.setStatus(NAME_APPRAISE_FAIL);
                        OutputUtils.info(log, goods);
                        ShoppingCommonUtil.initLogs(logs, goods);
                        continue;
                    }
                    goodsAppraise.invoke(instance, appConfigDto, goodsDto);
                    if (STR_0.equals(type)) {
                        serviceAppraise.invoke(instance, appConfigDto, goodsDto);
                    }
                    GoodsDto goods = (GoodsDto) BeanUtils.cloneBean(goodsDto);
                    goods.setStatus(NAME_APPRAISE_SUCCESS);
                    OutputUtils.info(log, goods);
                    ShoppingCommonUtil.initLogs(logs, goods);
                    orderNumValue--;
                    OutputUtils.info(orderNum, String.valueOf(orderNumValue));
                    setProgress(new BigDecimal(currentNum - orderNumValue).divide(new BigDecimal(currentNum), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    ShoppingCommonUtil.restMoment(appConfigDto, log);
                }
            } else {
                ShoppingCommonUtil.noAppraiseGoods(appConfigDto, log);
            }
            LoggerUtils.writeAppraiseInfo(functionConfig.getCode(), currentDate, logs);
            query(appConfigDto, clazz, false);
            if (orderNumValue > 0 && currentNum != orderNumValue) {
                doExecute(clazz, functionConfig, type);
            } else {
               if(CollectionUtils.isNotEmpty(goodsDtoList)) {
                    ShoppingCommonUtil.appraiseComplete(appConfigDto, log);
                }
                setProgress(1);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.toString());
        } finally {
            ComponentUtils.setButtonEnabled(execute, query);
        }
    }

    protected void init(Class clazz, String type) {
        try {
            if (instance == null) {
                instance = clazz.newInstance();
                instanceService = ServiceAppraiseController.class.newInstance();
            }
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
        orderNumValue = shoppingDto.getOrderNumValue();
        goodsDtoList = shoppingDto.getGoodsDtoList();
    }
}
