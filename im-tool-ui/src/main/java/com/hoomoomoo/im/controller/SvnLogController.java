package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.dto.SvnLogDto;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.SvnUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.tmatesoft.svn.core.SVNException;

import java.util.List;

/**
 * @author humm23693
 * @description svn提交文件记录
 * @package com.hoomoomoo.im.controller
 * @date 2021/04/18
 */
public class SvnLogController {

    @FXML
    private TextField svnName;

    @FXML
    private TextField svnTimes;

    @FXML
    private Button svnSubmit;

    @FXML
    private TableView<?> svnLog;

    @FXML
    private TextArea fileLog;

    @FXML
    void executeSubmit(ActionEvent event) throws SVNException {
        String name = svnName.getText();
        LoggerUtils.clearLog(svnLog);
        LoggerUtils.clearLog(fileLog);
        List<SvnLogDto> logDtoList = SvnUtils.getSvnLog(Integer.valueOf(svnTimes.getText()));
        for (SvnLogDto svnLogDto : logDtoList) {
            LoggerUtils.info(svnLog, svnLogDto);
            LoggerUtils.info(fileLog, svnLogDto.getFile());
        }
    }

}
