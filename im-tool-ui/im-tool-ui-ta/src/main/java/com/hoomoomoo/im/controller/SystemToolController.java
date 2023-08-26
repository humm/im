package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import com.hoomoomoo.im.utils.TaCommonUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;

import java.awt.*;
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
    private Button shakeMouseBtn;

    @FXML
    private Button cancelShakeMouseBtn;

    private Timer shakeMouseTimer;

    private Robot robot;

    private int moveStep = 1;


    @FXML
    void shakeMouse(ActionEvent event) throws Exception {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, NAME_SHAKE_MOUSE));
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
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

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        moveStep = Integer.valueOf(appConfigDto.getSystemToolShakeMouseStep());
        if (Boolean.valueOf(appConfigDto.getSystemToolShakeMouseAuto())) {
            shakeMouse(null);
        }
    }

    private String getShakeMouseMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_SHAKE_MOUSE + "】") + STR_SPACE + msg + STR_NEXT_LINE;
    }

    private void doShakeMouse(AppConfigDto appConfigDto) {
        try {
            if (robot == null) {
                robot = new Robot();
            }
        } catch (AWTException e) {
            LoggerUtils.info(getShakeMouseMsg(e.getMessage()));
            OutputUtils.info(logs, getShakeMouseMsg(e.getMessage()));
        }
        Point pos = MouseInfo.getPointerInfo().getLocation();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        OutputUtils.info(logs, getShakeMouseMsg("显示器分辨率: " + screenSize.getWidth() + " * " + screenSize.getHeight()));
        OutputUtils.info(logs, getShakeMouseMsg("鼠标当前位置: " + pos.x + " * " + pos.y));
        if (pos.x >= screenSize.getWidth() || pos.y >= screenSize.getHeight()) {
            moveStep = moveStep * -1;
        }
        if (pos.x == 0 || pos.y == 0) {
            moveStep = Math.abs(moveStep);
        }
        OutputUtils.info(logs, getShakeMouseMsg("鼠标移动步长: " + moveStep));
        OutputUtils.info(logs, getShakeMouseMsg("模拟鼠标移动 ......"));
        robot.mouseMove(pos.x + moveStep, pos.y + moveStep);
        OutputUtils.info(logs, getShakeMouseMsg("鼠标移动位置: " + (pos.x + moveStep) + " * " + (pos.y + moveStep)));
        OutputUtils.info(logs, STR_NEXT_LINE);

        List<String> record = new ArrayList<>();
        record.add(getShakeMouseMsg("鼠标当前位置: " + pos.x + " * " + pos.y));

        if (appConfigDto.getSystemToolShakeMouseStopTime().compareTo(CommonUtils.getCurrentDateTime13()) <= 0) {
            String stopMsg = getShakeMouseMsg("截止时间【" + appConfigDto.getSystemToolShakeMouseStopTime() + "】自动停止......" + STR_NEXT_LINE);
            OutputUtils.info(logs, stopMsg);
            record.add(stopMsg);
            cancelShakeMouse(null);
        }
        LoggerUtils.writeLogInfo(SYSTEM_TOOL.getCode(), new Date(), record);
    }
}
