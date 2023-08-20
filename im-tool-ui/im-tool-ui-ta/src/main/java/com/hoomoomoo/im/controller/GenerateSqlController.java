package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import com.hoomoomoo.im.utils.TaCommonUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.GENERATE_SQL;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/10/15
 */
public class GenerateSqlController extends BaseController implements Initializable {

    @FXML
    private AnchorPane generateSql;

    @FXML
    private TextField databaseNum;

    @FXML
    private TextField tableNum;

    @FXML
    private TextField databaseCode;

    @FXML
    private TextField tableCode;

    @FXML
    private TextArea column;

    @FXML
    private TextArea query;

    @FXML
    private RadioButton select;

    @FXML
    private RadioButton update;

    @FXML
    private RadioButton delete;

    @FXML
    private RadioButton truncate;

    @FXML
    private TextArea sql;

    @FXML
    private Button execute;

    @FXML
    void selectSelect(ActionEvent event) {
        OutputUtils.selected(select, true);
        OutputUtils.selected(update, false);
        OutputUtils.selected(delete, false);
        OutputUtils.selected(truncate, false);
    }

    @FXML
    void selectUpdate(ActionEvent event) {
        OutputUtils.selected(select, false);
        OutputUtils.selected(update, true);
        OutputUtils.selected(delete, false);
        OutputUtils.selected(truncate, false);
    }

    @FXML
    void selectDelete(ActionEvent event) {
        OutputUtils.selected(select, false);
        OutputUtils.selected(update, false);
        OutputUtils.selected(delete, true);
        OutputUtils.selected(truncate, false);
    }

    @FXML
    void selectTruncate(ActionEvent event) {
        OutputUtils.selected(select, false);
        OutputUtils.selected(update, false);
        OutputUtils.selected(delete, false);
        OutputUtils.selected(truncate, true);
    }

    @FXML
    void copy(ActionEvent event) {
        schedule.requestFocus();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sql.getText()), null);
    }

    @FXML
    void execute(ActionEvent event) {
        try {
            OutputUtils.clearLog(sql);
            LoggerUtils.info(String.format(BaseConst.MSG_USE, GENERATE_SQL.getName()));
            if (!TaCommonUtils.checkConfig(sql, GENERATE_SQL.getCode())) {
                return;
            }
            setProgress(0);
            generateSql();
            updateProgress();
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(sql, e.getMessage());
        }
    }

    private void generateSql() throws Exception {

        new Thread(() -> {
            try {
                execute.setDisable(true);
                List<String> fileLog = new ArrayList<>();
                Date currentDate = new Date();
                int database = Integer.valueOf(databaseNum.getText().trim());
                int table = Integer.valueOf(tableNum.getText().trim());
                String databaseCodeCurrent = databaseCode.getText().trim();
                String tableCodeCurrent = tableCode.getText().trim();
                String columnCurrent = column.getText().trim().replaceAll("\\s+", STR_SPACE);
                String queryCurrent = query.getText().trim().replaceAll("\\s+", STR_SPACE);
                if (StringUtils.isBlank(databaseCodeCurrent)) {
                    OutputUtils.info(sql, String.format(MSG_SET, "分库代码"));
                    setProgress(-1);
                    return;
                }
                if (StringUtils.isBlank(tableCodeCurrent)) {
                    OutputUtils.info(sql, String.format(MSG_SET, "分表代码"));
                    setProgress(-1);
                    return;
                }
                List<String> contentList = new ArrayList<>();
                String sqlType = getSqlType();
                if (database > 0 && table > 0) {
                    for (int i=1; i<=database; i++) {
                        inner: for (int j=1; j<=table; j++) {
                            StringBuilder content = new StringBuilder();
                            if (STR_1.equals(sqlType)) {
                                content.append("select '" + i + "' 分库号, '" + j + "' 分表号, " + columnCurrent + " t.* from " + databaseCodeCurrent + i + "." + tableCodeCurrent + j + " t " + queryCurrent);
                                fileLog.add(content.toString());
                                contentList.add(content.append(STR_NEXT_LINE).toString());
                                if (j != table) {
                                    fileLog.add("union all");
                                    contentList.add("union all" + STR_NEXT_LINE);
                                }
                                continue inner;
                            } else if (STR_2.equals(sqlType)) {
                                content.append("update " + databaseCodeCurrent + i + "." + tableCodeCurrent + j + " t set " + columnCurrent + " " + queryCurrent + ";");
                            } else if (STR_3.equals(sqlType)) {
                                content.append("delete from " + databaseCodeCurrent + i + "." + tableCodeCurrent + j + " t " + queryCurrent + ";");
                            } else if (STR_4.equals(sqlType)) {
                                content.append("truncate table " + databaseCodeCurrent + i + "." + tableCodeCurrent + j + ";");
                            }
                            fileLog.add(content.toString());
                            contentList.add(content.append(STR_NEXT_LINE).toString());
                        }
                        if (STR_1.equals(sqlType)) {
                            if (i != database) {
                                fileLog.add("union all");
                                contentList.add("union all" + STR_NEXT_LINE);
                            }
                        }
                    }
                }
                OutputUtils.info(sql, contentList);
                LoggerUtils.writeLogInfo(GENERATE_SQL.getCode(), currentDate, fileLog);
                setProgress(1);
            } catch (Exception e) {
                setProgress(-1);
                LoggerUtils.info(e);
                OutputUtils.info(sql, e.getMessage());
            } finally {
                execute.setDisable(false);
            }
        }).start();
    }

    private String getSqlType() {
        if (select.isSelected()) {
            return String.valueOf(select.getUserData());
        } else if (update.isSelected()) {
            return String.valueOf(update.getUserData());
        } else if (delete.isSelected()) {
            return String.valueOf(delete.getUserData());
        } else {
            return String.valueOf(truncate.getUserData());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppConfigDto appConfigDto = null;
        try {
            appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();

            if (StringUtils.isNotBlank(appConfigDto.getGenerateSqlDatabaseNum())) {
                OutputUtils.info(databaseNum, appConfigDto.getGenerateSqlDatabaseNum());
            }

            if (StringUtils.isNotBlank(appConfigDto.getGenerateSqlTableNum())) {
                OutputUtils.info(tableNum, appConfigDto.getGenerateSqlTableNum());
            }

            if (StringUtils.isNotBlank(appConfigDto.getGenerateSqlDatabaseCode())) {
                OutputUtils.info(databaseCode, appConfigDto.getGenerateSqlDatabaseCode());
            }

            if (StringUtils.isNotBlank(appConfigDto.getGenerateSqlTableCode())) {
                OutputUtils.info(tableCode, appConfigDto.getGenerateSqlTableCode());
            }

            String sqlType = appConfigDto.getGenerateSqlType();
            if (STR_1.equals(sqlType)) {
                selectSelect(null);
            } else if (BaseConst.STR_2.equals(sqlType)) {
                selectUpdate(null);
            } else if (BaseConst.STR_3.equals(sqlType)) {
                selectDelete(null);
            } else if (BaseConst.STR_4.equals(sqlType)) {
                selectTruncate(null);
            }
        } catch (Exception e) {
            LoggerUtils.info(e);
            OutputUtils.info(sql, e.getMessage());
        }
    }
}
