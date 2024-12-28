package com.hoomoomoo.im.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;
import static com.hoomoomoo.im.main.CheckConfigConst.*;

/**
 * 复核字段 配置检查
 */
public class CheckConfirmColumn {

    private static int index;
    private static Map<String, Set<String>> existConfirmColumn = new LinkedHashMap<>();
    private static Map<String, Set<String>> needConfirmColumn = new LinkedHashMap<>();
    private static Set<String> needConfirmUrl = new LinkedHashSet<>();
    private static String WORK_FLOW_SUB_TRANS = CHECK_MAIN_PATH + "sql\\pub\\001initdata\\basedata\\workflow\\tbworkflowsubtrans-fund.sql";

    public static void main(String[] args) throws IOException {
        executeCheck();
    }

    public static void executeCheck() throws IOException {
        String checkPath = CHECK_FRONT_PATH + "console-fund-ta-vue";
        String resPath = CHECK_RESULT_PATH + "checkConfirmColumn.sql";
        System.out.println();
        System.out.println("检查路径 ... " + checkPath);
        doCheck(checkPath, resPath);
        System.out.println();
        System.out.println("结果路径 ... " + resPath);
        System.out.println();
    }

    private static void doCheck(String checkPath, String resPath) throws IOException {
        check(new File(checkPath));
        Map<String, Set<String>> lackField = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entry : needConfirmColumn.entrySet()) {
            String url = entry.getKey();
            Set<String> value = entry.getValue();
            if (existConfirmColumn.containsKey(url)) {
                Set<String> existColumn = existConfirmColumn.get(url);
                for (String column : value) {
                    String columnTranslate = column + "Name";
                    if (!existColumn.contains(column) && !existColumn.contains(columnTranslate)) {
                        if (lackField.containsKey(url)) {
                            lackField.get(url).add(column);
                        } else {
                            Set<String> field = new LinkedHashSet<>();
                            field.add(column);
                            lackField.put(url, field);
                        }
                    }
                }
            } else {
                lackField.put(url, value);
            }
        }
        Map<String, Set<String>> urlGroup = new LinkedHashMap<>();
        Iterator<Map.Entry<String, Set<String>>> iterator = lackField.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Set<String>> entry = iterator.next();
            String url = entry.getKey();
            Set<String> columns = entry.getValue();
            if (deleteUrl(urlGroup, url, columns.size())) {
                iterator.remove();
            }
        }
        initNeedConfirmUrl();
        int num = 0;
        List<String> content = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : lackField.entrySet()) {
            String url = entry.getKey();
            if (!needConfirmUrl.contains(url) || skipUrl(url)) {
                continue;
            }
            Set<String> columns = entry.getValue();
            num++;
            url = getUrlGroup(urlGroup, url, columns.size());
            content.add(url);
            for (String ele : columns) {
                content.add("     -- " + ele);
            }
            content.add(STR_BLANK);
        }
        content.add(0, "-- url组总数:" + num + STR_NEXT_LINE);
        content.add(0, "-- 特别注意: 无列表页面git未检查 比如单笔TA发起业务");
        content.add(0, "-- 复核页面缺少字段");
        FileUtils.writeFile(resPath, content, false);
    }

    private static void initNeedConfirmUrl() throws IOException {
        String content = FileUtils.readNormalFileToString(WORK_FLOW_SUB_TRANS, false);
        String[] urlList = content.split(";");
        for (String url : urlList) {
            String transCode = ScriptSqlUtils.getTransCodeByWorkFlow(url);
            String subTransCode = ScriptSqlUtils.getSubTransCodeByWorkFlow(url);
            if (transCode == null || subTransCode == null) {
                continue;
            }
            needConfirmUrl.add(transCode + "$" + subTransCode);
        }
    }

    private static boolean skipUrl(String url) {
        Set<String> skipUrl = new HashSet<String>(){{
            add("Query");
            add("Qry");
            add("Export");
        }};
        for (String ele : skipUrl) {
            if (url.endsWith(ele)) {
                return true;
            }
        }
        return false;
    }

    private static String getUrlGroup(Map<String, Set<String>> urlGroup, String url, int fieldSize) {
        String groupCode = getGroupCode(url, fieldSize);
        if (urlGroup.containsKey(groupCode)) {
            return String.join(STR_NEXT_LINE, urlGroup.get(groupCode));
        }
        return url;
    }

    private static boolean deleteUrl(Map<String, Set<String>> urlGroup, String url, int fieldSize) {
        boolean flag;
        String groupCode = getGroupCode(url, fieldSize);
        if (urlGroup.containsKey(groupCode)) {
            urlGroup.get(groupCode).add(url);
            flag = true;
        } else {
            Set<String> urlList = new LinkedHashSet<>();
            urlList.add(url);
            urlGroup.put(groupCode, urlList);
            flag = false;
        }
        return flag;
    }

    private static String getGroupCode(String url, int fieldSize) {
        String transCode = url.split("\\$")[0];
        return transCode + "$" + transCode + "$" + fieldSize;
    }

    private static void check(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                check(item);
            }
        } else {
            String fileName = file.getName();
            String filePath = file.getPath();
            if (fileName.endsWith(".js") && filePath.contains("api\\reConfirm\\fund")) {
                index++;
                if (index % 100 == 0) {
                    System.out.print(".");
                }
                List<String> contentList = FileUtils.readNormalFile(file.getPath(), true);
                StringBuilder contentTemp = new StringBuilder();
                for (String item : contentList) {
                    item = item.trim();
                    if (item.startsWith(ANNOTATION_NORMAL_2)) {
                        continue;
                    }
                    int index = item.indexOf(ANNOTATION_NORMAL_2);
                    if (index != -1) {
                        item = item.substring(0, index);
                    }
                    contentTemp.append(item);
                }
                String content = CommonUtils.formatStrToSingleSpace(contentTemp.toString());
                if (StringUtils.isNotBlank(content)) {
                    int start = content.indexOf("[");
                    int end = content.lastIndexOf("]");
                    if (start != -1 && end != -1) {
                        getExistConfirmColumn(fileName, content.substring(start, end + 1));
                    }
                }
            } else if (fileName.endsWith(".vue")) {
                index++;
                if (index % 100 == 0) {
                    System.out.print(".");
                }
                List<String> contentList = FileUtils.readNormalFile(file.getPath(), true);
                StringBuilder contentTemp = new StringBuilder();
                Map<String, Set<String>> configColumns = new LinkedHashMap<>();
                Map<String, Set<String>> configButtons = new LinkedHashMap<>();
                for (String item : contentList) {
                    item = item.trim();
                    if (item.startsWith(ANNOTATION_NORMAL_2)) {
                        continue;
                    }
                    int index = item.indexOf(ANNOTATION_NORMAL_2);
                    if (index != -1) {
                        item = item.substring(0, index);
                    }
                    contentTemp.append(item);
                }
                String content = CommonUtils.formatStrToSingleSpace(contentTemp.toString());
                if (StringUtils.isNotBlank(content)) {
                    getConfigColumns(configColumns, fileName, content);
                    getConfigButtons(configButtons, fileName, content);
                    if (MapUtils.isNotEmpty(configColumns) && MapUtils.isNotEmpty(configButtons)) {
                        for (Map.Entry<String, Set<String>> button : configButtons.entrySet()) {
                            String fileNameButton = button.getKey();
                            Set<String> urlButton = button.getValue();
                            for (String url : urlButton) {
                                needConfirmColumn.put(url, configColumns.get(fileNameButton));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void getConfigButtons(Map<String, Set<String>> configButtons, String fileName, String content) {
        String[] buttons = content.split("hasBtnAuth");
        for (String ele : buttons) {
            ele = ele.trim();
            if (ele.contains("$") && ele.startsWith("(")) {
                int index = ele.indexOf(")");
                if (index != -1) {
                    ele = formatFieldCode(ele.substring(1, index));
                    if (ele.contains("$") && !ele.startsWith("$")) {
                        if (configButtons.containsKey(fileName)) {
                            configButtons.get(fileName).add(ele);
                        } else {
                            Set<String> config = new LinkedHashSet<>();
                            config.add(ele);
                            configButtons.put(fileName, config);
                        }
                    }
                }
            }
        }
    }

    private static void getConfigColumns(Map<String, Set<String>> configColumns, String fileName, String content) {
        int start = content.indexOf("columns: [");
        if (start == -1) {
            start = content.indexOf("columns:[");
        }
        if (start == -1) {
            return;
        }
        int end = content.indexOf("created");
        int mounted = content.indexOf("mounted");
        if (end > mounted && start < mounted) {
            end = mounted;
        }
        int methods = content.indexOf("methods");
        if (end > methods && start < methods) {
            end = methods;
        }
        if (end == -1) {
            return;
        }
        if (start > end) {
            end = content.length();
        }
        String columns = content.substring(start, end);
        String[] columnsList = columns.split("key");
        for (int i=1; i<columnsList.length; i++) {
            String fieldCode = columnsList[i];
            int begIndex = fieldCode.indexOf(":");
            int endIndex = fieldCode.indexOf(",");
            if (begIndex != -1 && endIndex != -1 && begIndex < endIndex) {
                fieldCode = formatFieldCode(fieldCode.substring(begIndex + 1, endIndex));
                if (!skipFieldCode(fieldCode)) {
                    if (configColumns.containsKey(fileName)) {
                        configColumns.get(fileName).add(fieldCode);
                    } else {
                        Set<String> config = new LinkedHashSet<>();
                        config.add(fieldCode);
                        configColumns.put(fileName, config);
                    }
                }
            }
        }
    }

    private static String formatFieldCode(String fieldCode) {
        Set<String> delete = new HashSet<String>(){{
            add("'");
            add("\"");
            add("}");
            add("`");
        }};
        for (String ele : delete) {
            fieldCode = fieldCode.replace(ele, STR_BLANK);
        }
        return fieldCode.trim();
    }

    private static boolean skipFieldCode(String fieldCode) {
        Set<String> skipField = new HashSet<String>(){{
            add("quote");
            add("multTaCode");
        }};
        for (String ele : skipField) {
            if (fieldCode.equals(ele)) {
                return true;
            }
        }
        Set<String> skip = new HashSet<String>(){{
            add(".");
            add(",");
            add(";");
            add("=");
            add("(");
            add(")");
            add("[");
            add("]");
        }};
        for (String ele : skip) {
            if (fieldCode.contains(ele)) {
                return true;
            }
        }
        return false;
    }

    private static void getExistConfirmColumn(String fileName, String content) {
        List<Map<String, Object>> columnList = (List<Map<String, Object>>)JSONArray.parse(content);
        Set<String> column = new LinkedHashSet<>();
        for (Map<String, Object> ele : columnList) {
            Object configColumn = ele.get("addForm");
            if (configColumn instanceof JSONArray) {
                JSONArray field = (JSONArray)configColumn;
                for (int i=0; i<field.size(); i++) {
                    column.add(((JSONObject)field.get(i)).getString("key"));
                }
            }
            for (Map.Entry<String, Object> config : ele.entrySet()) {
                String key = config.getKey();
                Object value = config.getValue();
                if ("addForm".equals(key)) {
                    continue;
                }
                if (value instanceof JSONArray) {
                    JSONArray url = (JSONArray)value;
                    for (int i=0; i<url.size(); i++) {
                        existConfirmColumn.put(url.get(i).toString(), column);
                    }
                } else {
                    existConfirmColumn.put(value.toString(), column);
                }
            }
        }
    }
}
