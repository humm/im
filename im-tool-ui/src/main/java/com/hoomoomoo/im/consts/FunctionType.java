package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public enum FunctionType {

    SVN_LOG("1", "svn提交记录", "/conf/fxml/svnLog.fxml"),

    SVN_UPDATE("2", "svn代码更新", "/conf/fxml/svnUpdate.fxml"),

    FUND_INFO("3", "基金信息", "/conf/fxml/fundInfo.fxml"),

    PROCESS_INFO("4", "流程信息", "/conf/fxml/processInfo.fxml"),

    SCRIPT_UPDATE("5", "升级脚本", "/conf/fxml/scriptUpdate.fxml"),

    STAT_INFO("6", "统计分析", "/conf/fxml/statInfo.fxml"),

    ABOUT_INFO("7", "关于", "/conf/fxml/aboutInfo.fxml");


    private String type;

    private String name;

    private String path;

    FunctionType(String type, String name, String path) {
        this.type = type;
        this.name = name;
        this.path = path;
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

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}
