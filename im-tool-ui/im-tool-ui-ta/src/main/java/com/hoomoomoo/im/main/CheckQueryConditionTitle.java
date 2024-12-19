package com.hoomoomoo.im.main;

import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;
import static com.hoomoomoo.im.main.CheckConfigConsts.CHECK_FRONT_PATH;
import static com.hoomoomoo.im.main.CheckConfigConsts.CHECK_RESULT_PATH;

/**
 * 查询条件 配置检查
 */
public class CheckQueryConditionTitle {

    private static int index;
    private static List<String> res = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        executeCheck();
    }

    public static void executeCheck() throws IOException {
        String checkPath = CHECK_FRONT_PATH + "front\\HUI1.0\\console-fund-ta-vue";
        String resPath = CHECK_RESULT_PATH + "checkQueryConditionTitle.sql";
        System.out.println();
        System.out.println("检查路径 ... " + checkPath);
        doCheck(checkPath, resPath);
        System.out.println();
        System.out.println("结果路径 ... " + resPath);
        System.out.println();
    }

    private static void doCheck(String checkPath, String resPath) throws IOException {
        check(new File(checkPath));
        res.add(0, "-- 文件总数:" + res.size() + STR_NEXT_LINE);
        res.add(0, "-- 同时调用方法setColsDiffResolution和addTitleSearchCondition");
        FileUtils.writeFile(resPath, res, false);

    }

    private static void check(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                check(item);
            }
        } else {
            String fileName = file.getName();
            if (fileName.endsWith(".vue")) {
                index++;
                if (index % 100 == 0) {
                    System.out.print(".");
                }
                String content = FileUtils.readNormalFileToString(file.getPath(), false);
                content = CommonUtils.formatStrToSingleSpace(content);
                if (StringUtils.isNotBlank(content)) {
                    if (content.indexOf("mounted()") != -1) {
                        checkElement(fileName, content);
                    }
                }
            }
        }
    }

    private static void checkElement(String fileName, String content) {
        int mark1 = content.indexOf("setColsDiffResolution");
        int mark2 = content.indexOf("addTitleSearchCondition");
        if (mark1 != -1 && mark2 != -1) {
            res.add(fileName);
        }
    }

}
