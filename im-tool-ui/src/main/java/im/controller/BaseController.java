package im.controller;

import im.utils.LoggerUtils;
import im.utils.OutputUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import static im.consts.BaseConst.SYMBOL_PERCENT;
import static im.consts.BaseConst.SYMBOL_POINT_SLASH;

/**
 * @author humm23693
 * @description TODO
 * @package im.controller
 * @date 2021/11/6
 */
public class BaseController {

    @FXML
    public ProgressIndicator schedule;

    @FXML
    public Label scheduleText;

    public double progress = 0;

    public void updateProgress(double step) {
        new Thread(() -> {
            while (true) {
                if (progress >= 0.98) {
                    break;
                }
                if (progress <= 0.6) {
                    setProgress(progress + step);
                } else if (progress < 0.98) {
                    setProgress(progress + 0.01);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LoggerUtils.info(e);
                }
            }
        }).start();
    }

    public void updateProgress() {
        updateProgress(0.05);
    }

    synchronized public void setProgress(double value) {
        try {
            progress = value;
            Platform.runLater(() -> {
                schedule.setProgress(progress);
                OutputUtils.info(scheduleText, String.valueOf(value * 100).split(SYMBOL_POINT_SLASH)[0] + SYMBOL_PERCENT);
                schedule.requestFocus();
            });
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
