package im.service;

import im.model.Box;
import im.model.Msg;

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
