package com.hoomoomoo.im.utils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import static com.hoomoomoo.im.consts.BaseConst.STR_BLANK;

public class InputUtils {

    public static String getComponentValue(Object obj) {
        if (obj == null) {
            return STR_BLANK;
        }
        if (obj instanceof TextArea) {
            return ((TextArea) obj).getText();
        } else if (obj instanceof TextField) {
            return ((TextField) obj).getText();
        } else if (obj instanceof Label) {
            return ((Label) obj).getText();
        } else if (obj instanceof ComboBox) {
            Object value = ((ComboBox) obj).getSelectionModel().getSelectedItem();
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return STR_BLANK;
    }

    public static String getComboBoxValue(ActionEvent event) {
        Object value = ((ComboBox)event.getSource()).getSelectionModel().getSelectedItem();
        return value == null ? STR_BLANK : value.toString();
    }
}
