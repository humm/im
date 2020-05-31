package com.hoomoomoo.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.hoomoomoo.im.consts.BusinessConst.STR_1;

/**
 * @author hoomoomoo
 * @description 模块配置实体类
 * @package com.hoomoomoo.im.model
 * @date 2019/11/02
 */

@Data
public class SysModuleModel extends BaseModel {

    /** 首页模块 开始 */

    @ApiModelProperty(value="用户信息模块", required = false)
    private String user;

    @ApiModelProperty(value="提示信息模块", required = false)
    private String tips;

    @ApiModelProperty(value="登入信息模块", required = false)
    private String login;

    @ApiModelProperty(value="版本信息模块", required = false)
    private String version;

    @ApiModelProperty(value="注册信息模块", required = false)
    private String register;

    /** 首页模块 结束 */

    public SysModuleModel() {
        this.user = STR_1;
        this.tips = STR_1;
        this.login = STR_1;
        this.version = STR_1;
        this.register = STR_1;
    }
}
