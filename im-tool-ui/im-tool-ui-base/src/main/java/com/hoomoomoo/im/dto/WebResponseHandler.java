package com.hoomoomoo.im.dto;

import com.hoomoomoo.im.utils.LoggerUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.Data;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
public class WebResponseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String queryString = exchange.getRequestURI().getQuery();
        Map<String,String> requestParam = formDataToMap(queryString);
        exchange.sendResponseHeaders(200, 0);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write("收到消息".getBytes(ENCODING_GBK));
        outputStream.close();
    }

    private static Map<String,String> formDataToMap(String formData ) {
        Map<String,String> result = new HashMap<>(16);
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split(SYMBOL_AND);
        Arrays.stream(items).forEach(item ->{
            final String[] keyAndVal = item.split(SYMBOL_EQUAL);
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode(keyAndVal[0], ENCODING_GBK);
                    final String val = URLDecoder.decode(keyAndVal[1], ENCODING_GBK);
                    result.put(key,val);
                }catch (UnsupportedEncodingException e) {
                    LoggerUtils.info(e);
                }
            }
        });
        return result;
    }
}
