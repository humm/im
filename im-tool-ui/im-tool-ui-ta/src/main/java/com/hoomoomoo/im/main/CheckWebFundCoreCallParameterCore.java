package com.hoomoomoo.im.main;


import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.STR_BLANK;
import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;


public class CheckWebFundCoreCallParameterCore {

    private static int index;
    private static List<String> special = new ArrayList<>();
    private static Map<String, Set<String>> methodAll = new LinkedHashMap<>();
    private static String[] trimRightMark = new String[]{
            "(Integer.parseInt(delDto.getCfmDate",
            "(dto.getServiceName",
            "(currentItem.",
            "(data.getPrdCode",
            "(dto.getPrdCode",
            "(dataset.getString",
            "(SysArgCache.",
            "(item.getBusinParam",
            "(FundCommonUtil"
    };
    private static String[] trimLeftMark = new String[]{
            "Integer.parseInt(",
            "dto.setKeyCode(",
            "addDto.setKeyCode(",
            "impDto.setKeyCode(",
            "delDto.setKeyCode(",
            "dto.setKeyCode(",
            "dto.setCfmBegDate(String.valueOf(",
            "dto.setCfmEndDate(String.valueOf(",
            "dto.setCfmCloseDate(String.valueOf(",
            "impDto.setSerialNo(",
            "paramList.add("
    };
    private static Set<String> exclude = new HashSet<>(Arrays.asList(
            "FundReportExpDTO::new)",
            "FundReportServiceFactory",
            "FundReportServiceFactory",
            "IFundReportService",
            "FundPubServiceFactory.getSystemPublicService",
            "SpringContextFactory.getApplicationContext().getBean",
            "FundPubServiceFactory.getFundManualLiquiService",
            "FundPubServiceFactory.getProcessNodeService",
            "FundPubServiceFactory.getBaseDuplicateService",
            "SequenceCache.getInstance(FundPubSequenceService.CFM_SERIAL).getSequenceNo",
            ".getFundReportService"
    ));

    private static Set<String> excludeMethod = new HashSet<>(Arrays.asList(
            "chgIdType",
            "getCommonNo",
            "getMaxBatchNo",
            "getTaCfmNo",
            "isWorkDay",
            "getWorkDate",
            "getWorkDayByOffset",
            "unitePrdNavInfoQuery",
            "setNodeFinished",
            "setScheTriggerEnable",
            "checkJzbfExport",
            "checkKfExport",
            "checkAgencyCfmExport",
            "checkLiqTrustee",
            "checkAgencyLiqMark",
            "addFundMultiDealLog",
            "getYearDays",
            "existsLiqFlow",
            "getRelationPrdList",
            "selectRelateDelPrdList",
            "getLiqDealBatchNo",
            "setQueryCondition",
            "deleteFromTable",
            "setLiquCompletePrdCondition",
            "checkExistsDealingSchedule",
            "selectAllSellerCompleteImp",
            "pushData2QueryDB",
            "OpenStopscheduletrigger",
            "deleteTableCondition",
            "confirmFunddayCheckAction",
            "syncTradeBaseDataToQuery",
            "generateServiceOfAuditHints",
            "getBusinParamNo",
            "andInListConditionStringVaule",
            "addNetInfo",
            "prepareRptSql",
            "getFundReportSqlTmp",
            "getDefaultReportParam",
            "getCheckSql4Show",
            "getLastDeductBusinFlag",
            "getRegisterDate",
            "getLastProvisionWay",
            "refreshCache",
            "getProduct",
            "getDagRunId",
            "checkRollBackBusinParam",
            "checkDealingDataOutput"
    ));

    private static Map<String, Map<String, String>> methodDesc = new LinkedHashMap<String, Map<String, String>>(){{
        put("FundProductInfoSetController.java", new HashMap<String, String>(){{
            put("systemPublicService.getOutLandWorkDayByOffset", "新版ued系统日初始化校验是否存在待复核数据");
        }});
        put("FundBatchD7NavFileImportService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getFrequencyNavService().batchImpServiceDealFundDay", "批量D7净值文件导入  无调用");
        }});
        put("FundBatchNavFileImportService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getFrequencyNavService().batchImpServiceDealFundDay", "批量净值文件导入  前台单独菜单  接口导入");
        }});
        put("FundCustomDataExportService.java", new HashMap<String, String>(){{
            put("fundManualLiquiService.expAllManagerData", "清算流程  客服数据导出");
        }});
        put("FundFunddayCheckService.java", new HashMap<String, String>(){{
            put("fundManualLiquiService.initFunddayService", "清算流程  行情复核打开页面");
        }});
        put("FundFunddayInputService.java", new HashMap<String, String>(){{
            put("fundInitDateService.fundStatCreate", "清算流程  行情导入打开页面 【未开通 开通当天新增的产品不参与当天清算】前提下");
            put("fundManualLiquiService.saveFundday", "清算流程  行情导入  保存按钮");
            put("fundManualLiquiService.updateFlowLogAndPublishFrequency", "清算流程  行情导入  保存按钮");
        }});
        put("FundNavReBateService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getFrequencyNavService().batchImpServiceDealFundDay", "净值返账页面  同步按钮");
        }});
        put("FundNavSynchroniseService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getFrequencyNavService().batchImpServiceDealFundDay", "净值同步页面  同步按钮");
        }});
        put("FundPrdNavExcelImportService.java", new HashMap<String, String>(){{
            put("frequencyNavService.updateNavByExcel", "批量净值Excel导入  导入按钮");
        }});
        put("FundSpecialPrdNavStatExpService.java", new HashMap<String, String>(){{
            put("fundInitDateService.fundStatCreate", "特殊行情信息导出 特殊行情生成按钮");
        }});
        put("FundUniProfitRulePreviewAllService.java", new HashMap<String, String>(){{
            put("fundInitDateService.createUniProfitFactDatePreView", "统一业绩提成规则设置  全部预览按钮");
        }});
        put("FundUniProfitRulePreviewSelectService.java", new HashMap<String, String>(){{
            put("fundInitDateService.createUniProfitFactDatePreView", "统一业绩提成规则设置  预览选中行按钮");
        }});
        put("FundFundOpendayListSetService.java", new HashMap<String, String>(){{
            put("fundInitDateService.fundStatCreate", "专户开放周期设置  新增  修改  删除  导入 按钮");
        }});
        put("FundFundOpenDayRuleService.java", new HashMap<String, String>(){{
            put("fundInitDateService.generateFundStatusDayTmp", "专户开放周期设置-按规则  生成产品开放日日历按钮");
            put("fundInitDateService.fundAllOpenDaysConCat", "专户开放周期设置-按规则  生成产品开放日日历按钮  提示弹窗【生成产品开放日完成，是否合并连续开放日】");
        }});
        put("FundFundStatusPreviewService.java", new HashMap<String, String>(){{
            put("fundInitDateService.createFundStatusPreview", "基金状态预览  预览按钮");
        }});
        put("FundOpenFrequencySetCheckService.java", new HashMap<String, String>(){{
            put("fundInitDateService.createByFundOpenFrequencyAudit", "专户产品开放日频率控制方案设置-开放日频率稽核");
        }});
        put("FundProductInfoSetService.java", new HashMap<String, String>(){{
            put("syncSysinfoService.syncProductDateToProduct", "基金信息设置  同步数据到到正式表【中信特有功能】");
        }});
        put("FundRulePreviewAllService.java", new HashMap<String, String>(){{
            put("fundInitDateService.generateFundStatusDayTmp", "专户开放周期设置-按规则  全部预览按钮");
        }});
        put("FundRulePreviewSelectService.java", new HashMap<String, String>(){{
            put("fundInitDateService.generateFundStatusDayTmp", "专户开放周期设置-按规则  预览选中行按钮");
            put("fundInitDateService.generateFundStatusDayTmpNew", "专户开放周期设置-按规则  预览历史已有按钮");
            put("fundInitDateService.genSpecialFundStatusAllYear", "专户开放周期设置-按规则  生成全年开放周期按钮");
        }});
        put("FundZhappointRulePreAllService.java", new HashMap<String, String>(){{
            put("fundAppointPreViewService.createFundAppointPreView", "专户产品预约期设置  全部预览按钮");
        }});
        put("FundZhappointRulePreSelectService.java", new HashMap<String, String>(){{
            put("fundAppointPreViewService.createFundAppointPreView", "专户产品预约期设置  预览选中行按钮");
        }});
        put("FundForceRedeemService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getMemoryCacheService().getFundDelayCfmInfo", "强制赎回  发起赎回");
        }});
        put("FundApiRealtimeService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getFundProfitProjService().getRealTimeVirtualProfit", "实时接口  虚拟计提");
        }});
        put("FundAutoLiqApiServiceImpl.java", new HashMap<String, String>(){{
            put("fundRealTimeReqService.dbNotInBackUpOrRestore", "实时接口  启动清算  校验数据库备份恢复状态");
            put("fundAutoLiqService.insertFundPrdListTmp", "实时接口  启动清算");
            put("fundAutoLiqService.selectDelPrdList", "实时接口  启动清算");
            put("fundAutoLiqService.selectRealLiqPrdCount", "实时接口  启动清算");
            put("fundAutoLiqService.insertFundRollBackPrdList", "实时接口  回退");
            put("fundAutoLiqService.deleteFundRollData2Request", "实时接口  回退");
            put("fundAutoLiqService.insertFundRollData2Request", "实时接口  回退");
        }});
        put("FundReqRealtimeRequestService.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getMemoryCacheService().getFundDelayCfmInfo", "交易申请实时申请查询  审核按钮");
        }});
        put("CCQueryKJQSFileController.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getSystemPublicService().andInListConditionStringVaule", "");
        }});
        put("FAQueryKJQSFileController.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getSystemPublicService().andInListConditionStringVaule", "");
        }});
        put("PrdSellerParamInfoController.java", new HashMap<String, String>(){{
            put("FundPubServiceFactory.getFundArLimitService().getPrdcodeArLimitInfo", "查询个户交易限制信息");
            put("FundPubServiceFactory.getExportNavService().getFundPrdStatusChgStatusThesame07file", "按07文件导出 逻辑返回 基金状态/基金转换状态");
            put("FundPubServiceFactory.getFundArLimitService().getNextTradeDayByPrdAndSerller", "根据产品代码销售商代码下一开放日");
            put("FundPubServiceFactory.getFundProductService().getRdmBelongAssetbyPrdcode", "获取赎回归基金资产比例");
            put("FundPubServiceFactory.getExportNavService().getFundFeeRateExpInfo", "按C5文件导出 逻辑返回 费率信息");
            put("FundPubServiceFactory.getExportNavService().getFundPrdChgRelationExpInfo", "按C3文件导出 逻辑返回 基金转换信息");
        }});
    }};


    public static void main(String[] args) throws IOException {
        String checkPath = "E:\\workspace\\ta6\\server\\ta-web-manager-fund-core\\src";
        String resPath = "C:\\Users\\hspcadmin\\Desktop\\checkWebFundCoreCallParameterCore.sql";
        System.out.println();
        System.out.println("开始检查 ...");
        System.out.println("检查路径 ... " + checkPath);
        doCheck(checkPath, resPath);
        System.out.println();
        System.out.println("完成检查 ...");
        System.out.println("结果路径 ... " + resPath);
        System.out.println();
    }

    private static void doCheck(String checkPath, String resPath) throws IOException {
        check(new File(checkPath));
        int totalNum = 0;
        int descNum = 0;
        List<String> res = new ArrayList<>();
        res.add("-- 前台代码直接调用后台代码方法(待确认影响范围) ");
        StringBuilder desc = new StringBuilder("    private static Map<String, Map<String, String>> methodDesc = new LinkedHashMap<String, Map<String, String>>(){{\n");
        for (Map.Entry<String, Set<String>> entry : methodAll.entrySet()) {
            String key = entry.getKey();
            Set<String> method = entry.getValue();
            Iterator<String> iterator = method.iterator();
            while (iterator.hasNext()) {
                String methodName = iterator.next();
                for (String ele : excludeMethod) {
                    if (methodName.contains(ele)) {
                        iterator.remove();
                        continue;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(method)) {
                res.add(key);
                Map<String, String> methodDescEle = methodDesc.get(key);
                desc.append("        put(\"" + key + "\", new HashMap<String, String>(){{\n");
                for (String ele : method) {
                    totalNum++;
                    desc.append("            put(\"" + ele + "\", \"\");\n");
                    if (methodDescEle.containsKey(ele)) {
                        res.add("     -- " + methodDescEle.get(ele));
                    } else {
                        descNum++;
                        res.add("-- 请补充方法说明");
                    }
                    res.add("     " + ele);
                }
                desc.append("        }});\n");
            }
        }
        desc.append("    }};\n");

        res.add(STR_BLANK);
        res.add("-- 前台代码直接调用后台代码方法(默认不影响 需再次确认) ");
        for (String ele : excludeMethod) {
            res.add("     " + ele);
        }
        res.add(STR_BLANK);
        res.addAll(special);
        res.add(0, "-- 方法总数:" + totalNum + "  补充方法说明:" + descNum + "  特殊场景:" + special.size() + STR_NEXT_LINE);
        FileUtils.writeFile(resPath, res, false);
    }

    private static void check(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                check(item);
            }
        } else {
            String fileName = file.getName();
            if (fileName.endsWith(".java")) {
                index++;
                if (index % 100 == 0) {
                    System.out.print(".");
                }
                String content = FileUtils.readNormalFileToString(file.getPath(), false);
                List<String> contents = Arrays.asList(content.split(";"));
                if (CollectionUtils.isNotEmpty(contents)) {
                    Set<String> markService = new HashSet<>();
                    Set<String> method = new LinkedHashSet<>();
                    for (String ele : contents) {
                        ele = CommonUtils.formatStrToSingleSpace(ele);
                        if (ele.contains("import") && ele.contains("com.hundsun.ta.pub.fund.services.")) {
                            String serviceName = ele.substring(ele.lastIndexOf(".") + 1);
                            markService.add(serviceName);
                            if (serviceName.startsWith("I")) {
                                markService.add(serviceName.substring(1, 2).toLowerCase() + serviceName.substring(2));
                            }
                        } else if (ele.contains("@Autowired")) {
                            String eleTmp = ele;
                            if (eleTmp.contains("{")) {
                                eleTmp = eleTmp.substring(eleTmp.indexOf("{") + 1).trim();
                            }
                            boolean mark = false;
                            for (String service : markService) {
                                if (eleTmp.contains(service)) {
                                    mark = true;
                                    break;
                                }
                            }
                            if (mark) {
                                String[] elements = eleTmp.split(" ");
                                if (elements.length >= 3) {
                                    markService.add(elements[2]);
                                } else {
                                    special.add("-- 特殊场景请检查: " + fileName + " " + ele);
                                }
                            }
                        } else {
                            if (CollectionUtils.isNotEmpty(markService)) {
                                for (String service : markService) {
                                    if (ele.contains(service)) {
                                        if (!methodAll.containsKey(fileName)) {
                                            methodAll.put(fileName, method);
                                        }
                                        String[] eleTmp = ele.replaceAll("\\) \\.", ").").replaceAll("!", "").split(" ");
                                        for (String tmp : eleTmp) {
                                            if (tmp.contains(service)) {
                                                if (tmp.startsWith("(")) {
                                                    tmp = tmp.substring(1);
                                                }
                                                if (tmp.contains("(")) {
                                                    tmp = tmp.substring(0, tmp.lastIndexOf("("));
                                                }
                                                for (String flag : trimRightMark) {
                                                    int index = tmp.indexOf(flag);
                                                    if (index != -1) {
                                                        tmp = tmp.substring(0, index);
                                                        break;
                                                    }
                                                }
                                                for (String flag : trimLeftMark) {
                                                    int index = tmp.indexOf(flag);
                                                    if (index != -1) {
                                                        tmp = tmp.substring(index + flag.length());
                                                        break;
                                                    }
                                                }
                                                if (!tmp.equals(service) && !exclude.contains(tmp)) {
                                                    method.add(tmp);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
