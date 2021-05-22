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

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/27
 */
public class LoggerUtils {

    public static void info(String msg) {
        System.out.println(CommonUtils.getCurrentDateTime1(new Date()) + STR_SPACE + msg + STR_SYMBOL_NEXT_LINE);
        writeAppLog(msg);
    }

    public static void writeAppLog(String mgs) {
        try {
            String logFilePath = "/logs/appLog/" + CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG;
            StringBuilder log = new StringBuilder(CommonUtils.getCurrentDateTime1(new Date())).append(STR_SPACE);
            log.append(mgs).append(STR_SYMBOL_NEXT_LINE_2);
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath).getPath(), log.toString(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeSvnLogInfo(Date startDate, List<LogDto> svnLogDtoList) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getEnableLog()) {
                return;
            }
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            // 写日志文件
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_1)).append(STR_SPACE).append("开始").append(STR_SYMBOL_NEXT_LINE);
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
            log.append(FunctionConfig.getName(STR_1)).append(STR_SPACE).append("结束").append(STR_SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_1)).append(STR_SPACE);
            log.append("耗时:").append(costTime).append("秒").append(STR_SPACE);
            log.append("svn版本个数:").append(svnLogDtoList.size()).append(STR_SPACE);
            log.append("文件个数:").append(fileNum).append(STR_SYMBOL_NEXT_LINE_2);
            String logFilePath = "/logs/svnLog/" + CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG;
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath).getPath(), log.toString(), true);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath("/logs/svnLog/00000000.log").getPath();
            writeStatFile(statFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeSvnUpdateInfo(Date startDate, List<String> filePath) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getEnableLog()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_2)).append(STR_SPACE).append("开始").append(STR_SYMBOL_NEXT_LINE);
            for (String item : filePath) {
                log.append(getLineIndentation()).append(item).append(STR_SYMBOL_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_2)).append(STR_SPACE).append("结束").append(STR_SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_2)).append(STR_SPACE);
            log.append("耗时:").append(costTime).append("秒").append(STR_SYMBOL_NEXT_LINE_2);
            String logFilePath = "/logs/svnUpdate/" + CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG;
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath).getPath(), log.toString(), true);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath("/logs/svnUpdate/00000000.log").getPath();
            writeStatFile(statFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFundInfo(Date startDate, String filePath) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getEnableLog()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_3)).append(STR_SPACE).append("开始").append(STR_SYMBOL_NEXT_LINE);
            log.append(getLineIndentation()).append(filePath).append(STR_SYMBOL_NEXT_LINE);
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_3)).append(STR_SPACE).append("结束").append(STR_SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_3)).append(STR_SPACE);
            log.append("耗时:").append(costTime).append("秒").append(STR_SYMBOL_NEXT_LINE_2);
            String logFilePath = "/logs/fundInfo/" + CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG;
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath).getPath(), log.toString(), true);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath("/logs/fundInfo/00000000.log").getPath();
            writeStatFile(statFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeProcessInfo(Date startDate, List<String> filePathList) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getEnableLog()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_4)).append(STR_SPACE).append("开始").append(STR_SYMBOL_NEXT_LINE);
            for (String item : filePathList) {
                log.append(getLineIndentation()).append(item).append(STR_SYMBOL_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_4)).append(STR_SPACE).append("结束").append(STR_SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_4)).append(STR_SPACE);
            log.append("耗时:").append(costTime).append("秒").append(STR_SYMBOL_NEXT_LINE_2);
            String logFilePath = "/logs/processInfo/" + CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG;
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath).getPath(), log.toString(), true);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath("/logs/processInfo/00000000.log").getPath();
            writeStatFile(statFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeScriptUpdateInfo(Date startDate, List<String> filePath) {
        try {
            AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
            if (!appConfigDto.getEnableLog()) {
                return;
            }
            // 写日志文件
            Date endDate = new Date();
            StringBuilder log = new StringBuilder();
            log.append(CommonUtils.getCurrentDateTime1(startDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_5)).append(STR_SPACE).append("开始").append(STR_SYMBOL_NEXT_LINE);
            for (String item : filePath) {
                log.append(getLineIndentation()).append(item).append(STR_SYMBOL_NEXT_LINE);
            }
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_5)).append(STR_SPACE).append("结束").append(STR_SYMBOL_NEXT_LINE);
            long costTime = (endDate.getTime() - startDate.getTime()) / 1000;
            log.append(CommonUtils.getCurrentDateTime1(endDate)).append(STR_SPACE);
            log.append(FunctionConfig.getName(STR_5)).append(STR_SPACE);
            log.append("耗时:").append(costTime).append("秒").append(STR_SYMBOL_NEXT_LINE_2);
            String logFilePath = "/logs/scriptUpdate/" + CommonUtils.getCurrentDateTime3() + FILE_TYPE_LOG;
            FileUtils.writeFile(FileUtils.getFilePath(logFilePath).getPath(), log.toString(), true);
            // 写统计文件
            String statFilePath = FileUtils.getFilePath("/logs/scriptUpdate/00000000.log").getPath();
            writeStatFile(statFilePath);
        } catch (Exception e) {
            e.printStackTrace();
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
                    String[] line = item.split(STR_SYMBOL_COLON);
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
            statLog.append("首次使用时间: ").append(date).append(STR_SYMBOL_NEXT_LINE);
            statLog.append("末次使用时间: ").append(date).append(STR_SYMBOL_NEXT_LINE);
            statLog.append("使用次数: 1");
            FileUtils.writeFile(filePath, statLog.toString(), false);
        }
    }

    private static String getLineIndentation() {
        return STR_SPACE_2;
    }

}
