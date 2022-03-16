package com.hoomoomoo.im.service.impl;

import com.hoomoomoo.im.model.Box;
import com.hoomoomoo.im.model.Msg;
import com.hoomoomoo.im.service.ReflexService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.service.impl
 * @date 2020/11/20
 */

@Service
public class ReflexServiceImpl implements ReflexService {
    @Override
    public Map function01() {
        System.out.println("function01");
        return null;
    }

    @Override
    public Map function02(String msg) {
        System.out.println(msg);
        return null;
    }

    @Override
    public Map function03(Map msg) {
        System.out.println(msg);
        return null;
    }

    @Override
    public Map function04(Box box) {
        System.out.println(box.getId());
        return null;
    }

    @Override
    public Map function05(Box<Msg> box) {
        System.out.println(box.getData().getCode());
        return null;
    }
}
