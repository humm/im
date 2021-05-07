package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseDto;
import com.hoomoomoo.im.dto.LogDto;
import javafx.application.Platform;
import javafx.scene.control.*;

import java.util.List;

/**
 * @author humm23693
 * @description 输出工具类
 * @package com.hoomoomoo.im
 * @date 2021/04/19
 */
public class OutputUtils {

    private final static String STR_EMPTY = "";

    public static void info(TableView tableView, BaseDto baseDto) {
        Platform.runLater(() -> {
            tableView.getItems().add(baseDto);
        });
    }

    public static void info(TableView tableView, String msg) {
        LogDto logDto = new LogDto();
        logDto.setTime(CommonUtils.getCurrentDateTime1());
        logDto.setMsg(msg);
        Platform.runLater(() -> {
            tableView.getItems().add(logDto);
        });
    }

    public static void info(Object obj, String text) {
        Platform.runLater(() -> {
            if (obj instanceof TextArea) {
                ((TextArea) obj).appendText(text);
            } else if (obj instanceof TextField) {
                ((TextField) obj).setText(text);
            } else if (obj instanceof Label) {
                ((Label) obj).setText(text);
            }
        });
    }

    public static void selected(Object obj, Boolean selected) {
        Platform.runLater(() -> {
            if (obj instanceof RadioButton) {
                ((RadioButton) obj).setSelected(selected);
            }
        });
    }

    public static void info(TextArea textArea, List<String> text) {
        Platform.runLater(() -> {
            for (String item : text) {
                textArea.appendText(item);
            }
        });
    }

    public static void clearLog(Object obj) {
        Platform.runLater(() -> {
            if (obj instanceof TableView) {
                ((TableView) obj).getItems().clear();
            } else if (obj instanceof TextArea) {
                ((TextArea) obj).clear();
            } else if (obj instanceof TextField) {
                ((TextField) obj).clear();
            } else if (obj instanceof Label) {
                ((Label) obj).setText(STR_EMPTY);
            }
        });
    }
}
