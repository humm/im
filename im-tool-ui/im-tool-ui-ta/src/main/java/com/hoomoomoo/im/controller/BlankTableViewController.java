package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.VersionDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
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
        code.setPrefWidth(320);

        TableColumn closeDate = new TableColumn<>("封版时间");
        closeDate.setCellValueFactory(new PropertyValueFactory<>("closeDate"));
        closeDate.setPrefWidth(150);
        closeDate.setEditable(true);
        closeDate.setStyle(STYLE_CENTER);

        TableColumn publishDate = new TableColumn<>("发版时间");
        publishDate.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        publishDate.setPrefWidth(150);
        publishDate.setEditable(true);
        publishDate.setStyle(STYLE_CENTER);

        /*TableColumn orderNo = new TableColumn<>("指定排序");
        orderNo.setCellValueFactory(new PropertyValueFactory<>("orderNo"));
        orderNo.setPrefWidth(100);
        orderNo.setEditable(true);
        orderNo.setStyle(STYLE_CENTER);
        orderNo.setOnEditCommit(event -> {
            int index = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getTablePosition().getRow();
            VersionDto item = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getTableView().getItems().get(index);
            String order = ((TableColumn.CellEditEvent<VersionDto, Object>) event).getNewValue().toString();
            item.setOrderNo(order);
            writeExtendFile(item);
        });*/

        TableColumn closeInterval = new TableColumn<>("封版截止");
        closeInterval.setCellValueFactory(new PropertyValueFactory<>("closeInterval"));
        closeInterval.setPrefWidth(100);
        closeInterval.setEditable(false);
        closeInterval.setStyle(STYLE_CENTER);

        TableColumn publishInterval = new TableColumn<>("发版截止");
        publishInterval.setCellValueFactory(new PropertyValueFactory<>("publishInterval"));
        publishInterval.setPrefWidth(100);
        publishInterval.setStyle(STYLE_CENTER);

        table.getColumns().addAll(code, closeDate, publishDate, closeInterval, publishInterval);
        showVersion(versionDtoList);

        close.setLayoutX(400);
    }

    private void showVersion(List<VersionDto> versionDtoList) {
        OutputUtils.clearLog(table);
        for (VersionDto versionDto : versionDtoList) {
            String code = versionDto.getCode();
            StringBuilder customCode = new StringBuilder();
            for (int i=0; i<code.length(); i++) {
                String ele = String.valueOf(code.charAt(i));
                if (CommonUtils.isNumber(ele)) {
                    customCode.append(ele);
                }
            }
            versionDto.setCustomOrderNo(customCode.toString());
        }
        versionDtoList.sort(new Comparator<VersionDto>() {
            @Override
            public int compare(VersionDto o1, VersionDto o2) {
                return o1.getCustomOrderNo().compareTo(o2.getCustomOrderNo());
            }
        });
        OutputUtils.infoList(table, versionDtoList, false);
    }

}
