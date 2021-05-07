package com.hoomoomoo.im.cache;

import jxl.Sheet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransMemoryCache {
    private static Map<String, Map<String, Object>> map = new ConcurrentHashMap<>();
    public static final String TRANS_CODE = "trans_code";
    public static final String TRANS_NAME = "trans_name";
    public static final String ENABLE_FLAG = "enable_flag";
    public static final String CHANNELS = "channels";
    public static final String HOST_ONLINE = "host_online";
    public static final String TRANS_TYPE = "trans_type";
    public static final String MONITOR_FLAG = "monitor_flag";
    public static final String LOG_LEVEL = "log_level";
    public static final String CANCEL_FLAG = "cancel_flag";
    public static final String ERASE_FLAG = "erase_flag";
    public static final String MON_TRANS_TYPE = "mon_trans_type";
    public static final String RESERVE1 = "reserve1";
    public static final String RESERVE2 = "reserve2";
    public static final String RESERVE3 = "reserve3";

    /**
     * @return
     */
    public static Map<String, Map<String, Object>> getCacheMap() {
        return map;
    }

    public static Map<String, Object> getCacheMap(String taskCode) {
        return map.get(taskCode);
    }


    /**
     * 初始化缓存
     */
    public static void initCache(Sheet sheet) {
        // 数据结构 主key 是 task code，二级key 是 字段名称
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
