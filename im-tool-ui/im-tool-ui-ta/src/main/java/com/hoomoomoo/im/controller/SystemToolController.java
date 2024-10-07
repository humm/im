package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.ScriptCompareSql;
import com.hoomoomoo.im.extend.ScriptRepairSql;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.extend.ScriptUpdateSql;
import com.hoomoomoo.im.task.SystemToolTask;
import com.hoomoomoo.im.task.SystemToolTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE.*;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE_EXTEND.REPAIR_EXT;
import static com.hoomoomoo.im.consts.BaseConst.SQL_CHECK_TYPE_EXTEND.REPAIR_OLD_MENU;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class SystemToolController implements Initializable {

    @FXML
    private TextArea logs;

    @FXML
    private TextArea baseLogs;

    @FXML
    private Button shakeMouseBtn;

    @FXML
    private Button cancelShakeMouseBtn;

    @FXML
    private Button clearVersionBtn;

    @FXML
    private Button updateVersionBtn;

    @FXML
    private Button showVersionBtn;

    private Timer shakeMouseTimer;

    private Robot robot;

    private int moveStep = 1;

    private boolean executeFlag = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JvmCache.setSystemToolController(this);
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        moveStep = Integer.valueOf(appConfigDto.getSystemToolShakeMouseStep());
        if (Boolean.valueOf(appConfigDto.getSystemToolShakeMouseAuto())) {
            shakeMouse(null);
            addLog(NAME_SHAKE_MOUSE);
        }
    }

    @FXML
    void shakeMouse(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, NAME_SHAKE_MOUSE));
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        shakeMouseBtn.setDisable(true);
        cancelShakeMouseBtn.setDisable(false);
        if (shakeMouseTimer == null) {
            shakeMouseTimer = new Timer();
        }
        shakeMouseTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doShakeMouse(appConfigDto);
            }
        }, 1, Long.valueOf(appConfigDto.getSystemToolShakeMouseTimer()) * 1000);
        addLog(NAME_SHAKE_MOUSE);
    }

    @FXML
    void cancelShakeMouse(ActionEvent event) {
        shakeMouseBtn.setDisable(false);
        cancelShakeMouseBtn.setDisable(true);
        if (shakeMouseTimer != null) {
            shakeMouseTimer.cancel();
            shakeMouseTimer = null;
        }
        addLog(NAME_SHAKE_MOUSE);
    }

    @FXML
    void updateVersion(ActionEvent event) {
        updateVersionBtn.setDisable(true);
        try {
            executeUpdateVersion();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            updateVersionBtn.setDisable(false);
        }
    }

    @FXML
    void clearVersion(ActionEvent event) {
        clearVersionBtn.setDisable(true);
        try {
            String statPath = FileUtils.getFilePath(PATH_VERSION_EXTEND_STAT);
            FileUtils.writeFile(statPath, STR_BLANK, false);
            OutputUtils.info(logs, getUpdateVersionMsg("清除个性化成功"));
            OutputUtils.info(logs, STR_NEXT_LINE);
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            clearVersionBtn.setDisable(false);
        }
        addLog("同步发版时间");
    }

    @FXML
    void showVersion(ActionEvent event) {
        showVersionBtn.setDisable(true);
        try {
            JvmCache.getHepTodoController().doShowVersion();
        } catch (Exception e) {
            LoggerUtils.info(e);
            LoggerUtils.info(e);
            OutputUtils.info(logs, e.getMessage());
        } finally {
            showVersionBtn.setDisable(false);
        }
        addLog("查看发版时间");
    }

    @FXML
    void showSystemLog(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SYSTEM_LOG, "系统日志");
            addLog("查看系统日志");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("查看系统日志错误"));
        }
    }

    @FXML
    void clearSystemLog(ActionEvent event) {
        try {
            FileUtils.deleteFile(new File(FileUtils.getFilePath(String.format(SUB_PATH_LOG, "appLog"))));
            MenuFunctionConfig.FunctionConfig[] functionConfigs = MenuFunctionConfig.FunctionConfig.values();
            for (MenuFunctionConfig.FunctionConfig functionConfig : functionConfigs) {
                FileUtils.deleteFile(new File(FileUtils.getFilePath(String.format(SUB_PATH_LOG, functionConfig.getLogFolder()))));
            }
            OutputUtils.info(logs, getCommonMsg(NAME_CLEAR_LOG, "日志清除成功"));
            addLog("清除系统日志");
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(logs, getCheckMenuMsg("清除系统日志错误"));
        }
    }

    private String formatDate(String date) {
        return StringUtils.isBlank(date) ? STR_0 : date.replaceAll(STR_HYPHEN, STR_BLANK);
    }

    private String getShakeMouseMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_SHAKE_MOUSE + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    private String getUpdateVersionMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_UPDATE_VERSION + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    public static String getCheckMenuMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_CHECK_MENU + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    public static String getCommonMsg (String functionName, String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ functionName + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    private void doShakeMouse(AppConfigDto appConfigDto) {
        try {
            if (robot == null) {
                robot = new Robot();
            }
        } catch (AWTException e) {
            LoggerUtils.info(getShakeMouseMsg(e.getMessage()));
            OutputUtils.info(baseLogs, getShakeMouseMsg(e.getMessage()));
        }
        Point pos = MouseInfo.getPointerInfo().getLocation();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        OutputUtils.info(baseLogs, getShakeMouseMsg("显示器分辨率: " + screenSize.getWidth() + " * " + screenSize.getHeight()));
        OutputUtils.info(baseLogs, getShakeMouseMsg("鼠标当前位置: " + pos.x + " * " + pos.y));
        if (pos.x >= screenSize.getWidth() || pos.y >= screenSize.getHeight()) {
            moveStep = moveStep * -1;
        }
        if (pos.x == 0 || pos.y == 0) {
            moveStep = Math.abs(moveStep);
        }
        OutputUtils.info(baseLogs, getShakeMouseMsg("鼠标移动步长: " + moveStep));
        OutputUtils.info(baseLogs, getShakeMouseMsg("模拟鼠标移动 ......"));
        robot.mouseMove(pos.x + moveStep, pos.y + moveStep);
        OutputUtils.info(baseLogs, getShakeMouseMsg("鼠标移动位置: " + (pos.x + moveStep) + " * " + (pos.y + moveStep)));
        OutputUtils.info(baseLogs, STR_NEXT_LINE);

        if (appConfigDto.getSystemToolShakeMouseStopTime().compareTo(CommonUtils.getCurrentDateTime13()) <= 0) {
            String stopMsg = getShakeMouseMsg("截止时间【" + appConfigDto.getSystemToolShakeMouseStopTime() + "】自动停止......" + STR_NEXT_LINE);
            OutputUtils.info(baseLogs, stopMsg);
            cancelShakeMouse(null);
        }
    }

    public void executeUpdateVersion() throws Exception {
        List<String> list = new ArrayList<>();
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        String filePath = appConfigDto.getSystemToolUpdateVersionPath();
        FileInputStream fileInputStream = null;
        if (StringUtils.isBlank(filePath)) {
            if (logs != null) {
                OutputUtils.info(logs, getUpdateVersionMsg("请配置【system.tool.update.version.path】"));
            } else {
                throw new Exception("请配置【system.tool.update.version.path】");
            }
        } else {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    List<File> files = Arrays.asList(file.listFiles());
                    if (CollectionUtils.isNotEmpty(files)) {
                        files = files.stream().filter(ele -> ele.getName().contains("版本列表")).collect(Collectors.toList());
                        Collections.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o1.lastModified() - o2.lastModified() >= 0 ? -1 : 1;
                            }
                        });
                    }
                    if (CollectionUtils.isNotEmpty(files)) {
                        filePath = files.get(0).getPath();
                    }
                }
                fileInputStream = getVersionFile(null, filePath, 0);
                HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
                HSSFSheet sheet = workbook.getSheetAt(0);
                int rows = sheet.getLastRowNum();
                for (int i = 1; i <= rows; i++) {
                    StringBuilder item = new StringBuilder();
                    HSSFRow row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    String version = row.getCell(1).toString();
                    if (StringUtils.isBlank(version)) {
                        continue;
                    }
                    String closeDate = formatDate(row.getCell(2).toString());
                    String publishDate = formatDate(row.getCell(3).toString());
                    if (StringUtils.isBlank(closeDate)) {
                        closeDate = publishDate;
                    }
                    String customer = STR_SPACE;
                    item.append(version).append(STR_SEMICOLON).append(closeDate).append(STR_SEMICOLON).append(publishDate)
                            .append(STR_SEMICOLON).append(customer).append(STR_SEMICOLON);
                    list.add(item.toString());
                }
                String statPath = FileUtils.getFilePath(PATH_VERSION_STAT);
                FileUtils.writeFile(statPath, list, false);
                if (logs != null) {
                    OutputUtils.info(logs, getUpdateVersionMsg("同步成功"));
                    OutputUtils.info(logs, STR_NEXT_LINE);
                }
                addLog("同步发版时间");
            } catch (Exception e) {
                LoggerUtils.info(e);
                throw new Exception(e.getMessage());
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private static FileInputStream getVersionFile(FileInputStream fileInputStream, String filePath, int times) throws FileNotFoundException {
        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains("拒绝访问")) {
                times++;
                if (times <= 3) {
                    getVersionFile(fileInputStream, filePath, times);
                }
                throw new FileNotFoundException("权限不够,请重试");
            } else {
                throw new FileNotFoundException("文件不存在,请检查");
            }
        }
        return fileInputStream;
    }

    public static void addLog(String msg) {
        List<String> logs = new ArrayList<>();
        logs.add(msg);
        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), logs);
    }

}
