package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
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
            Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_STARTER_FXML)));
            Scene scene = new Scene(root);
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            String appName = appConfigDto.getAppName() + SYMBOL_SPACE_2;
            if (!FileUtils.startByJar()) {
                appName += String.format(MSG_APP_TITLE, APP_MODE_NAME, APP_MODE_NAME_APP);
                String pathFolder = FileUtils.getPathFolder().replace(APP_CODE_BASE, appCode);
                String sourceIcon = pathFolder + PATH_ICON;
                String factoryIcon = pathFolder + FACTORY_ICON;
                FileUtils.addWatermark(new File(sourceIcon), new File(factoryIcon));
                primaryStage.getIcons().add(new Image(FACTORY_ICON));
            } else {
                primaryStage.getIcons().add(new Image(PATH_ICON));
            }
            appName += String.format(MSG_APP_TITLE, NAME_VERSION, CommonUtils.getVersion());
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
            /*if (CommonUtils.checkUser(appConfigDto.getAppUser(), APP_USER_IM_SERVER)) {
                ServerUtils.initServer(SERVER_URL, SERVER_PORT);
            } else {
                LoggerUtils.info(HttpRequestUtils.sendPost(SERVER_HTTP + SYMBOL_COLON + SERVER_PORT + SERVER_URL, KEY_VERSION + SYMBOL_EQUAL + CommonUtils.getVersion()));
            }*/
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }
}
