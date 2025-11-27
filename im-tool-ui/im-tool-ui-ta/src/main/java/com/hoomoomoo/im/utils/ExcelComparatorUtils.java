package com.hoomoomoo.im.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelComparatorUtils {

    // 存储差异信息
    private static final List<String> DIFF_LIST = new ArrayList<>();

    /**
     * 对比两个Excel文件是否一致
     *
     * @param filePath1 第一个Excel文件路径
     * @param filePath2 第二个Excel文件路径
     * @return true=一致，false=不一致；差异信息可通过getDiffList()获取
     */
    public static boolean compareExcel(String filePath1, String filePath2) {
        // 清空历史差异
        DIFF_LIST.clear();
        if (!new File(filePath1).exists()) {
            LoggerUtils.info(filePath1 + " 文件不存在，不比较");
            return false;
        }
        if (!new File(filePath2).exists()) {
            LoggerUtils.info(filePath2 + " 文件不存在，不比较");
            return false;
        }
        try {
            FileInputStream fis1 = new FileInputStream(filePath1);
            FileInputStream fis2 = new FileInputStream(filePath2);
            Workbook workbook1 = getWorkbook(fis1, filePath1);
            Workbook workbook2 = getWorkbook(fis2, filePath2);
            // 1. 校验工作表数量
            if (workbook1.getNumberOfSheets() != workbook2.getNumberOfSheets()) {
                DIFF_LIST.add("工作表数量不一致：文件1=" + workbook1.getNumberOfSheets() + "，文件2=" + workbook2.getNumberOfSheets());
                return false;
            }

            // 2. 逐工作表对比
            for (int sheetIndex = 0; sheetIndex < workbook1.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet1 = workbook1.getSheetAt(sheetIndex);
                Sheet sheet2 = workbook2.getSheetAt(sheetIndex);

                // 校验工作表名称
                String sheetName1 = workbook1.getSheetName(sheetIndex);
                String sheetName2 = workbook2.getSheetName(sheetIndex);
                if (!sheetName1.equals(sheetName2)) {
                    DIFF_LIST.add("工作表名称不一致：索引" + sheetIndex + "，文件1=" + sheetName1 + "，文件2=" + sheetName2);
                }

                // 3. 逐行对比
                // 获取最大行数（避免漏判空行）
                int maxRowNum = Math.max(sheet1.getLastRowNum(), sheet2.getLastRowNum());
                for (int rowIndex = 0; rowIndex <= maxRowNum; rowIndex++) {
                    Row row1 = sheet1.getRow(rowIndex);
                    Row row2 = sheet2.getRow(rowIndex);

                    // 校验行是否存在
                    if (row1 == null && row2 == null) {
                        continue; // 两行都为空，跳过
                    }
                    if (row1 == null) {
                        DIFF_LIST.add("工作表[" + sheetName1 + "]行[" + rowIndex + "]：文件1无此行，文件2有");
                        continue;
                    }
                    if (row2 == null) {
                        DIFF_LIST.add("工作表[" + sheetName1 + "]行[" + rowIndex + "]：文件1有此行，文件2无");
                        continue;
                    }

                    // 4. 逐单元格对比
                    int maxCellNum = Math.max(row1.getLastCellNum(), row2.getLastCellNum());
                    for (int cellIndex = 0; cellIndex < maxCellNum; cellIndex++) {
                        Cell cell1 = row1.getCell(cellIndex);
                        Cell cell2 = row2.getCell(cellIndex);

                        // 获取单元格值（统一格式）
                        String cellValue1 = getCellValue(cell1);
                        String cellValue2 = getCellValue(cell2);

                        // 校验单元格值
                        if (!cellValue1.equals(cellValue2)) {
                            DIFF_LIST.add("工作表[" + sheetName1 + "]行[" + rowIndex + "]列[" + cellIndex + "]值不一致：" + "文件1=" + cellValue1 + "，文件2=" + cellValue2);
                        }
                    }
                }
            }

            // 无差异则返回true
            return DIFF_LIST.isEmpty();
        } catch (IOException e) {
            LoggerUtils.error(e);
            return false;
        }
    }

    /**
     * 根据文件流和路径获取Workbook（兼容xls和xlsx）
     */
    private static Workbook getWorkbook(FileInputStream fis, String filePath) throws IOException {
        if (filePath.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis); // 2007+格式
        } else if (filePath.endsWith(".xls")) {
            return new HSSFWorkbook(fis); // 2003格式
        } else {
            throw new IllegalArgumentException("不支持的Excel格式：" + filePath);
        }
    }

    /**
     * 获取单元格值（统一转为字符串，避免类型差异）
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // 空单元格返回空字符串
        }

        // 设置日期格式（避免日期转为数字）
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim(); // 去除首尾空格，避免空格导致的误判
    }

    /**
     * 获取差异列表
     */
    public static List<String> getDiffList() {
        return new ArrayList<>(DIFF_LIST);
    }

}
