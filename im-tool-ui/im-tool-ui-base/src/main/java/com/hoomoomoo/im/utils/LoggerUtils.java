package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.dto.LogDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.consts.MenuFunctionConfig.FunctionConfig.*;

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

    public static void appStartInfo(String msg) {
        try {
            if (CommonUtils.isSuperUser()) {
                info(msg, true);
            }
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    public static void info(String msg, boolean includeDate) {
        StringBuilder log = new StringBuilder();
        if (includeDate) {
            log.append(CommonUtils.getCurrentDateTime1(new Date()) + STR_SPACE);
        }
        log.append(msg).append(STR_NEXT_LINE);
        System.out.print(log);
        writeAppLog(msg, includeDate);
    }

    public static void info(String msg) {
        info(msg, true);
    }

    public static void error(String msg) {
        info(msg, true);
    }

    public static void error(String msg, boolean includeDate) {
        info(msg, includeDate);
    }

    public static void error(Throwable e) {
        e.printStackTrace();
        writeAppLog(e.toString(), e.getStackTrace());
    }

    public static void error(Exception e) {
        e.printStackTrace();
        writeAppLog(e.toString(), e.getStackTrace());
    }

    public static void writeAppLog(String mgs, boolean includeDate) {
        try {
            String logFilePath = String.format(PATH_LOG, KEY_APP_LOG, CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            StringBuilder log = new StringBuilder();
            if (includeDate) {
                log.append(CommonUtils.getCurrentDateTime1(new Date())).append(STR_SPACE);
            }
            log.append(mgs).append(STR_NEXT_LINE_2);
            FileUtils.writeFileAppend(FileUtils.getFilePath(logFilePath), log.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeAppLog(String errorMessage, StackTraceElement[] errorStackTrace) {
        try {
            String logFilePath = String.format(PATH_LOG, KEY_APP_LOG, CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            StringBuilder log = new StringBuilder(CommonUtils.getCurrentDateTime1(new Date())).append(STR_NEXT_LINE);
            log.append(getLineIndentation()).append(errorMessage).append(STR_NEXT_LINE);
            for (int i = 0; i < errorStackTrace.length; i++) {
                log.append(getLineIndentation()).append(errorStackTrace[i].toString()).append(STR_NEXT_LINE);
            }
            FileUtils.writeFileAppend(FileUtils.getFilePath(logFilePath), log.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeSvnLogInfo(Date startDate, List<LogDto> svnLogDtoList) {
        try {
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, SVN_LOG.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            // 写日志文件
            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.equals(appConfigDto.getAppLogEnable(), STR_FALSE)) {
                return;
            }
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(SVN_LOG.getName()).append(STR_SPACE).append(START).append(STR_NEXT_LINE);
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
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(SVN_LOG.getName()).append(STR_SPACE).append(END).append(STR_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(SVN_LOG.getCode()).append(STR_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(STR_SPACE);
            log.append(SVN_NUM).append(svnLogDtoList.size()).append(STR_SPACE);
            log.append(FILE_NUM).append(fileNum).append(STR_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, SVN_LOG.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFileAppend(FileUtils.getFilePath(logFilePath), log.toString());
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    public static void writeSvnUpdateInfo(Date startDate, List<String> filePath) {
        writeLogInfo(SVN_UPDATE.getCode(), startDate, filePath);
    }

    public static void writeFundInfo(Date startDate, List<String> filePath) {
        writeLogInfo(FUND_INFO.getCode(), startDate, filePath);
    }

    public static void writeProcessInfo(Date startDate, List<String> filePathList) {
        writeLogInfo(PROCESS_INFO.getCode(), startDate, filePathList);
    }

    public static void writeScriptUpdateInfo(Date startDate, List<String> filePath) {
        writeLogInfo(SCRIPT_UPDATE.getCode(), startDate, filePath);
    }

    public static void writeGenerateCodeInfo(Date startDate, List<String> fileName) {
        writeLogInfo(GENERATE_CODE.getCode(), startDate, fileName);
    }

    public static void writeCopyCodeInfo(Date startDate, List<String> fileName) {
        writeLogInfo(COPY_CODE.getCode(), startDate, fileName);
    }

    public static void writeLogInfo(String functionCode, Date startDate, List<String> logs) {
        try {
            MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionCode);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, functionConfig.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.equals(appConfigDto.getAppLogEnable(), STR_FALSE)) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(functionConfig.getName()).append(STR_SPACE).append(START).append(STR_NEXT_LINE);
            for (String item : logs) {
                log.append(getLineIndentation()).append(item).append(STR_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(functionConfig.getName()).append(STR_SPACE).append(END).append(STR_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(functionConfig.getName()).append(STR_SPACE);
            log.append(COST).append(costTime).append(UNIT).append(STR_NEXT_LINE_2);
            String logFilePath = String.format(PATH_LOG, functionConfig.getLogFolder(), CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG);
            FileUtils.writeFileAppend(FileUtils.getFilePath(logFilePath), log.toString());
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    public static void writeDatabaScriptLogInfo(String functionCode, List<String> logs, String filePath) {
        try {
            MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionCode);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, functionConfig.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);

            AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
            if (StringUtils.equals(appConfigDto.getAppLogEnable(), STR_FALSE)) {
                return;
            }
            FileUtils.writeFile(filePath, logs);
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }


    public static void writeConfigSetInfo(String functionCode) {
        try {
            MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionCode);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath(String.format(PATH_STAT, functionConfig.getLogFolder() + FILE_TYPE_STAT));
            writeStatFile(statFilePath);
        } catch (Exception e) {
            LoggerUtils.error(e);
        }
    }

    private static void writeStatFile(String filePath) throws IOException {
        File file = new File(filePath);
        String date = CommonUtils.getCurrentDateTime1();
        if (file.exists()) {
            // 修改文件
            List<String> content = FileUtils.readNormalFile(filePath);
            for (int i = 0; i < content.size(); i++) {
                String item = content.get(i);
                if (i == 1) {
                    // 更新末次使用时间
                    content.set(i, new StringBuilder("末次使用时间: ").append(date).toString());
                }
                if (i == 2) {
                    // 更新使用次数
                    String[] line = item.split(STR_COLON);
                    if (line.length == 2) {
                        int times = Integer.valueOf(line[1].trim()) + 1;
                        content.set(i, new StringBuilder("使用次数: ").append(times).toString());
                    }
                }
            }
            FileUtils.writeFile(filePath, content);
        } else {
            // 重写文件
            StringBuilder statLog = new StringBuilder();
            statLog.append("首次使用时间: ").append(date).append(STR_NEXT_LINE);
            statLog.append("末次使用时间: ").append(date).append(STR_NEXT_LINE);
            statLog.append("使用次数: 1");
            FileUtils.writeFile(filePath, statLog.toString());
        }
    }

    private static String getLineIndentation() {
        return STR_SPACE_3;
    }

}
