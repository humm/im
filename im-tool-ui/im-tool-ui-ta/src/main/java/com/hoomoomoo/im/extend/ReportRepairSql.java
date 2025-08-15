package com.hoomoomoo.im.extend;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

public class ReportRepairSql {

    private static final String ADD_REPORT_FIELD = "add_report_field";
    private static final String ADD_REPORT = "add_report";

    public void repair(AppConfigDto appConfigDto) throws Exception {
        readFile(new File(appConfigDto.getSystemToolCheckMenuFundExtPath() + ScriptSqlUtils.extReport));
    }

    private static void readFile(File file) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for(int i = 0; i < files.length; ++i) {
                readFile(files[i]);
            }
        } else {
            repairReportSql(file);
            repairReportFieldType(file);
        }

    }

    private static void repairReportFieldType(File file) throws Exception {
        String filePath = file.getAbsolutePath();
        List<String> contents = FileUtils.readNormalFile(filePath);
        boolean flag = false;
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < contents.size(); i++) {
            res.setLength(0);
            String item = contents.get(i);
            String itemLower = item.toLowerCase().trim();
            if (itemLower.contains(KEY_VALUES)) {
                String[] values = getValues(item, 7, false);
                if (values != null) {
                    if (values[3].contains(STR_QUOTES_SINGLE)) {
                        continue;
                    }
                    String reportCode = values[0];
                    String fieldCode = values[1];
                    String fieldName = values[2];
                    String visable = changeValue(values[3]);
                    String dataWidth = values[4];
                    String allowEdit = changeValue(values[5]);
                    String fieldNo = values[6];
                    String valuesCode = item.split("\\(")[0].trim();
                    String insertValue = valuesCode + " (%s, %s, %s, %s, %s, %s, %s);";
                    String annotation = annotationInfo(item);
                    res.append(annotation + String.format(insertValue, reportCode, fieldCode, fieldName, visable, dataWidth, allowEdit, fieldNo));
                    contents.set(i, res.toString());
                    flag = true;
                }
            }
        }

        if (flag) {
            FileUtils.writeFile(filePath, contents);
        }

    }

    private static void repairReportSql(File file) throws Exception {
        String filePath = file.getAbsolutePath();
        List<String> contents = FileUtils.readNormalFile(filePath);
        boolean flag = false;
        boolean commit = false;
        boolean needDelete = true;
        String reportCode = getReportCode(contents);

        for(int i = 0; i < contents.size(); ++i) {
            String item = contents.get(i);
            String itemLower = item.toLowerCase().trim();
            if (StringUtils.equals(itemLower, ANNOTATION_NORMAL)) {
                contents.set(i, STR_BLANK);
            }

            if (itemLower.contains(KEY_DELETE) && itemLower.contains("tbfundreport")) {
                String deleteReportCode = getReportCodeByDelete(item);
                if (StringUtils.isNotBlank(reportCode) && StringUtils.isNotBlank(deleteReportCode) && StringUtils.equals(reportCode, deleteReportCode)) {
                    contents.set(i, STR_BLANK);
                }
            }

            if (itemLower.startsWith(KEY_COMMIT)) {
                commit = true;
            }

            if (itemLower.contains(KEY_CALL) && (itemLower.contains(ADD_REPORT_FIELD) || itemLower.contains(ADD_REPORT))) {
                if (itemLower.contains(ADD_REPORT_FIELD)) {
                    flag = true;
                    item = changeToReportField(item);
                    if (needDelete) {
                        needDelete = false;
                        item = String.format("delete from tbfundreportfield where report_code = %s;\n\n", reportCode) + item;
                    }
                } else if (itemLower.contains(ADD_REPORT)) {
                    flag = true;
                    item = changeToReport(item);
                }

                contents.set(i, item);
            }
        }

        if (flag && !commit) {
            contents.add(KEY_COMMIT + STR_SEMICOLON);
        }

        if (flag) {
            FileUtils.writeFile(filePath, contents);
        }

    }

    private static String changeValue(String val) {
        return val.contains(STR_QUOTES_SINGLE) ? val : STR_QUOTES_SINGLE + val + STR_QUOTES_SINGLE;
    }

    private static String changeToReport(String item) throws Exception {
        String[] values = getValuesByFunction(item);
        StringBuilder res = new StringBuilder();
        String reportCode = values[1];
        String reportName = values[2];
        String orderNo = values[3];
        String controlFlag = values[4];
        String templateName = values[5];
        String projectDesc = values[6];
        String annotation = annotationInfo(item);
        res.append(annotation + String.format("delete from tbfundreport where report_code = %s;\n", reportCode));
        res.append(annotation + "insert into tbfundreport (report_code, report_name, order_no, control_flag, template_name, project_desc, service_name, group_name, summary)\n");
        String insertValue;
        insertValue = "values (%s, %s, %s, %s, %s, %s, ' ', ' ', ' ');\n";
        res.append(annotation + String.format(insertValue, reportCode, reportName, orderNo, controlFlag, templateName, projectDesc));
        return res.toString();
    }

    private static String changeToReportField(String item) throws Exception {
        String[] values = getValuesByFunction(item);
        StringBuilder res = new StringBuilder();
        String reportCode = values[1];
        String fieldCode = values[2];
        String fieldName = values[3];
        String visable = changeValue(values[4]);
        String dataWidth = values[5];
        String allowEdit = changeValue(values[6]);
        String fieldNo = values[7];
        String annotation = annotationInfo(item);
        res.append(annotation + "insert into tbfundreportfield (report_code, field_code, field_name, visable, data_width, allow_edit, field_no)\n");
        String insertValue = "values (%s, %s, %s, %s, %s, %s, %s);\n";
        res.append(annotation + String.format(insertValue, reportCode, fieldCode, fieldName, visable, dataWidth, allowEdit, fieldNo));
        return res.toString();
    }

    private static String[] getValuesByFunction(String item) throws Exception {
        return getValues(item, 8, true);
    }

    private static String[] getValues(String item, int length, boolean error) throws Exception {
        int start = item.indexOf(STR_BRACKETS_LEFT);
        int end = item.lastIndexOf(STR_BRACKETS_RIGHT);
        if (start >= end) {
            return null;
        } else {
            String[] ele = item.substring(start + 1, end).split(STR_COMMA);

            for(int i = 0; i < ele.length; ++i) {
                ele[i] = ele[i].trim();
            }

            if (ele.length != length) {
                if (error) {
                    throw new Exception("数据格式错误");
                } else {
                    return null;
                }
            } else {
                return ele;
            }
        }
    }

    private static String getReportCode(List<String> contents) throws Exception {
        String reportCode = STR_BLANK;
        for(int i = 0; i < contents.size(); ++i) {
            String item = contents.get(i);
            String itemLower = item.toLowerCase().trim();
            if ((itemLower.contains(ADD_REPORT_FIELD) || itemLower.contains(ADD_REPORT)) && StringUtils.isBlank(reportCode)) {
                reportCode = getValuesByFunction(item)[1];
            }
        }

        return reportCode;
    }

    private static String getReportCodeByDelete(String item) throws Exception {
        String[] delete = item.split(STR_EQUAL);
        if (delete.length != 2) {
            throw new Exception("数据格式错误");
        } else {
            return delete[1].trim().split(STR_SEMICOLON)[0];
        }
    }

    private static String annotationInfo(String item) {
        return item.trim().startsWith(ANNOTATION_NORMAL) ? ANNOTATION_NORMAL + STR_SPACE : STR_BLANK;
    }

}
