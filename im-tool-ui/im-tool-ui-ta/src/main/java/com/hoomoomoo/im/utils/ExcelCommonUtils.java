package com.hoomoomoo.im.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelCommonUtils {

    private static Font getBoldFont(SXSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        return font;
    }

    private static CellStyle getCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));
        return cellStyle;
    }

    private static CellStyle getBaseCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = getCellStyle(workbook);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return cellStyle;
    }

    public static CellStyle getTitleCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = getBaseCellStyle(workbook);
        Font font = getBoldFont(workbook);
        font.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    public static CellStyle getCenterCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = getCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    public static CellStyle getRedCenterCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = getCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        font.setFontName("等线");
        cellStyle.setFont(font);
        return cellStyle;
    }

    public static CellStyle getBlueCenterCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = getCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setFontName("等线");
        cellStyle.setFont(font);
        return cellStyle;
    }

    public static CellStyle getWrapTextCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = getCellStyle(workbook);
        cellStyle.setWrapText(true);
        return cellStyle;
    }
}
