package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.SystemToolController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class MenuCompare {

    private String resultPath = "";
    private String basePathExt = "\\sql\\pub\\001initdata\\extradata\\";
    private String basePathRouter = "\\front\\HUI1.0\\console-fund-ta-vue\\router\\modules\\";
    private String baseMenu = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu.sql";
    private String newUedPage = "UED\\newUedPage.sql";
    private Map<String, List<String>> menuCache = new LinkedHashMap<>();
    private Map<String, String> menuExistCache = new LinkedHashMap<>();
    private int extFileNum = 0;
    private int needAddMenuNum = 0;
    private List<String> needAddMenu = new ArrayList<>();
    Set<String> skip = new HashSet<>();

    private TextArea logs;

    public MenuCompare(TextArea logs) throws Exception {
        try {
            this.logs = logs;
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
            if (StringUtils.isBlank(basePath)) {
                OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("请配置参数【system.tool.check.menu.base.path】"));
                throw new Exception("请配置参数【system.tool.check.menu.base.path】");
            }
            String resPath = appConfigDto.getSystemToolCheckMenuResultPath();
            if (StringUtils.isBlank(resPath)) {
                OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("请配置参数【system.tool.check.menu.result.path】"));
                throw new Exception("请配置参数【system.tool.check.menu.result.path】");
            }
            basePathExt = basePath + basePathExt;
            basePathRouter = basePath + basePathRouter;
            baseMenu = basePath + baseMenu;
            newUedPage = basePathExt + newUedPage;
            resultPath = resPath + "\\";

            String confPath = FileUtils.getFilePath(PATH_MENU_SKIP);
            List<String> content = FileUtils.readNormalFile(confPath, false);
            for (String item : content) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                skip.add(item.trim());
            }

            OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("扫描菜单脚本 开始"));
            initMenuExt();
            initConfigMenuCache();
            OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("扫描菜单脚本【" + extFileNum + "】"));
            OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("现有菜单【" + menuCache.size() + "】"));
            OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("扫描菜单脚本 结束"));
        } catch (IOException e) {
            OutputUtils.info(logs, e.getMessage());
        }
    }

    public void check() throws Exception {
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("analysis 开始"));
        analysis();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("analysis 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("dailyOperationsRouter 开始"));
        dailyOperationsRouter();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("dailyOperationsRouter 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundAccount 开始"));
        fundAccount();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundAccount 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundDataPermission 开始"));
        fundDataPermission();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundDataPermission 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundOnlineParameters 开始"));
        fundOnlineParameters();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundOnlineParameters 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundOrder 开始"));
        fundOrder();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundOrder 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundReport 开始"));
        fundReport();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundReport 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundTrade 开始"));
        fundTrade();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("fundTrade 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("specialHandling 开始"));
        specialHandling();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("specialHandling 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("sysinfoRouter 开始"));
        sysinfoRouter();
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("sysinfoRouter 结束"));

        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("汇总统计 开始"));
        needAddMenu.add(0, "-- 待处理菜单总共【" + needAddMenuNum + "】");
        FileUtils.writeFile(resultPath + "menu.sql", needAddMenu, false);
        OutputUtils.info(logs, SystemToolController.getCheckMenuMsg("汇总统计 结束"));
    }

    private void sysinfoRouter() throws Exception {
        List<String> sysinfoRouter = FileUtils.readNormalFile(basePathRouter + "sysinfoRouter.js", false);
        Map<String, String> menuMap = initMenuRouter(sysinfoRouter);
        compareMenu(menuMap, "sysinfoRouter.sql");
    }

    private void specialHandling() throws Exception {
        List<String> specialHandling = FileUtils.readNormalFile(basePathRouter + "specialHandling.js", false);
        Map<String, String> menuMap = initMenuRouter(specialHandling);
        compareMenu(menuMap, "specialHandling.sql");
    }

    private void fundTrade() throws Exception {
        List<String> fundTrade = FileUtils.readNormalFile(basePathRouter + "fundTrade.js", false);
        Map<String, String> menuMap = initMenuRouter(fundTrade);
        compareMenu(menuMap, "fundTrade.sql");
    }

    private void fundReport() throws Exception {
        List<String> fundReport = FileUtils.readNormalFile(basePathRouter + "fundReport.js", false);
        Map<String, String> menuMap = initMenuRouter(fundReport);
        compareMenu(menuMap, "fundReport.sql");
    }

    private void fundOrder() throws Exception {
        List<String> fundOrder = FileUtils.readNormalFile(basePathRouter + "fundOrder.js", false);
        Map<String, String> menuMap = initMenuRouter(fundOrder);
        compareMenu(menuMap, "fundOrder.sql");
    }

    private void fundOnlineParameters() throws Exception {
        List<String> fundOnlineParameters = FileUtils.readNormalFile(basePathRouter + "fundOnlineParameters.js", false);
        Map<String, String> menuMap = initMenuRouter(fundOnlineParameters);
        compareMenu(menuMap, "fundOnlineParameters.sql");
    }

    private void fundDataPermission() throws Exception {
        List<String> fundDataPermission = FileUtils.readNormalFile(basePathRouter + "fundDataPermission.js", false);
        Map<String, String> menuMap = initMenuRouter(fundDataPermission);
        compareMenu(menuMap, "fundDataPermission.sql");
    }

    private void fundAccount() throws Exception {
        List<String> fundAccount = FileUtils.readNormalFile(basePathRouter + "fundAccount.js", false);
        Map<String, String> menuMap = initMenuRouter(fundAccount);
        compareMenu(menuMap, "fundAccount.sql");
    }

    private void dailyOperationsRouter() throws Exception {
        List<String> dailyOperationsRouter = FileUtils.readNormalFile(basePathRouter + "dailyOperationsRouter.js", false);
        Map<String, String> menuMap = initMenuRouter(dailyOperationsRouter);
        compareMenu(menuMap, "dailyOperationsRouter.sql");
    }


    private void analysis() throws Exception {
        List<String> analysis = FileUtils.readNormalFile(basePathRouter + "analysis.js", false);
        List<String> analysisByTrans = FileUtils.readNormalFile(basePathRouter + "analysisByTrans.js", false);
        Map<String, String> menuMap = initMenuRouter(analysis);
        menuMap.putAll(initMenuRouter(analysisByTrans));
        compareMenu(menuMap, "analysis.sql");
    }

    private void compareMenu(Map<String, String> menuMap, String fileName) throws Exception {
        Map<String, String> menu = new LinkedHashMap();
        Iterator iterator = menuMap.keySet().iterator();
        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            if (!menu.containsKey(key) && !menu.containsKey(key + "T") && !menu.containsKey(key.substring(0, key.length() - 1))) {
                menu.put(key, menuMap.get(key));
            }
        }
        Map<String, String> extend = new LinkedHashMap();
        Iterator checkIterator = menu.keySet().iterator();
        while(checkIterator.hasNext()) {
            String key = (String)checkIterator.next();
            if (!menuExistCache.containsKey(key) && !menuExistCache.containsKey(key + "T")) {
                extend.put(key, menu.get(key));
            }
        }
        Iterator<String> extendIterator = extend.keySet().iterator();

        List<String> menuCodeList = new ArrayList<>();
        while(extendIterator.hasNext()) {
            String menuCode = extendIterator.next();
            if (!skip.contains(menuCode)) {
                List<String> menuInfo = menuCache.get(menuCode);
                if (menuInfo != null) {
                    menuCode += "   " + menuInfo.get(0) + "\n";
                    for (int i=1; i<menuInfo.size(); i++) {
                        menuCode += "   --" + menuInfo.get(i) + "\n";
                    }
                } else {
                    menuCode += "\n";
                }
                menuCodeList.add(menuCode);
                needAddMenuNum++;
            }
        }
        menuCodeList.add(0, "-- 待处理菜单【" + menuCodeList.size() + "】\n");
        needAddMenu.add("\n\n-- ************************ " + fileName.replace(".sql", " ************************"));
        needAddMenu.addAll(menuCodeList);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (STR_0.equals(appConfigDto.getSystemToolCheckMenuSubFile()) && !"menu.sql".equals(fileName)) {
            return;
        }
        FileUtils.writeFile(resultPath + fileName, menuCodeList, false);
    }

    private Map<String, String> initConfigMenuCache() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> config = FileUtils.readNormalFile(newUedPage, false);
        Iterator<String> iterator = config.iterator();
        while(iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                break;
            }
            String menuCode = getMenuCode(item);
            if (menuCode != null) {
                menuExistCache.put(menuCode, menuCode);
            }
        }
        return menuExistCache;
    }

    private static String handleMenu(String item) {
        if (item.contains("values") || item.contains("VALUES")) {
            if (item.split("values").length > 1) {
                return item.split("values")[1];
            } else if (item.split("VALUES").length > 1) {
                return item.split("VALUES")[1];
            }
        }
        return null;
    }

    private static String getMenuCode(String item) {
        String menuCode = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            menuCode = menuCodeInfo[0];
            if (menuCode.contains("'")) {
                menuCode = menuCode.split("'")[1];
            }
        }
        return menuCode;
    }

    private static String getMenuName(String item) {
        String menuName = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            menuName = menuCodeInfo[4];
            if (menuName.contains("'")) {
                menuName = menuName.split("'")[1];
            }
        }
        return menuName;
    }

    private static Map<String, String> initMenuRouter(List<String> menu) {
        Map<String, String> menuMap = new LinkedHashMap();
        Iterator<String> iterator = menu.iterator();
        while(iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains("=>")) {
                String menuCode = item.split("=>")[0].split(":")[0].trim();
                if (!menuCode.startsWith("//")) {
                    menuMap.put(menuCode, menuCode);
                }
            }
        }
        return menuMap;
    }

    private void initMenuExt() throws IOException {
        initMenuByFile(new File(baseMenu));
        extFileNum++;
        File fileExt = new File(basePathExt);
        for (File file : fileExt.listFiles()) {
            initMenuByFile(file);
        }
    }

    private void initMenuByFile(File file) throws IOException {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                initMenuByFile(item);
            }
        } else {
            List<String> content = FileUtils.readNormalFile(file.getPath(), false);
            boolean endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete") || item.toLowerCase().contains("tsys_menu_std") || item.toLowerCase().contains("tsys_menu_ext")) {
                    continue;
                }
                if (item.toLowerCase().contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (item.contains("tsys_menu") || item.contains("TSYS_MENU")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = getMenuCode(item);
                    String menuName = getMenuName(item);
                    if (menuCode != null && menuName != null) {
                        String filePath = file.getPath();
                        if (menuCache.containsKey(menuCode)) {
                            menuCache.get(menuCode).add(filePath);
                        } else {
                            List<String> menu = new ArrayList<>();
                            menu.add(menuName);
                            if (!filePath.endsWith("07console-fund-ta-vue-menu.sql")) {
                                menu.add(filePath);
                            }
                            menuCache.put(menuCode, menu);
                        }
                        extFileNum++;
                    }
                    endFlag = true;
                }
            }
        }
    }
}
