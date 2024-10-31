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
        MENU_SCRIPT("menuCommon", "通用", 20, "common"),
        MENU_SYSTEM("menuSystem", "定制", 50, "system"),

        // 公共菜单
        MENU_SET("menuSet", "设置", 1000, "set");

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
        SVN_LOG("100", "提交记录", "/conf/fxml/svnLog.fxml", "svnLog", "svnLog", "menuSvn", "用户信息,提交记录"),

        SVN_UPDATE("110", "文件更新", "/conf/fxml/svnUpdate.fxml", "svnUpdate", "svnUpdate", "menuSvn", "用户信息,文件更新"),

        SVN_REALTIME_STAT("120", "实时统计", "/conf/fxml/svnRealtimeStat.fxml", "svnRealtimeStat", "svnRealtimeStat", "menuSvn", "用户信息,统计用户,实时统计"),

        SVN_HISTORY_STAT("130", "历史统计", "/conf/fxml/svnHistoryStat.fxml", "svnHistoryStat", "svnHistoryStat", "menuSvn", "用户信息,统计用户,历史统计"),

        SCRIPT_UPDATE("200", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "scriptUpdate", "scriptUpdate", "menuCommon", "升级脚本"),

        PROCESS_INFO("210", "流程信息", "/conf/fxml/processInfo.fxml", "processInfo", "processInfo", "menuCommon", "流程信息"),

        FUND_INFO("220", "基金信息", "/conf/fxml/fundInfo.fxml", "fundInfo", "fundInfo", "menuCommon", "基金信息"),

        DATABASE_SCRIPT("230", "执行脚本", "/conf/fxml/databaseScript.fxml", "databaseScript", "databaseScript", "menuCommon", "执行脚本"),

        GENERATE_SQL("240", "分库分表", "/conf/fxml/generateSql.fxml", "generateSql", "generateSql", "menuCommon", "分库分表"),

        COPY_CODE("300", "复制代码", "/conf/fxml/copyCode.fxml", "copyCode", "copyCode", "menuCommon", "复制代码"),

        //GENERATE_CODE("310", "生成代码", "/conf/fxml/generateCode.fxml", "generateCode", "generateCode", "menuCommon", "生成代码"),

        TASK_TODO("400", "开发任务", "/conf/fxml/hepTaskTodo.fxml", "hepTaskTodo", "hepTaskTodo", "menuSystem", "开发任务"),

        TASK_SYNC("410", "同步需求", "/conf/fxml/hepTaskSync.fxml", "hepTaskSync", "hepTaskSync", "menuSystem", "同步需求"),

        SCRIPT_CHECK("500", "脚本检查", "/conf/fxml/scriptCheck.fxml", "scriptCheck", "scriptCheck", "menuSystem", "脚本检查"),

        CHANGE_FUNCTION_TOOL("600", "版本切换", "/conf/fxml/changeFunctionTool.fxml", "changeFunctionTool", "changeFunctionTool", "menuSystem", "版本切换"),

        SYSTEM_TOOL("700", "高级工具", "/conf/fxml/systemTool.fxml", "systemTool", "systemTool", "menuSystem", "高级工具"),

        // 公共组件 3000-9999
        CONFIG_SET("9999", "参数设置", "/conf/fxml/configSet.fxml", "configSet", "configSet", "menuSet", "应用信息"),

        FUNCTION_STAT_INFO("3000", "使用统计", "/conf/fxml/functionStatInfo.fxml", "functionStatInfo", "functionStatInfo", "menuSet", ""),

        ABOUT_INFO("3100", "关于", "/conf/fxml/aboutInfo.fxml", "aboutInfo", "aboutInfo", "menuSet", "");


        private String code;

        private String name;

        private String path;

        private String logFolder;

        private String menuId;

        private String parentMenuId;

        private String titleConf;

        FunctionConfig(String code, String name, String path, String logFolder, String menuId, String parentMenuId, String titleConf) {
            this.code = code;
            this.name = name;
            this.path = path;
            this.logFolder = logFolder;
            this.menuId = menuId;
            this.parentMenuId = parentMenuId;
            this.titleConf = titleConf;
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

        public String getTitleConf() {
            return titleConf;
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


