package im.config.bean;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hoomoomoo
 * @description 公用参数配置类
 * @package im.config.bean
 * @date 2019/08/29
 */

@Component
@Data
public class SystemConfigBean {

    /**
     * freemarker 编码
     */
    @Value("${spring.freemarker.charset}")
    private String freemarkerCharset;

    /**
     * freemarker 模板路径
     */
    @Value("${spring.freemarker.template-loader-path}")
    private String freemarkerTemplateLoaderPath;

    /**
     * 应用名称
     */
    @Value("${server.servlet.context-path}")
    private String appName;

    /**
     * 应用名称
     */
    @Value("${server.servlet.context-describe}")
    private String appDescribe;

    /**
     * 帮助信息
     */
    @Value("${app.help}")
    private String appHelp;
}
