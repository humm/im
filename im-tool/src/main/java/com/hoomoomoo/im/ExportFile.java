package com.hoomoomoo.im;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;

/**
 * @Author hoomoomoo
 * @Description 文件复制合并
 * @package com.hoomoomoo.im
 * @Date 2020/06/07
 */

public class ExportFile {

    private static final Logger logger = LoggerFactory.getLogger(ExportFile.class);

    /**
     * 工作目录前缀
     */
    private static String WORKSPACE = "";

    /**
     * 导出文件目录
     */
    private static String EXPORT_WORKSPACE = "";

    /**
     * 文件地址读取文件
     */
    private static String FILE_PATH = "filePath.txt";

    /**
     * 配置文件
     */
    private static String PROPERTIES_PATH = "application.properties";

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
     * 错误提示消息
     */
    private static StringBuffer MESSAGE = new StringBuffer();

    /**
     * 编码格式
     */
    private static String ENCODING = "GBK";

    /**
     * 输出文件后缀
     */
    private static String FILE_SUFFIX = ".txt";

    /**
     * 合并文件内容
     */
    private static StringBuffer CONTENT = new StringBuffer();

    /**
     * 斜杠
     */
    private static final String SYMBOL_SLASH = "/";

    /**
     * 反斜杠
     */
    private static final String SYMBOL_BACKSLASH = "\\";

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
     * 减号
     */
    private static final String SYMBOL_MINUS = "-";

    /**
     * 换行
     */
    private static final String SYMBOL_NEXT_LINE = "\n";
    /**
     * 注释
     */
    private static final String SYMBOL_IGNORE = "--";

    /**
     * 成功
     */
    private static final String SUCCESS = "success";

    /**
     * 失败
     */
    private static final String FAIL = "fail";

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
     * 文件操作模式 1:单文件复制  2:文件合并
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
     * 格式化模板
     */
    private static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";


    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS);
        CURRENT_DATE = simpleDateFormat.format(new Date());
        try {
            // 设置启动模式
            getStartMode();
            // 读取配置文件参数
            getProperties();
            if (OPERATE_TYPE.equals(OPERATE_TYPE_COPY)) {
                // 复制文件
                copyPatch();
            } else if (OPERATE_TYPE.equals(OPERATE_TYPE_MERGE)) {
                // 合并文件
                mergePatch();
            } else if (OPERATE_TYPE.equals(OPERATE_TYPE_COVER)) {
                // 覆盖文件
                coverPatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 覆盖文件入口
     */
    private static void coverPatch() {
        File fileDirectory = new File(WORKSPACE);
        if (!fileDirectory.isDirectory()) {
            throw new RuntimeException(String.format("源文件工作目录[ %s ]不存在", WORKSPACE));
        }
        File[] fileDirectoryList = fileDirectory.listFiles();
        if (fileDirectoryList != null) {
            Arrays.sort(fileDirectoryList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long sort = o2.lastModified() - o1.lastModified();
                    if (sort > 0) {
                        return 1;
                    } else if (sort == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            File coverFileDirectory = fileDirectoryList[0];
            if (!coverFileDirectory.isDirectory()) {
                throw new RuntimeException(String.format("[ %s ]不是文件夹", coverFileDirectory.getAbsolutePath()));
            }
            File[] coverFileList = coverFileDirectory.listFiles();
            for (File coverFile : coverFileList) {
                coverFile(coverFile, coverFileDirectory.getAbsolutePath());
            }
            savePathStatus(coverFileDirectory.getAbsolutePath());
        }
    }

    /**
     * 覆盖文件
     */
    private static void coverFile(File file, String fileDirectory) {
        if (!file.isDirectory()) {
            cover(file, fileDirectory);
        } else {
            File[] fileList = file.listFiles();
            for (File single : fileList) {
                if (single.isDirectory()) {
                    coverFile(single, fileDirectory);
                } else {
                    cover(single, fileDirectory);
                }
            }
        }

    }

    /**
     * 覆盖单个文件
     */
    private static void cover(File file, String fileDirectory) {
        if ((SUCCESS + FILE_SUFFIX).equals(file.getName()) || (FAIL + FILE_SUFFIX).equals(file.getName())) {
            return;
        }
        String inputPath = file.getAbsolutePath().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
        String[] exportWorkspaceList = EXPORT_WORKSPACE.split(SYMBOL_COMMA);
        for (String exportWorkspace : exportWorkspaceList) {
            String exportPath = inputPath.replace(fileDirectory, exportWorkspace);
            copyFile(inputPath, exportPath);
            logger.info(String.format("覆盖文件[ %s ]", inputPath));
            READ_NUM++;
        }
    }

    /**
     * 复制文件入口
     */
    private static void copyPatch() {
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(checkFile()));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    String[] subInputPath = inputPath.trim().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH).split(SYMBOL_BLANK_SPACE);
                    String sourcePath = subInputPath[subInputPath.length - 1].trim();
                    if (!sourcePath.isEmpty()) {
                        logger.info(String.format("复制文件[ %s ]", sourcePath));
                        String exportPath = sourcePath.replace(WORKSPACE, EXPORT_WORKSPACE + CURRENT_DATE + SYMBOL_BACKSLASH);
                        copyFile(sourcePath, exportPath);
                        READ_NUM++;
                    }
                }
            }
            savePathStatus(null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MESSAGE.append(e.toString()).append(SYMBOL_NEXT_LINE);
            EXCEPTION_STATUS = true;
        } catch (IOException e) {
            e.printStackTrace();
            MESSAGE.append(e.toString()).append(SYMBOL_NEXT_LINE);
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
     * 复制文件
     *
     * @param sourcePath
     * @param exportPath
     */
    private static void copyFile(String sourcePath, String exportPath) {
        int lastIndex = exportPath.lastIndexOf(SYMBOL_BACKSLASH);
        String path = exportPath.substring(0, lastIndex);
        File inFile = new File(sourcePath);
        File outFile = new File(exportPath);
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
                MESSAGE.append(SUCCESS).append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                MESSAGE.append(e.toString()).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(FAIL).append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE);
                EXCEPTION_STATUS = true;
            } catch (IOException e) {
                e.printStackTrace();
                MESSAGE.append(e.toString()).append(SYMBOL_NEXT_LINE);
                MESSAGE.append(FAIL).append(SYMBOL_SPACE).append(sourcePath).append(SYMBOL_NEXT_LINE);
                EXCEPTION_STATUS = true;
            } finally {
                try {
                    fileInputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置复制、覆盖结果状态
     */
    private static void savePathStatus(String coverDirectory) {
        String statusPath = EXPORT_WORKSPACE + CURRENT_DATE;
        if (StringUtils.isNotBlank(coverDirectory)) {
            statusPath = coverDirectory;
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
                String msg = "文件复制完成,文件数量[ %s ]";
                if (StringUtils.isNotBlank(coverDirectory)) {
                    msg = "文件覆盖完成,文件数量[ %s ]";
                }
                logger.info(String.format(msg, READ_NUM));
            } else {
                String msg = "文件复制失败,读取文件数量[ %s ],复制文件数量[ %s ]";
                if (StringUtils.isNotBlank(coverDirectory)) {
                    msg = "文件覆盖失败,读取文件数量[ %s ],覆盖文件数量[ %s ]";
                }
                logger.error(String.format(msg, READ_NUM, COPY_NUM));
                if (StringUtils.isBlank(coverDirectory)) {
                    logger.error(String.format("请检查[ %s ]编码格式,请尝试转换文件格式为[ UTF-8或GBK ]", FILE_PATH));
                }
                logger.error(MESSAGE.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            printStream.close();
        }
    }

    /**
     * 合并文件入口
     */
    private static void mergePatch() {
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(checkFile()));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if (inputPath.trim().startsWith(SYMBOL_IGNORE)) {
                        continue;
                    }
                    String[] subInputPath = inputPath.trim().replace(SYMBOL_SLASH, SYMBOL_BACKSLASH).split(SYMBOL_BLANK_SPACE);
                    String path = subInputPath[subInputPath.length - 1].trim();
                    logger.info(String.format("合并文件[ %s ]", path));
                    CONTENT.append(getFileContent(path));
                    READ_NUM++;
                }
            }
            createFile(CONTENT.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
     * @return
     */
    private static String getFileContent(String fileName) {
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
            String content;
            while ((content = reader.readLine()) != null) {
                stringBuffer.append(content).append(SYMBOL_NEXT_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
     * 合并文件
     *
     * @param content
     */
    private static void createFile(String content) {
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
            logger.info(String.format("文件合并完成,文件数量[ %s ]", READ_NUM));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    /**
     * @param
     * @author: 校验文件是否存在
     * @date: 2020/08/09
     * @return:
     */
    private static File checkFile() {
        File file;
        if (START_MODE_PROJECT.equals(START_MODE)) {
            file = new File(ExportFile.class.getClassLoader().getResource(FILE_PATH).getFile());
        } else {
            file = new File(FILE_PATH);
        }
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("源文件配置文件[ %s ] 不存在", FILE_PATH));
        }
        return file;
    }

    /**
     * 设置启动模式
     */
    private static void getStartMode() {
        URL url = ExportFile.class.getResource("ExportFile.class");
        if (url.toString().startsWith(START_MODE_JAR)) {
            START_MODE = START_MODE_JAR;
        } else {
            START_MODE = START_MODE_PROJECT;
        }
    }

    /**
     * 读取配置文件参数
     */
    private static void getProperties() {
        InputStream pro;
        try {
            if (START_MODE_PROJECT.equals(START_MODE)) {
                pro = ExportFile.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
            } else {
                pro = new FileInputStream(new File(PROPERTIES_PATH));
            }
            Properties config = new Properties();
            config.load(pro);
            String operateType = config.getProperty("operateType");
            if (StringUtils.isNotBlank(operateType)) {
                OPERATE_TYPE = operateType;
            }
            logger.info(String.format("文件操作模式[ %s ]", OPERATE_TYPE));
            String workspace = config.getProperty("workspace");
            if (StringUtils.isNotBlank(workspace)) {
                if (!workspace.endsWith(SYMBOL_SLASH) && !workspace.endsWith(SYMBOL_BACKSLASH)) {
                    workspace += SYMBOL_BACKSLASH;
                }
                WORKSPACE = workspace.replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
                logger.info(String.format("源文件工作目录[ %s ]", WORKSPACE));
            } else if (OPERATE_TYPE_COPY.equals(OPERATE_TYPE) || OPERATE_TYPE_COVER.equals(OPERATE_TYPE)) {
                throw new RuntimeException("请设置源文件工作目录[ workspace ]");
            }
            String exportWorkspace = config.getProperty("exportWorkspace");
            if (StringUtils.isNotBlank(exportWorkspace)) {
                if (!exportWorkspace.endsWith(SYMBOL_SLASH) && !exportWorkspace.endsWith(SYMBOL_BACKSLASH)) {
                    exportWorkspace += SYMBOL_BACKSLASH;
                }
                EXPORT_WORKSPACE = exportWorkspace.replace(SYMBOL_SLASH, SYMBOL_BACKSLASH);
                logger.info(String.format("导出文件工作目录[ %s ]", EXPORT_WORKSPACE));
            } else {
                throw new RuntimeException("请设置导出文件工作目录[ exportWorkspace ]");
            }
            String encoding = config.getProperty("encoding");
            if (StringUtils.isNotBlank(encoding)) {
                ENCODING = encoding;
            }
            if (OPERATE_TYPE_MERGE.equals(OPERATE_TYPE)) {
                logger.info(String.format("文件编码格式[ %s ]", ENCODING));
            }
            String fileSuffix = config.getProperty("fileSuffix");
            if (StringUtils.isNotBlank(fileSuffix)) {
                FILE_SUFFIX = fileSuffix;
            }
            logger.info(String.format("文件后缀[ %s ]", FILE_SUFFIX));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
