package im.service.impl;

import im.service.ExecuteService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im.service.impl
 * @date 2020/10/24
 */

@Service("sql")
public class SqlService implements ExecuteService {

    @Override
    public String executeService(Map param) {
        return "SqlService";
    }
}
