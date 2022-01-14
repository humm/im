package com.hoomoomoo.im.util;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/1/9
 */
public class ShoppingCommonUtil {

    public static boolean checkConfig(TableView<?> log, String functionType) throws Exception {
        boolean flag = true;
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        if (functionType.equals(FunctionConfig.WAIT_APPRAISE.getCode()) || functionType.equals(FunctionConfig.SHOW_ORDER.getCode())
                || functionType.equals(FunctionConfig.APPEND_APPRAISE.getCode()) || functionType.equals(FunctionConfig.SERVICE_APPRAISE.getCode())) {
            if (StringUtils.isBlank(appConfigDto.getJdCookie())) {
                OutputUtils.info(log, MSG_WAIT_APPRAISE_JD_COOKIE);
                flag = false;
            }
        }
        return flag;
    }

    public static void initCookie(AppConfigDto appConfigDto, Connection connection) {
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
        if (StringUtils.isBlank(appConfigDto.getJdCookie())) {
            return;
        }
        String[] cookies = appConfigDto.getJdCookie().split(BaseConst.SYMBOL_SEMICOLON);
        for (String item : cookies) {
            String[] itemCookie = item.split(BaseConst.SYMBOL_EQUALS);
            if (itemCookie.length == 2) {
                connection.cookie(itemCookie[0], itemCookie[1]);
            }
        }
    }

    public static void initLogs(List<String> logs, GoodsDto goodsDto){
        StringBuilder log = new StringBuilder();
        log.append(goodsDto.getOrderId()).append(SYMBOL_SPACE);
        log.append(goodsDto.getGoodsId()).append(SYMBOL_SPACE);
        log.append(goodsDto.getGoodsName()).append(SYMBOL_SPACE);
        log.append(goodsDto.getStatus()).append(SYMBOL_SPACE);
        logs.add(log.toString());
    }

    public static String getHrefId(String href) {
        String hrefId = BaseConst.SYMBOL_EMPTY;
        if (StringUtils.isBlank(href)) {
            return hrefId;
        }
        int indexStart = href.lastIndexOf(BaseConst.SYMBOL_SLASH);
        int indexEnd = href.lastIndexOf(BaseConst.SYMBOL_POINT);
        if (indexEnd != -1 && indexEnd > indexStart) {
            hrefId = href.substring(indexStart + 1, indexEnd);
        }
        return hrefId;
    }

    public static String getJdOrderId(String href) {
        String orderId = BaseConst.SYMBOL_EMPTY;
        if (StringUtils.isBlank(href)) {
            return orderId;
        }
        int index = href.lastIndexOf(KEY_ORDER_ID);
        if (index == -1) {
            return orderId;
        }
        return href.substring(index + KEY_ORDER_ID.length() + 1);
    }

    public static String getJdSortId(String href) {
        String sortId = BaseConst.SYMBOL_EMPTY;
        if (StringUtils.isBlank(href)) {
            return sortId;
        }
        int index = href.lastIndexOf(KEY_SORT);
        if (index == -1) {
            return sortId;
        }
        return href.substring(index + KEY_SORT.length() + 1);
    }

    public static boolean effectiveJdCookie(Document document, TableView<?> log, Label userName, Label orderNum) {
        if (document.text().contains(NAME_JD_LOGIN)) {
            GoodsDto fail = new GoodsDto();
            fail.setGoodsName(MSG_WAIT_APPRAISE_JD_COOKIE);
            OutputUtils.info(log, fail);
            OutputUtils.info(userName, MSG_WAIT_APPRAISE_JD_COOKIE);
            if (orderNum != null) {
                OutputUtils.info(orderNum, STR_0);
            }
            return false;
        }
        return true;
    }

    public static boolean initJdUser(AppConfigDto appConfigDto, TableView<?> log, Label userName, Label orderNum) throws IOException {
        if (appConfigDto.getRefreshConfig()) {
            Connection connection = Jsoup.connect(appConfigDto.getJdUser());
            ShoppingCommonUtil.initCookie(appConfigDto, connection);
            Document user = connection.get();
            if (!effectiveJdCookie(user, log, userName, orderNum)) {
                return false;
            }
            String userCode = user.select("div.user-set.userset-lcol div#aliasBefore strong").text();
            appConfigDto.setJdUserCode(userCode);
        }
        OutputUtils.info(userName, appConfigDto.getJdUserCode());
        appConfigDto.setRefreshConfig(false);
        return true;
    }

    public static int getWaitHandleNum (Document document, String type) {
       int num = 0;
        Elements list = document.select("div#main ul li.trig-item");;
        for (Element element : list) {
            String sortId = getJdSortId(element.select("a").attr(KEY_HREF));
            String nums = element.select("em").text();
            if (StringUtils.isBlank(nums)) {
                nums = element.select("b").text();
            }
            if (type.equals(sortId)) {
                num = StringUtils.isBlank(nums) ? 0 : Integer.valueOf(nums);
                break;
            }
        }
       return num;
    }

    public static void restMoment(AppConfigDto appConfigDto, TableView<?> log) throws InterruptedException {
        GoodsDto goodsDto = new GoodsDto();
        goodsDto.setGoodsName(NAME_REST_MOMENT);
        OutputUtils.info(log, goodsDto);
        Thread.sleep(Integer.valueOf(appConfigDto.getJdIntervalTime()) * 1000);
    }

    public static void noAppraiseGoods(AppConfigDto appConfigDto, TableView<?> log) {
        GoodsDto noGoods = new GoodsDto();
        noGoods.setGoodsName(NAME_NO_APPRAISE_GOODS);
        OutputUtils.info(log, noGoods);
    }

    public static void appraiseComplete(AppConfigDto appConfigDto, TableView<?> log) {
        GoodsDto noGoods = new GoodsDto();
        noGoods.setGoodsName(NAME_APPRAISE_COMPLETE);
        OutputUtils.info(log, noGoods);
    }
}

