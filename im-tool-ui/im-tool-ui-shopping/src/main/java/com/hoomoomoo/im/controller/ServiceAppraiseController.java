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
import org.apache.commons.lang3.StringUtils;
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
import static com.hoomoomoo.im.consts.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class ServiceAppraiseController extends BaseController implements Initializable {

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
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SHOW_ORDER.getName()));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!ShoppingCommonUtil.checkConfig(log, SERVICE_APPRAISE.getCode())) {
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
                initServiceAppraise(ConfigCache.getConfigCache().getAppConfigDto(), true);
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
                initServiceAppraise(appConfigDto, true);
                List<String> logs = new ArrayList<>();
                Date currentDate = new Date();
                if (CollectionUtils.isNotEmpty(goodsDtoList)) {
                    for (GoodsDto goodsDto : goodsDtoList) {
                        goodsDto.setStatus(NAME_APPRAISEING);
                        OutputUtils.info(log, goodsDto);
                        ShoppingCommonUtil.initLogs(logs, goodsDto);
                        WaitAppraiseController.doServiceAppraise(appConfigDto, goodsDto);
                        Thread.sleep(Integer.valueOf(appConfigDto.getJdIntervalTime()) * 1000);
                        GoodsDto goods = (GoodsDto)BeanUtils.cloneBean(goodsDto);
                        goods.setStatus(NAME_APPRAISE_SUCCESS);
                        OutputUtils.info(log, goods);
                        ShoppingCommonUtil.initLogs(logs, goods);
                        this.orderNumValue--;
                        this.goodsNumValue--;
                        OutputUtils.info(goodsNum, String.valueOf(goodsNumValue));
                        OutputUtils.info(orderNum, String.valueOf(orderNumValue));
                        break;
                    }
                    GoodsDto success = new GoodsDto();
                    success.setGoodsName(NAME_APPRAISE_COMPLETE);
                    OutputUtils.info(log, success);
                } else {
                    GoodsDto noGoods = new GoodsDto();
                    noGoods.setGoodsName(NAME_NO_APPRAISE_GOODS);
                    OutputUtils.info(log, noGoods);
                }
                LoggerUtils.writeServiceAppraise(currentDate, logs);
                setProgress(1);
                initServiceAppraise(appConfigDto, false);
            } catch (Exception e) {
                LoggerUtils.info(e);
                OutputUtils.info(log, e.toString());
            } finally {
                ComponentUtils.setButtonEnabled(execute, query);
            }
        }).start();
    }

    private void initServiceAppraise(AppConfigDto appConfigDto, boolean initLog) {
        orderNumValue = 0;
        goodsNumValue = 0;
        OutputUtils.clearLog(orderGoodsList);
        if (initLog) {
            OutputUtils.clearLog(log);
        }
        goodsDtoList = new ArrayList<>();
        try {
            Document showOrder = getServiceAppraise(appConfigDto);
            if (showOrder.text().contains(NAME_JD_LOGIN)) {
                GoodsDto fail = new GoodsDto();
                fail.setGoodsName(NAME_JD_LOGIN_FAIL);
                OutputUtils.info(log, fail);
                return;
            }
            Elements orderList = showOrder.select("table.td-void.order-tb tbody tr.tr-bd");
            for (Element order : orderList){
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
                this.orderNumValue++;
                this.goodsNumValue++;
                GoodsDto goodsDto = new GoodsDto();
                goodsDto.setOrderId(orderId);
                goodsDto.setGoodsId(goodsId);
                goodsDto.setGoodsName(goodsName);
                goodsDtoList.add(goodsDto);
                OutputUtils.info(orderGoodsList, goodsDto);
            }
            OutputUtils.info(orderNum, String.valueOf(orderNumValue));
            OutputUtils.info(goodsNum, String.valueOf(goodsNumValue));
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
    }

    private static Document getServiceAppraise(AppConfigDto appConfigDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdServiceAppraise());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        return connection.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (appConfigDto.getJdInitQuery()) {
                initServiceAppraise(appConfigDto, true);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
