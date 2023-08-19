package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
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
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SVN_UPDATE;

/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class SystemToolController implements Initializable {

    public static final Long SHAKE_MOUSE_TIMER = 3 * 1000L;

    public static final String SHAKE_MOUSE_STOP_TIME = "220000";

    @FXML
    private TextArea logs;

    @FXML
    private Button shakeMouseBtn;

    @FXML
    private Button cancelShakeMouseBtn;

    private Timer shakeMouseTimer;
    private Robot robot;
    private int lastX;
    private int lastY;
    private int moveStep = 1;


    @FXML
    void shakeMouse(ActionEvent event) {
        LoggerUtils.info(String.format(BaseConst.MSG_USE, NAME_SHAKE_MOUSE));
        shakeMouseBtn.setDisable(true);
        cancelShakeMouseBtn.setDisable(false);
        if (shakeMouseTimer == null) {
            shakeMouseTimer = new Timer();
        }
        shakeMouseTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doShakeMouse();
            }
        }, 1, SHAKE_MOUSE_TIMER);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private String getShakeMouseMsg (String msg) {
        return TaCommonUtils.getMsgContainDate("【"+ NAME_SHAKE_MOUSE + "】") + SYMBOL_SPACE + msg + SYMBOL_NEXT_LINE;
    }

    private void doShakeMouse() {
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
            moveStep = Math.abs(moveStep) * -1;
        }
        moveStep = moveStep * -1;
        OutputUtils.info(logs, getShakeMouseMsg("模拟鼠标移动步长 " + moveStep));
        OutputUtils.info(logs, getShakeMouseMsg("模拟鼠标移动......"));
        robot.mouseMove(pos.x + moveStep, pos.y + moveStep);
        OutputUtils.info(logs, getShakeMouseMsg("鼠标移动位置: " + pos.x + " * " + pos.y));
        OutputUtils.info(logs, SYMBOL_NEXT_LINE);

        if (SHAKE_MOUSE_STOP_TIME.compareTo(CommonUtils.getCurrentDateTime13()) <= 0) {
            OutputUtils.info(logs, getShakeMouseMsg("截止时间【" + SHAKE_MOUSE_STOP_TIME + "】自动停止......"));
            cancelShakeMouse(null);
        }
    }
}
