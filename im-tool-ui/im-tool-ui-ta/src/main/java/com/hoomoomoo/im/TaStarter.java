package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.StarterUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description 应用启动
 * @package im
 * @date 2021/04/18
 */
public class TaStarter extends Application {

    @Override
    public void start(Stage primaryStage) {
        StarterUtils.start(primaryStage, APP_CODE_TA);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
