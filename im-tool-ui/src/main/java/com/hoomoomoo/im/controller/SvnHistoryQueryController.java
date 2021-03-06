package com.hoomoomoo.im.controller;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.SvnStatDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.LoggerUtils;
import com.hoomoomoo.im.utils.OutputUtils;
import com.hoomoomoo.im.utils.SvnUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2021/05/31
 */
public class SvnHistoryQueryController implements Initializable {

    @FXML
    private ProgressIndicator schedule;

    @FXML
    private Label scheduleText;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea stat1;

    @FXML
    private TextArea stat2;

    @FXML
    private TextArea stat3;

    @FXML
    private Label notice;

    private static int statNum = 3;

    private double progress = 0;

    @FXML
    void execute(ActionEvent event) {
        try {
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
                    OutputUtils.clearLog(stat1);
                    OutputUtils.clearLog(stat2);
                    OutputUtils.clearLog(stat3);
                    String selectDate = date.getValue().toString();
                    Date start = CommonUtils.getCurrentDateTime7(selectDate + " 00:00:00");
                    Date end = CommonUtils.getCurrentDateTime7(selectDate + " 23:59:59");
                    LinkedHashMap<String, SvnStatDto> svnStat = SvnUtils.getSvnLog(start, end, new LinkedHashMap<>(), false);
                    LinkedHashMap<String, String> userList = appConfigDto.getSvnStatUser();
                    int num = userList.size();
                    List<TextArea> statList = new ArrayList<>();
                    List<SvnStatDto> svnStatDtoList = new ArrayList<>();
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
                            OutputUtils.info(statList.get(index), SYMBOL_NEXT_LINE_2);
                        }
                        String userCode = iterator.next();
                        outputStatInfo(statList.get(index), svnStat.get(userCode));
                        index++;
                        svnStatDtoList.add(svnStat.get(userCode));
                    }
                    OutputUtils.info(notice, getOrderInfo(svnStatDtoList, appConfigDto));
                } catch (Exception e) {
                    LoggerUtils.info(e);
                } finally {
                }
                setProgress(1);
            } catch (Exception e) {
                LoggerUtils.info(e);
            }
        }).start();
    }

    private void updateProgress() {
        new Thread(() -> {
            while (true) {
                if (progress >= 0.95) {
                    break;
                }
                if (progress <= 0.6) {
                    setProgress(progress + 0.01);
                } else if (progress < 0.9) {
                    setProgress(progress + 0.005);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LoggerUtils.info(e);
                }
            }
        }).start();
    }

    private String getOrderInfo(List<SvnStatDto> svnStatDtoList, AppConfigDto appConfigDto) {
        StringBuilder order = new StringBuilder("提交代码次数排序:  ");
        if (STR_2.equals(appConfigDto.getSvnStatHistoryOrderType())) {
            order = new StringBuilder("修改文件个数排序:  ");
        }
        Collections.sort(svnStatDtoList, (o1, o2) -> {
            if (STR_1.equals(appConfigDto.getSvnStatHistoryOrderType())) {
                return o2.getSubmitTimes() - o1.getSubmitTimes();
            } else {
                return o2.getFileNum() - o1.getFileNum();
            }

        });
        for (int i = 0; i < svnStatDtoList.size(); i++) {
            SvnStatDto item = svnStatDtoList.get(i);
            if (i > 10) {
                break;
            }
            order.append(item.getUserName()).append(SYMBOL_SPACE_2);
        }
        return order.toString();

    }

    private void outputStatInfo(TextArea stat, SvnStatDto svnStatDto) {
        OutputUtils.info(stat, svnStatDto.getUserName() + SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, SYMBOL_SPACE_4 + "首次提交时间: " + svnStatDto.getFirstTime() + SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, SYMBOL_SPACE_4 + "末次提交时间: " + svnStatDto.getLastTime() + SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, SYMBOL_SPACE_4 + "提交代码次数: " + svnStatDto.getSubmitTimes() + SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, SYMBOL_SPACE_4 + "修改文件个数: " + svnStatDto.getFileNum() + SYMBOL_NEXT_LINE_2);
        OutputUtils.info(stat, SYMBOL_SPACE_4 + "修改文件次数: " + svnStatDto.getFileTimes() + SYMBOL_NEXT_LINE_2);
    }

    synchronized private void setProgress(double value) {
        try {
            progress = value;
            Platform.runLater(() -> {
                schedule.setProgress(progress);
                scheduleText.setText(String.valueOf(value * 100).split(SYMBOL_POINT_SLASH)[0] + SYMBOL_PERCENT);
                schedule.requestFocus();
            });
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        date.setValue(yesterday);
        execute(null);
    }
}
