package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
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

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class WaitAppraiseController extends BaseController implements Initializable {

    @FXML
    private Label orderNum;

    @FXML
    private Label goodsNum;

    @FXML
    private TableView<?> orderGoodsList;

    @FXML
    private TableView<?> log;

    @FXML
    private Button execute;

    private List<GoodsDto> goodsDtoList;

    private int orderNumValue;

    private int goodsNumValue;

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, WAIT_APPRAISE.getName()));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            setProgress(0);
            this.execute(appConfigDto);
            updateProgress(0.01);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(log, e.toString());
        }
    }

    private void execute(AppConfigDto appConfigDto) throws Exception {
        new Thread(() -> {
            try {
                execute.setDisable(true);
                initWaitAppraise(appConfigDto, true);
                List<String> logs = new ArrayList<>();
                Date currentDate = new Date();
                if (CollectionUtils.isNotEmpty(goodsDtoList)) {
                    for (GoodsDto goodsDto : goodsDtoList) {
                        goodsDto.setStatus(NAME_APPRAISEING);
                        OutputUtils.info(log, goodsDto);
                        ShoppingCommonUtil.initLogs(logs, goodsDto);
                        doGoodsAppraise(appConfigDto, goodsDto);
                        doServiceAppraise(appConfigDto, goodsDto);
                        Thread.sleep(Integer.valueOf(appConfigDto.getJdIntervalTime()) * 1000);
                        GoodsDto goods = (GoodsDto)BeanUtils.cloneBean(goodsDto);
                        goods.setStatus(NAME_APPRAISE_SUCCESS);
                        OutputUtils.info(log, goods);
                        ShoppingCommonUtil.initLogs(logs, goods);
                        this.goodsNumValue--;
                        OutputUtils.info(goodsNum, String.valueOf(goodsNumValue));
                        OutputUtils.info(orderNum, String.valueOf(ShoppingCommonUtil.getOrderNum(goodsDtoList)));
                    }
                }
                GoodsDto success = new GoodsDto();
                success.setOrderId(STR_999999999999);
                success.setGoodsId(STR_999999999999);
                success.setGoodsName(STR_999999999999);
                success.setStatus(NAME_APPRAISE_COMPLETE);
                OutputUtils.info(log, success);
                LoggerUtils.writeWaitAppraiseInfo(currentDate, logs);
                setProgress(1);
                initWaitAppraise(appConfigDto, false);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.toString());
            } finally {
                execute.setDisable(false);
            }
        }).start();
    }

    private static String getGoodsAppraiseInfo(AppConfigDto appConfigDto, String goodsId) throws IOException {
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
            }
        }
        return appraiseMsg;
    }

    private static Document doServiceAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseWaitService() + "?voteid=145&ruleid=" + goodsDto.getOrderId());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put("oid", goodsDto.getOrderId());
        requestData.put("gid", BaseConst.STR_69);
        requestData.put("sid", BaseConst.STR_549656);
        requestData.put("stid", BaseConst.STR_0);
        requestData.put("tags", BaseConst.SYMBOL_EMPTY);
        requestData.put("ro1827", "1827A1");
        requestData.put("ro1828", "1828A1");
        requestData.put("ro591", "591A1");
        requestData.put("ro592", "592A1");
        requestData.put("ro593", "593A1");
        requestData.put("ro899", "899A1");
        requestData.put("ro900", "900A1");
        connection.data(requestData);
        return connection.post();
    }

    private static Document doGoodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseWaitGoods());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put("orderId", goodsDto.getOrderId());
        requestData.put("productId", goodsDto.getGoodsId());
        requestData.put("score", BaseConst.STR_5);
        requestData.put("saveStatus", BaseConst.STR_1);
        requestData.put("anonymousFlag", BaseConst.STR_1);
        requestData.put("content", URLEncoder.encode(goodsDto.getAppraiseInfo()));
        connection.data(requestData);
        return connection.post();
    }

    private void initWaitAppraise(AppConfigDto appConfigDto, boolean initLog) {
        orderNumValue = 0;
        goodsNumValue = 0;
        OutputUtils.clearLog(orderGoodsList);
        if (initLog) {
            OutputUtils.clearLog(log);
        }
        goodsDtoList = new ArrayList<>();
        try {
            Document waitAppraise = getWaitAppraise(appConfigDto);
            Elements orderList = waitAppraise.select("table.td-void.order-tb tbody");
            for (Element order : orderList){
                String orderId = order.select("tr td span.number a").text();
                Elements operateList = order.select("tr td div.operate a");
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
                this.orderNumValue++;
                Elements goodsList = order.select("tr td div.p-name a");
                for (Element goods : goodsList) {
                    String goodsName = goods.text();
                    String goodsHref = goods.attr("href");
                    int indexStart = goodsHref.lastIndexOf(BaseConst.SYMBOL_SLASH);
                    int indexEnd = goodsHref.lastIndexOf(BaseConst.SYMBOL_POINT);
                    String goodsId = BaseConst.SYMBOL_EMPTY;
                    if (indexEnd != -1 && indexEnd > indexStart) {
                        goodsId = goodsHref.substring(indexStart + 1, indexEnd);
                    }
                    String appraiseInfo = getGoodsAppraiseInfo(appConfigDto, goodsId);
                    GoodsDto goodsDto = new GoodsDto();
                    goodsDto.setOrderId(orderId);
                    goodsDto.setGoodsId(goodsId);
                    goodsDto.setGoodsName(goodsName);
                    goodsDto.setAppraiseInfo(appraiseInfo);
                    goodsDtoList.add(goodsDto);
                    goodsNumValue++;
                    OutputUtils.info(orderGoodsList, goodsDto);
                }
            }
            OutputUtils.info(orderNum, String.valueOf(orderNumValue));
            OutputUtils.info(goodsNum, String.valueOf(goodsNumValue));
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
            initWaitAppraise(appConfigDto, true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
