package com.hoomoomoo.im.extend;

import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.controller.SystemToolController;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.*;

public class ScriptCompareSql {

    private String resultPath = "";
    private String basePathExt = "";
    private String basePathRouter = "";
    private String baseMenu = "";
    private String newUedPage = "";
    private String menuCondition = "";
    private String productConfig = "";

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
    private Map<String, String> newMenuExistCache = new LinkedHashMap<>();
    // 新版已配置trans
    private Set<String> newMenuTransExistCache = new LinkedHashSet<>();
    // 新版已配置菜单  menu_code/menu_name reserve parent_code
    private Map<String, String[]> newMenuElementCache = new LinkedHashMap<>();
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
    // 忽略新版菜单合法性
    Map<String, Set<String>> skipLegalNewMenu = new HashMap<>();
    // 路由缓存信息
    Set<String> menuRouterCache = new LinkedHashSet<>();

    public ScriptCompareSql() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        try {
            String basePath = appConfigDto.getSystemToolCheckMenuFundBasePath();
            if (StringUtils.isBlank(basePath)) {
                throw new Exception("请配置参数【system.tool.check.menu.fund.base.path】\n");
            }
            String extPath = appConfigDto.getSystemToolCheckMenuFundExtPath();
            if (StringUtils.isBlank(extPath)) {
                throw new Exception("请配置参数【system.tool.check.menu.fund.ext.path】\n");
            }
            String baseRouterPath = appConfigDto.getSystemToolCheckMenuFundBaseRouterPath();
            if (StringUtils.isBlank(baseRouterPath)) {
                throw new Exception("请配置参数【system.tool.check.menu.fund.base.router.path】\n");
            }
            String resPath = appConfigDto.getSystemToolCheckMenuResultPath();
            if (StringUtils.isBlank(resPath)) {
                throw new Exception("请配置参数【system.tool.check.menu.result.path】\n");
            }
            basePathExt = extPath + ScriptSqlUtils.basePathExt;
            basePathRouter = baseRouterPath + ScriptSqlUtils.basePathRouter;
            baseMenu = basePath + ScriptSqlUtils.baseMenu;
            newUedPage = basePath + ScriptSqlUtils.newUedPage;
            menuCondition = basePath + ScriptSqlUtils.menuCondition;
            productConfig = basePath + ScriptSqlUtils.productConfig;
            resultPath = resPath + "\\";

            String confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.LACK_NEW_MENU_ALL.getPathConf());
            List<String> content = FileUtils.readNormalFile(confPath);
            initSkipCache(content, skipNewMenuCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.LACK_OLD_NEW_ALL.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipCache(content, skipOldMenuCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.LACK_ROUTER.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipCache(content, skipRouterCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.DIFF_NEW_ALL_EXT.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipCache(content, skipNewDiffMenuCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.DIFF_OLD_ALL_EXT.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipCache(content, skipOldDiffMenuCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.LACK_LOG.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipTransCache(content, skipLogCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.ERROR_LOG.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipTransCache(content, skipErrorLogCache);

            confPath = FileUtils.getFilePath(SQL_CHECK_TYPE.LEGAL_NEW_MENU.getPathConf());
            content = FileUtils.readNormalFile(confPath);
            initSkipMapCache(content, skipLegalNewMenu);

            initBaseAndExtMenuCache();
            initConfigUedMenuCache();
            initConfigBaseMenuCache();
        } catch (IOException e) {
            LoggerUtils.error(e);
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
            for (int i = 0; i < element.length; i++) {
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

    private void initSkipMapCache(List<String> content, Map<String, Set<String>> skipCache) {
        for (String item : content) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            String[] element = CommonUtils.trimStrToSpace(item).split(STR_SPACE);
            if (element.length >= 2) {
                String checkType = element[0];
                for (int i = 1; i < element.length; i++) {
                    String menuName = element[i];
                    if (skipCache.containsKey(checkType)) {
                        skipCache.get(checkType).add(menuName);
                    } else {
                        Set<String> ele = new HashSet<>();
                        ele.add(menuName);
                        skipCache.put(checkType, ele);
                    }
                }
            }
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
        // 开通脚本合法性
        extLegalCheck(appConfigDto);
        // 检查结果汇总
        fileSummary(appConfigDto);
    }

    private void fileSummary(AppConfigDto appConfigDto) throws Exception {
        List<String> fileContent = new ArrayList<>();
        SQL_CHECK_TYPE[] fileList = SQL_CHECK_TYPE.values();
        int summary = -1;
        int total = 0;
        for (SQL_CHECK_TYPE item : fileList) {
            String name = item.getName();
            if (CHECK_RESULT_SUMMARY.getName().equals(name)) {
                continue;
            }
            summary = -1;
            List<String> content = FileUtils.readNormalFile(resultPath + "\\" + item.getFileName());
            for (String line : content) {
                if (line.contains("待处理") && line.contains("【") && line.contains("】")) {
                    summary = Integer.valueOf(line.split("【")[1].split("】")[0]);
                    break;
                }
            }
            if (summary > 0) {
                total += summary;
                content.add(STR_NEXT_LINE_3);
                fileContent.addAll(content);
            }
        }
        fileContent.add(0, String.format(MSG_WAIT_HANDLE_NUM, total));
        fileContent.add(0, String.format(MSG_WAIT_HANDLE_EVENT, CHECK_RESULT_SUMMARY.getName()));
        FileUtils.writeFile(resultPath + CHECK_RESULT_SUMMARY.getFileName(), fileContent);
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
            LoggerUtils.info(SystemToolController.getCheckMenuMsg("忽略路由文件 " + systemToolCheckMenuSkipRouter));
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
            List<String> menuCodeList = FileUtils.readNormalFile(item.getPath());
            Set<String> menuMap = initMenuRouter(menuCodeList);
            if ("analysis".equals(fileName)) {
                List<String> extendMenuCodeList = FileUtils.readNormalFile(basePathRouter + "analysisByTrans.js");
                menuMap.addAll(initMenuRouter(extendMenuCodeList));
            }
            allRouterMenu.addAll(menuMap);
            compareMenu(menuMap, LACK_NEW_MENU_ALL.getName() + STR_SPACE_2 + fileName + ".sql");
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
        Iterator<String> uedExistIterator = newMenuExistCache.keySet().iterator();
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
            String menuInfo = buildMenuInfo(menuCode);
            if (!skipMenu(menuCode, menuInfo)) {
                menuCodeList.add(buildMenuInfo(menuCode));
                needAddUedMenuNum++;
            }
        }
        if (CollectionUtils.isNotEmpty(menuCodeList)) {
            writeFile(menuCodeList, LACK_NEW_MENU_ALL.getName() + STR_SPACE_2 + "extend.sql");
        }
        needAddUedMenu.add(0, String.format(MSG_WAIT_HANDLE_NUM_0, needAddUedMenuNum));
        needAddUedMenu.add(0, String.format(MSG_WAIT_HANDLE_EVENT, LACK_NEW_MENU_ALL.getName()));
        FileUtils.writeFile(resultPath + LACK_NEW_MENU_ALL.getFileName(), needAddUedMenu);
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
        lackMenuList.add(0, String.format(MSG_WAIT_HANDLE_NUM, lackMenu.size()));
        lackMenuList.add(0, String.format(MSG_WAIT_HANDLE_EVENT, LACK_OLD_NEW_ALL.getName()));
        FileUtils.writeFile(resultPath + LACK_OLD_NEW_ALL.getFileName(), lackMenuList);
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
                            diffValue.add(buildDiffValueInfo(MSG_PATH_TYPE_EXT, menuValueExt));
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
                            diffValue.add(buildDiffValueInfo(MSG_PATH_TYPE_BASE, menuValue));
                            diffValue.add(buildDiffValueInfo(MSG_PATH_TYPE_EXT, menuValueExt));
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
        newMenDiffInfo.add(0, String.format(MSG_WAIT_HANDLE_NUM, newMenDiff.size()));
        newMenDiffInfo.add(0, String.format(MSG_WAIT_HANDLE_EVENT, DIFF_NEW_ALL_EXT.getName()));
        FileUtils.writeFile(resultPath + DIFF_NEW_ALL_EXT.getFileName(), newMenDiffInfo);
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
                            diffValue.add(buildDiffValueInfo(MSG_PATH_TYPE_EXT, menuValueExt));
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
                            diffValue.add(buildDiffValueInfo(MSG_PATH_TYPE_BASE, menuValue));
                            diffValue.add(buildDiffValueInfo(MSG_PATH_TYPE_EXT, menuValueExt));
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
        oldMenDiffInfo.add(0, String.format(MSG_WAIT_HANDLE_NUM, oldMenDiff.size()));
        oldMenDiffInfo.add(0, String.format(MSG_WAIT_HANDLE_EVENT, DIFF_OLD_ALL_EXT.getName()));
        FileUtils.writeFile(resultPath + DIFF_OLD_ALL_EXT.getFileName(), oldMenDiffInfo);
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
            String menu = buildMenuInfo(menuCode);
            // 文件类型忽略检查
            if (skipItem(skipRouterCache, menu)) {
                iteratorLack.remove();
                continue;
            }
            menuCodeList.add(menu);
        }
        menuCodeList.add(0, String.format(MSG_WAIT_HANDLE_NUM, lackRouter.size()));
        menuCodeList.add(0, String.format(MSG_WAIT_HANDLE_EVENT, LACK_ROUTER.getName()));
        FileUtils.writeFile(resultPath + LACK_ROUTER.getFileName(), menuCodeList);
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
            if (!subTransExtCache.containsKey(transCode) && !skipLogCache.contains(transCode) && !transCode.endsWith("QryC") && !transCode.endsWith("QueryC") && !transCode.endsWith("ColC")) {
                String menu = buildMenuInfo(subTransCache, transCode);
                if (menu.contains("查询列") || menu.contains("确认列")) {
                    continue;
                }
                subTransExtList.add(menu);
            }
        }
        subTransExtList.add(0, String.format(MSG_WAIT_HANDLE_NUM, subTransExtList.size()));
        subTransExtList.add(0, String.format(MSG_WAIT_HANDLE_EVENT, LACK_LOG.getName()));
        FileUtils.writeFile(resultPath + LACK_LOG.getFileName(), subTransExtList);
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
            String opDir = subTransExtCache.get(transCode).get(0).split(STR_SPACE)[0];
            if (!opDir.equals(ScriptSqlUtils.getSubTransCodeOpDir(subTransCode, STR_BLANK))) {
                subTransExtErrorList.add(buildMenuTransInfo(subTransExtCache, transCode));
            }
        }
        subTransExtErrorList.add(0, String.format(MSG_WAIT_HANDLE_NUM, subTransExtErrorList.size()));
        subTransExtErrorList.add(0, "-- *************************************  0:新增 1:修改 2:删除 3:其他 4:查询 5:下载 6:导入 7:审批 8:接口 9:复制  *************************************");
        subTransExtErrorList.add(0, String.format(MSG_WAIT_HANDLE_EVENT, ERROR_LOG.getName()));
        FileUtils.writeFile(resultPath + ERROR_LOG.getFileName(), subTransExtErrorList);
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
        menu.add(0, String.format(MSG_WAIT_HANDLE_NUM, existMenu.size()).replace("待处理", "菜单总数"));
        menu.add(0, String.format(MSG_WAIT_HANDLE_EVENT, ALL_MENU.getName()));
        FileUtils.writeFile(resultPath + ALL_MENU.getFileName(), menu);
    }

    /**
     * 开通脚本合法性
     */
    private void extLegalCheck(AppConfigDto appConfigDto) throws Exception {
        Map<String, Set<String>> resMap = new LinkedHashMap<>();
        Map<String, Set<String>> productExtMap = new LinkedHashMap<>();

        resMap.put("tsys_trans", new LinkedHashSet<>());
        resMap.put("tsys_subtrans", new LinkedHashSet<>());
        resMap.put("tsys_subtrans_ext", new LinkedHashSet<>());
        resMap.put("tbworkflowsubtrans", new LinkedHashSet<>());
        resMap.put("tbworkflowsubtransext", new LinkedHashSet<>());
        resMap.put("add_report_field", new LinkedHashSet<>());
        resMap.put("add_report", new LinkedHashSet<>());
        resMap.put("tbmenucondition", new LinkedHashSet<>());
        resMap.put("tbdataelement", new LinkedHashSet<>());
        resMap.put("tbtemplaterelgroup", new LinkedHashSet<>());
        resMap.put("tbelementgroup", new LinkedHashSet<>());
        File fileExt = new File(basePathExt);
        Set<String> skip = ScriptSqlUtils.initExtLegalSkip();
        for (File file : fileExt.listFiles()) {
            checkMenuByFile(file, resMap, skip, true);
            checkMenuByFile(file, productExtMap, skip, false);
        }
        Map<String, Set<String>> productTips = new LinkedHashMap<>();
        String content = FileUtils.readNormalFileToString(new File(productConfig).getPath());
        if (StringUtils.isNotEmpty(content)) {
            String[] data = content.split(STR_SEMICOLON);
            for (String item : data) {
                item = item.toLowerCase();
                if (item.contains("tbpageelement")) {
                    String id = ScriptSqlUtils.getElement(item, 0);
                    String code = ScriptSqlUtils.getElement(item, 4);
                    String name = ScriptSqlUtils.getElement(item, 5);
                    String field = String.format("id: %s  elementCode: %s  elementName: %s", id, code, name);
                    if (productExtMap.containsKey(id) && !item.contains(ANNOTATION_NORMAL)) {
                        productTips.put(field, productExtMap.get(id));
                    }
                }
            }
        }


        int total = 0;
        List<String> res = new ArrayList<>();
        Iterator<String> iterator = resMap.keySet().iterator();
        while (iterator.hasNext()) {
            String checkType = iterator.next();
            Set<String> checkRes = resMap.get(checkType);
            if (CollectionUtils.isEmpty(checkRes)) {
                continue;
            }
            total += checkRes.size();
            res.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在错误表配置信息 " + checkType));
            res.add(String.format(MSG_WAIT_HANDLE_NUM, checkRes.size()));
            res.addAll(checkRes);
        }

        if (MapUtils.isNotEmpty(productTips)) {
            total += productTips.size();
            res.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "基金信息tbpageelement存在开通脚本且全量脚本未注释 "));
            res.add(String.format(MSG_WAIT_HANDLE_NUM, productTips.size()));
            for (Map.Entry<String, Set<String>> entry : productTips.entrySet()) {
                String field = entry.getKey();
                Set<String> pathSet = entry.getValue();
                res.add(field);
                for (String path : pathSet) {
                    res.add(STR_SPACE_4 + path);
                }
                res.add(STR_SPACE);
            }
        }
        res.add(0, String.format(MSG_WAIT_HANDLE_NUM_0, total));
        res.add(0, String.format(MSG_WAIT_HANDLE_EVENT, LEGAL_EXT_MENU.getName()));
        FileUtils.writeFile(resultPath + LEGAL_EXT_MENU.getFileName(), res);
    }

    private void checkMenuByFile(File file, Map<String, Set<String>> res, Set<String> skip, boolean checkTable) throws IOException {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                checkMenuByFile(item, res, skip, checkTable);
            }
        } else {
            String fileName = file.getName();
            String filePath = file.getPath();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            if (skip.contains(fileName)) {
                return;
            }
            if (checkTable) {
                List<String> content = FileUtils.readNormalFile(filePath);
                for (String ele : content) {
                    ele = CommonUtils.trimStrToSpace(ele).toLowerCase();
                    if (ele.contains("call") && ele.contains("add_report_field")) {
                        res.get("add_report_field").add(filePath);
                    } else if (ele.contains("call") && ele.contains("add_report")) {
                        res.get("add_report").add(filePath);
                    } else if (!ele.contains("insert into")) {
                        continue;
                    } else if (ele.contains("tsys_trans ") || ele.contains("tsys_trans(")) {
                        res.get("tsys_trans").add(filePath);
                    } else if (ele.contains("tsys_subtrans ") || ele.contains("tsys_subtrans(")) {
                        res.get("tsys_subtrans").add(filePath);
                    } else if (ele.contains("tsys_subtrans_ext ") || ele.contains("tsys_subtrans_ext(")) {
                        res.get("tsys_subtrans_ext").add(filePath);
                    } else if (ele.contains("tbworkflowsubtrans ") || ele.contains("tbworkflowsubtrans(")) {
                        res.get("tbworkflowsubtrans").add(filePath);
                    } else if (ele.contains("tbworkflowsubtransext ") || ele.contains("tbworkflowsubtransext(")) {
                        res.get("tbworkflowsubtrans").add(filePath);
                    } else if (ele.contains("tbmenucondition")) {
                        res.get("tbmenucondition").add(filePath);
                    } else if (ele.contains("tbdataelement")) {
                        res.get("tbdataelement").add(filePath);
                    } else if (ele.contains("tbtemplaterelgroup")) {
                        res.get("tbtemplaterelgroup").add(filePath);
                    } else if (ele.contains("tbelementgroup")) {
                        res.get("tbelementgroup").add(filePath);
                    }

                }
            } else {
                String content = FileUtils.readNormalFileToString(filePath);
                String[] data = content.split(STR_SEMICOLON);
                for (String item : data) {
                    item = item.toLowerCase();
                    if (item.contains("tbpageelement")) {
                        String id = ScriptSqlUtils.getElement(item, 0);
                        if (StringUtils.isBlank(id)) {
                            continue;
                        }
                        if (res.containsKey(id)) {
                            res.get(id).add(filePath);
                        } else {
                            Set<String> filedPath = new LinkedHashSet<>();
                            filedPath.add(filePath);
                            res.put(id, filedPath);
                        }
                    }
                }
            }

        }

    }

    /**
     * 新版菜单合法性
     *
     * @param appConfigDto
     * @throws Exception
     */
    private void newMenuLegalCheck(AppConfigDto appConfigDto) throws Exception {
        // 菜单名称存在空格
        Iterator<String> menuUedExistIterator = newMenuExistCache.keySet().iterator();
        List<String> menuInfo = new ArrayList<>();
        List<String> sameMenuName = new ArrayList<>();
        Set<String> addSameMenu = new HashSet<>();
        Set<String> menuNameNotStartByFund = new HashSet<>();
        Map<String, String> menuNameCache = new HashMap<>();
        Set<String> skipSameMenu = skipLegalNewMenu.get("存在相同菜单名称");
        Set<String> skipNotStartByFund = skipLegalNewMenu.get("菜单代码不是以fund开始");
        while (menuUedExistIterator.hasNext()) {
            String menuCode = menuUedExistIterator.next();
            String menuName = newMenuExistCache.get(menuCode);
            String menu = menuCode + "   " + menuName;
            if (menuName.length() != menuName.trim().length()) {
                menuInfo.add(menu);
            }
            if (!menuCode.startsWith("fund") && !ScriptRepairSql.includePubMenu.contains(menuCode)) {
                if (CollectionUtils.isEmpty(skipNotStartByFund) || !skipNotStartByFund.contains(menuCode)) {
                    menuNameNotStartByFund.add(menu);
                }
            }
            if (newMenuTransExistCache.contains(menuCode)) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(skipSameMenu) && skipSameMenu.contains(menuName)) {
                continue;
            }
            if (menuNameCache.containsKey(menuName)) {
                if (!addSameMenu.contains(menuName)) {
                    sameMenuName.add(menuNameCache.get(menuName));
                } else {
                    addSameMenu.add(menuName);
                }
                sameMenuName.add(menu);
            } else {
                menuNameCache.put(menuName, menu);
            }
        }
        int total = menuInfo.size();
        if (CollectionUtils.isNotEmpty(menuInfo)) {
            menuInfo.add(0, String.format(MSG_WAIT_HANDLE_NUM, menuInfo.size()));
            menuInfo.add(0, String.format(MSG_WAIT_HANDLE_EVENT, "菜单名称存在空格"));
        }

        // 存在相同菜单名称
        total += sameMenuName.size();
        if (CollectionUtils.isNotEmpty(sameMenuName)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在相同菜单名称"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, sameMenuName.size()));
            menuInfo.addAll(sameMenuName);
        }

        total += menuNameNotStartByFund.size();
        if (CollectionUtils.isNotEmpty(menuNameNotStartByFund)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "菜单代码不是以fund开始"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, menuNameNotStartByFund.size()));
            menuInfo.addAll(menuNameNotStartByFund);
        }

        // 存在子菜单 不存在虚拟父菜单
        Iterator<String> iterator = newMenuElementCache.keySet().iterator();
        List<String> reserve1List = new ArrayList<>();
        while (iterator.hasNext()) {
            String menuCode = iterator.next();
            String menuName = newMenuElementCache.get(menuCode)[0];
            String menuReserve = newMenuElementCache.get(menuCode)[1];
            if (!STR_0.equals(menuReserve)) {
                continue;
            }
            Iterator<String> iteratorTmp = newMenuElementCache.keySet().iterator();
            boolean flag = false;
            while (iteratorTmp.hasNext()) {
                String menuCodeTmp = iteratorTmp.next();
                if (menuCode.equals(menuCodeTmp)) {
                    continue;
                }
                String menuReserveTmp = newMenuElementCache.get(menuCodeTmp)[1];
                String[] reserve = menuReserveTmp.split(STR_COMMA);
                for (String item : reserve) {
                    if (menuCode.equals(item)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                if (StringUtils.isBlank(menuName)) {
                    menuName = STR_BLANK;
                }
                String menu = menuCode + "   " + menuName;
                reserve1List.add(menu);
            }
        }
        total += reserve1List.size();
        if (CollectionUtils.isNotEmpty(reserve1List)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在子菜单 不存在虚拟父菜单"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, reserve1List.size()));
            menuInfo.addAll(reserve1List);
        }

        // 存在虚拟父菜单 未配置子菜单
        iterator = newMenuElementCache.keySet().iterator();
        List<String> reserve2List = new ArrayList<>();
        while (iterator.hasNext()) {
            String menuCode = iterator.next();
            String menuName = newMenuElementCache.get(menuCode)[0];
            String menuReserve = newMenuElementCache.get(menuCode)[1];
            if (StringUtils.isBlank(menuReserve) && newMenuTransExistCache.contains(menuCode)) {
                if (StringUtils.isBlank(menuName)) {
                    menuName = STR_BLANK;
                }
                String menu = menuCode + "   " + menuName;
                reserve2List.add(menu);
            }
        }
        total += reserve2List.size();
        if (CollectionUtils.isNotEmpty(reserve2List)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在虚拟父菜单 未配置子菜单"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, reserve2List.size()));
            menuInfo.addAll(reserve2List);
        }

        // 注释内容不存在开始结束标识
        List<String> config = FileUtils.readNormalFile(newUedPage);
        List<String> remarkError = new ArrayList<>();
        for (int i = 6; i < config.size(); i++) {
            String item = config.get(i).toLowerCase().trim();
            if (!item.contains(ANNOTATION_NORMAL)) {
                continue;
            }
            if (item.contains("delete") || item.contains("insert") || item.contains("values") || item.contains("废弃页面已抽取到平台")) {
                continue;
            }
            if (!item.contains("开始") && !item.contains("结束")) {
                if (!remarkError.contains(item)) {
                    remarkError.add(item);
                }
            }
        }
        total += remarkError.size();
        if (CollectionUtils.isNotEmpty(remarkError)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "注释内容不存在开始结束标识"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, remarkError.size()));
            menuInfo.addAll(remarkError);
        }

        List<String> errorNextLine = new ArrayList<>();
        List<String> errorTable = new ArrayList<>();
        List<String> errorAnnotation = new ArrayList<>();
        List<String> menu = FileUtils.readNormalFile(newUedPage);
        for (String item : menu) {
            String lower = item.toLowerCase().trim();
            if (StringUtils.isBlank(lower)) {
                continue;
            }
            if (!lower.startsWith("--") && !lower.startsWith("delete") && !lower.startsWith("insert") && !lower.startsWith("values") && !lower.startsWith("commit")) {
                errorNextLine.add(item);
            }
            String tableName = ScriptSqlUtils.getTableName(item);
            if (StringUtils.isNotBlank(tableName) && !"tsys_menu_std".equals(tableName) && !"tsys_trans".equals(tableName)) {
                errorTable.add(item);
            }
            if (lower.startsWith(ANNOTATION_NORMAL) && !lower.startsWith(ANNOTATION_NORMAL + STR_SPACE)) {
                errorAnnotation.add(ANNOTATION_NORMAL + item);
            }
        }
        total += errorNextLine.size();
        if (CollectionUtils.isNotEmpty(errorNextLine)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在错误换行"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, errorNextLine.size()));
            menuInfo.addAll(errorNextLine);
        }

        total += errorTable.size();
        if (CollectionUtils.isNotEmpty(errorTable)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在错误表配置信息"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, errorTable.size()));
            menuInfo.addAll(errorTable);
        }

        total += errorAnnotation.size();
        if (CollectionUtils.isNotEmpty(errorAnnotation)) {
            menuInfo.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, "存在错误注释信息 注释与内容之间必须包含空格"));
            menuInfo.add(String.format(MSG_WAIT_HANDLE_NUM, errorAnnotation.size()));
            menuInfo.addAll(errorAnnotation);
        }

        menuInfo.add(0, String.format(MSG_WAIT_HANDLE_NUM_0, total));
        menuInfo.add(0, String.format(MSG_WAIT_HANDLE_EVENT, LEGAL_NEW_MENU.getName()));
        FileUtils.writeFile(resultPath + LEGAL_NEW_MENU.getFileName(), menuInfo);
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
            if (!newMenuExistCache.containsKey(key) && !newMenuExistCache.containsKey(key + "T")) {
                extend.add(key);
            }
        }
        Iterator<String> extendIterator = extend.iterator();
        List<String> menuCodeList = new ArrayList<>();
        while (extendIterator.hasNext()) {
            String menuCode = extendIterator.next();
            if (!skipNewMenuCache.contains(menuCode)) {
                String menuInfo = buildMenuInfo(menuCode);
                if (!skipMenu(menuCode, menuInfo)) {
                    menuCodeList.add(menuInfo);
                    needAddUedMenuNum++;
                }
            }
        }
        totalMenu.addAll(menuMap);
        writeFile(menuCodeList, fileName);
    }

    private boolean skipMenu(String menuCode, String menuInfo) {
        return StringUtils.equals((menuCode + STR_NEXT_LINE), menuInfo);
    }

    private void writeFile(List<String> menuCodeList, String fileName) throws Exception {
        if (CollectionUtils.isEmpty(menuCodeList)) {
            return;
        }
        menuCodeList.add(0, String.format(MSG_WAIT_HANDLE_NUM_1, menuCodeList.size()));
        needAddUedMenu.add(STR_NEXT_LINE_2 + String.format(MSG_WAIT_HANDLE_EVENT, fileName.replace(".sql", STR_BLANK)));
        needAddUedMenu.addAll(menuCodeList);
        if (!LACK_NEW_MENU_ALL.getFileName().equals(fileName)) {
            return;
        }
        FileUtils.writeFile(resultPath + fileName, menuCodeList);
    }

    private String buildDiffValueInfo(String menuType, String value) {
        return STR_SPACE_2 + menuType + STR_SPACE_2 + value;
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
        List<String> config = FileUtils.readNormalFile(newUedPage);
        Iterator<String> iterator = config.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                break;
            }
            String menuCode = ScriptSqlUtils.getMenuCode(item);
            if (menuCode != null) {
                int menuLen = ScriptSqlUtils.getMenuValueLen(item);
                // tsys_trans
                if (menuLen == 8) {
                    newMenuTransExistCache.add(menuCode);
                    continue;
                }
                if (menuLen < 16) {
                    continue;
                }
                String menuName = ScriptSqlUtils.getMenuName(item);
                String menuReserve = ScriptSqlUtils.getMenuReserve(item);
                String menuParentCode = ScriptSqlUtils.getParentCode(item);
                newMenuExistCache.put(menuCode, menuName);
                newMenuElementCache.put(menuCode, new String[]{menuName, menuReserve, menuParentCode});
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
        List<String> config = FileUtils.readNormalFile(baseMenu);
        Iterator<String> iterator = config.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains(appConfigDto.getSystemToolCheckMenuEndFlag())) {
                break;
            }
            String menuCode = ScriptSqlUtils.getMenuCode(item);
            if (menuCode != null) {
                menuBaseExistCache.add(menuCode);
                if (ScriptSqlUtils.getMenuValueLen(item) != 16) {
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


    private static String getMenuDetail(int len, String item) throws Exception {
        String res = STR_BLANK;
        item = ScriptSqlUtils.handleSqlForValues(item);
        if (item != null) {
            String[] value = ScriptUpdateSql.handleValue(len, item.substring(item.indexOf("(") + 1, item.lastIndexOf(")")).split(","));
            for (String ele : value) {
                if (!StringUtils.isBlank(res)) {
                    res += "-";
                }
                res += ele;
            }
        }
        return res.trim();
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
    }

    private boolean skipItem(Set<String> skipConfig, String message) {
        message = message.trim();
        if (StringUtils.isBlank(message)) {
            return true;
        }
        for (String conf : skipConfig) {
            if (message.endsWith(conf)) {
                return true;
            }
        }
        return false;
    }

    private void initMenuByFile(File file) throws Exception {
        if (file.isDirectory()) {
            for (File item : file.listFiles()) {
                initMenuByFile(item);
            }
        } else {
            String fileName = file.getName();
            String filePath = file.getPath();
            if (!fileName.endsWith(FILE_TYPE_SQL)) {
                return;
            }
            if (filePath.contains("\\001initdata\\special\\") || filePath.contains("\\001initdata\\UED\\")) {
                return;
            }
            List<String> content = FileUtils.readNormalFile(file.getPath());
            // 缓存菜单信息
            boolean endFlag = true;
            for (String item : content) {
                String itemLower = item.toLowerCase();
                if (itemLower.contains("delete")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_mapping")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_std") || itemLower.contains("tsys_menu_ext") || itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = ScriptSqlUtils.getMenuCode(item);
                    String menuName = ScriptSqlUtils.getMenuName(item);
                    if (menuCode != null && menuName != null) {
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
                if (itemLower.contains("tsys_menu_mapping")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_std") || itemLower.contains("tsys_menu_ext") || itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = ScriptSqlUtils.getMenuCode(item);
                    if (menuCode != null) {
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
                if (itemLower.contains("tsys_menu_mapping")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu ") || itemLower.contains("tsys_menu_ext") || itemLower.contains("tbfundgranttablestmp")) {
                    continue;
                }
                if (itemLower.contains("tsys_menu_std")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    String menuCode = ScriptSqlUtils.getMenuCode(item);
                    String menuName = ScriptSqlUtils.getMenuName(item);
                    if (menuCode != null) {
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
                    String menuCode = ScriptSqlUtils.getMenuCode(item);
                    if (menuCode != null) {
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
                if (itemLower.contains("tsys_subtrans_mapping")) {
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
                    if (ScriptSqlUtils.getMenuValueLen(item) != 11) {
                        continue;
                    }
                    String transCode = ScriptSqlUtils.getSubTransCodeByWhole(item);
                    String transName = ScriptSqlUtils.getSubTransNameByWhole(item);
                    if (transCode != null && transName != null) {
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
                String itemLower = item.toLowerCase().trim();
                if (StringUtils.isBlank(itemLower)) {
                    continue;
                }
                if (itemLower.startsWith("delete")) {
                    continue;
                }
                if (itemLower.contains("tbfundgranttablestmp") || itemLower.contains("tsys_subtrans ") || itemLower.contains("tsys_trans ")) {
                    continue;
                }
                if (itemLower.contains("tsys_subtrans_ext")) {
                    endFlag = false;
                }
                if (!endFlag && item.trim().endsWith(";")) {
                    if (ScriptSqlUtils.getMenuValueLen(item) < 7) {
                        continue;
                    }
                    String transCode = ScriptSqlUtils.getSubTransCodeByWhole(item);
                    String transOpDir = ScriptSqlUtils.getSubTransOpDirByWhole(item);
                    if (transCode != null) {
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
        // 0:新增 1:修改 2:删除 3:其他 4:查询 5:下载 6:导入 7:审批 8:接口 9:复制
        switch (opDir) {
            case STR_0:
                opDir = "0 新增";
                break;
            case STR_1:
                opDir = "1 修改";
                break;
            case STR_2:
                opDir = "2 删除";
                break;
            case STR_3:
                opDir = "3 其他";
                break;
            case STR_4:
                opDir = "4 查询";
                break;
            case STR_5:
                opDir = "5 下载";
                break;
            case STR_6:
                opDir = "6 导入";
                break;
            default:
                break;
        }
        return opDir;
    }

    private static boolean addFilePath(String path) {
        return !path.endsWith("07console-fund-ta-vue-menu.sql");
    }
}