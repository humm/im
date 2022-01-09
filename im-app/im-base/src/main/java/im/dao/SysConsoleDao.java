package im.dao;

import im.model.SysConsoleQueryModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hoomoomoo
 * @description 首页数据Dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/10/27
 */

@Mapper
public interface SysConsoleDao {

    /**
     * 查询最后一笔收入
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectIncomeLast(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询月度收入
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectIncomeMonth(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询上年本月度收入
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectIncomePreviousYearMonth(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询本年上月度收入
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectIncomePreviousMonth(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询年度收入
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectIncomeYear(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询总收入
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectIncomeTotal(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询最后一笔送礼
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectGiftSendLast(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询年度送礼
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectGiftSendYear(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询总送礼
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectGiftSendTotal(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询最后一笔收礼
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectGiftReceiveLast(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询年度收礼
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectGiftReceiveYear(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询总收礼
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectGiftReceiveTotal(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询最后一次登入日志
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectLoginLast(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询本月登入次数
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectLoginMonthTime(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询本年登入次数
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectLoginYearTime(SysConsoleQueryModel sysConsoleQueryModel);

    /**
     * 查询总登入次数
     *
     * @param sysConsoleQueryModel
     * @return
     */
    String selectLoginTotalTime(SysConsoleQueryModel sysConsoleQueryModel);

}
