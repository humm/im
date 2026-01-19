package com.hoomoomoo.im.main;

import com.hoomoomoo.im.utils.FileUtils;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;

public class CheckReport {
	private static Set<String> fileSet = new LinkedHashSet();
	private static Set<String> noCommitSet = new LinkedHashSet();
	private static Set<String> deleteSet = new LinkedHashSet();
	private static Set<String> userSet = new LinkedHashSet();
	private static boolean printDetail = false;
	private static final int batchNum = 10;
	private static int updateNum = 1;
	private static int totalNum = 0;

	public CheckReport() {
	}

	public static void main(String[] args) throws Exception {
		readFile(new File("D:\\workspace\\ta6-git\\fund\\db\\extradata\\pub\\001initdata\\Report"), false);
		println("补充commit开始 ============================================================");
		Iterator var1 = noCommitSet.iterator();

		String file;
		while(var1.hasNext()) {
			file = (String)var1.next();
			println(file);
		}

		println("补充commit结束 ============================================================");
		println("补充delete开始 ============================================================");
		var1 = deleteSet.iterator();

		while(var1.hasNext()) {
			file = (String)var1.next();
			println(file);
		}

		println("补充delete结束 ============================================================");
		println("删除user开始 ============================================================");
		var1 = userSet.iterator();

		while(var1.hasNext()) {
			file = (String)var1.next();
			println(file);
		}

		println("删除user结束 ============================================================");
		System.out.println("补充delete总数: " + deleteSet.size());
		System.out.println("补充commit总数: " + noCommitSet.size());
		System.out.println("删除user总数: " + userSet.size());
		System.out.println("分批修改文件总数: " + fileSet.size());
		System.out.println("待修改文件总数: " + (totalNum - fileSet.size()));
	}

	private static void readFile(File file, boolean check) throws Exception {
		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for(int i = 0; i < files.length; ++i) {
				readFile(files[i], check);
			}
		} else {
			repairReportSql(file, check);
			repairReportFieldType(file);
		}

	}

	private static void repairReportFieldType(File file) throws Exception {
		String filePath = file.getAbsolutePath();
		List<String> contents = FileUtils.readNormalFile(filePath);
		boolean flag = false;

		for(int i = 0; i < contents.size(); ++i) {
			String item = contents.get(i);
			String itemLower = item.toLowerCase().trim();
			if (itemLower.contains("values")) {
				String[] values = getValues(item, 7, false);
				if (values != null) {
					if (values[3].contains("'")) {
						break;
					}

					StringBuilder res = new StringBuilder();
					String reportCode = values[0];
					String fieldCode = values[1];
					String fieldName = values[2];
					String visable = changeValue(values[3]);
					String dataWidth = values[4];
					String allowEdit = changeValue(values[5]);
					String fieldNo = values[6];
					String insertValue = "values (%s, %s, %s, %s, %s, %s, %s);";
					String annotation = annotationInfo(item);
					res.append(annotation + String.format(insertValue, reportCode, fieldCode, fieldName, visable, dataWidth, allowEdit, fieldNo));
					contents.set(i, res.toString());
					flag = true;
				}
			}
		}

		if (flag) {
			if (batching()) {
				FileUtils.writeFile(filePath, contents);
				fileSet.add(filePath);
				++updateNum;
			}

			++totalNum;
		}

	}

	private static void repairReportSql(File file, boolean check) throws Exception {
		String filePath = file.getAbsolutePath();
		List<String> contents = FileUtils.readNormalFile(filePath);
		boolean flag = false;
		boolean commit = false;
		boolean needDelete = true;
		String reportCode = getReportCode(contents);

		for(int i = 0; i < contents.size(); ++i) {
			String item = (String)contents.get(i);
			String itemLower = item.toLowerCase().trim();
			if (check) {
				if (itemLower.contains("tbfundonekeyexpdetail")) {
					if (!fileSet.contains(filePath)) {
						fileSet.add(filePath);
						println(filePath);
					}

					println(item);
				}
			} else {
				if (StringUtils.equals(itemLower, "--")) {
					contents.set(i, "");
				}

				if (itemLower.contains("delete") && itemLower.contains("tbfundreport")) {
					String deleteReportCode = getReportCodeByDelete(item);
					if (StringUtils.isNotBlank(reportCode) && StringUtils.isNotBlank(deleteReportCode) && StringUtils.equals(reportCode, deleteReportCode)) {
						contents.set(i, "");
						if (!userSet.contains(filePath) && batching()) {
							userSet.add(filePath);
						}
					}
				}

				if (itemLower.startsWith("commit")) {
					commit = true;
				}

				if (itemLower.contains("call") && (itemLower.contains("add_report_field") || itemLower.contains("add_report"))) {
					if (!fileSet.contains(filePath) && batching()) {
						fileSet.add(filePath);
						println(filePath);
					}

					if (itemLower.contains("add_report_field")) {
						flag = true;
						item = changeToReportField(item);
						if (needDelete) {
							needDelete = false;
							item = String.format("delete from tbfundreportfield where report_code = %s;\n\n", reportCode) + item;
						}
					} else if (itemLower.contains("add_report")) {
						flag = true;
						item = changeToReport(item);
					}

					contents.set(i, item);
				}
			}
		}

		if (flag && !commit && batching()) {
			noCommitSet.add(filePath);
			contents.add("commit;");
		}

		if (flag) {
			if (batching()) {
				FileUtils.writeFile(filePath, contents);
				++updateNum;
			}

			++totalNum;
		}

	}

	private static boolean batching() {
		return updateNum <= batchNum;
	}

	private static String changeValue(String val) {
		return val.contains("'") ? val : "'" + val + "'";
	}

	private static String changeToReport(String item) throws Exception {
		boolean update = false;
		String[] values = getValuesByFunction(item);
		StringBuilder res = new StringBuilder();
		String reportCode = values[1];
		String reportName = values[2];
		String orderNo = values[3];
		String controlFlag = values[4];
		String templateName = values[5];
		String projectDesc = values[6];
		String annotation = annotationInfo(item);
		if (!update) {
			res.append(annotation + String.format("delete from tbfundreport where report_code = %s;\n", reportCode));
		}

		res.append(annotation + "insert into tbfundreport (report_code, report_name, order_no, control_flag, template_name, project_desc, service_name, group_name, summary)\n");
		String insertValue;
		if (update) {
			insertValue = "select %s, %s, %s, %s, %s, %s, ' ', ' ', ' '\n\tfrom (select count(1) param_exists from tbfundreport t where t.report_code = %s) a\nwhere a.param_exists = 0;\n";
			res.append(annotation + String.format(insertValue, reportCode, reportName, orderNo, controlFlag, templateName, projectDesc, reportCode));
			String updateValue = "update tbfundreport set report_name = %s, order_no = %s, control_flag = %s,\n\ttemplate_name = %s, project_desc= %s\nwhere report_code = %s;\n";
			res.append(annotation + String.format(updateValue, reportName, orderNo, controlFlag, templateName, projectDesc, reportCode));
		} else {
			insertValue = "values (%s, %s, %s, %s, %s, %s, ' ', ' ', ' ');\n";
			res.append(annotation + String.format(insertValue, reportCode, reportName, orderNo, controlFlag, templateName, projectDesc));
		}

		print(res.toString());
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
		print(res.toString());
		return res.toString();
	}

	private static void print(String item) {
		if (printDetail) {
			System.out.print(item);
		}

	}

	private static void println(String item) {
		if (printDetail) {
			System.out.println(item);
		}

	}

	private static String[] getValuesByFunction(String item) throws Exception {
		return getValues(item, 8, true);
	}

	private static String[] getValues(String item, int length, boolean error) throws Exception {
		int start = item.indexOf("(");
		int end = item.lastIndexOf(")");
		if (start >= end) {
			return null;
		} else {
			String[] ele = item.substring(start + 1, end).split(",");

			for(int i = 0; i < ele.length; ++i) {
				ele[i] = ele[i].trim();
			}

			if (ele.length != length) {
				if (error) {
					throw new Exception("数据格式错误" + STR_NEXT_LINE);
				} else {
					return null;
				}
			} else {
				return ele;
			}
		}
	}

	private static String getReportCode(List<String> contents) throws Exception {
		String reportCode = "";
		for(int i = 0; i < contents.size(); ++i) {
			String item = (String)contents.get(i);
			String itemLower = item.toLowerCase().trim();
			if ((itemLower.contains("add_report_field") || itemLower.contains("add_report")) && StringUtils.isBlank(reportCode)) {
				reportCode = getValuesByFunction(item)[1];
			}
		}

		return reportCode;
	}

	private static String getReportCodeByDelete(String item) throws Exception {
		String[] delete = item.split("=");
		if (delete.length != 2) {
			throw new Exception("数据格式错误" + STR_NEXT_LINE);
		} else {
			return delete[1].trim().split(";")[0];
		}
	}

	private static String annotationInfo(String item) {
		return item.trim().startsWith("--") ? "-- " : "";
	}
}
