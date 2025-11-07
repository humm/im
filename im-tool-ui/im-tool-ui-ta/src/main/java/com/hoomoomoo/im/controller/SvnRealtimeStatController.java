package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import com.hoomoomoo.im.task.SvnRealtimeStatTask;
import com.hoomoomoo.im.task.SvnRealtimeStatTaskParam;
import com.hoomoomoo.im.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.KEY_NOTICE;
import static com.hoomoomoo.im.consts.BaseConst.STR_TRUE;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.SVN_REALTIME_STAT;

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

    private boolean initFlag = true;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_REALTIME_STAT.getName()));
            if (!TaCommonUtils.checkConfig(notice, SVN_REALTIME_STAT.getCode())) {
                return;
            }
            setProgress(0);
            updateProgress();
            TaskUtils.execute(new SvnRealtimeStatTask(new SvnRealtimeStatTaskParam(this)));
            LoggerUtils.writeSvnRealtimeStatInfo();
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    public void stat() {
        try {
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            while (true) {
                if (!initFlag && CommonUtils.getOpenTab(appConfigDto.getTabPane(), MenuFunctionConfig.FunctionConfig.SVN_REALTIME_STAT) == null) {
                    LoggerUtils.info(SVN_REALTIME_STAT.getName() + "已关闭，停止自动刷新");
                    break;
                }
                initFlag = false;
                try {
                    setProgress(0);
                    if (startDate == null) {
                        startDate = CommonUtils.getCurrentDateTime6("00:00:00");
                    }
                    Date date = new Date();
                    if (StringUtils.equals(appConfigDto.getSvnStatReset(), STR_TRUE)) {
                        if (!CommonUtils.getCurrentDateTime9(startDate).equals(CommonUtils.getCurrentDateTime9(date))) {
                            svnStat.clear();
                            LoggerUtils.writeSvnRealtimeStatInfo();
                        }
                    }
                    OutputUtils.info(statTime, CommonUtils.getCurrentDateTime8(date));
                    SvnUtils.getSvnLog(startDate, date, svnStat, false);
                    OutputUtils.info(notice, BaseConst.STR_SPACE);
                    OutputUtils.info(costTime, BaseConst.STR_SPACE);
                    OutputUtils.clearLog(stat);
                    List<SvnStatDto> svnStatDtoList = TaCommonUtils.sortSvnStatDtoList(appConfigDto, svnStat);
                    for (SvnStatDto item : svnStatDtoList) {
                        OutputUtils.info(stat, item);
                    }
                    startDate = date;
                    OutputUtils.info(notice, svnStat.get(KEY_NOTICE).getNotice());
                    OutputUtils.info(costTime, (System.currentTimeMillis() - date.getTime()) / 1000 + "秒");
                } catch (Exception e) {
                    OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e));
                    LoggerUtils.error(e);
                } finally {
                    setProgress(1);
                }
                Thread.sleep(appConfigDto.getSvnStatInterval() * 1000);
            }
        } catch (Exception e) {
            OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.STR_SPACE + ExceptionMsgUtils.getMsg(e));
            LoggerUtils.error(e);
        }
    }
}
