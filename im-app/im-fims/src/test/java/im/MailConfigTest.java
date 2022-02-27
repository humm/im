package im;

import im.service.SysInterfaceService;
import im.service.SysMailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author hoomoomoo
 * @description 邮件测试
 * @package com.hoomoomoo.im.test
 * @date 2019/08/03
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FimsStarter.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MailConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(MailConfigTest.class);

    @Autowired
    private SysMailService sysMailService;

    @Autowired
    private SysInterfaceService sysInterfaceService;

    @Test
    public void send() {
    }

    @Test
    public void receive() {
    }

    @Test
    public void handleMailRequest() {
    }
}
