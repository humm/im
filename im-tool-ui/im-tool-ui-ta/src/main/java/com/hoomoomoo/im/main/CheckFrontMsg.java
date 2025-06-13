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
 * h-msg-box 配置检查
 */
public class CheckFrontMsg {

    private static int index;
    private static Map<String, Set<String>> res = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        executeCheck();
    }

    public static void executeCheck() throws IOException {
        String checkPath = CHECK_FRONT_PATH + "console-fund-ta-vue";
        String resPath = CHECK_RESULT_PATH + "checkFrontMsg.sql";
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
        int boxSize = 0;
        for (Map.Entry<String, Set<String>> entry : res.entrySet()) {
            String fileName = entry.getKey();
            Set<String> box = entry.getValue();
            content.add(fileName);
            for (String ele : box) {
                content.add("     -- " + ele);
                boxSize++;
            }
        }
        content.add(0, "-- 文件总数:" + res.size() + "  弹窗总数:" + boxSize + STR_NEXT_LINE);
        content.add(0, "-- <h-msg-box> 使用v-if且transfer未配置为false");
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
            if (fileName.endsWith(".vue") || fileName.endsWith(".js")) {
                index++;
                if (index % 100 == 0) {
                    System.out.print(".");
                }
                String content = FileUtils.readNormalFileToString(file.getPath());
                content = CommonUtils.formatStrToSingleSpace(content);
                if (StringUtils.isNotBlank(content)) {
                    if (content.indexOf("h-msg-box") != -1) {
                        checkElement(file.getAbsolutePath(), content);
                    }
                }
            }
        }
    }

    private static void checkElement(String fileName, String content) {
        int start = content.indexOf("<h-msg-box");
        if (start == -1) {
            return;
        }
        int end = start + content.substring(start).indexOf(">");
        if (end == -1) {
            return;
        }
        String ele = content.substring(start, end + 1);
        if (ele.contains("v-if") && !ele.contains("transfer=\"false\"")) {
            if (res.containsKey(fileName)) {
                res.get(fileName).add(ele);
            } else {
                Set<String> box = new LinkedHashSet<>();
                box.add(ele);
                res.put(fileName, box);
            }
        }
        content = content.substring(end + 10);
        checkElement(fileName, content);
    }
}
