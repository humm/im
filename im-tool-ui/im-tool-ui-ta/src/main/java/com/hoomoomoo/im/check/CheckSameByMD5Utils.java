package com.hoomoomoo.im.check;

import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CheckSameByMD5Utils {

    private static Map<String, File> exist = new HashMap<>();
    private static Map<String, File> repeat = new LinkedHashMap();
    private static Set<File> blankFileType = new HashSet<>();
    private static int num = 0;

    public static void checkSameByMD5(String location, String[] skipConfig, boolean deleteSame) {
        checkSameMD5(new File(location), skipConfig);
        Ui.showCheckResult(exist, repeat, blankFileType, num, deleteSame);
    }

    private static void checkSameMD5(File file, String[] skipConfig) {
        System.out.println(file.getAbsolutePath() + " 检查中...");
        File[] list = file.listFiles();
        if (list != null) {
            List<File> fileList = Arrays.asList(list);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o2.getName().compareTo(o1.getName());
                }
            });
            for (File item : fileList) {
                if (item.isDirectory()) {
                    boolean skip = false;
                    String fileName = item.getName();
                    inner: for (String ele : skipConfig) {
                        if (fileName.contains(ele)) {
                            skip = true;
                            break inner;
                        }
                    }
                    if (!skip) {
                        checkSameMD5(item, skipConfig);
                    }
                } else {
                    String fileName = item.getName();
                    String fileType = "";
                    if (fileName.contains(".")) {
                        String[] fileInfo = fileName.split("\\.");
                        fileType = fileInfo[1];
                    }
                    if (StringUtils.isBlank(fileType)) {
                        blankFileType.add(item);
                        continue;
                    }
                    String fileInfo = getFileInfo(item.getAbsolutePath());
                    if (exist.containsKey(fileInfo)) {
                        repeat.put(fileInfo, item);
                        continue;
                    }
                    exist.put(fileInfo, item);
                    num++;
                }
            }
        }
    }

    public static String getFileInfo(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] bytes = new byte[1024];
                int read = 0;
                while ((read = fis.read(bytes)) != -1) {
                    digest.update(bytes, 0, read);
                }
            }
            byte[] digestBytes = digest.digest();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b));
            }
        } catch (Exception e) {
            System.out.println("获取文件MD5信息异常" + e.getMessage());
            sb.append(System.currentTimeMillis());
        }
        return sb.toString();
    }
}
