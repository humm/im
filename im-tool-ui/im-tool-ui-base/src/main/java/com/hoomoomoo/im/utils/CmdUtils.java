package com.hoomoomoo.im.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static com.hoomoomoo.im.consts.BaseConst.ENCODING_GBK;
import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/3/31
 */
public class CmdUtils {
    public static String exe(String command) {
        BufferedReader bufferedReader = null;
        StringBuilder content = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName(ENCODING_GBK)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + STR_NEXT_LINE);
            }
        } catch (IOException e) {
            LoggerUtils.info(e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                LoggerUtils.info(e);
            }
        }
        return content.toString();
    }
}
