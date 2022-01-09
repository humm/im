package com.hoomoomoo.im.util;

import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GoodsDto;
import org.jsoup.Connection;

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

    public static void initCookie(AppConfigDto appConfigDto, Connection connection) {
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
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

    public static int getOrderNum(List<GoodsDto> goodsDtoList) {
        Map<String, String> count = new HashMap<>(goodsDtoList.size());
        for (GoodsDto goodsDto : goodsDtoList) {
            if (NAME_APPRAISEING.equals(goodsDto.getStatus())) {
                continue;
            }
            if (!count.containsKey(goodsDto.getOrderId())) {
                count.put(goodsDto.getOrderId(), goodsDto.getOrderId());
            }
        }
        return count.size();
    }
}

