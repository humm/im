package com.hoomoomoo.im.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

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
    private static final Integer TIME_OUT = 3 * 1000;

    public static String sendPost(String url, Map<String, String> param) throws IOException {
        StringBuilder paramStr = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (index != 0) {
                paramStr.append(STR_AND);
            }
            paramStr.append(entry.getKey()).append(STR_EQUAL).append(entry.getValue());
            index++;
        }
        return sendPost(url, paramStr.toString());
    }

    private static String sendPost(String url, String param) throws IOException {
        String result = STR_BLANK;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.setRequestProperty("accept", ACCEPT);
            connection.setRequestProperty("connection", CONNECTION);
            connection.setRequestProperty("user-agent", USER_AGENT);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            out = new PrintWriter(connection.getOutputStream());
            out.print(param);
            out.flush();
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), ENCODING_UTF8));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                LoggerUtils.error(e2);
            }
        }
        return result;
    }
}
