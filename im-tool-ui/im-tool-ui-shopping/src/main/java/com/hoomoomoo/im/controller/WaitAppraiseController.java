package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.dto.ShoppingDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
import com.hoomoomoo.im.utils.ComponentUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.WAIT_APPRAISE;
import static com.hoomoomoo.im.util.ShoppingCommonUtil.getPageInfo;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class WaitAppraiseController extends ShoppingBaseController implements Initializable {

    @FXML
    void execute(ActionEvent event) {
        super.execute(WaitAppraiseController.class, WAIT_APPRAISE);
    }

    @FXML
    void query(ActionEvent event) {
        super.executeQuery(WaitAppraiseController.class, WAIT_APPRAISE);
    }

    @FXML
    void pause(ActionEvent event) {
        super.executePause(WaitAppraiseController.class, WAIT_APPRAISE);
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
                int appraiseSize = appraiseList.size();
                if (appraiseSize < appraiseNum) {
                    appraiseMsg = ((JSONObject) appraiseList.get(appraiseSize - 1)).get("content").toString();
                } else {
                    appraiseMsg = BaseConst.SYMBOL_EMPTY;
                    for (int i=appraiseSize-1; i>=appraiseSize-appraiseNum; i--) {
                        appraiseMsg += ((JSONObject) appraiseList.get(i)).get("content").toString() + BaseConst.SYMBOL_NEXT_LINE;
                    }
                }
                if (appraiseMsg.length() < JD_APPRAISE_LENGTH_MIN) {
                    appraiseMsg = appConfigDto.getJdAppraiseDefault() + BaseConst.SYMBOL_NEXT_LINE + appraiseMsg;
                } else if (appraiseMsg.length() > JD_APPRAISE_LENGTH_MAX) {
                    appraiseMsg = appraiseMsg.substring(0, JD_APPRAISE_LENGTH_MAX);
                }
            }
        }
        return appraiseMsg;
    }

    public static Document goodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseWaitGoods());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put(KEY_ORDER_ID, goodsDto.getOrderId());
        requestData.put(KEY_PRODUCT_ID, goodsDto.getGoodsId());
        requestData.put(KEY_SCORE, BaseConst.STR_5);
        requestData.put(KEY_SAVE_STATUS, BaseConst.STR_1);
        requestData.put(KEY_ANONYMOUS_FLAG, BaseConst.STR_1);
        requestData.put(KEY_CONTENT, URLEncoder.encode(goodsDto.getAppraiseInfo(), ENCODING_UTF8));
        connection.data(requestData);
        return connection.post();
    }

    public static ShoppingDto queryData(AppConfigDto appConfigDto, Boolean initLog, TableView orderGoodsList, TableView log, Label orderNum) {
        int orderNumValue = 0;
        OutputUtils.clearLog(orderGoodsList);
        if (initLog) {
            OutputUtils.clearLog(log);
        }
        List<GoodsDto> goodsDtoList = new ArrayList<>();
        try {
            Document waitAppraise = getWaitAppraise(appConfigDto, 1);
            orderNumValue = ShoppingCommonUtil.getWaitHandleNum(waitAppraise, STR_0);
            int page = getPageInfo(orderNumValue);
            for (int i=1; i<=page; i++) {
                waitAppraise = getWaitAppraise(appConfigDto, i);
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
            }
            if (orderNum != null) {
                OutputUtils.info(orderNum, String.valueOf(orderNumValue));
            }
        } catch (IOException e) {
            LoggerUtils.info(e);
            ShoppingCommonUtil.info(appConfigDto, log, e.getMessage());
        }
        com.hoomoomoo.im.dto.ShoppingDto shoppingDto = new ShoppingDto();
        shoppingDto.setGoodsDtoList(goodsDtoList);
        shoppingDto.setOrderNumValue(String.valueOf(orderNumValue));
        shoppingDto.setType(WAIT_APPRAISE.getCode());
        shoppingDto.setTypeName(WAIT_APPRAISE.getName());
        return shoppingDto;
    }

    public static Document getWaitAppraise(AppConfigDto appConfigDto, int page) throws IOException {
        return getQueryData(appConfigDto, appConfigDto.getJdAppraiseWait(), page);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.init(WaitAppraiseController.class, WAIT_APPRAISE);
    }
}
