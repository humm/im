package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/11/07
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LearnStarter.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RedisTest {

    private static final String namespace = "com.hoomoomoo.im";

    @Lazy
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void redisUtil() {
        redisUtil.set(namespace, "baseType", "我是基本类型.");
        Map<String, String> map = new HashMap<>(16);
        map.put("test3", "我是set3");
        map.put("test4", "我是set4");
        redisUtil.mset(namespace, map);

        redisUtil.hset(namespace, "hashset", "test1", "我是set1");
        redisUtil.hset(namespace, "hashset", "test2", "我是set2");

        Map<String, Object> map1 = new HashMap<>(16);
        map1.put("set5", "我是set5");
        map1.put("set6", "我是set6");
        redisUtil.hmset(namespace, "hmset", map1, 10L);

        List list = new ArrayList<>();
        list.add("1");
        list.add("2");
        redisUtil.lSet(namespace, "list", list);
    }

}
