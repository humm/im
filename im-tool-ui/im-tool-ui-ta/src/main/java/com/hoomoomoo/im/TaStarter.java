package com.hoomoomoo.im;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.StarterUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

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
