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

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description 应用启动
 * @package com.hoomoomoo.im
 * @date 2021/04/18
 */
public class MainStarter extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            LoggerUtils.info(STR_MSG_DIVIDE_LINE);
            LoggerUtils.info(String.format(STR_MSG_START, "应用启动"));
            FileUtils.unJar("/conf/app.conf");
            LoggerUtils.info(String.format(STR_MSG_UPDATE, "配置文件"));
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            LoggerUtils.info(String.format(STR_MSG_LOAD, "配置信息"));
            primaryStage.getIcons().add(new Image("/conf/image/icon.png"));
            Parent root = new FXMLLoader().load(new FileInputStream(FileUtils.getFilePath("/conf/fxml/starter.fxml")));
            Scene scene = new Scene(root);
            // 中文空格显示有问题
            scene.getStylesheets().add(FileUtils.getFilePathUrl("/conf/style/progressIndicator.css").toExternalForm());
            primaryStage.setTitle(appConfigDto.getAppName());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            LoggerUtils.info(String.format(STR_MSG_COMPLETE, "应用启动"));
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
