package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.KEY_NOTICE;
import static com.hoomoomoo.im.consts.BaseConst.SYMBOL_EMPTY;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_DAY_STAT;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_REALTIME_STAT;

/**
 * @author humm23693
 * @description 功能废弃 无实际使用场景
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/30
 */
public class SvnDayStatController extends BaseController implements Initializable {

    @FXML
    private Button statButton;

    @FXML
    private Button viewButton;

    @FXML
    private TextArea statLog;

    private int maxVersionConfig = 0;

    @FXML
    void executeStat(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_DAY_STAT.getName()));
            if (!TaCommonUtil.checkConfig(statLog, SVN_DAY_STAT.getCode())) {
                return;
            }
            setProgress(0);
            updateProgress();
            stat();
            LoggerUtils.writeSvnDayStatInfo();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @FXML
    void executeView(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            maxVersionConfig = Integer.valueOf(appConfigDto.getSvnMaxRevision());
        }catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void stat() {
        new Thread(() -> {
            try {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                try {
                    statButton.setDisable(true);
                    viewButton.setDisable(true);
                    OutputUtils.clearLog(statLog);
                    appConfigDto.setSvnMaxRevision(appConfigDto.getSvnTaskMaxRevision());
                    Iterator<String> iterator = appConfigDto.getSvnUrl().keySet().iterator();
                    List<LogDto> logDtoList = new ArrayList();
                    while (iterator.hasNext()) {
                        appConfigDto.setSvnRep(iterator.next());
                        logDtoList.addAll(SvnUtils.getSvnLog(0, SYMBOL_EMPTY));
                    }
                    if (CollectionUtils.isEmpty(logDtoList)) {
                        OutputUtils.info(statLog, CommonUtils.getCurrentDateTime1() + " 未获取到相关提交记录\n");
                    } else {
                        Collections.sort(logDtoList, new Comparator<LogDto>() {
                            @Override
                            public int compare(LogDto o1, LogDto o2) {
                                return o1.compareTo(o2);
                            }
                        });



                    }
                } catch (Exception e) {
                    OutputUtils.info(statLog, ExceptionMsgUtils.getMsg(e));
                    LoggerUtils.info(e);
                } finally {
                    appConfigDto.setSvnMaxRevision(String.valueOf(maxVersionConfig));
                    setProgress(1);
                    statButton.setDisable(false);
                    viewButton.setDisable(false);
                }
            } catch (Exception e) {
                OutputUtils.info(statLog, ExceptionMsgUtils.getMsg(e));
                LoggerUtils.info(e);
            }
        }).start();
    }
}
