package com.hoomoomoo.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author hoomoomoo
 * @description 应用启动类
 * @package com.hoomoomoo.im
 * @date 2020/10/24
 */
@SpringBootApplication
@ServletComponentScan
@EnableSwagger2
public class LearnStarter {

    public static void main(String[] args) {
        SpringApplication.run(LearnStarter.class, args);
    }

}
