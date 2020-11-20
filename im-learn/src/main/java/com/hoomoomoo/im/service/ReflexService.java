package com.hoomoomoo.im.service;

import com.hoomoomoo.im.model.Box;
import com.hoomoomoo.im.model.Msg;

import java.util.Map;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.service
 * @date 2020/11/20
 */
public interface ReflexService {

    Map function01();

    Map function02(String msg);

    Map function03(Map msg);

    Map function04(Box box);

    Map function05(Box<Msg> box);

}
