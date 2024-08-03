package com.hoomoomoo.im.check;


import com.hoomoomoo.im.utils.FileUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Ui {

    private static final String[] skipConfig = new String[]{"婚纱"};
    private static final String location = "F:\\归档";

    public static void main(String[] args) {
        CheckSameByNameUtils.checkSameByName(location, skipConfig, false);
        //CheckSameByMD5Utils.checkSameByMD5(location, skipConfig, false);
    }


    public static void showCheckResult(Map<String, File> exist, Map<String, File> repeat, Set<File> blankFileType, int num, boolean deleteSame) {
        System.out.println("检查完成...");
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

        System.out.println("照片总数: " + num);
        int repeatNum = repeat.size();
        if (repeatNum > 0) {
            System.out.println("照片重复: " + repeatNum);
        }
        Iterator<String> iterator = repeat.keySet().iterator();
        while (iterator.hasNext()) {
            String fileInfo = iterator.next();
            File file = repeat.get(fileInfo);
            String msg = "";
            if (deleteSame) {
                FileUtils.deleteFile(file);
                msg = "删除重复照片 ";
            }
            System.out.println(msg + file.getAbsolutePath() + " => " + exist.get(fileInfo).getAbsolutePath());
        }
    }

}
