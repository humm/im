package com.hoomoomoo.im;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
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
        try {
            ConfigCache.initAppCodeCache(APP_CODE_TA);
            LoggerUtils.info(MSG_DIVIDE_LINE, false);
            LoggerUtils.info(String.format(MSG_START, "应用启动"));
            FileUtils.unJar(PATH_APP);
            LoggerUtils.info(String.format(MSG_UPDATE, "配置文件"));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            LoggerUtils.info(String.format(MSG_LOAD, "配置信息"));
            primaryStage.getIcons().add(new Image(PATH_ICON));
            Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_STARTER_FXML)));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
            primaryStage.setTitle(appConfigDto.getAppName());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            LoggerUtils.info(String.format(MSG_COMPLETE, "应用启动"));
            primaryStage.setOnCloseRequest(event -> {
                try {
                    primaryStage.close();
                    if (FileUtils.startByJar(PATH_APP)) {
                        String processName = FileUtils.getJarName().replace(FILE_TYPE_JAR, FILE_TYPE_EXE);
                        processName = processName.substring(processName.lastIndexOf(SYMBOL_SLASH) + 1);
                        Runtime.getRuntime().exec(String.format(CMD_KILL_APP, processName));
                    }
                } catch (IOException e) {
                }
            });
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
