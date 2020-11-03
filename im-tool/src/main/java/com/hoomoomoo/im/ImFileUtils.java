package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.SvnUtils;
import com.hoomoomoo.im.utils.UnZipAnRarUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @Author hoomoomoo
 * @Description 文件工具类
 * @package com.hoomoomoo.im
 * @Date 2020/06/07
 */

public class ImFileUtils {

    /**
     * 应用名称
     */
    private static String NAME_CONTENT = "文件处理程序";

    /**
     * 应用版本
     */
    private static String NAME_VERSION = "   3.5.0";

    /**
     * 配置文件
     */
    private static final String PROPERTIES_PATH = "application.properties";

    /**
     * 文件路径地址
     */
    private static final String FILE_PATH = "path.txt";

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
     * 复制排除时间戳
     */
    private static Map<String, String> EXCLUDE_TIMESTAMP = new LinkedHashMap(16);

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
     * 连续操作模式
     */
    private static boolean OPERATE_CONTINUE = true;

    /**
     * 连续操作间隔时间
     */
    private static int OPERATE_CONTINUE_TIME = 5;

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
    private static int LINE_OFFSET = 0;

    /**
     * 成功后删除文件
     */
    private static boolean DELETE_AFTER_SUCCESS = true;

    /**
     * svn更新
     */
    private static boolean SVN_UPDATE = true;

    /**
     * svn 用户名
     */
    private static String SVN_USERNAME = "";

    /**
     * svn 版本号
     */
    private static Long SVN_VERSION = -1L;

    /**
     * svn 密码
     */
    private static String SVN_PASSWORD = "";

    /**
     * svn 文件后缀
     */
    private static String SVN_FILE_NAME = ".svn";

    /**
     * 模式选择颜色
     */
    private static String MODE_COLOR = "";

    /**
     * 成功信息颜色
     */
    private static String SUCCESS_COLOR = "";

    /**
     * 失败信息颜色
     */
    public static String ERROR_COLOR = "";

    /**
     * 参数信息颜色
     */
    private static String PARAMETER_COLOR = "";

    /**
     * 编码格式
     */
    private static String ENCODING = "GBK";

    /**
     * 提示文件后缀
     */
    private static String FILE_SUFFIX = ".txt";

    /**
     * 数据库升级脚本目录
     */
    private static final String FILE_PATH_UPDATE = "数据库升级脚本";

    /**
     * 升级脚本
     */
    private static final String FILE_NAME_UPDATE = "_升级脚本";

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
     * 模式重置
     */
    private static final String OPERATE_MODE_RESET = "0";

    /**
     * 退出程序
     */
    private static final String OPERATE_VERSION_EXIT = "0";

    /**
     * 启动模式 1:Jar包启动 2:工程启动
     */
    private static String START_MODE = "jar";

    /**
     * 操作版本
     */
    private static String OPERATE_VERSION = "";

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
     * svn更新
     */
    private static final String OPERATE_MODE_SVN = "mode.svn";

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
        // 控制台颜色控制开始
        AnsiConsole.systemInstall();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        CURRENT_DATE = simpleDateFormat.format(new Date());
        // 设置启动模式
        getStartMode();
        // 读取配置文件参数
        run(getConfigProperties(), true);
        // 控制台颜色控制开始
        AnsiConsole.systemUninstall();
    }

    /**
     * 主入口
     *
     * @param config
     * @author: hoomoomoo
     * @date: 2020/09/07
     * @return:
     */
    private static void run(Map config, boolean init) {
        try {
            // 获取配置参数
            getProperties(config, init);
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
                case OPERATE_MODE_SVN:
                    // svn更新
                    updateSvn();
                    break;
                default:
                    break;
            }
            continueOperate(config);
        } catch (Exception e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
            continueOperate(config);
        }
    }

    /**
     * 连续操作
     *
     * @param config
     * @author: hoomoomoo
     * @date: 2020/10/20
     * @return:
     */
    private static void continueOperate(Map config) {
        // 清除上一次操作信息
        clean();
        // 连续操作模式控制应用退出
        exit();
        // 连续操作模式
        run(config, false);
    }

    /**
     * 应用退出控制
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/09/08
     * @return:
     */
    private static void exit() {
        CommonUtils.println(SYMBOL_NEXT_LINE, SYMBOL_EMPTY, false);
        if (!OPERATE_CONTINUE) {
            CommonUtils.sleep(5);
            System.exit(0);
        }
        new Thread(() -> {
            // 连续操作模式
            while (StringUtils.isBlank(OPERATE_MODE)) {
                CommonUtils.sleep(1);
                OPERATE_CONTINUE_TIME--;
                if (OPERATE_CONTINUE_TIME == 0) {
                    System.exit(0);
                }
            }
        }).start();
    }

    /**
     * svn更新
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/09/19
     * @return:
     */
    private static void updateSvn() {
        Map updateMap = new HashMap(16);
        Iterator<Map.Entry<String, String[]>> iterator = VERSION_CONFIG.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> item = iterator.next();
            String[] version = item.getValue();
            if (updateMap.containsKey(version[2])) {
                continue;
            } else {
                updateMap.put(version[2], version[2]);
            }
            if (!updateSvn(version[2])) {
                EXCEPTION_STATUS = true;
                throw new RuntimeException("svn同步异常");
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
                        UnZipAnRarUtils.unZip(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH_1);
                    } else if (fileName.endsWith(FILE_TYPE_RAR)) {
                        UnZipAnRarUtils.unRar(file, fileDirectory.getAbsolutePath() + SYMBOL_BACKSLASH_1);
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
                    File updateFile = new File(EXPORT_WORKSPACE + FILE_PATH_UPDATE);
                    if (!updateFile.exists()) {
                        updateFile = new File(EXPORT_WORKSPACE);
                    }
                    getUpdateScriptPath(file.getName(), updateFile.getAbsolutePath());
                    if (StringUtils.isBlank(FILE_NAME_PATH_UPDATE)) {
                        int index = file.getName().lastIndexOf(SYMBOL_POINT_1);
                        String suffix = fileName.substring(index);
                        fileName = fileName.replace(FILE_NAME_UPDATE + suffix, suffix);
                        getUpdateScriptPath(fileName, updateFile.getAbsolutePath());
                        if (StringUtils.isBlank(FILE_NAME_PATH_UPDATE)) {
                            throw new RuntimeException(String.format("[ %s ]更新文件目录不匹配", file.getAbsolutePath()));
                        }
                    }
                    READ_NUM++;
                    CommonUtils.println(String.format("更新文件[ %s ]", FILE_NAME_PATH_UPDATE), SYMBOL_EMPTY);
                    updateScriptFile(file.getAbsolutePath(), FILE_NAME_PATH_UPDATE);
                }
            }
            if (DELETE_AFTER_SUCCESS && READ_NUM == COPY_NUM && !EXCEPTION_STATUS) {
                for (File file : fileList) {
                    CommonUtils.deleteFile(file);
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
        String inputPath = CommonUtils.convertBackslash(file.getAbsolutePath());
        String exportPath = inputPath.replace(fileDirectory, EXPORT_WORKSPACE);
        copySingleFile(inputPath, exportPath);
        CommonUtils.println(String.format("更新文件[ %s ]", inputPath), SYMBOL_EMPTY);
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
                    FILE_DIRECTORY_PATH_UPDATE = CommonUtils.convertBackslash(source.getParentFile().getAbsolutePath());
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
                FILE_NAME_PATH_UPDATE = CommonUtils.convertBackslash(file.getAbsolutePath());
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
        String fileContent = SYMBOL_NEXT_LINE + getFileContent(sourcePath);
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
                int lineOffset = LINE_OFFSET;
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
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
            // 内容还原
            try {
                Files.write(Paths.get(targetPath), sourceLines, Charset.forName(ENCODING));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
            CommonUtils.sortFile(fileDirectoryList);
            File coverFileDirectory = fileDirectoryList[0];
            if (!coverFileDirectory.isDirectory()) {
                throw new RuntimeException(String.format("[ %s ]不是文件夹", coverFileDirectory.getAbsolutePath()));
            }
            File[] coverFileList = coverFileDirectory.listFiles();
            for (File coverFile : coverFileList) {
                coverMultipleFile(coverFile, CommonUtils.convertBackslash(coverFileDirectory.getAbsolutePath()));
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
        String inputPath = CommonUtils.convertBackslash(file.getAbsolutePath());
        READ_NUM++;
        String exportPath = inputPath.replace(fileDirectory, EXPORT_WORKSPACE);
        copySingleFile(inputPath, exportPath);
        CommonUtils.println(String.format("覆盖文件[ %s ]", inputPath), SYMBOL_EMPTY);
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
        String exportWorkspace = EXPORT_WORKSPACE + CURRENT_DATE + SYMBOL_BACKSLASH_1;
        if (StringUtils.isNotBlank(EXCLUDE_TIMESTAMP.get(OPERATE_VERSION))) {
            exportWorkspace = EXPORT_WORKSPACE;
        }
        try {
            String inputPath;
            bufferedReader = getBufferedReader(FILE_PATH, true);
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    String[] subInputPath = CommonUtils.convertBackslash(inputPath.trim()).split(SYMBOL_BLANK_SPACE);
                    String sourcePath = subInputPath[subInputPath.length - 1].trim();
                    if (!sourcePath.isEmpty()) {
                        READ_NUM++;
                        CommonUtils.println(String.format("复制文件[ %s ]", sourcePath), SYMBOL_EMPTY);
                        String exportPath = null;
                        exportPath = sourcePath.replace(WORKSPACE, exportWorkspace);
                        copySingleFile(sourcePath, exportPath);
                        if (EXCEPTION_STATUS) {
                            break;
                        }
                    }
                }
            }
            savePathStatus(STATUS_MODE_COPY, exportWorkspace);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            EXCEPTION_STATUS = true;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
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
            msg.append(FAIL).append(" 文件不存在").append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE).toString();
            MESSAGE.append(msg);
            FAIL_MESSAGE.append(msg);
            EXCEPTION_STATUS = true;
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
        String fileName = SUCCESS;
        if (READ_NUM != COPY_NUM || EXCEPTION_STATUS) {
            fileName = FAIL;
        }
        if (StringUtils.isBlank(EXCLUDE_TIMESTAMP.get(OPERATE_VERSION))) {
            File statusFolder = new File(directory);
            if (!statusFolder.exists()) {
                statusFolder.mkdirs();
            }
            String statusFilename = directory + SYMBOL_BACKSLASH_1 + fileName + FILE_SUFFIX;
            File file = new File(statusFilename);
            PrintStream printStream = null;
            try {
                printStream = new PrintStream(new FileOutputStream(file));
                printStream.println(MESSAGE.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                EXCEPTION_STATUS = true;
            } finally {
                printStream.close();
            }
        }
        if (SUCCESS.equals(fileName)) {
            CommonUtils.println(String.format("文件%s完成 文件数量[ %s ]", statusType, READ_NUM), SUCCESS_COLOR);
        } else {
            CommonUtils.println(String.format("文件%s失败 读取文件数量[ %s ] %s文件数量[ %s ]", statusType, READ_NUM, statusType, COPY_NUM), ERROR_COLOR);
            CommonUtils.println(FAIL_MESSAGE.toString(), ERROR_COLOR);
            if (STATUS_MODE_COPY.equals(statusType) || STATUS_MODE_MERGE.equals(statusType)) {
                CommonUtils.println(String.format("请检查[ %s ]文件中路径是否存在", FILE_PATH), ERROR_COLOR);
            }
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
            bufferedReader = getBufferedReader(FILE_PATH, true);
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    READ_NUM++;
                    String[] subInputPath = CommonUtils.convertBackslash(inputPath.trim()).split(SYMBOL_BLANK_SPACE);
                    String path = subInputPath[subInputPath.length - 1].trim();
                    CommonUtils.println(String.format("合并文件[ %s ]", path), SYMBOL_EMPTY);
                    CONTENT.append(getFileContent(path));
                    if (EXCEPTION_STATUS) {
                        break;
                    }
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
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
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
                reader = getBufferedReader(fileName, false);
                String content;
                while ((content = reader.readLine()) != null) {
                    stringBuffer.append(content).append(SYMBOL_NEXT_LINE);
                }
                msg.append(SUCCESS).append(SYMBOL_SPACE).append(fileName).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(msg);
                COPY_NUM++;
            } else {
                msg.append(FAIL).append(" 文件不存在").append(SYMBOL_SPACE).append(fileName).append(SYMBOL_NEXT_LINE).toString();
                MESSAGE.append(msg);
                FAIL_MESSAGE.append(msg);
                EXCEPTION_STATUS = true;
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
        String contentFolderPath = EXPORT_WORKSPACE + CURRENT_DATE;
        String fileName = FILE_NAME_MERGE + SYMBOL_MINUS + READ_NUM;
        String statusFilename = contentFolderPath + SYMBOL_BACKSLASH_1 + fileName + FILE_SUFFIX;
        File contentFolder = new File(contentFolderPath);
        if (!contentFolder.exists()) {
            contentFolder.mkdirs();
        }
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
        return contentFolderPath;
    }

    /**
     * 校验文件是否存在
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    private static String checkFile(String fileName, boolean changeName) {
        if (START_MODE_PROJECT.equals(START_MODE) && changeName) {
            fileName = ImFileUtils.class.getClassLoader().getResource(fileName).getFile();
        }
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("源文件[ %s ] 不存在", fileName));
        }
        return fileName;
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
     * 读取配置文件参数
     *
     * @param config
     * @param init
     * @author: hoomoomoo
     * @date: 2020/09/07
     * @return:
     */
    private static void getProperties(Map<String, String> config, boolean init) {
        // 获取颜色配置
        String nameColor = config.get("im.name.color");
        String nameContent = config.get("im.name.content");
        if (StringUtils.isNotBlank(nameContent)) {
            NAME_CONTENT = nameContent;
        }
        String modeColor = config.get("im.mode.color");
        if (StringUtils.isNotBlank(modeColor)) {
            MODE_COLOR = modeColor;
        }
        String versionColor = config.get("im.version.color");
        String successColor = config.get("im.success.color");
        if (StringUtils.isNotBlank(successColor)) {
            SUCCESS_COLOR = successColor;
        }
        String errorColor = config.get("im.error.color");
        if (StringUtils.isNotBlank(errorColor)) {
            ERROR_COLOR = errorColor;
        }
        String parameterColor = config.get("im.parameter.color");
        if (StringUtils.isNotBlank(parameterColor)) {
            PARAMETER_COLOR = parameterColor;
        }

        // 颜色debug
        debugColor(config.get("im.color.debug"));
        if (init) {
            StringBuffer star = new StringBuffer(SYMBOL_STAR_3);
            for (int i = 0; i < NAME_CONTENT.length() * 4; i++) {
                star.append(SYMBOL_STAR);
            }
            CommonUtils.println(star.toString(), nameColor);
            CommonUtils.println(SYMBOL_STAR_3 + NAME_CONTENT + NAME_VERSION, nameColor);
            CommonUtils.println(star.toString(), nameColor);
        }

        // 获取模式配置
        getModeConfig(config);

        // 获取版本配置
        getVersionConfig(config);

        // 获取svn配置信息
        String svnUpdate = config.get("svn.update");
        if (StringUtils.isNotBlank(svnUpdate)) {
            SVN_UPDATE = Boolean.valueOf(svnUpdate);
        }
        if (SVN_UPDATE) {
            String svnUsername = config.get("svn.username");
            if (StringUtils.isNotBlank(svnUsername)) {
                SVN_USERNAME = svnUsername;
            } else {
                throw new RuntimeException("svn用户名[ svn.username ]不能为空");
            }
            String svnPassword = config.get("svn.password");
            if (StringUtils.isNotBlank(svnPassword)) {
                SVN_PASSWORD = svnPassword;
            } else {
                throw new RuntimeException("svn密码[ svn.password ]不能为空");
            }
        }

        // 模式选择
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String code = scanner.next();
            if (OPERATE_VERSION_EXIT.equals(code)) {
                System.exit(0);
            }
            if (MODE_CONFIG.get(code) == null) {
                CommonUtils.println("模式不存在 请重新选择", errorColor);
            } else {
                OPERATE_MODE = MODE_CONFIG.get(code)[2];
                CommonUtils.println(String.format("模式设置为[ %s ]", MODE_CONFIG.get(code)[1]), successColor);
                break;
            }
        }

        // 版本号选择
        if (!OPERATE_MODE_SVN.equals(OPERATE_MODE)) {
            CommonUtils.println("请选择版本:", SYMBOL_EMPTY);
            Iterator<Map.Entry<String, String[]>> iterator = VERSION_CONFIG.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String[]> item = iterator.next();
                if (item.getKey().startsWith(OPERATE_MODE)) {
                    CommonUtils.println(String.format("[ %s ] %s", item.getValue()[0], item.getValue()[item.getValue().length - 1]), versionColor);
                }
            }
            CommonUtils.println(String.format("[ %s ] 重选模式", OPERATE_MODE_RESET), versionColor);
            while (true) {
                String code = OPERATE_MODE + SYMBOL_POINT_1 + scanner.next();
                if (VERSION_CONFIG.get(code) == null) {
                    if ((OPERATE_MODE + SYMBOL_POINT_1 + OPERATE_MODE_RESET).equals(code)) {
                        getProperties(config, false);
                        return;
                    }
                    CommonUtils.println("版本不存在 请重新选择", errorColor);
                } else {
                    WORKSPACE = VERSION_CONFIG.get(code)[1];
                    EXPORT_WORKSPACE = VERSION_CONFIG.get(code)[2];
                    OPERATE_VERSION = VERSION_CONFIG.get(code)[4];
                    CommonUtils.println(String.format("版本设置为[ %s ]", VERSION_CONFIG.get(code)[4]), successColor);
                    CommonUtils.println(String.format("源文件工作目录[ %s ]", WORKSPACE), parameterColor);
                    CommonUtils.println(String.format("导出文件工作目录[ %s ]", EXPORT_WORKSPACE), parameterColor);
                    if (!updateSvn(EXPORT_WORKSPACE)) {
                        EXCEPTION_STATUS = true;
                        throw new RuntimeException("svn同步异常");
                    }
                    break;
                }
            }

            // 编码格式
            if (OPERATE_MODE_MERGE.equals(OPERATE_MODE) || OPERATE_MODE_UPDATE.equals(OPERATE_MODE)) {
                String encoding = config.get("encoding");
                if (StringUtils.isNotBlank(encoding)) {
                    ENCODING = encoding;
                }
                CommonUtils.println(String.format("文件编码格式[ %s ]", ENCODING), parameterColor);
            }

            // 文件后缀名称
            String fileSuffix = config.get("file.suffix");
            if (StringUtils.isNotBlank(fileSuffix)) {
                FILE_SUFFIX = fileSuffix;
            }
            CommonUtils.println(String.format("文件后缀名称[ %s ]", FILE_SUFFIX), parameterColor);
        }

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
            CommonUtils.println(String.format("更新模式文件定位[ %s ]", LINE_CONTENT), parameterColor);

            // 更新模式成功后删除源文件
            String deleteAfterSuccess = config.get("mode.update.delete.after.success");
            if (StringUtils.isNotBlank(deleteAfterSuccess)) {
                DELETE_AFTER_SUCCESS = Boolean.valueOf(deleteAfterSuccess);
            }
            CommonUtils.println(String.format("更新模式成功后删除源文件[ %s ]", DELETE_AFTER_SUCCESS), parameterColor);

            // 更新模式指定文件定位
            getMoreUpdateLine(config);
        }

        // 复制模式排除时间戳
        if (OPERATE_MODE_COPY.equals(OPERATE_MODE)) {
            String timestamp = config.get("mode.copy.exclude.timestamp");
            if (StringUtils.isNotBlank(timestamp)) {
                String[] items = timestamp.split(SYMBOL_DOLLAR);
                for (String item : items) {
                    EXCLUDE_TIMESTAMP.put(item, item);
                }
            }
        }

        // 连续操作模式
        String operateContinue = config.get("operate.continue");
        if (StringUtils.isNotBlank(operateContinue)) {
            OPERATE_CONTINUE = Boolean.valueOf(operateContinue);
        }
        CommonUtils.println(String.format("连续操作模式[ %s ]", OPERATE_CONTINUE), parameterColor);

        // 连续操作间隔时间
        String operateContinueTime = config.get("operate.continue.time");
        if (StringUtils.isNotBlank(operateContinueTime)) {
            OPERATE_CONTINUE_TIME = Integer.valueOf(operateContinueTime);
        }
        CommonUtils.println(String.format("连续操作间隔时间[ %s ]", OPERATE_CONTINUE_TIME), parameterColor);
    }

    /**
     * 读取配置文件
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/09/03
     * @return:
     */
    private static Map<String, String> getConfigProperties() {
        Map<String, String> config = new LinkedHashMap<>(16);
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = getBufferedReader(PROPERTIES_PATH, true);
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
                    config.put(item[0], CommonUtils.convertUnicodeToCh(item[1]));
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
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    /**
     * 获取更新模式指定文件定位
     *
     * @param config
     * @author: hoomoomoo
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
     * @author: hoomoomoo
     * @date: 2020/09/06
     * @return:
     */
    private static void getModeConfig(Map config) {
        if (config != null) {
            CommonUtils.println("请选择模式:", SYMBOL_EMPTY);
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
                    CommonUtils.println(String.format("[ %s ] %s", modeExtend[0], modeExtend[1]), MODE_COLOR);
                }
            }
            CommonUtils.println(String.format("[ %s ] %s", OPERATE_VERSION_EXIT, "退出"), MODE_COLOR);
        }
    }

    /**
     * 获取配置版本
     *
     * @param config
     * @author: hoomoomoo
     * @date: 2020/09/06
     * @return:
     */
    private static void getVersionConfig(Map config) {
        if (config != null) {
            Iterator<Map.Entry<String, String>> iterator = config.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                String versionName = item.getKey();
                if (versionName.contains(".version.") && versionName.startsWith("mode.")) {
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
                        versionExtend[1] = CommonUtils.convertBackslash(versionExtend[1] + SYMBOL_BACKSLASH_2);
                    }
                    if (!versionExtend[2].endsWith(SYMBOL_SLASH) && !versionExtend[2].endsWith(SYMBOL_BACKSLASH_2)) {
                        versionExtend[2] = CommonUtils.convertBackslash(versionExtend[2] + SYMBOL_BACKSLASH_2);
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
     * @author: hoomoomoo
     * @date: 2020/09/09
     * @return:
     */
    private static void clean() {
        WORKSPACE = SYMBOL_EMPTY;
        EXPORT_WORKSPACE = SYMBOL_EMPTY;
        OPERATE_MODE = SYMBOL_EMPTY;
        MESSAGE = new StringBuffer();
        FAIL_MESSAGE = new StringBuffer();
        CONTENT = new StringBuffer();
        READ_NUM = 0;
        COPY_NUM = 0;
        EXCEPTION_STATUS = false;
    }

    /**
     * 颜色debug
     *
     * @param debug
     * @author: hoomoomoo
     * @date: 2020/09/10
     * @return:
     */
    private static void debugColor(String debug) {
        if (StringUtils.isNotBlank(debug) && Boolean.valueOf(debug)) {
            CommonUtils.println("black.. black... black", "black");
            CommonUtils.println("red.. red... red", "red");
            CommonUtils.println("green.. green... green", "green");
            CommonUtils.println("yellow.. yellow... yellow", "yellow");
            CommonUtils.println("blue.. blue... blue", "blue");
            CommonUtils.println("magenta.. magenta... magenta", "magenta");
            CommonUtils.println("cyan.. cyan... cyan", "cyan");
            CommonUtils.println("white.. white... white", "white");
        }
    }

    /**
     * svn更新
     *
     * @param workspace
     * @author: hoomoomoo
     * @date: 2020/09/12
     * @return:
     */
    private static boolean updateSvn(String workspace) {
        CommonUtils.println(String.format("同步工作目录[ %s ]", workspace), PARAMETER_COLOR);
        if (SVN_UPDATE && CommonUtils.isSuffixDirectory(new File(workspace), SVN_FILE_NAME)) {
            CommonUtils.print("svn同步执行中...", SYMBOL_EMPTY, true, false);
            SVN_VERSION = -1L;
            executing();
            Long svnVersion = SvnUtils.update(SVN_USERNAME, SVN_PASSWORD, workspace);
            SVN_VERSION = svnVersion;
            while (true) {
                CommonUtils.sleep(1);
                if (SVN_VERSION > svnVersion) {
                    SVN_VERSION--;
                    break;
                }
            }
            return SVN_VERSION > 0;
        }
        CommonUtils.println("非svn目录 无需同步", SYMBOL_EMPTY);
        return true;
    }

    /**
     * 执行中标识
     *
     * @param
     * @author: hoomoomoo
     * @date: 2020/09/19
     * @return:
     */
    private static void executing() {
        new Thread(() -> {
            while (true) {
                CommonUtils.sleep(1);
                if (SVN_VERSION == -1L) {
                    CommonUtils.print(SYMBOL_POINT_1, SYMBOL_EMPTY, false, false);
                } else {
                    CommonUtils.println(SYMBOL_EMPTY, SYMBOL_EMPTY, false);
                    if (SVN_VERSION > 0) {
                        CommonUtils.println(String.format("svn已同步至版本[ %s ]", SVN_VERSION), SYMBOL_EMPTY);
                    }
                    SVN_VERSION++;
                    break;
                }
            }
        }).start();
    }

    /**
     * 获取文件编码格式
     *
     * @param fileName
     * @author: hoomoomoo
     * @date: 2020/10/17
     * @return:
     */
    private static BufferedReader getBufferedReader(String fileName, boolean changeName) throws FileNotFoundException,
            UnsupportedEncodingException {
        fileName = checkFile(fileName, changeName);
        String fileEncode = CommonUtils.getFileEncode(fileName);
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), fileEncode));
    }
}
