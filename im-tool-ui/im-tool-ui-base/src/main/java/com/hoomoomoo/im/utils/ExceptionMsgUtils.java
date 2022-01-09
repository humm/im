package com.hoomoomoo.im.utils;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/28
 */
public class ExceptionMsgUtils {

    private final static String SVN_ERROR_CODE_E170001 = "E170001";
    private final static String SVN_ERROR_CODE_E175002 = "E175002";
    private final static String SVN_ERROR_CODE_E155004 = "E155004";
    private final static String FORBIDDEN = "403 Forbidden";

    public static String getMsg(Exception e) {
        if (e.toString().contains(FORBIDDEN)) {
            return e.toString();
        } else if (e.toString().contains(SVN_ERROR_CODE_E170001)) {
            return "请检查配置项[ svn.username ] [ svn.password ]";
        } else if (e.toString().contains(SVN_ERROR_CODE_E175002)) {
            return "请检查网络连接是否正常; svn路径是否存在";
        } else if (e.toString().contains(SVN_ERROR_CODE_E155004)) {
            return "请至svn路径执行[ cleanUp ]并选择[ Break write ]";
        } else {
            return e.toString();
        }
    }
}
