package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.VersionDto;
import com.hoomoomoo.im.utils.*;
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

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;


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

        TableColumn code = new TableColumn<>("版本编号");
        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        code.setPrefWidth(300);

        TableColumn meomo = new TableColumn<>("版本说明");
        meomo.setCellValueFactory(new PropertyValueFactory<>("memo"));
        meomo.setPrefWidth(370);

        TableColumn clientName = new TableColumn<>("客户名称");
        clientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientName.setEditable(false);
        clientName.setPrefWidth(330);

        TableColumn closeDate = new TableColumn<>("封版时间");
        closeDate.setCellValueFactory(new PropertyValueFactory<>("closeDate"));
        closeDate.setPrefWidth(120);
        closeDate.setEditable(true);
        closeDate.setStyle(STYLE_CENTER);
        closeDate.setOnEditCommit(event -> {
            int index = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getTablePosition().getRow();
            VersionDto item = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getTableView().getItems().get(index);
            String close = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getNewValue().toString();
            item.setCloseDate(close);
            writeExtendFile(item, close, null);
        });

        TableColumn publishDate = new TableColumn<>("发版时间");
        publishDate.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        publishDate.setPrefWidth(120);
        publishDate.setEditable(true);
        publishDate.setStyle(STYLE_CENTER);
        publishDate.setOnEditCommit(event -> {
            int index = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getTablePosition().getRow();
            VersionDto item = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getTableView().getItems().get(index);
            String publish = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getNewValue().toString();
            item.setPublishDate(publish);
            writeExtendFile(item, null, publish);
        });

        TableColumn closeInterval = new TableColumn<>("封版截止");
        closeInterval.setCellValueFactory(new PropertyValueFactory<>("closeInterval"));
        closeInterval.setPrefWidth(120);
        closeInterval.setEditable(false);
        closeInterval.setStyle(STYLE_CENTER);

        TableColumn publishInterval = new TableColumn<>("发版截止");
        publishInterval.setCellValueFactory(new PropertyValueFactory<>("publishInterval"));
        publishInterval.setPrefWidth(120);
        publishInterval.setStyle(STYLE_CENTER);

        table.getColumns().addAll(code, meomo, clientName, closeDate, publishDate, closeInterval, publishInterval);
        showVersion(versionDtoList);
    }

    private void showVersion(List<VersionDto> versionDtoList) {
        OutputUtils.clearLog(table);
        int currentDate = Integer.valueOf(CommonUtils.getCurrentDateTime3());
        versionDtoList.sort(new Comparator<VersionDto>() {
            @Override
            public int compare(VersionDto o1, VersionDto o2) {
                int closeDate1 = Integer.valueOf(o1.getCloseDate());
                int closeDate2 = Integer.valueOf(o2.getCloseDate());
                int publishDate1 = Integer.valueOf(o1.getPublishDate());
                int publishDate2 = Integer.valueOf(o2.getPublishDate());
                if (closeDate1 < currentDate) {
                    closeDate1 = 20991231;
                }
                if (closeDate2 < currentDate) {
                    closeDate2 = 20991231;
                }
                if (publishDate1 < currentDate) {
                    publishDate1 = 20991231;
                }
                if (publishDate2 < currentDate) {
                    publishDate2 = 20991231;
                }
                int num = closeDate1 - closeDate2;
                if (num == 0) {
                    num = publishDate1 - publishDate2;
                }
                return num;
            }
        });
        OutputUtils.infoList(table, versionDtoList, false);
    }

    private void writeExtendFile(VersionDto versionDto, String closeDate, String publishDate) {
        List<String> versionList = new ArrayList<>();
        try {
            versionList = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_VERSION_EXTEND_STAT), false);
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
        boolean hasRecord = false;
        String versionCode = versionDto.getCode();
        String close = versionDto.getCloseDate();
        String publish = versionDto.getPublishDate();
        if (CollectionUtils.isNotEmpty(versionList)) {
            for (int i=0; i<versionList.size(); i++) {
                String item = versionList.get(i);
                String[] elementList = item.split(STR_SEMICOLON);
                String version = elementList[0];
                close = elementList[1];
                publish = elementList[2];
                if (versionCode.equals(version)) {
                    hasRecord = true;
                    if (closeDate != null) {
                        close = closeDate;
                    }
                    if (publishDate != null) {
                        publish = publishDate;
                    }
                    versionList.set(i, versionCode + STR_SEMICOLON +  close + STR_SEMICOLON + publish);
                }
            }
        }
        if (!hasRecord) {
            if (closeDate != null) {
                close = closeDate;
            }
            if (publishDate != null) {
                publish = publishDate;
            }
            versionList.add(versionCode + STR_SEMICOLON +  close + STR_SEMICOLON + publish);
        }
        String statPath = FileUtils.getFilePath(PATH_VERSION_EXTEND_STAT);
        try {
            FileUtils.writeFile(statPath, versionList, false);
        } catch (IOException e) {
            LoggerUtils.info(e);
        }

        List<VersionDto> versionDtoList = new ArrayList<>();
        try {
            versionDtoList = HepTaskTodoController.getVersionInfo();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
        showVersion(versionDtoList);
    }

}
