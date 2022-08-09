package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import com.hoomoomoo.im.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.KEY_NOTICE;
import static com.hoomoomoo.im.consts.FunctionConfig.SVN_REALTIME_STAT;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/30
 */
public class SvnRealtimeStatController extends BaseController implements Initializable {

    @FXML
    private Label statTime;

    @FXML
    private Label costTime;

    @FXML
    private TableView stat;

    @FXML
    private Label notice;

    private Date startDate;

    private LinkedHashMap<String, SvnStatDto> svnStat = new LinkedHashMap<>(16);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_REALTIME_STAT.getName()));
            if (!TaCommonUtil.checkConfig(stat, SVN_REALTIME_STAT.getCode())) {
                return;
            }
            setProgress(0);
            updateProgress();
            stat();
            LoggerUtils.writeSvnRealtimeStatInfo();
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private void stat() {
        new Thread(() -> {
            try {
                AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
                while (true) {
                    try {
                        setProgress(0);
                        if (startDate == null) {
                            startDate = CommonUtils.getCurrentDateTime6("00:00:00");
                        }
                        Date date = new Date();
                        if (appConfigDto.getSvnStatReset()) {
                            if (!CommonUtils.getCurrentDateTime9(startDate).equals(CommonUtils.getCurrentDateTime9(date))) {
                                svnStat.clear();
                                LoggerUtils.writeSvnRealtimeStatInfo();
                            }
                        }
                        OutputUtils.info(statTime, CommonUtils.getCurrentDateTime8(date));
                        SvnUtils.getSvnLog(startDate, date, svnStat, true);
                        OutputUtils.info(notice, BaseConst.SYMBOL_SPACE);
                        OutputUtils.info(costTime, BaseConst.SYMBOL_SPACE);
                        OutputUtils.clearLog(stat);
                        List<SvnStatDto> svnStatDtoList = TaCommonUtil.sortSvnStatDtoList(appConfigDto, svnStat);
                        for (SvnStatDto item : svnStatDtoList) {
                            OutputUtils.info(stat, item);
                        }
                        startDate = date;
                        OutputUtils.info(notice, svnStat.get(KEY_NOTICE).getNotice());
                        OutputUtils.info(costTime, (System.currentTimeMillis() - date.getTime()) / 1000 + "秒");
                    } catch (Exception e) {
                        OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
                        LoggerUtils.info(e);
                    } finally {
                        setProgress(1);
                    }
                    Thread.sleep(appConfigDto.getSvnStatInterval() * 1000);
                }
            } catch (Exception e) {
                OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
                LoggerUtils.info(e);
            }
        }).start();
    }
}
