package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseDto;
import com.hoomoomoo.im.dto.LogDto;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.Date;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_EMPTY;

/**
 * @author humm23693
 * @description 输出工具类
 * @package im
 * @date 2021/04/19
 */
public class OutputUtils {

    public static void info(TableView tableView, BaseDto baseDto, boolean scroll) {
        if (tableView == null) {
            return;
        }
        Platform.runLater(() -> {
            tableView.getItems().add(baseDto);
            if (scroll) {
                tableView.scrollTo(tableView.getItems().size());
            }
            setEnabled(tableView);
        });
    }

    public static void info(TableView tableView, BaseDto baseDto) {
        info(tableView, baseDto, true);
    }

    public static void infoList(TableView tableView, List<? extends BaseDto> baseDtoList, boolean scroll) {
        if (tableView == null) {
            return;
        }
        Platform.runLater(() -> {
            for (BaseDto baseDto : baseDtoList) {
                tableView.getItems().add(baseDto);
            }
            if (scroll) {
                tableView.scrollTo(tableView.getItems().size());
            }
            setEnabled(tableView);
        });
    }

    public static void infoList(TableView tableView, List<? extends BaseDto> baseDtoList) {
        infoList(tableView, baseDtoList, true);
    }

    public static void info(TableView tableView, String msg) {
        info(tableView, msg, false);
    }

    public static void info(TableView tableView, String msg, boolean onlyTime) {
        if (tableView == null) {
            return;
        }
        LogDto logDto = new LogDto();
        logDto.setTime(CommonUtils.getCurrentDateTime1());
        if (onlyTime) {
            logDto.setTime(CommonUtils.getCurrentDateTime8(new Date()));
        }
        logDto.setMsg(msg);
        Platform.runLater(() -> {
            tableView.getItems().add(logDto);
            tableView.scrollTo(tableView.getItems().size());
            setEnabled(tableView);
        });
    }

    private static void setEnabled(TableView tableView) {
        tableView.setEditable(true);
        ObservableList<TableColumn> columns = tableView.getColumns();
        for (int i=0; i<columns.size(); i++) {
            if (i != 0) {
                columns.get(i).setCellFactory(TextFieldTableCell.forTableColumn());
            }
        }
    }

    public static void info(Object obj, String text) {
        if (obj == null) {
            return;
        }
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

    public static void selected(Object obj, boolean selected) {
        if (obj == null) {
            return;
        }
        Platform.runLater(() -> {
            if (obj instanceof RadioButton) {
                ((RadioButton) obj).setSelected(selected);
            }
        });
    }

    public static void info(TextArea textArea, List<String> text) {
        if (textArea == null) {
            return;
        }
        Platform.runLater(() -> {
            for (String item : text) {
                textArea.appendText(item);
            }
        });
    }

    public static void clearLog(Object obj) {
        if (obj == null) {
            return;
        }
        Platform.runLater(() -> {
            if (obj instanceof TableView) {
                ((TableView) obj).getItems().clear();
            } else if (obj instanceof TextArea) {
                ((TextArea) obj).clear();
            } else if (obj instanceof TextField) {
                ((TextField) obj).clear();
            } else if (obj instanceof Label) {
                ((Label) obj).setText(SYMBOL_EMPTY);
            }
        });
    }
}
