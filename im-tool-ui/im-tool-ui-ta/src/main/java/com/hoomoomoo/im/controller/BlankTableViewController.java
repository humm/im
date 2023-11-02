package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.VersionDto;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.STYLE_CENTER;


/**
 * @author humm23693
 * @description
 * @package com.hoomoomoo.im.controller
 * @date 2021/9/16
 */
public class BlankTableViewController implements Initializable {

    @FXML
    private TableView table;

    @FXML
    private Button close;

    @FXML
    void onClose(ActionEvent event) throws Exception {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        appConfigDto.getChildStage().close();
        appConfigDto.setChildStage(null);
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
        List<VersionDto> versionDtoList = appConfigDto.getVersionDtoList();
        OutputUtils.clearLog(table);
        TableColumn code = new TableColumn<>("版本编号");
        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        code.setPrefWidth(300);

        TableColumn meomo = new TableColumn<>("版本说明");
        meomo.setCellValueFactory(new PropertyValueFactory<>("memo"));
        meomo.setPrefWidth(370);

        TableColumn closeDate = new TableColumn<>("封版时间");
        closeDate.setCellValueFactory(new PropertyValueFactory<>("closeDate"));
        closeDate.setPrefWidth(120);
        closeDate.setStyle(STYLE_CENTER);

        TableColumn publishDate = new TableColumn<>("发版时间");
        publishDate.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        publishDate.setPrefWidth(120);
        publishDate.setStyle(STYLE_CENTER);

        TableColumn closeInterval = new TableColumn<>("封版截止");
        closeInterval.setCellValueFactory(new PropertyValueFactory<>("closeInterval"));
        closeInterval.setPrefWidth(120);
        closeInterval.setStyle(STYLE_CENTER);

        TableColumn publishInterval = new TableColumn<>("发版截止");
        publishInterval.setCellValueFactory(new PropertyValueFactory<>("publishInterval"));
        publishInterval.setPrefWidth(120);
        publishInterval.setStyle(STYLE_CENTER);

        TableColumn clientName = new TableColumn<>("客户名称");
        clientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientName.setPrefWidth(330);

        table.getColumns().addAll(code, meomo, closeDate, publishDate, closeInterval, publishInterval, clientName);
        if (CollectionUtils.isNotEmpty(versionDtoList)) {
            Platform.runLater(() -> {
                for (VersionDto item : versionDtoList)  {
                    table.getItems().add(item);
                    // 设置行
                    initRowColor(table);
                }
            });
        }
    }

    private void initRowColor(TableView table) {
        table.setRowFactory(new Callback<TableView<VersionDto>, TableRow<VersionDto>>(){
            @Override
            public TableRow<VersionDto> call(TableView<VersionDto> param) {
                final TableRow<VersionDto> row = new TableRow<VersionDto>() {
                    @Override
                    protected void updateItem(VersionDto item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null && getIndex() > -1){
                            int closeInterval =  Integer.valueOf(item.getCloseInterval());
                            int publishInterval =  Integer.valueOf(item.getPublishInterval());
                            if ((closeInterval >= 0 && closeInterval <= 3) || publishInterval <= 3) {
                                setStyle("-fx-text-background-color: blue;");
                            } else if (closeInterval <= 7 || publishInterval <= 7) {
                                setStyle("-fx-text-background-color: #804000;");
                            } else {
                                setStyle("-fx-text-background-color: black;");
                            }
                        }
                    }
                };
                return row;
            }
        });
    }
}
