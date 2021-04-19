package com.hoomoomoo.im;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * @author humm23693
 * @description 应用启动
 * @package com.hoomoomoo.im
 * @date 2021/04/18
 */
public class MainStarter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image("image/icon.png"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/starter.fxml"));
        primaryStage.setTitle("开发助手");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            //设置对话框标题
            alert.setTitle("退出");
            //设置内容
            alert.setHeaderText("确定要退出吗？");
            //显示对话框
            Optional<ButtonType> result = alert.showAndWait();
            //如果点击OK
            if (result.get() == ButtonType.OK) {
                primaryStage.close();
                //否则
            } else {
                event.consume();
            }
        });
    }
}
