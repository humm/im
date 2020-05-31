package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysReportModel;
import com.hoomoomoo.im.model.SysReportQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 报表信息dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/09/08
 */

@Mapper
public interface SysReportDao {

    /**
     * 收入信息年度报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectIncomeYear(SysReportQueryModel sysReportQueryModel);

    /**
     * 收入信息月度报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectIncomeMonth(SysReportQueryModel sysReportQueryModel);

    /**
     * 收入信息来源报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectIncomeSource(SysReportQueryModel sysReportQueryModel);

    /**
     * 收入信息类型报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectIncomeType(SysReportQueryModel sysReportQueryModel);

    /**
     * 收入信息极值报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectIncomePeak(SysReportQueryModel sysReportQueryModel);

    /**
     * 随礼信息年度报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectGiftYear(SysReportQueryModel sysReportQueryModel);

    /**
     * 随礼信息月度报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectGiftMonth(SysReportQueryModel sysReportQueryModel);

    /**
     * 随礼信息来源报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectGiftSource(SysReportQueryModel sysReportQueryModel);

    /**
     * 随礼信息类型报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectGiftType(SysReportQueryModel sysReportQueryModel);

    /**
     * 随礼信息极值报表
     *
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectGiftPeak(SysReportQueryModel sysReportQueryModel);

    /**
     * 随礼信息报表
     * @param sysReportQueryModel
     * @return
     */
    List<SysReportModel> selectGift(SysReportQueryModel sysReportQueryModel);


}
