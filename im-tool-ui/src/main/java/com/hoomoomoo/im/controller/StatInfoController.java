package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.consts.FunctionType;
import com.hoomoomoo.im.utils.FileUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;

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

    @FXML
    private TextArea svnLog;

    @FXML
    private TextArea svnUpdate;

    @FXML
    private TextArea fundInfo;

    @FXML
    private TextArea processInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> svnLogStat = new ArrayList<>(3);
        List<String> svnUpdateStat = new ArrayList<>(3);
        List<String> fundInfoStat = new ArrayList<>(3);
        List<String> processInfoStat = new ArrayList<>(3);
        try {
            svnLogStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/svnLog/00000000.log").getPath(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            svnUpdateStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/svnUpdate/00000000.log").getPath(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fundInfoStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/fundInfo/00000000.log").getPath(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            processInfoStat = FileUtils.readNormalFile(FileUtils.getFilePath("/logs/processInfo/00000000.log").getPath(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputUtils.clearLog(svnLogStat);
        OutputUtils.clearLog(svnUpdateStat);
        OutputUtils.clearLog(fundInfoStat);
        OutputUtils.clearLog(processInfoStat);

        OutputUtils.info(svnLog, FunctionType.getName(STR_1) + STR_NEXT_LINE_2);
        OutputUtils.info(svnUpdate, FunctionType.getName(STR_2) + STR_NEXT_LINE_2);
        OutputUtils.info(fundInfo, FunctionType.getName(STR_3) + STR_NEXT_LINE_2);
        OutputUtils.info(processInfo, FunctionType.getName(STR_4) + STR_NEXT_LINE_2);

        if (CollectionUtils.isNotEmpty(svnLogStat)) {
            for (String item : svnLogStat) {
                OutputUtils.info(svnLog, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(svnUpdateStat)) {
            for (String item : svnUpdateStat) {
                OutputUtils.info(svnUpdate, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(fundInfoStat)) {
            for (String item : fundInfoStat) {
                OutputUtils.info(fundInfo, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }
        if (CollectionUtils.isNotEmpty(processInfoStat)) {
            for (String item : processInfoStat) {
                OutputUtils.info(processInfo, STR_SPACE_4 + item + STR_NEXT_LINE_2);
            }
        }

    }
}
