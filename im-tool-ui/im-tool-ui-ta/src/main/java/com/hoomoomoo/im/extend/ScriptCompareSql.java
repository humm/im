package com.hoomoomoo.im.extend;

import com.alibaba.fastjson.JSONObject;
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

public class ScriptCompareSql {

    private String resultPath = "";
    private String basePathExt = "\\sql\\pub\\001initdata\\extradata\\";
    private String basePathRouter = "\\front\\HUI1.0\\console-fund-ta-vue\\router\\modules\\";
    private String baseMenu = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu.sql";
    private String newUedPage = "\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu-new-ued.sql";
    // 扫描文件数量
    private int extFileNum = 0;
    private int needAddUedMenuNum = 0;
    // 所有菜单
    private Set<String> totalMenu = new HashSet<>();
    // 新版需增加菜单
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
    // 新版已配置菜单  menu_code/menu_name
    private Map<String, String> menuUedExistCache = new LinkedHashMap<>();
    // 新版已配置菜单  menu_code/menu_name reserve
    private Map<String, String[]> menuUedReserveCache = new LinkedHashMap<>();
    // 全量脚本已配置菜单
    private Set<String> menuBaseExistCache = new LinkedHashSet<>();
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
    // 路由缓存信息
    Set<String> menuRouterCache = new LinkedHashSet<>();

    public ScriptCompareSql() throws Exception {
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

            initBaseAndExtMenuCache();
            initConfigUedMenuCache();
            initConfigBaseMenuCache();
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

        // 缺少新版全量
        lackNewAllMenuCheck(appConfigDto);
        // 缺少老版全量
        lackOldAllMenuCheck(appConfigDto);
        // 新版全量开通不同
        newMenuAllDiffExtCheck(appConfigDto);
        // 老版全量开通不同
        oldMenuAllDiffExtCheck(appConfigDto);
        // 缺少路由
        lackRouterCheck(appConfigDto);
        // 缺少日志
        lackLogCheck(appConfigDto);
        // 错误日志
        logErrorCheck(appConfigDto);
        // 所有菜单
        allMenuStat(appConfigDto);
        // 新版菜单合法性
        newMenuLegalCheck(appConfigDto);
    }

    /**
     * 缺少新版全量
     */
    private void lackNewAllMenuCheck(AppConfigDto appConfigDto) throws Exception {
        File file = new File(basePathRouter);
        if (!file.isDirectory()) {
            throw new Exception("路由文件目录不存在，请检查\n");
        }
        Set<String> skipRouter = new HashSet<>();
        String systemToolCheckMenuSkipRouter = appConfigDto.getSystemToolCheckMenuSkipRouter();
        if (StringUtils.isNotBlank(systemToolCheckMenuSkipRouter)) {
            System.out.println(SystemToolController.getCheckMenuMsg("忽略路由文件 " + systemToolCheckMenuSkipRouter));
            String[] skip = systemToolCheckMenuSkipRouter.split(",");
            for (String item : skip) {
                skipRouter.add(item);
            }
        }
        File[] fileList = file.listFiles();
        Set<String> allRouterMenu = new HashSet<>();
        for (File item : fileList) {
            String fileName = item.getName().replace(".js", "");
            if ("analysisByTrans".equals(fileName) || skipRouter.contains(fileName)) {
                continue;
            }
            List<String> menuCodeList = FileUtils.readNormalFile(item.getPath(), false);
            Set<String> menuMap = initMenuRouter(menuCodeList);
            if ("analysis".equals(fileName)) {
                List<String> extendMenuCodeList = FileUtils.readNormalFile(basePathRouter + "analysisByTrans.js", false);
                menuMap.addAll(initMenuRouter(extendMenuCodeList));
            }
            allRouterMenu.addAll(menuMap);
            compareMenu(menuMap, fileName + ".sql");
            menuRouterCache.addAll(menuMap);
        }

        // 校验多余非四级菜单
        List<String> menuCodeList = new ArrayList<>();
        Map<String, Map<String, String>> menuTemp = JSONObject.parseObject(JSONObject.toJSONString(newMenuExtCache), Map.class);
        Iterator<String> allRouterIterator = allRouterMenu.iterator();
        while (allRouterIterator.hasNext()) {
            String key = allRouterIterator.next();
            menuTemp.remove(key);
            menuTemp.remove(key + "T");
            menuTemp.remove(key.substring(0, key.length() - 1));
        }
        Iterator<String> uedExistIterator = menuUedExistCache.keySet().iterator();
        while (uedExistIterator.hasNext()) {
            String menuCode = uedExistIterator.next();
            menuTemp.remove(menuCode);
            menuTemp.remove(menuCode + "T");
        }
        Iterator<String> menuTempIterator = menuTemp.keySet().iterator();
        while (menuTempIterator.hasNext()) {
            String menuCode = menuTempIterator.next();
            if (skipNewMenuCache.contains(menuCode)) {
                continue;
            }
            menuCodeList.add(buildMenuInfo(menuCode));
            needAddUedMenuNum++;
        }
        writeFile(menuCodeList, "extend.sql");
        needAddUedMenu.add(0, "-- 待处理【" + needAddUedMenuNum + "】");
        needAddUedMenu.add(0, "-- ************************************* 缺少新版全量 *************************************");
        FileUtils.writeFile(resultPath + "10.缺少新版全量.sql", needAddUedMenu, false);
    }

    /**
     * 缺少老版全量
     */
    private void lackOldAllMenuCheck(AppConfigDto appConfigDto) throws Exception {
        Set<String> lackMenu = new HashSet<>();
        Iterator<String> iteratorExt = menuExtCache.keySet().iterator();
        while (iteratorExt.hasNext()) {
            String menuCode = iteratorExt.next();
            if (!menuBaseExistCache.contains(menuCode) && transCache.containsKey(menuCode) && !skipOldMenuCache.contains(menuCode)) {
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
        lackMenuList.add(0, "-- ************************************* 缺少老版全量 *************************************");
        FileUtils.writeFile(resultPath + "20.缺少老版全量.sql", lackMenuList, false);
    }

    /**
     * 新版全量开通不同
     *
     * @throws IOException
     */
    private void newMenuAllDiffExtCheck(AppConfigDto appConfigDto) throws Exception {
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
        newMenDiffInfo.add(0, "-- ************************************* 新版全量开通不同 *************************************");
        FileUtils.writeFile(resultPath + "30.新版全量开通不同.sql", newMenDiffInfo, false);
    }

    /**
     * 老版全量开通不同
     *
     * @param appConfigDto
     * @throws Exception
     */
    private void oldMenuAllDiffExtCheck(AppConfigDto appConfigDto) throws Exception {
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
        oldMenDiffInfo.add(0, "-- ************************************* 老版全量开通不同 *************************************");
        FileUtils.writeFile(resultPath + "40.老版全量开通不同.sql", oldMenDiffInfo, false);
    }

    /**
     * 缺少路由
     *
     * @param appConfigDto
     * @throws Exception
     */
    private void lackRouterCheck(AppConfigDto appConfigDto) throws Exception {
        Set<String> lackRouter = new HashSet<>();
        Iterator<String> iterator = menuCache.keySet().iterator();
        while (iterator.hasNext()) {
            String menuCode = iterator.next();
            if (!totalMenu.contains(menuCode) && transCache.containsKey(menuCode) && !skipRouterCache.contains(menuCode)) {
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
        menuCodeList.add(0, "-- ************************************* 缺少路由 *************************************");
        FileUtils.writeFile(resultPath + "50.缺少路由.sql", menuCodeList, false);
    }

    /**
     * 缺少日志
     */
    private void lackLogCheck(AppConfigDto appConfigDto) throws Exception {
        // 缺少日志配置检查
        Iterator<String> subTransIterator = subTransCache.keySet().iterator();
        List<String> subTransExtList = new ArrayList<>();
        while (subTransIterator.hasNext()) {
            String transCode = subTransIterator.next().trim();
            if (!subTransExtCache.containsKey(transCode) && !skipLogCache.contains(transCode) && !transCode.endsWith("QryC") && !transCode.endsWith("ColC")) {
                subTransExtList.add(buildMenuInfo(subTransCache, transCode));
            }
        }
        subTransExtList.add(0, "-- 待处理【" + subTransExtList.size() + "】\n\n");
        subTransExtList.add(0, "-- ************************************* 缺少日志 *************************************");
        FileUtils.writeFile(resultPath + "60.缺少日志.sql", subTransExtList, false);
    }

    /**
     * 错误日志
     *
     * @param appConfigDto
     * @throws Exception
     */
    private void logErrorCheck(AppConfigDto appConfigDto) throws Exception {
        // 日志配置错误检查
        Iterator<String> subTransExtIterator = subTransExtCache.keySet().iterator();
        List<String> subTransExtErrorList = new ArrayList<>();
        while (subTransExtIterator.hasNext()) {
            String transCode = subTransExtIterator.next();
            if (skipErrorLogCache.contains(transCode)) {
                continue;
            }
            String subTransCode = transCode.split("-")[1].trim();
            String opDir = subTransExtCache.get(transCode).get(0).split("-")[0];
            if (!opDir.equals(ScriptUtils.getSubTransCodeOpDir(subTransCode, STR_BLANK))) {
                subTransExtErrorList.add(buildMenuTransInfo(subTransExtCache, transCode));
            }
        }
        subTransExtErrorList.add(0, "-- 待处理【" + subTransExtErrorList.size() + "】\n\n");
        subTransExtErrorList.add(0, "-- ************************************* 0-新增 1-修改 2-删除 3-其他 4-查询 5-下载 6-导入 *************************************");
        subTransExtErrorList.add(0, "-- ******************************************************* 错误日志 *******************************************************");
        FileUtils.writeFile(resultPath + "70.错误日志.sql", subTransExtErrorList, false);
    }

    /**
     * 所有菜单
     *
     * @param appConfigDto
     * @throws Exception
     */
    private void allMenuStat(AppConfigDto appConfigDto) throws Exception {
        Iterator<String> menuIterator = menuCache.keySet().iterator();
        List<String> menu = new ArrayList<>();
        Set<String> existMenu = new HashSet<>();
        while (menuIterator.hasNext()) {
            String menuCode = menuIterator.next();
            if (menuRouterCache.contains(menuCode)) {
                if (existMenu.contains(menuCode) || existMenu.contains(menuCode + "T") || existMenu.contains(menuCode.substring(0, menuCode.length() - 1))) {
                    continue;
                }
                existMenu.add(menuCode);
                menu.add(buildMenuInfo(menuCode));
            }
        }
        menu.add(0, "-- 菜单总数【" + existMenu.size() + "】\n\n");
        menu.add(0, "-- ************************************* 所有菜单 *************************************");
        FileUtils.writeFile(resultPath + "5.所有菜单.sql", menu, false);
    }

    /**
     * 新版菜单合法性
     *
     * @param appConfigDto
     * @throws Exception
     */
    private void newMenuLegalCheck(AppConfigDto appConfigDto) throws Exception {
        // 菜单名称存在空格
        Iterator<String> menuUedExistIterator = menuUedExistCache.keySet().iterator();
        List<String> menuInfo = new ArrayList<>();
        while (menuUedExistIterator.hasNext()) {
            String menuCode = menuUedExistIterator.next();
            String menuName = menuUedExistCache.get(menuCode);
            if (menuName.length() != menuName.trim().length()) {
                menuInfo.add(menuCode + "   " + menuName);
            }

        }
        int total = menuInfo.size();
        menuInfo.add(0, "-- 待处理【" + menuInfo.size() + "】\n\n");
        menuInfo.add(0, "-- ************************************* 菜单名称存在空格 *************************************");

        // 存在系统间合并不存在合并后菜单
        Iterator<String> iterator = menuUedReserveCache.keySet().iterator();
        List<String> reserve1List = new ArrayList<>();
        while (iterator.hasNext()) {
            String menuCode = iterator.next();
            String menuName = menuUedReserveCache.get(menuCode)[0];
            String menuReserve = menuUedReserveCache.get(menuCode)[1];
            if (StringUtils.isBlank(menuReserve.trim()) || !STR_0.equals(menuReserve)) {
                continue;
            }
            Iterator<String> iteratorTmp = menuUedReserveCache.keySet().iterator();
            boolean flag = false;
            while (iteratorTmp.hasNext()) {
                String menuCodeTmp = iteratorTmp.next();
                if (menuCode.equals(menuCodeTmp)) {
                    continue;
                }
                String menuReserveTmp = menuUedReserveCache.get(menuCodeTmp)[1];
                String[] reserve = menuReserveTmp.split(STR_COMMA);
                for (String item : reserve) {
                    if (menuCode.equals(item)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                String menu = menuCode + "   " + menuName;
                reserve1List.add(menu);
            }
        }
        total += reserve1List.size();
        menuInfo.add("\n\n-- ************************************* 存在系统间合并不存在合并后菜单 *************************************");
        menuInfo.add("-- 待处理【" + reserve1List.size() + "】\n\n");
        menuInfo.addAll(reserve1List);

        // 存在合并后菜单不存在系统间合并
        Iterator<String> iteratorReserve2 = menuUedReserveCache.keySet().iterator();
        List<String> reserve2List = new ArrayList<>();
        while (iteratorReserve2.hasNext()) {
            String menuCode = iteratorReserve2.next();
            String menuName = menuUedReserveCache.get(menuCode)[0];
            String menuReserve = menuUedReserveCache.get(menuCode)[1];
            if (StringUtils.isBlank(menuReserve.trim()) || STR_0.equals(menuReserve)) {
                continue;
            }
            String[] menuReserveList = menuReserve.split(STR_COMMA);
            for (String item : menuReserveList) {
                Iterator<String> iteratorTmp = menuUedReserveCache.keySet().iterator();
                boolean flag = false;
                while (iteratorTmp.hasNext()) {
                    String menuCodeTmp = iteratorTmp.next();
                    if (menuCode.equals(menuCodeTmp)) {
                        continue;
                    }
                    String menuReserveTmp = menuUedReserveCache.get(menuCodeTmp)[1];
                    if (item.equals(menuCodeTmp) && STR_0.equals(menuReserveTmp)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    String menu = item + "   " + menuUedExistCache.get(item);
                    ;
                    reserve2List.add(menu);
                }
            }
        }

        total += reserve2List.size();
        menuInfo.add("\n\n-- ************************************* 存在合并后菜单不存在系统间合并 *************************************");
        menuInfo.add("-- 待处理【" + reserve2List.size() + "】\n\n");
        menuInfo.addAll(reserve2List);


        menuInfo.add(0, "-- 待处理【" + total + "】\n\n");
        menuInfo.add(0, "-- ************************************* 新版菜单合法性 *************************************");
        FileUtils.writeFile(resultPath + "80.新版菜单合法性.sql", menuInfo, false);
    }

    private void compareMenu(Set<String> menuMap, String fileName) throws Exception {
        Set<String> menu = new LinkedHashSet<>();
        Iterator iterator = menuMap.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (!menu.contains(key) && !menu.contains(key + "T") && !menu.contains(key.substring(0, key.length() - 1))) {
                menu.add(key);
            }
        }
        Set<String> extend = new LinkedHashSet<>();
        Iterator checkIterator = menu.iterator();
        while (checkIterator.hasNext()) {
            String key = (String) checkIterator.next();
            if (!menuUedExistCache.containsKey(key) && !menuUedExistCache.containsKey(key + "T")) {
                extend.add(key);
            }
        }
        Iterator<String> extendIterator = extend.iterator();
        List<String> menuCodeList = new ArrayList<>();
        while (extendIterator.hasNext()) {
            String menuCode = extendIterator.next();
            if (!skipNewMenuCache.contains(menuCode)) {
                menuCodeList.add(buildMenuInfo(menuCode));
                needAddUedMenuNum++;
            }
        }
        totalMenu.addAll(menuMap);
        writeFile(menuCodeList, fileName);
    }

    private void writeFile(List<String> menuCodeList, String fileName) throws Exception {
        menuCodeList.add(0, "-- 待处理【" + menuCodeList.size() + "】\n");
        needAddUedMenu.add("\n\n-- ************************************* " + fileName.replace(".sql", " *************************************"));
        needAddUedMenu.addAll(menuCodeList);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        if (!"10.缺少新版全量.sql".equals(fileName)) {
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
            if (menuCode != null) {
                if (getMenuValueLen(item) < 18) {
                    continue;
                }
                String menuName = getMenuName(item);
                String menuReserve = getMenuReserve(item);
                menuUedExistCache.put(menuCode, menuName);
                menuUedReserveCache.put(menuCode, new String[]{menuName, menuReserve});
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
                menuBaseExistCache.add(menuCode);
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

    private static String getMenuReserve(String item) {
        String menuReserve = STR_BLANK;
        item = handleMenu(item);
        if (item != null) {
            String[] menuCodeInfo = item.split(",");
            if (menuCodeInfo.length >= 17) {
                for (int i=17; i<menuCodeInfo.length; i++) {
                    String ele =  menuCodeInfo[i];
                    if (ele.contains("'")) {
                        if (ele.trim().startsWith("'")) {
                            ele = ele.split("'")[1];
                        } else {
                            ele = ele.split("'")[0];
                        }
                    }
                    menuReserve += ele + STR_COMMA;
                }
            }
        }
        if (menuReserve.contains("'")) {
            menuReserve = menuReserve.split("'")[0];
        }
        if (menuReserve.contains(STR_BRACKETS_RIGHT)) {
            menuReserve = menuReserve.substring(0, menuReserve.lastIndexOf(STR_BRACKETS_RIGHT));
        }
        if (menuReserve.endsWith(STR_COMMA)) {
            menuReserve = menuReserve.substring(0, menuReserve.lastIndexOf(STR_COMMA));
        }
        return menuReserve;
    }

    private static int getMenuValueLen(String item) {
        if (!item.contains("(") || !item.contains(")")) {
            return -1;
        }
        if (item.toLowerCase().contains("insert") && item.toLowerCase().contains("values")) {
            return -1;
        }
        return item.substring(item.indexOf("(") + 1, item.lastIndexOf(")")).split(",").length;
    }

    private static String getMenuDetail(int len, String item) throws Exception {
        String res = STR_BLANK;
        item = handleMenu(item);
        if (item != null) {
            String[] value = ScriptUpdateSql.handleValue(len, item.substring(item.indexOf("(") + 1, item.lastIndexOf(")")).split(","));
            for (String ele : value) {
                if (!StringUtils.isBlank(res)) {
                    res += "-";
                }
                res += ele;
            }
        }
        return res.trim().substring(1);
    }

    private static Set<String> initMenuRouter(List<String> menu) {
        Set<String> menuSet = new LinkedHashSet<>();
        Iterator<String> iterator = menu.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains("=>")) {
                String menuCode = item.split("=>")[0].split(":")[0].trim();
                if (!menuCode.startsWith("//")) {
                    menuSet.add(menuCode);
                }
            }
        }
        return menuSet;
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
            List<String> content = FileUtils.readNormalFile(file.getPath(), false);
            // 缓存菜单信息
            boolean endFlag = true;
            for (String item : content) {
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_std") || itemLower.contains("tsys_menu_ext") || itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu")) {
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
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_std") || itemLower.contains("tsys_menu_ext") || itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu")) {
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
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu ") || itemLower.contains("tsys_menu_ext") || itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_std")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = getMenuCode(item);
                    String menuName = getMenuName(item);
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
                        // 补充菜单名称信息
                        if (menuCache.containsKey(menuCode)) {
                            if (addFilePath(filePath)) {
                                List<String> location = menuCache.get(menuCode);
                                boolean flag = true;
                                for (String ele : location) {
                                    if (ele.equals(filePath)) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    menuCache.get(menuCode).add(filePath);
                                }
                            }
                        } else {
                            List<String> menu = new ArrayList<>();
                            menu.add(menuName);
                            if (addFilePath(filePath)) {
                                menu.add(filePath);
                            }
                            menuCache.put(menuCode, menu);
                        }
                    }
                    endFlag = true;
                }
            }

            // 缓存交易码信息
            endFlag = true;
            for (String item : content) {
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_trans")) {
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
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tbfundgranttablestmp")
                        || itemLower.contains("tsys_trans ") || itemLower.contains("tsys_subtrans_ext")) {
                    continue;
                }
                if (itemLower.contains("tsys_subtrans")) {
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
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tbfundgranttablestmp") || itemLower.contains("tsys_subtrans ") || itemLower.contains("tsys_trans ")) {
                    continue;
                }
                if (itemLower.contains("tsys_subtrans_ext")) {
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