package com.hoomoomoo.im.consts;

import java.util.regex.Pattern;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public class BaseConst {

    public static final String STR_1 = "1";
    public static final String STR_2 = "2";
    public static final String STR_3 = "3";
    public static final String STR_4 = "4";
    public static final String STR_5 = "5";
    public static final String STR_6 = "6";
    public static final String STR_7 = "7";
    public static final String STR_98 = "98";
    public static final String STR_99 = "99";

    public final static String ENCODING_UTF8 = "UTF-8";
    public final static String ENCODING_GBK = "GBK";
    public final static String ENCODING_GB = "GB";

    public final static String FILE_TYPE_NORMAL = "1";
    public final static String FILE_TYPE_CONFIG = "2";

    public static final String START_MODE_JAR = ".jar!";

    public static final String KEY_SVN_PASSWORD = "svn.password";
    public static final String KEY_SVN_UPDATE = "svn.update.";
    public static final String KEY_SCRIPT_UPDATE = "script.update.table.";

    public static final String FILE_TYPE_SVN = ".svn";
    public static final String FILE_TYPE_LOG = ".log";
    public static final String FILE_TYPE_JAR = ".jar";
    public static final String FILE_TYPE_BAK = ".bak";

    public static final String STR_NAME_VALUES = "values";
    public static final String STR_NAME_CONF = "conf";
    public static final String STR_NAME_FILE_SLASH = "file:/";
    public static final String STR_NAME_FILE = "file:";

    public final static String STR_SECURITY_FLAG = "+++";

    public final static String STR_EMPTY = "";
    public final static String STR_SPACE = " ";
    public final static String STR_SPACE_2 = "  ";
    public final static String STR_SPACE_4 = "    ";

    public final static String STR_SYMBOL_EQUALS = "=";
    public static final String STR_SYMBOL_COMMA = ",";
    public static final String STR_SYMBOL_SEMICOLON = ";";
    public static final String STR_SYMBOL_COLON = ":";
    public static final String STR_SYMBOL_HYPHEN = "-";
    public final static String STR_SYMBOL_POINT = ".";
    public static final String STR_SYMBOL_SLASH = "/";
    public static final String STR_SYMBOL_BRACKETS_LEFT = "(";
    public static final String STR_SYMBOL_BRACKETS_RIGHT = ")";
    public static final String STR_SYMBOL_NEXT_LINE = "\n";
    public static final String STR_SYMBOL_NEXT_LINE_2 = "\n\n";

    public final static String ANNOTATION_NORMAL = "--";
    public final static String ANNOTATION_CONFIG = "#";

    public static final Pattern STR_PATTERN = Pattern.compile("(\\\\u(\\w{4}))");

    public static final String STR_MSG_SVN_USERNAME = "请设置[ svn.username ]";
    public static final String STR_MSG_SVN_PASSWORD = "请设置[ svn.password ]";
    public static final String STR_MSG_SVN_URL = "请设置[ svn.url ]";
    public static final String STR_MSG_SVN_UPDATE_TA6 = "请设置[ svn.update.ta6 ]";
    public static final String STR_MSG_FUND_GENERATE_PATH = "请设置[ fund.generate.path ]";
    public static final String STR_MSG_PROCESS_GENERATE_PATH_SCHEDULE = "请设置[ process.generate.path.schedule ]";
    public static final String STR_MSG_PROCESS_GENERATE_PATH_TRANS = "请设置[ process.generate.path.trans ]";

    public static final String STR_MSG_OPEN = "打开[ %s ] 功能界面";
    public static final String STR_MSG_USE = "使用[ %s ] 功能";
    public static final String STR_MSG_UPDATE = "更新[ %s ] 完成";
    public static final String STR_MSG_LOAD = "加载[ %s ] 完成";
    public static final String STR_MSG_INIT = "初始化[ %s ] 完成";
    public static final String STR_MSG_INIT_EXCEPTION = "初始化[ %s ] 异常";
    public static final String STR_MSG_ENCRYPT = "加密[ %s ] 完成";
    public static final String STR_MSG_CHECK = "校验[ %s ] 完成";
    public static final String STR_MSG_START = "开始[ %s ]";
    public static final String STR_MSG_COMPLETE = "完成[ %s ]";
    public static final String STR_MSG_FUNCTION_NOT_EXIST = "不存在[ %s ]功能";
    public static final String STR_MSG_LICENSE_NOT_EXIST = "加载证书信息失败,请检查证书文件[ license.init ]";
    public static final String STR_MSG_LICENSE_EXPIRE = "授权证书已过期,授权截止[ %s ]";
    public static final String STR_MSG_LICENSE_NOT_AUTH = "未授权[ %s ]功能";

    public static final String STR_CONFIG_PREFIX = "#################### ";
    public static final String STR_CONFIG_APP_TITLE = "应用信息";
    public static final String STR_CONFIG_SVN_TITLE = "svn用户信息";

}
