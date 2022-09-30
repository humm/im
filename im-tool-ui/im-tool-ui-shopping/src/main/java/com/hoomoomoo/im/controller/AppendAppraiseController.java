package com.hoomoomoo.im.controller;

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
import java.net.URLEncoder;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.APPEND_APPRAISE;
import static com.hoomoomoo.im.util.ShoppingCommonUtil.getPageInfo;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2022/1/8
 */
public class AppendAppraiseController extends ShoppingBaseController implements Initializable {

    @FXML
    void execute(ActionEvent event) {
        super.execute(AppendAppraiseController.class, APPEND_APPRAISE);
    }

    @FXML
    void query(ActionEvent event) {
        super.executeQuery(AppendAppraiseController.class, APPEND_APPRAISE);
    }

    @FXML
    void pause(ActionEvent event) {
        super.executePause(AppendAppraiseController.class, APPEND_APPRAISE);
    }

    public static Document goodsAppraise(AppConfigDto appConfigDto, GoodsDto goodsDto) throws IOException {
        Connection connection = Jsoup.connect(appConfigDto.getJdAppendAppraiseExecute());
        ShoppingCommonUtil.initCookie(appConfigDto, connection);
        Map<String, String> requestData = new HashMap<>(6);
        requestData.put(KEY_ORDER_ID, goodsDto.getOrderId());
        requestData.put(KEY_PRODUCT_ID, goodsDto.getGoodsId());
        requestData.put(KEY_CONTENT, URLEncoder.encode(goodsDto.getAppraiseInfo(), ENCODING_UTF8));
        requestData.put(KEY_IMGS, SYMBOL_EMPTY);
        requestData.put(KEY_ANONYMOUS_FLAG, STR_1);
        requestData.put(KEY_SCORE, STR_5);
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
            Document appendAppraise = getAppendAppraise(appConfigDto, 1);
            orderNumValue = ShoppingCommonUtil.getWaitHandleNum(appendAppraise, STR_3);
            int page = getPageInfo(orderNumValue);
            for (int i=1; i<=page; i++) {
                appendAppraise = getAppendAppraise(appConfigDto, i);
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
        shoppingDto.setType(APPEND_APPRAISE.getCode());
        shoppingDto.setTypeName(APPEND_APPRAISE.getName());
        return shoppingDto;
    }

    public static Document getAppendAppraise(AppConfigDto appConfigDto, int page) throws IOException {
        return getQueryData(appConfigDto, appConfigDto.getJdAppendAppraise(), page);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.init(AppendAppraiseController.class, APPEND_APPRAISE);
    }
}
