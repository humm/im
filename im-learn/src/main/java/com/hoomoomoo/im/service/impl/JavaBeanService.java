package com.hoomoomoo.im.service.impl;

import com.hoomoomoo.im.service.ExecuteService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.service.impl
 * @date 2020/10/24
 */
@Service("javaBean")
public class JavaBeanService implements ExecuteService {

    @Override
    public String executeService(Map param) {
        return "JavaBeanService";
    }
}
