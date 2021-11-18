package com.hoomoomoo.im.service;

import com.hoomoomoo.im.dto.GenerateCodeDto;
import com.hoomoomoo.im.utils.CommonUtils;
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
public class GenerateService {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "Service";
        String packageName = PACKAGE_JAVA_PREFIX + "impl." + generateCodeDto.getMenuList().get(0)[0];

        generateCodeDto.setServiceName(fileName);
        generateCodeDto.setServicePackageName(packageName + SYMBOL_POINT + fileName);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.alibaba.fastjson.JSONArray;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.common.share.dataset.DatasetService;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.component.biz.authority.cache.UserInfo;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.impl.bizkernel.runtime.exception.BizBussinessException;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.impl.db.session.DBSessionFactory;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.db.session.IDBSession;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.share.dataset.IDataset;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.base.BizAuditExtendEntity;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.util.ManageUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.util.UserInfoSessionUtil;").append(SYMBOL_NEXT_LINE);
        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append("import " + generateCodeDto.getImportPackageName() + ";").append(SYMBOL_NEXT_LINE);
        }
        content.append("import " + generateCodeDto.getDtoPackageName() + ";").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.impl.FundBaseService;").append(SYMBOL_NEXT_LINE);
        content.append("import " + generateCodeDto.getInterfacePackageName() + ";").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.util.FundCommonUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.util.FundManageDbSessionFactory;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.log.LcptLog;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.base.ApplyConsoleAdapter;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.interfaces.IWorkProcessService;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.util.WorkFlowUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.base.fund.constant.IFundConst;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.base.fund.constant.IFundDict;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.constant.IErrMsg;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.domain.bean.Dict;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.services.factory.PubSequenceFactory;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.util.TaManageUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.util.DataUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.util.DatasetUtil;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.util.HsSqlString;").append(SYMBOL_NEXT_LINE);
        content.append("import org.apache.commons.lang3.StringUtils;").append(SYMBOL_NEXT_LINE);
        content.append("import org.springframework.beans.factory.annotation.Autowired;").append(SYMBOL_NEXT_LINE);
        content.append("import org.springframework.stereotype.Service;").append(SYMBOL_NEXT_LINE);
        content.append("import java.sql.SQLException;").append(SYMBOL_NEXT_LINE);
        content.append("import java.util.Arrays;").append(SYMBOL_NEXT_LINE);
        content.append("import java.util.HashMap;").append(SYMBOL_NEXT_LINE);
        content.append("import java.util.Map;").append(SYMBOL_NEXT_LINE);
        content.append("import java.util.List;").append(SYMBOL_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.base.webmanager.QueryStatInfo;").append(SYMBOL_NEXT_LINE);
        content.append(initTranslate(generateCodeDto, STR_0)).append(SYMBOL_NEXT_LINE_2);
        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@Service").append(SYMBOL_NEXT_LINE);
        content.append("public class " + fileName + " extends FundBaseService<" + generateCodeDto.getDtoNameDto() + "> implements " + generateCodeDto.getInterfaceName() + " {").append(SYMBOL_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(SYMBOL_NEXT_LINE_2);

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append("    @Autowired").append(SYMBOL_NEXT_LINE);
            content.append("    private IWorkProcessService workProcessService;").append(SYMBOL_NEXT_LINE_2);
        }
        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "结果集翻译", SYMBOL_EMPTY, METHOD_REQUEST_PARAM_STRING));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public void datasetTranslate(IDataset dataset) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        if (dataset.getRowCount() < 1) {").append(SYMBOL_NEXT_LINE);
        content.append("            return;").append(SYMBOL_NEXT_LINE);
        content.append("        }").append(SYMBOL_NEXT_LINE_2);
        content.append("        HashMap<String, List<Dict>> dictMap = new HashMap<>(16);").append(SYMBOL_NEXT_LINE);
        content.append(initTranslate(generateCodeDto, STR_1));
        content.append("        dataset.beforeFirst();").append(SYMBOL_NEXT_LINE);
        content.append("        for (int i = 0; i < dataset.getRowCount(); i++) {").append(SYMBOL_NEXT_LINE);
        content.append("            dataset.next();").append(SYMBOL_NEXT_LINE_2);
        content.append(initTranslate(generateCodeDto, STR_2));
        content.append("        }").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获取查询拼接sql", METHOD_RETURN_PARAM_HS_SQL_STRING, METHOD_REQUEST_PARAM_DTO));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public HsSqlString getHss(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append("        String[] selectField = {").append(SYMBOL_NEXT_LINE);
        content.append(initQueryColumn(generateCodeDto)).append(SYMBOL_NEXT_LINE);
        content.append("        };").append(SYMBOL_NEXT_LINE);
        content.append("        String tableName = FundCustomIndexSetAuditService.TABLE_NAME + \" a \";").append(SYMBOL_NEXT_LINE);
        content.append("        HsSqlString hss = new HsSqlString(tableName, selectField);").append(SYMBOL_NEXT_LINE_2);
        content.append(initCondition(generateCodeDto)).append(SYMBOL_NEXT_LINE);
        content.append("        // 默认排序字段").append(SYMBOL_NEXT_LINE);
        content.append("        return hss;").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        if (PAGE_TYPE_QUERY.equals(generateCodeDto.getPageType())) {
            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获取汇总统计信息", METHOD_RETURN_PARAM_QUERY_STAT_INFO, null));
            content.append("   @Override").append(SYMBOL_NEXT_LINE);
            content.append("   protected QueryStatInfo getQueryStatInfo(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append("        QueryStatInfo statInfo = new QueryStatInfo();").append(SYMBOL_NEXT_LINE);
            content.append("        statInfo.setCacheResult(false);").append(SYMBOL_NEXT_LINE);
            content.append("        // 汇总统计信息").append(SYMBOL_NEXT_LINE);
            content.append("        return statInfo;").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);
        }

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_QUERY, METHOD_RETURN_PARAM_IDATASET));
        content.append("    @Override").append(SYMBOL_NEXT_LINE);
        content.append("    public IDataset queryService(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
        content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
        content.append("        return super.commonQuery(dto, session);").append(SYMBOL_NEXT_LINE);
        content.append("    }").append(SYMBOL_NEXT_LINE_2);

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_ADD, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(SYMBOL_NEXT_LINE);
            content.append("    public IDataset addService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = JSONArray.parseArray(dto.getDtoList(), " + generateCodeDto.getDtoNameDto() + ".class);").append(SYMBOL_NEXT_LINE);
            content.append("        if (dtoListTmp == null || dtoListTmp.size() < 1) {").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"新增失败，数据错误\");").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        // 生成待复核流水").append(SYMBOL_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(SYMBOL_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(SYMBOL_NEXT_LINE);
            content.append("        try {").append(SYMBOL_NEXT_LINE);
            content.append("            session.beginTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(SYMBOL_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " addDto = dtoListTmp.get(i);").append(SYMBOL_NEXT_LINE);
            content.append("                // 检查正表数据").append(SYMBOL_NEXT_LINE);
            content.append("                 checkHasAdd(addDto);").append(SYMBOL_NEXT_LINE);
            content.append("                // 检查复核表数据").append(SYMBOL_NEXT_LINE);
            content.append("                checkHasAddAsy(addDto);").append(SYMBOL_NEXT_LINE);
            content.append("                addDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_ADD);").append(SYMBOL_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, addDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_ADD, i == dtoListTmp.size() - 1);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            session.endTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
            content.append("            try {").append(SYMBOL_NEXT_LINE);
            content.append("                session.rollback();").append(SYMBOL_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(SYMBOL_NEXT_LINE);
            content.append("                LcptLog.getConsoleLog().error(\"数据库回滚失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            LcptLog.getConsoleLog().error(\"新增失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"新增失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("         return returnDataset;").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_EDIT, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(SYMBOL_NEXT_LINE);
            content.append("    public IDataset editService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = JSONArray.parseArray(dto.getDtoList(), " + generateCodeDto.getDtoNameDto() + ".class);").append(SYMBOL_NEXT_LINE);
            content.append("        if (dtoListTmp == null || dtoListTmp.size() < 1) {").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"修改失败，数据错误\");").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        // 生成待复核流水").append(SYMBOL_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(SYMBOL_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(SYMBOL_NEXT_LINE);
            content.append("        try {").append(SYMBOL_NEXT_LINE);
            content.append("            session.beginTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(SYMBOL_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " editDto = dtoListTmp.get(i);").append(SYMBOL_NEXT_LINE);
            content.append("                // 检查复核表数据").append(SYMBOL_NEXT_LINE);
            content.append("                checkHasAddAsy(editDto);").append(SYMBOL_NEXT_LINE);
            content.append("                // 工作流交易码").append(SYMBOL_NEXT_LINE);
            content.append("                editDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_EDIT);").append(SYMBOL_NEXT_LINE);
            content.append("                // 调用公共方法操作数据库").append(SYMBOL_NEXT_LINE);
            content.append("                 returnDataset = addAsyRecord(session, editDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_UPDATE, i == dtoListTmp.size() - 1);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            session.endTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
            content.append("            try {").append(SYMBOL_NEXT_LINE);
            content.append("                session.rollback();").append(SYMBOL_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(SYMBOL_NEXT_LINE);
            content.append("                 LcptLog.getTransLogNoCache().error(\"数据库回滚失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"修改失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"修改失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("         }").append(SYMBOL_NEXT_LINE);
            content.append("        return returnDataset;").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_DELETE, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(SYMBOL_NEXT_LINE);
            content.append("    public IDataset deleteService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = JSONArray.parseArray(dto.getDtoList(), " + generateCodeDto.getDtoNameDto() + ".class);").append(SYMBOL_NEXT_LINE);
            content.append("        if (dtoListTmp == null || dtoListTmp.size() < 1) {").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"删除失败，数据错误\");").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(SYMBOL_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(SYMBOL_NEXT_LINE);
            content.append("        try {").append(SYMBOL_NEXT_LINE);
            content.append("            session.beginTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(SYMBOL_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " delDto = dtoListTmp.get(i);").append(SYMBOL_NEXT_LINE);
            content.append("                // 检查复核表数据").append(SYMBOL_NEXT_LINE);
            content.append("                checkHasAddAsy(delDto);").append(SYMBOL_NEXT_LINE);
            content.append("                // 工作流交易码").append(SYMBOL_NEXT_LINE);
            content.append("                delDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_DELETE);").append(SYMBOL_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, delDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_DELETE, i == dtoListTmp.size() - 1);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            session.endTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
            content.append("            try {").append(SYMBOL_NEXT_LINE);
            content.append("                session.rollback();").append(SYMBOL_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(SYMBOL_NEXT_LINE);
            content.append("                LcptLog.getTransLogNoCache().error(\"数据回滚失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"删除失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"删除失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        return returnDataset;").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_IMPORT, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(SYMBOL_NEXT_LINE);
            content.append("    public IDataset importService (" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = getData();").append(SYMBOL_NEXT_LINE);
            content.append("        // 生成待复核流水").append(SYMBOL_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(SYMBOL_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(SYMBOL_NEXT_LINE);
            content.append("        try {").append(SYMBOL_NEXT_LINE);
            content.append("            session.beginTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(SYMBOL_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " impDto = dtoListTmp.get(i);").append(SYMBOL_NEXT_LINE);
            content.append("                // 检查正表数据").append(SYMBOL_NEXT_LINE);
            content.append("                checkHasAdd(impDto);").append(SYMBOL_NEXT_LINE);
            content.append("                // 检查复核表数据").append(SYMBOL_NEXT_LINE);
            content.append("                checkHasAddAsy(impDto);").append(SYMBOL_NEXT_LINE);
            content.append("                impDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_IMPORT);").append(SYMBOL_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, impDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_ADD, i == dtoListTmp.size() - 1);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            session.endTransaction();").append(SYMBOL_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
            content.append("            try {").append(SYMBOL_NEXT_LINE);
            content.append("                session.rollback();").append(SYMBOL_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(SYMBOL_NEXT_LINE);
            content.append("                LcptLog.getConsoleLog().error(\"数据回滚失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("            LcptLog.getConsoleLog().error(\"导入失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"导入失败\", e);").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        return returnDataset;").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获得唯一记录的查询条件(主键或唯一索引)", METHOD_RETURN_PARAM_STRING, null));
            content.append("    private String getUniqueCondition(" + generateCodeDto.getDtoNameDto() + " dto) {").append(SYMBOL_NEXT_LINE);
            content.append("        StringBuilder condition = new StringBuilder();").append(SYMBOL_NEXT_LINE);
            StringBuilder condition = new StringBuilder("");
            if (StringUtils.isNotEmpty(generateCodeDto.getPrimaryKey())) {
                String[] keys = generateCodeDto.getPrimaryKey().split(SYMBOL_COMMA);
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];
                    String keyHump = CommonUtils.lineToHump(key);
                    content.append("        String " + keyHump + " = dto.get" + CommonUtils.initialUpper(keyHump) + "();").append(SYMBOL_NEXT_LINE);
                    if (i != 0) {
                        condition.append("        condition.append(\" and " + key + " = '\" + " + keyHump + " + \"'\");").append(SYMBOL_NEXT_LINE);
                    } else {
                        condition.append("        condition.append(\" " + key + " = '\" + " + keyHump + " + \"'\");").append(SYMBOL_NEXT_LINE);
                    }
                }
            }
            condition.append("        return condition.toString();");
            content.append(condition.toString()).append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "检查正式表是否有数据重复", SYMBOL_EMPTY, null));
            content.append("    private void checkHasAdd(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
            content.append("        try {").append(SYMBOL_NEXT_LINE);
            content.append("            String sql = \"select count(1) cnt from \" + " + generateCodeDto.getAuditServiceName() + ".TABLE_NAME + \" a where \" + getUniqueCondition(dto);").append(SYMBOL_NEXT_LINE);
            content.append("            if (session.account(sql) > 0) {").append(SYMBOL_NEXT_LINE);
            content.append("                throw new BizBussinessException(\"已存在该记录\");").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"已存在记录校验出错\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(IErrMsg.ERR_DEFAULT, \"已存在记录校验出错\");").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "检查复核表数据是否存在未复核流水", SYMBOL_EMPTY, null));
            content.append("    private void checkHasAddAsy(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(SYMBOL_NEXT_LINE);
            content.append("        try {").append(SYMBOL_NEXT_LINE);
            content.append("                        String sql = \"select count(1) cnt from \" + FundPropFavourSetAuditService.AUDIT_TABLE_NAME + \" a where \" + getUniqueCondition(dto) +").append(SYMBOL_NEXT_LINE);
            content.append("                                \"and (serial_status = ? or serial_status = ? or serial_status = ?)\";").append(SYMBOL_NEXT_LINE);
            content.append("            if (session.account(sql, BizAuditExtendEntity.STATUS_UNCHECK, BizAuditExtendEntity.STATUS_RETURN, BizAuditExtendEntity.STATUS_SAVE) > 0) {").append(SYMBOL_NEXT_LINE);
            content.append("                throw new BizBussinessException(\"存在未复核流水\");").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(SYMBOL_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"未复核流水校验出错\", e);").append(SYMBOL_NEXT_LINE);
            content.append("            throw new BizBussinessException(IErrMsg.ERR_DEFAULT, \"未复核流水校验出错\");").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "通用操作数据库方法(包含新增 修改 删除)",
                    METHOD_RETURN_PARAM_IDATASET, METHOD_REQUEST_PARAM_SESSION, METHOD_REQUEST_PARAM_DTO,
                    METHOD_REQUEST_PARAM_ENTRY_SERIAL_NO, METHOD_REQUEST_PARAM_ENTRY_ORDER_NO,
                    METHOD_REQUEST_PARAM_OPERATOR_MODE, METHOD_REQUEST_PARAM_FLAG));
            content.append("    private IDataset addAsyRecord(IDBSession session, " + generateCodeDto.getDtoNameDto() + " dto, String entrySerialNo,").append(SYMBOL_NEXT_LINE);
            content.append("                                  int entryOrderNo, String operatorMode, boolean flag) throws BizBussinessException, SQLException {").append(SYMBOL_NEXT_LINE);
            content.append("        UserInfo userInfo = UserInfoSessionUtil.getCurrUserInfo();").append(SYMBOL_NEXT_LINE);
            content.append("        HsSqlString hss = new HsSqlString(" + generateCodeDto.getAuditServiceName() + ".AUDIT_TABLE_NAME, HsSqlString.TypeInsert);").append(SYMBOL_NEXT_LINE);
            Map<String, Map<String, String>> columnMap = generateCodeDto.getColumnMap();
            Iterator<String> iterator = columnMap.keySet().iterator();
            while (iterator.hasNext()) {
                String column = iterator.next();
                if (GenerateCommon.skipColumn(column)) {
                    continue;
                }
                Map<String, String> columnInfo = columnMap.get(column);
                content.append("        hss.set(\"" + columnInfo.get(KEY_COLUMN_UNDERLINE) + "\", dto.get" + CommonUtils.initialUpper(column) + "());").append(SYMBOL_NEXT_LINE);
            }
            content.append("        ApplyConsoleAdapter.setOperationInfo(hss, entrySerialNo, entryOrderNo, BizAuditExtendEntity.STATUS_UNCHECK, operatorMode);").append(SYMBOL_NEXT_LINE);
            content.append("        session.executeByList(hss.getSqlString(), hss.getParamList());").append(SYMBOL_NEXT_LINE);
            content.append("        if (flag) {").append(SYMBOL_NEXT_LINE);
            content.append("            String returnCode = WorkFlowUtil.commonAddWorkProcess(dto.getTransCodeAndSubTransCode(),").append(SYMBOL_NEXT_LINE);
            content.append("                     userInfo.getUserId(), entrySerialNo, Integer.toString(entryOrderNo), workProcessService);").append(SYMBOL_NEXT_LINE);
            content.append("            return WorkFlowUtil.getWorkFlowDataSet(returnCode);").append(SYMBOL_NEXT_LINE);
            content.append("        }").append(SYMBOL_NEXT_LINE);
            content.append("        return DatasetService.getDefaultInstance().getDataset();").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获取导入数据",
                    String.format(METHOD_RETURN_PARAM_LIST, generateCodeDto.getDtoPackageName()), SYMBOL_EMPTY));
            content.append("    private List<" + generateCodeDto.getDtoNameDto() + "> getData() throws BizBussinessException {").append(SYMBOL_NEXT_LINE);
            content.append("        return FundCommonUtil.getImportData(\"" + generateCodeDto.getFunctionCode() + "Import.xlsx\", " + CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "ExcelConfig.class, " + generateCodeDto.getDtoNameDto() + ".class);").append(SYMBOL_NEXT_LINE);
            content.append("    }").append(SYMBOL_NEXT_LINE_2);
        }
        content.append("}").append(SYMBOL_NEXT_LINE_2);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }

    private static String initTranslate(GenerateCodeDto generateCodeDto, String type) {
        StringBuilder content = new StringBuilder();
        Map<String, Map<String, String>> tableColumn = generateCodeDto.getColumnMap();
        if (tableColumn.containsKey(KEY_PRD_CODE)) {
            if (STR_0.equals(type)) {
                content.append("import com.hundsun.lcpt.ta.pub.fund.domain.bean.FundProduct;").append(SYMBOL_NEXT_LINE);
            } else if (STR_1.equals(type)) {
                content.append("        Map<String, FundProduct> productMap = new HashMap<>(16);").append(SYMBOL_NEXT_LINE);
                content.append("        FundProduct productInfo;").append(SYMBOL_NEXT_LINE);
            } else if (STR_2.equals(type)) {
                content.append("            productInfo = FundManageCacheUtil.getProduct(productMap, dataset.getString(\"prd_code\"));").append(SYMBOL_NEXT_LINE);
                content.append("            DatasetUtil.setValue(dataset, \"prd_code_name\", ManageUtil.append(dataset.getString(\"prd_code\"), productInfo.getPrdName()));").append(SYMBOL_NEXT_LINE);
            }
        }
        if (tableColumn.containsKey(KEY_SELLER_CODE)) {
            if (STR_0.equals(type)) {
                content.append("import com.hundsun.lcpt.ta.pub.fund.domain.bean.FundSellerInfo;").append(SYMBOL_NEXT_LINE);
            } else if (STR_1.equals(type)) {
                content.append("        Map<String, FundSellerInfo> sellerMap = new HashMap<>(16);").append(SYMBOL_NEXT_LINE);
                content.append("        FundSellerInfo sellerInfo;").append(SYMBOL_NEXT_LINE);
            } else if (STR_2.equals(type)) {
                content.append("            sellerInfo = FundManageCacheUtil.getSeller(sellerMap, dataset.getString(\"seller_code\"));").append(SYMBOL_NEXT_LINE);
                content.append("            DatasetUtil.setValue(dataset, \"seller_code_name\", ManageUtil.append(dataset.getString(\"seller_code\"), sellerInfo.getSellerName()));").append(SYMBOL_NEXT_LINE);
            }
        }
        if (tableColumn.containsKey(KEY_BRANCH_NO)) {
            if (STR_0.equals(type)) {
                content.append("import com.hundsun.lcpt.ta.pub.fund.domain.bean.FundNetInfo;").append(SYMBOL_NEXT_LINE);
            } else if (STR_1.equals(type)) {
                content.append("        Map<String, FundNetInfo> branchMap = new HashMap<>(16);").append(SYMBOL_NEXT_LINE);
                content.append("        FundNetInfo branchInfo;").append(SYMBOL_NEXT_LINE);
            } else if (STR_2.equals(type)) {
                content.append("            branchInfo = FundManageCacheUtil.getBranch(branchMap, dataset.getString(\"seller_code\"), dataset.getString(\"branch_no\"));").append(SYMBOL_NEXT_LINE);
                content.append("            DatasetUtil.setValue(dataset, \"branch_no_name\", ManageUtil.append(dataset.getString(\"branch_no\"), branchInfo.getBranchName()));").append(SYMBOL_NEXT_LINE);
            }
        }
        if (STR_2.equals(type) && (tableColumn.containsKey("inClientNo") || tableColumn.containsKey("idType"))) {
            content.append("            if (IFundConst.CNST_STR_0.equals(dataset.getString(\"client_type\"))) {").append(SYMBOL_NEXT_LINE);
            content.append("                DatasetUtil.setValue(dataset, \"id_type_name\", ManageUtil.getDictPrompt(dictMap, IFundDict.F_C20023, dataset.getString(\"id_type\")));").append(SYMBOL_NEXT_LINE);
            content.append("            } else if (IFundConst.CNST_STR_1.equals(dataset.getString(\"client_type\"))) {").append(SYMBOL_NEXT_LINE);
            content.append("                DatasetUtil.setValue(dataset, \"id_type_name\", ManageUtil.getDictPrompt(dictMap, IFundDict.F_C20022, dataset.getString(\"id_type\")));").append(SYMBOL_NEXT_LINE);
            content.append("            } else if (IFundConst.CNST_STR_2.equals(dataset.getString(\"client_type\"))) {").append(SYMBOL_NEXT_LINE);
            content.append("                DatasetUtil.setValue(dataset, \"id_type_name\", ManageUtil.getDictPrompt(dictMap, IFundDict.F_C20379, dataset.getString(\"id_type\")));").append(SYMBOL_NEXT_LINE);
            content.append("            }").append(SYMBOL_NEXT_LINE);
        }
        if (STR_0.equals(type) && StringUtils.isNotEmpty(content)) {
            content.append("import com.hundsun.lcpt.fund.util.FundManageCacheUtil;").append(SYMBOL_NEXT_LINE);
        }
        if (!STR_0.equals(type) && StringUtils.isNotEmpty(content)) {
            content.append(SYMBOL_NEXT_LINE);
        }
        return content.toString();
    }

    private static String initQueryColumn(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder("               ");
        Map<String, Map<String, String>> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            Map<String, String> columnInfo = tableColumn.get(column);
            if (GenerateCommon.skipColumn(column)) {
                continue;
            }
            content.append("\"a." + columnInfo.get(KEY_COLUMN_UNDERLINE) + ",\"\n                + ");
        }
        String queryColumn = content.toString();
        int indexEnd = queryColumn.lastIndexOf(",");
        return queryColumn.substring(0, indexEnd) + "\"";
    }

    private static String initCondition(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder();
        Map<String, Map<String, String>> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            Map<String, String> columnInfo = tableColumn.get(column);
            if (GenerateCommon.skipColumn(column)) {
                continue;
            }
            if (KEY_PRD_CODE.equals(column)) {
                content.append("        String prdCode = dto.getPrdCode();").append(SYMBOL_NEXT_LINE);
                content.append("        if (!DataUtil.isNullStr(prdCode)) {").append(SYMBOL_NEXT_LINE);
                content.append("            if (prdCode.contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(SYMBOL_NEXT_LINE);
                content.append("                hss.setWhere(\"a.prd_code in (\" + TaManageUtil.getWhereByList(Arrays.asList(prdCode.split(IFundConst.CNST_PUNCTUATION_COMMA))) + \") \");").append(SYMBOL_NEXT_LINE);
                content.append("            } else {").append(SYMBOL_NEXT_LINE);
                content.append("                hss.setWhere(\"a.prd_code\", prdCode);").append(SYMBOL_NEXT_LINE);
                content.append("            }").append(SYMBOL_NEXT_LINE);
                content.append("        }").append(SYMBOL_NEXT_LINE);
            } else if (KEY_SELLER_CODE.equals(column)) {
                content.append("        String sellerCode = dto.getSellerCode();").append(SYMBOL_NEXT_LINE);
                content.append("        if (!DataUtil.isNullStr(sellerCode)) {").append(SYMBOL_NEXT_LINE);
                content.append("            if (sellerCode.split(IFundConst.CNST_PUNCTUATION_COMMA).length > 1) {").append(SYMBOL_NEXT_LINE);
                content.append("                hss.setWhere(\"a.seller_code in (\" + TaManageUtil.getWhereByList(Arrays.asList(sellerCode.split(IFundConst.CNST_PUNCTUATION_COMMA))) + \")\");").append(SYMBOL_NEXT_LINE);
                content.append("            } else {").append(SYMBOL_NEXT_LINE);
                content.append("                hss.setWhere(\"a.seller_code\", sellerCode);").append(SYMBOL_NEXT_LINE);
                content.append("            }").append(SYMBOL_NEXT_LINE);
                content.append("        }").append(SYMBOL_NEXT_LINE);
            } else if (KEY_BRANCH_NO.equals(column)) {
                content.append("        String branchNo = dto.getBranchNo();").append(SYMBOL_NEXT_LINE);
                content.append("        if (!DataUtil.isNullStr(branchNo)) {").append(SYMBOL_NEXT_LINE);
                content.append("            if (branchNo.contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(SYMBOL_NEXT_LINE);
                content.append("                hss.setWhere(\"a.branch_no in (\" + TaManageUtil.getWhereByList(Arrays.asList(branchNo.split(IFundConst.CNST_PUNCTUATION_COMMA))) + \")\");").append(SYMBOL_NEXT_LINE);
                content.append("            } else {").append(SYMBOL_NEXT_LINE);
                content.append("                hss.setWhere(\"a.branch_no\", branchNo);").append(SYMBOL_NEXT_LINE);
                content.append("            }").append(SYMBOL_NEXT_LINE);
                content.append("        }").append(SYMBOL_NEXT_LINE);
            } else {
                String columnType = columnInfo.get(KEY_COLUMN_TYPE);
                String columnUnderline = columnInfo.get(KEY_COLUMN_UNDERLINE);
                if (StringUtils.isNotEmpty(columnInfo.get(KEY_COLUMN_DICT))) {
                    content.append("        String " + column + " = dto.get" + CommonUtils.initialUpper(column) + "();").append(SYMBOL_NEXT_LINE);
                    content.append("        if (!DataUtil.isNullStr(" + column + ")) {").append(SYMBOL_NEXT_LINE);
                    content.append("            if (" + column + ".contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(SYMBOL_NEXT_LINE);
                    content.append("                hss.setWhere(\"a." + columnUnderline + " in (\" + TaManageUtil.getWhereByList(Arrays.asList(" + column + ".split(IFundConst.CNST_PUNCTUATION_COMMA))) + \")\");").append(SYMBOL_NEXT_LINE);
                    content.append("            } else {").append(SYMBOL_NEXT_LINE);
                    content.append("                hss.setWhere(\"a." + columnUnderline + "\", " + column + ");").append(SYMBOL_NEXT_LINE);
                    content.append("            }").append(SYMBOL_NEXT_LINE);
                    content.append("        }").append(SYMBOL_NEXT_LINE);
                } else if (KEY_COLUMN_TYPE_DATE.equals(columnType)) {
                    content.append("        String " + column + " = dto.get" + CommonUtils.initialUpper(column) + "();").append(SYMBOL_NEXT_LINE);
                    content.append("        if (!DataUtil.isNullStr(" + column + ") && " + column + ".contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(SYMBOL_NEXT_LINE);
                    content.append("            String[] " + column + "Split = " + column + ".split(IFundConst.CNST_PUNCTUATION_COMMA);").append(SYMBOL_NEXT_LINE);
                    content.append("            hss.setWhere(\"a." + columnUnderline + "\", \" >= \", " + column + "Split[0]);").append(SYMBOL_NEXT_LINE);
                    content.append("            hss.setWhere(\"a." + columnUnderline + "\", \" <= \", " + column + "Split[1]);").append(SYMBOL_NEXT_LINE);
                    content.append("        }").append(SYMBOL_NEXT_LINE);
                } else if (KEY_COLUMN_TYPE_INTEGER.equals(columnType) || KEY_COLUMN_TYPE_NUMBER.equals(columnType)) {
                    content.append("        String " + column + " = dto.get" + CommonUtils.initialUpper(column) + "();").append(SYMBOL_NEXT_LINE);
                    content.append("        if (!DataUtil.isNullStr(" + column + ")) {").append(SYMBOL_NEXT_LINE);
                    content.append("            String[] " + column + "Arr = " + column + ".split(IFundConst.CNST_PUNCTUATION_COMMA);").append(SYMBOL_NEXT_LINE);
                    content.append("            if (" + column + "Arr.length >= 1) {").append(SYMBOL_NEXT_LINE);
                    content.append("                if (" + column + ".startsWith(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(SYMBOL_NEXT_LINE);
                    content.append("                    hss.setWhere(\"a." + columnUnderline + "\", \" <= \", " + column + "Arr[1]);").append(SYMBOL_NEXT_LINE);
                    content.append("                } else {").append(SYMBOL_NEXT_LINE);
                    content.append("                    hss.setWhere(\"a." + columnUnderline + "\", \" >= \", " + column + "Arr[0]);").append(SYMBOL_NEXT_LINE);
                    content.append("                }").append(SYMBOL_NEXT_LINE);
                    content.append("            }").append(SYMBOL_NEXT_LINE);
                    content.append("            if (" + column + "Arr.length == 2) {").append(SYMBOL_NEXT_LINE);
                    content.append("                hss.setWhere(\"a." + columnUnderline + "\", \" <= \", " + column + "Arr[1]);").append(SYMBOL_NEXT_LINE);
                    content.append("            }").append(SYMBOL_NEXT_LINE);
                    content.append("        }").append(SYMBOL_NEXT_LINE);
                } else {
                    content.append("        if (!DataUtil.isNullStr(dto.get" + CommonUtils.initialUpper(column) + "())) {").append(SYMBOL_NEXT_LINE);
                    content.append("             hss.setWhere(\"a." + columnUnderline + "\", dto.get" + CommonUtils.initialUpper(column) + "());").append(SYMBOL_NEXT_LINE);
                    content.append("        }").append(SYMBOL_NEXT_LINE);
                }
            }
        }
        return content.toString();
    }

}
