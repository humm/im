package com.hoomoomoo.im.main;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hoomoomoo.im.consts.BaseConst.STR_COMMA;
import static com.hoomoomoo.im.consts.BaseConst.STR_NEXT_LINE;

/**
 * 流程节点互斥
 */
public class BuildMutualExclusionConfig {

    public static void main(String[] args) {
        Map<String, String> config = new LinkedHashMap<>();
        config.put("A", "A名称");
        config.put("B", "B名称");
        config.put("C", "C名称");
        Iterator<String> iterator = config.keySet().iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            for (String ele : config.keySet()) {
                if (!StringUtils.equals(item, ele)) {
                    buildConfig(item, ele, config.get(item) + STR_COMMA + config.get(ele));
                }
            }
            iterator.remove();
        }
    }

    private static void buildConfig(String jobCode, String mutexJobCode, String mutexMessage) {
        StringBuilder sql = new StringBuilder();
        sql.append(String.format("delete from tbschedulejobmutex where sche_job_code = '%s' and mutex_job_code = '%s' and busin_type = '5';", jobCode, mutexJobCode)).append(STR_NEXT_LINE);
        sql.append("insert into tbschedulejobmutex (sche_job_code, mutex_job_code, busin_type, remarks)").append(STR_NEXT_LINE);
        sql.append(String.format("values ('%s', '%s', '5', '%s');", jobCode, mutexJobCode, mutexMessage)).append(STR_NEXT_LINE);
        System.out.println(sql);
    }
}
