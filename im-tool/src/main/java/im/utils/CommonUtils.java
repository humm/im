package im.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.Ansi;
import org.hswebframework.utils.file.EncodingDetect;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static im.consts.BaseConst.*;

/**
 * @author hoomoomoo
 * @description 通用工具类
 * @package com.hoomoomoo.im.utils
 * @date 2020/09/12
 */
public class CommonUtils {

    /**
     * unicode字符串正则
     */
    public static final Pattern PATTERN = Pattern.compile("(\\\\u(\\w{4}))");


    /**
     * 删除文件
     *
     * @param file
     * @author: hoomoomoo
     * @date: 2020/08/23
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
     * 文件排序
     *
     * @param fileList
     * @author: hoomoomoo
     * @date: 2020/08/23
     * @return:
     */
    public static void sortFile(File[] fileList) {
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
     * unicode转中文字符串
     *
     * @param str
     * @author: hoomoomoo
     * @date: 2020/09/03
     * @return:
     */
    public static String convertUnicodeToCh(String str) {
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
     * 深沉睡眠
     *
     * @param sleepTime
     * @author: hoomoomoo
     * @date: 2020/09/08
     * @return:
     */
    public static void sleep(Integer sleepTime) {
        try {
            Thread.sleep(sleepTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制台输出
     *
     * @param content
     * @param color
     * @author: hoomoomoo
     * @date: 2020/09/09
     * @return:
     */
    public static void print(boolean singleColor, String content, String color, boolean lineHead, boolean nextLine) {
        if (lineHead && StringUtils.isNotBlank(content) && StringUtils.startsWith(content, "[ ")) {
            if (singleColor) {
                System.out.print(SYMBOL_STAR_3_MORE);
            } else {
                System.out.print(Ansi.ansi().fg(getColor(SYMBOL_EMPTY)).a(SYMBOL_STAR_3_MORE).reset());
            }
        } else if (lineHead) {
            if (singleColor) {
                System.out.print(SYMBOL_STAR_3);
            } else {
                System.out.print(Ansi.ansi().fg(getColor(SYMBOL_EMPTY)).a(SYMBOL_STAR_3).reset());
            }
        }
        if (StringUtils.isBlank(color)) {
            color = SYMBOL_EMPTY;
        }
        if (nextLine) {
            if (singleColor) {
                System.out.println(content);
            } else {
                System.out.println(Ansi.ansi().fg(getColor(color)).a(content).reset());
            }
        } else {
            if (singleColor) {
                System.out.print(content);
            } else {
                System.out.print(Ansi.ansi().fg(getColor(color)).a(content).reset());
            }
        }
    }

    /**
     * 控制台输出
     *
     * @param content
     * @param color
     * @param singleColor
     * @author: hoomoomoo
     * @date: 2020/09/09
     * @return:
     */
    public static void println(boolean singleColor, String content, String color, boolean lineHead) {
        print(singleColor, content, color, lineHead, true);
    }

    /**
     * 控制台输出
     *
     * @param content
     * @param color
     * @param singleColor
     * @author: hoomoomoo
     * @date: 2020/09/09
     * @return:
     */
    public static void println(boolean singleColor, String content, String color) {
        print(singleColor, content, color, true, true);
    }

    /**
     * 获取颜色
     *
     * @param color
     * @author: hoomoomoo
     * @date: 2020/09/09
     * @return:
     */
    private static Ansi.Color getColor(String color) {
        if (StringUtils.isBlank(color)) {
            return Ansi.Color.WHITE;
        }
        Ansi.Color colorType = Ansi.Color.WHITE;
        switch (color.toUpperCase()) {
            case "BLACK":
                colorType = Ansi.Color.BLACK;
                break;
            case "RED":
                colorType = Ansi.Color.RED;
                break;
            case "GREEN":
                colorType = Ansi.Color.GREEN;
                break;
            case "YELLOW":
                colorType = Ansi.Color.YELLOW;
                break;
            case "BLUE":
                colorType = Ansi.Color.BLUE;
                break;
            case "MAGENTA":
                colorType = Ansi.Color.MAGENTA;
                break;
            case "CYAN":
                colorType = Ansi.Color.CYAN;
                break;
            case "WHITE":
                colorType = Ansi.Color.WHITE;
                break;
            default:
                break;
        }
        return colorType;
    }

    /**
     * 反斜线转换
     *
     * @param value
     * @author: hoomoomoo
     * @date: 2020/09/06
     * @return:
     */
    public static String convertBackslash(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value.replace(SYMBOL_BACKSLASH_2, SYMBOL_SLASH).replace(SYMBOL_BACKSLASH_1, SYMBOL_SLASH);
        }
        return value;
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

    /**
     * 获取文件编码格式
     *
     * @param filePath
     * @author: hoomoomoo
     * @date: 2020/09/19
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
     * map 转 javaBean
     *
     * @param map
     * @param clazz
     * @author: hoomoomoo
     * @date: 2020/12/02
     * @return:
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> clazz) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.newInstance();
            BeanUtils.populate(obj, map);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
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
    public static Map<String, String> convertMap(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        Map<String, String> afterMap = new LinkedHashMap<>(map.size());
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> item = iterator.next();
            String key = item.getKey();
            String value = item.getValue();
            StringBuffer convertKey = new StringBuffer();
            boolean isPoint = false;
            for (int i = 0; i < key.length(); i++) {
                char single = key.charAt(i);
                if (String.valueOf(single).equals(SYMBOL_POINT_1)) {
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
}
