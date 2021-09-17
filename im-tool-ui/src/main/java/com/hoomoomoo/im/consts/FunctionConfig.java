package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public enum FunctionConfig {

    SVN_LOG("10", "svn提交记录", "/conf/fxml/svnLog.fxml", "menuItemSvnLog", "svnLog"),

    SVN_UPDATE("20", "svn代码更新", "/conf/fxml/svnUpdate.fxml", "menuItemSvnUpdate", "svnUpdate"),

    FUND_INFO("30", "基金信息", "/conf/fxml/fundInfo.fxml", "menuItemFundInfo", "fundInfo"),

    PROCESS_INFO("40", "流程信息", "/conf/fxml/processInfo.fxml", "menuItemProcessInfo", "processInfo"),

    SCRIPT_UPDATE("50", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "menuItemScriptUpdate", "scriptUpdate"),

    SVN_REALTIME_STAT("960", "svn实时统计", "/conf/fxml/svnRealtimeStat.fxml", "menuItemSvnRealtimeStat", "svnRealtimeStat"),

    SVN_HISTORY_STAT("970", "svn历史统计", "/conf/fxml/svnHistoryStat.fxml", "menuItemSvnHistoryStat", "svnHistoryStat"),

    FUNCTION_STAT_INFO("980", "功能使用统计", "/conf/fxml/functionStatInfo.fxml", "menuItemFunctionStat", "functionStatInfo"),

    CONFIG_SET("990", "参数设置", "/conf/fxml/configSet.fxml", "menuItemConfigSet", "configSet"),

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

}
