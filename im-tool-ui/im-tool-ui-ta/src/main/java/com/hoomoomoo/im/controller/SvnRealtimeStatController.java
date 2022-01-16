package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.BaseConst;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import com.hoomoomoo.im.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.*;

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
    private TextArea stat1;

    @FXML
    private TextArea stat2;

    @FXML
    private TextArea stat3;

    @FXML
    private Label notice;

    private Date startDate;

    private LinkedHashMap<String, SvnStatDto> svnStat = new LinkedHashMap<>(16);

    private static int statNum = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LoggerUtils.info(String.format(BaseConst.MSG_USE, SVN_REALTIME_STAT.getName()));
            if (!TaCommonUtil.checkConfig(stat1, SVN_REALTIME_STAT.getCode())) {
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
                        OutputUtils.clearLog(stat1);
                        OutputUtils.clearLog(stat2);
                        OutputUtils.clearLog(stat3);
                        LinkedHashMap<String, String> userList = appConfigDto.getSvnStatUser();
                        int num = userList.size();
                        List<TextArea> statList = new ArrayList<>();
                        for (int i = 1; i <= num; i++) {
                            int remainder = i % statNum;
                            switch (remainder) {
                                case 1:
                                    statList.add(stat1);
                                    break;
                                case 2:
                                    statList.add(stat2);
                                    break;
                                default:
                                    statList.add(stat3);
                                    break;
                            }
                        }
                        Iterator<String> iterator = userList.keySet().iterator();
                        int index = 0;
                        while (iterator.hasNext()) {
                            if (index >= statNum) {
                                OutputUtils.info(statList.get(index), BaseConst.SYMBOL_NEXT_LINE_2);
                            }
                            String userCode = iterator.next();
                            outputStatInfo(statList.get(index), svnStat.get(userCode));
                            index++;
                        }
                        startDate = date;
                        OutputUtils.info(notice, svnStat.get(BaseConst.KEY_NOTICE).getNotice());
                        OutputUtils.info(costTime, (System.currentTimeMillis() - date.getTime()) / 1000 + "秒");
                    } catch (Exception e) {
                        OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
                        LoggerUtils.info(e);
                    } finally {
                    }
                    setProgress(1);
                    Thread.sleep(appConfigDto.getSvnStatInterval() * 1000);
                }
            } catch (Exception e) {
                OutputUtils.info(notice, CommonUtils.getCurrentDateTime1() + BaseConst.SYMBOL_SPACE + ExceptionMsgUtils.getMsg(e));
                LoggerUtils.info(e);
            }
        }).start();
    }

    private void outputStatInfo(TextArea stat, SvnStatDto svnStatDto) {
        OutputUtils.info(stat, svnStatDto.getUserName() + BaseConst.SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, BaseConst.SYMBOL_SPACE_4 + "首次提交时间: " + svnStatDto.getFirstTime() + BaseConst.SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, BaseConst.SYMBOL_SPACE_4 + "末次提交时间: " + svnStatDto.getLastTime() + BaseConst.SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, BaseConst.SYMBOL_SPACE_4 + "提交代码次数: " + svnStatDto.getSubmitTimes() + BaseConst.SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, BaseConst.SYMBOL_SPACE_4 + "修改文件个数: " + svnStatDto.getFileNum() + BaseConst.SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, BaseConst.SYMBOL_SPACE_4 + "修改文件次数: " + svnStatDto.getFileTimes() + BaseConst.SYMBOL_NEXT_LINE_2);
    }
}