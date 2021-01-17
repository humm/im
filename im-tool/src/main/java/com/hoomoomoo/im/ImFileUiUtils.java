package com.hoomoomoo.im;

import com.hoomoomoo.im.model.*;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.UnZipAnRarUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author hoomoomoo
 * @description 文件工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/12/02
 */
public class ImFileUiUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImFileUiUtils.class);

    private static final String CLAZZ = "ImFileUiUtils.class";

    private static final String WINDOWS_UI = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    /**
     * 应用配置文件
     */
    private static final String APP_LABEL_PROPERTIES = "conf/app.label.properties";

    /**
     * 应用配置文件
     */
    private static final String APP_CONFIG_PROPERTIES = "conf/app.config.properties";

    /**
     * 版本配置文件
     */
    private static final String APP_VERSION_PROPERTIES = "conf/app.version.properties";

    /**
     * 文件路径配置
     */
    private static final String FILE_PATH = "path.txt";

    /**
     * 版本配置信息
     */
    private static List<VersionInfo> VERSION_CONFIG = new ArrayList<>();

    /**
     * 模式组件配置信息
     */
    private static Map<String, ModeComponent> MODE_COMPONENT_CONFIG = new LinkedHashMap(4);

    /**
     * 应用配置信息
     */
    private static AppLabel APP_LABEL;

    /**
     * 应用配置信息
     */
    private static AppConfig APP_CONFIG;

    /**
     * 主窗口
     */
    private static JFrame MAIN_FRAME;

    /**
     * 系统设置窗口
     */
    private static JFrame SSETTING_APP_FRAME;

    /**
     * 监听类型 版本
     */
    private static final String LISTENER_VERSION = "version";

    /**
     * 监听类型 执行按钮
     */
    private static final String LISTENER_BUTTON_EXECUTE = "execute";

    /**
     * 监听类型 系统设置提交按钮
     */
    private static final String LISTENER_BUTTON_SETTING_APP_SUBMIT = "appSubmit";

    /**
     * 异常标识
     */
    private static boolean EXCEPTION_STATUS = false;

    /**
     * 当前日期
     */
    private static String CURRENT_DATE;

    /**
     * 选择模式
     */
    private static String SELECTED_MODE = null;

    /**
     * 选择版本
     */
    private static VersionInfo SELECTED_VERSION = null;

    /**
     * 选择模式组件
     */
    private static ModeComponent SELECTED_MODE_COMPONENT = new ModeComponent();

    /**
     * 系统设置组件
     */
    private static SettingAppComponent SETTING_APP_COMPONENT = new SettingAppComponent();

    /**
     * 工作目录前缀
     */
    private static String WORKSPACE = "";

    /**
     * 导出文件目录
     */
    private static String EXPORT_WORKSPACE = "";

    /**
     * 进度头部信息
     */
    private static String SCHEDULE_TITLE = "";

    /**
     * 合并文件内容
     */
    private static StringBuffer CONTENT = new StringBuffer();

    /**
     * 提示消息
     */
    private static StringBuffer MESSAGE = new StringBuffer();

    /**
     * 错误提示消息
     */
    private static StringBuffer FAIL_MESSAGE = new StringBuffer();

    /**
     * 读取文件路径数量
     */
    private static int READ_NUM = 0;

    /**
     * 复制文件数量
     */
    private static int COPY_NUM = 0;

    /**
     * 执行中
     */
    private static String EXECUTE_NUM = "--";

    /**
     * 升级脚本path
     */
    private static String FILE_NAME_PATH_UPDATE = "";

    /**
     * 更新文件目录path
     */
    private static String FILE_DIRECTORY_PATH_UPDATE = "";

    /**
     * 启动模式 1:Jar包启动 2:工程启动
     */
    private static String START_MODE = "jar";

    private final static String SVN_ERROR_CODE_E170001 = "E170001";
    private final static String SVN_ERROR_CODE_E175002 = "E175002";
    private final static String SVN_ERROR_CODE_E155004 = "E155004";
    private static Integer SVN_ERROR_TIMES = 0;

    private static final String TEXT_TEMPLATE = "  %s  ";


    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS);
        CURRENT_DATE = simpleDateFormat.format(new Date());
        // 设置启动模式
        initStartMode();
        // 读取应用配置
        Map afterAppConfig = CommonUtils.convertMap(getConfigProperties(APP_LABEL_PROPERTIES));
        APP_LABEL = (AppLabel) CommonUtils.mapToObject(afterAppConfig, AppLabel.class);

        afterAppConfig = CommonUtils.convertMap(getConfigProperties(APP_CONFIG_PROPERTIES));
        APP_CONFIG = (AppConfig) CommonUtils.mapToObject(afterAppConfig, AppConfig.class);
        // 读取版本配置
        getVersionConfig(getConfigProperties(APP_VERSION_PROPERTIES));
        // 初始化窗口
        initMainWindow();
        while (true) {
        }
    }

    /**
     * 初始化主窗口
     *
     * @param
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static void initMainWindow() {
        formatUi();
        JFrame mainFrame = new JFrame(APP_LABEL.getAppName());
        MAIN_FRAME = mainFrame;
        mainFrame.setLayout(new BorderLayout());
        // 初始化菜单
        initMenu();
        // 初始化模式
        initMode();
        // 设置窗口大小
        mainFrame.setSize(APP_LABEL.getAppWidth(), APP_LABEL.getAppHeight());
        // 初始化窗口位置
        initWindowLocation(mainFrame);
        // 绑定关闭事件
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 初始化子窗口
     *
     * @param frameName
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static void initSubWindow(String frameName) {
        formatUi();
        JFrame subFrame = new JFrame(frameName);
        subFrame.setLayout(new BorderLayout());
        if (frameName.equals(APP_LABEL.getMenuSettingApp())) {
            initSettingApp(subFrame);
        }
        // 设置窗口大小
        subFrame.setSize(APP_LABEL.getAppWidth() - 500, APP_LABEL.getAppHeight() - 150);
        // 初始化窗口位置
        initWindowLocation(subFrame);
        // 绑定关闭事件
        subFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // 禁止该表窗体大小
        subFrame.setResizable(false);
    }

    /**
     * 初始化窗口位置
     *
     * @param jFrame
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static void initWindowLocation(JFrame jFrame) {
        //设置窗口居中显示
        int windowWidth = jFrame.getWidth();
        int windowHeight = jFrame.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        jFrame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);
        // 设置可见
        jFrame.setVisible(true);
        // 设置窗口大小可变
        jFrame.setResizable(true);
    }

    /**
     * 初始化系统设置面板
     *
     * @param jFrame
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static void initSettingApp(JFrame jFrame) {
        SSETTING_APP_FRAME = jFrame;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox svnUpdate = (JComboBox) initComponentInfo(panel, APP_LABEL.getSvnUpdate(), initBooleanJComboBox(),
                String.valueOf(APP_CONFIG.getSvnUpdate()));
        JTextField svnUsername = (JTextField) initComponentInfo(panel, APP_LABEL.getSvnUsername(), new JTextField(),
                APP_CONFIG.getSvnUsername());
        JPasswordField svnPassword = (JPasswordField) initComponentInfo(panel, APP_LABEL.getSvnPassword(),
                new JPasswordField(), APP_CONFIG.getSvnPassword());
        JTextField svnFileName = (JTextField) initComponentInfo(panel, APP_LABEL.getSvnFileName(), new JTextField(),
                APP_CONFIG.getSvnFileName());
        JTextField fileSuffix = (JTextField) initComponentInfo(panel, APP_LABEL.getFileSuffix(), new JTextField(),
                APP_CONFIG.getFileSuffix());
        JComboBox updateDeleteSuccess = (JComboBox) initComponentInfo(panel, APP_LABEL.getUpdateDeleteSuccess(),
                initBooleanJComboBox(), String.valueOf(APP_CONFIG.getUpdateDeleteSuccess()));
        JTextField lineContentOffset = (JTextField) initComponentInfo(panel, APP_LABEL.getLineContentOffset(),
                new JTextField(), APP_CONFIG.getLineContentOffset());
        JTextField mergerFileName = (JTextField) initComponentInfo(panel, APP_LABEL.getMergerFileName(),
                new JTextField(),
                APP_CONFIG.getMergerFileName());
        JComboBox fileEncoding = (JComboBox) initComponentInfo(panel, APP_LABEL.getFileEncoding(), initEncodingJComboBox(),
                APP_CONFIG.getFileEncoding());
        JButton submit = new JButton(formatText(APP_LABEL.getButtonSubmit()));
        submit.setPreferredSize(new Dimension(385, 25));
        // 设置监听事件
        addActionListener(submit, LISTENER_BUTTON_SETTING_APP_SUBMIT);
        panel.add(submit);
        jFrame.add(panel);

        SETTING_APP_COMPONENT.setSvnUpdate(svnUpdate);
        SETTING_APP_COMPONENT.setSvnUsername(svnUsername);
        SETTING_APP_COMPONENT.setSvnPassword(svnPassword);
        SETTING_APP_COMPONENT.setSvnFileName(svnFileName);
        SETTING_APP_COMPONENT.setFileSuffix(fileSuffix);
        SETTING_APP_COMPONENT.setUpdateDeleteSuccess(updateDeleteSuccess);
        SETTING_APP_COMPONENT.setLineContentOffset(lineContentOffset);
        SETTING_APP_COMPONENT.setMergerFileName(mergerFileName);
        SETTING_APP_COMPONENT.setFileEncoding(fileEncoding);
    }

    /**
     * 初始化菜单
     *
     * @author: humm23693
     * @date: 2020/11/27
     * @return:
     */
    private static void initMenu() {
        // 创建菜单栏
        JMenuBar menu = new JMenuBar();
        JMenu setting = new JMenu(APP_LABEL.getMenuSetting());
        JMenu help = new JMenu(APP_LABEL.getMenuHelp());
        menu.add(setting);
        menu.add(help);
        MAIN_FRAME.setJMenuBar(menu);

        // 设置子菜单
        JMenuItem config = new JMenuItem(APP_LABEL.getMenuSettingVersion());
        JMenuItem version = new JMenuItem(APP_LABEL.getMenuSettingApp());
        setting.add(config);
        setting.add(version);

        JMenuItem app = new JMenuItem(APP_LABEL.getMenuHelpApp());
        JMenuItem author = new JMenuItem(APP_LABEL.getMenuHelpAuthor());
        help.add(app);
        help.add(author);

        // 设置监听事件
        addActionListener(config, null);
        addActionListener(version, null);
        addActionListener(app, null);
        addActionListener(author, null);
    }

    /**
     * 初始化选项卡
     *
     * @author: humm23693
     * @date: 2020/11/27
     * @return:
     */
    private static void initMode() {
        // 创建选项卡面板
        JTabbedPane mode = new JTabbedPane();

        // 初始化复制面板
        mode.addTab(formatText(APP_LABEL.getModeCopy()), createPanel(APP_LABEL.getModeCopy()));
        // 初始化更新面板
        mode.addTab(formatText(APP_LABEL.getModeUpdate()), createPanel(APP_LABEL.getModeUpdate()));
        // 初始化同步面板
        mode.addTab(formatText(APP_LABEL.getModeSync()), createPanel(APP_LABEL.getModeSync()));
        // 初始化覆盖面板
        mode.addTab(formatText(APP_LABEL.getModeCover()), createPanel(APP_LABEL.getModeCover()));
        // 初始化合并面板
        mode.addTab(formatText(APP_LABEL.getModeMerger()), createPanel(APP_LABEL.getModeMerger()));

        MAIN_FRAME.setContentPane(mode);

        // 添加选项卡选中状态改变的监听器
        addActionListener(mode, null);

        // 设置默认选中的选项卡
        mode.setSelectedIndex(0);

    }

    /**
     * 初始化选择组件
     *
     * @param items
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static JComboBox initJComboBox(SelectItem... items) {
        JComboBox<SelectItem> jComboBox = new JComboBox();
        for (SelectItem item : items) {
            jComboBox.addItem(item);
        }
        return jComboBox;
    }

    /**
     * 初始化是否选择框
     *
     * @param
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static JComboBox initBooleanJComboBox() {
        return initJComboBox(new SelectItem("false", "否"), new SelectItem("true", "是"));
    }

    /**
     * 初始化是否选择框
     *
     * @param
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static JComboBox initEncodingJComboBox() {
        return initJComboBox(new SelectItem("GBK", "GBK"), new SelectItem("UFT-8", "UFT-8"));
    }

    /**
     * 初始化组件
     *
     * @param labelName
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static JComponent initComponentInfo(JPanel panel, String labelName, JComponent jComponent, String value) {
        JLabel jLabel = new JLabel(formatText(labelName));
        initComponent(jLabel, 200, 20);
        panel.add(jLabel);
        initComponent(jComponent, 170, 25);
        panel.add(jComponent);
        if (value != null) {
            if (jComponent instanceof JTextField) {
                ((JTextField) jComponent).setText(value);
            } else if (jComponent instanceof JPasswordField) {
                ((JPasswordField) jComponent).setText(value);
            } else if (jComponent instanceof JComboBox) {
                if (ENCODING_GBK.equals(value)) {
                    ((JComboBox) jComponent).setSelectedIndex(0);
                } else if (ENCODING_UTF8.equals(value)) {
                    ((JComboBox) jComponent).setSelectedIndex(1);
                } else if (Boolean.parseBoolean(value)) {
                    ((JComboBox) jComponent).setSelectedIndex(1);
                } else {
                    ((JComboBox) jComponent).setSelectedIndex(0);
                }
            }
        }
        return jComponent;
    }

    /**
     * 设置组件长宽
     *
     * @param jComponent
     * @param width
     * @param height
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static void initComponent(JComponent jComponent, int width, int height) {
        jComponent.setPreferredSize(new Dimension(width, height));
    }

    /**
     * 创建TAB面板
     *
     * @param mode
     * @author: humm23693
     * @date: 2020/11/27
     * @return:
     */
    private static JComponent createPanel(String mode) {
        JPanel tab = new JPanel(new BorderLayout(0, 0));
        // 设置版本号
        JPanel version = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 设置路径 及 进度信息
        JPanel workspaceSchedule = new JPanel(new BorderLayout(0, 0));
        JPanel workspaceBox = new JPanel(new BorderLayout(0, 0));

        // 源头工作路径
        JPanel sourceWorkspace = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sourceLabel = new JLabel(formatText(APP_LABEL.getWorkspaceSource()));
        JTextField sourcePath = new JTextField();
        sourceWorkspace.add(sourceLabel);
        sourceWorkspace.add(sourcePath);

        // 目标工作路径
        JPanel targetWorkspace = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel targetLabel = new JLabel(formatText(APP_LABEL.getWorkspaceTarget()));
        JTextField targetPath = new JTextField();
        targetWorkspace.add(targetLabel);
        targetWorkspace.add(targetPath);

        // 工作路径
        JPanel workspace = new JPanel(new BorderLayout(0, 0));
        if (!mode.equals(APP_LABEL.getModeMerger())) {
            workspace.add(sourceWorkspace, BorderLayout.NORTH);
        }
        workspace.add(targetWorkspace, BorderLayout.CENTER);

        // 设置区域滚动
        JScrollPane workspaceScrollPane = new JScrollPane(workspace);
        if (mode.equals(APP_LABEL.getModeMerger())) {
            workspaceScrollPane.setPreferredSize(new Dimension(0, 55));
        } else {
            workspaceScrollPane.setPreferredSize(new Dimension(0, 90));
        }

        // 工作路径 执行按钮组合
        JButton execute = new JButton(APP_LABEL.getButtonExecute());
        formatButtonUi(execute);
        if (!mode.equals(APP_LABEL.getModeSync())) {
            workspaceBox.add(workspaceScrollPane, BorderLayout.CENTER);
            workspaceBox.add(execute, BorderLayout.EAST);
        } else {
            workspaceBox.add(execute, BorderLayout.NORTH);
        }

        // 设置监听事件
        addActionListener(execute, LISTENER_BUTTON_EXECUTE);
        workspaceSchedule.add(workspaceBox, BorderLayout.NORTH);

        // 设置进度信息
        TextArea schedule = new TextArea();
        workspaceSchedule.add(schedule, BorderLayout.CENTER);

        // 状态信息
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel resultLabel = new JLabel(APP_LABEL.getStatusResult());
        JLabel result = new JLabel();
        status.add(resultLabel);
        status.add(result);

        JLabel readNum = new JLabel();
        addBlank(status, new JLabel(APP_LABEL.getStatusRead()), readNum);
        status.add(readNum);

        JLabel successNum = new JLabel();
        addBlank(status, new JLabel(APP_LABEL.getStatusSuccess()), successNum);
        status.add(successNum);

        JLabel failNum = new JLabel();
        addBlank(status, new JLabel(APP_LABEL.getStatusFail()), failNum);
        status.add(failNum);

        if (!mode.equals(APP_LABEL.getModeSync())) {
            // 设置区域滚动
            JScrollPane versionScrollPane = new JScrollPane(version);
            versionScrollPane.setPreferredSize(new Dimension(0, 55));
            // 组件组合
            tab.add(versionScrollPane, BorderLayout.NORTH);
        }
        tab.add(workspaceSchedule, BorderLayout.CENTER);
        tab.add(status, BorderLayout.SOUTH);

        ModeComponent modeComponent = new ModeComponent();
        modeComponent.setMode(mode);
        modeComponent.setVersion(version);
        modeComponent.setSourcePath(sourcePath);
        modeComponent.setTargetPath(targetPath);
        modeComponent.setSchedule(schedule);
        modeComponent.setResult(result);
        modeComponent.setReadNum(readNum);
        modeComponent.setSuccessNum(successNum);
        modeComponent.setFailNum(failNum);
        MODE_COMPONENT_CONFIG.put(mode, modeComponent);

        // 设置默认模式版本信息
        if (mode.equals(APP_LABEL.getModeCopy())) {
            SELECTED_MODE = APP_LABEL.getModeCopy();
            SELECTED_MODE_COMPONENT = modeComponent;
            initVersion();
        }
        initWorkspaceBlank(modeComponent);
        return tab;
    }

    /**
     * 监听事件
     *
     * @param action
     * @param actionType
     * @author: humm23693
     * @date: 2020/11/27
     * @return:
     */
    private static void addActionListener(Object action, String actionType) {
        if (action instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) action;
            menuItem.addActionListener(e -> {
                String menuName = menuItem.getActionCommand();
                logger.info(menuName);
                if (APP_LABEL.getMenuSettingApp().equals(menuName)) {
                    initSubWindow(menuName);
                }
            });
        } else if (action instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) action;
            tabbedPane.addChangeListener(e -> {
                int index = tabbedPane.getSelectedIndex();
                SELECTED_MODE = tabbedPane.getTitleAt(index).trim();
                SELECTED_MODE_COMPONENT = MODE_COMPONENT_CONFIG.get(SELECTED_MODE);
                initWorkspaceBlank(null);
                SELECTED_VERSION = null;
                logger.info("选择模式 {}:{}", index, SELECTED_MODE);
                String content = String.format("当前模式【%s】 \n", SELECTED_MODE);
                initSchedule(content, null, false);
                initVersion();
            });
        } else if (action instanceof JButton) {
            JButton button = (JButton) action;
            button.addActionListener(e -> {
                if (LISTENER_VERSION.equals(actionType)) {
                    logger.info("选择版本 {}", button.getActionCommand());
                    initWorkspace(button.getActionCommand());
                } else if (LISTENER_BUTTON_EXECUTE.equals(actionType)) {
                    logger.info(button.getActionCommand());
                    executeFile();
                } else if (LISTENER_BUTTON_SETTING_APP_SUBMIT.equals(actionType)) {
                    logger.info(button.getActionCommand());
                    saveSettingApp();
                } else {
                    logger.info(button.getActionCommand());
                    initSchedule("暂不能处理该事件", Color.RED, false);
                }
                formatButtonUi(button);
            });
        }
    }

    /**
     * 保存系统设置信息
     *
     * @param
     * @author: hspcadmin
     * @date: 2020/12/18
     * @return:
     */
    private static void saveSettingApp() {
        SettingApp settingApp = new SettingApp();
        settingApp.setSvnUpdate(((SelectItem) SETTING_APP_COMPONENT.getSvnUpdate().getSelectedItem()).getCode());
        settingApp.setSvnUsername(SETTING_APP_COMPONENT.getSvnUsername().getText().trim());
        settingApp.setSvnPassword(SETTING_APP_COMPONENT.getSvnPassword().getText().trim());
        settingApp.setSvnFileName(SETTING_APP_COMPONENT.getSvnFileName().getText().trim());
        settingApp.setFileSuffix(SETTING_APP_COMPONENT.getFileSuffix().getText().trim());
        settingApp.setUpdateDeleteSuccess(((SelectItem) SETTING_APP_COMPONENT.getUpdateDeleteSuccess().getSelectedItem()).getCode());
        settingApp.setLineContentOffset(SETTING_APP_COMPONENT.getLineContentOffset().getText().trim());
        settingApp.setMergerFileName(SETTING_APP_COMPONENT.getMergerFileName().getText().trim());
        settingApp.setFileEncoding(((SelectItem) SETTING_APP_COMPONENT.getFileEncoding().getSelectedItem()).getCode());
        logger.info(settingApp.toString());
        Properties properties = new Properties();
        try {
            properties.load(getInputStreamReader(APP_CONFIG_PROPERTIES, true));
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        }
    }

    /**
     * 文件操作
     *
     * @param
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void executeFile() {
        if (!SELECTED_MODE.equals(APP_LABEL.getModeSync()) && SELECTED_VERSION == null) {
            initSchedule(APP_LABEL.getDialogExecuteContent(), Color.RED, false);
//            JOptionPane.showMessageDialog(MAIN_FRAME, APP_LABEL.getDialogExecuteContent(), APP_LABEL.getDialogTitle(), JOptionPane.WARNING_MESSAGE);
            return;
        }
        SCHEDULE_TITLE = String.format("当前模式【%s】\n", SELECTED_MODE);
        initSchedule(SCHEDULE_TITLE, null, false);
        cleanHistory();
        try {
            if (SELECTED_MODE.equals(APP_LABEL.getModeCopy())) {
                // 复制文件
                new Thread(() -> {
                    try {
                        copyFile();
                    } catch (Exception e) {
                        initStatus(APP_LABEL.getStatusResultFail(), SYMBOL_EMPTY, Color.RED);
                        initSchedule(e.getMessage(), Color.RED, false);
                    }
                }).start();
            } else if (SELECTED_MODE.equals(APP_LABEL.getModeUpdate())) {
                // 更新文件
                new Thread(() -> {
                    try {
                        updateFile();
                    } catch (Exception e) {
                        initStatus(APP_LABEL.getStatusResultFail(), SYMBOL_EMPTY, Color.RED);
                        initSchedule(e.getMessage(), Color.RED, false);
                    }
                }).start();
            } else if (SELECTED_MODE.equals(APP_LABEL.getModeSync())) {
                // svn更新
                new Thread(() -> {
                    try {
                        syncSvn();
                    } catch (Exception e) {
                        initStatus(APP_LABEL.getStatusResultFail(), SYMBOL_EMPTY, Color.RED);
                        initSchedule(e.getMessage(), Color.RED, false);
                    }
                }).start();
            } else if (SELECTED_MODE.equals(APP_LABEL.getModeCover())) {
                // 覆盖文件
                new Thread(() -> {
                    try {
                        coverFile();
                    } catch (Exception e) {
                        initStatus(APP_LABEL.getStatusResultFail(), SYMBOL_EMPTY, Color.RED);
                        initSchedule(e.getMessage(), Color.RED, false);
                    }
                }).start();
            } else if (SELECTED_MODE.equals(APP_LABEL.getModeMerger())) {
                // 合并文件
                new Thread(() -> {
                    try {
                        mergerFile();
                    } catch (Exception e) {
                        initStatus(APP_LABEL.getStatusResultFail(), SYMBOL_EMPTY, Color.RED);
                        initSchedule(e.getMessage(), Color.RED, false);
                    }
                }).start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MAIN_FRAME, e.getMessage(), APP_LABEL.getDialogTitle(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 清除上次操作信息
     *
     * @param
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void cleanHistory() {
        FILE_DIRECTORY_PATH_UPDATE = SYMBOL_EMPTY;
        FILE_NAME_PATH_UPDATE = SYMBOL_EMPTY;
        MESSAGE = new StringBuffer();
        FAIL_MESSAGE = new StringBuffer();
        CONTENT = new StringBuffer();
        EXCEPTION_STATUS = false;
        READ_NUM = 0;
        COPY_NUM = 0;
        initStatus(APP_LABEL.getStatusResultExecute(), EXECUTE_NUM, Color.DARK_GRAY);
    }

    /**
     * 合并文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void mergerFile() {
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = getBufferedReader(FILE_PATH, true);
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    READ_NUM++;
                    String[] subInputPath = CommonUtils.convertBackslash(inputPath.trim()).split(SYMBOL_BLANK_SPACE);
                    String path = subInputPath[subInputPath.length - 1].trim();
                    initSchedule(String.format("合并文件【%s】\n", path), null, true);
                    CONTENT.append(getFileContent(path));
                    if (EXCEPTION_STATUS) {
                        break;
                    }
                }
            }
            savePathStatus(APP_LABEL.getModeMerger(), createFile(CONTENT.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建合并文件
     *
     * @param content
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static String createFile(String content) {
        String contentFolderPath = EXPORT_WORKSPACE + CURRENT_DATE;
        String fileName = APP_CONFIG.getMergerFileName() + SYMBOL_MINUS + READ_NUM;
        String statusFilename = contentFolderPath + SYMBOL_BACKSLASH_1 + fileName + APP_CONFIG.getFileSuffix();
        File contentFolder = new File(contentFolderPath);
        if (!contentFolder.exists()) {
            contentFolder.mkdirs();
        }
        File file = new File(statusFilename);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), APP_CONFIG.getFileEncoding())));
            out.write(content);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            out.close();
        }
        return contentFolderPath;
    }

    /**
     * 覆盖文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void coverFile() {
        File fileDirectory = new File(WORKSPACE);
        if (!fileDirectory.isDirectory()) {
            throw new RuntimeException(String.format("源文件工作目录【%s】不存在", WORKSPACE));
        }
        File[] fileDirectoryList = fileDirectory.listFiles();
        if (fileDirectoryList != null) {
            CommonUtils.sortFile(fileDirectoryList);
            File coverFileDirectory = fileDirectoryList[0];
            if (!coverFileDirectory.isDirectory()) {
                throw new RuntimeException(String.format("【%s】不是文件夹", coverFileDirectory.getAbsolutePath()));
            }
            File[] coverFileList = coverFileDirectory.listFiles();
            for (File coverFile : coverFileList) {
                coverMultipleFile(coverFile, CommonUtils.convertBackslash(coverFileDirectory.getAbsolutePath()));
            }
            savePathStatus(APP_LABEL.getModeCover(), coverFileDirectory.getAbsolutePath());
        }
    }

    /**
     * 覆盖文件夹下文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void coverMultipleFile(File file, String fileDirectory) {
        if (!file.isDirectory()) {
            coverSingleFile(file, fileDirectory);
        } else {
            File[] fileList = file.listFiles();
            for (File single : fileList) {
                if (single.isDirectory()) {
                    coverMultipleFile(single, fileDirectory);
                } else {
                    coverSingleFile(single, fileDirectory);
                }
            }
        }
    }

    /**
     * 覆盖单个文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void coverSingleFile(File file, String fileDirectory) {
        if ((APP_LABEL.getStatusResultSuccess() + APP_CONFIG.getFileSuffix()).equals(file.getName())
                || (APP_LABEL.getStatusResultFail() + APP_CONFIG.getFileSuffix()).equals(file.getName())) {
            return;
        }
        String inputPath = CommonUtils.convertBackslash(file.getAbsolutePath());
        READ_NUM++;
        String exportPath = inputPath.replace(fileDirectory, EXPORT_WORKSPACE);
        initSchedule(String.format("覆盖文件【%s】\n", inputPath), null, true);
        copySingleFile(inputPath, exportPath);
    }

    /**
     * svn更新
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/09/19
     * @return:
     */
    private static void syncSvn() {
        Map updateMap = new HashMap(16);
        if (VERSION_CONFIG != null) {
            for (VersionInfo version : VERSION_CONFIG) {
                if (updateMap.containsKey(version.getTargetPath())) {
                    continue;
                } else {
                    updateMap.put(version.getTargetPath(), version.getTargetPath());
                }
                if (!updateSvn(version.getTargetPath())) {
                    EXCEPTION_STATUS = true;
                    throw new RuntimeException("svn同步异常\n");
                }
            }
        }
        initSchedule("\nsvn同步完成\n", null, true);
        initStatus(APP_LABEL.getStatusResultSuccess(), SYMBOL_EMPTY, Color.BLUE);
    }

    /**
     * svn更新
     *
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/12
     * @return:
     */
    private static boolean updateSvn(String workspace) {
        initSchedule(String.format("同步工作目录【%s】\n", workspace), null, true);
        if (APP_CONFIG.getSvnUpdate() && CommonUtils.isSuffixDirectory(new File(workspace), APP_CONFIG.getSvnFileName())) {
            initSchedule("svn同步执行中...", null, true);
            APP_CONFIG.setSvnVersion(-1L);
            executing();
            Long svnVersion = update(APP_CONFIG.getSvnUsername(), APP_CONFIG.getSvnPassword(), workspace);
            APP_CONFIG.setSvnVersion(svnVersion);
            while (true) {
                CommonUtils.sleep(1);
                if (APP_CONFIG.getSvnVersion() > svnVersion) {
                    APP_CONFIG.setSvnVersion(svnVersion - 1);
                    break;
                }
            }
            return APP_CONFIG.getSvnVersion() > 0;
        }
        initSchedule("非svn目录 无需同步\n", null, true);
        return true;
    }

    /**
     * 执行中标识
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/09/19
     * @return:
     */
    private static void executing() {
        new Thread(() -> {
            while (true) {
                CommonUtils.sleep(1);
                if (APP_CONFIG.getSvnVersion() == -1L) {
                    initSchedule(SYMBOL_POINT_3, null, true);
                } else {
                    initSchedule(SYMBOL_POINT_3 + SYMBOL_NEXT_LINE, null, true);
                    if (APP_CONFIG.getSvnVersion() > 0) {
                        initSchedule(String.format("svn已同步至版本【%s】\n", APP_CONFIG.getSvnVersion()), null, true);
                    }
                    APP_CONFIG.setSvnVersion(APP_CONFIG.getSvnVersion() + 1);
                    break;
                }
            }
        }).start();
    }

    /**
     * 文件更新
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateFile() {
        File fileDirectory = new File(WORKSPACE);
        if (!fileDirectory.isDirectory()) {
            throw new RuntimeException(String.format("源文件工作目录【%s】不存在", WORKSPACE));
        }
        File[] fileList = fileDirectory.listFiles();
        if (fileList != null) {
            // 解压文件
            for (File file : fileList) {
                if (file.isDirectory()) {
                    continue;
                }
                String fileName = file.getName();
                try {
                    if (fileName.endsWith(FILE_TYPE_ZIP)) {
                        UnZipAnRarUtils.unZip(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH_1);
                    } else if (fileName.endsWith(FILE_TYPE_RAR)) {
                        UnZipAnRarUtils.unRar(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH_1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    EXCEPTION_STATUS = true;
                }
            }
            // 更新文件
            fileList = fileDirectory.listFiles();
            for (File file : fileList) {
                String fileName = file.getName();
                if (fileName.endsWith(FILE_TYPE_ZIP) || fileName.endsWith(FILE_TYPE_RAR)) {
                    continue;
                }
                if ((APP_LABEL.getStatusResultSuccess() + APP_CONFIG.getFileSuffix()).equals(file.getName())
                        || (APP_LABEL.getStatusResultFail() + APP_CONFIG.getFileSuffix()).equals(file.getName())) {
                    continue;
                }
                if (file.isDirectory()) {
                    // 文件夹
                    File target = new File(EXPORT_WORKSPACE);
                    Map<String, String> targetDirectory = new HashMap<>(10);
                    if (target != null) {
                        File[] targetList = target.listFiles();
                        for (File targetFile : targetList) {
                            targetDirectory.put(targetFile.getName(), targetFile.getName());
                        }
                    }
                    getWorkspaceAbsolutely(file.getAbsolutePath(), targetDirectory);
                    if (StringUtils.isBlank(FILE_DIRECTORY_PATH_UPDATE)) {
                        throw new RuntimeException(String.format("【%s】更新文件目录不匹配", file.getAbsolutePath()));
                    }
                    updateMultipleFile(file, FILE_DIRECTORY_PATH_UPDATE);
                } else {
                    // 单个文件,升级脚本,增量更新
                    File updateFile = new File(EXPORT_WORKSPACE + APP_LABEL.getScriptUpdateDictionary());
                    if (!updateFile.exists()) {
                        updateFile = new File(EXPORT_WORKSPACE);
                    }
                    getUpdateScriptPath(file.getName(), updateFile.getAbsolutePath());
                    if (StringUtils.isBlank(FILE_NAME_PATH_UPDATE)) {
                        int index = file.getName().lastIndexOf(SYMBOL_POINT_1);
                        String suffix = fileName.substring(index);
                        fileName = fileName.replace(APP_LABEL.getScriptUpdateName() + suffix, suffix);
                        getUpdateScriptPath(fileName, updateFile.getAbsolutePath());
                        if (StringUtils.isBlank(FILE_NAME_PATH_UPDATE)) {
                            throw new RuntimeException(String.format("【%s】更新文件目录不匹配", file.getAbsolutePath()));
                        }
                    }
                    READ_NUM++;
                    initSchedule(String.format("更新文件【%s】\n", FILE_NAME_PATH_UPDATE), null, true);
                    updateScriptFile(file.getAbsolutePath(), FILE_NAME_PATH_UPDATE);
                }
            }
            if (APP_CONFIG.getUpdateDeleteSuccess() && READ_NUM == COPY_NUM && !EXCEPTION_STATUS) {
                for (File file : fileList) {
                    CommonUtils.deleteFile(file);
                }
            }
        }
        savePathStatus(APP_LABEL.getModeUpdate(), WORKSPACE);
    }

    /**
     * 更新升级脚本
     *
     * @param sourcePath
     * @param targetPath
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateScriptFile(String sourcePath, String targetPath) {
        String fileContent = SYMBOL_NEXT_LINE + getFileContent(sourcePath);
        List<String> sourceLines = new ArrayList<>();
        COPY_NUM--;
        try {
            List<String> lines = Files.readAllLines(Paths.get(targetPath), Charset.forName(APP_CONFIG.getFileEncoding()));
            sourceLines.addAll(lines);
            int position = 0;
            if (StringUtils.isBlank(APP_CONFIG.getLineContent())) {
                lines.add(fileContent);
            } else {
                String[] line = APP_CONFIG.getLineSpecialContentOffset(SELECTED_VERSION.getVersionName(), targetPath);
                String lineContent = line[0];
                int lineOffset = Integer.valueOf(line[1]);
                if (CollectionUtils.isNotEmpty(lines)) {
                    for (int i = 0; i < lines.size(); i++) {
                        String content = lines.get(i);
                        if (lineContent.equals(content.trim())) {
                            position = i + lineOffset;
                        }
                    }
                }
                lines.add(position, fileContent);
            }
            Files.write(Paths.get(targetPath), lines, Charset.forName(APP_CONFIG.getFileEncoding()));
            COPY_NUM++;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
            // 内容还原
            try {
                Files.write(Paths.get(targetPath), sourceLines, Charset.forName(APP_CONFIG.getFileEncoding()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取单个文件内容
     *
     * @param fileName
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static String getFileContent(String fileName) {
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer msg = new StringBuffer();
        try {
            File file = new File(fileName);
            if (file.exists()) {
                reader = getBufferedReader(fileName, false);
                String content;
                while ((content = reader.readLine()) != null) {
                    stringBuffer.append(content).append(SYMBOL_NEXT_LINE);
                }
                initSchedule(String.format("%s【%s】\n", APP_LABEL.getScheduleStatusSuccess(), fileName), null, true);
                MESSAGE.append(msg);
                COPY_NUM++;
            } else {
                msg.append(APP_LABEL.getScheduleStatusFail()).append(String.format(" 文件不存在【%s】\n", fileName));
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
                return SYMBOL_NEXT_LINE;
            }
        } catch (IOException e) {
            e.printStackTrace();
            msg.append(APP_LABEL.getScheduleStatusFail()).append(String.format(" %s【%s】\n", e.getMessage(), fileName));
            MESSAGE.append(msg);
            FAIL_MESSAGE.append(msg);
            EXCEPTION_STATUS = true;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stringBuffer.append(SYMBOL_NEXT_LINE);
        return stringBuffer.toString();
    }

    /**
     * 更新文件夹下文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateMultipleFile(File file, String fileDirectory) {
        if (!file.isDirectory()) {
            updateSingleFile(file, fileDirectory);
        } else {
            File[] fileList = file.listFiles();
            for (File single : fileList) {
                if (single.isDirectory()) {
                    updateMultipleFile(single, fileDirectory);
                } else {
                    updateSingleFile(single, fileDirectory);
                }
            }
        }

    }

    /**
     * 更新单个文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateSingleFile(File file, String fileDirectory) {
        READ_NUM++;
        String inputPath = CommonUtils.convertBackslash(file.getAbsolutePath());
        String exportPath = inputPath.replace(fileDirectory, EXPORT_WORKSPACE);
        copySingleFile(inputPath, exportPath);
        initSchedule(String.format("更新文件【%s】\n", inputPath), null, true);
    }

    /**
     * 获取替换文件绝对路径
     *
     * @param filePath
     * @param targetDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static boolean getWorkspaceAbsolutely(String filePath, Map<String, String> targetDirectory) {
        File source = new File(filePath);
        if (source != null) {
            if (source.isDirectory()) {
                String fileName = source.getName();
                if (!StringUtils.isNotBlank(targetDirectory.get(fileName))) {
                    File[] subSourceList = source.listFiles();
                    for (File subSource : subSourceList) {
                        if (getWorkspaceAbsolutely(subSource.getAbsolutePath(), targetDirectory)) {
                            break;
                        }
                    }
                } else {
                    FILE_DIRECTORY_PATH_UPDATE = CommonUtils.convertBackslash(source.getParentFile().getAbsolutePath());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取升级脚本绝对路径
     *
     * @param fileName
     * @param filePath
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static boolean getUpdateScriptPath(String fileName, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (File singleFile : fileList) {
                if (getUpdateScriptPath(fileName, singleFile.getAbsolutePath())) {
                    break;
                }
            }
        } else {
            if (fileName.equals(file.getName())) {
                FILE_NAME_PATH_UPDATE = CommonUtils.convertBackslash(file.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void copyFile() {
        BufferedReader bufferedReader = null;
        String exportWorkspace = EXPORT_WORKSPACE + CURRENT_DATE + SYMBOL_BACKSLASH_1;
        if (STR_0.equals(SELECTED_VERSION.getStatusFile())) {
            exportWorkspace = EXPORT_WORKSPACE;
        }
        try {
            String inputPath;
            bufferedReader = getBufferedReader(FILE_PATH, true);
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    String[] subInputPath = CommonUtils.convertBackslash(inputPath.trim()).split(SYMBOL_BLANK_SPACE);
                    String sourcePath = subInputPath[subInputPath.length - 1].trim();
                    if (!sourcePath.isEmpty()) {
                        READ_NUM++;
                        initSchedule(String.format("复制文件【%s】\n", sourcePath), null, true);
                        String exportPath = sourcePath.replace(WORKSPACE, exportWorkspace);
                        copySingleFile(sourcePath, exportPath);
                        if (EXCEPTION_STATUS) {
                            break;
                        }
                    }
                }
            }
            savePathStatus(APP_LABEL.getModeCopy(), exportWorkspace);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param sourcePath
     * @param exportPath
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void copySingleFile(String sourcePath, String exportPath) {
        StringBuffer msg = new StringBuffer();
        if (sourcePath.equals(exportPath)) {
            if (sourcePath.startsWith(WORKSPACE)) {
                // 自我复制
                COPY_NUM++;
                initSchedule(String.format("%s【%s】\n", APP_LABEL.getScheduleStatusSuccess(), sourcePath), null, true);
                msg.append(APP_LABEL.getScheduleStatusSuccess()).append(String.format("【%s】\n", sourcePath));
                MESSAGE.append(msg);
            } else {
                // 源文件与版本不匹配
                msg.append(APP_LABEL.getScheduleStatusFail()).append(String.format(" 源文件与版本不匹配【%s】\n", sourcePath));
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
            }
            return;
        }
        int lastIndex = exportPath.lastIndexOf(SYMBOL_SLASH);
        String path = exportPath.substring(0, lastIndex);
        File inFile = new File(sourcePath);
        File outFile = new File(exportPath);
        if (inFile.exists() && inFile.isFile()) {
            File sourceFolder = new File(path);
            if (!sourceFolder.exists()) {
                sourceFolder.mkdirs();
            }
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                byte[] cache = new byte[1024];
                int length;
                fileInputStream = new FileInputStream(inFile);
                fileOutputStream = new FileOutputStream(outFile);
                while ((length = fileInputStream.read(cache)) != -1) {
                    fileOutputStream.write(cache, 0, length);
                }
                COPY_NUM++;
                initSchedule(String.format("%s【%s】\n", APP_LABEL.getScheduleStatusSuccess(), sourcePath), null, true);
                msg.append(APP_LABEL.getScheduleStatusSuccess()).append(String.format("【%s】\n", sourcePath));
                MESSAGE.append(msg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                msg.append(APP_LABEL.getScheduleStatusFail()).append(String.format(" %s【%s】\n", e.getMessage(), sourcePath));
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
            } catch (IOException e) {
                e.printStackTrace();
                msg.append(APP_LABEL.getScheduleStatusFail()).append(String.format(" %s【%s】\n", e.getMessage(), sourcePath));
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
            } finally {
                try {
                    fileInputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            msg.append(APP_LABEL.getScheduleStatusFail()).append(String.format(" 文件不存在【%s】\n", sourcePath));
            MESSAGE.append(msg);
            FAIL_MESSAGE.append(msg);
            EXCEPTION_STATUS = true;
        }
    }

    /**
     * 设置结果状态
     *
     * @param statusType
     * @param directory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void savePathStatus(String statusType, String directory) {
        String fileName = APP_LABEL.getStatusResultSuccess();
        if (READ_NUM != COPY_NUM || EXCEPTION_STATUS) {
            fileName = APP_LABEL.getStatusResultFail();
        }
        if (STR_1.equals(SELECTED_VERSION.getStatusFile())) {
            File statusFolder = new File(directory);
            if (!statusFolder.exists()) {
                statusFolder.mkdirs();
            }
            String statusFilename = directory + SYMBOL_BACKSLASH_1 + fileName + APP_CONFIG.getFileSuffix();
            File file = new File(statusFilename);
            PrintStream printStream = null;
            try {
                printStream = new PrintStream(new FileOutputStream(file));
                printStream.println(MESSAGE.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                EXCEPTION_STATUS = true;
            } finally {
                printStream.close();
            }
        }
        if (APP_LABEL.getStatusResultSuccess().equals(fileName)) {
            initStatus(APP_LABEL.getStatusResultSuccess(), Color.BLUE);
            String content = String.format("\n%s%s 读取文件数量【%s】 成功文件数量【%s】 失败文件数量【%s】\n", APP_LABEL.getButtonExecute(),
                    APP_LABEL.getStatusResultSuccess(), READ_NUM, COPY_NUM, READ_NUM - COPY_NUM);
            initSchedule(content, null, true);
        } else {
            initStatus(APP_LABEL.getStatusResultFail(), Color.RED);
            initSchedule(FAIL_MESSAGE.toString(), Color.RED, false);
            if (APP_LABEL.getModeCopy().equals(statusType) || APP_LABEL.getModeMerger().equals(statusType)) {
                initSchedule(String.format("请检查【%s】文件中路径是否存在", FILE_PATH), Color.RED, true);
            }
        }
    }

    /**
     * 初始化状态栏信息
     *
     * @param result
     * @param color
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void initStatus(String result, Color color) {
        SELECTED_MODE_COMPONENT.getResult().setForeground(color);
        SELECTED_MODE_COMPONENT.getReadNum().setForeground(color);
        SELECTED_MODE_COMPONENT.getSuccessNum().setForeground(color);
        SELECTED_MODE_COMPONENT.getFailNum().setForeground(color);
        SELECTED_MODE_COMPONENT.getResult().setText(result);
        SELECTED_MODE_COMPONENT.getReadNum().setText(String.valueOf(READ_NUM));
        SELECTED_MODE_COMPONENT.getSuccessNum().setText(String.valueOf(COPY_NUM));
        SELECTED_MODE_COMPONENT.getFailNum().setText(String.valueOf(READ_NUM - COPY_NUM));
    }

    /**
     * 初始化状态栏信息
     *
     * @param result
     * @param num
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void initStatus(String result, String num, Color color) {
        SELECTED_MODE_COMPONENT.getResult().setForeground(color);
        SELECTED_MODE_COMPONENT.getReadNum().setForeground(color);
        SELECTED_MODE_COMPONENT.getSuccessNum().setForeground(color);
        SELECTED_MODE_COMPONENT.getFailNum().setForeground(color);

        SELECTED_MODE_COMPONENT.getResult().setText(result);
        SELECTED_MODE_COMPONENT.getReadNum().setText(num);
        SELECTED_MODE_COMPONENT.getSuccessNum().setText(num);
        SELECTED_MODE_COMPONENT.getFailNum().setText(num);
    }

    /**
     * 初始化版本信息
     *
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void initVersion() {
        JPanel version = SELECTED_MODE_COMPONENT.getVersion();
        version.removeAll();
        List<VersionInfo> versions = getModeVersion();
        for (VersionInfo item : versions) {
            JButton subVersion = new JButton(item.getVersionName());
            formatButtonUi(subVersion);
            version.add(subVersion);
            // 设置监听事件
            addActionListener(subVersion, LISTENER_VERSION);
        }
        getFocus(SELECTED_MODE_COMPONENT.getResult());
    }

    /**
     * 获取焦点
     *
     * @param jComponent
     * @author: hspcadmin
     * @date: 2020/12/17
     * @return:
     */
    private static void getFocus(JComponent jComponent) {
        jComponent.dispatchEvent(new FocusEvent(jComponent, FocusEvent.FOCUS_GAINED, true));
        jComponent.requestFocusInWindow();
    }

    /**
     * 格式化button按钮
     *
     * @param button
     * @author: hspcadmin
     * @date: 2020/12/16
     * @return:
     */
    private static void formatButtonUi(JButton button) {
        // 去除按钮焦点虚线框
        button.setFocusPainted(false);
    }

    /**
     * 更多空白
     *
     * @param times
     * @author: hspcadmin
     * @date: 2020/12/14
     * @return:
     */
    private static String moreBlank(int times) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < times; i++) {
            stringBuffer.append(SYMBOL_SPACE_10);
        }
        return stringBuffer.toString();
    }

    /**
     * 初始化工作路径空白
     *
     * @param modeComponent
     * @author: hspcadmin
     * @date: 2020/12/16
     * @return:
     */
    private static void initWorkspaceBlank(ModeComponent modeComponent) {
        if (modeComponent == null) {
            modeComponent = SELECTED_MODE_COMPONENT;
        }
        modeComponent.getSourcePath().setPreferredSize(new Dimension(700, 25));
        modeComponent.getSourcePath().setEnabled(false);
        modeComponent.getTargetPath().setPreferredSize(new Dimension(700, 25));
        modeComponent.getTargetPath().setEnabled(false);
    }

    /**
     * 状态栏空格占位
     *
     * @param panel
     * @param describe
     * @param num
     * @author: hspcadmin
     * @date: 2020/12/14
     * @return:
     */
    private static void addBlank(JPanel panel, JLabel describe, JLabel num) {
        JLabel blankSuccessLabel = new JLabel(SYMBOL_SPACE_3);
        panel.add(blankSuccessLabel);
        panel.add(describe);
        panel.add(num);
    }

    /**
     * 获取对应模式版本号
     *
     * @author: hspcadmin
     * @date: 2020/12/14
     * @return:
     */
    private static List<VersionInfo> getModeVersion() {
        List<VersionInfo> version = new ArrayList();
        for (VersionInfo item : VERSION_CONFIG) {
            if (SELECTED_MODE.equals(item.getMode())) {
                version.add(item);
            }
        }
        return version;
    }

    /**
     * 获取选择版本信息
     *
     * @param versionName
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static VersionInfo getSelectedVersion(String versionName) {
        VersionInfo version = new VersionInfo();
        List<VersionInfo> versionList = getModeVersion();
        if (versionList != null) {
            for (VersionInfo item : versionList) {
                if (versionName.equals(item.getVersionName())) {
                    version = item;
                    SELECTED_VERSION = item;
                    break;
                }
            }
        }
        return version;
    }

    /**
     * 初始化工作目录
     *
     * @param versionName
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void initWorkspace(String versionName) {
        VersionInfo version = getSelectedVersion(versionName);
        if (version != null) {
            WORKSPACE = version.getSourcePath();
            EXPORT_WORKSPACE = version.getTargetPath();
            SELECTED_MODE_COMPONENT.getSourcePath().setText(version.getSourcePath());
            SELECTED_MODE_COMPONENT.getTargetPath().setText(version.getTargetPath());
            String content = String.format("当前模式【%s】\n当前版本【%s】\n", SELECTED_MODE, versionName);
            SCHEDULE_TITLE = content;
            initSchedule(content, null, false);
        }
    }

    /**
     * 初始化进度信息
     *
     * @param color
     * @param content
     * @param isAppend
     * @author: hspcadmin
     * @date: 2020/12/15
     * @return:
     */
    private static void initSchedule(String content, Color color, boolean isAppend) {
        SELECTED_MODE_COMPONENT.getSchedule().setFont(new Font(APP_LABEL.getFontName(), APP_LABEL.getFontStyle(),
                APP_LABEL.getFontSize()));
        if (color != null) {
            SELECTED_MODE_COMPONENT.getSchedule().setForeground(color);
        } else {
            SELECTED_MODE_COMPONENT.getSchedule().setForeground(Color.BLACK);
        }
        if (isAppend) {
            SELECTED_MODE_COMPONENT.getSchedule().append(content);
        } else {
            SELECTED_MODE_COMPONENT.getSchedule().setText(content);
        }
    }

    /**
     * 获取配置版本
     *
     * @param config
     * @author: hoomoomoo
     * @date: 2020/09/06
     * @return:
     */
    private static void getVersionConfig(Map config) {
        if (config != null) {
            Iterator<Map.Entry<String, String>> iterator = config.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                String versionName = item.getKey();
                String versionValue = item.getValue();
                String[] versionValueItem = versionValue.split(SYMBOL_DOLLAR);
                if (versionValueItem.length != 3) {
                    throw new RuntimeException(String.format("版本[ %s ]配置[ %s ]格式错误", versionName, versionValue));
                }
                VersionInfo itemVersion = new VersionInfo();
                // 获取版本号
                if (!versionValueItem[0].endsWith(SYMBOL_SLASH) && !versionValueItem[0].endsWith(SYMBOL_BACKSLASH_2)) {
                    versionValueItem[0] = CommonUtils.convertBackslash(versionValueItem[0] + SYMBOL_BACKSLASH_2);
                }
                if (!versionValueItem[1].endsWith(SYMBOL_SLASH) && !versionValueItem[1].endsWith(SYMBOL_BACKSLASH_2)) {
                    versionValueItem[1] = CommonUtils.convertBackslash(versionValueItem[1] + SYMBOL_BACKSLASH_2);
                }
                String[] versionNameItem = versionName.split(SYMBOL_POINT_2);
                if (versionNameItem.length != 2) {
                    throw new RuntimeException(String.format("版本[ %s ]配置[ %s ]格式错误", versionName, item.getValue()));
                }
                if (versionNameItem[0].equals(APP_LABEL.getModeCopyCode())) {
                    itemVersion.setMode(APP_LABEL.getModeCopy());
                } else if (versionNameItem[0].equals(APP_LABEL.getModeUpdateCode())) {
                    itemVersion.setMode(APP_LABEL.getModeUpdate());
                } else if (versionNameItem[0].equals(APP_LABEL.getModeSyncCode())) {
                    itemVersion.setMode(APP_LABEL.getModeSync());
                } else if (versionNameItem[0].equals(APP_LABEL.getModeCoverCode())) {
                    itemVersion.setMode(APP_LABEL.getModeCover());
                } else if (versionNameItem[0].equals(APP_LABEL.getModeMergerCode())) {
                    itemVersion.setMode(APP_LABEL.getModeMerger());
                }
                itemVersion.setVersionName(versionNameItem[1]);
                itemVersion.setSourcePath(versionValueItem[0]);
                itemVersion.setTargetPath(versionValueItem[1]);
                itemVersion.setStatusFile(versionValueItem[2]);
                VERSION_CONFIG.add(itemVersion);
            }
        }
    }

    /**
     * 读取配置文件
     *
     * @param fileName
     * @author: hoomoomoo
     * @date: 2020/09/03
     * @return:
     */
    private static Map<String, String> getConfigProperties(String fileName) {
        Map<String, String> config = new LinkedHashMap<>(16);
        Properties properties = new Properties();
        try {
            properties.load(getInputStreamReader(fileName, true));
            Enumeration items = properties.propertyNames();
            while (items.hasMoreElements()) {
                String item = String.valueOf(items.nextElement());
                config.put(item, properties.getProperty(item));
            }
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        }
        // todo 待删除
        if (true) {
            return config;
        }
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = getBufferedReader(fileName, true);
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (StringUtils.isBlank(inputPath.trim()) || inputPath.trim().startsWith(SYMBOL_WEI)) {
                        continue;
                    }
                    if (!inputPath.trim().contains(SYMBOL_EQUALS)) {
                        throw new RuntimeException(String.format("[ %s ]文件中[ %s ]格式错误", fileName, inputPath.trim()));
                    }
                    String[] item = inputPath.trim().split(SYMBOL_EQUALS);
                    if (item.length != 2) {
                        continue;
                    }
                    config.put(item[0], CommonUtils.convertUnicodeToCh(item[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    /**
     * 初始化启动模式
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void initStartMode() {
        URL url = ImFileUiUtils.class.getResource(CLAZZ);
        if (url.toString().startsWith(START_MODE_JAR)) {
            START_MODE = START_MODE_JAR;
        } else {
            START_MODE = START_MODE_PROJECT;
        }
    }

    /**
     * 获取文件编码格式
     *
     * @param fileName
     * @author: hoomoomoo
     * @date: 2020/10/17
     * @return:
     */
    private static BufferedReader getBufferedReader(String fileName, boolean changeName) throws FileNotFoundException,
            UnsupportedEncodingException {
        fileName = checkFile(fileName, changeName);
        String fileEncode = CommonUtils.getFileEncode(fileName);
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), fileEncode));
    }

    /**
     * 获取文件编码格式
     *
     * @param fileName
     * @author: hoomoomoo
     * @date: 2020/10/17
     * @return:
     */
    private static InputStreamReader getInputStreamReader(String fileName, boolean changeName) throws FileNotFoundException,
            UnsupportedEncodingException {
        fileName = checkFile(fileName, changeName);
        String fileEncode = CommonUtils.getFileEncode(fileName);
        return new InputStreamReader(new FileInputStream(fileName), fileEncode);
    }

    /**
     * 校验文件是否存在
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static String checkFile(String fileName, boolean changeName) {
        if (START_MODE_PROJECT.equals(START_MODE) && changeName) {
            fileName = ImFileUtils.class.getClassLoader().getResource(fileName).getFile();
        }
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("源文件[ %s ] 不存在", fileName));
        }
        return fileName;
    }

    /**
     * 格式化按钮名称
     *
     * @param buttonName
     * @author: humm23693
     * @date: 2020/11/27
     * @return:
     */
    private static String formatText(String buttonName) {
        if (StringUtils.isNotBlank(buttonName)) {
            return String.format(TEXT_TEMPLATE, buttonName);
        }
        return SYMBOL_EMPTY;
    }

    /**
     * 界面美化
     *
     * @param
     * @author: humm23693
     * @date: 2020/11/27
     * @return:
     */
    private static void formatUi() {
        try {
            UIManager.setLookAndFeel(WINDOWS_UI);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Font font = new Font(APP_LABEL.getFontName(), APP_LABEL.getFontStyle(), APP_LABEL.getFontSize());
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    /**
     * svn 更新
     *
     * @param svnClientManager
     * @param workspace
     * @param svnRevision
     * @param svnDepth
     * @author: hoomoomoo
     * @date: 2020/09/11
     * @return:
     */
    private static long update(SVNClientManager svnClientManager, File workspace,
                               SVNRevision svnRevision, SVNDepth svnDepth) {
        SVNUpdateClient svnUpdateClient = svnClientManager.getUpdateClient();
        svnUpdateClient.setIgnoreExternals(false);
        try {
            Long version = svnUpdateClient.doUpdate(workspace, svnRevision, svnDepth, false, false);
            SVN_ERROR_TIMES = 0;
            return version;
        } catch (SVNException e) {
            if (e.toString().contains(SVN_ERROR_CODE_E170001)) {
                initSchedule("svn同步异常 请检查配置项【svn.username】【 svn.password】", Color.RED, false);
            } else if (e.toString().contains(SVN_ERROR_CODE_E175002)) {
                initSchedule("svn同步异常 请检查网络连接是否正常;svn目录是否存在", Color.RED, false);
            } else if (e.toString().contains(SVN_ERROR_CODE_E155004)) {
                if (SVN_ERROR_TIMES == 0) {
                    SVN_ERROR_TIMES++;
                    if (cleanUp(svnClientManager, workspace)) {
                        update(svnClientManager, workspace, svnRevision, svnDepth);
                    } else {
                        initSchedule(String.format("svn同步异常 请至工作目录[ %s ]执行cleanUp并选择 Break write locks", workspace.getAbsolutePath()), Color.RED, false);
                    }
                } else {
                    initSchedule(String.format("svn同步异常 请至工作目录[ %s ]执行cleanUp并选择 Break write locks", workspace.getAbsolutePath()), Color.RED, false);
                }
            } else {
                e.printStackTrace();
            }
        }
        return 0L;
    }

    /**
     * svn 更新
     *
     * @param username
     * @param password
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/12
     * @return:
     */
    public static long update(String username, String password, String workspace) {
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, username, password);
        return update(svnClientManager, new File(workspace), SVNRevision.HEAD, SVNDepth.INFINITY);
    }

    /**
     * svn 解锁
     *
     * @param svnClientManager
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/20
     * @return:
     */
    private static boolean cleanUp(SVNClientManager svnClientManager, File workspace) {
        SVNWCClient svnwcClient = svnClientManager.getWCClient();
        try {
            svnwcClient.doCleanup(workspace);
        } catch (SVNException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * svn 解锁
     *
     * @param username
     * @param password
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/20
     * @return:
     */
    public static boolean cleanUp(String username, String password, String workspace) {
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions isvnOptions = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) isvnOptions, username, password);
        return cleanUp(svnClientManager, new File(workspace));
    }


}
