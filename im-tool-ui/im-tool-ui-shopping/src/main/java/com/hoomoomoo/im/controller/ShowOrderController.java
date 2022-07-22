package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.dto.ShoppingDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
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
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.*;
import static com.hoomoomoo.im.util.ShoppingCommonUtil.getPageInfo;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class ShowOrderController extends ShoppingBaseController implements Initializable {

    @FXML
    void execute(ActionEvent event) {
        super.execute(ShowOrderController.class, SHOW_ORDER);
    }

    @FXML
    void query(ActionEvent event) {
        super.executeQuery(ShowOrderController.class, SHOW_ORDER);
    }

    @FXML
    void pause(ActionEvent event) {
        super.executePause(ShowOrderController.class, SHOW_ORDER);
    }

    public static String getGoodsImgUrl(AppConfigDto appConfigDto, String goodsId) throws IOException {
        String imgUrl = SYMBOL_EMPTY;
        Connection connection = Jsoup.connect(appConfigDto.getJdShowOrderInfo() + "?productId=" + goodsId);
        ShoppingCommonUtil.initCookie(appConfigDto,connection);
        Document showOrder = connection.get();
        JSONObject showOrderInfo = JSONObject.parseObject(showOrder.select("body").text());
        if (showOrderInfo != null && !showOrderInfo.isEmpty()) {
            JSONArray images = (JSONArray)((JSONObject)showOrderInfo.get("imgComments")).get("imgList");
            if (images != null && !images.isEmpty()) {
                imgUrl = ((JSONObject) images.get(0)).get("imageUrl").toString();
            }
        }
        return imgUrl;
    }

    public static Document goodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdShowOrderExecute());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put(KEY_ORDER_ID, goodsDto.getOrderId());
        requestData.put(KEY_PRODUCT_ID, goodsDto.getGoodsId());
        requestData.put(KEY_IMGS, goodsDto.getAppraiseImgUrl());
        requestData.put(KEY_SAVE_STATUS, STR_3);
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
            Document showOrder = getShowOrder(appConfigDto, 1);
            orderNumValue = ShoppingCommonUtil.getWaitHandleNum(showOrder, STR_1);
            int page = getPageInfo(orderNumValue);
            for (int i=1; i<=page; i++) {
                showOrder = getShowOrder(appConfigDto, i);
                Elements orderList = showOrder.select("div.comt-plists div.comt-plist");
                for (Element order : orderList){
                    Elements orderInfo = order.select("div.pro-info");
                    String orderId = orderInfo.attr("oid");
                    String goodsId = orderInfo.attr("pid");
                    String goodsName = orderInfo.select("div.p-name a").text();
                    Elements operateList = orderInfo.select("div.op-btns a");
                    boolean isOperate = false;
                    for (Element operate : operateList) {
                        String operateName = operate.text();
                        if (NAME_JD_SHOW_ORDER.equals(operateName)) {
                            isOperate = true;
                        }
                    }
                    if (!isOperate) {
                        continue;
                    }
                    String imgUrl = getGoodsImgUrl(appConfigDto, goodsId);
                    GoodsDto goodsDto = new GoodsDto();
                    goodsDto.setOrderId(orderId);
                    goodsDto.setGoodsId(goodsId);
                    goodsDto.setGoodsName(goodsName);
                    goodsDto.setAppraiseImgUrl(imgUrl);
                    goodsDtoList.add(goodsDto);
                    OutputUtils.info(orderGoodsList, goodsDto);
                }
            }
            if (orderNum != null) {
                OutputUtils.info(orderNum, String.valueOf(orderNumValue));
            }
        } catch (IOException e) {
            LoggerUtils.info(e);
            ShoppingCommonUtil.info(appConfigDto, log, e.getMessage());
        }
        ShoppingDto shoppingDto = new ShoppingDto();
        shoppingDto.setGoodsDtoList(goodsDtoList);
        shoppingDto.setOrderNumValue(String.valueOf(orderNumValue));
        shoppingDto.setType(SHOW_ORDER.getCode());
        shoppingDto.setTypeName(SHOW_ORDER.getName());
        return shoppingDto;
    }

    public static Document getShowOrder(AppConfigDto appConfigDto, int page) throws IOException {
        return getQueryData(appConfigDto, appConfigDto.getJdShowOrder(), page);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.init(ShowOrderController.class, SHOW_ORDER);
    }
}
