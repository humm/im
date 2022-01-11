package com.hoomoomoo.im.utils;

import javafx.application.Platform;
import javafx.scene.control.Button;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/1/11
 */
public class ComponentUtils {

    public static void setButtonDisabled(Button... buttons) {
        setButtonStatus(true, buttons);
    }

    public static void setButtonEnabled(Button... buttons) {
        setButtonStatus(false, buttons);
    }

    public static void setButtonStatus(boolean disabled, Button... buttons) {
        Platform.runLater(() -> {
            for (Button button : buttons) {
                button.setDisable(disabled);
            }
        });
    }
}
