package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseDto;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.STR_EMPTY;

/**
 * @author humm23693
 * @description 输出工具类
 * @package com.hoomoomoo.im
 * @date 2021/04/19
 */
public class OutputUtils {

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
                ((TextField) obj).setText(text);
            } else if (obj instanceof Label) {
                ((Label) obj).setText(text);
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
