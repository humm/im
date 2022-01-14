package com.hoomoomoo.im.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.util.ShoppingCommonUtil;
import com.hoomoomoo.im.utils.CommonUtils;
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
import java.math.BigDecimal;
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
public class AppendAppraiseController extends ShoppingBaseController implements Initializable {

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(log);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SHOW_ORDER.getName()));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!ShoppingCommonUtil.checkConfig(log, APPEND_APPRAISE.getCode())) {
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
                ComponentUtils.setButtonDisabled(execute, query);
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    initAppendAppraise(appConfigDto, true);
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
                if(!ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
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
                            GoodsDto goods = (GoodsDto) BeanUtils.cloneBean(goodsDto);
                            goods.setGoodsId(NAME_GOODS_NOT_EXIST);
                            goods.setStatus(NAME_APPRAISE_FAIL);
                            OutputUtils.info(log, goods);
                            ShoppingCommonUtil.initLogs(logs, goods);
                            continue;
                        }
                        doGoodsAppraise(appConfigDto, goodsDto);
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
                LoggerUtils.writeAppendAppraise(currentDate, logs);
                initAppendAppraise(appConfigDto, false);
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

    private static Document doGoodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppendAppraiseExecute());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put(KEY_ORDER_ID, goodsDto.getOrderId());
        requestData.put(KEY_PRODUCT_ID, goodsDto.getGoodsId());
        requestData.put(KEY_CONTENT, URLEncoder.encode(goodsDto.getAppraiseInfo()));
        requestData.put(KEY_IMGS, SYMBOL_EMPTY);
        requestData.put(KEY_ANONYMOUS_FLAG, STR_1);
        requestData.put(KEY_SCORE, STR_5);
        connection.data(requestData);
        return connection.post();
    }

    private void initAppendAppraise(AppConfigDto appConfigDto, boolean initLog) {
        orderNumValue = 0;
        OutputUtils.clearLog(orderGoodsList);
        if (initLog) {
            OutputUtils.clearLog(log);
        }
        goodsDtoList = new ArrayList<>();
        try {
            Document appendAppraise = getAppendAppraise(appConfigDto);
            orderNumValue = ShoppingCommonUtil.getWaitHandleNum(appendAppraise, STR_3);
            Elements orderList = appendAppraise.select("table.td-void.order-tb tbody tr.tr-bd");
            for (Element order : orderList) {
                Elements goodsIdEle = order.select("div.goods-item div.p-name a");
                String goodsIdHref = goodsIdEle.attr(KEY_HREF);
                String goodsName = goodsIdEle.text();
                String goodsId = ShoppingCommonUtil.getHrefId(goodsIdHref);
                Elements operateList = order.select("div.operate a");
                String orderId = SYMBOL_EMPTY;
                boolean isOperate = false;
                for (Element operate : operateList) {
                    String operateName = operate.text();
                    if (NAME_JD_APPEND_APPRAISEING.equals(operateName)) {
                        orderId = ShoppingCommonUtil.getJdOrderId(operate.attr(KEY_HREF));
                        isOperate = true;
                    }
                }
                if (!isOperate) {
                    continue;
                }
                String appraiseInfo = WaitAppraiseController.getGoodsAppraiseInfo(appConfigDto, goodsId);
                GoodsDto goodsDto = new GoodsDto();
                goodsDto.setOrderId(orderId);
                goodsDto.setGoodsId(goodsId);
                goodsDto.setGoodsName(goodsName);
                goodsDto.setAppraiseInfo(appraiseInfo);
                goodsDtoList.add(goodsDto);
                OutputUtils.info(orderGoodsList, goodsDto);
            }
            OutputUtils.info(orderNum, String.valueOf(orderNumValue));
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
    }

    private static Document getAppendAppraise(AppConfigDto appConfigDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppendAppraise());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        return connection.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (appConfigDto.getJdInitQuery()) {
                if (ShoppingCommonUtil.initJdUser(appConfigDto, log, userName, orderNum)) {
                    initAppendAppraise(appConfigDto, true);
                }
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
