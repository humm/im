package com.hoomoomoo.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;


/**
 * @author hoomoomoo
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2019/08/03
 */
@SpringBootApplication
@ServletComponentScan
@PropertySource("classpath:/dubbo.properties")
public class WebStarter {

    public static void main(String[] args) {
        SpringApplication.run(WebStarter.class, args);
    }

}
