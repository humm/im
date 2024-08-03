package com.hoomoomoo.im.check;

import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class CheckSameByNameUtils {

    private static Map<String, File> exist = new HashMap<>();
    private static Map<String, File> repeat = new LinkedHashMap();
    private static Set<File> blankFileType = new HashSet<>();
    private static int num = 0;


    public static void checkSameByName(String location, String[] skipConfig, boolean deleteSame) {
        checkSameName(new File(location), skipConfig);
        Ui.showCheckResult(exist, repeat, blankFileType, num, deleteSame);
    }

    private static void checkSameName(File file, String[] skipConfig) {
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
                        checkSameName(item, skipConfig);
                    }
                } else {
                    String fileName = item.getName();
                    String fileType = "";
                    if (fileName.contains(".")) {
                        String[] fileInfo = fileName.split("\\.");
                        fileName = fileInfo[0];
                        fileType = fileInfo[1];
                    }
                    if (StringUtils.isBlank(fileType)) {
                        blankFileType.add(item);
                        continue;
                    }
                    String mark = fileName.split("\\(")[0] + fileType;
                    if (exist.containsKey(mark)) {
                        repeat.put(mark, item);
                        continue;
                    }
                    exist.put(mark, item);
                    num++;
                }
            }
        }
    }

}
