package com.hoomoomoo.im.main;

import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;
import static com.hoomoomoo.im.main.CheckConfigConst.CHECK_FRONT_PATH;
import static com.hoomoomoo.im.main.CheckConfigConst.CHECK_RESULT_PATH;

/**
 * 方法导入未使用 配置检查
 */
public class CheckImportFunctionNotUse {

    private static int index;
    private static Map<String, Set<String>> res = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        executeCheck();
    }

    public static void executeCheck() throws IOException {
        String checkPath = CHECK_FRONT_PATH + "console-fund-ta-vue";
        String resPath = CHECK_RESULT_PATH + "checkImportFunctionNotUse.sql";
        System.out.println();
        System.out.println("检查路径 ... " + checkPath);
        doCheck(checkPath, resPath);
        System.out.println();
        System.out.println("结果路径 ... " + resPath);
        System.out.println();
    }

    private static void doCheck(String checkPath, String resPath) throws IOException {
        check(new File(checkPath));
        List<String> content = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : res.entrySet()) {
            String fileName = entry.getKey();
            Set<String> method = entry.getValue();
            content.add(fileName);
            for (String ele : method) {
                content.add("     -- " + ele);
            }
        }
        content.add(0, "-- 文件总数:" + res.size() + STR_NEXT_LINE);
        content.add(0, "-- 方法导入未使用");
        FileUtils.writeFile(resPath, content);

    }

    private static void check(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                check(item);
            }
        } else {
            String fileName = file.getName();
            String fileFullName = file.getAbsolutePath();
            if (fileName.endsWith(".vue")) {
                index++;
                if (index % 100 == 0) {
                    System.out.print(".");
                }
                String content = FileUtils.readNormalFileToString(file.getPath());
                content = CommonUtils.formatStrToSingleSpace(content);
                if (StringUtils.isNotBlank(content)) {
                    if (!fileFullName.contains("asyncPub") && content.indexOf("import {") != -1) {
                        checkElement(fileFullName, content);
                    }
                }
            }
        }
    }

    private static void checkElement(String fileName, String content) {
        String[] checkContent = content.split("export default");
        if (checkContent.length <= 1) {
            return;
        }
        String[] importList = content.split("import \\{");
        for (String item : importList) {
            if (item.contains("}") && item.contains("from") && item.contains("@") && !item.contains("<")) {
                String[] methods = item.split("}")[0].split(",");
                for (String ele : methods) {
                    ele = ele.trim();
                    if (!checkContent[1].contains(ele)) {
                        if (res.containsKey(fileName)) {
                            res.get(fileName).add(ele);
                        } else {
                            Set<String> method = new LinkedHashSet<>();
                            method.add(ele);
                            res.put(fileName, method);
                        }
                    }
                }
            }
        }

    }
}
