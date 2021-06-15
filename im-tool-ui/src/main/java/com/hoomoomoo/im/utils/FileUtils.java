package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.dto.BaseDto;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hswebframework.utils.file.EncodingDetect;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description 文件工具类
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/23
 */
public class FileUtils {

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
    public static LinkedHashMap<String, String> readConfigFileToMapIncludePoint(String filePath) throws IOException {
        return convertMap((HashMap<String, String>) readFile(filePath, FILE_TYPE_CONFIG, false), false);
    }

    /**
     * 读取配置文件
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static LinkedHashMap<String, String> readConfigFileToMap(String filePath) throws IOException {
        return convertMap((LinkedHashMap<String, String>) readFile(filePath, FILE_TYPE_CONFIG, false), true);
    }


    /**
     * 读取正常文件
     *
     * @param
     * @author: humm23693
     * @date: 2021/04/23
     * @return:
     */
    public static List<String> readNormalFile(String filePath, boolean skipAnnotation) throws IOException {
        return (List<String>) readFile(filePath, FILE_TYPE_NORMAL, skipAnnotation);
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param contentList
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static void writeFile(String filePath, List<String> contentList, boolean isAppend) throws IOException {
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }
        String content = SYMBOL_EMPTY;
        for (String item : contentList) {
            content += item + SYMBOL_NEXT_LINE;
        }
        writeFile(filePath, content, isAppend);
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param content
     * @author: humm23693
     * @date: 2021/04/28
     * @return:
     */
    public static void writeFile(String filePath, String content, boolean isAppend) throws IOException {
        // 判断文件夹是否存在
        String folderPath = filePath.substring(0, filePath.lastIndexOf(SYMBOL_SLASH));
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(filePath);
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, isAppend), getFileEncode(filePath))));
        printWriter.write(content);
        printWriter.flush();
        printWriter.close();
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
    public static Object readFile(String filePath, String fileType, boolean skipAnnotation) throws IOException {
        List<String> fileContent = new LinkedList();
        LinkedHashMap<String, String> fileContentMap = new LinkedHashMap<>(16);
        BufferedReader bufferedReader;
        bufferedReader = getBufferedReader(filePath);
        String content;
        while ((content = bufferedReader.readLine()) != null) {
            if (FILE_TYPE_NORMAL.equals(fileType)) {
                buildFileContent(fileContent, content, skipAnnotation);
            } else if (FILE_TYPE_CONFIG.equals(fileType)) {
                buildFileContentMap(fileContentMap, content);
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
    public static void buildFileContent(List<String> fileContent, String content, boolean skipAnnotation) {
        if (skipAnnotation && content.startsWith(ANNOTATION_TYPE_NORMAL)) {
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
    public static void buildFileContentMap(Map<String, String> fileContentMap, String content) {
        if (StringUtils.isBlank(content) && StringUtils.isBlank(content.trim())) {
            return;
        }
        if (content.startsWith(ANNOTATION_TYPE_CONFIG)) {
            return;
        }
        if (!content.contains(SYMBOL_EQUALS)) {
            return;
        }
        String[] item = content.split(SYMBOL_EQUALS);
        if (item.length == 2) {
            fileContentMap.put(item[0], convertUnicodeToChar(item[1]));
        } else if (item.length > 2) {
            int index = content.indexOf(SYMBOL_EQUALS) + 1;
            fileContentMap.put(item[0], convertUnicodeToChar(content.substring(index)));
        } else {
            fileContentMap.put(item[0], SYMBOL_EMPTY);
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
    public static String getFileEncode(String filePath) {
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
    public static BufferedReader getBufferedReader(String filePath) throws FileNotFoundException,
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
    public static String convertUnicodeToChar(String str) {
        Matcher matcher = STR_PATTERN.matcher(str);
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
     * map 转 javaBean
     *
     * @param map
     * @param clazz
     * @author: hoomoomoo
     * @date: 2020/12/02
     * @return:
     */
    public static Object mapToObject(Map<String, String> map, Class<?> clazz) throws Exception {
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
    public static LinkedHashMap<String, String> convertMap(HashMap<String, String> map, boolean deletePoint) {
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
            if (deletePoint) {
                boolean isPoint = false;
                for (int i = 0; i < key.length(); i++) {
                    char single = key.charAt(i);
                    if (String.valueOf(single).equals(SYMBOL_POINT)) {
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
            } else {
                convertKey.append(key);
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
    public static String getFilePath(String path) {
        String fileAbsolute = getPathFolder() + path;
        if (fileAbsolute.contains(START_MODE_JAR)) {
            fileAbsolute = new File(SYMBOL_EMPTY).getAbsolutePath() + path;
        }
        if (fileAbsolute.startsWith(SYMBOL_SLASH)) {
            fileAbsolute = fileAbsolute.substring(1);
        }
        return fileAbsolute.replaceAll("%20", " ");
    }

    /**
     * 获取文件路径
     *
     * @param path
     * @author: humm23693
     * @date: 2021/04/26
     * @return:
     */
    public static URL getFileUrl(String path) throws MalformedURLException {
        String file = getPathFolder() + path;
        if (file.contains(START_MODE_JAR)) {
            int jarIndex = file.indexOf(START_MODE_JAR);
            String filePath = file.substring(jarIndex + 1);
            filePath = filePath.substring(filePath.indexOf(SYMBOL_SLASH));
            String folderPath = file.substring(0, jarIndex);
            folderPath = folderPath.substring(0, folderPath.lastIndexOf(SYMBOL_SLASH));
            file = folderPath + filePath;
        } else {
            file = KEY_FILE + file;
        }
        return new URL(file);
    }


    public static String getPathFolder() {
        return FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    }

    /**
     * jar启动
     *
     * @param path
     * @author: humm23693
     * @date: 2021/06/03
     * @return:
     */
    public static boolean startByJar(String path) {
        return getPathFolder().contains(START_MODE_JAR);
    }

    /**
     * 获取jar名称
     *
     * @author: humm23693
     * @date: 2021/06/03
     * @return:
     */
    public static String getJarName() {
        String jarPath = getPathFolder().replace(KEY_FILE_SLASH, SYMBOL_EMPTY);
        int jarIndex = jarPath.indexOf(START_MODE_JAR);
        String filePath = jarPath.substring(0, jarIndex);
        return filePath.substring(0, jarIndex).replaceAll("%20", " ") + FILE_TYPE_JAR;
    }

    /**
     * 解压jar包
     *
     * @param path
     * @author: humm23693
     * @date: 2021/04/26
     * @return:
     */
    public static void unJar(String path) throws Exception {
        if (!startByJar(path)) {
            return;
        }
        String url = getFilePath(path);
        File file = new File(url);
        LinkedHashMap<String, String> oldAppConfig = new LinkedHashMap<>(16);

        // 临时备份历史配置文件名称及路径
        String bakFileName = file.getName() + FILE_TYPE_BAK;
        String bakFilePath = file.getParentFile().getParentFile().getPath();
        String bakFile = bakFilePath + SYMBOL_SLASH + bakFileName;

        if (file.exists()) {
            // 读取历史配置文件
            oldAppConfig = FileUtils.readConfigFileToMapIncludePoint(url);

            // 备份历史配置文件
            copyFile(file, new File(bakFile));

        } else {
            // 读取备份配置文件
            File bak = new File(bakFile);
            if (bak.exists()) {
                oldAppConfig = FileUtils.readConfigFileToMapIncludePoint(bakFile);
            }
        }

        // 删除历史解压文件
        File confFolder = file.getParentFile();
        File[] oldFileList = confFolder.listFiles();
        if (oldFileList != null) {
            for (File item : oldFileList) {
                deleteFile(item);
            }
            deleteFile(confFolder);
        }

        // 解压文件
        String folder = new File(SYMBOL_EMPTY).getAbsolutePath().replaceAll("%20", " ");
        String tempFolder = folder + SYMBOL_SLASH + "im-tool-ui.temp" + SYMBOL_SLASH;
        // 生成临时解压文件夹
        File temp = new File(tempFolder);
        deleteFile(temp);
        temp.mkdirs();

        UnZipRarUtils.unZip(new File(getJarName()), tempFolder);

        // 复制文件
        copyFolder(tempFolder + KEY_CONF, folder);
        File[] fileList = new File(tempFolder).listFiles();
        for (File item : fileList) {
            deleteFile(item);
        }
        // 删除解压零时文件夹
        deleteFile(new File(tempFolder));

        // 更新配置文件
        List<String> updateContent = new ArrayList<>(16);
        List<String> content = FileUtils.readNormalFile(url, false);
        for (int i = 0; i < content.size(); i++) {
            String item = content.get(i);
            // 获取历史svn代码更新配置
            if (item.startsWith(KEY_SVN_UPDATE_LOCATION)) {
                List<String> updateConfig = getUpdateConfig(oldAppConfig, KEY_SVN_UPDATE_LOCATION);
                updateContent.addAll(updateConfig);
                if (CollectionUtils.isNotEmpty(updateConfig)) {
                    continue;
                }
            }
            // 更新历史升级脚本配置
            if (item.startsWith(KEY_SCRIPT_UPDATE_TABLE)) {
                List<String> updateConfig = getUpdateConfig(oldAppConfig, KEY_SCRIPT_UPDATE_TABLE);
                updateContent.addAll(updateConfig);
                if (CollectionUtils.isNotEmpty(updateConfig)) {
                    continue;
                }
            }
            // 更新历史配置项
            Iterator<String> iterator = oldAppConfig.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = oldAppConfig.get(key);
                if (item.startsWith(key + SYMBOL_EQUALS)) {
                    int index = item.indexOf(SYMBOL_EQUALS) + 1;
                    item = item.substring(0, index) + value;
                }
            }
            updateContent.add(item);
        }
        // 读取svn统计配置信息
        if (StringUtils.isNotBlank(oldAppConfig.get(KEY_SVN_STAT))) {
            // 读取模板配置项信息
            String svnStatPath = FileUtils.getFilePath(PATH_SVN_STAT);
            List<String> svnStatContent = FileUtils.readNormalFile(svnStatPath, false);
            StringBuilder svnStat = new StringBuilder();
            List<String> updateSvnStatContent = new ArrayList<>(16);
            for (String item : svnStatContent) {
                svnStat.append(item);
            }
            svnStatContent = Arrays.asList(SecurityUtils.getDecryptString(svnStat.toString()).split(SYMBOL_NEXT_LINE));
            for (String item : svnStatContent) {
                // 更新svn统计用户信息
                if (item.startsWith(KEY_SVN_STAT_USER)) {
                    List<String> updateConfig = getUpdateConfig(oldAppConfig, KEY_SVN_STAT_USER);
                    updateSvnStatContent.addAll(updateConfig);
                    if (CollectionUtils.isNotEmpty(updateConfig)) {
                        continue;
                    }
                }
                // 更新历史配置项
                Iterator<String> iterator = oldAppConfig.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = oldAppConfig.get(key);
                    if (item.startsWith(key + SYMBOL_EQUALS)) {
                        int index = item.indexOf(SYMBOL_EQUALS) + 1;
                        item = item.substring(0, index) + value;
                    }
                }
                updateSvnStatContent.add(item);
            }
            updateContent.addAll(updateSvnStatContent);
        }
        FileUtils.writeFile(url, updateContent, false);

        // 删除 备份历史配置文件
        deleteFile(new File(bakFile));
    }

    public static List<String> getUpdateConfig(Map<String, String> content, String keyWord) {
        List<String> update = new ArrayList<>(16);
        Iterator<String> iterator = content.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = content.get(key);
            if (key.startsWith(keyWord)) {
                String item = key + SYMBOL_EQUALS + value;
                update.add(item);
            }
        }
        return update;
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

    /**
     * 是否指定后缀工作目录
     *
     * @param file
     * @param suffix
     * @author: hoomoomoo
     * @date: 2020/09/12
     * @return:
     */
    public static boolean isSuffixDirectory(File file, String suffix) {
        boolean exist = false;
        if (file != null && suffix != null) {
            if (!file.isDirectory()) {
                exist = false;
            }
            // 判断当前文件下是否存在文件
            File[] fileList = file.listFiles();
            if (fileList == null) {
                return false;
            }
            for (File item : fileList) {
                if (item.getName().equals(suffix)) {
                    exist = true;
                    break;
                }
            }
            // 判断父文件夹下是否存在文件
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                if (isSuffixDirectory(parentFile, suffix)) {
                    exist = true;
                }
            }
        }
        return exist;
    }

}
