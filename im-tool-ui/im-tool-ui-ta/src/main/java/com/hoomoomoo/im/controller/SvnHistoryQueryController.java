package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import com.hoomoomoo.im.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SVN_HISTORY_STAT;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/31
 */
public class SvnHistoryQueryController extends BaseController implements Initializable {

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private TableView stat;

    @FXML
    private Label notice;

    @FXML
    void execute(ActionEvent event) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_HISTORY_STAT.getName()));
            if (!TaCommonUtils.checkConfig(notice, MenuFunctionConfig.FunctionConfig.SVN_REALTIME_STAT.getCode())) {
                return;
            }
            setProgress(0);
            updateProgress();
            stat();
            LoggerUtils.writeSvnHistoryStatInfo();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void stat() {
        new Thread(() -> {
            try {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                try {
                    setProgress(0);
                    String startSelected = dateStart.getValue().toString();
                    String endSelected = dateEnd.getValue().toString();
                    Date start = CommonUtils.getCurrentDateTime7(startSelected + " 00:00:00");
                    Date end = CommonUtils.getCurrentDateTime7(endSelected + " 23:59:59");
                    OutputUtils.clearLog(stat);
                    LinkedHashMap<String, SvnStatDto> svnStat = SvnUtils.getSvnLog(start, end, new LinkedHashMap<>(), false);
                    List<SvnStatDto> svnStatDtoList = TaCommonUtils.sortSvnStatDtoList(appConfigDto, svnStat);
                    for (SvnStatDto item : svnStatDtoList) {
                        OutputUtils.info(stat, item);
                    }
                    OutputUtils.info(notice, getOrderInfo(svnStatDtoList, appConfigDto));
                } catch (Exception e) {
                    OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e));
                    LoggerUtils.info(e);
                } finally {
                    setProgress(1);
                }
            } catch (Exception e) {
                OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e));
                LoggerUtils.info(e);
            }
        }).start();
    }

    private String getOrderInfo(List<SvnStatDto> svnStatDtoList, AppConfigDto appConfigDto) {
        StringBuilder order = new StringBuilder("提交代码次数排序:  ");
        if (BaseConst.STR_2.equals(appConfigDto.getSvnStatHistoryOrderType())) {
            order = new StringBuilder("修改文件个数排序:  ");
        }
        for (int i = 0; i < svnStatDtoList.size(); i++) {
            SvnStatDto item = svnStatDtoList.get(i);
            if (i > 10) {
                break;
            }
            order.append(item.getUserName()).append(BaseConst.STR_SPACE_2);
        }
        return order.toString();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        dateStart.setValue(yesterday);
        dateEnd.setValue(yesterday);
        execute(null);
    }
}
