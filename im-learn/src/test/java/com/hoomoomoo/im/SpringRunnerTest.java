package com.hoomoomoo.im;

import com.hoomoomoo.im.config.SpringContextHolder;
import com.hoomoomoo.im.model.Box;
import com.hoomoomoo.im.model.Msg;
import com.hoomoomoo.im.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
public class SpringRunnerTest {

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

    @Test
    public void Reflex() {
        String className = "com.hoomoomoo.im.service.ReflexService";
        try {
            Class clzss = Class.forName(className);
            Object clzssBean = SpringContextHolder.getBean(clzss);
            Method[] methods = clzss.getMethods();
            for (Method method : methods) {
                System.out.println(method.getName());
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    System.out.println(parameter.getType());
                }
                if ("function02".equals(method.getName())) {
                    method.invoke(clzssBean, "function02");
                }
                if ("function03".equals(method.getName())) {
                    Map map = new HashMap();
                    map.put("code", "function03");
                    map.put("msg", "测试哦");
                    method.invoke(clzssBean, map);
                }
                if ("function04".equals(method.getName())) {
                    Box box = new Box<>();
                    box.setId("function04");
                    box.setName("测试下咯");
                    method.invoke(clzssBean, box);
                }
                if ("function05".equals(method.getName())) {
                    Box<Msg> box = new Box<>();
                    box.setId("function05");
                    box.setName("测试下咯");
                    Msg msg = new Msg();
                    msg.setCode("05");
                    msg.setContent("测试测试");
                    box.setData(msg);
//                    method.invoke(clzssBean, JSONObject.parseObject(JSONObject.toJSONString(box), parameters[0].getParameterizedType()));
                    method.invoke(clzssBean, box);
                }

                System.out.println("----------------------------------------");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
