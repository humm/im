package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.SystemToolController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class MenuCompareSql {

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
    // 老版本增值功能菜单配置详情
    private Map<String, Map<String, String>> oldMenuExtCache = new LinkedHashMap<>();
    // 老版本全量菜单配置详情
    private Map<String, Map<String, String>> oldMenuBaseCache = new LinkedHashMap<>();
    // 新版本增值功能菜单配置详情
    private Map<String, Map<String, String>> newMenuExtCache = new LinkedHashMap<>();
    // 新版本全量菜单配置详情
    private Map<String, Map<String, String>> newMenuBaseCache = new LinkedHashMap<>();
    // 子交易码缓存
    private Map<String, List<String>> subTransCache = new LinkedHashMap<>();
    // 日志缓存
    private Map<String, List<String>> subTransExtCache = new LinkedHashMap<>();
    // 所有交易码缓存
    private Map<String, List<String>> transCache = new LinkedHashMap<>();
    // 新版ued已配置菜单
    private Map<String, String> menuUedExistCache = new LinkedHashMap<>();
    // 全量脚本已配置菜单
    private Map<String, String> menuBaseExistCache = new LinkedHashMap<>();
    // 忽略全量新版
    Set<String> skipNewMenuCache = new HashSet<>();
    // 忽略全量老版
    Set<String> skipOldMenuCache = new HashSet<>();
    // 忽略路由信息
    Set<String> skipRouterCache = new HashSet<>();
    // 忽略全量开通新版
    Set<String> skipNewDiffMenuCache = new HashSet<>();
    // 忽略全量开通老版
    Set<String> skipOldDiffMenuCache = new HashSet<>();
    // 配置忽略路由
    Set<String> skipLogCache = new HashSet<>();
    // 忽略日志信息
    Set<String> skipErrorLogCache = new HashSet<>();

    public MenuCompareSql() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        try {
            String basePath = appConfigDto.getSystemToolCheckMenuBasePath();
            if (StringUtils.isBlank(basePath)) {
                throw new Exception("请配置参数【system.tool.check.menu.base.path】\n");
            }
            String resPath = appConfigDto.getSystemToolCheckMenuResultPath();
            if (StringUtils.isBlank(resPath)) {
                throw new Exception("请配置参数【system.tool.check.menu.result.path】\n");
            }
            basePathExt = basePath + basePathExt;
            basePathRouter = basePath + basePathRouter;
            baseMenu = basePath + baseMenu;
            newUedPage = basePath + newUedPage;
            resultPath = resPath + "\\";

            String confPath = FileUtils.getFilePath(PATH_SKIP_NEW_MENU);
            List<String> content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipNewMenuCache);

            confPath = FileUtils.getFilePath(PATH_SKIP_OLD_MENU);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipOldMenuCache);

            confPath = FileUtils.getFilePath(PATH_SKIP_ROUTER);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipRouterCache);

            confPath = FileUtils.getFilePath(PATH_SKIP_NEW_DIFF_MENU);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipNewDiffMenuCache);

            confPath = FileUtils.getFilePath(PATH_SKIP_OLD_DIFF_MENU);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipCache(content, skipOldDiffMenuCache);

            confPath = FileUtils.getFilePath(PATH_SKIP_LOG);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipTransCache(content, skipLogCache);

            confPath = FileUtils.getFilePath(PATH_SKIP_ERROR_LOG);
            content = FileUtils.readNormalFile(confPath, false);
            initSkipTransCache(content, skipErrorLogCache);

            System.out.print(SystemToolController.getCheckMenuMsg("扫描菜单脚本 开始"));
            System.out.print(SystemToolController.getCheckMenuMsg("扫描开通脚本 开始"));
            initBaseAndExtMenuCache();
            System.out.print(SystemToolController.getCheckMenuMsg("扫描开通脚本 结束"));
            System.out.print(SystemToolController.getCheckMenuMsg("扫描新版全量脚本 开始"));
            initConfigUedMenuCache();
            System.out.print(SystemToolController.getCheckMenuMsg("扫描新版全量脚本 结束"));
            System.out.print(SystemToolController.getCheckMenuMsg("扫描老板全量脚本 开始"));
            initConfigBaseMenuCache();
            System.out.print(SystemToolController.getCheckMenuMsg("扫描老板全量脚本 结束"));
            System.out.print(SystemToolController.getCheckMenuMsg("扫描菜单脚本【" + extFileNum + "】"));
            System.out.print(SystemToolController.getCheckMenuMsg("现有菜单【" + menuCache.size() + "】"));
            System.out.print(SystemToolController.getCheckMenuMsg("扫描菜单脚本 结束"));
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
    }

    private void initSkipTransCache(List<String> content, Set<String> skipCache) {
        for (String item : content) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            String[] element = item.split("-");
            String transCode = STR_BLANK;
            String tranSubCode = STR_BLANK;
            for (int i=0; i<element.length; i++) {
                String ele = element[i].trim();
                if (StringUtils.isBlank(ele)) {
                    continue;
                }
                if (i == 0) {
                    transCode = ele.split(STR_SPACE)[0].trim();
                } else if (i == 1) {
                    tranSubCode = ele.split(STR_SPACE)[0].trim();
                }
            }
            skipCache.add(transCode + " - " + tranSubCode);
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
        needAddUedMenu.add(0, "-- ************************************* 全量新版UED缺少菜单 *************************************");
        FileUtils.writeFile(resultPath + "1.全量新版UED缺少菜单.sql", needAddUedMenu, false);
        System.out.print(SystemToolController.getCheckMenuMsg("统计 结束"));

        System.out.print(SystemToolController.getCheckMenuMsg("全量老版菜单检查 开始"));
        Set<String> lackMenu = new HashSet<>();
        Iterator<String> iteratorExt = menuExtCache.keySet().iterator();
        while (iteratorExt.hasNext()) {
            String menuCode = iteratorExt.next();
            if (!menuBaseExistCache.containsKey(menuCode) && transCache.containsKey(menuCode) && !skipOldMenuCache.contains(menuCode)) {
                lackMenu.add(menuCode);
            }
        }
        List<String> lackMenuList = new ArrayList<>();
        Iterator<String> iteratorLackMenu = lackMenu.iterator();
        while (iteratorLackMenu.hasNext()) {
            String menuCode = iteratorLackMenu.next();
            lackMenuList.add(buildMenuInfo(menuCode));
        }
        lackMenuList.add(0, "-- 待处理【" + lackMenu.size() + "】\n\n");
        lackMenuList.add(0, "-- ************************************* 全量老版UED缺少菜单 *************************************");
        FileUtils.writeFile(resultPath + "2.全量老版UED缺少菜单.sql", lackMenuList, false);
        System.out.print(SystemToolController.getCheckMenuMsg("全量老版菜单检查 结束"));

        // 新版UED菜单 全量开通不一致
        System.out.print(SystemToolController.getCheckMenuMsg("新版UED菜单全量开通不一致检查 开始"));
        Map<String, List<String>> newMenDiff = new LinkedHashMap<>();
        Iterator<String> newMenuIterator = newMenuExtCache.keySet().iterator();
        while (newMenuIterator.hasNext()) {
            String menuCode = newMenuIterator.next();
            if (skipNewDiffMenuCache.contains(menuCode)) {
                continue;
            }
            Map<String, String> menuInfo = newMenuExtCache.get(menuCode);
            Map<String, String> baseMenuInfo = newMenuBaseCache.get(menuCode);
            if (MapUtils.isNotEmpty(baseMenuInfo)) {
                String menuValue = baseMenuInfo.get(newUedPage);
                Iterator<String> menuInfoIterator = menuInfo.keySet().iterator();
                List<String> diffValue = new ArrayList<>();
                while (menuInfoIterator.hasNext()) {
                    String filePath = menuInfoIterator.next();
                    String menuValueExt = menuInfo.get(filePath);
                    if (!StringUtils.equals(menuValue, menuValueExt)) {
                        if (newMenDiff.containsKey(menuCode)) {
                            newMenDiff.get(menuCode).add(filePath);
                            diffValue.add(buildDiffValueInfo(menuValueExt));
                        } else {
                            List<String> pathList = new ArrayList<>();
                            List<String> menuName = menuCache.get(menuCode);
                            if (CollectionUtils.isNotEmpty(menuName)) {
                                pathList.add(menuName.get(0));
                            } else {
                                pathList.add(STR_SPACE);
                            }
                            pathList.add(filePath);
                            newMenDiff.put(menuCode, pathList);
                            diffValue.add(buildDiffValueInfo(menuValue));
                            diffValue.add(buildDiffValueInfo(menuValueExt));
                        }
                    }
                }
                if (newMenDiff.containsKey(menuCode)) {
                    newMenDiff.get(menuCode).addAll(diffValue);
                }
            }
        }
        List<String> newMenDiffInfo = new ArrayList<>();
        Iterator<String> newMenDiffIterator = newMenDiff.keySet().iterator();
        while (newMenDiffIterator.hasNext()) {
            String menuCode = newMenDiffIterator.next();
            newMenDiffInfo.add(buildMenuInfo(newMenDiff, menuCode));
        }
        newMenDiffInfo.add(0, "-- 待处理【" + newMenDiff.size() + "】\n\n");
        newMenDiffInfo.add(0, "-- ************************************* 新版UED菜单全量开通不一致 *************************************");
        FileUtils.writeFile(resultPath + "3.新版UED菜单全量开通不一致.sql", newMenDiffInfo, false);
        System.out.print(SystemToolController.getCheckMenuMsg("新版UED菜单全量开通不一致检查 结束"));

        // 老板UED菜单 全量开通不一致
        System.out.print(SystemToolController.getCheckMenuMsg("老板UED菜单全量开通不一致检查 开始"));
        Map<String, List<String>> oldMenDiff = new LinkedHashMap<>();
        Iterator<String> oldMenuIterator = oldMenuExtCache.keySet().iterator();
        while (oldMenuIterator.hasNext()) {
            String menuCode = oldMenuIterator.next();
            if (skipOldDiffMenuCache.contains(menuCode)) {
                continue;
            }
            Map<String, String> menuInfo = oldMenuExtCache.get(menuCode);
            Map<String, String> baseMenuInfo = oldMenuBaseCache.get(menuCode);
            if (MapUtils.isNotEmpty(baseMenuInfo)) {
                String menuValue = baseMenuInfo.get(baseMenu);
                Iterator<String> menuInfoIterator = menuInfo.keySet().iterator();
                List<String> diffValue = new ArrayList<>();
                while (menuInfoIterator.hasNext()) {
                    String filePath = menuInfoIterator.next();
                    String menuValueExt = menuInfo.get(filePath);
                    if (!StringUtils.equals(menuValue, menuValueExt)) {
                        if (oldMenDiff.containsKey(menuCode)) {
                            oldMenDiff.get(menuCode).add(filePath);
                            diffValue.add(buildDiffValueInfo(menuValueExt));
                        } else {
                            List<String> pathList = new ArrayList<>();
                            List<String> menuName = menuCache.get(menuCode);
                            if (CollectionUtils.isNotEmpty(menuName)) {
                                pathList.add(menuName.get(0));
                            } else {
                                pathList.add(STR_SPACE);
                            }
                            pathList.add(filePath);
                            oldMenDiff.put(menuCode, pathList);
                            diffValue.add(buildDiffValueInfo(menuValue));
                            diffValue.add(buildDiffValueInfo(menuValueExt));
                        }
                    }
                }
                if (oldMenDiff.containsKey(menuCode)) {
                    oldMenDiff.get(menuCode).addAll(diffValue);
                }
            }
        }
        List<String> oldMenDiffInfo = new ArrayList<>();
        Iterator<String> oldMenDiffIterator = oldMenDiff.keySet().iterator();
        while (oldMenDiffIterator.hasNext()) {
            String menuCode = oldMenDiffIterator.next();
            oldMenDiffInfo.add(buildMenuInfo(oldMenDiff, menuCode));
        }
        oldMenDiffInfo.add(0, "-- 待处理【" + oldMenDiff.size() + "】\n\n");
        oldMenDiffInfo.add(0, "-- ************************************* 老板UED菜单全量开通不一致 *************************************");
        FileUtils.writeFile(resultPath + "4.老板UED菜单全量开通不一致.sql", oldMenDiffInfo, false);
        System.out.print(SystemToolController.getCheckMenuMsg("老板UED菜单全量开通不一致检查 结束"));

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
            menuCodeList.add(buildMenuInfo(menuCode));
        }
        menuCodeList.add(0, "-- 待处理【" + lackRouter.size() + "】\n\n");
        menuCodeList.add(0, "-- ************************************* 存在菜单缺少路由 *************************************");
        FileUtils.writeFile(resultPath + "5.存在菜单缺少路由.sql", menuCodeList, false);
        System.out.print(SystemToolController.getCheckMenuMsg("路由检查 结束"));

        // 缺少日志配置检查
        System.out.print(SystemToolController.getCheckMenuMsg("日志检查 开始"));
        Iterator<String> subTransIterator = subTransCache.keySet().iterator();
        List<String> subTransExtList = new ArrayList<>();
        while (subTransIterator.hasNext()) {
            String transCode = subTransIterator.next().trim();
            if (!subTransExtCache.containsKey(transCode) && !skipLogCache.contains(transCode) && !transCode.endsWith("QryC") && !transCode.endsWith("ColC")) {
                subTransExtList.add(buildMenuInfo(subTransCache, transCode));
            }
        }
        subTransExtList.add(0, "-- 待处理【" + subTransExtList.size() + "】\n\n");
        subTransExtList.add(0, "-- ************************************* 缺少日志信息 *************************************");
        FileUtils.writeFile(resultPath + "6.缺少日志信息.sql", subTransExtList, false);
        System.out.print(SystemToolController.getCheckMenuMsg("日志检查 结束"));

        // 日志配置错误检查
        System.out.print(SystemToolController.getCheckMenuMsg("日志错误检查 开始"));
        Iterator<String> subTransExtIterator = subTransExtCache.keySet().iterator();
        List<String> subTransExtErrorList = new ArrayList<>();
        while (subTransExtIterator.hasNext()) {
            String transCode = subTransExtIterator.next();
            if (skipErrorLogCache.contains(transCode)) {
                continue;
            }
            String subTransCode = transCode.split("-")[1].trim();
            String opDir = subTransExtCache.get(transCode).get(0).split("-")[0];
            // 0-新增 1-修改 2-删除 3-其他 4-查询 5-下载 6-导入
            boolean error = false;
            switch (opDir) {
                case STR_0:
                    if (!subTransCode.endsWith("Add")) {
                        error = true;
                    }
                    break;
                case STR_1:
                    if (!subTransCode.endsWith("Edit") && !subTransCode.endsWith("Edt")) {
                        error = true;
                    }
                    break;
                case STR_2:
                    if (!subTransCode.endsWith("Delete") && !subTransCode.endsWith("Del")) {
                        error = true;
                    }
                    break;
                case STR_3:
                    error = true;
                    break;
                case STR_4:
                    if (!subTransCode.endsWith("Query") && !subTransCode.endsWith("Qry")) {
                        error = true;
                    }
                    break;
                case STR_5:
                    if (!subTransCode.endsWith("Export") && !subTransCode.endsWith("Exp") && !subTransCode.endsWith("Download") && !subTransCode.endsWith("Dwn")) {
                        error = true;
                    }
                    break;
                case STR_6:
                    if (!subTransCode.endsWith("Import") && !subTransCode.endsWith("Imp")) {
                        error = true;
                    }
                    break;
                default:
                    error = true;
                    break;

            }
            if (error) {
                subTransExtErrorList.add(buildMenuTransInfo(subTransExtCache, transCode));
            }
        }
        subTransExtErrorList.add(0, "-- 待处理【" + subTransExtErrorList.size() + "】\n\n");
        subTransExtErrorList.add(0, "-- ************************************* 0-新增 1-修改 2-删除 3-其他 4-查询 5-下载 6-导入 *************************************");
        subTransExtErrorList.add(0, "-- ******************************************************* 日志错误信息 *******************************************************");
        FileUtils.writeFile(resultPath + "7.日志错误信息.sql", subTransExtErrorList, false);
        System.out.print(SystemToolController.getCheckMenuMsg("日志错误检查 结束"));

        System.out.print(SystemToolController.getCheckMenuMsg("所有菜单检查 开始"));
        Iterator<String> menuIterator = menuCache.keySet().iterator();
        List<String> menu = new ArrayList<>();
        Set<String> existMenu = new HashSet<>();
        while (menuIterator.hasNext()) {
            String menuCode = menuIterator.next();
            if (!skipNewMenuCache.contains(menuCode) && transCache.containsKey(menuCode)) {
                if (existMenu.contains(menuCode) || existMenu.contains(menuCode + "T") || existMenu.contains(menuCode.substring(0, menuCode.length() - 1))) {
                    continue;
                }
                existMenu.add(menuCode);
                menu.add(buildMenuInfo(menuCode));
            }
        }
        menu.add(0, "-- 菜单总数【" + existMenu.size() + "】\n\n");
        menu.add(0, "-- ************************************* 所有非弹窗菜单 *************************************");
        FileUtils.writeFile(resultPath + "8.所有非弹窗菜单.sql", menu, false);
        System.out.print(SystemToolController.getCheckMenuMsg("所有菜单检查 结束"));

        System.out.print(SystemToolController.getCheckMenuMsg("新版UED菜单合法性检查 开始"));
        Iterator<String> menuUedExistIterator = menuUedExistCache.keySet().iterator();
        List<String> uedMenu = new ArrayList<>();
        while (menuUedExistIterator.hasNext()) {
            String menuCode = menuUedExistIterator.next();
            String menuName = menuUedExistCache.get(menuCode);
            if (menuName.length() != menuName.trim().length()) {
                uedMenu.add(menuCode + "   " + menuName);
            }

        }
        int total = uedMenu.size();
        uedMenu.add(0, "-- 待处理【" + uedMenu.size() + "】\n\n");
        uedMenu.add(0, "-- ************************************* 菜单名称存在空格 *************************************");
        uedMenu.add(0, "-- 待处理【" + total + "】\n\n");
        uedMenu.add(0, "-- ************************************* 新版UED菜单合法性 *************************************");
        FileUtils.writeFile(resultPath + "9.新版UED菜单合法性.sql", uedMenu, false);
        System.out.print(SystemToolController.getCheckMenuMsg("新版UED菜单合法性检查 结束"));


    }

    private void compareMenu(Map<String, String> menuMap, String fileName) throws Exception {
        Map<String, String> menu = new LinkedHashMap();
        Iterator iterator = menuMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (!menu.containsKey(key) && !menu.containsKey(key + "T") && !menu.containsKey(key.substring(0, key.length() - 1))) {
                menu.put(key, menuMap.get(key));
            }
        }
        Map<String, String> extend = new LinkedHashMap();
        Iterator checkIterator = menu.keySet().iterator();
        while (checkIterator.hasNext()) {
            String key = (String) checkIterator.next();
            if (!menuUedExistCache.containsKey(key) && !menuUedExistCache.containsKey(key + "T")) {
                extend.put(key, menu.get(key));
            }
        }
        Iterator<String> extendIterator = extend.keySet().iterator();

        List<String> menuCodeList = new ArrayList<>();
        while (extendIterator.hasNext()) {
            String menuCode = extendIterator.next();
            if (!skipNewMenuCache.contains(menuCode)) {
                menuCodeList.add(buildMenuInfo(menuCode));
                needAddUedMenuNum++;
            }
        }
        totalMenu.putAll(menuMap);
        menuCodeList.add(0, "-- 待处理【" + menuCodeList.size() + "】\n");
        needAddUedMenu.add("\n\n-- ************************************* " + fileName.replace(".sql", " *************************************"));
        needAddUedMenu.addAll(menuCodeList);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (STR_0.equals(appConfigDto.getSystemToolCheckMenuSubFile()) && !"1.全量新版UED缺少菜单.sql".equals(fileName)) {
            return;
        }
        FileUtils.writeFile(resultPath + fileName, menuCodeList, false);
    }

    private String buildDiffValueInfo(String value) {
        return "  " + value;
    }

    private String buildMenuInfo(String menuCode) {
        List<String> menuInfo = menuCache.get(menuCode);
        if (menuInfo != null) {
            menuCode += "   " + menuInfo.get(0) + "\n";
            for (int i = 1; i < menuInfo.size(); i++) {
                menuCode += "   -- " + menuInfo.get(i) + "\n";
            }
        } else {
            menuCode += "\n";
        }
        return menuCode;
    }

    private String buildMenuTransInfo(Map<String, List<String>> menu, String menuCode) {
        List<String> menuInfo = menu.get(menuCode);
        if (menuInfo != null) {
            List<String> transInfo = subTransCache.get(menuCode);
            if (transInfo != null) {
                String transName = transInfo.get(0);
                menuCode += "   " + transName;
            }
            menuCode += "   " + menuInfo.get(0) + "\n";
            for (int i = 1; i < menuInfo.size(); i++) {
                menuCode += "   -- " + menuInfo.get(i) + "\n";
            }
        } else {
            menuCode += "\n";
        }
        return menuCode;
    }

    private String buildMenuInfo(Map<String, List<String>> menu, String menuCode) {
        List<String> menuInfo = menu.get(menuCode);
        if (menuInfo != null) {
            menuCode += "   " + menuInfo.get(0) + "\n";
            for (int i = 1; i < menuInfo.size(); i++) {
                menuCode += "   -- " + menuInfo.get(i) + "\n";
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
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                break;
            }
            String menuCode = getMenuCode(item);
            String menuName = getMenuName(item);
            if (menuCode != null) {
                if (getMenuValueLen(item) != 18) {
                    continue;
                }
                menuUedExistCache.put(menuCode, menuName);
                if (newMenuBaseCache.containsKey(menuCode)) {
                    newMenuBaseCache.get(menuCode).put(newUedPage, getMenuDetail(18, item));
                } else {
                    Map<String, String> fileMap = new HashMap<>();
                    fileMap.put(newUedPage, getMenuDetail(18, item));
                    newMenuBaseCache.put(menuCode, fileMap);
                }
            }
        }
    }

    private void initConfigBaseMenuCache() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<String> config = FileUtils.readNormalFile(baseMenu, false);
        Iterator<String> iterator = config.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                break;
            }
            String menuCode = getMenuCode(item);
            if (menuCode != null) {
                menuBaseExistCache.put(menuCode, menuCode);
                if (getMenuValueLen(item) != 16) {
                    continue;
                }
                if (oldMenuBaseCache.containsKey(menuCode)) {
                    oldMenuBaseCache.get(menuCode).put(baseMenu, getMenuDetail(16, item));
                } else {
                    Map<String, String> fileMap = new HashMap<>();
                    fileMap.put(baseMenu, getMenuDetail(16, item));
                    oldMenuBaseCache.put(menuCode, fileMap);
                }
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

    private static int getMenuValueLen(String item) {
        if (!item.contains("(") || !item.contains(")")) {
            return -1;
        }
        return item.substring(item.indexOf("(") + 1, item.lastIndexOf(")")).split(",").length;
    }

    private static String getMenuDetail(int len, String item) throws Exception {
        String res = STR_BLANK;
        item = handleMenu(item);
        if (item != null) {
            String[] value = MenuUpdateSql.handleValue(len, item.substring(item.indexOf("(") + 1, item.lastIndexOf(")")).split(","));
            for (String ele : value) {
                if (!StringUtils.isBlank(res)) {
                    res += "-";
                }
                res += ele;
            }
        }
        return res.trim().substring(1);
    }

    private static Map<String, String> initMenuRouter(List<String> menu) {
        Map<String, String> menuMap = new LinkedHashMap();
        Iterator<String> iterator = menu.iterator();
        while (iterator.hasNext()) {
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

    private void initBaseAndExtMenuCache() throws Exception {
        File fileExt = new File(basePathExt);
        for (File file : fileExt.listFiles()) {
            initMenuByFile(file);
        }
        // 缓存增值功能
        Iterator<String> iterator = menuCache.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            List<String> menu = menuCache.get(key);
            menuExtCache.put(key, menu);
        }
        initMenuByFile(new File(baseMenu));
        extFileNum++;
    }

    private void initMenuByFile(File file) throws Exception {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                initMenuByFile(item);
            }
        } else {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            //System.out.print(SystemToolController.getCheckMenuMsg("扫描文件" + fileName));
            List<String> content = FileUtils.readNormalFile(file.getPath(), false);
            // 缓存菜单信息
            boolean endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_menu_std") || item.toLowerCase().contains("tsys_menu_ext") || item.toLowerCase().contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_menu")) {
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

            // 缓存老版本菜单详情信息
            endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_menu_std") || item.toLowerCase().contains("tsys_menu_ext") || item.toLowerCase().contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_menu")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = getMenuCode(item);
                    if (menuCode != null) {
                        String filePath = file.getPath();
                        if (addFilePath(filePath)) {
                            if (oldMenuExtCache.containsKey(menuCode)) {
                                oldMenuExtCache.get(menuCode).put(filePath, getMenuDetail(16, item));
                            } else {
                                Map<String, String> fileMap = new HashMap<>();
                                fileMap.put(filePath, getMenuDetail(16, item));
                                oldMenuExtCache.put(menuCode, fileMap);
                            }
                        }
                    }
                    endFlag = true;
                }
            }

            // 缓存新版本菜单详情信息
            endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_menu ") || item.toLowerCase().contains("tsys_menu_ext") || item.toLowerCase().contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_menu_std")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = getMenuCode(item);
                    if (menuCode != null) {
                        String filePath = file.getPath();
                        if (addFilePath(filePath)) {
                            if (newMenuExtCache.containsKey(menuCode)) {
                                newMenuExtCache.get(menuCode).put(filePath, getMenuDetail(18, item));
                            } else {
                                Map<String, String> fileMap = new HashMap<>();
                                fileMap.put(filePath, getMenuDetail(18, item));
                                newMenuExtCache.put(menuCode, fileMap);
                            }
                        }
                    }
                    endFlag = true;
                }
            }

            // 缓存交易码信息
            endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete")) {
                    continue;
                }
                if (item.toLowerCase().contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_trans")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = getMenuCode(item);
                    if (menuCode != null) {
                        String filePath = file.getPath();
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

            // 缓存子交易码信息
            endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete")) {
                    continue;
                }
                if (item.toLowerCase().contains("tbfundgranttablestmp") || item.toLowerCase().contains("tsys_trans ") || item.toLowerCase().contains("tsys_subtrans_ext")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_subtrans")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    if (getMenuValueLen(item) != 11) {
                        continue;
                    }
                    String transCode = getSubTransCode(item);
                    String transName = getSubTransName(item);
                    if (transCode != null && transName != null) {
                        String filePath = file.getPath();
                        if (subTransCache.containsKey(transCode)) {
                            if (addFilePath(filePath)) {
                                subTransCache.get(transCode).add(filePath);
                            }
                        } else {
                            List<String> menu = new ArrayList<>();
                            menu.add(transName);
                            if (addFilePath(filePath)) {
                                menu.add(filePath);
                            }
                            subTransCache.put(transCode, menu);
                        }
                    }
                    endFlag = true;
                }
            }

            // 缓存日志信息
            endFlag = true;
            for (String item : content) {
                if (item.toLowerCase().contains("delete")) {
                    continue;
                }
                if (item.toLowerCase().contains("tbfundgranttablestmp") || item.toLowerCase().contains("tsys_subtrans ") || item.toLowerCase().contains("tsys_trans ")) {
                    continue;
                }
                if (item.toLowerCase().contains("tsys_subtrans_ext")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    if (getMenuValueLen(item) != 7) {
                        continue;
                    }
                    String transCode = getSubTransCode(item);
                    String transOpDir = getSubTransOpDir(item);
                    if (transCode != null) {
                        String filePath = file.getPath();
                        if (subTransExtCache.containsKey(transCode)) {
                            if (addFilePath(filePath)) {
                                subTransExtCache.get(transCode).add(filePath);
                            }
                        } else {
                            List<String> menu = new ArrayList<>();
                            menu.add(handleOpDir(transOpDir));
                            if (addFilePath(filePath)) {
                                menu.add(filePath);
                            }
                            subTransExtCache.put(transCode, menu);
                        }
                    }
                    endFlag = true;
                }
            }
        }
    }

    private static String handleOpDir(String opDir) {
        // 0-新增 1-修改 2-删除 3-其他 4-查询 5-下载 6-导入
        switch (opDir) {
            case STR_0:
                opDir = "0-新增";
                break;
            case STR_1:
                opDir = "1-修改";
                break;
            case STR_2:
                opDir = "2-删除";
                break;
            case STR_3:
                opDir = "3-其他";
                break;
            case STR_4:
                opDir = "4-查询";
                break;
            case STR_5:
                opDir = "5-下载";
                break;
            case STR_6:
                opDir = "6-导入";
                break;
            default:
                break;
        }
        return opDir;
    }

    private static String getSubTransCode(String item) {
        String transCode = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transCode = menuCodeInfo[0];
            if (transCode.contains("'")) {
                transCode = transCode.split("'")[1];
            }
            String subTransCode = menuCodeInfo[1];
            if (subTransCode.contains("'")) {
                subTransCode = subTransCode.split("'")[1];
            }
            transCode += " - " + subTransCode;
        }
        return transCode;
    }

    private static String getSubTransName(String item) {
        String transName = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transName = menuCodeInfo[2];
            if (transName.contains("'")) {
                transName = transName.split("'")[1];
            }
        }
        return transName;
    }

    private static String getSubTransOpDir(String item) {
        String transName = null;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            transName = menuCodeInfo[2];
            if (transName.contains("'")) {
                transName = transName.split("'")[1];
            }
        }
        return transName;
    }


    private static boolean addFilePath(String path) {
        return !path.endsWith("07console-fund-ta-vue-menu.sql");
    }
}