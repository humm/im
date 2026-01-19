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
        MENU_OTHER("menuServicing", "整备", 410, "other"),
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
        SVN_LOG(
                "100",
                "提交记录",
                "用户信息,提交记录",
                "/conf/fxml/svnLog.fxml",
                "svnLog",
                "menuRepository",
                "0",
                ""
        ),
        SVN_UPDATE(
                "110",
                "文件更新",
                "用户信息,文件更新",
                "/conf/fxml/svnUpdate.fxml",
                "svnUpdate",
                "menuRepository",
                "0",
                ""
        ),

        SCRIPT_UPDATE(
                "200",
                "升级脚本",
                "升级脚本",
                "/conf/fxml/scriptUpdate.fxml",
                "scriptUpdate",
                "menuScript",
                "0",
                ""
        ),
        PROCESS_INFO(
                "210",
                "流程信息",
                "流程信息",
                "/conf/fxml/processInfo.fxml",
                "processInfo",
                "menuScript",
                "0",
                ""
        ),
        FUND_INFO(
                "220",
                "基金信息",
                "基金信息",
                "/conf/fxml/fundInfo.fxml",
                "fundInfo",
                "menuScript",
                "0",
                ""
        ),
        DATABASE_SCRIPT(
                "230",
                "执行脚本",
                "执行脚本",
                "/conf/fxml/databaseScript.fxml",
                "databaseScript",
                "menuScript",
                "0",
                ""
        ),
        GENERATE_SQL(
                "240",
                "分库分表",
                "分库分表",
                "/conf/fxml/generateSql.fxml",
                "generateSql",
                "menuScript",
                "0",
                ""
        ),

        COPY_CODE("300",
                "复制文件",
                "复制文件",
                "/conf/fxml/copyCode.fxml",
                "copyCode",
                "menuFile",
                "0",
                ""
        ),
        FILE_SYNC("420",
                "同步文件",
                "同步文件",
                "/conf/fxml/fileSync.fxml",
                "fileSync",
                "menuFile",
                "1",
                ""
        ),
        GENERATE_CODE(
                "310",
                "生成代码",
                "生成代码",
                "/conf/fxml/generateCode.fxml",
                "generateCode",
                "menuFile",
                "1",
                ""
        ),

        TASK_TODO(
                "400",
                "开发任务",
                "开发任务",
                "/conf/fxml/hepTaskTodo.fxml",
                "hepTaskTodo",
                "menuHep",
                "0",
                ""
        ),

        CHANGE_TOOL(
                "600",
                "环境准备",
                "环境准备",
                "/conf/fxml/changeTool.fxml",
                "changeTool",
                "menuServicing",
                "0",
                "ChangeToolController.java,ChangeFunctionTask.java,ChangeFunctionTaskParam.java,changeTool.fxml"
        ),
        PARAMETER_TOOL(
                "650",
                "文档更新",
                "文档更新",
                "/conf/fxml/parameterTool.fxml",
                "parameterTool",
                "menuServicing",
                "0",
                "CheckResultController.java,ParameterToolController.java,ExcelComparatorUtils.java,ParameterToolTask.java,ParameterToolTaskParam.java,parameterTool.fxml,paramRealtimeSetExclude.json,paramRealtimeSetInclude.json"
        ),
        SCRIPT_CHECK(
                "700",
                "文件检查",
                "文件检查",
                "/conf/fxml/scriptCheck.fxml",
                "scriptCheck",
                "menuServicing",
                "0",
                ""
        ),
        CHECK_VERSION(
                "800",
                "版本校验",
                "版本校验",
                " ",
                "checkVersion",
                "menuServicing",
                "1",
                ""
        ),

        SYSTEM_TOOL(
                "2000",
                "系统工具",
                "系统工具",
                "/conf/fxml/systemTool.fxml",
                "systemTool",
                "menuTool",
                "0",
                ""
        ),

        // 公共组件 3000-9999
        CONFIG_SET(
                "9999",
                "参数设置",
                "应用信息",
                "/conf/fxml/configSet.fxml",
                "configSet",
                "menuParam",
                "0",
                ""
        ),

        FUNCTION_STAT_INFO(
                "3000",
                "使用统计",
                "使用统计",
                "/conf/fxml/functionStatInfo.fxml",
                "functionStatInfo",
                "menuHelp",
                "0",
                ""
        ),

        ABOUT_INFO(
                "3100",
                "关于",
                "关于",
                "/conf/fxml/aboutInfo.fxml",
                "aboutInfo",
                "menuHelp",
                "0",
                ""
        );

        private String code;

        private String name;

        private String path;

        private String menuId;

        private String parentMenuId;

        private String titleConf;

        private String hide;

        private String relateFile;

        FunctionConfig(String code, String name, String titleConf, String path, String menuId, String parentMenuId, String hide, String relateFile) {
            this.code = code;
            this.name = name;
            this.titleConf = titleConf;
            this.path = path;
            this.menuId = menuId;
            this.parentMenuId = parentMenuId;
            this.hide = hide;
            this.relateFile = relateFile;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static String getName(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.name;
                }
            }
            return null;
        }

        public String getPath() {
            return path;
        }

        public static String getPath(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.path;
                }
            }
            return null;
        }

        public String getLogFolder() {
            return menuId;
        }

        public static String getLogFolder(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.menuId;
                }
            }
            return null;
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

        public static String getHide(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.hide;
                }
            }
            return null;
        }

        public String getRelateFile() {
            return relateFile;
        }

        public static String getRelateFile(String code) {
            for (FunctionConfig tab : FunctionConfig.values()) {
                if (tab.getCode().equals(code)) {
                    return tab.relateFile;
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


