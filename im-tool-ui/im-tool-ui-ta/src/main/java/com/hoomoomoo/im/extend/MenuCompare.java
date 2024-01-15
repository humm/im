package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.SystemToolController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
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
    private String newUedPage = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu-new-ued.sql";
    // 扫描文件数量
    private int extFileNum = 0;
    private int needAddUedMenuNum = 0;
    // 所有菜单
    private Map<String, String> totalMenu = new HashMap<>();
    // 新版ued需增加菜单
    private List<String> needAddUedMenu = new ArrayList<>();
    // 所有菜单缓存
    private Map<String, List<String>> menuCache = new LinkedHashMap<>();
    // 增值功能菜单缓存
    private Map<String, List<String>> menuExtCache = new LinkedHashMap<>();
    // 所有交易码缓存
    private Map<String, List<String>> transCache = new LinkedHashMap<>();
    // 新版ued已配置菜单
    private Map<String, String> menuUedExistCache = new LinkedHashMap<>();
    // 全量脚本已配置菜单
    private Map<String, String> menuBaseExistCache = new LinkedHashMap<>();
    // 新版ued菜单忽略
    Set<String> skipMenuCache = new HashSet<>();
    // 全量菜单忽略
    Set<String> skipBaseMenuCache = new HashSet<>();
    // 路由信息忽略
    Set<String> skipRouterCache = new HashSet<>();

    public MenuCompare() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        try {
            String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
            if (StringUtils.isBlank(basePath)) {
                 System.out.print(SystemToolController.getCheckMenuMsg("请配置参数【system.tool.check.menu.base.path】"));
                throw new Exception("请配置参数【system.tool.check.menu.base.path】");
            }
            String resPath = appConfigDto.getSystemToolCheckMenuResultPath();
            if (StringUtils.isBlank(resPath)) {
                System.out.print(SystemToolController.getCheckMenuMsg("请配置参数【system.tool.check.menu.result.path】"));
                throw new Exception("请配置参数【system.tool.check.menu.result.path】");
            }
            basePathExt = basePath + basePathExt;
            basePathRouter = basePath + basePathRouter;
            baseMenu = basePath + baseMenu;
            newUedPage = basePath + newUedPage;
            resultPath = resPath + "\\";

            String confPath = FileUtils.getFilePath(PATH_MENU_SKIP);
            List<String> content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipMenuCache);

            confPath = FileUtils.getFilePath(PATH_MENU_BASE_SKIP);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipBaseMenuCache);
            
            confPath = FileUtils.getFilePath(PATH_ROUTER_SKIP);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipRouterCache);

            System.out.print(SystemToolController.getCheckMenuMsg("扫描菜单脚本 开始"));
            initMenuExt(appConfigDto);
            initConfigUedMenuCache();
            initConfigBaseMenuCache();
            System.out.print(SystemToolController.getCheckMenuMsg("扫描菜单脚本【" + extFileNum + "】"));
            System.out.print(SystemToolController.getCheckMenuMsg("现有菜单【" + menuCache.size() + "】"));
            System.out.print(SystemToolController.getCheckMenuMsg("扫描菜单脚本 结束"));
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }

    private void initSkipCache(List<String> content, Set<String> skipCache) {
        for (String item : content) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            String[] element = item.split(STR_SPACE);
            for (String ele : element) {
                if (StringUtils.isBlank(ele)) {
                    continue;
                }
                skipCache.add(ele.trim());
            }
        }
    }

    public void check() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        File file = new File(basePathRouter);
        if (!file.isDirectory()) {
            System.out.print(SystemToolController.getCheckMenuMsg("路由文件目录不存在，清检查"));
            return;
        }
        Set<String> skipRouter = new HashSet<>();
        String systemToolCheckMenuSkipRouter = appConfigDto.getSystemToolCheckMenuSkipRouter();
        if (StringUtils.isNotBlank(systemToolCheckMenuSkipRouter)) {
            System.out.print(SystemToolController.getCheckMenuMsg("忽略路由文件 " + systemToolCheckMenuSkipRouter));
            String[] skip = systemToolCheckMenuSkipRouter.split(",");
            for (String item : skip) {
                skipRouter.add(item);
            }
        }
        File[] fileList = file.listFiles();
        for (File item : fileList) {
            String fileName = item.getName().replace(".js", "");
            if ("analysisByTrans".equals(fileName) || skipRouter.contains(fileName)) {
                continue;
            }
            System.out.print(SystemToolController.getCheckMenuMsg(fileName + " 开始"));
            List<String> menuCodeList = FileUtils.readNormalFile(item.getPath(), false);
            Map<String, String> menuMap = initMenuRouter(menuCodeList);
            if ("analysis".equals(fileName)) {
                List<String> extendMenuCodeList = FileUtils.readNormalFile(basePathRouter + "analysisByTrans.js", false);
                menuMap.putAll(initMenuRouter(extendMenuCodeList));
            }
            compareMenu(menuMap, fileName + ".sql");
            System.out.print(SystemToolController.getCheckMenuMsg(fileName + " 结束"));
        }
        System.out.print(SystemToolController.getCheckMenuMsg("统计 开始"));
        needAddUedMenu.add(0, "-- 待处理【" + needAddUedMenuNum + "】");
        needAddUedMenu.add(0, "-- ************************************* 存在老版UED菜单 缺少新版UED菜单 *************************************");
        FileUtils.writeFile(resultPath + "lackMenu.sql", needAddUedMenu, false);
        System.out.print(SystemToolController.getCheckMenuMsg("统计 结束"));

        System.out.print(SystemToolController.getCheckMenuMsg("路由检查 开始"));
        Set<String> lackRouter = new HashSet<>();
        Iterator<String> iterator = menuCache.keySet().iterator();
        while (iterator.hasNext()) {
            String menuCode = iterator.next();
            if (!totalMenu.containsKey(menuCode) && transCache.containsKey(menuCode) && !skipRouterCache.contains(menuCode)) {
                lackRouter.add(menuCode);
            }
        }
        List<String> menuCodeList = new ArrayList<>();
        Iterator<String> iteratorLack = lackRouter.iterator();
        while (iteratorLack.hasNext()) {
            String menuCode = iteratorLack.next();
            menuCodeList.add(buildMenuCodeInfo(menuCode));
        }
        menuCodeList.add(0, "-- 待处理【" + lackRouter.size() + "】\n\n");
        menuCodeList.add(0, "-- ************************************* 存在脚本配置 缺少路由信息 *************************************");
        FileUtils.writeFile(resultPath + "lackRouter.sql", menuCodeList, false);
        System.out.print(SystemToolController.getCheckMenuMsg("路由检查 结束"));

        System.out.print(SystemToolController.getCheckMenuMsg("全量脚本检查 开始"));
        Set<String> lackMenu = new HashSet<>();
        Iterator<String> iteratorExt = menuExtCache.keySet().iterator();
        while (iteratorExt.hasNext()) {
            String menuCode = iteratorExt.next();
            if (!menuBaseExistCache.containsKey(menuCode) && transCache.containsKey(menuCode) && !skipBaseMenuCache.contains(menuCode)) {
                lackMenu.add(menuCode);
            }
        }
        List<String> lackMenuList = new ArrayList<>();
        Iterator<String> iteratorLackMenu = lackMenu.iterator();
        while (iteratorLackMenu.hasNext()) {
            String menuCode = iteratorLackMenu.next();
            lackMenuList.add(buildMenuCodeInfo(menuCode));
        }
        lackMenuList.add(0, "-- 待处理【" + lackMenu.size() + "】\n\n");
        lackMenuList.add(0, "-- ************************************* 存在开通脚本菜单 缺少老版本全量菜单 *************************************");
        FileUtils.writeFile(resultPath + "lackBaseMenu.sql", lackMenuList, false);
        System.out.print(SystemToolController.getCheckMenuMsg("全量脚本检查 结束"));

        System.out.print(SystemToolController.getCheckMenuMsg("所有菜单检查 开始"));
        Iterator<String> menuIterator = menuCache.keySet().iterator();
        List<String> menu = new ArrayList<>();
        Set<String> existMenu = new HashSet<>();
        while (menuIterator.hasNext()) {
            String menuCode = menuIterator.next();
            if (!skipMenuCache.contains(menuCode) && transCache.containsKey(menuCode)) {
                if (existMenu.contains(menuCode) || existMenu.contains(menuCode + "T") || existMenu.contains(menuCode.substring(0, menuCode.length() - 1))) {
                    continue;
                }
                existMenu.add(menuCode);
                menu.add(buildMenuCodeInfo(menuCode));
            }
        }
        menu.add(0, "-- 菜单总数【" + existMenu.size() + "】\n\n");
        menu.add(0, "-- ************************************* 自建TA所有菜单 *************************************");
        FileUtils.writeFile(resultPath + "menu.sql", menu, false);
        System.out.print(SystemToolController.getCheckMenuMsg("所有菜单检查 结束"));


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
            if (!menuUedExistCache.containsKey(key) && !menuUedExistCache.containsKey(key + "T")) {
                extend.put(key, menu.get(key));
            }
        }
        Iterator<String> extendIterator = extend.keySet().iterator();

        List<String> menuCodeList = new ArrayList<>();
        while(extendIterator.hasNext()) {
            String menuCode = extendIterator.next();
            if (!skipMenuCache.contains(menuCode)) {
                menuCodeList.add(buildMenuCodeInfo(menuCode));
                needAddUedMenuNum++;
            }
        }
        totalMenu.putAll(menuMap);
        menuCodeList.add(0, "-- 待处理【" + menuCodeList.size() + "】\n");
        needAddUedMenu.add("\n\n-- ************************************* " + fileName.replace(".sql", " *************************************"));
        needAddUedMenu.addAll(menuCodeList);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (STR_0.equals(appConfigDto.getSystemToolCheckMenuSubFile()) && !"lackMenu.sql".equals(fileName)) {
            return;
        }
        FileUtils.writeFile(resultPath + fileName, menuCodeList, false);
    }

    private String buildMenuCodeInfo(String menuCode) {
        List<String> menuInfo = menuCache.get(menuCode);
        if (menuInfo != null) {
            menuCode += "   " + menuInfo.get(0) + "\n";
            for (int i=1; i<menuInfo.size(); i++) {
                menuCode += "   --" + menuInfo.get(i) + "\n";
            }
        } else {
            menuCode += "\n";
        }
        return menuCode;
    }

    private void initConfigUedMenuCache() throws Exception {
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
                menuUedExistCache.put(menuCode, menuCode);
            }
        }
    }

    private void initConfigBaseMenuCache() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> config = FileUtils.readNormalFile(baseMenu, false);
        Iterator<String> iterator = config.iterator();
        while(iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                break;
            }
            String menuCode = getMenuCode(item);
            if (menuCode != null) {
                menuBaseExistCache.put(menuCode, menuCode);
            }
        }
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

    private void initMenuExt(AppConfigDto appConfigDto) throws IOException {
        File fileExt = new File(basePathExt);
        for (File file : fileExt.listFiles()) {
            initMenuByFile(appConfigDto, file);
        }
        // 缓存增值功能
        Iterator<String> iterator = menuCache.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            List<String> menu = menuCache.get(key);
            menuExtCache.put(key, menu);
        }
        initMenuByFile(appConfigDto, new File(baseMenu));
        extFileNum++;
    }

    private void initMenuByFile(AppConfigDto appConfigDto, File file) throws IOException {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                initMenuByFile(appConfigDto, item);
            }
        } else {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            System.out.print(SystemToolController.getCheckMenuMsg("扫描文件" + fileName));
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
                            if (addFilePath(filePath)) {
                                menuCache.get(menuCode).add(filePath);
                            }
                        } else {
                            List<String> menu = new ArrayList<>();
                            menu.add(menuName);
                            if (addFilePath(filePath)) {
                                menu.add(filePath);
                            }
                            menuCache.put(menuCode, menu);
                        }
                        extFileNum++;
                    }
                    endFlag = true;
                }
            }

            for (String item : content) {
                if (item.toLowerCase().contains("delete") || item.toLowerCase().contains("tsys_menu_std") || item.toLowerCase().contains("tsys_menu_ext")) {
                    continue;
                }
                if (item.toLowerCase().contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (item.contains("tsys_trans") || item.contains("TSYS_TRANS")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = getMenuCode(item);
                    if (menuCode != null) {
                        String filePath = file.getPath();
                        if (filePath.endsWith("newUedPage.sql")) {
                            continue;
                        }
                        if (transCache.containsKey(menuCode)) {
                            if (addFilePath(filePath)) {
                                transCache.get(menuCode).add(filePath);
                            }
                        } else {
                            List<String> menu = new ArrayList<>();
                            if (addFilePath(filePath)) {
                                menu.add(filePath);
                            }
                            transCache.put(menuCode, menu);
                        }
                    }
                    endFlag = true;
                }
            }
        }
    }

    private static boolean addFilePath(String path) {
        return !path.endsWith("07console-fund-ta-vue-menu.sql");
    }
}