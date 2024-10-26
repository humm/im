package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.AppCache;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 格式化日期
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static String getCurrentDateTime5(String date) {
        if (date.length() != 8) {
            return date;
        }
        return date.substring(0, 4) + STR_HYPHEN + date.substring(4, 6) + STR_HYPHEN + date.substring(6);
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
    public static String getLastDayByWeek2() {
        return getLastDayByWeek().replaceAll(STR_HYPHEN, STR_BLANK);
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
            LoggerUtils.info(e);
        }
        return tips;
    }

    public static boolean checkLicense(String functionCode) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (StringUtils.isBlank(functionCode)) {
            if (Integer.valueOf(CommonUtils.getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
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
            functionDtoList = functionConfigToFunctionDto(appCode, CommonUtils.getAppFunctionConfig(appCode));
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
        if (Integer.valueOf(CommonUtils.getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
            return new ArrayList<>();
        }
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (isSuperUser()) {
            String appCode = ConfigCache.getAppCodeCache();
            functionDtoList = functionConfigToFunctionDto(appCode, CommonUtils.getAppFunctionConfig(appCode));
        }
        return functionDtoList;
    }

    public static void showAuthFunction(MenuBar menuBar, TabPane functionTab) throws Exception {
        String appCode = ConfigCache.getAppCodeCache();
        List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
        if (isSuperUser()) {
            functionDtoList = functionConfigToFunctionDto(appCode, CommonUtils.getAppFunctionConfig(appCode));
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
                    MenuItem item = new MenuItem();
                    item.setId(functionConfig.getMenuId());
                    item.setText(functionConfig.getName());
                    initMenuName(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            CommonUtils.openMenu(event, functionTab);
                        }
                    });
                    list.add(item);
                }
            }
        }
    }

    public static void initMenuBar(MenuBar menuBar, List<FunctionDto> functionDtoList) throws Exception {
        if (CollectionUtils.isNotEmpty(functionDtoList)) {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
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
        String[] mac = SecurityUtils.getDecryptString(SUPER_MAC_ADDRESS).split(STR_COMMA);
        String current = getMacAddress();
        for (String item : mac) {
            if (item.equals(current)) {
                return true;
            }
        }
        return false;
    }

    public static String initialUpper(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String initialLower(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
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
            List<String> versionContent = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION), false);
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
            LoggerUtils.info(e);
        }
        return version;
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
                if (item.getText().equals(CommonUtils.getMenuName(tabCode, tabName))) {
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
            String menuId = ((MenuItem)event.getSource()).getId();
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
            Tab tab = CommonUtils.getOpenTab(functionTab, functionConfig);
            if (tab == null) {
                tab = CommonUtils.getFunctionTab(functionConfig.getPath(), functionConfig.getName(), functionConfig.getCode(), functionConfig.getName());
                setTabStyle(tab, functionConfig);
                bindTabEvent(tab);
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
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
        //CommonUtils.setIcon(tab, "/conf/image/" + menuConfig.getMenuIcon() + ".png", MENUITEM_ICON_SIZE);
    }

    private static void setMenuStyle(Menu menu, String icon) {
        CommonUtils.setIcon(menu, "/conf/image/" + icon + ".png", MENUITEM_ICON_SIZE);
    }

    public static void setIcon(Object element, String iconPath, int size) {
        ImageView image = new ImageView(new Image(iconPath));
        image.setPreserveRatio(true);
        image.setFitHeight(size);
        if (element instanceof MenuItem) {
            ((MenuItem)element).setGraphic(image);
        } else if (element instanceof Menu) {
            ((Menu)element).setGraphic(image);
        } else if (element instanceof Tab) {
            ((Tab)element).setGraphic(image);
        }
    }

    public static void bindTabEvent (Tab tab) {
        if (tab == null) {
            return;
        }
        tab.setOnClosed(new EventHandler<Event>() {
            @SneakyThrows
            @Override
            public void handle(Event t) {
                AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
                List<Timer> timerList = appConfigDto.getTimerList();
                if (CollectionUtils.isNotEmpty(timerList)) {
                    for (Timer timer : timerList) {
                        timer.cancel();
                    }
                }
            }
        });
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
            // 加载已授权功能
            CommonUtils.showAuthFunction(menuBar, functionTab);

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            appConfigDto.setTabPane(functionTab);

            String showTab = appConfigDto.getAppTabShow();
            if (StringUtils.isNotBlank(showTab)) {
                String[] tabs = showTab.split(BaseConst.STR_COMMA);
                for (String tab : tabs) {
                    if (StringUtils.isBlank(getName(tab))) {
                        LoggerUtils.info(String.format(BaseConst.MSG_FUNCTION_NOT_EXIST, tab));
                        continue;
                    }
                    // 校验功能是否有权限
                    if (!CommonUtils.checkLicense(tab)) {
                        continue;
                    }
                    MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(tab);
                    Tab openTab = CommonUtils.getFunctionTab(getPath(tab), getName(tab), functionConfig.getCode(), functionConfig.getName());
                    setTabStyle(openTab, functionConfig);
                    bindTabEvent(openTab);
                    functionTab.getTabs().add(openTab);
                }
            } else {
                // 默认打开有权限的第一个功能
                List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    FunctionDto functionDto = functionDtoList.get(0);
                    Tab tab = CommonUtils.getFunctionTab(getPath(functionDto.getFunctionCode()), getName(functionDto.getFunctionCode()), functionDto.getFunctionCode(), functionDto.getFunctionName());
                    MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
                    setTabStyle(tab, functionConfig);
                    bindTabEvent(tab);
                    functionTab.getTabs().add(tab);
                }
            }
            // 监听获取当前正在打开的功能
            functionTab.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null) {
                    String tab = newTab.getText();
                    // appConfigDto.setActivateFunction(tab);
                }
            });
            LoggerUtils.appStartInfo(String.format(BaseConst.MSG_INIT, NAME_CONFIG_VIEW));
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static String getMenuName(String menuCode, String menuName) {
        return menuCode + STR_COLON + menuName;
    }

    public static String getComponentValue(Object obj) {
        if (obj instanceof TextArea) {
            ((TextArea) obj).getText().trim();
        } else if (obj instanceof TextField) {
            return ((TextField)obj).getText().trim();
        } else if (obj instanceof ComboBox) {
            return (String)((ComboBox)obj).getSelectionModel().getSelectedItem();
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
            LoggerUtils.info(e);
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

    public static Service<Void> getCloseInfoService() {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }
            };
            }
        };
        return service;
    }

    public static void showTipsByInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(String.format(". . . . . . %s . . . . . .", msg));
        Service<Void> service = getCloseInfoService();
        service.setOnSucceeded(e -> alert.hide());
        service.start();
        alert.show();
    }

    public static void showTipsByError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("提示");
        alert.setHeaderText(String.format(". . . . . . %s . . . . . .", msg));
        /*Service<Void> service = getCloseInfoService();
        service.setOnSucceeded(e -> alert.hide());
        service.start();*/
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
        if (ver.startsWith("2022")) {
            ver = "TA6.0V" + ver;
        } else {
            ver ="TA6.0-FUND.V" + ver;
        }
        return ver;
    }
}
