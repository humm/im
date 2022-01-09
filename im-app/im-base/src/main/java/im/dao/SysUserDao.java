package im.dao;

import im.model.SysUserModel;
import im.model.SysUserQueryModel;
import im.model.SysUserRoleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hoomoomoo
 * @description 用户信息dao
 * @package im.dao
 * @date 2019/08/11
 */

@Mapper
public interface SysUserDao {

    /**
     * 查询用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    List<SysUserModel> selectSysUser(SysUserQueryModel sysUserQueryModel);

    /**
     * 分页查询用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    List<SysUserModel> selectPage(SysUserQueryModel sysUserQueryModel);
    
    /**
     * 删除用户信息
     *
     * @param sysUserModelList
     * @return
     */
    void delete(List<SysUserModel> sysUserModelList);

    /**
     * 重置用户密码
     *
     * @param sysUserModelList
     * @param userPassword
     * @return
     */
    void reset(@Param("userPassword") String userPassword, @Param("list") List<SysUserModel> sysUserModelList);

    /**
     * 查询用户信息
     *
     * @param sysUserQueryModel
     * @return
     */
    SysUserModel selectOne(SysUserQueryModel sysUserQueryModel);

    /**
     * 保存用户信息
     *
     * @param sysUserModel
     */
    void save(SysUserModel sysUserModel);


    /**
     * 校验userCode是否存在
     *
     * @param sysUserQueryModel
     * @return
     */
    Boolean checkUserCode(SysUserQueryModel sysUserQueryModel);

    /**
     * 删除用户角色信息
     *
     * @param sysUserModel
     */
    void deleteUserRole(SysUserModel sysUserModel);

    /**
     * 保存用户角色信息
     *
     * @param sysUserRoleModel
     */
    void saveUserRole(SysUserRoleModel sysUserRoleModel);

    /**
     * 查询用户角色信息
     *
     * @param sysUserQueryModel
     */
    List<SysUserRoleModel> selectUserRole(SysUserQueryModel sysUserQueryModel);

    /**
     * 修改用户密码
     *
     * @param sysUserModel
     */
    void changPassword(SysUserModel sysUserModel);


}
