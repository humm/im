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
public class GenerateService {

    public static String init(GenerateCodeDto generateCodeDto) throws Exception {
        String fileName = CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "Service";
        String packageName = PACKAGE_JAVA_PREFIX + "impl." + generateCodeDto.getMenuList().get(0)[0];

        generateCodeDto.setServiceName(fileName);
        generateCodeDto.setServicePackageName(packageName + STR_POINT + fileName);

        StringBuilder content = new StringBuilder(GenerateCommon.generateFileDescribe(generateCodeDto, fileName, packageName));

        content.append("import com.alibaba.fastjson.JSONArray;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.common.share.dataset.DatasetService;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.component.biz.authority.cache.UserInfo;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.impl.bizkernel.runtime.exception.BizBussinessException;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.impl.db.session.DBSessionFactory;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.db.session.IDBSession;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.jres.interfaces.share.dataset.IDataset;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.base.BizAuditExtendEntity;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.util.ManageUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.bizframe.core.util.UserInfoSessionUtil;").append(STR_NEXT_LINE);
        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append("import " + generateCodeDto.getImportPackageName() + ";").append(STR_NEXT_LINE);
        }
        content.append("import " + generateCodeDto.getDtoPackageName() + ";").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.impl.FundBaseService;").append(STR_NEXT_LINE);
        content.append("import " + generateCodeDto.getInterfacePackageName() + ";").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.util.FundCommonUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.fund.util.FundManageDbSessionFactory;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.log.LcptLog;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.base.ApplyConsoleAdapter;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.interfaces.IWorkProcessService;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.pub.util.WorkFlowUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.base.fund.constant.IFundConst;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.base.fund.constant.IFundDict;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.constant.IErrMsg;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.domain.bean.Dict;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.services.factory.PubSequenceFactory;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.pub.util.TaManageUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.util.DataUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.util.DatasetUtil;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.util.HsSqlString;").append(STR_NEXT_LINE);
        content.append("import org.apache.commons.lang3.StringUtils;").append(STR_NEXT_LINE);
        content.append("import org.springframework.beans.factory.annotation.Autowired;").append(STR_NEXT_LINE);
        content.append("import org.springframework.stereotype.Service;").append(STR_NEXT_LINE);
        content.append("import java.math.BigDecimal;").append(STR_NEXT_LINE);
        content.append("import java.sql.SQLException;").append(STR_NEXT_LINE);
        content.append("import java.util.Arrays;").append(STR_NEXT_LINE);
        content.append("import java.util.HashMap;").append(STR_NEXT_LINE);
        content.append("import java.util.Map;").append(STR_NEXT_LINE);
        content.append("import java.util.List;").append(STR_NEXT_LINE);
        content.append("import com.hundsun.lcpt.ta.web.pub.stat.QueryStatInfo;").append(STR_NEXT_LINE);
        content.append(initTranslate(generateCodeDto, STR_0)).append(STR_NEXT_LINE_2);
        content.append(GenerateCommon.generateClassDescribe(generateCodeDto, fileName));

        content.append("@Service").append(STR_NEXT_LINE);
        content.append("public class " + fileName + " extends FundBaseService<" + generateCodeDto.getDtoNameDto() + "> implements " + generateCodeDto.getInterfaceName() + " {").append(STR_NEXT_LINE_2);
        content.append("    public static final String HUNDSUN_VERSION = \"@system 理财登记过户平台 @version 6.0.0.0 @lastModiDate " + CommonUtils.getCurrentDateTime3() + " @describe " + generateCodeDto.getAuthor() + "\";").append(STR_NEXT_LINE_2);

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append("    @Autowired").append(STR_NEXT_LINE);
            content.append("    private IWorkProcessService workProcessService;").append(STR_NEXT_LINE_2);
        }
        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "结果集翻译", STR_BLANK, METHOD_REQUEST_PARAM_STRING));
        content.append("    @Override").append(STR_NEXT_LINE);
        content.append("    public void datasetTranslate(IDataset dataset) throws BizBussinessException {").append(STR_NEXT_LINE);
        content.append("        if (dataset.getRowCount() < 1) {").append(STR_NEXT_LINE);
        content.append("            return;").append(STR_NEXT_LINE);
        content.append("        }").append(STR_NEXT_LINE_2);
        content.append("        HashMap<String, List<Dict>> dictMap = new HashMap<>(16);").append(STR_NEXT_LINE);
        content.append(initTranslate(generateCodeDto, STR_1));
        content.append("        dataset.beforeFirst();").append(STR_NEXT_LINE);
        content.append("        for (int i = 0; i < dataset.getRowCount(); i++) {").append(STR_NEXT_LINE);
        content.append("            dataset.next();").append(STR_NEXT_LINE_2);
        content.append(initTranslate(generateCodeDto, STR_2));
        content.append(initDictTranslate(generateCodeDto));
        content.append("        }").append(STR_NEXT_LINE);
        content.append("    }").append(STR_NEXT_LINE_2);

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获取查询拼接sql", METHOD_RETURN_PARAM_HS_SQL_STRING, METHOD_REQUEST_PARAM_DTO));
        content.append("    @Override").append(STR_NEXT_LINE);
        content.append("    public HsSqlString getHss(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
        content.append("        String[] selectField = {").append(STR_NEXT_LINE);
        content.append(initQueryColumn(generateCodeDto)).append(STR_NEXT_LINE);
        content.append("        };").append(STR_NEXT_LINE);
        content.append("        String tableName = " + generateCodeDto.getAuditServiceName() + ".TABLE_NAME + \" a \";").append(STR_NEXT_LINE);
        content.append("        HsSqlString hss = new HsSqlString(tableName, selectField);").append(STR_NEXT_LINE_2);
        content.append(initCondition(generateCodeDto)).append(STR_NEXT_LINE);
        if (StringUtils.isNotBlank(generateCodeDto.getColumnQueryOrder())) {
            content.append("        // 默认排序字段").append(STR_NEXT_LINE);
            content.append("        hss.setOrder(" + generateCodeDto.getColumnQueryOrder() + ");").append(STR_NEXT_LINE);
        }
        content.append("        return hss;").append(STR_NEXT_LINE);
        content.append("    }").append(STR_NEXT_LINE_2);

        if (PAGE_TYPE_QUERY.equals(generateCodeDto.getPageType())) {
            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获取汇总统计信息", METHOD_RETURN_PARAM_QUERY_STAT_INFO, null));
            content.append("   @Override").append(STR_NEXT_LINE);
            content.append("   protected QueryStatInfo getQueryStatInfo(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        QueryStatInfo statInfo = new QueryStatInfo();").append(STR_NEXT_LINE);
            content.append("        statInfo.setCacheResult(false);").append(STR_NEXT_LINE);
            content.append("        // 汇总统计信息").append(STR_NEXT_LINE);
            content.append("        return statInfo;").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);
        }

        content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_QUERY, METHOD_RETURN_PARAM_IDATASET));
        content.append("    @Override").append(STR_NEXT_LINE);
        content.append("    public IDataset queryService(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
        content.append(GenerateCommon.getDBSession(generateCodeDto)).append(STR_NEXT_LINE);
        content.append("        return super.commonQuery(dto, session);").append(STR_NEXT_LINE);
        content.append("    }").append(STR_NEXT_LINE_2);

        if (PAGE_TYPE_SET.equals(generateCodeDto.getPageType())) {
            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_ADD, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(STR_NEXT_LINE);
            content.append("    public IDataset addService(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = JSONArray.parseArray(dto.getDtoList(), " + generateCodeDto.getDtoNameDto() + ".class);").append(STR_NEXT_LINE);
            content.append("        if (dtoListTmp == null || dtoListTmp.size() < 1) {").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"新增失败，数据错误\");").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("        // 生成待复核流水").append(STR_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(STR_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(STR_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(STR_NEXT_LINE);
            content.append("        try {").append(STR_NEXT_LINE);
            content.append("            session.beginTransaction();").append(STR_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(STR_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " addDto = dtoListTmp.get(i);").append(STR_NEXT_LINE);
            content.append("                // 检查正表数据").append(STR_NEXT_LINE);
            content.append("                 checkHasAdd(addDto);").append(STR_NEXT_LINE);
            content.append("                // 检查复核表数据").append(STR_NEXT_LINE);
            content.append("                checkHasAddAsy(addDto);").append(STR_NEXT_LINE);
            content.append("                addDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_ADD);").append(STR_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, addDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_ADD, i == dtoListTmp.size() - 1);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            session.endTransaction();").append(STR_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(STR_NEXT_LINE);
            content.append("            try {").append(STR_NEXT_LINE);
            content.append("                session.rollback();").append(STR_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(STR_NEXT_LINE);
            content.append("                LcptLog.getConsoleLog().error(\"数据库回滚失败\", e);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            LcptLog.getConsoleLog().error(\"新增失败\", e);").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"新增失败\", e);").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("         return returnDataset;").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_EDIT, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(STR_NEXT_LINE);
            content.append("    public IDataset editService(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = JSONArray.parseArray(dto.getDtoList(), " + generateCodeDto.getDtoNameDto() + ".class);").append(STR_NEXT_LINE);
            content.append("        if (dtoListTmp == null || dtoListTmp.size() < 1) {").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"修改失败，数据错误\");").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("        // 生成待复核流水").append(STR_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(STR_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(STR_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(STR_NEXT_LINE);
            content.append("        try {").append(STR_NEXT_LINE);
            content.append("            session.beginTransaction();").append(STR_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(STR_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " editDto = dtoListTmp.get(i);").append(STR_NEXT_LINE);
            content.append(buildCheckHasAdd(generateCodeDto));
            content.append("                // 检查复核表数据").append(STR_NEXT_LINE);
            content.append("                checkHasAddAsy(editDto);").append(STR_NEXT_LINE);
            content.append("                // 工作流交易码").append(STR_NEXT_LINE);
            content.append("                editDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_EDIT);").append(STR_NEXT_LINE);
            content.append("                // 调用公共方法操作数据库").append(STR_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, editDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_UPDATE, i == dtoListTmp.size() - 1);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            session.endTransaction();").append(STR_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(STR_NEXT_LINE);
            content.append("            try {").append(STR_NEXT_LINE);
            content.append("                session.rollback();").append(STR_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(STR_NEXT_LINE);
            content.append("                 LcptLog.getTransLogNoCache().error(\"数据库回滚失败\", e);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"修改失败\", e);").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"修改失败\", e);").append(STR_NEXT_LINE);
            content.append("         }").append(STR_NEXT_LINE);
            content.append("        return returnDataset;").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_DELETE, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(STR_NEXT_LINE);
            content.append("    public IDataset deleteService(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = JSONArray.parseArray(dto.getDtoList(), " + generateCodeDto.getDtoNameDto() + ".class);").append(STR_NEXT_LINE);
            content.append("        if (dtoListTmp == null || dtoListTmp.size() < 1) {").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"删除失败，数据错误\");").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(STR_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(STR_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(STR_NEXT_LINE);
            content.append("        try {").append(STR_NEXT_LINE);
            content.append("            session.beginTransaction();").append(STR_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(STR_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " delDto = dtoListTmp.get(i);").append(STR_NEXT_LINE);
            content.append("                // 检查复核表数据").append(STR_NEXT_LINE);
            content.append("                checkHasAddAsy(delDto);").append(STR_NEXT_LINE);
            content.append("                // 工作流交易码").append(STR_NEXT_LINE);
            content.append("                delDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_DELETE);").append(STR_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, delDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_DELETE, i == dtoListTmp.size() - 1);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            session.endTransaction();").append(STR_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(STR_NEXT_LINE);
            content.append("            try {").append(STR_NEXT_LINE);
            content.append("                session.rollback();").append(STR_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(STR_NEXT_LINE);
            content.append("                LcptLog.getTransLogNoCache().error(\"数据回滚失败\", e);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"删除失败\", e);").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"删除失败\", e);").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("        return returnDataset;").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, METHOD_TYPE_IMPORT, METHOD_RETURN_PARAM_IDATASET));
            content.append("    @Override").append(STR_NEXT_LINE);
            content.append("    public IDataset importService(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        List<" + generateCodeDto.getDtoNameDto() + "> dtoListTmp = getData();").append(STR_NEXT_LINE);
            content.append("        // 生成待复核流水").append(STR_NEXT_LINE);
            content.append("        String entrySerialNo = PubSequenceFactory.getSequence().getAsyEntrySerialNo();").append(STR_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(STR_NEXT_LINE);
            content.append("        IDataset returnDataset = null;").append(STR_NEXT_LINE);
            content.append("        try {").append(STR_NEXT_LINE);
            content.append("            session.beginTransaction();").append(STR_NEXT_LINE);
            content.append("            for (int i = 0; i < dtoListTmp.size(); i++) {").append(STR_NEXT_LINE);
            content.append("                " + generateCodeDto.getDtoNameDto() + " impDto = dtoListTmp.get(i);").append(STR_NEXT_LINE);
            content.append(convertImportColumn(generateCodeDto.getColumnMap()));
            content.append("                // 检查正表数据").append(STR_NEXT_LINE);
            content.append("                checkHasAdd(impDto);").append(STR_NEXT_LINE);
            content.append("                // 检查复核表数据").append(STR_NEXT_LINE);
            content.append("                checkHasAddAsy(impDto);").append(STR_NEXT_LINE);
            content.append("                impDto.setTransCodeAndSubTransCode(" + generateCodeDto.getAuditServiceName() + ".SUB_TRANSCODE_IMPORT);").append(STR_NEXT_LINE);
            content.append("                returnDataset = addAsyRecord(session, impDto, entrySerialNo, i + 1, BizAuditExtendEntity.OPERATOR_MODE_ADD, i == dtoListTmp.size() - 1);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            session.endTransaction();").append(STR_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(STR_NEXT_LINE);
            content.append("            try {").append(STR_NEXT_LINE);
            content.append("                session.rollback();").append(STR_NEXT_LINE);
            content.append("            } catch (SQLException e1) {").append(STR_NEXT_LINE);
            content.append("                LcptLog.getConsoleLog().error(\"数据回滚失败\", e);").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("            LcptLog.getConsoleLog().error(\"导入失败\", e);").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(\"导入失败\", e);").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("        return returnDataset;").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获得唯一记录的查询条件(主键或唯一索引)", METHOD_RETURN_PARAM_STRING, null));
            content.append("    private String getUniqueCondition(" + generateCodeDto.getDtoNameDto() + " dto) {").append(STR_NEXT_LINE);
            content.append("        StringBuilder condition = new StringBuilder();").append(STR_NEXT_LINE);
            StringBuilder condition = new StringBuilder("");
            if (MapUtils.isNotEmpty(generateCodeDto.getPrimaryKeyMap())) {
                Iterator<String> iterator = generateCodeDto.getPrimaryKeyMap().keySet().iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    String key = iterator.next().toLowerCase();
                    String keyHump = CommonUtils.lineToHump(key);
                    content.append("        String " + keyHump + " = dto.get" + CommonUtils.initialUpper(keyHump) + "();").append(STR_NEXT_LINE);
                    if (index != 0) {
                        condition.append("        condition.append(\" and " + key + " = '\" + " + keyHump + " + \"'\");").append(STR_NEXT_LINE);
                    } else {
                        condition.append("        condition.append(\" " + key + " = '\" + " + keyHump + " + \"'\");").append(STR_NEXT_LINE);
                    }
                    index++;
                }
            }
            condition.append("        return condition.toString();");
            content.append(condition.toString()).append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "检查正式表是否有数据重复", STR_BLANK, null));
            content.append("    private void checkHasAdd(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append(GenerateCommon.getDBSession(generateCodeDto)).append(STR_NEXT_LINE);
            content.append("        try {").append(STR_NEXT_LINE);
            content.append("            String sql = \"select count(1) cnt from \" + " + generateCodeDto.getAuditServiceName() + ".TABLE_NAME + \" a where \" + getUniqueCondition(dto);").append(STR_NEXT_LINE);
            content.append("            if (session.account(sql) > 0) {").append(STR_NEXT_LINE);
            content.append("                throw new BizBussinessException(\"已存在该记录" + getRepeatTips(generateCodeDto) + ");").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
            content.append("        } catch (SQLException e) {").append(STR_NEXT_LINE);
            content.append("            LcptLog.getTransLogNoCache().error(\"已存在记录校验出错\", e);").append(STR_NEXT_LINE);
            content.append("            throw new BizBussinessException(IErrMsg.ERR_DEFAULT, \"已存在记录校验出错\");").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "检查复核表数据是否存在未复核流水", STR_BLANK, null));
            content.append("    private void checkHasAddAsy(" + generateCodeDto.getDtoNameDto() + " dto) throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        FundCommonUtil.checkHasAddAsy(\"select count(1) cnt from \" + " + generateCodeDto.getAuditServiceName() + ".AUDIT_TABLE_NAME + \" a where \" +").append(STR_NEXT_LINE);
            content.append("            " + getCheckHasAddAsyColumn(generateCodeDto, STR_1)).append(STR_NEXT_LINE);
            content.append("            \"and (serial_status = ? or serial_status = ? or serial_status = ?)\",").append(STR_NEXT_LINE);
            content.append("            dto, " + getCheckHasAddAsyColumn(generateCodeDto, STR_2)).append(STR_NEXT_LINE);
            content.append("            BizAuditExtendEntity.STATUS_UNCHECK, BizAuditExtendEntity.STATUS_RETURN, BizAuditExtendEntity.STATUS_SAVE);").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "通用操作数据库方法(包含新增 修改 删除)",
                    METHOD_RETURN_PARAM_IDATASET, METHOD_REQUEST_PARAM_SESSION, METHOD_REQUEST_PARAM_DTO,
                    METHOD_REQUEST_PARAM_ENTRY_SERIAL_NO, METHOD_REQUEST_PARAM_ENTRY_ORDER_NO,
                    METHOD_REQUEST_PARAM_OPERATOR_MODE, METHOD_REQUEST_PARAM_FLAG));
            content.append("    private IDataset addAsyRecord(IDBSession session, " + generateCodeDto.getDtoNameDto() + " dto, String entrySerialNo,").append(STR_NEXT_LINE);
            content.append("                                  int entryOrderNo, String operatorMode, boolean flag) throws BizBussinessException, SQLException {").append(STR_NEXT_LINE);
            content.append("        UserInfo userInfo = UserInfoSessionUtil.getCurrUserInfo();").append(STR_NEXT_LINE);
            content.append("        HsSqlString hss = new HsSqlString(" + generateCodeDto.getAuditServiceName() + ".AUDIT_TABLE_NAME, HsSqlString.TypeInsert);").append(STR_NEXT_LINE);
            Map<String, ColumnInfoDto> columnMap = generateCodeDto.getColumnMap();
            Iterator<String> iterator = columnMap.keySet().iterator();
            while (iterator.hasNext()) {
                String column = iterator.next();
                ColumnInfoDto columnInfo = columnMap.get(column);
                if (GenerateCommon.skipColumn(columnInfo)) {
                    continue;
                }
                content.append("        hss.set(\"" + columnInfo.getColumnUnderline() + "\", " + getAsyRecordColumnValue(columnInfo) + ");").append(STR_NEXT_LINE);
            }
            content.append("        ApplyConsoleAdapter.setOperationInfo(hss, entrySerialNo, entryOrderNo, BizAuditExtendEntity.STATUS_UNCHECK, operatorMode);").append(STR_NEXT_LINE);
            content.append("        session.executeByList(hss.getSqlString(), hss.getParamList());").append(STR_NEXT_LINE);
            content.append("        if (flag) {").append(STR_NEXT_LINE);
            content.append("            String returnCode = WorkFlowUtil.commonAddWorkProcess(dto.getTransCodeAndSubTransCode(),").append(STR_NEXT_LINE);
            content.append("                     userInfo.getUserId(), entrySerialNo, Integer.toString(entryOrderNo), workProcessService);").append(STR_NEXT_LINE);
            content.append("            return WorkFlowUtil.getWorkFlowDataSet(returnCode);").append(STR_NEXT_LINE);
            content.append("        }").append(STR_NEXT_LINE);
            content.append("        return DatasetService.getDefaultInstance().getDataset();").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);

            content.append(GenerateCommon.generateMethodDescribe(generateCodeDto, null, "获取导入数据", String.format(METHOD_RETURN_PARAM_LIST, generateCodeDto.getDtoPackageName()), STR_BLANK));
            content.append("    private List<" + generateCodeDto.getDtoNameDto() + "> getData() throws BizBussinessException {").append(STR_NEXT_LINE);
            content.append("        return FundCommonUtil.getImportData(\"" + generateCodeDto.getFunctionCode() + "Import.xlsx\", " + CommonUtils.initialUpper(generateCodeDto.getFunctionCode()) + "ExcelConfig.class, " + generateCodeDto.getDtoNameDto() + ".class);").append(STR_NEXT_LINE);
            content.append("    }").append(STR_NEXT_LINE_2);
        }
        content.append("}").append(STR_NEXT_LINE_2);
        return GenerateCommon.generateJavaFile(generateCodeDto, packageName, fileName, content.toString());
    }

    private static String getAsyRecordColumnValue(ColumnInfoDto columnInfo) {
        String column = columnInfo.getColumnCode();
        String value = "dto.get" + CommonUtils.initialUpper(column) + "()";
        String columnType = columnInfo.getColumnType();
        if (KEY_COLUMN_TYPE_DATE.equals(columnType) || KEY_COLUMN_TYPE_INTEGER.equals(columnType) ) {
            value = "StringUtils.isEmpty(" + value + ") ? 0 : Integer.valueOf(" + value + ")";
        } else if (KEY_COLUMN_TYPE_NUMBER.equals(columnType)) {
            value = "StringUtils.isEmpty(" + value + ") ? 0 : Double.valueOf(" + value + ")";
        }
        return value;
    }

    private static String initDictTranslate(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder();
        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        while (iterator.hasNext()) {
            ColumnInfoDto columnInfoDto = tableColumn.get(iterator.next());
            if (StringUtils.isNotEmpty(columnInfoDto.getColumnDict())) {
                content.append("            DatasetUtil.setValue(dataset, \"" + columnInfoDto.getColumnUnderline() + "_name\", ManageUtil.getDictPrompt(dictMap, IFundDict." + columnInfoDto.getColumnDict() + ", dataset.getString(\"" + columnInfoDto.getColumnUnderline() + "\")));").append(STR_NEXT_LINE);
            }
        }
        return content.toString();
    }

    private static String initTranslate(GenerateCodeDto generateCodeDto, String type) {
        StringBuilder content = new StringBuilder();
        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        if (tableColumn.containsKey(KEY_PRD_CODE)) {
            if (STR_0.equals(type)) {
                content.append("import com.hundsun.lcpt.ta.pub.fund.domain.bean.FundProduct;").append(STR_NEXT_LINE);
            } else if (STR_1.equals(type)) {
                content.append("        Map<String, FundProduct> productMap = new HashMap<>(16);").append(STR_NEXT_LINE);
                content.append("        FundProduct productInfo;").append(STR_NEXT_LINE);
            } else if (STR_2.equals(type)) {
                content.append("            productInfo = FundManageCacheUtil.getProduct(productMap, dataset.getString(\"prd_code\"));").append(STR_NEXT_LINE);
                content.append("            DatasetUtil.setValue(dataset, \"prd_code_name\", ManageUtil.append(dataset.getString(\"prd_code\"), productInfo.getPrdName()));").append(STR_NEXT_LINE);
            }
        }
        if (tableColumn.containsKey(KEY_SELLER_CODE)) {
            if (STR_0.equals(type)) {
                content.append("import com.hundsun.lcpt.ta.pub.fund.domain.bean.FundSellerInfo;").append(STR_NEXT_LINE);
            } else if (STR_1.equals(type)) {
                content.append("        Map<String, FundSellerInfo> sellerMap = new HashMap<>(16);").append(STR_NEXT_LINE);
                content.append("        FundSellerInfo sellerInfo;").append(STR_NEXT_LINE);
            } else if (STR_2.equals(type)) {
                content.append("            sellerInfo = FundManageCacheUtil.getSeller(sellerMap, dataset.getString(\"seller_code\"));").append(STR_NEXT_LINE);
                content.append("            DatasetUtil.setValue(dataset, \"seller_code_name\", ManageUtil.append(dataset.getString(\"seller_code\"), sellerInfo.getSellerName()));").append(STR_NEXT_LINE);
            }
        }
        if (tableColumn.containsKey(KEY_BRANCH_NO)) {
            if (STR_0.equals(type)) {
                content.append("import com.hundsun.lcpt.ta.pub.fund.domain.bean.FundNetInfo;").append(STR_NEXT_LINE);
            } else if (STR_1.equals(type)) {
                content.append("        Map<String, FundNetInfo> branchMap = new HashMap<>(16);").append(STR_NEXT_LINE);
                content.append("        FundNetInfo branchInfo;").append(STR_NEXT_LINE);
            } else if (STR_2.equals(type)) {
                content.append("            branchInfo = FundManageCacheUtil.getBranch(branchMap, dataset.getString(\"seller_code\"), dataset.getString(\"branch_no\"));").append(STR_NEXT_LINE);
                content.append("            DatasetUtil.setValue(dataset, \"branch_no_name\", ManageUtil.append(dataset.getString(\"branch_no\"), branchInfo.getBranchName()));").append(STR_NEXT_LINE);
            }
        }
        if (STR_2.equals(type) && (tableColumn.containsKey("clientType") || tableColumn.containsKey("idType"))) {
            content.append("            if (IFundConst.CNST_STR_0.equals(dataset.getString(\"client_type\"))) {").append(STR_NEXT_LINE);
            content.append("                DatasetUtil.setValue(dataset, \"id_type_name\", ManageUtil.getDictPrompt(dictMap, IFundDict.F_C20023, dataset.getString(\"id_type\")));").append(STR_NEXT_LINE);
            content.append("            } else if (IFundConst.CNST_STR_1.equals(dataset.getString(\"client_type\"))) {").append(STR_NEXT_LINE);
            content.append("                DatasetUtil.setValue(dataset, \"id_type_name\", ManageUtil.getDictPrompt(dictMap, IFundDict.F_C20022, dataset.getString(\"id_type\")));").append(STR_NEXT_LINE);
            content.append("            } else if (IFundConst.CNST_STR_2.equals(dataset.getString(\"client_type\"))) {").append(STR_NEXT_LINE);
            content.append("                DatasetUtil.setValue(dataset, \"id_type_name\", ManageUtil.getDictPrompt(dictMap, IFundDict.F_C20379, dataset.getString(\"id_type\")));").append(STR_NEXT_LINE);
            content.append("            }").append(STR_NEXT_LINE);
        }
        if (STR_0.equals(type) && StringUtils.isNotEmpty(content)) {
            content.append("import com.hundsun.lcpt.fund.util.FundManageCacheUtil;").append(STR_NEXT_LINE);
        }
        if (!STR_0.equals(type) && StringUtils.isNotEmpty(content)) {
            content.append(STR_NEXT_LINE);
        }
        return content.toString();
    }

    private static String initQueryColumn(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder("               ");
        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            ColumnInfoDto columnInfo = tableColumn.get(column);
            if (GenerateCommon.skipColumn(columnInfo)) {
                continue;
            }
            content.append("\"a." + columnInfo.getColumnUnderline() + ",\"\n                + ");
        }
        String queryColumn = content.toString();
        int indexEnd = queryColumn.lastIndexOf(",");
        if (indexEnd == -1) {
            return queryColumn;
        }
        return queryColumn.substring(0, indexEnd) + "\"";
    }

    private static String initCondition(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder();
        Map<String, ColumnInfoDto> tableColumn = generateCodeDto.getColumnMap();
        Iterator<String> iterator = tableColumn.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            ColumnInfoDto columnInfo = tableColumn.get(column);
            if (GenerateCommon.skipColumn(columnInfo) || !STR_1.equals(columnInfo.getColumnQuery())) {
                continue;
            }
            if (column.contains(KEY_PRD_CODE)) {
                content.append("        String prdCode = dto.get" + CommonUtils.initialUpper(column) + "();").append(STR_NEXT_LINE);
                content.append("        if (!DataUtil.isNullStr(prdCode)) {").append(STR_NEXT_LINE);
                content.append("            if (prdCode.contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(STR_NEXT_LINE);
                content.append("                FundCommonUtil.setHsSqlWhereInByList(hss, \"a.prd_code\", Arrays.asList(prdCode.split(IFundConst.CNST_PUNCTUATION_COMMA)));").append(STR_NEXT_LINE);
                content.append("            } else {").append(STR_NEXT_LINE);
                content.append("                hss.setWhere(\"a.prd_code\", prdCode);").append(STR_NEXT_LINE);
                content.append("            }").append(STR_NEXT_LINE);
                content.append("        }").append(STR_NEXT_LINE);
            } else if (column.contains(KEY_SELLER_CODE)) {
                content.append("        String sellerCode = dto.get" + CommonUtils.initialUpper(column) + "();").append(STR_NEXT_LINE);
                content.append("        if (!DataUtil.isNullStr(sellerCode)) {").append(STR_NEXT_LINE);
                content.append("            if (sellerCode.split(IFundConst.CNST_PUNCTUATION_COMMA).length > 1) {").append(STR_NEXT_LINE);
                content.append("                FundCommonUtil.setHsSqlWhereInByList(hss, \"a.seller_code\", Arrays.asList(sellerCode.split(IFundConst.CNST_PUNCTUATION_COMMA)));").append(STR_NEXT_LINE);
                content.append("            } else {").append(STR_NEXT_LINE);
                content.append("                hss.setWhere(\"a.seller_code\", sellerCode);").append(STR_NEXT_LINE);
                content.append("            }").append(STR_NEXT_LINE);
                content.append("        }").append(STR_NEXT_LINE);
            } else if (column.contains(KEY_BRANCH_NO)) {
                content.append("        String branchNo = dto.get" + CommonUtils.initialUpper(column) + "();").append(STR_NEXT_LINE);
                content.append("        if (!DataUtil.isNullStr(branchNo)) {").append(STR_NEXT_LINE);
                content.append("            if (branchNo.contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(STR_NEXT_LINE);
                content.append("                FundCommonUtil.setHsSqlWhereInByList(hss, \"a.branch_no\", Arrays.asList(branchNo.split(IFundConst.CNST_PUNCTUATION_COMMA)));").append(STR_NEXT_LINE);
                content.append("            } else {").append(STR_NEXT_LINE);
                content.append("                hss.setWhere(\"a.branch_no\", branchNo);").append(STR_NEXT_LINE);
                content.append("            }").append(STR_NEXT_LINE);
                content.append("        }").append(STR_NEXT_LINE);
            } else {
                String columnType = columnInfo.getColumnType();
                String columnUnderline = columnInfo.getColumnUnderline();
                if (StringUtils.isNotEmpty(columnInfo.getColumnDict())) {
                    content.append("        String " + column + " = dto.get" + CommonUtils.initialUpper(column) + "();").append(STR_NEXT_LINE);
                    content.append("        if (!DataUtil.isNullStr(" + column + ")) {").append(STR_NEXT_LINE);
                    content.append("            if (" + column + ".contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(STR_NEXT_LINE);
                    content.append("                FundCommonUtil.setHsSqlWhereInByList(hss, \"a." + columnUnderline + "\", Arrays.asList(" + column + ".split(IFundConst.CNST_PUNCTUATION_COMMA)));").append(STR_NEXT_LINE);
                    content.append("            } else {").append(STR_NEXT_LINE);
                    content.append("                hss.setWhere(\"a." + columnUnderline + "\", " + column + ");").append(STR_NEXT_LINE);
                    content.append("            }").append(STR_NEXT_LINE);
                    content.append("        }").append(STR_NEXT_LINE);
                } else if (KEY_COLUMN_TYPE_DATE.equals(columnType)) {
                    content.append("        String " + column + " = dto.get" + CommonUtils.initialUpper(column) + "();").append(STR_NEXT_LINE);
                    content.append("        if (!DataUtil.isNullStr(" + column + ") && " + column + ".contains(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(STR_NEXT_LINE);
                    content.append("            String[] " + column + "Split = " + column + ".split(IFundConst.CNST_PUNCTUATION_COMMA);").append(STR_NEXT_LINE);
                    content.append("            hss.setWhere(\"a." + columnUnderline + "\", \" >= \", " + column + "Split[0]);").append(STR_NEXT_LINE);
                    content.append("            hss.setWhere(\"a." + columnUnderline + "\", \" <= \", " + column + "Split[1]);").append(STR_NEXT_LINE);
                    content.append("        }").append(STR_NEXT_LINE);
                } else if (KEY_COLUMN_TYPE_INTEGER.equals(columnType) || KEY_COLUMN_TYPE_NUMBER.equals(columnType)) {
                    content.append("        String " + column + " = dto.get" + CommonUtils.initialUpper(column) + "();").append(STR_NEXT_LINE);
                    content.append("        if (!DataUtil.isNullStr(" + column + ")) {").append(STR_NEXT_LINE);
                    content.append("            String[] " + column + "Arr = " + column + ".split(IFundConst.CNST_PUNCTUATION_COMMA);").append(STR_NEXT_LINE);
                    content.append("            if (" + column + "Arr.length >= 1) {").append(STR_NEXT_LINE);
                    content.append("                if (" + column + ".startsWith(IFundConst.CNST_PUNCTUATION_COMMA)) {").append(STR_NEXT_LINE);
                    content.append("                    hss.setWhere(\"a." + columnUnderline + "\", \" <= \", " + column + "Arr[1]);").append(STR_NEXT_LINE);
                    content.append("                } else {").append(STR_NEXT_LINE);
                    content.append("                    hss.setWhere(\"a." + columnUnderline + "\", \" >= \", " + column + "Arr[0]);").append(STR_NEXT_LINE);
                    content.append("                }").append(STR_NEXT_LINE);
                    content.append("            }").append(STR_NEXT_LINE);
                    content.append("            if (" + column + "Arr.length == 2) {").append(STR_NEXT_LINE);
                    content.append("                hss.setWhere(\"a." + columnUnderline + "\", \" <= \", " + column + "Arr[1]);").append(STR_NEXT_LINE);
                    content.append("            }").append(STR_NEXT_LINE);
                    content.append("        }").append(STR_NEXT_LINE);
                } else {
                    content.append("        if (!DataUtil.isNullStr(dto.get" + CommonUtils.initialUpper(column) + "())) {").append(STR_NEXT_LINE);
                    content.append("             hss.setWhere(\"a." + columnUnderline + "\", dto.get" + CommonUtils.initialUpper(column) + "());").append(STR_NEXT_LINE);
                    content.append("        }").append(STR_NEXT_LINE);
                }
            }
        }
        return content.toString();
    }

    private static String convertImportColumn(Map<String, ColumnInfoDto> columnInfo) {
        StringBuilder content = new StringBuilder();
        Iterator<String> iterator = columnInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfo.get(columnCode);
            if (GenerateCommon.skipColumn(columnInfoDto)) {
                continue;
            }
            if (columnInfoDto.getColumnName().contains(STR_PERCENT)) {
                content.append("                impDto.set" + CommonUtils.initialUpper(columnCode) + "(new BigDecimal(impDto.get" + CommonUtils.initialUpper(columnCode) + "()).divide(new BigDecimal(\"100\")).toString());").append(STR_NEXT_LINE);
            }
        }
        if (StringUtils.isNotEmpty(content.toString())){
            return "                // 数据百分比处理\n" + content.toString();
        }
        return content.toString();
    }

    private static String getRepeatTips(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder(STR_SPACE);
        Iterator<String> iterator = generateCodeDto.getPrimaryKeyMap().keySet().iterator();
        Map<String, ColumnInfoDto> columnInfoDtoMap = generateCodeDto.getColumnMap();
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfoDtoMap.get(CommonUtils.lineToHump(columnCode));
            if (GenerateCommon.skipColumn(columnInfoDto)) {
                continue;
            }
            content.append("【" + columnInfoDto.getColumnName() + "】值【\" + dto.get" + CommonUtils.initialUpper(columnInfoDto.getColumnCode()) + "() + \"】，");
        }
        int index = content.lastIndexOf("，");
        if (index != -1) {
            return content.substring(0, index) + "\"";
        }
        return content.toString().trim();
    }

    private static String getCheckHasAddAsyColumn(GenerateCodeDto generateCodeDto, String type) {
        StringBuilder content = new StringBuilder();
        Map<String, ColumnInfoDto> columnInfoDtoMap = generateCodeDto.getColumnMap();
        Iterator<String> iterator = columnInfoDtoMap.keySet().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            String columnCode = iterator.next();
            ColumnInfoDto columnInfoDto = columnInfoDtoMap.get(columnCode);
            if (GenerateCommon.skipColumn(columnInfoDto)) {
                continue;
            }
            if (STR_1.equals(type)) {
                if (index != 0) {
                    content.append("and ");
                }
                content.append(columnInfoDto.getColumnUnderline() + " = ? ");
            } else {
                content.append("dto.get" + CommonUtils.initialUpper(columnCode) + "(), ");
            }
            index++;
        }
        if (STR_1.equals(type)) {
            return "\"" + content.toString() + "\" + ";
        }
        return content.toString();
    }

    private static String buildCheckHasAdd(GenerateCodeDto generateCodeDto) {
        StringBuilder content = new StringBuilder();
        Map<String, String> columnInfoDtoMap = generateCodeDto.getAsyKeyMap();
        Iterator<String> iterator = columnInfoDtoMap.keySet().iterator();
        if (MapUtils.isNotEmpty(columnInfoDtoMap)) {
            int index = 0;
            content.append("                if (");
            while (iterator.hasNext()) {
                String oriColumnCode = iterator.next();
                String columnCode = columnInfoDtoMap.get(oriColumnCode);
                if (index !=0) {
                    content.append("\n                        || ");
                }
                content.append("!StringUtils.equals(dto.get" + CommonUtils.initialUpper(oriColumnCode) + "(), dto.get" + CommonUtils.initialUpper(columnCode) + "())");
                index++;
            }
            content.append("){").append(STR_NEXT_LINE);
            content.append("                   checkHasAdd(dto);").append(STR_NEXT_LINE);
            content.append("                }").append(STR_NEXT_LINE);
        }
        return content.toString();
    }
}
