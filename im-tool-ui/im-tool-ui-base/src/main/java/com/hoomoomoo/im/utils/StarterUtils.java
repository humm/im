package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
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
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/4/3
 */
public class StarterUtils {

    public static void start(Stage primaryStage, String appCode) {
        try {
            ConfigCache.initAppCodeCache(appCode);
            LoggerUtils.info(MSG_DIVIDE_LINE, false);
            LoggerUtils.info(String.format(MSG_START, NAME_APP_START));
            FileUtils.unJar(PATH_APP);
            LoggerUtils.info(String.format(MSG_UPDATE, NAME_CONFIG_FILE));
            LoggerUtils.info(String.format(MSG_LOAD, NAME_CONFIG_INFO));
            primaryStage.getIcons().add(new Image(PATH_ICON));
            Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_STARTER_FXML)));
            Scene scene = new Scene(root);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String appName = appConfigDto.getAppName();
            if (!FileUtils.startByJar()) {
                appName += SYMBOL_HYPHEN_1 + APP_MODE_NAME_APP;
            }
            appName += SYMBOL_HYPHEN_1 + CommonUtils.getVersion();
            scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
            primaryStage.setTitle(appName);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            LoggerUtils.info(String.format(MSG_COMPLETE, NAME_APP_START));
            primaryStage.setOnCloseRequest(event -> {
                try {
                    primaryStage.close();
                    if (FileUtils.startByJar()) {
                        String processName = FileUtils.getJarName().replace(FILE_TYPE_JAR, FILE_TYPE_EXE);
                        processName = processName.substring(processName.lastIndexOf(SYMBOL_SLASH) + 1);
                        Runtime.getRuntime().exec(String.format(CMD_KILL_APP, processName));
                    }
                } catch (IOException e) {
                }
            });
            WebUtils.initWebServer(SERVER_URL, PORT);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
