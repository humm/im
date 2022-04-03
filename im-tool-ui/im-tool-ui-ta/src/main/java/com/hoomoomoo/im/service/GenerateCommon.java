package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service.generate
 * @date 2021/10/15
 */
public class GenerateCommon {

    public static String generateFileDescribe(GenerateCodeDto generateCodeDto, String fileName, String packageName) {
        StringBuilder content = new StringBuilder();
        content.append("/********************************************").append(SYMBOL_NEXT_LINE);
        content.append(" * 文件名称: " + fileName + FILE_TYPE_JAVA).append(SYMBOL_NEXT_LINE);
        content.append(" * 系统名称: 理财登记过户平台").append(SYMBOL_NEXT_LINE);
        content.append(" * 模块名称:").append(SYMBOL_NEXT_LINE);
        content.append(" * 软件版权: 恒生电子股份有限公司").append(SYMBOL_NEXT_LINE);
        content.append(" * 功能说明: " + generateCodeDto.getFunctionName() + "").append(SYMBOL_NEXT_LINE);
        content.append(" * 系统版本: 6.0.0.0").append(SYMBOL_NEXT_LINE);
        content.append(" * 开发人员: " + generateCodeDto.getAuthor() + "").append(SYMBOL_NEXT_LINE);
        content.append(" * 开发时间: " + CommonUtils.getCurrentDateTime4() + "").append(SYMBOL_NEXT_LINE);
        content.append(" * 审核人员:").append(SYMBOL_NEXT_LINE);
        content.append(" * 相关文档:").append(SYMBOL_NEXT_LINE);
        content.append(" * 修改记录: 修改日期    修改人员    修改说明").append(SYMBOL_NEXT_LINE);
        content.append(" *       1. " + CommonUtils.getCurrentDateTime4() + "   " + generateCodeDto.getAuthor() + "   创建类").append(SYMBOL_NEXT_LINE);
        content.append(" *********************************************/").append(SYMBOL_NEXT_LINE_2);

        content.append("package " + packageName + ";").append(SYMBOL_NEXT_LINE_2);

        return content.toString();
    }

    public static String generateClassDescribe(GenerateCodeDto generateCodeDto, String fileName) {
        StringBuilder content = new StringBuilder();
        content.append("/**").append(SYMBOL_NEXT_LINE);
        content.append(" * @ClassName " + fileName + "").append(SYMBOL_NEXT_LINE);
        content.append(" * @Description " + generateCodeDto.getFunctionName() + "").append(SYMBOL_NEXT_LINE);
        content.append(" * @Author " + generateCodeDto.getAuthor() + "").append(SYMBOL_NEXT_LINE);
        content.append(" * @Date " + CommonUtils.getCurrentDateTime1() + "").append(SYMBOL_NEXT_LINE);
        content.append(" **/").append(SYMBOL_NEXT_LINE);
        return content.toString();
    }

    public static String generateMethodDescribe(GenerateCodeDto generateCodeDto, String operateType, String returnParam) {
        return generateMethodDescribe(generateCodeDto, operateType, null, returnParam, null);
    }

    public static String generateMethodDescribe(GenerateCodeDto generateCodeDto, String operateType,
                                                String describe, String returnParam, String... requestParam) {
        if (StringUtils.isEmpty(returnParam)) {
            returnParam = SYMBOL_EMPTY;
        }
        if (requestParam == null) {
            requestParam = new String[]{"dto"};
        }
        if (StringUtils.isEmpty(describe)) {
            describe = generateCodeDto.getFunctionName() + SYMBOL_HYPHEN + operateType;
        }
        StringBuilder content = new StringBuilder();
        content.append("    /**").append(SYMBOL_NEXT_LINE);
        content.append("     * " + describe).append(SYMBOL_NEXT_LINE);
        content.append("     *").append(SYMBOL_NEXT_LINE);
        for (String item : requestParam) {
            content.append("     * @param " + item).append(SYMBOL_NEXT_LINE);
        }
        content.append("     * @author: " + generateCodeDto.getAuthor() + "").append(SYMBOL_NEXT_LINE);
        content.append("     * @date: " + CommonUtils.getCurrentDateTime6() + "").append(SYMBOL_NEXT_LINE);
        content.append("     * @return: " + returnParam).append(SYMBOL_NEXT_LINE);
        content.append("     */").append(SYMBOL_NEXT_LINE);
        return content.toString();
    }

    public static String generateJavaFile(GenerateCodeDto generateCodeDto, String packageName, String fileName,
                                          String content) throws IOException {
        String pathName = generateCodeDto.getJavaPath() + PATH_JAVA_PREFIX + packageName.replace(SYMBOL_POINT, SYMBOL_SLASH) + SYMBOL_SLASH + fileName + FILE_TYPE_JAVA;
        FileUtils.writeFile(pathName, content, ENCODING_UTF8, false);
        return pathName;
    }

    public static String generateSqlFile(GenerateCodeDto generateCodeDto, String fileName, String content) throws IOException {
        String pathName = generateCodeDto.getSqlPath() + SYMBOL_SLASH + fileName + FILE_TYPE_SQL;
        FileUtils.writeFile(pathName, content, ENCODING_UTF8, false);
        return pathName;
    }

    public static String generateVueFile(GenerateCodeDto generateCodeDto, String packageName, String fileName, String content) throws IOException {
        String pathName = generateCodeDto.getVuePath() + SYMBOL_SLASH + packageName.replace(SYMBOL_POINT, SYMBOL_SLASH) + SYMBOL_SLASH + fileName + FILE_TYPE_VUE;
        FileUtils.writeFile(pathName, content, ENCODING_UTF8, false);
        return pathName;
    }

    public static String generateRouteFile(GenerateCodeDto generateCodeDto, String fileName, String content) throws IOException {
        String pathName = generateCodeDto.getRoutePath() + SYMBOL_SLASH + fileName + FILE_TYPE_ROUTE;
        FileUtils.writeFile(pathName, content, ENCODING_UTF8, false);
        return pathName;
    }

    public static String generateSubTransCode(GenerateCodeDto generateCodeDto, String operateType) throws IOException {
        return "    public static final String SUB_TRANSCODE_" + operateType.toUpperCase() + " = ManageUtil.appendSplit(\"$\", \"" + generateCodeDto.getFunctionCode() + "\", \"" + generateCodeDto.getFunctionCode() + operateType + "\");" + SYMBOL_NEXT_LINE;
    }

    public static String getDBSession(GenerateCodeDto generateCodeDto) {
        StringBuilder session = new StringBuilder();
        switch (generateCodeDto.getDbType()) {
            case DB_TYPE_PUB:
                session.append("        IDBSession session = FundManageDbSessionFactory.getPubSession();");
                break;
            case DB_TYPE_TRANS:
                session.append("        IDBSession session = FundManageDbSessionFactory.getTransSession();");
                break;
            case DB_TYPE_TRANS_QUERY:
                session.append("        IDBSession session = FundManageDbSessionFactory.getTransQuerySession();");
                break;
            case DB_TYPE_TRANS_ORDER:
                session.append("        IDBSession session = FundManageDbSessionFactory.getOrderSession();");
                break;
            default:
                session.append("        IDBSession session = FundManageDbSessionFactory.getPubSession();");
                break;
        }
        return session.toString();
    }

    public static String getSerialVersionUid() {
        StringBuilder serialVersionUid = new StringBuilder();
        for (int i = 0; i < 19; i++) {
            int num = (int) (Math.random() * 10);
            if (num >= 9) {
                num = 8;
            } else if (num == 0) {
                num = 1;
            }
            serialVersionUid.append(num);
        }
        return serialVersionUid.append("L").toString().replace("-9999999999999999999L", "-8999999999999999999L");
    }

    public static boolean skipColumn(ColumnInfoDto columnInfoDto) {
        return skipColumn(columnInfoDto, true);
    }

    public static boolean skipColumn(ColumnInfoDto columnInfoDto, boolean skip) {
        if (columnInfoDto == null) {
            return true;
        }
        String columnCode = columnInfoDto.getColumnCode();
        String columnName = columnInfoDto.getColumnName();
        if (KEY_TRANS_CODE_AND_SUB_TRANS_CODE.equals(columnCode) || KEY_TRANS_CODE_AND_SUB_TRANS_CODE_UNDERLINE.equals(columnCode)
                || KEY_TA_CODE.equals(columnCode) || KEY_TA_CODE_UNDERLINE.equals(columnCode)) {
            return true;
        }
        if (!skip) {
            return false;
        }
        if (StringUtils.isEmpty(columnName)) {
            return true;
        }
        return false;
    }

    public static int getColumnPrecision(ColumnInfoDto columnInfo) {
        int columnPrecision = 2;
        if (StringUtils.isNotBlank(columnInfo.getColumnPrecision())) {
            columnPrecision = Integer.valueOf(columnInfo.getColumnPrecision());
            if(columnInfo.getColumnName().contains(SYMBOL_PERCENT)) {
                columnPrecision = columnPrecision - 2;
            }
        }
        return columnPrecision;
    }

    public static boolean hasComponent(GenerateCodeDto generateCodeDto, String type) {
        boolean hasFlag = false;
        Map<String, ColumnInfoDto> columnInfoDtoMap = generateCodeDto.getColumnMap();
        Iterator<String> iterator = columnInfoDtoMap.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfo = columnInfoDtoMap.get(columnCode);
            if (GenerateCommon.skipColumn(columnInfo)) {
                continue;
            }
            switch (type) {
                case STR_1:
                    if (columnCode.toLowerCase().contains(KEY_PRD_CODE.toLowerCase())) {
                        hasFlag = true;
                        break;
                    }
                    break;
                case STR_2:
                    if(columnInfo.getColumnName().contains(SYMBOL_PERCENT)) {
                        hasFlag = true;
                        break;
                    }
                    break;
                case STR_3:
                    if(STR_1.equals(columnInfo.getColumnBatchUpdate())) {
                        hasFlag = true;
                        break;
                    }
                    break;
                case STR_4:
                    if(STR_1.equals(columnInfo.getColumnMultiSingle())) {
                        hasFlag = true;
                        break;
                    }
                    break;
                case STR_5:
                    if(STR_1.equals(columnInfo.getColumnMulti())) {
                        hasFlag = true;
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
        return hasFlag;
    }
}
