package com.hoomoomoo.im.cache;

import jxl.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskMemoryCache {
    /**
     * 全量task
     */
    private static Map<String, Map<String, Object>> map = new ConcurrentHashMap<>();
    /**
     * 用于存放 已经生成过的taskcode
     */
    private static Map<String, String> existMap = new ConcurrentHashMap<>();
    public static final String SCHE_TASK_CODE = "sche_task_code";
    public static final String HEAD_TASK = "head_task";
    public static final String SCHE_TASK_NAME = "sche_task_name";
    public static final String FUNCTION_ID = "function_id";
    public static final String PARENT_FUNDCITION_ID = "parent_function_id";
    public static final String SCHE_TASK_REDO = "sche_task_redo";
    public static final String SCHE_TASK_TIMEOUT = "sche_task_timeout";
    public static final String SCHE_TASK_RETRYCOUNT = "sche_task_retrycount";
    public static final String SCHE_TASK_ISUSE = "sche_task_isuse";
    public static final String SCHE_TASK_ISHIDE = "sche_task_ishide";
    public static final String SCHE_TASK_MEMO = "sche_task_memo";
    public static final String SCHE_TASK_DEPENDENCIES = "sche_task_dependencies";
    public static final String BANK_NO = "bank_no";
    public static final String TA_CODE = "ta_code";
    public static final String SCHE_TASK_ISSKIP = "sche_task_isskip";
    public static final String SCHE_TASK_SKIPREASON = "sche_task_skipreason";
    public static final String SCHE_TASK_DELAYTIME = "sche_task_delaytime";
    public static final String SCHE_TASK_PAUSE = "sche_task_pause";
    public static final String SCHE_TASK_BUSINPARAM = "busin_param";
    public static final String SCHE_TASK_RESERVE = "sche_reserve";

    public static void cleaExistMap() {
        existMap.clear();
    }

    public static void setExistMap(String key) {
        existMap.put(key, key);
    }

    public static boolean isExistMap(String key) {
        if (existMap.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, Map<String, Object>> getCacheMap() {
        return map;
    }

    public static Map<String, Object> getCacheMap(String taskCode) {
        return map.get(taskCode);
    }

    public static List<Map<String, Object>> getCacheMapByFunction(String functionId, String reserve) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry entry : map.entrySet()) {
            if (((Map<String, Object>) entry.getValue()).get(PARENT_FUNDCITION_ID).equals(functionId) &&
                    ((Map<String, Object>) entry.getValue()).get(SCHE_TASK_RESERVE).equals(reserve)) {
                list.add((Map<String, Object>) entry.getValue());
            }

        }
        return list;
    }

    /**
     * 初始化缓存
     */
    public static void initCache(Sheet sheet) {
        // 数据结构 主key 是 task code，二级key 是 字段名称
        map.clear();
        int rows = sheet.getRows();
        for (int k = 2; k < rows; k++) {
            Map<String, Object> subMap = new ConcurrentHashMap<>();
            // 判断有多少行
            int columns = sheet.getColumns();
            for (int i = 0; i < columns; i++) {
                subMap.put(sheet.getCell(i, 1).getContents(), sheet.getCell(i, k).getContents());
            }
            map.put(sheet.getCell(0, k).getContents(), subMap);

        }
    }

}
