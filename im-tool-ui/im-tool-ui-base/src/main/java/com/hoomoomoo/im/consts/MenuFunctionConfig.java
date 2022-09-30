package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public class MenuFunctionConfig {

    public enum MenuConfig {

        // TA菜单
        MENU_SVN("menuSvn", "SVN", 10),
        MENU_SCRIPT("menuScript", "脚本", 20),
        MENU_CODE("menuCode", "代码", 30),
        MENU_TOOL("menuTool", "其他", 40),

        // Shopping菜单
        MENU_JD("menuJd", "京东", 10),

        // 公共菜单
        MENU_SET("menuSet", "设置", 1000),
        MENU_HELP("menuHelp", "帮助", 1100);

        private String menuId;

        private String menuName;

        private Integer menuOrder;

        public String getMenuId() {
            return menuId;
        }

        public String getMenuName() {
            return menuName;
        }

        public Integer getMenuOrder() {
            return menuOrder;
        }

        MenuConfig(String menuId, String menuName, Integer menuOrder) {
            this.menuId = menuId;
            this.menuName = menuName;
            this.menuOrder = menuOrder;
        }

        public static MenuConfig getMenuConfig(String code) {
            for (MenuConfig menu : MenuConfig.values()) {
                if (menu.getMenuId().equals(code)) {
                    return menu;
                }
            }
            return null;
        }
    }

    public enum FunctionConfig {
        // TA工具组件 0-999
        SVN_LOG("10", "提交记录", "/conf/fxml/svnLog.fxml", "svnLog", "svnLog", "menuSvn"),

        SVN_UPDATE("50", "代码更新", "/conf/fxml/svnUpdate.fxml", "svnUpdate", "svnUpdate", "menuSvn"),

        SVN_REALTIME_STAT("100", "实时统计", "/conf/fxml/svnRealtimeStat.fxml", "svnRealtimeStat", "svnRealtimeStat", "menuSvn"),

        SVN_HISTORY_STAT("150", "历史统计", "/conf/fxml/svnHistoryStat.fxml", "svnHistoryStat", "svnHistoryStat", "menuSvn"),

        SCRIPT_UPDATE("200", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "scriptUpdate", "scriptUpdate", "menuScript"),

        PROCESS_INFO("250", "流程信息", "/conf/fxml/processInfo.fxml", "processInfo", "processInfo", "menuScript"),

        FUND_INFO("300", "基金信息", "/conf/fxml/fundInfo.fxml", "fundInfo", "fundInfo", "menuScript"),

        GENERATE_SQL("350", "分库分表", "/conf/fxml/generateSql.fxml", "generateSql", "generateSql", "menuScript"),

        COPY_CODE("400", "复制代码", "/conf/fxml/copyCode.fxml", "copyCode", "copyCode", "menuCode"),

        GENERATE_CODE("450", "生成代码", "/conf/fxml/generateCode.fxml", "generateCode", "generateCode", "menuCode"),

        DATABASE_SCRIPT("500", "数据库脚本", "/conf/fxml/databaseScript.fxml", "databaseScript", "databaseScript", "menuTool"),

        // Shopping 1000-1999
        JD_AUTO("1000", "自动评价", "/conf/fxml/jdAuto.fxml", "jdAuto", "jdAuto", "menuJd"),

        WAIT_APPRAISE("1100", "待评价", "/conf/fxml/waitAppraise.fxml", "waitAppraise", "waitAppraise", "menuJd"),

        SHOW_ORDER("1200", "待晒单", "/conf/fxml/showOrder.fxml", "showOrder", "showOrder", "menuJd"),

        APPEND_APPRAISE("1300", "待追评", "/conf/fxml/appendAppraise.fxml", "appendAppraise", "appendAppraise", "menuJd"),

        SERVICE_APPRAISE("1400", "服务评价", "/conf/fxml/serviceAppraise.fxml", "serviceAppraise", "serviceAppraise", "menuJd"),

        JD_COOKIE("1500", "京东Cookie设置", "/conf/fxml/jdCookie.fxml", "jdCookie", "jdCookie", "menuSet"),


        // 公共组件 2000-2999
        CONFIG_SET("2000", "参数设置", "/conf/fxml/configSet.fxml", "configSet", "configSet", "menuSet"),

        FUNCTION_STAT_INFO("2100", "使用统计", "/conf/fxml/functionStatInfo.fxml", "functionStatInfo", "functionStatInfo", "menuHelp"),

        ABOUT_INFO("2200", "关于", "/conf/fxml/aboutInfo.fxml", "aboutInfo", "aboutInfo", "menuHelp");

        private String code;

        private String name;

        private String path;

        private String logFolder;

        private String menuId;

        private String parentMenuId;

        FunctionConfig(String code, String name, String path, String logFolder, String menuId, String parentMenuId) {
            this.code = code;
            this.name = name;
            this.path = path;
            this.logFolder = logFolder;
            this.menuId = menuId;
            this.parentMenuId = parentMenuId;
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

        public String getLogFolder() {
            return logFolder;
        }

        public String getMenuId() {
            return menuId;
        }

        public String getParentMenuId() {
            return parentMenuId;
        }

        public static String getName(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.name;
                }
            }
            return null;
        }

        public static String getNameByMenuId(String menuId) {
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

}


