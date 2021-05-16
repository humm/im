package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.consts.FunctionType;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/09
 */
public class StatInfoController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(StatInfoController.class);


    @FXML
    private TextArea stat1;

    @FXML
    private TextArea stat2;

    @FXML
    private TextArea stat3;

    @FXML
    private TextArea stat4;

    @FXML
    private TextArea stat5;

    @FXML
    private TextArea stat6;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> svnLogStat = new ArrayList<>(3);
        List<String> svnUpdateStat = new ArrayList<>(3);
        List<String> fundInfoStat = new ArrayList<>(3);
        List<String> processInfoStat = new ArrayList<>(3);
        List<String> scriptUpdateStat = new ArrayList<>(3);
        try {
            svnLogStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/svnLog/00000000.log").getPath(), false);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        try {
            svnUpdateStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/svnUpdate/00000000.log").getPath(), false);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        try {
            fundInfoStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/fundInfo/00000000.log").getPath(), false);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        try {
            processInfoStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/processInfo/00000000.log").getPath(), false);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        try {
            scriptUpdateStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/scriptUpdate/00000000.log").getPath(), false);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }

        OutputUtils.clearLog(svnLogStat);
        OutputUtils.clearLog(svnUpdateStat);
        OutputUtils.clearLog(fundInfoStat);
        OutputUtils.clearLog(processInfoStat);
        OutputUtils.clearLog(scriptUpdateStat);

        OutputUtils.info(stat1, FunctionType.getName(STR_1) + STR_NEXT_LINE_2);
        OutputUtils.info(stat2, FunctionType.getName(STR_2) + STR_NEXT_LINE_2);
        OutputUtils.info(stat3, FunctionType.getName(STR_3) + STR_NEXT_LINE_2);
        OutputUtils.info(stat4, FunctionType.getName(STR_4) + STR_NEXT_LINE_2);
        OutputUtils.info(stat5, FunctionType.getName(STR_5) + STR_NEXT_LINE_2);

        if (CollectionUtils.isNotEmpty(svnLogStat)) {
            for (String item : svnLogStat) {
                OutputUtils.info(stat1, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(svnUpdateStat)) {
            for (String item : svnUpdateStat) {
                OutputUtils.info(stat2, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(fundInfoStat)) {
            for (String item : fundInfoStat) {
                OutputUtils.info(stat3, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(processInfoStat)) {
            for (String item : processInfoStat) {
                OutputUtils.info(stat4, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(scriptUpdateStat)) {
            for (String item : scriptUpdateStat) {
                OutputUtils.info(stat5, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
    }
}
