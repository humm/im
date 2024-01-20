package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.extend.MenuCompareSql;
import com.hoomoomoo.im.extend.MenuUpdateSql;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
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

    private Timer shakeMouseTimer;

    private Robot robot;

    private int moveStep = 1;


    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        moveStep = Integer.valueOf(appConfigDto.getSystemToolShakeMouseStep());
        if (Boolean.valueOf(appConfigDto.getSystemToolShakeMouseAuto())) {
            shakeMouse(null);
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
    }

    @FXML
    void cancelShakeMouse(ActionEvent event) {
        shakeMouseBtn.setDisable(false);
        cancelShakeMouseBtn.setDisable(true);
        if (shakeMouseTimer != null) {
            shakeMouseTimer.cancel();
            shakeMouseTimer = null;
        }
    }

    @FXML
    void updateVersion(ActionEvent event) {
        updateVersionBtn.setDisable(true);
        try {
            executeUpdateVersion();
        } catch (Exception e) {
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
            List<String> record = new ArrayList<>();
            record.add(getUpdateVersionMsg("清除个性化成功"));
            LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), record);
        } catch (Exception e) {
            OutputUtils.info(logs, e.getMessage());
        } finally {
            clearVersionBtn.setDisable(false);
        }
    }

    private boolean checkFlaging = false;

    @FXML
    void checkMenu(ActionEvent event) {
        if (checkFlaging) {
            OutputUtils.info(logs, getCheckMenuMsg("检查中 >>>  请稍后再试 >>>"));
            return;
        }
        checkFlaging = true;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputUtils.info(logs, getCheckMenuMsg("检查开始"));
                        OutputUtils.info(logs, getCheckMenuMsg("检查中 >>>  请稍后 >>>  >>>  >>>"));
                        new MenuCompareSql().check();
                        OutputUtils.info(logs, getCheckMenuMsg("检查结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        List<String> record = new ArrayList<>();
                        record.add(getCheckMenuMsg("检查成功"));
                        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), record);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OutputUtils.info(logs, getCheckMenuMsg(e.getMessage()));
                    } finally {
                        checkFlaging = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            OutputUtils.info(logs, getCheckMenuMsg(e.getMessage()));
        }
    }

    @FXML
    void skipNewMenu(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_NEW_MENU, "忽略全量新版");
    }

    @FXML
    void skipOldMenu(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_OLD_MENU, "忽略全量老版");
    }

    @FXML
    void skipNewDiff(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_NEW_DIFF_MENU, "忽略全量开通新版");
    }

    @FXML
    void skipOldDiff(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_OLD_DIFF_MENU, "忽略全量开通老版");
    }

    @FXML
    void skipRouter(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_ROUTER, "配置忽略路由");
    }

    @FXML
    void skipLog(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_LOG, "忽略日志信息");
    }

    @FXML
    void skipErrorLog(ActionEvent event) throws Exception {
        TaCommonUtils.openBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_SKIP_ERROR_LOG, "忽略错误日志");
    }

    @FXML
    void showCheckResult(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_CHECK_RESULT, "检查结果");
        } catch (Exception e) {
            if (e instanceof LoadException) {
                OutputUtils.info(logs, getCheckMenuMsg("结果文件不存在 >>> 请检查"));
            } else {
            }
        }
    }

    @FXML
    void showUpdateResult(ActionEvent event) {
        try {
            TaCommonUtils.openMultipleBlankChildStage(PAGE_TYPE_SYSTEM_TOOL_UPDATE_RESULT, "检查结果");
        } catch (Exception e) {
            if (e instanceof LoadException) {
                OutputUtils.info(logs, getUpdateMenuMsg("结果文件不存在 >>> 请检查"));
            } else {
                OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
            }
        }
    }

    @FXML
    void updateMenu(ActionEvent event) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 设置颜色
                        // logs.setStyle("-fx-text-fill: green;");
                        OutputUtils.info(logs, getUpdateMenuMsg("生成开始"));
                        new MenuUpdateSql().generateSql();
                        OutputUtils.info(logs, getUpdateMenuMsg("生成结束"));
                        OutputUtils.info(logs, STR_NEXT_LINE);
                        List<String> record = new ArrayList<>();
                        record.add(getUpdateMenuMsg("生成成功"));
                        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), record);
                    } catch (Exception e) {
                        OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
                    }
                }
            }).start();
        } catch (Exception e) {
            OutputUtils.info(logs, getUpdateMenuMsg(e.getMessage()));
        }
    }

    private String formatDate(String date) {
        if (StringUtils.isNotBlank(date) && (date.contains("E") || date.contains("e"))) {
            date = new BigDecimal(date).toPlainString();
        }
        return date;
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

    public static String getUpdateMenuMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_UPDATE_MENU + "】") + STR_SPACE + msg + STR_NEXT_LINE;
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

        List<String> record = new ArrayList<>();
        record.add(getShakeMouseMsg("鼠标当前位置: " + pos.x + " * " + pos.y));

        if (appConfigDto.getSystemToolShakeMouseStopTime().compareTo(CommonUtils.getCurrentDateTime13()) <= 0) {
            String stopMsg = getShakeMouseMsg("截止时间【" + appConfigDto.getSystemToolShakeMouseStopTime() + "】自动停止......" + STR_NEXT_LINE);
            OutputUtils.info(baseLogs, stopMsg);
            record.add(stopMsg);
            cancelShakeMouse(null);
        }
        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), record);
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
                fileInputStream = new FileInputStream(filePath);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheetAt(0);
                int rows = sheet.getLastRowNum();
                for (int i = 2; i <= rows; i++) {
                    StringBuilder item = new StringBuilder();
                    XSSFRow row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    String version = row.getCell(2).toString();
                    if (StringUtils.isBlank(version)) {
                        continue;
                    }
                    String closeDate = formatDate(row.getCell(4).toString());
                    String publishDate = formatDate(row.getCell(5).toString());
                    if (StringUtils.isBlank(closeDate)) {
                        closeDate = publishDate;
                    }
                    String customer = row.getCell(6).toString();
                    String memo = row.getCell(3).toString();
                    item.append(version).append(STR_SEMICOLON).append(closeDate).append(STR_SEMICOLON).append(publishDate)
                            .append(STR_SEMICOLON).append(customer).append(STR_SEMICOLON).append(memo);
                    list.add(item.toString());
                }
                String statPath = FileUtils.getFilePath(PATH_VERSION_STAT);
                FileUtils.writeFile(statPath, list, false);
                if (logs != null) {
                    OutputUtils.info(logs, getUpdateVersionMsg("同步成功"));
                    OutputUtils.info(logs, STR_NEXT_LINE);
                }

                List<String> record = new ArrayList<>();
                record.add(getUpdateVersionMsg("同步发版时间成功"));
                LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), record);
            } catch (Exception e) {
                throw new Exception(e);
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
}
