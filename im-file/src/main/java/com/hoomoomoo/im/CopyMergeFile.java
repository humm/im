package com.hoomoomoo.im;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @Author hoomoomoo
 * @Description 文件复制合并
 * @package com.hoomoomoo.im
 * @Date 2020/06/07
 */

public class CopyMergeFile {

    private static final Logger logger = LoggerFactory.getLogger(CopyMergeFile.class);

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
    private static String FILE_SUFFIX = "txt";

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
    private static final String SYMBOL_MINUS  = "-";

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
     * 格式化模板
     */
    private static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS);
        CURRENT_DATE = simpleDateFormat.format(new Date());
        try{
            // 设置启动模式
            getStartMode();
            // 读取配置文件参数
            getProperties();
            if(OPERATE_TYPE.equals(OPERATE_TYPE_COPY)){
                // 复制文件
                copyPatch();
            }else if(OPERATE_TYPE.equals(OPERATE_TYPE_MERGE)){
                // 合并文件
                mergePatch();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置启动模式
     */
    private static void getStartMode(){
        URL url = CopyMergeFile.class.getResource("CopyMergeFile.class");
        if(url.toString().startsWith(START_MODE_JAR)){
            START_MODE = START_MODE_JAR;
        }else{
            START_MODE = START_MODE_PROJECT;
        }
    }

    /**
     * 读取配置文件参数
     */
    private static void getProperties(){
        InputStream pro;
        try {
            if(START_MODE_PROJECT.equals(START_MODE)){
                pro = CopyMergeFile.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
            }else{
                pro = new FileInputStream(new File(PROPERTIES_PATH));
            }
            Properties config = new Properties();
            config.load(pro);
            String operateType = config.getProperty("operateType");
            if (StringUtils.isNotBlank(operateType)) {
                OPERATE_TYPE = operateType;
            }
            String workspace = config.getProperty("workspace");
            if (StringUtils.isNotBlank(workspace)) {
                if (!workspace.endsWith(SYMBOL_SLASH) && workspace.endsWith(SYMBOL_BACKSLASH)) {
                    workspace += SYMBOL_BACKSLASH;
                }
                WORKSPACE = workspace;
                logger.info(String.format("导出源文件工作目录: %s", workspace));
            } else if (OPERATE_TYPE_COPY.equals(OPERATE_TYPE)) {
                throw new RuntimeException("请设置导出源文件工作目录[workspace]");
            }
            String exportWorkspace = config.getProperty("exportWorkspace");
            if (StringUtils.isNotBlank(exportWorkspace)) {
                if (!exportWorkspace.endsWith(SYMBOL_SLASH) && !exportWorkspace.endsWith(SYMBOL_BACKSLASH)) {
                    exportWorkspace += SYMBOL_BACKSLASH;
                }
                EXPORT_WORKSPACE = exportWorkspace;
                logger.info(String.format("导出文件工作目录: %s", exportWorkspace));
            } else {
                throw new RuntimeException("请设置导出文件工作目录[exportWorkspace]");
            }
            String encoding = config.getProperty("encoding");
            if (StringUtils.isNotBlank(encoding)) {
                ENCODING = encoding;
            }
            if (OPERATE_TYPE_MERGE.equals(OPERATE_TYPE)) {
                logger.info(String.format("文件编码格式: %s", ENCODING));
            }
            logger.info(String.format("文件操作模式: %s", OPERATE_TYPE));
            String fileSuffix = config.getProperty("fileSuffix");
            if (StringUtils.isNotBlank(fileSuffix)) {
                FILE_SUFFIX = fileSuffix;
            }
            logger.info(String.format("输出文件后缀名称: %s", FILE_SUFFIX));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件
     */
    private static void copyPatch() {
        File file;
        if(START_MODE_PROJECT.equals(START_MODE)){
            file = new File(CopyMergeFile.class.getClassLoader().getResource(FILE_PATH).getFile());
        }else{
            file = new File(FILE_PATH);
        }
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("源文件配置文件: %s 不存在", FILE_PATH));
        }
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if(inputPath.trim().startsWith(SYMBOL_IGNORE)){
                        continue;
                    }
                    String[] subInputPath = inputPath.trim().replace(SYMBOL_SLASH,SYMBOL_BACKSLASH).split(SYMBOL_BLANK_SPACE);
                    String sourcePath = subInputPath[subInputPath.length - 1].trim();
                    logger.info(String.format("复制文件: %s", sourcePath));
                    if(!sourcePath.isEmpty()){
                        String exportPath = sourcePath.replace(WORKSPACE, EXPORT_WORKSPACE + CURRENT_DATE + SYMBOL_BACKSLASH);
                        copyFile(sourcePath, exportPath);
                        READ_NUM++;
                    }
                }
            }
            savePathStatus();
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
     * 设置复制结果状态
     */
    private static void savePathStatus(){
        String statusPath = EXPORT_WORKSPACE + CURRENT_DATE;
        File statusFolder = new File(statusPath);
        if(!statusFolder.exists()){
            statusFolder.mkdirs();
        }
        String fileName = SUCCESS;
        if(READ_NUM != COPY_NUM || EXCEPTION_STATUS){
            fileName = FAIL;
        }
        String statusFilename = statusPath + SYMBOL_BACKSLASH + fileName + FILE_SUFFIX;
        File file = new File(statusFilename);
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(MESSAGE.toString());
            if(SUCCESS.equals(fileName)){
                logger.info(String.format("文件复制完成,文件个数: %s",READ_NUM));
            }else{
                logger.error(String.format("文件复制失败,读取文件个数: %s,复制文件个数: %s", READ_NUM, COPY_NUM));
                logger.error(String.format("请检查[%s]编码格式,若乱码请尝试转换文件格式[UTF-8 或 GBK]", FILE_PATH));
                logger.error(MESSAGE.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            printStream.close();
        }
    }

    /**
     * 合并文件
     */
    private static void mergePatch() {
        File file;
        if(START_MODE_PROJECT.equals(START_MODE)){
            file = new File(CopyMergeFile.class.getClassLoader().getResource(FILE_PATH).getFile());
        }else{
            file = new File(FILE_PATH);
        }
        if (!file.exists() || file.isDirectory()) {
            throw new RuntimeException(String.format("源文件配置文件: %s 不存在", FILE_PATH));
        }
        BufferedReader bufferedReader = null;
        try {
            String inputPath;
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((inputPath = bufferedReader.readLine()) != null) {
                if (!inputPath.isEmpty()) {
                    if(inputPath.trim().startsWith(SYMBOL_IGNORE)){
                        continue;
                    }
                    String[] subInputPath = inputPath.trim().replace(SYMBOL_SLASH,SYMBOL_BACKSLASH).split(SYMBOL_BLANK_SPACE);
                    String path = subInputPath[subInputPath.length - 1].trim();
                    logger.info(String.format("合并文件: %s", path));
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
     * 获取文件内容
     *
     * @param fileName
     * @return
     */
    private static String getFileContent(String fileName){
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
    private static void createFile(String content){
        String statusPath = EXPORT_WORKSPACE + CURRENT_DATE;
        File statusFolder = new File(statusPath);
        if(!statusFolder.exists()){
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
            logger.info(String.format("文件合并完成,文件个数: %s", READ_NUM));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}
