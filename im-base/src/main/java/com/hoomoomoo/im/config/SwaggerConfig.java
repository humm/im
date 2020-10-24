package com.hoomoomoo.im.config;

import com.hoomoomoo.im.config.bean.ConfigBean;
import com.hoomoomoo.im.consts.BaseCueConst;
import com.hoomoomoo.im.util.SysLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author hoomoomoo
 * @description Swagger配置
 * @package com.hoomoomoo.im.config
 * @date 2019/08/08
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Autowired
    private ConfigBean configBean;

    @Bean
    public Docket init() {
        SysLogUtils.load(logger, BaseCueConst.LOG_BUSINESS_TYPE_SWAGGER);
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(configBean.getSwagger())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(configBean.getSwaggerLocation()))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(BaseCueConst.SWAGGER_TITLE)
                .description(BaseCueConst.SWAGGER_DESCRIPTION)
                .version(BaseCueConst.SWAGGER_VERSION)
                .build();
    }
}
