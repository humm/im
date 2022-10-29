package com.hoomoomoo.im.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.ENCODING_GBK;
import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_EMPTY;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/4/9
 */
public class HttpRequestUtils {

    private static final String ACCEPT = "*/*";
    private static final String CONNECTION = "Keep-Alive";
    private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";
    private static final Integer TIME_OUT = 3000;

    public static String sendGet(String url, String param) {
        return send(url, param, false);
    }

    public static String sendPost(String url, String param) {
        return send(url, param, true);
    }

    public static String send(String url, String param, boolean post){
        String result = SYMBOL_EMPTY;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            if (!post) {
                url += "?" + param;
            }
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.setRequestProperty("accept", ACCEPT);
            connection.setRequestProperty("connection", CONNECTION);
            connection.setRequestProperty("user-agent", USER_AGENT);
            if (post) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                out = new PrintWriter(connection.getOutputStream());
                out.print(param);
                out.flush();
            }
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODING_GBK));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                LoggerUtils.info(e2);
            }
        }
        return result;
    }
}
