package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
import com.hoomoomoo.im.utils.ComponentUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.WAIT_APPRAISE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class WaitAppraiseController extends ShoppingBaseController implements Initializable {

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, WAIT_APPRAISE.getName()));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!ShoppingCommonUtil.checkConfig(log, WAIT_APPRAISE.getCode())) {
                return;
            }
            setProgress(0);
            this.execute(appConfigDto);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.toString());
        }
    }

    @FXML
    void query(ActionEvent event) {
        new Thread(() -> {
            try {
                setProgress(0);
                updateProgress(0.01);
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                ComponentUtils.setButtonDisabled(execute, query);
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    initWaitAppraise(appConfigDto, true);
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

    private void execute(AppConfigDto appConfigDto) throws Exception {
        new Thread(() -> {
            try {
                int currentNum = orderNumValue;
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    return;
                }
                ComponentUtils.setButtonDisabled(execute, query);
                List<String> logs = new ArrayList<>();
                Date currentDate = new Date();
                if (CollectionUtils.isNotEmpty(goodsDtoList)) {
                    for (GoodsDto goodsDto : goodsDtoList) {
                        goodsDto.setStatus(NAME_APPRAISEING);
                        OutputUtils.info(log, goodsDto);
                        ShoppingCommonUtil.initLogs(logs, goodsDto);
                        if (StringUtils.isBlank(goodsDto.getGoodsId())) {
                            GoodsDto goods = (GoodsDto)BeanUtils.cloneBean(goodsDto);
                            goods.setGoodsId(NAME_GOODS_NOT_EXIST);
                            goods.setStatus(NAME_APPRAISE_FAIL);
                            OutputUtils.info(log, goods);
                            ShoppingCommonUtil.initLogs(logs, goods);
                            continue;
                        }
                        doGoodsAppraise(appConfigDto, goodsDto);
                        ServiceAppraiseController.doServiceAppraise(appConfigDto, goodsDto);
                        GoodsDto goods = (GoodsDto)BeanUtils.cloneBean(goodsDto);
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
                LoggerUtils.writeWaitAppraiseInfo(currentDate, logs);
                initWaitAppraise(appConfigDto, false);
                if (orderNumValue > 0 && currentNum != orderNumValue) {
                    execute(appConfigDto);
                } else {
                    ShoppingCommonUtil.appraiseComplete(appConfigDto, log);
                    setProgress(1);
                }
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.toString());
            } finally {
                ComponentUtils.setButtonEnabled(execute, query);
            }
        }).start();
    }

    public static String getGoodsAppraiseInfo(AppConfigDto appConfigDto, String goodsId) throws IOException {
        int appraiseNum = Integer.valueOf(appConfigDto.getJdAppraiseNum());
        String appraiseMsg = appConfigDto.getJdAppraiseDefault();
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseInfo() + "?productId=" + goodsId + "&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&rid=0&fold=1");
        ShoppingCommonUtil.initCookie(appConfigDto,connection);
        Document appraise = connection.get();
        JSONObject appraiseInfo = JSONObject.parseObject(appraise.select("body").text());
        if (appraiseInfo != null && !appraiseInfo.isEmpty()) {
            JSONArray appraiseList = (JSONArray)appraiseInfo.get("comments");
            if (appraiseList != null && !appraiseList.isEmpty()) {
                if (appraiseList.size() < appraiseNum) {
                    appraiseMsg = ((JSONObject) appraiseList.get(0)).get("content").toString();
                } else {
                    appraiseMsg = BaseConst.SYMBOL_EMPTY;
                    for (int i=0; i<appraiseNum; i++) {
                        appraiseMsg += ((JSONObject) appraiseList.get(i)).get("content").toString() + BaseConst.SYMBOL_NEXT_LINE;
                    }
                }
                if (appraiseMsg.length() < JD_APPRAISE_LENGTH_MIN) {
                    appraiseMsg += BaseConst.SYMBOL_NEXT_LINE + appConfigDto.getJdAppraiseDefault();
                } else if (appraiseMsg.length() > JD_APPRAISE_LENGTH_MAX) {
                    appraiseMsg = appraiseMsg.substring(0, JD_APPRAISE_LENGTH_MAX);
                }
            }
        }
        return appraiseMsg;
    }

    private static Document doGoodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseWaitGoods());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put(KEY_ORDER_ID, goodsDto.getOrderId());
        requestData.put(KEY_PRODUCT_ID, goodsDto.getGoodsId());
        requestData.put(KEY_SCORE, BaseConst.STR_5);
        requestData.put(KEY_SAVE_STATUS, BaseConst.STR_1);
        requestData.put(KEY_ANONYMOUS_FLAG, BaseConst.STR_1);
        requestData.put(KEY_CONTENT, URLEncoder.encode(goodsDto.getAppraiseInfo()));
        connection.data(requestData);
        return connection.post();
    }

    private void initWaitAppraise(AppConfigDto appConfigDto, boolean initLog) {
        orderNumValue = 0;
        OutputUtils.clearLog(orderGoodsList);
        if (initLog) {
            OutputUtils.clearLog(log);
        }
        goodsDtoList = new ArrayList<>();
        try {
            Document waitAppraise = getWaitAppraise(appConfigDto);
            orderNumValue = ShoppingCommonUtil.getWaitHandleNum(waitAppraise, STR_0);
            Elements orderList = waitAppraise.select("table.td-void.order-tb tbody");
            for (Element order : orderList){
                String orderId = order.select("tr td span.number a").text();
                Elements operateList = order.select("tr td div.operate a");
                orderNumValue--;
                boolean isOperate = false;
                for (Element operate : operateList) {
                    String operateName = operate.text();
                    if (NAME_APPRAISE.equals(operateName)) {
                        isOperate = true;
                    }
                }
                if (!isOperate) {
                    continue;
                }
                Elements goodsList = order.select("tr td div.p-name a");
                for (Element goods : goodsList) {
                    orderNumValue++;
                    String goodsName = goods.text();
                    String goodsHref = goods.attr(KEY_HREF);
                    String goodsId = ShoppingCommonUtil.getHrefId(goodsHref);
                    String appraiseInfo = getGoodsAppraiseInfo(appConfigDto, goodsId);
                    GoodsDto goodsDto = new GoodsDto();
                    goodsDto.setOrderId(orderId);
                    goodsDto.setGoodsId(goodsId);
                    goodsDto.setGoodsName(goodsName);
                    goodsDto.setAppraiseInfo(appraiseInfo);
                    goodsDtoList.add(goodsDto);
                    OutputUtils.info(orderGoodsList, goodsDto);
                }
            }
            OutputUtils.info(orderNum, String.valueOf(orderNumValue));
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
    }

    private static Document getWaitAppraise(AppConfigDto appConfigDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseWait());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        return connection.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (appConfigDto.getJdInitQuery()) {
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    initWaitAppraise(appConfigDto, true);
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
