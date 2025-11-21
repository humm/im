package com.hoomoomoo.im.utils;

import com.alibaba.fastjson.JSON;
import com.hoomoomoo.im.cache.AppCache;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import com.hoomoomoo.im.timer.ImTimer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

/**
 * @author humm23693
 * @description 工具类
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/23
 */
public class CommonUtils {

    private final static String PATTERN1 = "yyyy-MM-dd HH:mm:ss";
    private final static String PATTERN2 = "yyyyMMddHHmmss";
    private final static String PATTERN3 = "yyyyMMdd";
    private final static String PATTERN4 = "yyyy-MM-dd";
    private final static String PATTERN5 = "HH:mm:ss";
    private final static String PATTERN6 = "yyyy/MM/dd";
    private final static String PATTERN7 = "yyyyMM";
    private final static String PATTERN8 = "yyyy-MM";
    private final static String PATTERN9 = "yyyy";
    private final static String PATTERN10 = "HHmmss";

    private static String MAC_ADDRESS_CACHE = "";

    private static Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    private static String SCAN_CURRENT_POSITION = "";
    private static long SCAN_CURRENT_POSITION_TIMES = 0;

    /**
     * 获取当前系统时间
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime1() {
        return new SimpleDateFormat(PATTERN1).format(new Date());
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime1(Date date) {
        return new SimpleDateFormat(PATTERN1).format(date);
    }

    /**
     * 获取当前系统时间
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime2() {
        return new SimpleDateFormat(PATTERN2).format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime3() {
        return new SimpleDateFormat(PATTERN3).format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime4() {
        return new SimpleDateFormat(PATTERN4).format(new Date());
    }

    /**
     * 获取明天日期
     *
     * @return
     */
    public static String getTomorrowDateTime() {
        return getCustomDateTime(1);
    }

    /**
     * 获取指定日期
     *
     * @param addDays
     * @return
     */
    public static String getCustomDateTime(int addDays) {
        return getCustomDateTime(LocalDate.now(), addDays);
    }

    /**
     * 获取指定日期
     *
     * @param addDays
     * @return
     */
    public static String getCustomDateTime(String currentDate, int addDays) {
        int year = Integer.parseInt(currentDate.substring(0, 4));
        int month = Integer.parseInt(currentDate.substring(4, 6));
        int day = Integer.parseInt(currentDate.substring(6));
        return getCustomDateTime(LocalDate.of(year, month, day), addDays);
    }

    /**
     * 获取指定日期
     *
     * @param addDays
     * @return
     */
    public static String getCustomDateTime(LocalDate currentDate, int addDays) {
        LocalDate nextDay = currentDate.plusDays(addDays);
        String month = String.valueOf(nextDay.getMonthValue());
        String day = String.valueOf(nextDay.getDayOfMonth());
        if (month.length() == 1) {
            month = STR_0 + month;
        }
        if (day.length() == 1) {
            day = STR_0 + day;
        }
        return nextDay.getYear() + month + day;
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDate(String date) {
        if (date.length() != 8) {
            return date;
        }
        return date.substring(0, 4) + STR_HYPHEN + date.substring(4, 6) + STR_HYPHEN + date.substring(6);
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateYmd(String date) {
        return date.replaceAll(STR_HYPHEN, STR_BLANK);
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime6() {
        return new SimpleDateFormat(PATTERN6).format(new Date());
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static Date getCurrentDateTime6(String time) throws ParseException {
        return new SimpleDateFormat(PATTERN1).parse(new SimpleDateFormat(PATTERN4).format(new Date()) + " " + time);
    }

    /**
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static Date getCurrentDateTime7(String date) throws ParseException {
        return new SimpleDateFormat(PATTERN1).parse(date);
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime8(Date date) {
        return new SimpleDateFormat(PATTERN5).format(date);
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime9(Date date) {
        return new SimpleDateFormat(PATTERN3).format(date);
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime10() {
        return new SimpleDateFormat(PATTERN7).format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime11() {
        return new SimpleDateFormat(PATTERN8).format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime12() {
        return new SimpleDateFormat(PATTERN9).format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime13() {
        return new SimpleDateFormat(PATTERN10).format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static String getCurrentDateTime14() {
        return new SimpleDateFormat(PATTERN5).format(new Date());
    }

    /**
     * 获取本周最后一天
     *
     * @return
     */
    public static String getLastDayByWeek() {
        LocalDate now = LocalDate.now();
        LocalDate lastDayOfWeek = now.with(DayOfWeek.SUNDAY);
        int year = lastDayOfWeek.getYear();
        int month = lastDayOfWeek.getMonthValue();
        int day = lastDayOfWeek.getDayOfMonth();
        return year + STR_HYPHEN + (month < 10 ? STR_0 + month : month) + STR_HYPHEN + (day < 10 ? STR_0 + day : day);
    }

    /**
     * 获取本周最后一天
     *
     * @return
     */
    public static String getLastDayByWeekYmd() {
        return getLastDayByWeek();
    }

    /**
     * 获取下周某天
     *
     * @param dayOfWeek
     * @return
     */
    public static String getNextWeekDayYmd(DayOfWeek dayOfWeek) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextWeekDay = currentDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        String nextWeekDayStr = nextWeekDay.toString().replaceAll(STR_HYPHEN, STR_BLANK);
        String weekLastDay = getLastDayByWeek().replaceAll(STR_HYPHEN, STR_BLANK);
        if (weekLastDay.compareTo(nextWeekDayStr) >= 0) {
            nextWeekDayStr = nextWeekDay.with(TemporalAdjusters.next(dayOfWeek)).toString().replaceAll(STR_HYPHEN, STR_BLANK);
        }
        return nextWeekDayStr;
    }

    /**
     * 获取本周某天
     *
     * @param dayOfWeek
     * @return
     */
    public static String getWeekDayYmd(DayOfWeek dayOfWeek) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextDay = currentDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        String nextDayStr = nextDay.toString().replaceAll(STR_HYPHEN, STR_BLANK);
        String weekLastDay = getLastDayByWeek().replaceAll(STR_HYPHEN, STR_BLANK);
        if (weekLastDay.compareTo(nextDayStr) < 0) {
            nextDayStr = currentDate.with(TemporalAdjusters.previous(dayOfWeek)).toString().replaceAll(STR_HYPHEN, STR_BLANK);
        }
        return nextDayStr;
    }

    public static String checkLicenseDate(AppConfigDto appConfigDto) {
        String tips = STR_BLANK;
        LicenseDto licenseDto = appConfigDto.getLicense();
        String effectiveDate = licenseDto.getEffectiveDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN3);
        try {
            Date effective = simpleDateFormat.parse(effectiveDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(effective);
            Long endDate = calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Long curretDate = calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);
            if (endDate - curretDate <= 5) {
                tips = String.format(MSG_LICENSE_EXPIRE_TIPS, endDate - curretDate);
            }
        } catch (ParseException e) {
            LoggerUtils.error(e);
        }
        return tips;
    }

    public static boolean checkLicense(String functionCode) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (StringUtils.isBlank(functionCode)) {
            if (Integer.valueOf(getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
                LoggerUtils.info(String.format(MSG_LICENSE_EXPIRE, licenseDto.getEffectiveDate()));
                return false;
            } else {
                return true;
            }
        }
        if (checkAuth(STR_1, functionCode)) {
            return true;
        }
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (isSuperUser()) {
            String appCode = ConfigCache.getAppCodeCache();
            functionDtoList = functionConfigToFunctionDto(appCode, getAppFunctionConfig(appCode));
        }
        if (isSyncMode()) {
            functionDtoList = functionDtoList.stream().filter(item -> item.getFunctionCode().equals(CONFIG_SET.getCode())).collect(Collectors.toList());
        }
        for (FunctionDto functionDto : functionDtoList) {
            if (functionCode.equals(functionDto.getFunctionCode())) {
                return true;
            }
        }
        return false;
    }

    public static List<FunctionDto> getAuthFunction() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (Integer.valueOf(getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
            return new ArrayList<>();
        }
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (isSuperUser()) {
            String appCode = ConfigCache.getAppCodeCache();
            functionDtoList = functionConfigToFunctionDto(appCode, getAppFunctionConfig(appCode));
        }
        return functionDtoList;
    }

    public static List<FunctionDto> getAuthFunctionByMavenInstall(String appCode) throws Exception {
        String licensePath = dealFilePath(FileUtils.getFilePath(PATH_LICENSE), appCode);
        String licenseContent = FileUtils.readNormalFileToString(licensePath);
        LicenseDto licenseDto = JSON.parseObject(SecurityUtils.getDecryptString(licenseContent), LicenseDto.class);
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (isSuperUser()) {
            functionDtoList = functionConfigToFunctionDto(appCode, getAppFunctionConfig(appCode));
        }
        return functionDtoList;
    }

    public static void showAuthFunction(MenuBar menuBar, TabPane functionTab) throws Exception {
        String appCode = ConfigCache.getAppCodeCache();
        List<FunctionDto> functionDtoList = getAuthFunction();
        if (isSuperUser()) {
            functionDtoList = functionConfigToFunctionDto(appCode, getAppFunctionConfig(appCode));
        }
        if (isSyncMode()) {
            functionDtoList = functionDtoList.stream().filter(item -> item.getFunctionCode().equals(CONFIG_SET.getCode())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(functionDtoList)) {
            return;
        }
        initMenuBar(menuBar, functionDtoList);
        ObservableList<Menu> menusList = menuBar.getMenus();
        for (Menu menu : menusList) {
            ObservableList<MenuItem> list = menu.getItems();
            for (FunctionDto functionDto : functionDtoList) {
                MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
                if (menu.getId().equals(functionConfig.getParentMenuId())) {
                    if (STR_TRUE.equals(functionConfig.getHide())) {
                        continue;
                    }
                    MenuItem item = new MenuItem();
                    item.setId(functionConfig.getMenuId());
                    item.setText(functionConfig.getName());
                    initMenuName(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            openMenu(event, functionTab);
                        }
                    });
                    list.add(item);
                }
            }
        }
    }

    public static void initMenuBar(MenuBar menuBar, List<FunctionDto> functionDtoList) throws Exception {
        if (CollectionUtils.isNotEmpty(functionDtoList)) {
            Map<String, MenuFunctionConfig.MenuConfig> menuConfigList = new LinkedHashMap<>(16);
            Map<String, Integer> authMenu = new LinkedHashMap<>(16);
            for (FunctionDto functionDto : functionDtoList) {
                MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
                if (functionConfig != null) {
                    int hasAuth = 1;
                    String parentMenuId = functionConfig.getParentMenuId();
                    if (authMenu.containsKey(parentMenuId)) {
                        authMenu.put(parentMenuId, authMenu.get(parentMenuId) + hasAuth);
                    } else {
                        authMenu.put(parentMenuId, hasAuth);
                    }
                    if (!menuConfigList.containsKey(parentMenuId)) {
                        MenuFunctionConfig.MenuConfig menuConfig = MenuFunctionConfig.MenuConfig.getMenuConfig(parentMenuId);
                        menuConfigList.put(parentMenuId, menuConfig);
                    }
                }
            }
            List<MenuFunctionConfig.MenuConfig> menuList = new ArrayList<>(menuConfigList.values());
            Collections.sort(menuList, new Comparator<MenuFunctionConfig.MenuConfig>() {
                @Override
                public int compare(MenuFunctionConfig.MenuConfig o1, MenuFunctionConfig.MenuConfig o2) {
                    return o1.getMenuOrder() - o2.getMenuOrder();
                }
            });

            ObservableList<Menu> menus = menuBar.getMenus();
            for (MenuFunctionConfig.MenuConfig menuConfig : menuList) {
                if (authMenu.get(menuConfig.getMenuId()) == 0) {
                    continue;
                }
                Menu menu = new Menu();
                menu.setId(menuConfig.getMenuId());
                menu.setText(menuConfig.getMenuName());
                setMenuStyle(menu, menuConfig.getMenuIcon());
                menus.add(menu);
            }

        }
    }

    public static void initMenuName(MenuItem item) {
        for (MenuFunctionConfig.FunctionConfig tab : MenuFunctionConfig.FunctionConfig.values()) {
            if (tab.getMenuId().equals(item.getId())) {
                item.setText(getMenuName(tab.getCode(), tab.getName()));
                break;
            }
        }
    }

    public static boolean checkAuth(String checkType, String functionCode) {
        if (STR_1.equals(checkType)) {
            return CONFIG_SET.getCode().equals(functionCode);
        } else if (STR_2.equals(checkType)) {
            return CONFIG_SET.getMenuId().equals(functionCode);
        }
        return false;
    }

    public static boolean isSuperUser() {
        if (!CommonUtils.proScene()) {
            return true;
        }
        String[] mac = SecurityUtils.getDecryptString(SUPER_MAC_ADDRESS).split(STR_COMMA);
        String current = getMacAddress();
        for (String item : mac) {
            if (item.equals(current)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSyncMode() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        return StringUtils.equals(APP_MODE_SYNC, appConfigDto.getAppMode());
    }

    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 加载配置功能
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-09-24
     * @return: java.util.List<com.hoomoomoo.im.consts.FunctionConfig>
     */
    public static List<MenuFunctionConfig.FunctionConfig> getAppFunctionConfig(String appCode) {
        List<MenuFunctionConfig.FunctionConfig> functionConfigList = new ArrayList<>();
        for (MenuFunctionConfig.FunctionConfig functionConfig : MenuFunctionConfig.FunctionConfig.values()) {
            functionConfigList.add(functionConfig);
        }
        return functionConfigList;
    }

    /**
     * 获取无需授权功能号
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-09-24
     * @return: java.util.List<com.hoomoomoo.im.consts.FunctionConfig>
     */
    public static List<MenuFunctionConfig.FunctionConfig> getNoAuthFunctionConfig(String appCode) {
        List<MenuFunctionConfig.FunctionConfig> functionConfigList = new ArrayList<>();
        for (MenuFunctionConfig.FunctionConfig functionConfig : MenuFunctionConfig.FunctionConfig.values()) {
            int functionCode = Integer.valueOf(functionConfig.getCode());
            if (functionCode == FUNCTION_CONFIG_SET) {
                functionConfigList.add(functionConfig);
            }
        }
        return functionConfigList;
    }

    /**
     * 获取无需授权功能号
     *
     * @param appCode
     * @author: humm23693
     * @date: 2022-09-24
     * @return:
     */
    public static Map<String, MenuFunctionConfig.FunctionConfig> getNoAuthFunctionConfigMap(String appCode) {
        Map<String, MenuFunctionConfig.FunctionConfig> functionConfig = new LinkedHashMap<>();
        List<MenuFunctionConfig.FunctionConfig> functionConfigList = getNoAuthFunctionConfig(appCode);
        for (MenuFunctionConfig.FunctionConfig item : functionConfigList) {
            functionConfig.put(item.getCode(), item);
        }
        return functionConfig;
    }

    public static void sortFunctionDtoList(List<FunctionDto> functionDtoList) {
        Collections.sort(functionDtoList, (o1, o2) -> {
            String submitTimes1 = StringUtils.isBlank(o1.getSubmitTimes()) ? STR_0 : o1.getSubmitTimes().trim();
            String submitTimes2 = StringUtils.isBlank(o2.getSubmitTimes()) ? STR_0 : o2.getSubmitTimes().trim();
            return Integer.valueOf(submitTimes2) - Integer.valueOf(submitTimes1);
        });
    }

    /**
     * 获取当前版本
     *
     * @param
     * @author: humm23693
     * @date: 2021/9/24
     * @return: java.lang.String
     */
    public static String getVersion() {
        String version = STR_BLANK;
        try {
            List<String> versionContent = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION));
            if (CollectionUtils.isNotEmpty(versionContent)) {
                for (String item : versionContent) {
                    if (item.startsWith(NAME_CURRENT_VERSION)) {
                        String[] itemVersion = item.split(STR_COLON);
                        if (itemVersion.length == 2) {
                            version = itemVersion[1].trim();
                        }
                    }
                }
            }
        } catch (IOException e) {
            LoggerUtils.error(e);
        }
        return version;
        /*String[] verList = version.split(STR_POINT_SLASH);
        String versionYear = verList[0];
        String versionNum = verList[1];
        long ver = Long.parseLong(versionNum);
        ver--;
        String verAfter = String.valueOf(ver);
        int supplementLength = versionNum.length() - verAfter.length();
        for (int i=0; i<supplementLength; i++) {
            verAfter = STR_0 + verAfter;
        }
        return versionYear + STR_POINT + verAfter;*/
    }

    /**
     * 获取功能TAB
     *
     * @param tabPath
     * @param tabName
     * @author: humm23693
     * @date: 2021/04/18
     * @return:
     */
    public static Tab getFunctionTab(String tabPath, String tabName, String menuCode, String menuName) throws IOException {
        Parent tab = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(tabPath)));
        Tab menu = new Tab(tabName, tab);
        menu.setText(getMenuName(menuCode, menuName));
        return menu;
    }

    /**
     * TAB是否已打开
     *
     * @author: humm23693
     * @date: 2021/04/18
     * @return:
     */
    public static Tab getOpenTab(TabPane functionTab, MenuFunctionConfig.FunctionConfig functionConfig) {
        ObservableList<Tab> tabList = functionTab.getTabs();
        if (tabList != null) {
            for (Tab item : tabList) {
                if (item.getText().equals(getMenuName(functionConfig.getCode(), functionConfig.getName()))) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * TAB是否已打开
     *
     * @author: humm23693
     * @date: 2021/04/18
     * @return:
     */
    public static Tab getOpenTab(TabPane functionTab, String tabCode, String tabName) {
        ObservableList<Tab> tabList = functionTab.getTabs();
        if (tabList != null) {
            for (Tab item : tabList) {
                if (item.getText().equals(getMenuName(tabCode, tabName))) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 打开菜单
     *
     * @param event
     * @param functionTab
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void openMenu(ActionEvent event, TabPane functionTab) {
        try {
            String menuId = ((MenuItem) event.getSource()).getId();
            MenuFunctionConfig.FunctionConfig functionConfig = null;
            for (MenuFunctionConfig.FunctionConfig item : MenuFunctionConfig.FunctionConfig.values()) {
                if (item.getMenuId().equals(menuId)) {
                    functionConfig = item;
                    break;
                }
            }

            if (functionConfig == null) {
                LoggerUtils.info(String.format(BaseConst.MSG_FUNCTION_NOT_EXIST, functionConfig.getCode()));
            }

            LoggerUtils.info(String.format(BaseConst.MSG_OPEN, functionConfig.getName()));
            Tab tab = getOpenTab(functionTab, functionConfig);
            if (tab == null) {
                tab = getFunctionTab(functionConfig.getPath(), functionConfig.getName(), functionConfig.getCode(), functionConfig.getName());
                setTabStyle(tab, functionConfig);
                bindTabEvent(tab);
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    private static void getTaskTodoList(AppConfigDto appConfigDto, TabPane functionTab, String tabCode) throws IOException, InterruptedException {
        if (StringUtils.equals(FUNCTION_TASK_TODO, tabCode)) {
            MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.TASK_TODO;
            String userExtend = appConfigDto.getHepTaskUserExtend();
            if (StringUtils.isNotBlank(userExtend)) {
                String[] user = userExtend.split(STR_COMMA);
                String taskTodoPath = functionConfig.getPath();
                String taskTodoName = functionConfig.getName();
                for (String extend : user) {
                    String[] extendInfo = extend.split(STR_COLON);
                    if (extendInfo.length == 2) {
                        String tabName = getMenuName(extendInfo[1], taskTodoName);
                        appConfigDto.setActivateFunction(tabName);
                        Tab openTab = CommonUtils.getFunctionTab(taskTodoPath, taskTodoName, extendInfo[1], taskTodoName);
                        CommonUtils.setTabStyle(openTab, functionConfig);
                        CommonUtils.bindTabEvent(openTab);
                        functionTab.getTabs().add(openTab);
                    }
                }
            }
        }
    }

    public static void setTabStyle(Tab tab, MenuFunctionConfig.FunctionConfig functionConfig) {
        tab.getStyleClass().add("tabClass");
        if (functionConfig == null) {
            return;
        }
        MenuFunctionConfig.MenuConfig menuConfig = null;
        for (MenuFunctionConfig.MenuConfig item : MenuFunctionConfig.MenuConfig.values()) {
            if (item.getMenuId().equals(functionConfig.getParentMenuId())) {
                menuConfig = item;
                break;
            }
        }
        if (menuConfig == null) {
            return;
        }
    }

    private static void setMenuStyle(Menu menu, String icon) {
        setIcon(menu, "/conf/image/" + icon + ".png", MENUITEM_ICON_SIZE);
    }

    public static void setIcon(Object element, String iconPath, int size) {
        ImageView image = new ImageView(new Image(iconPath));
        image.setPreserveRatio(true);
        image.setFitHeight(size);
        if (element instanceof MenuItem) {
            ((MenuItem) element).setGraphic(image);
        } else if (element instanceof Menu) {
            ((Menu) element).setGraphic(image);
        } else if (element instanceof Tab) {
            ((Tab) element).setGraphic(image);
        }
    }

    public static void bindTabEvent(Tab tab) {
        if (tab == null) {
            return;
        }
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                try {
                    AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                    if (tab.getText().equals(getMenuName(TASK_TODO.getCode(), TASK_TODO.getName()))) {
                        // todo
                    }
                } catch (Exception e) {
                    LoggerUtils.error(e);
                }
            }
        });
    }

    public static void stopTimer(AppConfigDto appConfigDto, String timerId) {
        Timer timer = appConfigDto.getTimerMap().get(timerId);
        if (timer != null) {
            timer.cancel();
            appConfigDto.getTimerMap().remove(timerId);
        }
    }

    /**
     * 功能初始化
     *
     * @param location
     * @param resources
     * @author: humm23693
     * @date: 2022-09-24
     * @return: void
     */
    public static void initialize(URL location, ResourceBundle resources, TabPane functionTab, MenuBar menuBar) {
        try {
            AppCache.FUNCTION_TAB_CACHE = functionTab;

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            appConfigDto.setTabPane(functionTab);

            // 加载已授权功能
            showAuthFunction(menuBar, functionTab);

            String showTab = appConfigDto.getAppTabShow();
            if (StringUtils.isNotBlank(showTab)) {
                String[] tabs = showTab.split(BaseConst.STR_COMMA);
                for (String tab : tabs) {
                    if (StringUtils.isBlank(getName(tab))) {
                        LoggerUtils.info(String.format(BaseConst.MSG_FUNCTION_NOT_EXIST, tab));
                        continue;
                    }
                    // 校验功能是否有权限
                    if (!checkLicense(tab)) {
                        continue;
                    }
                    MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(tab);
                    Tab openTab = getFunctionTab(getPath(tab), getName(tab), functionConfig.getCode(), functionConfig.getName());
                    setTabStyle(openTab, functionConfig);
                    bindTabEvent(openTab);
                    functionTab.getTabs().add(openTab);
                    appConfigDto.setActivateFunction(openTab.getText());
                    getTaskTodoList(appConfigDto, functionTab, tab);
                }
            } else {
                // 默认打开有权限的第一个功能
                List<FunctionDto> functionDtoList = getAuthFunction();
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    FunctionDto functionDto = functionDtoList.get(0);
                    Tab tab = getFunctionTab(getPath(functionDto.getFunctionCode()), getName(functionDto.getFunctionCode()), functionDto.getFunctionCode(), functionDto.getFunctionName());
                    MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
                    setTabStyle(tab, functionConfig);
                    bindTabEvent(tab);
                    functionTab.getTabs().add(tab);
                    appConfigDto.setActivateFunction(tab.getText());
                }
            }
            // 监听获取当前正在打开的功能
            functionTab.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null) {
                    String tabName = newTab.getText();
                    try {
                        AppConfigDto appConfig = ConfigCache.getAppConfigDtoCache();
                        String activatePrevFunction = appConfig.getActivateFunction();
                        appConfig.setActivatePrevFunction(activatePrevFunction);
                        appConfig.setActivateFunction(tabName);
                        LoggerUtils.info(String.format("上次激活页签【%s】", activatePrevFunction));
                        LoggerUtils.info(String.format("当前激活页签【%s】", tabName));
                    } catch (Exception e) {
                        LoggerUtils.error(e);
                    }
                }
            });
            LoggerUtils.appStartInfo(String.format(BaseConst.MSG_INIT, NAME_CONFIG_VIEW));
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    public static String getMenuName(String menuCode, String menuName) {
        return menuCode + STR_COLON + menuName;
    }

    public static String getComponentValue(Object obj) {
        if (obj instanceof TextArea) {
            ((TextArea) obj).getText().trim();
        } else if (obj instanceof TextField) {
            return ((TextField) obj).getText().trim();
        } else if (obj instanceof ComboBox) {
            return (String) ((ComboBox) obj).getSelectionModel().getSelectedItem();
        }
        return STR_BLANK;
    }

    public static String getIntervalDays(String start, String end) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN3);
        Long startDate = simpleDateFormat.parse(start).getTime();
        Long endDate = simpleDateFormat.parse(end).getTime();
        return String.valueOf((endDate - startDate) / 24 / 60 / 60 / 1000);
    }

    public static String getMacAddress() {
        if (!StringUtils.isBlank(MAC_ADDRESS_CACHE)) {
            return MAC_ADDRESS_CACHE;
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (byte b : mac) {
                        stringBuilder.append(String.format("%02X:", b));
                    }
                    if (stringBuilder.length() > 0) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    MAC_ADDRESS_CACHE = stringBuilder.toString();
                }
            }
        } catch (SocketException e) {
            LoggerUtils.error(e);
        }
        return MAC_ADDRESS_CACHE;
    }

    /**
     * 功能类型转换
     *
     * @param functionConfigList
     * @author: humm23693
     * @date: 2022-09-24
     * @return: java.util.List<com.hoomoomoo.im.dto.FunctionDto>
     */
    public static List<FunctionDto> functionConfigToFunctionDto(String appCode, List<MenuFunctionConfig.FunctionConfig> functionConfigList) {
        List<FunctionDto> functionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(functionConfigList)) {
            for (MenuFunctionConfig.FunctionConfig item : functionConfigList) {
                functionDtoList.add(new FunctionDto(item.getCode(), item.getName()));
            }
        }
        return functionDtoList;
    }

    public static String trimStrToBlank(String str) {
        return str.trim().replaceAll(STR_S_SLASH, STR_BLANK);
    }

    public static String trimStrToSpace(String str) {
        return str.trim().replaceAll(STR_S_SLASH, STR_SPACE);
    }

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static Service<Void> getCloseInfoService(int millis) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(millis);
                        return null;
                    }
                };
            }
        };
        return service;
    }

    public static void showTipsByInfo(String msg) {
        showTips(STR_1, msg, null, null, true, false, DEFAULT_AUTO_CLOSE_MILLIS);
    }

    public static void showTipsByInfo(String msg, int millis) {
        showTips(STR_1, msg, null, null, true, false, millis);
    }

    public static void showTipsByError(String msg) {
        showTips(STR_0, msg, null, null, true, false, DEFAULT_AUTO_CLOSE_MILLIS);
    }

    public static void showTipsByError(String msg, int millis) {
        showTips(STR_0, msg, null, null, true, false, millis);
    }

    public static void showTipsByError(String msg, boolean autoClose) {
        showTips(STR_0, msg, null, null, autoClose, false, DEFAULT_AUTO_CLOSE_MILLIS);
    }

    public static void showTipsByErrorNotAutoClose(String msg, List<String> detail) {
        boolean showDetail = false;
        String all = STR_BLANK;
        if (detail.size() > 30) {
            all = msg + STR_NEXT_LINE_2 + detail.stream().map(Object::toString).collect(Collectors.joining());
            detail = detail.subList(0, 30);
            detail.add("显示部分内容 . 详情参阅文件 " + getSpecialString(50, STR_POINT + STR_SPACE));
            showDetail = true;
        }
        showTips(STR_0, msg, detail.stream().map(Object::toString).collect(Collectors.joining()), all, false, showDetail, DEFAULT_AUTO_CLOSE_MILLIS);
    }

    public static void showTips(String tipsType, String msg, String detail, String all, boolean autoClose, boolean showDetail, int millis) {
        Alert alert;
        if (STR_0.equals(tipsType)) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setTitle("提示");
        alert.setHeaderText(String.format("%s", msg));
        if (detail != null) {
            alert.setContentText(detail);
        }
        if (autoClose) {
            if (detail == null) {
                int second = millis / 1000;
                int minute = second / 60;
                if (minute > 0) {
                    second = second % 60;
                }
                String time = (minute > 0 ? minute + "分" : STR_BLANK) + (second > 0 ? second + "秒" : STR_BLANK);
                alert.setContentText(time + "后将自动关闭");
            }
            Service<Void> service = getCloseInfoService(millis);
            service.setOnSucceeded(e -> alert.hide());
            service.start();
        }
        if (showDetail) {
            ButtonType detailBtn = new ButtonType("详情");
            ButtonType closeBtn = new ButtonType("关闭");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(detailBtn, closeBtn);
            alert.show();
            alert.setOnHidden(event -> {
                Optional<ButtonType> result = Optional.ofNullable(alert.getResult());
                result.ifPresent(buttonType -> {
                    if (buttonType == detailBtn) {
                        try {
                            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                            appConfigDto.setPageType(PAGE_TYPE_LOG_DETAIL);
                            appConfigDto.setErrorLogDetail(all);
                            Stage stage = appConfigDto.getErrorLogStage();
                            // 每次页面都重新打开
                            if (stage != null) {
                                stage.close();
                                appConfigDto.setErrorLogStage(null);
                            }
                            Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_BLANK_SET_FXML)));
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
                            stage = new Stage();
                            stage.getIcons().add(new Image(PATH_ICON));
                            stage.setScene(scene);
                            stage.setTitle("详情");
                            stage.setResizable(false);
                            stage.show();
                            appConfigDto.setErrorLogStage(stage);
                            stage.setOnCloseRequest(columnEvent -> {
                                appConfigDto.getErrorLogStage().close();
                                appConfigDto.setErrorLogStage(null);
                            });
                        } catch (Exception e) {
                            LoggerUtils.error(e);
                        }
                    }
                });
            });
        }
        alert.show();
    }

    public static void showTipsByRest() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("强制休息提醒");
        alert.setHeaderText(". . . . . . 已启动强制休息计划 . . . . . .");
        alert.setContentText(". . . 你已超负荷工作 ... 请注意休息 . . . ");
        alert.show();
    }

    public static int findLastCapital(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (Character.isUpperCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String getSimpleVer(String ver) {
        return ver.replaceAll("TA6.0V", STR_BLANK).replaceAll("TA6.0-FUND.V", STR_BLANK);
    }

    public static String getComplexVer(String ver) {
        if (ver.contains(KEY_VERSION_YEAR_2022)) {
            if (!ver.contains(STR_VERSION_PREFIX_2022)) {
                ver = STR_VERSION_PREFIX_2022 + ver;
            }
        } else {
            if (!ver.contains(STR_VERSION_PREFIX)) {
                ver = STR_VERSION_PREFIX + ver;
            }
        }
        return ver;
    }

    public static String formatStrToSingleSpace(String str) {
        if (StringUtils.isBlank(str)) {
            return STR_BLANK;
        }
        return str.replaceAll("\\s+", " ").trim();
    }

    /**
     * 文件路径转换
     *
     * @param path
     * @param appCode
     * @return
     */
    public static String dealFilePath(String path, String appCode) {
        int left = path.indexOf(appCode + STR_HYPHEN);
        int right = path.indexOf(FILE_TYPE_JAR);
        if (left > 0 && right > 0) {
            return path.substring(0, left) + "classes" + path.substring(right + FILE_TYPE_JAR.length());
        }
        return path;
    }

    public static void deleteVersionFile(String appCode) {
        if (!CommonUtils.proScene()) {
            return;
        }
        String path = FileUtils.getPathFolder().replace(KEY_FILE_SLASH, STR_BLANK).split(START_MODE_JAR)[0];
        path = path.substring(0, path.lastIndexOf("/"));
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        List<File> fileList = Arrays.asList(file.listFiles()).stream()
                .filter(item -> item.getName().endsWith(FILE_TYPE_JAR) && item.getName().contains(appCode)).collect(Collectors.toList());
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified() - o1.lastModified()).intValue();
            }
        });
        if (fileList.size() > 1) {
            Iterator<File> iterator = fileList.listIterator();
            while (iterator.hasNext()) {
                FileUtils.deleteFile(iterator.next());
            }
        }
    }

    public static void clearLog() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        int appLogSaveDay = appConfigDto.getAppLogSaveDay();
        if (appLogSaveDay <= 0) {
            return;
        }
        int index;
        MenuFunctionConfig.FunctionConfig[] functionConfigs = MenuFunctionConfig.FunctionConfig.values();
        List<String> logs = Arrays.stream(functionConfigs).map(item -> item.getLogFolder()).distinct().collect(Collectors.toList());
        logs.add("appLog");
        for (String file : logs) {
            File[] log = new File(FileUtils.getFilePath(String.format(SUB_PATH_LOG, file))).listFiles();
            if (log == null) {
                continue;
            }
            List<File> fileList = Arrays.asList(log);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.valueOf(o2.lastModified() - o1.lastModified()).intValue();
                }
            });
            if (fileList.size() > appLogSaveDay) {
                index = 0;
                Iterator<File> iterator = fileList.listIterator();
                while (iterator.hasNext()) {
                    File item = iterator.next();
                    index++;
                    if (appLogSaveDay >= index) {
                        continue;
                    }
                    FileUtils.deleteFile(item);
                }
            }
        }
    }

    public static void scanTimer() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        ImTimer timer = new ImTimer(SYSTEM_TOOL_TIMER_SCAN_TIMER);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Point pos = MouseInfo.getPointerInfo().getLocation();
                String currentPosition = pos.x + " * " + pos.y;
                if (StringUtils.equals(currentPosition, SCAN_CURRENT_POSITION)) {
                    SCAN_CURRENT_POSITION_TIMES++;
                } else {
                    SCAN_CURRENT_POSITION = currentPosition;
                    SCAN_CURRENT_POSITION_TIMES = 0;
                }
            }
        };
        timer.schedule(timerTask, 5000, appConfigDto.getSystemToolTimerScanTimer() * 1000);
        appConfigDto.getTimerMap().put(SYSTEM_TOOL_TIMER_SCAN_TIMER, timer);
    }

    public static boolean stopScan(AppConfigDto appConfigDto) {
        if (SCAN_CURRENT_POSITION_TIMES >= appConfigDto.getSystemToolTimerScanTimes()) {
            return true;
        }
        return false;
    }

    public static void scanLog() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        Map<String, Long> fileTime = new HashMap<>();
        ImTimer timer = new ImTimer(SYSTEM_TOOL_LOG_SCAN_TIMER);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                if ((System.currentTimeMillis() - start) / 1000 > appConfigDto.getSystemToolGarbageCollectionTimer()) {
                    start = System.currentTimeMillis();
                    LoggerUtils.info("请求垃圾回收");
                    System.gc();
                }
                if (CommonUtils.stopScan(appConfigDto)) {
                    if (appConfigDto.getScanTips()) {
                        LoggerUtils.info("长时间未活动，日志扫描略过");
                        appConfigDto.setScanTips(false);
                    }
                    return;
                }
                appConfigDto.setScanTips(true);
                Platform.runLater(() -> {
                    File log = new File(FileUtils.getFilePath(PATH_LOG_ROOT));
                    if (log.exists()) {
                        File[] subLog = log.listFiles();
                        for (File item : subLog) {
                            if (item.isDirectory()) {
                                List<File> files = Arrays.asList(item.listFiles());
                                if (CollectionUtils.isNotEmpty(files)) {
                                    Collections.sort(files, new Comparator<File>() {
                                        @Override
                                        public int compare(File o1, File o2) {
                                            return Long.valueOf(o2.lastModified() - o1.lastModified()).intValue();
                                        }
                                    });
                                    File file = files.get(0);
                                    String name = file.getAbsolutePath();
                                    Long modify = file.lastModified();
                                    Long modifyExist = fileTime.get(name);
                                    if (modifyExist != null && modify.compareTo(modifyExist) == 0) {
                                        continue;
                                    }
                                    fileTime.put(name, modify);
                                    try {
                                        List<String> content = FileUtils.readNormalFile(file.getAbsolutePath());
                                        ArrayList errorMessage = new ArrayList();
                                        String tipsDate = STR_0;
                                        for (int i = 0; i < content.size(); i++) {
                                            String line = content.get(i).trim();
                                            String tipsType = item.getName();
                                            if (line.startsWith(getCurrentDateTime4()) && CollectionUtils.isNotEmpty(errorMessage)) {
                                                showErrorMessage(appConfigDto, tipsType, tipsDate, file.getAbsolutePath(), errorMessage);
                                            }
                                            if (line.contains("Exception") || CollectionUtils.isNotEmpty(errorMessage)) {
                                                if (CollectionUtils.isEmpty(errorMessage)) {
                                                    String date = content.get(i - 1);
                                                    if (date.length() == 19) {
                                                        tipsDate = date;
                                                    }
                                                }
                                                if (!StringUtils.equals(MSG_DIVIDE_LINE.trim(), line)) {
                                                    errorMessage.add(line + STR_NEXT_LINE);
                                                }
                                                if (i == content.size() - 1) {
                                                    showErrorMessage(appConfigDto, tipsType, tipsDate, file.getAbsolutePath(), errorMessage);
                                                }
                                            }
                                        }
                                        cleanFile(content);
                                    } catch (Exception e) {
                                        LoggerUtils.error(e);
                                    }
                                }
                                cleanFile(files);
                            }
                            cleanFile(item);
                        }
                        appConfigDto.setInitScanLog(false);
                    }
                    cleanFile(log);
                });
            }
        };
        timer.schedule(timerTask, 5000, appConfigDto.getSystemToolLogScanTimer() * 1000);
        appConfigDto.getTimerMap().put(SYSTEM_TOOL_LOG_SCAN_TIMER, timer);
    }

    private static void showErrorMessage(AppConfigDto appConfigDto, String tipsType, String tipsDate, String fileName, List<String> message) {
        Map<String, String> scanLogTipsIndex = appConfigDto.getScanLogTipsIndex();
        boolean show = false;
        if (scanLogTipsIndex.containsKey(tipsType)) {
            String date = scanLogTipsIndex.get(tipsType);
            if (tipsDate.compareTo(date) > 0) {
                show = true;
            }
        } else {
            show = true;
        }
        if (show) {
            scanLogTipsIndex.put(tipsType, tipsDate);
            if (!appConfigDto.getInitScanLog()) {
                if (printImLog(message)) {
                    showTipsByErrorNotAutoClose(fileName + getSpecialString(150, STR_SPACE), message);
                }
            }
        }
        message.clear();
    }

    private static boolean printImLog(List<String> message) {
        if (CollectionUtils.isEmpty(message)) {
            return false;
        }
        String msg = message.stream().collect(Collectors.joining(STR_COMMA));
        if (msg.contains(NAME_NO_AUTH) || msg.contains(KEY_ERROR_CODE_NO_AUTH_SERVICE) || msg.contains(SKIP_LOG_TIPS)) {
            return false;
        }
        if (msg.contains(KEY_PACKAGE_IM)) {
            return true;
        }
        return false;
    }

    private static String getSpecialString(int num, String content) {
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < num; i++) {
            val.append(content);
        }
        return val.toString();
    }

    public static void cleanFile(Object... files) {
        if (files != null) {
            for (Object file : files) {
                file = null;
            }
        }
    }

    public static String[] getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        // 获取JVM总内存
        long totalMemory = runtime.totalMemory();
        // 获取空闲内存
        long freeMemory = runtime.freeMemory();
        // 获取最大可用内存
        long maxMemory = runtime.maxMemory();
        // 计算已用内存
        long usedMemory = totalMemory - freeMemory;
        String[] res = new String[3];
        String totalMemoryUnit = formatMemoryInfo(totalMemory);
        String usedMemoryUnit = formatMemoryInfo(usedMemory);
        res[0] = totalMemoryUnit + STR_SLASH + usedMemoryUnit;
        res[1] = totalMemoryUnit.replace(KEY_UNIT_MB, STR_BLANK);
        res[2] = usedMemoryUnit.replace(KEY_UNIT_MB, STR_BLANK);
        ;
        return res;
    }

    private static String formatMemoryInfo(long memory) {
        return String.valueOf(memory / (1024 * 1024));
    }

    public static String getRealDate(String oriDate) {
        StringBuilder date = new StringBuilder();
        if (StringUtils.isNotBlank(oriDate)) {
            for (int i = 0; i < oriDate.length(); i++) {
                char item = oriDate.charAt(i);
                if (Character.isDigit(item)) {
                    date.append(item);
                }
            }
        }
        if (date.length() > 8) {
            return date.substring(8);
        }
        return date.toString();
    }

    public static boolean isDebug() {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            return StringUtils.equals(KEY_LOG_DEBUG, appConfigDto.getAppLogLevel());
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
        return false;
    }

    public static boolean proScene() {
        return FileUtils.getPathFolder().contains(START_MODE_JAR);
    }

    /**
     * map 转 javaBean
     *
     * @param map
     * @param clazz
     * @author: hoomoomoo
     * @date: 2020/12/02
     * @return:
     */
    public static Object mapToObject(Map<String, String> map, Class clazz) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = clazz.newInstance();
        BeanUtils.populate(obj, map);
        return obj;
    }

    /**
     * map 转换
     *
     * @param map
     * @author: humm23693
     * @date: 2020/12/02
     * @return:
     */
    public static LinkedHashMap<String, String> convertMap(HashMap<String, String> map, boolean deletePoint) {
        if (map == null) {
            return null;
        }
        LinkedHashMap<String, String> afterMap = new LinkedHashMap<>(map.size());
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> item = iterator.next();
            String key = item.getKey();
            String value = item.getValue();
            StringBuffer convertKey = new StringBuffer();
            if (deletePoint) {
                boolean isPoint = false;
                for (int i = 0; i < key.length(); i++) {
                    char single = key.charAt(i);
                    if (String.valueOf(single).equals(STR_POINT)) {
                        isPoint = true;
                        continue;
                    } else {
                        if (isPoint) {
                            convertKey.append(String.valueOf(single).toUpperCase());
                        } else {
                            convertKey.append(single);
                        }
                        isPoint = false;
                    }
                }
            } else {
                convertKey.append(key);
            }
            afterMap.put(convertKey.toString(), value);
        }
        return afterMap;
    }

    public static void closeTab(MenuFunctionConfig.FunctionConfig functionConfig, boolean changeTab) throws Exception {
        ObservableList<Tab> tabs = AppCache.FUNCTION_TAB_CACHE.getTabs();
        Iterator<Tab> iterator = tabs.listIterator();
        while (iterator.hasNext()) {
            Tab tab = iterator.next();
            if (tab.getText().equals(CommonUtils.getMenuName(functionConfig.getCode(), functionConfig.getName()))) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LoggerUtils.error(e);
                }
                iterator.remove();
                break;
            }
            if (changeTab) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                String activateFunction = appConfigDto.getActivateFunction();
                if (StringUtils.isNotBlank(activateFunction)) {
                    String tabCode = activateFunction.split(STR_COLON)[0];
                    String tabName = activateFunction.split(STR_COLON)[1];
                    AppCache.FUNCTION_TAB_CACHE.getSelectionModel().select(CommonUtils.getOpenTab(AppCache.FUNCTION_TAB_CACHE, tabCode, tabName));
                }
            }
        }
    }
}
