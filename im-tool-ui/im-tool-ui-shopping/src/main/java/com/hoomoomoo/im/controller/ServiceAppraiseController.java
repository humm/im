package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.consts.BaseConst;
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
import org.apache.commons.lang3.StringUtils;
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
public class ServiceAppraiseController extends ShoppingBaseController implements Initializable {

    @FXML
    void execute(ActionEvent event) {
        super.execute(ServiceAppraiseController.class, SERVICE_APPRAISE);
    }

    @FXML
    void query(ActionEvent event) {
        super.executeQuery(ServiceAppraiseController.class, SERVICE_APPRAISE);
    }

    @FXML
    void pause(ActionEvent event) {
        super.executePause(ServiceAppraiseController.class, SERVICE_APPRAISE);
    }

    public static Document goodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        String type = serviceType(appConfigDto, goodsDto);
        if (StringUtils.isBlank(type)) {
            return null;
        }
        return goodsAppraise(appConfigDto, goodsDto, type);
    }

    public static String serviceType(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdServiceType() + "?sid=145&ot=0&payid=4&shipmentid=70&isCarShopOrder=&isDaojiaOrder=false&oid=" + goodsDto.getOrderId());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Document service = connection.get();
        JSONObject serviceInfo = JSONObject.parseObject(service.select("body").text());
        int serviceNum = 0;
         try {
             serviceNum =  ((JSONArray)(JSONObject.parseObject(((JSONArray)JSONObject.parseObject(serviceInfo.get("list").toString()).get("listGroup")).get(0).toString()).get("listGroup"))).size();
         } catch (Exception e) {
             LoggerUtils.info("获取服务评价类型错误 " + e.getMessage());
         }
        return serviceNum == 0 ? SYMBOL_EMPTY : serviceNum == 3 ? STR_1 : serviceNum == 5 ? STR_2 : SYMBOL_EMPTY;
    }
    public static Document goodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto, String type) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdServiceAppraiseExecute() + "?voteid=145&ruleid=" + goodsDto.getOrderId());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put("oid", goodsDto.getOrderId());
        requestData.put("gid", BaseConst.STR_69);
        requestData.put("sid", BaseConst.STR_549656);
        requestData.put("stid", BaseConst.STR_0);
        requestData.put("tags", BaseConst.SYMBOL_EMPTY);
        if (STR_1.equals(type)) {
            requestData.put("ro1827", "1827A1");
            requestData.put("ro1828", "1828A1");
            requestData.put("ro1829", "1829A1");
        } else {
            requestData.put("ro591", "591A1");
            requestData.put("ro592", "592A1");
            requestData.put("ro593", "593A1");
            requestData.put("ro899", "899A1");
            requestData.put("ro900", "900A1");
        }
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
            Document serviceAppraise = getServiceAppraise(appConfigDto, 1);
            orderNumValue = ShoppingCommonUtil.getWaitHandleNum(serviceAppraise, STR_4);
            int page = getPageInfo(orderNumValue);
            for (int i=1; i<=page; i++) {
                serviceAppraise = getServiceAppraise(appConfigDto, i);
                Elements orderList = serviceAppraise.select("table.td-void.order-tb tbody tr.tr-bd");
                for (Element order : orderList) {
                    Elements goodsIdEle = order.select("div.goods-item div.p-name a");
                    String goodsIdHref = goodsIdEle.attr(KEY_HREF);
                    String goodsName = goodsIdEle.text();
                    String goodsId = ShoppingCommonUtil.getHrefId(goodsIdHref);
                    String orderId = SYMBOL_EMPTY;
                    Elements operateList = order.select("div.operate a");
                    boolean isOperate = false;
                    for (Element operate : operateList) {
                        String operateName = operate.text();
                        if (NAME_SERVICE_APPRAISE.equals(operateName)) {
                            isOperate = true;
                        }
                        if (NAME_ORDER_DETAIL.equals(operateName)) {
                            orderId = operate.attr("oid");
                        }
                    }
                    if (!isOperate) {
                        continue;
                    }
                    GoodsDto goodsDto = new GoodsDto();
                    goodsDto.setOrderId(orderId);
                    goodsDto.setGoodsId(goodsId);
                    goodsDto.setGoodsName(goodsName);
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
        shoppingDto.setOrderNumValue(orderNumValue);
        shoppingDto.setType(SERVICE_APPRAISE.getCode());
        shoppingDto.setTypeName(SERVICE_APPRAISE.getName());
        return shoppingDto;
    }

    public static Document getServiceAppraise(AppConfigDto appConfigDto, int page) throws IOException {
        return getQueryData(appConfigDto, appConfigDto.getJdServiceAppraise(), page);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.init(ServiceAppraiseController.class, SERVICE_APPRAISE);
    }
}
