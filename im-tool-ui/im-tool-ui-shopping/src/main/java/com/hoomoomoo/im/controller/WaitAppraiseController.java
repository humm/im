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
import static com.hoomoomoo.im.consts.FunctionConfig.PROCESS_INFO;
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

    @FXML
    private Button query;

    private List<GoodsDto> goodsDtoList;

    private int orderNumValue;

    private int goodsNumValue;

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
            updateProgress(0.005);
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
                ComponentUtils.setButtonDisabled(execute, query);
                initWaitAppraise(ConfigCache.getConfigCache().getAppConfigDto(), true);
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
                ComponentUtils.setButtonDisabled(execute, query);
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
                    GoodsDto success = new GoodsDto();
                    success.setGoodsName(NAME_APPRAISE_COMPLETE);
                    OutputUtils.info(log, success);
                } else {
                    GoodsDto noGoods = new GoodsDto();
                    noGoods.setGoodsName(NAME_NO_APPRAISE_GOODS);
                    OutputUtils.info(log, noGoods);
                }
                LoggerUtils.writeWaitAppraiseInfo(currentDate, logs);
                setProgress(1);
                initWaitAppraise(appConfigDto, false);
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
            }
        }
        return appraiseMsg;
    }

    public static Document doServiceAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        doServiceAppraise(appConfigDto, goodsDto, STR_1);
        return doServiceAppraise(appConfigDto, goodsDto, STR_2);
    }

    private static Document doServiceAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto, String type) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppraiseWaitService() + "?voteid=145&ruleid=" + goodsDto.getOrderId());
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
            if (waitAppraise.text().contains(NAME_JD_LOGIN)) {
                GoodsDto fail = new GoodsDto();
                fail.setGoodsName(NAME_JD_LOGIN_FAIL);
                OutputUtils.info(log, fail);
                return;
            }
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
                    String goodsHref = goods.attr(KEY_HREF);
                    String goodsId = ShoppingCommonUtil.getHrefId(goodsHref);
                    String appraiseInfo = getGoodsAppraiseInfo(appConfigDto, goodsId);
                    GoodsDto goodsDto = new GoodsDto();
                    goodsDto.setOrderId(orderId);
                    goodsDto.setGoodsId(goodsId);
                    goodsDto.setGoodsName(goodsName);
                    goodsDto.setAppraiseInfo(appraiseInfo);
                    goodsDtoList.add(goodsDto);
                    this.goodsNumValue++;
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
            if (appConfigDto.getJdInitQuery()) {
                initWaitAppraise(appConfigDto, true);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
