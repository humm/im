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
        MENU_SVN("menuSvn", "SVN", 10, "svn"),
        MENU_SCRIPT("menuScript", "脚本", 20, "sql"),
        MENU_CODE("menuCode", "代码", 30, "code"),
        MENU_HEP("menuHep", "效能", 40, "hep"),
        MENU_SYSTEM("menuSystem", "系统", 50, "system"),

        // Shopping菜单
        MENU_JD("menuJd", "京东", 10, "jd"),

        // 公共菜单
        MENU_SET("menuSet", "设置", 1000, "set"),
        MENU_HELP("menuHelp", "帮助", 1100, "help");

        private String menuId;

        private String menuName;

        private Integer menuOrder;

        private String icon;

        public String getMenuId() {
            return menuId;
        }

        public String getMenuName() {
            return menuName;
        }

        public Integer getMenuOrder() {
            return menuOrder;
        }

        public String getMenuIcon() {
            return icon;
        }

        MenuConfig(String menuId, String menuName, Integer menuOrder, String icon) {
            this.menuId = menuId;
            this.menuName = menuName;
            this.menuOrder = menuOrder;
            this.icon = icon;
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
        SVN_LOG("100", "提交记录", "/conf/fxml/svnLog.fxml", "svnLog", "svnLog", "menuSvn"),

        SVN_UPDATE("110", "文件更新", "/conf/fxml/svnUpdate.fxml", "svnUpdate", "svnUpdate", "menuSvn"),

        SVN_REALTIME_STAT("120", "实时统计", "/conf/fxml/svnRealtimeStat.fxml", "svnRealtimeStat", "svnRealtimeStat", "menuSvn"),

        SVN_HISTORY_STAT("130", "历史统计", "/conf/fxml/svnHistoryStat.fxml", "svnHistoryStat", "svnHistoryStat", "menuSvn"),

        SCRIPT_UPDATE("200", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "scriptUpdate", "scriptUpdate", "menuScript"),

        PROCESS_INFO("210", "流程信息", "/conf/fxml/processInfo.fxml", "processInfo", "processInfo", "menuScript"),

        FUND_INFO("220", "基金信息", "/conf/fxml/fundInfo.fxml", "fundInfo", "fundInfo", "menuScript"),

        DATABASE_SCRIPT("230", "执行脚本", "/conf/fxml/databaseScript.fxml", "databaseScript", "databaseScript", "menuScript"),

        GENERATE_SQL("240", "分库分表", "/conf/fxml/generateSql.fxml", "generateSql", "generateSql", "menuScript"),

        COPY_CODE("300", "复制代码", "/conf/fxml/copyCode.fxml", "copyCode", "copyCode", "menuCode"),

        GENERATE_CODE("310", "生成代码", "/conf/fxml/generateCode.fxml", "generateCode", "generateCode", "menuCode"),

        TASK_TODO("400", "待开发任务", "/conf/fxml/hepTaskTodo.fxml", "hepTaskTodo", "hepTaskTodo", "menuHep"),

        SYSTEM_TOOL("500", "便捷工具", "/conf/fxml/systemTool.fxml", "systemTool", "systemTool", "menuSystem"),

        // Shopping 1000-1999
        JD_AUTO("1000", "自动评价", "/conf/fxml/jdAuto.fxml", "jdAuto", "jdAuto", "menuJd"),

        WAIT_APPRAISE("1100", "待评价", "/conf/fxml/waitAppraise.fxml", "waitAppraise", "waitAppraise", "menuJd"),

        SHOW_ORDER("1200", "待晒单", "/conf/fxml/showOrder.fxml", "showOrder", "showOrder", "menuJd"),

        APPEND_APPRAISE("1300", "待追评", "/conf/fxml/appendAppraise.fxml", "appendAppraise", "appendAppraise", "menuJd"),

        SERVICE_APPRAISE("1400", "服务评价", "/conf/fxml/serviceAppraise.fxml", "serviceAppraise", "serviceAppraise", "menuJd"),

        JD_COOKIE("1900", "京东Cookie设置", "/conf/fxml/jdCookie.fxml", "jdCookie", "jdCookie", "menuSet"),


        // 公共组件 2000-2999
        CONFIG_SET("2000", "参数设置", "/conf/fxml/configSet.fxml", "configSet", "configSet", "menuSet"),

        FUNCTION_STAT_INFO("3000", "使用统计", "/conf/fxml/functionStatInfo.fxml", "functionStatInfo", "functionStatInfo", "menuHelp"),

        ABOUT_INFO("3100", "关于", "/conf/fxml/aboutInfo.fxml", "aboutInfo", "aboutInfo", "menuHelp");

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


