package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.ColumnInfoDto;
import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.service.generate
 * @date 2021/10/15
 */
public class GenerateAuditService {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        if (PAGE_TYPE_QUERY.equals(generateCodeDto.getPageType())) {
            return SYMBOL_EMPTY;
        }
        String fileName = generateCodeDto.getAuditServiceName();
        String packageName = generateCodeDto.getAuditServicePackageName().replace(SYMBOL_POINT + fileName, SYMBOL_EMPTY);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.hundsun.jres.impl.bizkernel.runtime.exception.BizBussinessException;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.db.session.IDBSession;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.share.dataset.IDataset;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.util.ManageUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.util.FundCommonUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.log.LcptLog;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.base.AsynAuditConsoleAdapter;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.base.AsyncEntryAuditUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.dto.AsynAuditDTO;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.dto.WorkProcessDTO;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.interfaces.IWorkProcessAction;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.constant.IErrMsg;").append(SYMBOL_NEXT_LINE);
        content.append("import org.springframework.beans.factory.annotation.Autowired;").append(SYMBOL_NEXT_LINE);
        content.append("import org.springframework.stereotype.Service;").append(SYMBOL_NEXT_LINE);
        content.append("import java.sql.SQLException;").append(SYMBOL_NEXT_LINE);
        content.append("import java.util.Map;").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@Service").append(SYMBOL_NEXT_LINE);
        content.append("public class " + fileName + " extends AsynAuditConsoleAdapter implements IWorkProcessAction {").append(SYMBOL_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(SYMBOL_NEXT_LINE_2);

        content.append("    @Autowired").append(SYMBOL_NEXT_LINE);
        content.append("    " + generateCodeDto.getServiceName() + " service;").append(SYMBOL_NEXT_LINE_2);

        content.append("    /* ================================================ 常规需要改动的部分beg =============================================== */").append(SYMBOL_NEXT_LINE);

        content.append("    public static final String AUDIT_TABLE_NAME = \"" + generateCodeDto.getTableName() + "asy\";").append(SYMBOL_NEXT_LINE);
        content.append("    public static final String TABLE_NAME = \"" + generateCodeDto.getTableName() + "\";").append(SYMBOL_NEXT_LINE);
        content.append(GenerateCommon.generateSubTransCode(generateCodeDto, SUB_TRANSCODE_ADD));
        content.append(GenerateCommon.generateSubTransCode(generateCodeDto, SUB_TRANSCODE_EDIT));
        content.append(GenerateCommon.generateSubTransCode(generateCodeDto, SUB_TRANSCODE_DELETE));
        content.append(GenerateCommon.generateSubTransCode(generateCodeDto, SUB_TRANSCODE_IMPORT));
        content.append(SYMBOL_NEXT_LINE);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获得唯一记录的查询条件(主键或唯一索引)", METHOD_RETURN_PARAM_STRING, METHOD_REQUEST_PARAM_STRING));
        content.append("    private String getUniqueCondition(IDataset dataset) {").append(SYMBOL_NEXT_LINE);
        content.append("        StringBuilder condition = new StringBuilder();").append(SYMBOL_NEXT_LINE);
        StringBuilder condition = new StringBuilder();
        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        Map<String, ColumnInfoDto> asyTableColumn = generateCodeDto.getAsyColumnMap();
        if (MapUtils.isNotEmpty(generateCodeDto.getPrimaryKeyMap())) {
            Iterator<String> iterator = generateCodeDto.getPrimaryKeyMap().keySet().iterator();
            int index = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                String sourceKey = key;
                String keyHump = CommonUtils.lineToHump(key);
                String oriKey = "ori_" + key;
                String oriKeykeyHump = CommonUtils.lineToHump(oriKey);
                if (tableColumn.get(oriKeykeyHump) == null && asyTableColumn.get(oriKeykeyHump) != null) {
                    key = oriKey;
                    keyHump = oriKeykeyHump;
                }
                content.append("        String " + keyHump + " = dataset.getString(\"" + key + "\");").append(SYMBOL_NEXT_LINE);
                if (index != 0) {
                    condition.append("        condition.append(\" and " + sourceKey + " = '\" + " + keyHump + " + \"'\");").append(SYMBOL_NEXT_LINE);
                } else {
                    condition.append("        condition.append(\" " + sourceKey + " = '\" + " + keyHump + " + \"'\");").append(SYMBOL_NEXT_LINE);
                }
                index++;
            }
        }
        condition.append("        return condition.toString();");
        content.append(condition.toString()).append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "复核通过后处理", SYMBOL_EMPTY,
                METHOD_REQUEST_PARAM_TABLE_NAME, METHOD_REQUEST_PARAM_AUDIT_TABLE_NAME,
                METHOD_REQUEST_PARAM_ENTITY_DETAILS, METHOD_REQUEST_PARAM_SESSION, METHOD_REQUEST_PARAM_MAP));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    protected void afterDoCheckPass(String tableName, String auditTableName, IDataset entityDetails, IDBSession session, Map map) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        entityDetails.beforeFirst();").append(SYMBOL_NEXT_LINE);
        content.append("        for (int i = 0; i < entityDetails.getRowCount(); i++) {").append(SYMBOL_NEXT_LINE);
        content.append("            entityDetails.next();").append(SYMBOL_NEXT_LINE);
        content.append("            String entrySerialNo = entityDetails.getString(\"entry_serial_no\");").append(SYMBOL_NEXT_LINE);
        content.append("            String entryOrderNo = entityDetails.getString(\"entry_order_no\");").append(SYMBOL_NEXT_LINE);
        content.append("            String opDir = entityDetails.getString(\"op_dir\");").append(SYMBOL_NEXT_LINE);
        content.append("            // 复核表记录查询条件").append(SYMBOL_NEXT_LINE);
        content.append("            String condition = \"entry_serial_no ='\" + entrySerialNo + \"' and entry_order_no = \" + entryOrderNo;").append(SYMBOL_NEXT_LINE);
        content.append("            // 正式表记录查询条件").append(SYMBOL_NEXT_LINE);
        content.append("            String deleteCondition = getUniqueCondition(entityDetails);").append(SYMBOL_NEXT_LINE);
        content.append("            try {").append(SYMBOL_NEXT_LINE);
        content.append("                 // 同步临时表和正式表中的数据，用于审核通过时同步数据用，本方法不加事务安全，请在调用时增加").append(SYMBOL_NEXT_LINE);
        content.append("                AsyncEntryAuditUtil.doAcceptEntity(tableName, auditTableName, condition, deleteCondition, opDir, session);").append(SYMBOL_NEXT_LINE);
        content.append("            } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
        content.append("                LcptLog.getTransLogCache().error(\"复核通过失败\", e);").append(SYMBOL_NEXT_LINE);
        content.append("                throw new BizBussinessException(IErrMsg.ERR_DEFAULT, \"复核通过失败\");").append(SYMBOL_NEXT_LINE);
        content.append("            }").append(SYMBOL_NEXT_LINE);
        content.append("        }").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "复核通过后处理", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public void afterFhService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE);

        content.append("    /* ================================================ 常规需要改动的部分end =============================================== */").append(SYMBOL_NEXT_LINE);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "复核后续操作", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public void cfmService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        AsynAuditDTO asynAuditDTO = new AsynAuditDTO();").append(SYMBOL_NEXT_LINE);
        content.append("        setTableName(asynAuditDTO);").append(SYMBOL_NEXT_LINE);
        content.append("        asynAuditDTO.setResCode(workProcessDTO.getTransCodeAndSubTransCode().split(\"\\\\$\")[0]);").append(SYMBOL_NEXT_LINE);
        content.append("        asynAuditDTO.setIsCheckAuditUser(workProcessDTO.getIsCheckAuditUser() + \"\");").append(SYMBOL_NEXT_LINE);
        content.append("        asynAuditDTO.setEntrySerialNo(workProcessDTO.getEntrySerialNo());").append(SYMBOL_NEXT_LINE);
        content.append("        doCheckPass(asynAuditDTO);").append(SYMBOL_NEXT_LINE);
        content.append("        // 取出复核的dataset塞入，用于复核通过后的其他后续操作").append(SYMBOL_NEXT_LINE);
        content.append("        workProcessDTO.setAudiDataSet(asynAuditDTO.getAudiDataSet());").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "set表名", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_ASYN_AUDIT_DTO));
        content.append("    private void setTableName(AsynAuditDTO asynAuditDTO) {").append(SYMBOL_NEXT_LINE);
        content.append("        asynAuditDTO.setTableName(TABLE_NAME);").append(SYMBOL_NEXT_LINE);
        content.append("        asynAuditDTO.setAuditTableName(AUDIT_TABLE_NAME);").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获得唯一记录的查询条件(主键或唯一索引)",
                METHOD_RETURN_PARAM_IDATASET, METHOD_REQUEST_PARAM_SESSION, METHOD_REQUEST_PARAM_STRING));
        content.append("    private IDataset getUniqueDataset(IDBSession session, IDataset dataset) throws SQLException {").append(SYMBOL_NEXT_LINE);
        content.append("        return session.getDataSet(\"select * from \" + TABLE_NAME + \" where \" + getUniqueCondition(dataset));").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "取消服务", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public void cancelService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        AsynAuditDTO asynAuditDto = new AsynAuditDTO();").append(SYMBOL_NEXT_LINE);
        content.append("        asynAuditDto.setResCode(workProcessDTO.getTransCodeAndSubTransCode().split(\"\\\\$\")[0]);").append(SYMBOL_NEXT_LINE);
        content.append("        setTableName(asynAuditDto);").append(SYMBOL_NEXT_LINE);
        content.append("        workProcessDTO.setEntrySerialNo(workProcessDTO.getEntrySerialNo());").append(SYMBOL_NEXT_LINE);
        content.append("        doCheckFail(asynAuditDto);").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "待复核数据查询", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public String queryDataService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        return FundCommonUtil." + getCommonMethod(generateCodeDto, STR_1) + "(workProcessDTO, service, this, this.getClass());").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "未审批流程撤回后业务逻辑", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public void retractService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        doRetract(FundCommonUtil.retractService(workProcessDTO, TABLE_NAME, AUDIT_TABLE_NAME));").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "流程删除后业务逻辑", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public void delService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        doDel(FundCommonUtil.delService(workProcessDTO, TABLE_NAME, AUDIT_TABLE_NAME));").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "查询日志详情接口", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_WORK_PROCESS_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public String queryLogDataService(WorkProcessDTO workProcessDTO) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        return FundCommonUtil." + getCommonMethod(generateCodeDto, STR_2) + "(workProcessDTO, service, this, this.getClass());").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append("}").append(SYMBOL_NEXT_LINE_2);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }

    public static void getPackageName(GenerateCodeDto generateCodeDto) {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "AuditService";
        String packageName = PACKAGE_JAVA_PREFIX + "impl." + generateCodeDto.getMenuList().get(0)[0];

        generateCodeDto.setAuditServiceName(fileName);
        generateCodeDto.setAuditServicePackageName(packageName + SYMBOL_POINT + fileName);
    }

    private static String getCommonMethod(GenerateCodeDto generateCodeDto, String type) {
        String methodName = SYMBOL_EMPTY;
        if (STR_1.equals(type)) {
            switch (generateCodeDto.getDbType()) {
                case DB_TYPE_TRANS:
                    methodName = "queryRecheckDataByTrans";
                    break;
                case DB_TYPE_TRANS_ORDER:
                    methodName = "queryRecheckDataByOrder";
                    break;
                case DB_TYPE_PUB:
                case DB_TYPE_TRANS_QUERY:
                default:
                    methodName = "queryRecheckData";
                    break;
            }
        } else {
            switch (generateCodeDto.getDbType()) {
                case DB_TYPE_TRANS:
                    methodName = "queryLogDataByTrans";
                    break;
                case DB_TYPE_TRANS_ORDER:
                    methodName = "queryLogDataByOrder";
                    break;
                case DB_TYPE_PUB:
                case DB_TYPE_TRANS_QUERY:
                default:
                    methodName = "queryLogData";
                    break;
            }
        }
        return methodName;
    }
}
