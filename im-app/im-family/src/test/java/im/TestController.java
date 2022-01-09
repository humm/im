package im;

import im.model.SysGiftModel;
import im.model.SysGiftQueryModel;
import im.model.SysMenuTreeModel;
import im.model.base.SessionBean;
import im.service.SysMenuService;
import im.service.SysSqlService;
import im.util.SysSessionUtils;
import im.util.SysCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static im.config.RunDataConfig.CONFIG_SQL;

/**
 * @author hoomoomoo
 * @description 测试控制类
 * @package im.test.controller
 * @date 2019/08/08
 */

@RequestMapping("/test/")
@RestController
public class TestController {

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysSqlService sysSqlService;

    @ApiOperation("Session信息")
    @RequestMapping(value = "sessionBean", method = RequestMethod.POST)
    public SessionBean testSessionBean() {
        return SysSessionUtils.getSession();
    }

    @ApiOperation("菜单信息")
    @RequestMapping(value = "menu", method = RequestMethod.GET)
    public List<SysMenuTreeModel> selectMenuTree(String disabled, String roleId){
        return sysMenuService.selectMenuTree(disabled, roleId);
    }

    @ApiOperation("配置sql查询")
    @RequestMapping(value = "selectPage", method = RequestMethod.GET)
    public List selectPage(SysGiftQueryModel sysGiftQueryModel){
        List<SysGiftModel> list = sysSqlService.execute(CONFIG_SQL.get("sysGift-selectPage"), sysGiftQueryModel);
        return list;
    }

    @ApiOperation("配置sql删除")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public void delete(){
        List<SysGiftModel> list = new ArrayList<>();
        SysGiftModel sysGiftModel = new SysGiftModel();
        sysGiftModel.setGiftId("156");
        list.add(sysGiftModel);
        sysSqlService.execute(CONFIG_SQL.get("sysGift-delete"), list);
    }

    @ApiOperation("配置sql新增")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void add(SysGiftModel sysGiftModel){
        SysCommonUtils.setCreateUserInfo(sysGiftModel);
        sysSqlService.execute(CONFIG_SQL.get("sysGift-add"), sysGiftModel);
    }

    @ApiOperation("配置sql修改")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(SysGiftModel sysGiftModel){
        SysCommonUtils.setCreateUserInfo(sysGiftModel);
        sysSqlService.execute(CONFIG_SQL.get("sysGift-update"), sysGiftModel);
    }
}
