package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.FunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.FunctionConfig.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/27
 */
public class LoggerUtils {

    private final static String START = "开始";
    private final static String END = "结束";
    private final static String UNIT = "秒";
    private final static String COST = "耗时:";
    private final static String SVN_NUM = "svn版本个数:";
    private final static String FILE_NUM = "文件个数:";

    public static void info(String msg) {
        info(msg, true);
    }

    public static void info(String msg, boolean includeDate) {
        StringBuilder log = new StringBuilder();
        if (includeDate) {
            log.append(CommonUtils.getCurrentDateTime1(new Date()) + SYMBOL_SPACE);
        }
        log.append(msg).append(SYMBOL_NEXT_LINE);
        System.out.println(log);
        writeAppLog(msg, includeDate);
    }

    public static void info(Exception exception) {
        exception.printStackTrace();
        writeAppLog(exception);
    }

    public static void writeAppLog(String mgs, boolean includeDate) {
        try {
            String logFilePath = String.format(PATH_LOG, KEY_APP_LOG, CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            StringBuilder log = new StringBuilder();
            if (includeDate) {
                log.append(CommonUtils.getCurrentDateTime1(new Date())).append(SYMBOL_SPACE);
            }
            log.append(mgs).append(SYMBOL_NEXT_LINE_2);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeAppLog(Exception exception) {
        try {
            String logFilePath = String.format(PATH_LOG, KEY_APP_LOG, CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            StringBuilder log = new StringBuilder(CommonUtils.getCurrentDateTime1(new Date())).append(SYMBOL_NEXT_LINE);
            StackTraceElement[] stackTraceElements = exception.getStackTrace();
            for (int i = 0; i < stackTraceElements.length; i++) {
                log.append(getLineIndentation()).append(stackTraceElements[i].toString()).append(SYMBOL_NEXT_LINE);
            }
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeSvnLogInfo(Date startDate, List<LogDto> svnLogDtoList) {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, SVN_LOG.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            // 写日志文件
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getAppLogEnable()) {
                return;
            }
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_1)).append(SYMBOL_SPACE).append(START).append(SYMBOL_NEXT_LINE);
            int fileNum = 0;
            if (CollectionUtils.isNotEmpty(svnLogDtoList)) {
                for (LogDto item : svnLogDtoList) {
                    List<String> itemFile = item.getFile();
                    for (int i = 0; i < itemFile.size(); i++) {
                        fileNum++;
                        log.append(getLineIndentation()).append(itemFile.get(i));
                    }
                }
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_1)).append(SYMBOL_SPACE).append(END).append(SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_1)).append(SYMBOL_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(SYMBOL_SPACE);
            log.append(SVN_NUM).append(svnLogDtoList.size()).append(SYMBOL_SPACE);
            log.append(FILE_NUM).append(fileNum).append(SYMBOL_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, SVN_LOG.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeSvnUpdateInfo(Date startDate, List<String> filePath) {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, SVN_UPDATE.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getAppLogEnable()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_2)).append(SYMBOL_SPACE).append(START).append(SYMBOL_NEXT_LINE);
            for (String item : filePath) {
                log.append(getLineIndentation()).append(item).append(SYMBOL_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_2)).append(SYMBOL_SPACE).append(END).append(SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_2)).append(SYMBOL_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(SYMBOL_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, SVN_UPDATE.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeFundInfo(Date startDate, String filePath) {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, FUND_INFO.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getAppLogEnable()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_3)).append(SYMBOL_SPACE).append(START).append(SYMBOL_NEXT_LINE);
            log.append(getLineIndentation()).append(filePath).append(SYMBOL_NEXT_LINE);
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_3)).append(SYMBOL_SPACE).append(END).append(SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_3)).append(SYMBOL_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(SYMBOL_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, FUND_INFO.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeProcessInfo(Date startDate, List<String> filePathList) {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, PROCESS_INFO.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getAppLogEnable()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_4)).append(SYMBOL_SPACE).append(START).append(SYMBOL_NEXT_LINE);
            for (String item : filePathList) {
                log.append(getLineIndentation()).append(item).append(SYMBOL_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_4)).append(SYMBOL_SPACE).append(END).append(SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_4)).append(SYMBOL_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(SYMBOL_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, PROCESS_INFO.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeScriptUpdateInfo(Date startDate, List<String> filePath) {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, SCRIPT_UPDATE.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getAppLogEnable()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_5)).append(SYMBOL_SPACE).append(START).append(SYMBOL_NEXT_LINE);
            for (String item : filePath) {
                log.append(getLineIndentation()).append(item).append(SYMBOL_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_5)).append(SYMBOL_SPACE).append(END).append(SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(SYMBOL_SPACE);
            log.append(FunctionConfig.getName(STR_5)).append(SYMBOL_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(SYMBOL_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, SCRIPT_UPDATE.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath), log.toString(), true);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeSvnRealtimeStatInfo() {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, SVN_REALTIME_STAT.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeSvnHistoryStatInfo() {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, SVN_HISTORY_STAT.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    public static void writeConfigSetInfo() {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, CONFIG_SET.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);
        } catch (Exception e) {
            LoggerUtils.info(e);
        }
    }

    private static void writeStatFile(String filePath) throws IOException {
        File file = new File(filePath);
        String date = CommonUtils.getCurrentDateTime1();
        if (file.exists()) {
            // 修改文件
            List<String> content = FileUtils.readNormalFile(filePath, false);
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (i == 1) {
                    // 更新末次使用时间
                    content.set(i, new StringBuilder("末次使用时间: ").append(date).toString());
                }
                if (i == 2) {
                    // 更新使用次数
                    String[] line = item.split(SYMBOL_COLON);
                    if (line.length == 2) {
                        int times = Integer.valueOf(line[1].trim()) + 1;
                        content.set(i, new StringBuilder("使用次数: ").append(times).toString());
                    }
                }
            }
            FileUtils.writeFile(filePath, content, false);
        } else {
            // 重写文件
            StringBuilder statLog = new StringBuilder();
            statLog.append("首次使用时间: ").append(date).append(SYMBOL_NEXT_LINE);
            statLog.append("末次使用时间: ").append(date).append(SYMBOL_NEXT_LINE);
            statLog.append("使用次数: 1");
            FileUtils.writeFile(filePath, statLog.toString(), false);
        }
    }

    private static String getLineIndentation() {
        return SYMBOL_SPACE_3;
    }

}
