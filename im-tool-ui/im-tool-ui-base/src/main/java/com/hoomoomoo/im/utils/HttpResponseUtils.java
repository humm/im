package com.hoomoomoo.im.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2022/4/3
 */
public class HttpResponseUtils implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> requestParam = paramGet(exchange);
        requestParam.putAll(paramPost(exchange));
        LoggerUtils.info("请求版本: " + requestParam.get(KEY_VERSION));
        exchange.sendResponseHeaders(200, 0);
        OutputStream outputStream = exchange.getResponseBody();
        String responseMsg = ("返回版本: " + CommonUtils.getVersion());
        outputStream.write(responseMsg.getBytes(ENCODING_GBK));
        LoggerUtils.info(responseMsg);
        outputStream.close();
    }

    private static Map<String, String> paramGet(HttpExchange exchange) {
        return convertDataToMap(exchange.getRequestURI().getQuery());
    }

    private static Map<String, String> paramPost(HttpExchange exchange) {
        String result = STR_BLANK;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), ENCODING_GBK));;
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                LoggerUtils.info(e2);
            }

        }
        return convertDataToMap(result);
    }

    private static Map<String, String> convertDataToMap(String formData) {
        Map<String, String> result = new HashMap<>(16);
        if (formData == null || formData.trim().length() == 0) {
            return result;
        }
        String[] items = formData.split(STR_AND);
        Arrays.stream(items).forEach(item -> {
            final String[] keyAndVal = item.split(STR_EQUAL);
            if (keyAndVal.length == 2) {
                try {
                    final String key = URLDecoder.decode(keyAndVal[0], ENCODING_GBK);
                    final String val = URLDecoder.decode(keyAndVal[1], ENCODING_GBK);
                    result.put(key, val);
                } catch (UnsupportedEncodingException e) {
                    LoggerUtils.info(e);
                }
            }
        });
        return result;
    }
}
