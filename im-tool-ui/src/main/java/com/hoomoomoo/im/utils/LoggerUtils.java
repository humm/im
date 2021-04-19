package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseLogDto;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.List;

/**
 * @author humm23693
 * @description 日志工具类
 * @package com.hoomoomoo.im
 * @date 2021/04/19
 */
public class LoggerUtils {

    public static void info(TableView tableView, BaseLogDto baseLog) {
        Platform.runLater(() -> {
            // 更新UI
            tableView.getItems().add(baseLog);
        });
    }

    public static void info(TextArea textArea, String log) {
        Platform.runLater(() -> {
            // 更新UI
            textArea.appendText(log);
        });
    }

    public static void info(TextArea textArea, List<String> log) {
        Platform.runLater(() -> {
            // 更新UI
            for (String item : log) {
                textArea.appendText(item);
            }
        });
    }

    public static void clearLog(TableView tableView) {
        tableView.getItems().clear();
    }

    public static void clearLog(TextArea textArea) {
        textArea.clear();
    }
}
