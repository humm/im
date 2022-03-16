package com.hoomoomoo.im.dao;

import com.hoomoomoo.im.model.SysSystemQueryModel;
import com.hoomoomoo.im.model.SysTableModel;
import com.hoomoomoo.im.model.SysTableQueryModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author hoomoomoo
 * @description 系统级别公用Dao
 * @package com.hoomoomoo.im.dao
 * @date 2019/08/08
 */

@Mapper
public interface SysSystemDao {

    /**
     * 加载业务ID信息
     *
     * @return
     */
    List<String> loadBusinessId();

    /**
     * 查询页面按钮权限
     *
     * @param sysSystemQueryModel
     * @return
     */
    Boolean selectButtonAuthority(SysSystemQueryModel sysSystemQueryModel);

    /**
     * 查询数据权限
     *
     * @param sysSystemQueryModel
     * @return
     */
    Boolean selectDataAuthority(SysSystemQueryModel sysSystemQueryModel);

    /**
     * 查询系统当前存在表
     *
     * @param sysTableQueryModel
     * @return
     */
    int selectTableNum(SysTableQueryModel sysTableQueryModel);

    /**
     * 查询表字段信息
     *
     * @param sysTableQueryModel
     * @return
     */
    List<SysTableModel> selectTableColumn(SysTableQueryModel sysTableQueryModel);

    /**
     * 查询表主键信息
     *
     * @param sysTableQueryModel
     * @return
     */
    List<SysTableModel> selectTablePrimaryKey(SysTableQueryModel sysTableQueryModel);

    /**
     * 查询表数据
     *
     * @param sysTableQueryModel
     * @return
     */
    List<LinkedHashMap> selectTableData(SysTableQueryModel sysTableQueryModel);

    /**
     * 查询表数据总数
     *
     * @param sysTableQueryModel
     * @return
     */
    SysTableModel selectTableCount(SysTableQueryModel sysTableQueryModel);

    /**
     * 查询表数据
     *
     * @param sysTableQueryModel
     * @return
     */
    List<SysTableModel> selectTableColumnComments(SysTableQueryModel sysTableQueryModel);

}
