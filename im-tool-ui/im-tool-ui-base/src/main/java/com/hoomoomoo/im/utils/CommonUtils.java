package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.AppCache;
import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.BaseDto;
import com.hoomoomoo.im.dto.FunctionDto;
import com.hoomoomoo.im.dto.LicenseDto;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        return date.substring(0, 4) + SYMBOL_HYPHEN + date.substring(4, 6) + SYMBOL_HYPHEN + date.substring(6);
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

    public static String checkLicenseDate(AppConfigDto appConfigDto) {
        String tips = SYMBOL_EMPTY;
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
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (StringUtils.isBlank(functionCode)) {
            if (Integer.valueOf(CommonUtils.getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
                LoggerUtils.info(String.format(MSG_LICENSE_EXPIRE, licenseDto.getEffectiveDate()));
                return false;
            } else {
                return true;
            }
        }
        if (!checkUser(appConfigDto, STR_1, functionCode)) {
            if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                LoggerUtils.info(String.format(MSG_LICENSE_NOT_USE, MenuFunctionConfig.FunctionConfig.getName(functionCode)));
            }
            return false;
        }
        if (checkAuth(STR_1, functionCode)) {
            return true;
        }
        List<FunctionDto> functionDtoList = licenseDto.getFunction();
        if (CollectionUtils.isEmpty(functionDtoList)) {
            if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                LoggerUtils.info(String.format(MSG_LICENSE_NOT_AUTH, MenuFunctionConfig.FunctionConfig.getName(functionCode)));
            }
            return false;
        }
        for (FunctionDto functionDto : functionDtoList) {
            if (functionCode.equals(functionDto.getFunctionCode())) {
                return true;
            }
        }
        if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
            LoggerUtils.info(String.format(MSG_LICENSE_NOT_AUTH, MenuFunctionConfig.FunctionConfig.getName(functionCode)));
        }
        return false;
    }

    public static List<FunctionDto> getAuthFunction() throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        LicenseDto licenseDto = appConfigDto.getLicense();
        if (Integer.valueOf(CommonUtils.getCurrentDateTime3()) > Integer.valueOf(licenseDto.getEffectiveDate())) {
            return new ArrayList<>();
        }
        return licenseDto.getFunction();
    }

    public static void showAuthFunction(MenuBar menuBar, TabPane functionTab) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
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
                    if (!checkUser(appConfigDto, STR_2, functionConfig.getMenuId())) {
                        if (checkUser(appConfigDto.getAppUser(), APP_USER_IM)) {
                            LoggerUtils.info(String.format(MSG_LICENSE_NOT_USE, functionDto.getFunctionName()));
                        }
                        continue;
                    }
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
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            Map<String, MenuFunctionConfig.MenuConfig> menuConfigList = new LinkedHashMap<>(16);
            Map<String, Integer> authMenu = new LinkedHashMap<>(16);
            for (FunctionDto functionDto : functionDtoList) {
                MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
                if (functionConfig != null) {
                    int hasAuth = checkUser(appConfigDto, STR_1, functionDto.getFunctionCode()) ? 1 : 0;
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
                setMenuStyle(menu);
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
            return ABOUT_INFO.getCode().equals(functionCode) || CONFIG_SET.getCode().equals(functionCode)
                    || FUNCTION_STAT_INFO.getCode().equals(functionCode) || JD_COOKIE.getCode().equals(functionCode);
        } else if (STR_2.equals(checkType)) {
            return ABOUT_INFO.getMenuId().equals(functionCode) || CONFIG_SET.getMenuId().equals(functionCode)
                    || FUNCTION_STAT_INFO.getMenuId().equals(functionCode) || JD_COOKIE.getMenuId().equals(functionCode);
        }
        return false;
    }

    public static boolean checkUser(AppConfigDto appConfigDto, String checkType, String functionCode) {
        String appUser = appConfigDto.getAppUser();
        if (STR_1.equals(checkType)) {
            if (SVN_REALTIME_STAT.getCode().equals(functionCode) || SVN_HISTORY_STAT.getCode().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_SVN);
            } else if (TASK_TODO.getCode().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_HEP);
            }
        } else if (STR_2.equals(checkType)) {
            if (SVN_REALTIME_STAT.getMenuId().equals(functionCode) || SVN_HISTORY_STAT.getMenuId().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_SVN);
            } else if (TASK_TODO.getMenuId().equals(functionCode)) {
                return checkUser(appUser, APP_USER_IM_HEP);
            }
        }
        return true;
    }

    public static boolean checkUser(String appUser, String userName) {
        if (StringUtils.isBlank(appUser)) {
            return false;
        }
        String[] user = appUser.split(SYMBOL_COMMA);
        for (String item : user) {
            if (userName.equals(item)) {
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
            int functionCode = Integer.valueOf(functionConfig.getCode());
            if (APP_CODE_TA.equals(appCode) && functionCode < FUNCTION_CODE_1000) {
                functionConfigList.add(functionConfig);
            } else if (APP_CODE_SHOPPING.equals(appCode) && functionCode >= FUNCTION_CODE_1000 && functionCode < FUNCTION_CODE_2000) {
                functionConfigList.add(functionConfig);
            }
            if (functionCode >= FUNCTION_CODE_2000) {
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
     * @return: java.util.List<com.hoomoomoo.im.consts.FunctionConfig>
     */
    public static List<MenuFunctionConfig.FunctionConfig> getNoAuthFunctionConfig(String appCode) {
        List<MenuFunctionConfig.FunctionConfig> functionConfigList = new ArrayList<>();
        for (MenuFunctionConfig.FunctionConfig functionConfig : MenuFunctionConfig.FunctionConfig.values()) {
            int functionCode = Integer.valueOf(functionConfig.getCode());
            if (JD_COOKIE.getCode().equals(functionCode)) {
                functionConfigList.add(functionConfig);
            }
            if (functionCode > 2000) {
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

    public static boolean isNumber(String val) {
        if (StringUtils.isBlank(val)) {
            return true;
        }
        try {
            Integer.valueOf(val);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
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
        String version = SYMBOL_EMPTY;
        try {
            List<String> versionContent = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION), false);
            if (CollectionUtils.isNotEmpty(versionContent)) {
                for (String item : versionContent) {
                    if (item.startsWith(NAME_CURRENT_VERSION)) {
                        String[] itemVersion = item.split(SYMBOL_COLON);
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
        Tab menu=  new Tab(tabName, tab);
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
    public static Tab isOpen(TabPane functionTab, MenuFunctionConfig.FunctionConfig functionConfig) {
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
            Tab tab = CommonUtils.isOpen(functionTab, functionConfig);
            if (tab == null) {
                tab = CommonUtils.getFunctionTab(functionConfig.getPath(), functionConfig.getName(),
                        functionConfig.getCode(), functionConfig.getName());
                setTabStyle(tab);
                bindTabEvent(tab);
                functionTab.getTabs().add(tab);
            }
            functionTab.getSelectionModel().select(tab);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private static void setTabStyle(Tab tab) {
        tab.getStyleClass().add("tabClass");
    }

    private static void setMenuStyle(Menu menu) {
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

    private static void bindTabEvent (Tab tab) {
        if (tab == null) {
            return;
        }
        tab.setOnClosed(new EventHandler<Event>() {
            @SneakyThrows
            @Override
            public void handle(Event t) {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
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

            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String showTab = appConfigDto.getAppTabShow();
            if (StringUtils.isNotBlank(showTab)) {
                String[] tabs = showTab.split(BaseConst.SYMBOL_COMMA);
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
                    Tab openTab = CommonUtils.getFunctionTab(getPath(tab),
                            getName(tab), functionConfig.getCode(), functionConfig.getName());
                    setTabStyle(openTab);
                    bindTabEvent(openTab);
                    functionTab.getTabs().add(openTab);
                }
            } else {
                // 默认打开有权限的第一个功能
                List<FunctionDto> functionDtoList = CommonUtils.getAuthFunction();
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    FunctionDto functionDto = functionDtoList.get(0);
                    Tab tab = CommonUtils.getFunctionTab(getPath(functionDto.getFunctionCode()),
                            getName(functionDto.getFunctionCode()), functionDto.getFunctionCode(), functionDto.getFunctionName());
                    setTabStyle(tab);
                    bindTabEvent(tab);
                    functionTab.getTabs().add(tab);
                }
            }
            LoggerUtils.appStartInfo(String.format(BaseConst.MSG_INIT, NAME_CONFIG_VIEW));
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static String getMenuName(String menuCode, String menuName) {
        return menuCode + SYMBOL_COLON + menuName;
    }

}
