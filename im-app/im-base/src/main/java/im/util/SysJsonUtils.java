package im.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hoomoomoo
 * @description json工具类
 * @package im.util
 * @date 2020/02/08
 */

public class SysJsonUtils {

    private static final ObjectMapper objectMapper              = new ObjectMapper();
    private static boolean isPretty                             = false;
    private static final String STR_EMPTY                       = "";


    public static String toJson(Object object) {
        String jsonString = STR_EMPTY;
        try {
            if (isPretty) {
                jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                jsonString = objectMapper.writeValueAsString(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
