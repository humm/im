package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseDto;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * @author humm23693
 * @description 日志工具类
 * @package com.hoomoomoo.im
 * @date 2021/04/19
 */
public class LoggerUtils {

    public static void info(TableView tableView, BaseDto baseDto) {
        Platform.runLater(() -> {
            tableView.getItems().add(baseDto);
        });
    }

    public static void info(Object obj, String text) {
        Platform.runLater(() -> {
            if (obj instanceof TextArea) {
                ((TextArea) obj).appendText(text);
            } else if (obj instanceof TextField) {
                ((TextField) obj).appendText(text);
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
            }
        });
    }
}
