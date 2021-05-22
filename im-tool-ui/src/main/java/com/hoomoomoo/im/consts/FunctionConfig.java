package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public enum FunctionType {

    SVN_LOG("1", "svn提交记录", "/conf/fxml/svnLog.fxml", "menuItemSvnLog"),

    SVN_UPDATE("2", "svn代码更新", "/conf/fxml/svnUpdate.fxml", "menuItemSvnUpdate"),

    FUND_INFO("3", "基金信息", "/conf/fxml/fundInfo.fxml", "menuItemFundInfo"),

    PROCESS_INFO("4", "流程信息", "/conf/fxml/processInfo.fxml", "menuItemProcessInfo"),

    SCRIPT_UPDATE("5", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "menuItemScriptUpdate"),

    STAT_INFO("98", "统计分析", "/conf/fxml/statInfo.fxml", "menuItemStat"),

    ABOUT_INFO("99", "关于", "/conf/fxml/aboutInfo.fxml", "menuItemAbout");


    private String type;

    private String name;

    private String path;

    private String menuId;

    FunctionType(String type, String name, String path, String menuId) {
        this.type = type;
        this.name = name;
        this.path = path;
        this.menuId = menuId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getMenuId() {
        return path;
    }

    public static String getName(String type) {
        for (FunctionType tabType : FunctionType.values()) {
            if (tabType.getType().equals(type)) {
                return tabType.name;
            }
        }
        return null;
    }

    public static String getPath(String type) {
        for (FunctionType tabType : FunctionType.values()) {
            if (tabType.getType().equals(type)) {
                return tabType.path;
            }
        }
        return null;
    }

}
