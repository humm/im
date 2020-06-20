package com.hoomoomoo.im.config.bean;

import com.hoomoomoo.im.consts.BaseConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import static com.hoomoomoo.im.consts.BaseConst.APPLICATION_PROPERTIES;
import static com.hoomoomoo.im.consts.BaseConst.CONFIG;

/**
 * @author hoomoomoo
 * @description 系统参数配置
 * @package com.hoomoomoo.im.config.bean
 * @date 2019/08/03
 */

@Component
@PropertySource({BaseConst.APPLICATION_PROPERTIES})
@ConfigurationProperties(prefix = BaseConst.CONFIG)
@Data
public class ConfigBean {

    /**
     * 应用启动打印配置参数转换为*输出key
     */
    private String convertOutputKeyword;

    /**
     * 应用启动打印配置参数忽略key
     */
    private String ignoreOutputKeyword;

    /**
     * 是否开启Swagger
     */
    private Boolean swagger;

    /**
     * Swagger配置扫描路径
     */
    private String swaggerLocation;

    /**
     * 系统初始化模式
     */
    private String initMode;

    /**
     * 超级模式
     */
    private String superMode;

}
