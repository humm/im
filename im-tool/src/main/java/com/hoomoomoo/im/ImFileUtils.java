package com.hoomoomoo.im;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author hoomoomoo
 * @Description 文件工具类
 * @package com.hoomoomoo.im
 * @Date 2020/06/07
 */

public class ImFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImFileUtils.class);

    /**
     * 配置文件
     */
    private static final String PROPERTIES_PATH = "application.properties";

    /**
     * 文件路径地址
     */
    private static final String FILE_PATH = "path.txt";

    /**
     * 反斜杠
     */
    private static final String SYMBOL_BACKSLASH_2 = "\\\\";

    /**
     * 反斜杠
     */
    private static final String SYMBOL_BACKSLASH_1 = "\\";

    /**
     * 斜杠
     */
    private static final String SYMBOL_SLASH = "/";

    /**
     * #
     */
    private static final String SYMBOL_WEI = "#";

    /**
     * $
     */
    private static final String SYMBOL_DOLLAR = "\\$";

    /**
     * 逗号
     */
    private static final String SYMBOL_COMMA = ",";

    /**
     * 空格
     */
    private static final String SYMBOL_BLANK_SPACE = "\\s+";

    /**
     * 点符号
     */
    private static final String SYMBOL_POINT_2 = "\\.";

    /**
     * 点符号
     */
    private static final String SYMBOL_POINT_1 = ".";

    /**
     * 空格
     */
    private static final String SYMBOL_SPACE = " ";

    /**
     * 空串
     */
    private static final String SYMBOL_EMPTY = "";

    /**
     * 等号
     */
    private static final String SYMBOL_EQUALS = "=";

    /**
     * 减号
     */
    private static final String SYMBOL_MINUS = "-";

    /**
     * 冒号
     */
    private static final String SYMBOL_COLON = ":";

    /**
     * 换行
     */
    private static final String SYMBOL_NEXT_LINE = "\n";

    /**
     * 注释
     */
    private static final String SYMBOL_IGNORE = "--";

    /**
     * 格式化模板
     */
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";

    /**
     * 字符集异常
     */
    private static final String UN_MAPPABLE_CHARACT_EXCEPTION = "java.nio.charset.UnmappableCharacterException";

    /**
     * 指定文件定位行
     */
    private static Map<String, String[]> OTHER_LINE_CONTENT = new LinkedHashMap(16);

    /**
     * 模式配置
     */
    private static Map<String, String[]> MODE_CONFIG = new LinkedHashMap(16);

    /**
     * 版本配置
     */
    private static Map<String, String[]> VERSION_CONFIG = new LinkedHashMap(16);

    /**
     * unicode字符串正则
     */
    private static final Pattern PATTERN = Pattern.compile("(\\\\u(\\w{4}))");

    /**
     * 提示消息
     */
    private static StringBuffer MESSAGE = new StringBuffer();

    /**
     * 错误提示消息
     */
    private static StringBuffer FAIL_MESSAGE = new StringBuffer();

    /**
     * 合并文件内容
     */
    private static StringBuffer CONTENT = new StringBuffer();

    /**
     * 当前日期
     */
    private static String CURRENT_DATE;

    /**
     * 读取文件路径数量
     */
    private static int READ_NUM = 0;

    /**
     * 复制文件数量
     */
    private static int COPY_NUM = 0;

    /**
     * 异常标识
     */
    private static boolean EXCEPTION_STATUS = false;

    /**
     * 暂停模式
     */
    private static Boolean PAUSE_MODE = true;

    /**
     * 连续操作模式
     */
    private static Boolean OPERATE_CONTINUE = true;

    /**
     * 连续操作间隔时间
     */
    private static Integer OPERATE_CONTINUE_TIME = 5;

    /**
     * 成功暂停时间
     */
    private static Integer PAUSE_TIME_SUCCESS = 1;

    /**
     * 失败暂停时间
     */
    private static Integer PAUSE_TIME_FAIL = 5;

    /**
     * 错误次数
     */
    private static Integer ERROR_TIMES = 0;

    /**
     * 工作目录前缀
     */
    private static String WORKSPACE = "";

    /**
     * 导出文件目录
     */
    private static String EXPORT_WORKSPACE = "";

    /**
     * 指定行内容
     */
    private static String LINE_CONTENT = "";

    /**
     * 指定行内容偏移量
     */
    private static Integer LINE_OFFSET = 0;

    /**
     * 成功后删除文件
     */
    private static Boolean DELETE_AFTER_SUCCESS = true;

    /**
     * 编码格式
     */
    private static String ENCODING = "GBK";

    /**
     * 编码格式
     */
    private static String ENCODING_UTF8 = "UTF-8";

    /**
     * 编码格式
     */
    private static String ENCODING_GBK = "GBK";

    /**
     * 提示文件后缀
     */
    private static String FILE_SUFFIX = ".txt";

    /**
     * zip文件
     */
    private static final String FILE_TYPE_ZIP = ".zip";

    /**
     * rar文件
     */
    private static final String FILE_TYPE_RAR = ".rar";


    /**
     * 成功
     */
    private static final String SUCCESS = "success";

    /**
     * 失败
     */
    private static final String FAIL = "fail";

    /**
     * 数据库升级脚本目录
     */
    private static final String FILE_NAME_UPDATE = "数据库升级脚本";

    /**
     * 升级脚本path
     */
    private static String FILE_NAME_PATH_UPDATE = "";

    /**
     * 更新文件目录path
     */
    private static String FILE_DIRECTORY_PATH_UPDATE = "";

    /**
     * 合并文件名称
     */
    private static final String FILE_NAME_MERGE = "merge";

    /**
     * 启动模式 1:Jar包启动 2:工程启动
     */
    private static String START_MODE = "jar";

    /**
     * Jar启动模式
     */
    private static final String START_MODE_JAR = "jar";

    /**
     * 工程启动模式
     */
    private static final String START_MODE_PROJECT = "project";

    /**
     * 文件操作模式
     */
    private static String OPERATE_MODE = "";

    /**
     * 文件复制
     */
    private static final String OPERATE_MODE_COPY = "mode.copy";

    /**
     * 文件合并
     */
    private static final String OPERATE_MODE_MERGE = "mode.merge";

    /**
     * 文件覆盖
     */
    private static final String OPERATE_MODE_COVER = "mode.cover";

    /**
     * 文件更新
     */
    private static final String OPERATE_MODE_UPDATE = "mode.update";

    /**
     * 文件复制
     */
    private static final String STATUS_MODE_COPY = "复制";

    /**
     * 文件合并
     */
    private static final String STATUS_MODE_MERGE = "合并";

    /**
     * 文件覆盖
     */
    private static final String STATUS_MODE_COVER = "覆盖";

    /**
     * 文件更新
     */
    private static final String STATUS_MODE_UPDATE = "更新";


    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        CURRENT_DATE = simpleDateFormat.format(new Date());
        // 设置启动模式
        getStartMode();
        // 读取配置文件参数
        run(getConfigProperties());
    }

    /**
     * 主入口
     *
     * @param config
     * @author: humm23693
     * @date: 2020/09/07
     * @return:
     */
    private static void run(Map config) {
        // 获取配置参数
        getProperties(config);
        switch (OPERATE_MODE) {
            case OPERATE_MODE_COPY:
                // 复制文件
                copyFile();
                break;
            case OPERATE_MODE_MERGE:
                // 合并文件
                mergeFile();
                break;
            case OPERATE_MODE_COVER:
                // 覆盖文件
                coverFile();
                break;
            case OPERATE_MODE_UPDATE:
                // 更新文件
                updateFile();
                break;
            default:
                break;
        }
        // 连续操作模式控制应用退出
        exit();
        // 连续操作模式
        run(config);
        // 暂停模式控制
        if (PAUSE_MODE) {
            Integer sleepTime = PAUSE_TIME_SUCCESS;
            if (READ_NUM != COPY_NUM || EXCEPTION_STATUS) {
                sleepTime = PAUSE_TIME_FAIL;
            }
            sleep(sleepTime);
        }
    }

    /**
     * 应用退出控制
     *
     * @param
     * @author: humm23693
     * @date: 2020/09/08
     * @return:
     */
    private static void exit() {
        logger.info(SYMBOL_NEXT_LINE);
        new Thread(() -> {
            // 连续操作模式 上一次操作正常
            if (OPERATE_CONTINUE && READ_NUM == COPY_NUM && !EXCEPTION_STATUS) {
                clean();
                while (StringUtils.isBlank(OPERATE_MODE)) {
                    sleep(1);
                    OPERATE_CONTINUE_TIME--;
                    if (OPERATE_CONTINUE_TIME == 0) {
                        System.exit(0);
                    }
                }
            }
        }).start();
    }

    /**
     * 文件更新
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateFile() {
        File fileDirectory = new File(WORKSPACE);
        if (!fileDirectory.isDirectory()) {
            throw new RuntimeException(String.format("源文件工作目录[ %s ]不存在", WORKSPACE));
        }
        File[] fileList = fileDirectory.listFiles();
        if (fileList != null) {
            // 解压文件
            for (File file : fileList) {
                if (file.isDirectory()) {
                    continue;
                }
                String fileName = file.getName();
                try {
                    if (fileName.endsWith(FILE_TYPE_ZIP)) {
                        UnZipAnRarTool.unZip(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH_1);
                    } else if (fileName.endsWith(FILE_TYPE_RAR)) {
                        UnZipAnRarTool.unRar(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH_1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    EXCEPTION_STATUS = true;
                }
            }
            // 更新文件
            fileList = fileDirectory.listFiles();
            for (File file : fileList) {
                String fileName = file.getName();
                if (fileName.endsWith(FILE_TYPE_ZIP) || fileName.endsWith(FILE_TYPE_RAR)) {
                    continue;
                }
                if ((SUCCESS + FILE_SUFFIX).equals(file.getName()) || (FAIL + FILE_SUFFIX).equals(file.getName())) {
                    continue;
                }
                if (file.isDirectory()) {
                    // 文件夹
                    File target = new File(EXPORT_WORKSPACE);
                    Map<String, String> targetDirectory = new HashMap<>(10);
                    if (target != null) {
                        File[] targetList = target.listFiles();
                        for (File targetFile : targetList) {
                            targetDirectory.put(targetFile.getName(), targetFile.getName());
                        }
                    }
                    getWorkspaceAbsolutely(file.getAbsolutePath(), targetDirectory);
                    if (StringUtils.isBlank(FILE_DIRECTORY_PATH_UPDATE)) {
                        throw new RuntimeException(String.format("[ %s ]更新文件目录不匹配", file.getAbsolutePath()));
                    }
                    updateMultipleFile(file, FILE_DIRECTORY_PATH_UPDATE);
                } else {
                    // 单个文件,升级脚本,增量更新
                    File updateFile = new File(EXPORT_WORKSPACE + FILE_NAME_UPDATE);
                    if (!updateFile.exists()) {
                        updateFile = new File(EXPORT_WORKSPACE);
                    }
                    getUpdateScriptPath(file.getName(), updateFile.getAbsolutePath());
                    if (StringUtils.isBlank(FILE_NAME_PATH_UPDATE)) {
                        throw new RuntimeException(String.format("[ %s ]更新文件目录不匹配", file.getAbsolutePath()));
                    }
                    READ_NUM++;
                    logger.info(String.format("更新文件[ %s ]", FILE_NAME_PATH_UPDATE));
                    updateScriptFile(file.getAbsolutePath(), FILE_NAME_PATH_UPDATE);
                }
            }
            if (DELETE_AFTER_SUCCESS && READ_NUM == COPY_NUM && !EXCEPTION_STATUS) {
                for (File file : fileList) {
                    deleteFile(file);
                }
            }
        }
        savePathStatus(STATUS_MODE_UPDATE, WORKSPACE);
    }

    /**
     * 更新文件夹下文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateMultipleFile(File file, String fileDirectory) {
        if (!file.isDirectory()) {
            updateSingleFile(file, fileDirectory);
        } else {
            File[] fileList = file.listFiles();
            for (File single : fileList) {
                if (single.isDirectory()) {
                    updateMultipleFile(single, fileDirectory);
                } else {
                    updateSingleFile(single, fileDirectory);
                }
            }
        }

    }

    /**
     * 更新单个文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateSingleFile(File file, String fileDirectory) {
        READ_NUM++;
        String inputPath = convertBackslash(file.getAbsolutePath());
        String exportPath = inputPath.replace(fileDirectory, EXPORT_WORKSPACE);
        copySingleFile(inputPath, exportPath);
        logger.info(String.format("更新文件[ %s ]", inputPath));
    }

    /**
     * 获取替换文件绝对路径
     *
     * @param filePath
     * @param targetDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static boolean getWorkspaceAbsolutely(String filePath, Map<String, String> targetDirectory) {
        File source = new File(filePath);
        if (source != null) {
            if (source.isDirectory()) {
                String fileName = source.getName();
                if (!StringUtils.isNotBlank(targetDirectory.get(fileName))) {
                    File[] subSourceList = source.listFiles();
                    for (File subSource : subSourceList) {
                        if (getWorkspaceAbsolutely(subSource.getAbsolutePath(), targetDirectory)) {
                            break;
                        }
                    }
                } else {
                    FILE_DIRECTORY_PATH_UPDATE = convertBackslash(source.getParentFile().getAbsolutePath());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取升级脚本绝对路径
     *
     * @param fileName
     * @param filePath
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static boolean getUpdateScriptPath(String fileName, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (File singleFile : fileList) {
                if (getUpdateScriptPath(fileName, singleFile.getAbsolutePath())) {
                    break;
                }
            }
        } else {
            if (fileName.equals(file.getName())) {
                FILE_NAME_PATH_UPDATE = convertBackslash(file.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    /**
     * 更新升级脚本
     *
     * @param sourcePath
     * @param targetPath
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void updateScriptFile(String sourcePath, String targetPath) {
        String encoding = ENCODING;
        if (ERROR_TIMES != 0) {
            if (ENCODING_GBK.equals(ENCODING)) {
                ENCODING = ENCODING_UTF8;
            } else if (ENCODING_UTF8.equals(ENCODING)) {
                ENCODING = ENCODING_GBK;
            }
        }
        String fileContent = SYMBOL_NEXT_LINE + getFileContent(sourcePath);
        ENCODING = encoding;
        List<String> sourceLines = new ArrayList<>();
        COPY_NUM--;
        try {
            List<String> lines = Files.readAllLines(Paths.get(targetPath), Charset.forName(ENCODING));
            sourceLines.addAll(lines);
            int position = 0;
            if (StringUtils.isBlank(LINE_CONTENT)) {
                lines.add(fileContent);
            } else {
                String lineContent = LINE_CONTENT;
                Integer lineOffset = LINE_OFFSET;
                Iterator<Map.Entry<String, String[]>> iterator = OTHER_LINE_CONTENT.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String[]> file = iterator.next();
                    if (targetPath.endsWith(file.getKey())) {
                        lineContent = file.getValue()[1];
                        lineOffset = Integer.valueOf(file.getValue()[2]);
                        break;
                    }
                }
                if (CollectionUtils.isNotEmpty(lines)) {
                    for (int i = 0; i < lines.size(); i++) {
                        String content = lines.get(i);
                        if (lineContent.equals(content.trim())) {
                            position = i + lineOffset;
                        }
                    }
                }
                lines.add(position, fileContent);
            }
            Files.write(Paths.get(targetPath), lines, Charset.forName(ENCODING));
            COPY_NUM++;
            ERROR_TIMES = 0;
        } catch (IOException e) {
            // 内容还原
            try {
                Files.write(Paths.get(targetPath), sourceLines, Charset.forName(ENCODING));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (e.toString().startsWith(UN_MAPPABLE_CHARACT_EXCEPTION) && ERROR_TIMES == 0) {
                // 编码转换再读取一次 编码转换在方法入口
                ERROR_TIMES++;
                updateScriptFile(sourcePath, targetPath);
            } else if (e.toString().startsWith(UN_MAPPABLE_CHARACT_EXCEPTION)) {
                FAIL_MESSAGE.append(String.format("请检查[ %s ]编码格式,调整配置项[ encoding ]为对应值", sourcePath));
                EXCEPTION_STATUS = true;
                e.printStackTrace();
            } else {
                EXCEPTION_STATUS = true;
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param file
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList == null) {
                file.delete();
            } else {
                for (File subFile : fileList) {
                    deleteFile(subFile);
                }
                file.delete();
            }
        } else {
            file.delete();
        }
    }

    /**
     * 覆盖文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void coverFile() {
        File fileDirectory = new File(WORKSPACE);
        if (!fileDirectory.isDirectory()) {
            throw new RuntimeException(String.format("源文件工作目录[ %s ]不存在", WORKSPACE));
        }
        File[] fileDirectoryList = fileDirectory.listFiles();
        if (fileDirectoryList != null) {
            sortFile(fileDirectoryList);
            File coverFileDirectory = fileDirectoryList[0];
            if (!coverFileDirectory.isDirectory()) {
                throw new RuntimeException(String.format("[ %s ]不是文件夹", coverFileDirectory.getAbsolutePath()));
            }
            File[] coverFileList = coverFileDirectory.listFiles();
            for (File coverFile : coverFileList) {
                coverMultipleFile(coverFile, convertBackslash(coverFileDirectory.getAbsolutePath()));
            }
            savePathStatus(STATUS_MODE_COVER, coverFileDirectory.getAbsolutePath());
        }
    }

    /**
     * 覆盖文件夹下文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void coverMultipleFile(File file, String fileDirectory) {
        if (!file.isDirectory()) {
            coverSingleFile(file, fileDirectory);
        } else {
            File[] fileList = file.listFiles();
            for (File single : fileList) {
                if (single.isDirectory()) {
                    coverMultipleFile(single, fileDirectory);
                } else {
                    coverSingleFile(single, fileDirectory);
                }
            }
        }
    }

    /**
     * 覆盖单个文件
     *
     * @param file
     * @param fileDirectory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void coverSingleFile(File file, String fileDirectory) {
        if ((SUCCESS + FILE_SUFFIX).equals(file.getName()) || (FAIL + FILE_SUFFIX).equals(file.getName())) {
            return;
        }
        String inputPath = convertBackslash(file.getAbsolutePath());
        READ_NUM++;
        String exportPath = inputPath.replace(fileDirectory, EXPORT_WORKSPACE);
        copySingleFile(inputPath, exportPath);
        logger.info(String.format("覆盖文件[ %s ]", inputPath));
    }

    /**
     * 复制文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void copyFile() {
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(checkFile(FILE_PATH)));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    String[] subInputPath = convertBackslash(inputPath.trim()).split(SYMBOL_BLANK_SPACE);
                    String sourcePath = subInputPath[subInputPath.length - 1].trim();
                    if (!sourcePath.isEmpty()) {
                        READ_NUM++;
                        logger.info(String.format("复制文件[ %s ]", sourcePath));
                        String exportPath = sourcePath.replace(WORKSPACE, EXPORT_WORKSPACE + CURRENT_DATE + SYMBOL_BACKSLASH_1);
                        copySingleFile(sourcePath, exportPath);
                    }
                }
            }
            savePathStatus(STATUS_MODE_COPY, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param sourcePath
     * @param exportPath
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void copySingleFile(String sourcePath, String exportPath) {
        boolean flag = false;
        int lastIndex = exportPath.lastIndexOf(SYMBOL_SLASH);
        String path = exportPath.substring(0, lastIndex);
        File inFile = new File(sourcePath);
        File outFile = new File(exportPath);
        StringBuffer msg = new StringBuffer();
        if (inFile.exists() && inFile.isFile()) {
            File sourceFolder = new File(path);
            if (!sourceFolder.exists()) {
                sourceFolder.mkdirs();
            }
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                byte[] cache = new byte[1024];
                int length;
                fileInputStream = new FileInputStream(inFile);
                fileOutputStream = new FileOutputStream(outFile);
                while ((length = fileInputStream.read(cache)) != -1) {
                    fileOutputStream.write(cache, 0, length);
                    flag = true;
                }
                if (flag) {
                    fileOutputStream.flush();
                }
                COPY_NUM++;
                msg.append(SUCCESS).append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(msg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                msg.append(FAIL).append(SYMBOL_SPACE).append(e.getMessage()).append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
            } catch (IOException e) {
                e.printStackTrace();
                msg.append(FAIL).append(SYMBOL_SPACE).append(e.getMessage()).append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
            } finally {
                try {
                    fileInputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            msg.append(FAIL).append(" 不存在文件").append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE).toString();
            MESSAGE.append(msg);
            FAIL_MESSAGE.append(msg);
        }
    }

    /**
     * 设置结果状态
     *
     * @param statusType
     * @param directory
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void savePathStatus(String statusType, String directory) {
        String statusPath = EXPORT_WORKSPACE + CURRENT_DATE;
        if (StringUtils.isNotBlank(directory)) {
            statusPath = directory;
        }
        File statusFolder = new File(statusPath);
        if (!statusFolder.exists()) {
            statusFolder.mkdirs();
        }
        String fileName = SUCCESS;
        if (READ_NUM != COPY_NUM || EXCEPTION_STATUS) {
            fileName = FAIL;
        }
        String statusFilename = statusPath + SYMBOL_BACKSLASH_1 + fileName + FILE_SUFFIX;
        File file = new File(statusFilename);
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(MESSAGE.toString());
            if (SUCCESS.equals(fileName)) {
                logger.info(String.format("文件%s完成,文件数量[ %s ]", statusType, READ_NUM));
            } else {
                logger.error(String.format("文件%s失败,读取文件数量[ %s ],%s文件数量[ %s ]", statusType, READ_NUM, statusType, COPY_NUM));
                logger.error(FAIL_MESSAGE.toString());
                if (STATUS_MODE_COPY.equals(statusType) || STATUS_MODE_MERGE.equals(statusType)) {
                    logger.error(String.format("请检查[ %s ]编码格式,转换文件格式为[ GBK ]", FILE_PATH));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            printStream.close();
        }
    }

    /**
     * 合并文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void mergeFile() {
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(checkFile(FILE_PATH)));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    READ_NUM++;
                    String[] subInputPath = convertBackslash(inputPath.trim()).split(SYMBOL_BLANK_SPACE);
                    String path = subInputPath[subInputPath.length - 1].trim();
                    logger.info(String.format("合并文件[ %s ]", path));
                    CONTENT.append(getFileContent(path));
                }
            }
            savePathStatus(STATUS_MODE_MERGE, createFile(CONTENT.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取单个文件内容
     *
     * @param fileName
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static String getFileContent(String fileName) {
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer msg = new StringBuffer();
        try {
            File file = new File(fileName);
            if (file.exists()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
                String content;
                while ((content = reader.readLine()) != null) {
                    stringBuffer.append(content).append(SYMBOL_NEXT_LINE);
                }
                msg.append(SUCCESS).append(SYMBOL_SPACE).append(fileName).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(msg);
                COPY_NUM++;
            } else {
                msg.append(FAIL).append(" 不存在文件").append(SYMBOL_SPACE).append(fileName).append(SYMBOL_NEXT_LINE).toString();
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                return SYMBOL_NEXT_LINE;
            }
        } catch (IOException e) {
            e.printStackTrace();
            msg.append(FAIL).append(SYMBOL_SPACE).append(e.getMessage()).append(SYMBOL_SPACE).append(fileName).append(SYMBOL_NEXT_LINE);
            MESSAGE.append(msg);
            FAIL_MESSAGE.append(msg);
            EXCEPTION_STATUS = true;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stringBuffer.append(SYMBOL_NEXT_LINE);
        return stringBuffer.toString();
    }

    /**
     * 创建合并文件
     *
     * @param content
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static String createFile(String content) {
        String statusPath = EXPORT_WORKSPACE + CURRENT_DATE;
        File statusFolder = new File(statusPath);
        if (!statusFolder.exists()) {
            statusFolder.mkdirs();
        }
        String fileName = FILE_NAME_MERGE + SYMBOL_MINUS + READ_NUM;
        String statusFilename = statusPath + SYMBOL_BACKSLASH_1 + fileName + FILE_SUFFIX;
        File file = new File(statusFilename);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING)));
            out.write(content);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            out.close();
        }
        return statusPath;
    }

    /**
     * 校验文件是否存在
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static File checkFile(String fileName) {
        File file;
        if (START_MODE_PROJECT.equals(START_MODE)) {
            file = new File(ImFileUtils.class.getClassLoader().getResource(fileName).getFile());
        } else {
            file = new File(fileName);
        }
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("源文件配置文件[ %s ] 不存在", fileName));
        }
        return file;
    }

    /**
     * 设置启动模式
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void getStartMode() {
        URL url = ImFileUtils.class.getResource("ImFileUtils.class");
        if (url.toString().startsWith(START_MODE_JAR)) {
            START_MODE = START_MODE_JAR;
        } else {
            START_MODE = START_MODE_PROJECT;
        }
    }

    /**
     * 文件排序
     *
     * @param fileList
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void sortFile(File[] fileList) {
        Arrays.sort(fileList, (o1, o2) -> {
            long sort = o2.lastModified() - o1.lastModified();
            if (sort > 0) {
                return 1;
            } else if (sort == 0) {
                return 0;
            } else {
                return -1;
            }
        });
    }

    /**
     * 读取配置文件参数
     *
     * @param config
     * @author: humm23693
     * @date: 2020/09/07
     * @return:
     */
    private static void getProperties(Map<String, String> config) {
        // 获取模式配置
        getModeConfig(config);
        // 获取版本配置
        getVersionConfig(config);
        // 模式选择
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String code = scanner.next();
            if (MODE_CONFIG.get(code) == null) {
                logger.info("模式不存在,请重新选择");
            } else {
                OPERATE_MODE = MODE_CONFIG.get(code)[2];
                logger.info(String.format("选择模式为[ %s ]", MODE_CONFIG.get(code)[1]));
                break;
            }
        }
        // 版本号选择
        logger.info("请选择版本号:");
        Iterator<Map.Entry<String, String[]>> iterator = VERSION_CONFIG.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> item = iterator.next();
            if (item.getKey().startsWith(OPERATE_MODE)) {
                logger.info(String.format("[ %s ]%s", item.getValue()[0], item.getValue()[item.getValue().length - 1]));
            }
        }
        while (true) {
            String code = OPERATE_MODE + SYMBOL_POINT_1 + scanner.next();
            if (VERSION_CONFIG.get(code) == null) {
                logger.info("版本号不存在,请重新选择");
            } else {
                WORKSPACE = VERSION_CONFIG.get(code)[1];
                EXPORT_WORKSPACE = VERSION_CONFIG.get(code)[2];
                logger.info(String.format("源文件工作目录[ %s ]", WORKSPACE));
                logger.info(String.format("导出文件工作目录[ %s ]", EXPORT_WORKSPACE));
                break;
            }
        }

        // 编码格式
        if (OPERATE_MODE_MERGE.equals(OPERATE_MODE) || OPERATE_MODE_UPDATE.equals(OPERATE_MODE)) {
            String encoding = config.get("encoding");
            if (StringUtils.isNotBlank(encoding)) {
                ENCODING = encoding;
            }
            logger.info(String.format("文件编码格式[ %s ]", ENCODING));
        }

        // 文件后缀名称
        String fileSuffix = config.get("file.suffix");
        if (StringUtils.isNotBlank(fileSuffix)) {
            FILE_SUFFIX = fileSuffix;
        }
        logger.info(String.format("文件后缀名称[ %s ]", FILE_SUFFIX));

        // 更新模式获取文件定位
        if (OPERATE_MODE_UPDATE.equals(OPERATE_MODE)) {
            String line = config.get("mode.update.line");
            if (StringUtils.isNotBlank(line)) {
                String[] lines = line.split(SYMBOL_DOLLAR);
                if (lines.length != 2) {
                    throw new RuntimeException(String.format("更新模式文件定位[ mode.update.line ]配置[ %s ]格式错误", line));
                }
                LINE_CONTENT = lines[0];
                LINE_OFFSET = Integer.valueOf(lines[1]);
            }
            logger.info(String.format("更新模式文件定位[ %s ]", LINE_CONTENT));

            // 更新模式成功后删除源文件
            String deleteAfterSuccess = config.get("mode.update.delete.after.success");
            if (StringUtils.isNotBlank(deleteAfterSuccess)) {
                DELETE_AFTER_SUCCESS = Boolean.valueOf(deleteAfterSuccess);
            }
            logger.info(String.format("更新模式成功后删除源文件[ %s ]", DELETE_AFTER_SUCCESS));

            // 更新模式指定文件定位
            getMoreUpdateLine(config);
        }

        // 连续操作模式
        String operateContinue = config.get("operate.continue");
        if (StringUtils.isNotBlank(operateContinue)) {
            OPERATE_CONTINUE = Boolean.valueOf(operateContinue);
        }
        logger.info(String.format("连续操作模式[ %s ]", OPERATE_CONTINUE));

        // 连续操作间隔时间
        String operateContinueTime = config.get("operate.continue.time");
        if (StringUtils.isNotBlank(operateContinueTime)) {
            OPERATE_CONTINUE_TIME = Integer.valueOf(operateContinueTime);
        }
        logger.info(String.format("连续操作间隔时间[ %s ]", OPERATE_CONTINUE_TIME));

        // 暂停模式
        String pauseMode = config.get("pause.mode");
        if (StringUtils.isNotBlank(pauseMode)) {
            PAUSE_MODE = Boolean.valueOf(pauseMode);
        }
        logger.info(String.format("暂停模式[ %s ]", PAUSE_MODE));
        if (PAUSE_MODE) {
            String pauseTimeSuccess = config.get("pause.time.success");
            if (StringUtils.isNotBlank(pauseTimeSuccess)) {
                PAUSE_TIME_SUCCESS = Integer.valueOf(pauseTimeSuccess);
            }
            logger.info(String.format("成功暂停时间[ %s ]", PAUSE_TIME_SUCCESS));
            String pauseTimeFail = config.get("pause.time.fail");
            if (StringUtils.isNotBlank(pauseTimeFail)) {
                PAUSE_TIME_FAIL = Integer.valueOf(pauseTimeFail);
            }
            logger.info(String.format("失败暂停时间[ %s ]", PAUSE_TIME_FAIL));
        }
    }

    /**
     * 读取配置文件
     *
     * @param
     * @author: humm23693
     * @date: 2020/09/03
     * @return:
     */
    private static Map<String, String> getConfigProperties() {
        Map<String, String> config = new LinkedHashMap<>(16);
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(checkFile(PROPERTIES_PATH)));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (StringUtils.isBlank(inputPath.trim()) || inputPath.trim().startsWith(SYMBOL_WEI)) {
                        continue;
                    }
                    if (!inputPath.trim().contains(SYMBOL_EQUALS)) {
                        throw new RuntimeException(String.format("[ %s ]文件中[ %s ]格式错误", PROPERTIES_PATH, inputPath.trim()));
                    }
                    String[] item = inputPath.trim().split(SYMBOL_EQUALS);
                    if (item.length != 2) {
                        continue;
                    }
                    config.put(item[0], convertUnicodeToCh(item[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    /**
     * unicode转中文字符串
     *
     * @param str
     * @author: humm23693
     * @date: 2020/09/03
     * @return:
     */
    private static String convertUnicodeToCh(String str) {
        Matcher matcher = PATTERN.matcher(str);
        // 迭代，将str中的所有unicode转换为正常字符
        while (matcher.find()) {
            // 匹配出的每个字的unicode，比如\u67e5
            String unicodeFull = matcher.group(1);
            // 匹配出每个字的数字，比如\u67e5，会匹配出67e5
            String unicodeNum = matcher.group(2);
            // 将匹配出的数字按照16进制转换为10进制，转换为char类型，就是对应的正常字符了
            char singleChar = (char) Integer.parseInt(unicodeNum, 16);
            // 替换原始字符串中的unicode码
            str = str.replace(unicodeFull, singleChar + SYMBOL_EMPTY);
        }
        return str;
    }

    /**
     * 反斜线转换
     *
     * @param value
     * @author: humm23693
     * @date: 2020/09/06
     * @return:
     */
    private static String convertBackslash(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value.replace(SYMBOL_BACKSLASH_1, SYMBOL_SLASH).replace(SYMBOL_BACKSLASH_2, SYMBOL_SLASH);
        }
        return value;
    }

    /**
     * 获取更新模式指定文件定位
     *
     * @param config
     * @author: humm23693
     * @date: 2020/09/06
     * @return:
     */
    private static void getMoreUpdateLine(Map config) {
        if (config != null) {
            Iterator<Map.Entry<String, String>> iterator = config.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                String fileName = item.getKey();
                if (fileName.contains("mode.update.line.")) {
                    String line = item.getValue();
                    String[] lines = line.split(SYMBOL_DOLLAR);
                    if (lines.length != 3) {
                        throw new RuntimeException(String.format("更新模式指定文件定位[ %s ]配置[ %s ]格式错误", fileName, line));
                    }
                    OTHER_LINE_CONTENT.put(lines[0], lines);
                }
            }
        }
    }

    /**
     * 获取配置模式
     *
     * @param config
     * @author: humm23693
     * @date: 2020/09/06
     * @return:
     */
    private static void getModeConfig(Map config) {
        if (config != null) {
            logger.info("请选择模式:");
            Iterator<Map.Entry<String, String>> iterator = config.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                String modeName = item.getKey();
                if (modeName.contains("mode.config.")) {
                    String mode = item.getValue();
                    String[] modes = mode.split(SYMBOL_DOLLAR);
                    if (modes.length != 2) {
                        throw new RuntimeException(String.format("模式[ %s ]配置[ %s ]格式错误", modeName, mode));
                    }
                    String[] modeExtend = Arrays.copyOf(modes, modes.length + 1);
                    modeExtend[modeExtend.length - 1] = modeName.replace(".config", SYMBOL_EMPTY);
                    MODE_CONFIG.put(modes[0], modeExtend);
                    logger.info(String.format("[ %s ]%s", modeExtend[0], modeExtend[1]));
                }
            }
        }
    }

    /**
     * 深沉睡眠
     *
     * @param sleepTime
     * @author: humm23693
     * @date: 2020/09/08
     * @return:
     */
    private static void sleep(Integer sleepTime) {
        try {
            Thread.sleep(sleepTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置版本
     *
     * @param config
     * @author: humm23693
     * @date: 2020/09/06
     * @return:
     */
    private static void getVersionConfig(Map config) {
        if (config != null) {
            Iterator<Map.Entry<String, String>> iterator = config.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                String versionName = item.getKey();
                if (versionName.contains(".version.")) {
                    String version = item.getValue();
                    String[] versions = version.split(SYMBOL_DOLLAR);
                    if (versions.length != 3) {
                        throw new RuntimeException(String.format("版本[ %s ]配置[ %s ]格式错误", versionName, version));
                    }
                    String[] versionExtend = Arrays.copyOf(versions, versions.length + 2);
                    // 获取版本号
                    String[] versionCode = versionName.split(SYMBOL_POINT_2);
                    versionExtend[versionExtend.length - 1] = versionCode[versionCode.length - 1];
                    if (!versionExtend[1].endsWith(SYMBOL_SLASH) && !versionExtend[1].endsWith(SYMBOL_BACKSLASH_2)) {
                        versionExtend[1] = convertBackslashTwo(versionExtend[1] + SYMBOL_BACKSLASH_2);
                    }
                    if (!versionExtend[2].endsWith(SYMBOL_SLASH) && !versionExtend[2].endsWith(SYMBOL_BACKSLASH_2)) {
                        versionExtend[2] = convertBackslashTwo(versionExtend[2] + SYMBOL_BACKSLASH_2);
                    }
                    String versionItem = versionName.replace(".version", SYMBOL_EMPTY);
                    String versionItemCode = versionItem.replace(versionExtend[versionExtend.length - 1], versionExtend[0]);
                    VERSION_CONFIG.put(versionItemCode, versionExtend);
                }
            }
        }
    }

    /**
     * 参数清理
     *
     * @param
     * @author: humm23693
     * @date: 2020/09/09
     * @return:
     */
    private static void clean() {
        OPERATE_MODE = SYMBOL_EMPTY;
        MESSAGE = new StringBuffer();
        FAIL_MESSAGE = new StringBuffer();
        CONTENT = new StringBuffer();
        READ_NUM = 0;
        COPY_NUM = 0;
        EXCEPTION_STATUS = false;
        ERROR_TIMES = 0;
    }
}
