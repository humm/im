package com.hoomoomoo.im.consts;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.consts
 * @date 2021/04/25
 */
public class MenuFunctionConfig {

    public enum MenuConfig {

        MENU_REPOSITORY("menuRepository", "仓库", 100, "repository"),
        MENU_SCRIPT("menuScript", "脚本", 200, "script"),
        MENU_FILE("menuFile", "文件", 300, "file"),
        MENU_HEP("menuHep", "效能", 400, "hep"),
        MENU_OTHER("menuOther", "整备", 410, "other"),
        MENU_TOOL("menuTool", "系统", 450, "tool"),
        MENU_PARAM("menuParam", "参数", 500, "param"),
        MENU_HELP("menuHelp", "帮助", 600, "help");

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
        SVN_LOG("100", "提交记录", "/conf/fxml/svnLog.fxml", "svnLog", "svnLog", "menuRepository", "用户信息,提交记录", "0"),
        SVN_UPDATE("110", "文件更新", "/conf/fxml/svnUpdate.fxml", "svnUpdate", "svnUpdate", "menuRepository", "用户信息,文件更新", "0"),

        SCRIPT_UPDATE("200", "升级脚本", "/conf/fxml/scriptUpdate.fxml", "scriptUpdate", "scriptUpdate", "menuScript", "升级脚本", "0"),
        PROCESS_INFO("210", "流程信息", "/conf/fxml/processInfo.fxml", "processInfo", "processInfo", "menuScript", "流程信息", "0"),
        FUND_INFO("220", "基金信息", "/conf/fxml/fundInfo.fxml", "fundInfo", "fundInfo", "menuScript", "基金信息", "0"),
        DATABASE_SCRIPT("230", "执行脚本", "/conf/fxml/databaseScript.fxml", "databaseScript", "databaseScript", "menuScript", "执行脚本", "0"),
        GENERATE_SQL("240", "分库分表", "/conf/fxml/generateSql.fxml", "generateSql", "generateSql", "menuScript", "分库分表", "0"),

        COPY_CODE("300", "复制文件", "/conf/fxml/copyCode.fxml", "copyCode", "copyCode", "menuFile", "复制文件", "0"),
        FILE_SYNC("420", "同步文件", "/conf/fxml/fileSync.fxml", "fileSync", "fileSync", "menuFile", "同步文件", "1"),
        SCRIPT_CHECK("500", "检查文件", "/conf/fxml/scriptCheck.fxml", "scriptCheck", "scriptCheck", "menuFile", "检查文件", "0"),
        GENERATE_CODE("310", "生成代码", "/conf/fxml/generateCode.fxml", "generateCode", "generateCode", "menuFile", "生成代码", "1"),

        TASK_TODO("400", "开发任务", "/conf/fxml/hepTaskTodo.fxml", "hepTaskTodo", "hepTaskTodo", "menuHep", "开发任务", "0"),

        CHANGE_TOOL("600", "整备环境", "/conf/fxml/changeTool.fxml", "changeTool", "changeTool", "menuOther", "整备环境", "0"),

        SYSTEM_TOOL("700", "系统工具", "/conf/fxml/systemTool.fxml", "systemTool", "systemTool", "menuTool", "系统工具", "0"),

        // 公共组件 3000-9999
        CONFIG_SET("9999", "参数设置", "/conf/fxml/configSet.fxml", "configSet", "configSet", "menuParam", "应用信息", "0"),

        FUNCTION_STAT_INFO("3000", "使用统计", "/conf/fxml/functionStatInfo.fxml", "functionStatInfo", "functionStatInfo", "menuHelp", "", "0"),
        ABOUT_INFO("3100", "关于", "/conf/fxml/aboutInfo.fxml", "aboutInfo", "aboutInfo", "menuHelp", "", "0"),;


        private String code;

        private String name;

        private String path;

        private String logFolder;

        private String menuId;

        private String parentMenuId;

        private String titleConf;

        private String hide;

        FunctionConfig(String code, String name, String path, String logFolder, String menuId, String parentMenuId, String titleConf, String hide) {
            this.code = code;
            this.name = name;
            this.path = path;
            this.logFolder = logFolder;
            this.menuId = menuId;
            this.parentMenuId = parentMenuId;
            this.titleConf = titleConf;
            this.hide = hide;
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

        public String getHide() {
            return hide;
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

        public static String getHide(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.hide;
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


