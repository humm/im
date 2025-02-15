package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
            LoggerUtils.appStartInfo(String.format(MSG_UPDATE, NAME_CONFIG_FILE));
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            String appName = getAppName(appConfigDto.getAppName()) + STR_SPACE_2;
            if (!FileUtils.startByJar()) {
                appName += String.format(MSG_APP_TITLE, APP_MODE_NAME, APP_MODE_NAME_APP);
                String pathFolder = FileUtils.getPathFolder().replace(APP_CODE_BASE, appCode);
                String sourceIcon = pathFolder + PATH_ICON;
                String factoryIcon = pathFolder + FACTORY_ICON;
                FileUtils.addWatermark(new File(sourceIcon), new File(factoryIcon));
                primaryStage.getIcons().add(new Image(FACTORY_ICON));
            } else {
                Image icon;
                if (CommonUtils.isSyncMode()) {
                    icon = new Image(PATH_SYNC_ICON);
                } else {
                    icon = new Image(PATH_ICON);
                }
                primaryStage.getIcons().add(icon);
            }
            String mac = CommonUtils.getMacAddress();
            appName += String.format(MSG_APP_TITLE, NAME_VERSION, CommonUtils.getVersion());
            LoggerUtils.info(mac);
            // 校验证书是否过期
            if (!CommonUtils.checkLicense(null)) {
                appName += STR_SPACE_2 + String.format(MSG_LICENSE_EXPIRE, appConfigDto.getLicense().getEffectiveDate());
            } else {
                // 过期前提醒
                String tips = CommonUtils.checkLicenseDate(appConfigDto);
                if (StringUtils.isNotBlank(tips)) {
                    appName += STR_SPACE_2 + tips;
                }
            }
            LoggerUtils.appStartInfo(String.format(BaseConst.MSG_CHECK, NAME_CONFIG_LICENSE_DATE));

            Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath(PATH_STARTER_FXML)));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(FileUtils.getFileUrl(PATH_STARTER_CSS).toExternalForm());
            primaryStage.setTitle(appName);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
            int width = displayMode.getWidth();
            int height = displayMode.getHeight();
            if (width < 1700) {
                primaryStage.setWidth(width);
            }
            if (height < 900) {
                primaryStage.setHeight(height - 100);
            }
            primaryStage.show();
            LoggerUtils.info(String.format(MSG_COMPLETE, NAME_APP_START));
            primaryStage.setOnCloseRequest(event -> {
                try {
                    primaryStage.close();
                    if (FileUtils.startByJar()) {
                        String processName = FileUtils.getJarName().replace(FILE_TYPE_JAR, FILE_TYPE_EXE);
                        processName = processName.substring(processName.lastIndexOf(STR_SLASH) + 1);
                        Runtime.getRuntime().exec(String.format(CMD_KILL_APP, processName));
                    }
                } catch (IOException e) {
                }
            });

            CommonUtils.deleteVersionFile(appCode);

            if (true) {
                return;
            }
            // 启用服务器
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (CommonUtils.isSuperUser()) {
                        HttpServerUtils.initServer(SERVER_URL, SERVER_PORT);
                    } else {
                        LoggerUtils.info(HttpRequestUtils.sendPost(SERVER_HTTP + STR_COLON + SERVER_PORT + SERVER_URL, KEY_VERSION + STR_EQUAL + CommonUtils.getVersion()));
                    }
                }
            }).start();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private static String getAppName(String appName) {
        if (StringUtils.isBlank(appName)) {
            return "TA小工具";
        }
        String[] ele = appName.trim().split(STR_SEMICOLON);
        if (ele.length != 1) {
            int min = 0;
            int max = ele.length - 1;
            int index = min + (int) (Math.random() * (max - min + 1));
            appName = ele[index];
        }
        return appName;
    }


}
