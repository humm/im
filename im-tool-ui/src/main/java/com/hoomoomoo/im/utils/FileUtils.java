package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseDto;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hswebframework.utils.file.EncodingDetect;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author humm23693
 * @description 文件工具类
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/23
 */
public class FileUtils {

    private final static String ENCODING_UTF8 = "UTF-8";
    private final static String ENCODING_GBK = "GBK";
    private final static String ENCODING_GB = "GB";

    private final static String FILE_TYPE_NORMAL = "1";
    private final static String FILE_TYPE_CONFIG = "2";

    private final static String ANNOTATION_NORMAL = "--";
    private final static String ANNOTATION_CONFIG = "#";

    private final static String STR_EMPTY = "";
    private final static String STR_EQUALS = "=";
    private final static String STR_POINT = ".";
    private static final String STR_SLASH = "/";

    private final static Integer NUM_2 = 2;

    private static final Pattern PATTERN = Pattern.compile("(\\\\u(\\w{4}))");

    public static final String START_MODE_JAR = ".jar!";

    public static final String FILE_TYPE_JAR = ".jar";
    public static final String FILE_TYPE_CONF = "conf";
    public static final String FILE_TYPE_SLASH = "file:/";

    /**
     * 读取配置文件
     *
     * @param filePath
     * @author: humm23693
     * @date: 2021/04/24
     * @return:
     */
    public static BaseDto readConfigFileToObject(String filePath, Class<?> clazz) throws Exception {
        return (BaseDto) mapToObject(readConfigFileToMap(filePath), clazz);
    }

    /**
     * 读取配置文件
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static Map<String, String> readConfigFileToMap(String filePath) throws IOException {
        return convertMap((HashMap<String, String>) readFile(filePath, FILE_TYPE_CONFIG));
    }


    /**
     * 读取正常文件
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static List<String> readNormalFile(String filePath) throws IOException {
        return (List<String>) readFile(filePath, FILE_TYPE_NORMAL);
    }

    /**
     * 读取文件
     *
     * @param filePath
     * @param fileType 文件类型 1：普通文件 2：配置文件
     * @author: humm23693
     * @date: 2021/04/24
     * @return:
     */
    private static Object readFile(String filePath, String fileType) throws IOException {
        List<String> fileContent = new LinkedList();
        HashMap<String, String> fileContentMap = new HashMap<>(16);
        BufferedReader bufferedReader = null;
        bufferedReader = getBufferedReader(filePath);
        String content;
        while ((content = bufferedReader.readLine()) != null) {
            if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(content.trim())) {
                if (FILE_TYPE_NORMAL.equals(fileType)) {
                    buildFileContent(fileContent, content);
                } else if (FILE_TYPE_CONFIG.equals(fileType)) {
                    buildFileContentMap(filePath, fileContentMap, content);
                }
            }
        }
        return FILE_TYPE_NORMAL.equals(fileType) ? fileContent : fileContentMap;
    }

    /**
     * 生成返回结果
     *
     * @param fileContent
     * @param content
     * @author: humm23693
     * @date: 2021/04/24
     * @return:
     */
    private static void buildFileContent(List<String> fileContent, String content) {
        if (content.startsWith(ANNOTATION_NORMAL)) {
            return;
        }
        fileContent.add(content);
    }

    /**
     * 生成返回结果
     *
     * @param fileContentMap
     * @param content
     * @author: humm23693
     * @date: 2021/04/24
     * @return:
     */
    private static void buildFileContentMap(String filePath, Map<String, String> fileContentMap, String content) {
        if (content.startsWith(ANNOTATION_CONFIG)) {
            return;
        }
        if (!content.contains(STR_EQUALS)) {
            return;
        }
        String[] item = content.split(STR_EQUALS);
        if (item.length == NUM_2) {
            fileContentMap.put(item[0], convertUnicodeToChar(item[1]));
        } else {
            fileContentMap.put(item[0], STR_EMPTY);
        }
    }

    /**
     * 获取文件编码格式
     *
     * @param filePath
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    private static String getFileEncode(String filePath) {
        try {
            String fileEncode = EncodingDetect.getJavaEncode(filePath);
            if (StringUtils.startsWith(fileEncode.toUpperCase(), ENCODING_GB)) {
                return ENCODING_GBK;
            } else {
                return ENCODING_UTF8;
            }
        } catch (Exception e) {
            return ENCODING_GBK;
        }

    }

    /**
     * 获取文件读取类
     *
     * @param filePath
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    private static BufferedReader getBufferedReader(String filePath) throws FileNotFoundException,
            UnsupportedEncodingException {
        String fileEncode = getFileEncode(filePath);
        return new BufferedReader(new InputStreamReader(new FileInputStream(filePath), fileEncode));
    }

    /**
     * unicode转中文字符串
     *
     * @param str
     * @author: hoomoomoo
     * @date: 2020/09/03
     * @return:
     */
    private static String convertUnicodeToChar(String str) {
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
            str = str.replace(unicodeFull, singleChar + STR_EMPTY);
        }
        return str;
    }

    /**
     * map 转 javaBean
     *
     * @param map
     * @param clazz
     * @author: hoomoomoo
     * @date: 2020/12/02
     * @return:
     */
    private static Object mapToObject(Map<String, String> map, Class<?> clazz) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = clazz.newInstance();
        BeanUtils.populate(obj, map);
        return obj;
    }

    /**
     * map 转换
     *
     * @param map
     * @author: humm23693
     * @date: 2020/12/02
     * @return:
     */
    private static LinkedHashMap<String, String> convertMap(HashMap<String, String> map) {
        if (map == null) {
            return null;
        }
        LinkedHashMap<String, String> afterMap = new LinkedHashMap<>(map.size());
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> item = iterator.next();
            String key = item.getKey();
            String value = item.getValue();
            StringBuffer convertKey = new StringBuffer();
            boolean isPoint = false;
            for (int i = 0; i < key.length(); i++) {
                char single = key.charAt(i);
                if (String.valueOf(single).equals(STR_POINT)) {
                    isPoint = true;
                    continue;
                } else {
                    if (isPoint) {
                        convertKey.append(String.valueOf(single).toUpperCase());
                    } else {
                        convertKey.append(single);
                    }
                    isPoint = false;
                }
            }
            afterMap.put(convertKey.toString(), value);
        }
        return afterMap;
    }

    /**
     * 获取文件路径
     *
     * @param path
     * @author: humm23693
     * @date: 2021/04/26
     * @return:
     */
    public static URL getFilePath(String path) {
        URL url = FileUtils.class.getResource(path);
        if (url.getPath().contains(START_MODE_JAR)) {
            String jarPath = url.getPath();
            int jarIndex = jarPath.indexOf(START_MODE_JAR);
            String filePath = jarPath.substring(jarIndex + 1);
            filePath = filePath.substring(filePath.indexOf(STR_SLASH));
            String folderPath = jarPath.substring(0, jarIndex);
            folderPath = folderPath.substring(0, folderPath.lastIndexOf(STR_SLASH));
            try {
                return new URL(folderPath + filePath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * 解压jar包
     *
     * @param path
     * @author: humm23693
     * @date: 2021/04/26
     * @return:
     */
    public static void UnJar(String path) throws Exception {
        URL sourceUrl = FileUtils.class.getResource(path);
        if (sourceUrl.getPath().contains(START_MODE_JAR)) {
            URL url = getFilePath(path);
            File file = new File(url.getPath());
            if (!file.exists()) {
                // 解压文件
                String jarPath = sourceUrl.getPath().replace(FILE_TYPE_SLASH, STR_EMPTY);
                int jarIndex = jarPath.indexOf(START_MODE_JAR);
                String filePath = jarPath.substring(0, jarIndex);
                String fileName = filePath.substring(0, jarIndex) + FILE_TYPE_JAR;
                String folder = filePath.substring(0, filePath.lastIndexOf(STR_SLASH));
                // 生成临时解压文件夹
                String tempFolder = folder + STR_SLASH + CommonUtils.getCurrentDateTime2() + STR_SLASH;
                File temp = new File(tempFolder);
                if (!temp.exists()) {
                    temp.mkdirs();
                }
                UnZipAnRarUtils.unZip(new File(fileName), tempFolder);
                // 复制文件
                copyFolder(tempFolder + FILE_TYPE_CONF, folder);
                File[] fileList = new File(tempFolder).listFiles();
                for (File item : fileList) {
                    deleteFile(item);
                }
                deleteFile(new File(tempFolder));
            }
        }
    }

    /**
     * 删除文件
     *
     * @param file
     * @author: humm23693
     * @date: 2021/04/26
     * @return:
     */
    public static void deleteFile(File file) {
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
     * 复制文件夹
     *
     * @param resource 源路径
     * @param target   目标路径
     */
    public static void copyFolder(String resource, String target) throws Exception {
        File resourceFile = new File(resource);
        if (!resourceFile.exists()) {
            throw new Exception("源目标路径：[" + resource + "] 不存在...");
        }
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        // 获取源文件夹下的文件夹或文件
        File[] resourceFiles = resourceFile.listFiles();
        for (File file : resourceFiles) {
            File file1 = new File(targetFile.getAbsolutePath() + File.separator + resourceFile.getName());
            // 复制文件
            if (file.isFile()) {
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File targetFile1 = new File(file1.getAbsolutePath() + File.separator + file.getName());
                copyFile(file, targetFile1);
            }
            // 复制文件夹
            if (file.isDirectory()) {
                String dir1 = file.getAbsolutePath();
                String dir2 = file1.getAbsolutePath();
                copyFolder(dir1, dir2);
            }
        }

    }

    /**
     * 复制文件
     *
     * @param resource
     * @param target
     */
    public static void copyFile(File resource, File target) throws Exception {
        // 文件输入流并进行缓冲
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        byte[] bytes = new byte[1024 * 2];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
        }
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();
    }
}
