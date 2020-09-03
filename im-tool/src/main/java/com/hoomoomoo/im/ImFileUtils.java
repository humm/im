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
     * 多路径配置 版本号
     */
    private static Map<String, String> MULTIPLE_VERSION = new HashMap();

    /**
     * 多路径配置 工作目录
     */
    private static Map<String, String> MULTIPLE_EXPORTWORKSPACE = new HashMap();

    /**
     * 多文件 多文件定位行
     */
    private static Map<String, String[]> MULTIPLE_LINE_CONTENT = new HashMap();

    /**
     * unicode字符串正则
     */
    private static final Pattern PATTERN = Pattern.compile("(\\\\u(\\w{4}))");

    /**
     * 文件地址读取文件
     */
    private static final String FILE_PATH = "path.txt";

    /**
     * 斜杠
     */
    private static final String SYMBOL_SLASH = "/";

    /**
     * 反斜杠
     */
    private static final String SYMBOL_BACKSLASH = "\\";

    /**
     * #
     */
    private static final String SYMBOL_WEI = "#";

    /**
     * 逗号
     */
    private static final String SYMBOL_COMMA = ",";

    /**
     * 空格
     */
    private static final String SYMBOL_BLANK_SPACE = "\\s+";

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
    private static final String UN_MAPPABLE_CHARACT_EREXCEPTION = "java.nio.charset.UnmappableCharacterException";

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
     * 成功暂停时间
     */
    private static Integer PAUSE_SUCCESS_TIME = 1;

    /**
     * 失败暂停时间
     */
    private static Integer PAUSE_FAIL_TIME = 5;

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
     * 文件操作模式 1:文件复制 2:文件合并 3:文件覆盖
     */
    private static String OPERATE_TYPE = "copy";

    /**
     * 文件复制
     */
    private static final String OPERATE_TYPE_COPY = "copy";

    /**
     * 文件合并
     */
    private static final String OPERATE_TYPE_MERGE = "merge";

    /**
     * 文件覆盖
     */
    private static final String OPERATE_TYPE_COVER = "cover";

    /**
     * 文件更新
     */
    private static final String OPERATE_TYPE_UPDATE = "update";

    /**
     * 文件复制
     */
    private static final String STATUS_TYPE_COPY = "复制";

    /**
     * 文件合并
     */
    private static final String STATUS_TYPE_MERGE = "合并";

    /**
     * 文件覆盖
     */
    private static final String STATUS_TYPE_COVER = "覆盖";

    /**
     * 文件更新
     */
    private static final String STATUS_TYPE_UPDATE = "更新";


    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        CURRENT_DATE = simpleDateFormat.format(new Date());
        try {
            // 设置启动模式
            getStartMode();
            // 读取配置文件参数
            getProperties();
            switch (OPERATE_TYPE) {
                case OPERATE_TYPE_COPY:
                    // 复制文件
                    copyFile();
                    break;
                case OPERATE_TYPE_MERGE:
                    // 合并文件
                    mergeFile();
                    break;
                case OPERATE_TYPE_COVER:
                    // 覆盖文件
                    coverFile();
                    break;
                case OPERATE_TYPE_UPDATE:
                    // 更新文件
                    updateFile();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        }
        if (PAUSE_MODE) {
            Integer sleepTime = PAUSE_SUCCESS_TIME;
            if (READ_NUM != COPY_NUM || EXCEPTION_STATUS) {
                sleepTime = PAUSE_FAIL_TIME;
            }
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
                        UnZipAnRarTool.unZip(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH);
                    } else if (fileName.endsWith(FILE_TYPE_RAR)) {
                        UnZipAnRarTool.unRar(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH);
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
        savePathStatus(STATUS_TYPE_UPDATE, WORKSPACE);
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
        String inputPath = file.getAbsolutePath().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
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
                    FILE_DIRECTORY_PATH_UPDATE = source.getParentFile().getAbsolutePath();
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
                FILE_NAME_PATH_UPDATE = file.getAbsolutePath();
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
                Iterator<Map.Entry<String, String[]>> iterator = MULTIPLE_LINE_CONTENT.entrySet().iterator();
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
        } catch (IOException e) {
            // 内容还原
            try {
                Files.write(Paths.get(targetPath), sourceLines, Charset.forName(ENCODING));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (e.toString().startsWith(UN_MAPPABLE_CHARACT_EREXCEPTION) && ERROR_TIMES == 0) {
                // 编码转换再读取一次 编码转换在方法入口
                ERROR_TIMES++;
                updateScriptFile(sourcePath, targetPath);
            } else if (e.toString().startsWith(UN_MAPPABLE_CHARACT_EREXCEPTION)) {
                FAIL_MESSAGE.append(String.format("请检查[ %s ]编码格式,转换文件格式为[ GBK ]", sourcePath));
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
                coverMultipleFile(coverFile, coverFileDirectory.getAbsolutePath());
            }
            savePathStatus(STATUS_TYPE_COVER, coverFileDirectory.getAbsolutePath());
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
        String inputPath = file.getAbsolutePath().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
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
                    String[] subInputPath = inputPath.trim().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH).split(SYMBOL_BLANK_SPACE);
                    String sourcePath = subInputPath[subInputPath.length - 1].trim();
                    if (!sourcePath.isEmpty()) {
                        READ_NUM++;
                        logger.info(String.format("复制文件[ %s ]", sourcePath));
                        String exportPath = sourcePath.replace(WORKSPACE, EXPORT_WORKSPACE + CURRENT_DATE + SYMBOL_BACKSLASH);
                        copySingleFile(sourcePath, exportPath);
                    }
                }
            }
            savePathStatus(STATUS_TYPE_COPY, null);
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
        int lastIndex = exportPath.lastIndexOf(SYMBOL_BACKSLASH);
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
                }
                fileOutputStream.flush();
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
        String statusFilename = statusPath + SYMBOL_BACKSLASH + fileName + FILE_SUFFIX;
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
                if (STATUS_TYPE_COPY.equals(statusType) || STATUS_TYPE_MERGE.equals(statusType)) {
                    logger.error(String.format("若源文件路径存在中文,请检查[ %s ]编码格式,转换文件格式为[ GBK ]", FILE_PATH));
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
                    String[] subInputPath = inputPath.trim().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH).split(SYMBOL_BLANK_SPACE);
                    String path = subInputPath[subInputPath.length - 1].trim();
                    logger.info(String.format("合并文件[ %s ]", path));
                    CONTENT.append(getFileContent(path));
                }
            }
            savePathStatus(STATUS_TYPE_MERGE, createFile(CONTENT.toString()));
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
        String statusFilename = statusPath + SYMBOL_BACKSLASH + fileName + FILE_SUFFIX;
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
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static void getProperties() {
        Map<String, String> config = getConfigProperties();
        String operateType = config.get("operateType");
        if (StringUtils.isNotBlank(operateType)) {
            OPERATE_TYPE = operateType;
        }
        logger.info(String.format("文件操作模式[ %s ]", OPERATE_TYPE));
        String workspace = config.get("workspace");
        if (OPERATE_TYPE_COPY.equals(OPERATE_TYPE) || OPERATE_TYPE_COVER.equals(OPERATE_TYPE) || OPERATE_TYPE_UPDATE.equals(OPERATE_TYPE)) {
            if (StringUtils.isBlank(workspace)) {
                throw new RuntimeException("请设置源文件工作目录[ workspace ]");
            }
            if (!workspace.endsWith(SYMBOL_SLASH) && !workspace.endsWith(SYMBOL_BACKSLASH)) {
                workspace += SYMBOL_BACKSLASH;
            }
            WORKSPACE = workspace.replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
            logger.info(String.format("源文件工作目录[ %s ]", WORKSPACE));
        }
        String multipleVersion = config.get("multiple.version");
        String multipleExportWorkspace = config.get("multiple.exportWorkspace");
        if (StringUtils.isNotBlank(multipleVersion) && StringUtils.isNotBlank(multipleExportWorkspace)) {
            String[] versionList = multipleVersion.split(SYMBOL_COMMA);
            String[] exportWorkspaceList = multipleExportWorkspace.split(SYMBOL_COMMA);
            if (versionList.length != exportWorkspaceList.length) {
                throw new RuntimeException("多路径版本号[ multiple.version ]与多路径工作目录[ multiple.exportWorkspace ]不匹配");
            }
            for (int i = 0; i < versionList.length; i++) {
                String[] version = versionList[i].split(SYMBOL_COLON);
                if (version.length != 2) {
                    throw new RuntimeException(String.format("多路径版本号[ multiple.version ]中[ %s ]格式错误", versionList[i]));
                }
                MULTIPLE_VERSION.put(version[0], version[1]);
                MULTIPLE_EXPORTWORKSPACE.put(version[0], exportWorkspaceList[i]);
            }
            logger.info("请选择版本号:");
            Iterator<Map.Entry<String, String>> iterator = MULTIPLE_VERSION.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                logger.info(String.format("[ %s ]%s", item.getKey(), item.getValue()));
            }
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String code = scanner.next();
                if (StringUtils.isBlank(MULTIPLE_VERSION.get(code))) {
                    logger.info("版本号不存在,请重新选择");
                } else {
                    EXPORT_WORKSPACE = MULTIPLE_EXPORTWORKSPACE.get(code);
                    logger.info(String.format("导出文件工作目录[ %s ]", EXPORT_WORKSPACE));
                    scanner.close();
                    break;
                }
            }
        } else {
            String exportWorkspace = config.get("exportWorkspace");
            if (StringUtils.isNotBlank(exportWorkspace)) {
                if (!exportWorkspace.endsWith(SYMBOL_SLASH) && !exportWorkspace.endsWith(SYMBOL_BACKSLASH)) {
                    exportWorkspace += SYMBOL_BACKSLASH;
                }
                EXPORT_WORKSPACE = exportWorkspace.replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
                logger.info(String.format("导出文件工作目录[ %s ]", EXPORT_WORKSPACE));
            } else {
                throw new RuntimeException("请设置导出文件工作目录[ exportWorkspace ]");
            }
        }
        String multipleLineContent = config.get("multiple.lineContent");
        if (StringUtils.isNotBlank(multipleLineContent)) {
            String[] lineContent = multipleLineContent.split(SYMBOL_COMMA);
            for (int i = 0; i < lineContent.length; i++) {
                String[] content = lineContent[i].split(SYMBOL_COLON);
                if (content.length != 3) {
                    throw new RuntimeException(String.format("多文件定位行[ multiple.lineContent ]中[ %s ]格式错误", lineContent[i]));
                }
                MULTIPLE_LINE_CONTENT.put(content[0], content);
            }
        }
        if (OPERATE_TYPE_MERGE.equals(OPERATE_TYPE) || OPERATE_TYPE_UPDATE.equals(OPERATE_TYPE)) {
            String encoding = config.get("encoding");
            if (StringUtils.isNotBlank(encoding)) {
                ENCODING = encoding;
            }
            logger.info(String.format("文件编码格式[ %s ]", ENCODING));
        }
        String fileSuffix = config.get("fileSuffix");
        if (StringUtils.isNotBlank(fileSuffix)) {
            FILE_SUFFIX = fileSuffix;
        }
        logger.info(String.format("文件后缀[ %s ]", FILE_SUFFIX));
        if (OPERATE_TYPE_UPDATE.equals(OPERATE_TYPE)) {
            String lineContent = config.get("lineContent");
            if (StringUtils.isNotBlank(lineContent)) {
                LINE_CONTENT = lineContent;
            }
            logger.info(String.format("指定行内容[ %s ]", LINE_CONTENT));
            String lineOffset = config.get("lineOffset");
            if (StringUtils.isNotBlank(lineOffset)) {
                LINE_OFFSET = Integer.valueOf(lineOffset);
            }
            logger.info(String.format("指定行偏移量[ %s ]", LINE_OFFSET));
            String deleteAfterSuccess = config.get("deleteAfterSuccess");
            if (StringUtils.isNotBlank(deleteAfterSuccess)) {
                DELETE_AFTER_SUCCESS = Boolean.valueOf(deleteAfterSuccess);
            }
            logger.info(String.format("成功后删除源文件[ %s ]", DELETE_AFTER_SUCCESS));
        }
        String pauseMode = config.get("pauseMode");
        if (StringUtils.isNotBlank(pauseMode)) {
            PAUSE_MODE = Boolean.valueOf(pauseMode);
        }
        logger.info(String.format("暂停模式[ %s ]", PAUSE_MODE));
        if (PAUSE_MODE) {
            String pauseSuccessTime = config.get("pauseSuccessTime");
            if (StringUtils.isNotBlank(pauseSuccessTime)) {
                PAUSE_SUCCESS_TIME = Integer.valueOf(pauseSuccessTime);
            }
            logger.info(String.format("成功暂停时间[ %s ]", PAUSE_SUCCESS_TIME));
            String pauseFailTime = config.get("pauseFailTime");
            if (StringUtils.isNotBlank(pauseFailTime)) {
                PAUSE_FAIL_TIME = Integer.valueOf(pauseFailTime);
            }
            logger.info(String.format("失败暂停时间[ %s ]", PAUSE_FAIL_TIME));
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
        Map<String, String> config = new HashMap();
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
                        throw new RuntimeException(String.format("[ %s ]文件中[ %s ]格式错误", PROPERTIES_PATH, inputPath.trim()));
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
}
