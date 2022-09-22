package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public enum FunctionConfig {

    // TA工具组件 0-500
    SVN_LOG("10", "提交记录", "/conf/fxml/svnLog.fxml", "menuItemSvnLog", "svnLog"),

    SVN_UPDATE("20", "代码更新", "/conf/fxml/svnUpdate.fxml", "menuItemSvnUpdate", "svnUpdate"),

    FUND_INFO("30", "基金信息", "/conf/fxml/fundInfo.fxml", "menuItemFundInfo", "fundInfo"),

    PROCESS_INFO("40", "流程信息", "/conf/fxml/processInfo.fxml", "menuItemProcessInfo", "processInfo"),

    SCRIPT_UPDATE("50", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "menuItemScriptUpdate", "scriptUpdate"),

    GENERATE_CODE("60", "生成代码", "/conf/fxml/generateCode.fxml", "menuItemGenerateCode", "generateCode"),

    COPY_CODE("70", "复制代码", "/conf/fxml/copyCode.fxml", "menuItemCopyCode", "copyCode"),

    GENERATE_SQL("80", "分库分表", "/conf/fxml/generateSql.fxml", "menuItemGenerateSql", "generateSql"),

    SVN_REALTIME_STAT("390", "实时统计", "/conf/fxml/svnRealtimeStat.fxml", "menuItemSvnRealtimeStat", "svnRealtimeStat"),

    SVN_HISTORY_STAT("400", "历史统计", "/conf/fxml/svnHistoryStat.fxml", "menuItemSvnHistoryStat", "svnHistoryStat"),

    SVN_DAY_STAT("410", "日报统计", "/conf/fxml/svnDayStat.fxml", "menuItemSvnDayStat", "svnDayStat"),

    // 购物组件 500-900
    WAIT_APPRAISE("500", "待评价", "/conf/fxml/waitAppraise.fxml", "menuItemWaitAppraise", "waitAppraise"),

    SHOW_ORDER("510", "待晒单", "/conf/fxml/showOrder.fxml", "menuItemShowOrder", "showOrder"),

    APPEND_APPRAISE("520", "待追评", "/conf/fxml/appendAppraise.fxml", "menuItemAppendAppraise", "appendAppraise"),

    SERVICE_APPRAISE("530", "服务评价", "/conf/fxml/serviceAppraise.fxml", "menuItemServiceAppraise", "serviceAppraise"),

    JD_AUTO("540", "自动评价", "/conf/fxml/jdAuto.fxml", "menuItemJdAuto", "jdAuto"),

    JD_COOKIE("900", "京东Cookie设置", "/conf/fxml/jdCookie.fxml", "menuItemJdCookie", "jdCookie"),


    // 公共组件 901-
    CONFIG_SET("980", "参数设置", "/conf/fxml/configSet.fxml", "menuItemConfigSet", "configSet"),

    FUNCTION_STAT_INFO("990", "使用统计", "/conf/fxml/functionStatInfo.fxml", "menuItemFunctionStat", "functionStatInfo"),

    ABOUT_INFO("999", "关于", "/conf/fxml/aboutInfo.fxml", "menuItemAbout", "aboutInfo");

    private String code;

    private String name;

    private String path;

    private String menuId;

    private String logFolder;

    FunctionConfig(String code, String name, String path, String menuId, String logFolder) {
        this.code = code;
        this.name = name;
        this.path = path;
        this.menuId = menuId;
        this.logFolder = logFolder;
    }

    public String getCode() {
        return code;
    }

    public String getName() {

        return name;
    }

    public String getPath() {
        return path;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public static String getName(String code) {
        for (FunctionConfig tab : FunctionConfig.values()) {
            if (tab.getCode().equals(code)) {
                return tab.name;
            }
        }
        return null;
    }

    public static String getNameBymenuId(String menuId) {
        for (FunctionConfig tab : FunctionConfig.values()) {
            if (tab.getMenuId().equals(menuId)) {
                return tab.name;
            }
        }
        return null;
    }

    public static String getPath(String code) {
        for (FunctionConfig tab : FunctionConfig.values()) {
            if (tab.getCode().equals(code)) {
                return tab.path;
            }
        }
        return null;
    }

    public static String getMenuId(String code) {
        for (FunctionConfig tab : FunctionConfig.values()) {
            if (tab.getCode().equals(code)) {
                return tab.menuId;
            }
        }
        return null;
    }

    public static String getLogFolder(String code) {
        for (FunctionConfig tab : FunctionConfig.values()) {
            if (tab.getCode().equals(code)) {
                return tab.logFolder;
            }
        }
        return null;
    }

    public static FunctionConfig getFunctionConfig(String code) {
        for (FunctionConfig tab : FunctionConfig.values()) {
            if (tab.getCode().equals(code)) {
                return tab;
            }
        }
        return null;
    }
}
