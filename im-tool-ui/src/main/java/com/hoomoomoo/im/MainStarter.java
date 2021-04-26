package com.hoomoomoo.im;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author humm23693
 * @description 应用启动
 * @package com.hoomoomoo.im
 * @date 2021/04/18
 */
public class MainStarter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileUtils.UnJar("/conf/app.conf");
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfig();
        primaryStage.getIcons().add(new Image("/conf/image/icon.png"));
        Parent root = FXMLLoader.load(FileUtils.getFilePath("/conf/fxml/starter.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FileUtils.getFilePath("/conf/style/progressIndicator.css").toExternalForm());
        primaryStage.setTitle(appConfigDto.getAppName());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
