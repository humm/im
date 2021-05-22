package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public enum FunctionConfig {

    SVN_LOG("1", "svn提交记录", "/conf/fxml/svnLog.fxml", "menuItemSvnLog", "svnLog"),

    SVN_UPDATE("2", "svn代码更新", "/conf/fxml/svnUpdate.fxml", "menuItemSvnUpdate", "svnUpdate"),

    FUND_INFO("3", "基金信息", "/conf/fxml/fundInfo.fxml", "menuItemFundInfo", "fundInfo"),

    PROCESS_INFO("4", "流程信息", "/conf/fxml/processInfo.fxml", "menuItemProcessInfo", "processInfo"),

    SCRIPT_UPDATE("5", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "menuItemScriptUpdate", "scriptUpdate"),

    STAT_INFO("98", "统计分析", "/conf/fxml/statInfo.fxml", "menuItemStat", ""),

    ABOUT_INFO("99", "关于", "/conf/fxml/aboutInfo.fxml", "menuItemAbout", "");


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
