package com.hoomoomoo.im;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class Ui {

    private static Set<String> exist = new HashSet<>();
    private static Set<File> repeat = new HashSet<>();
    private static Set<File> blankFileType = new HashSet<>();
    private static int num = 0;
    private static String[] skipConfig = new String[]{"婚纱"};

    public static void main(String[] args) {
        String location = "F:\\";
        check(new File(location));

        Iterator<File> blankIterator = blankFileType.iterator();
        int blankFileNum = 0;
        while (blankIterator.hasNext()) {
            File file = blankIterator.next();
            String absolutePath = file.getAbsolutePath();
            if (absolutePath.contains("$RECYCLE.BIN")) {
                continue;
            }
            blankFileNum++;
            System.out.println("空白文件类型: " + absolutePath);
        }
        if (blankFileNum > 0) {
            System.out.println("空白文件类型总数: " + blankFileNum);
        }

        int repeatNum = repeat.size();
        Iterator<File> iterator = repeat.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            //FileUtils.deleteFile(file);
            System.out.println(file.getAbsolutePath());
        }
        if (repeatNum > 0) {
            System.out.println("照片重复: " + repeatNum);
        }
        System.out.println("照片总数: " + num);
    }

    public static void check(File file) {
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
                        check(item);
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
                    if (exist.contains(mark)) {
                        repeat.add(item);
                        continue;
                    }
                    exist.add(mark);
                    num++;
                }
            }
        }
    }
}
