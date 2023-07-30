package com.hoomoomoo.im.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.utils.CommonUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.controller
 * @date 2023/7/30
 */
public class HepController {
    private static Set<String> field = new LinkedHashSet<>();

    private final static Integer STATUS_200 = 200;
    private final static String STR_STATUS_200 = "200";
    private final static String REQUEST_URL = "http://cloudin.proxy.in.hundsun.com/openapi/invoke/defaultFormData";
    private final static String APP_ID = "dqwhyanulhrmrrnk";
    private final static String APP_KEY = "fbbbee8e31a646d3a3a45f5c0e5b3e9";
    private final static String CURRENT_USER_ID = "humm23693";
    private final static String METHOD_GET_FIELD_INFO = "devtool/getFieldInfo";
    private final static String METHOD_FETCH_TASK_BY_ID = "devtool/fetchTaskById";
    private final static String METHOD_UPDATE_TASK_STATUS = "devtool/updateTaskStatus";
    private final static String METHOD_FETCH_TASK_LIST = "devtool/fetchTaskList";
    private final static String STATUS_LIST = "0,4,5,8,14,16,17,18,6";
    private final static String OPERATE_TYPE_START = "1";
    private final static String OPERATE_TYPE_COMPLETE = "3";

    static {
        field.add(KEY_CHARSET);
        field.add(KEY_REAL_FINISH_TIME);
        field.add(KEY_OPERATE_TYPE);
        field.add(KEY_METHOD);
        field.add(KEY_SUGGESTION);
        field.add(KEY_FORMAT);
        field.add(KEY_CURRENT_USER_ID);
        field.add(KEY_SIGN);
        field.add(KEY_REAL_WORKLOAD);
        field.add(KEY_APP_KEY);
        field.add(KEY_FINISH_PERCENTAGE);
        field.add(KEY_INTEGRATE_ATTENTION);
        field.add(KEY_MODIFIED_FILE);
        field.add(KEY_STATUS_LIST);
        field.add(KEY_ID);
        field.add(KEY_APP_ID);
        field.add(KEY_EDIT_DESCRIPTION);
        field.add(KEY_TIMESTAMP);
    }
    // 0:待启动,4:开发中,5,8,14,16,17:待审核,18,6"
    public static void main(String[] args) throws IOException {
        // 获取任务列表
        HttpResponse response = sendPost(getTaskList());
        // 启动任务
        // HttpResponse response = sendPost(startTask("11247913"));
        // 完成任务
        // HttpResponse response = sendPost(completeTask("11243393"));
        if (response != null) {
            System.out.println("response: " + response.body());
            Map<String, Object> responseInfo = (Map)JSONObject.parse(response.body());
            String message = String.valueOf(responseInfo.get(KEY_MESSAGE));
            if (STATUS_200 == response.getStatus()) {
                String code = String.valueOf(responseInfo.get(KEY_CODE));
                if (STR_STATUS_200.equals(code)) {
                    Object data = responseInfo.get(KEY_DATA);
                    JSONArray items = new JSONArray();
                    if (data instanceof Map) {
                        items.add(data);
                    } else if (data instanceof List) {
                        items = (JSONArray)data;
                    }
                    // 任务列表处理
                    dealTaskList(items);
                } else {
                    System.out.println(message);
                }
            } else {
                System.out.println(message);
            }
        }
    }

    private static Map<String, Object> completeTask(String taskId) {
        // 获取列表数据
        Map<String, Object> request = new HashMap<>(16);
        request.put(KEY_METHOD, METHOD_GET_FIELD_INFO);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
        HttpResponse response = sendPost(request);
        if (response == null || STATUS_200 == response.getStatus()) {
            request.remove(KEY_OPERATE_TYPE);
            request.put(KEY_METHOD, METHOD_FETCH_TASK_BY_ID);
            request.put(KEY_ID, taskId);
        }
        response = sendPost(request);
        if (response == null || STATUS_200 == response.getStatus()) {
            request.put(KEY_METHOD, METHOD_UPDATE_TASK_STATUS);
            request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
            request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_COMPLETE);
            request.put(KEY_ID, taskId);
            // 实际完成时间
            request.put(KEY_REAL_FINISH_TIME, CommonUtils.getCurrentDateTime1());
            // 完成百分比
            request.put(KEY_FINISH_PERCENTAGE, OPERATE_TYPE_START);
            // 集成注意
            request.put(KEY_INTEGRATE_ATTENTION, "");
            // 今日总工时
            request.put(KEY_REAL_WORKLOAD, "3");
            // 修改文件
            request.put(KEY_MODIFIED_FILE, "/branches/fix/FUND/TA6.0-FUND.V202304.03.001/Sources/ta/fund/front/HUI1.0/console-fund-ta-vue/components/ConfigTable.vue\\r<br>/branches/fix/FUND/TA6.0-FUND.V202304.03.001/Sources/ta/fund/server/ta-web-manager-fund-core/src/main/java/com/hundsun/lcpt/fund/impl/fundanalysis/FundStaticShareInfoQueryService.java");
            // 修改说明
            request.put(KEY_EDIT_DESCRIPTION, "修复&nbsp持有人份额信息查询报错&nbsp");
            // 测试建议
            request.put(KEY_SUGGESTION, "修复&nbsp持有人份额信息查询报错&nbsp");
        }
        return request;
    }

    private static Map<String, Object> startTask(String taskId) {
        // 获取列表数据
        Map<String, Object> request = new HashMap<>(8);
        request.put(KEY_METHOD, METHOD_GET_FIELD_INFO);
        request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_START);
        HttpResponse response = sendPost(request);
        if (response == null || STATUS_200 == response.getStatus()) {
            request.remove(KEY_OPERATE_TYPE);
            request.put(KEY_METHOD, METHOD_FETCH_TASK_BY_ID);
            request.put(KEY_ID, taskId);
        }
        response = sendPost(request);
        if (response == null || STATUS_200 == response.getStatus()) {
            request.put(KEY_METHOD, METHOD_UPDATE_TASK_STATUS);
            request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
            request.put(KEY_OPERATE_TYPE, OPERATE_TYPE_START);
            request.put(KEY_ID, taskId);
        }
        return request;
    }

    private static Map<String, Object> getTaskList() throws IOException {
        // 获取列表数据
        Map<String, Object> request = new HashMap<>(3);
        request.put(KEY_CURRENT_USER_ID, CURRENT_USER_ID);
        request.put(KEY_METHOD, METHOD_FETCH_TASK_LIST);
        request.put(KEY_STATUS_LIST, STATUS_LIST);
        return request;
    }

    private static void dealTaskList(JSONArray items) {
        items = sortItems(items);
        for (int i=0; i<items.size(); i++) {
            Map<String, Object> item = (Map)items.get(i);
            String taskId = getValue(item, KEY_ID);
            String taskNumber = getValue(item, KEY_TASK_NUMBER);
            String taskName = getValue(item, KEY_NAME);
            String productName = getValue(item, "product_name");
            String taskVersion = getValue(item, "sprint_version");
            String taskStatus = getValue(item, "status");
            String taskStatusName = getValue(item, "status_name");
            String taskFinishTime = getValue(item, KEY_ESTIMATE_FINISH_TIME);
            System.out.println(taskId + " " + taskNumber + " " + taskName + " " + productName + " " + taskVersion + " " + taskStatus + " " + taskStatusName + " " + taskFinishTime);
        }
    }

    private static HttpResponse sendPost(Map<String, Object> param) {
        param.put(KEY_APP_ID, APP_ID);
        param.put(KEY_APP_KEY, APP_KEY);
        param.put(KEY_CHARSET, "utf-8");
        param.put(KEY_FORMAT, "MD5");
        param.put(KEY_TIMESTAMP, System.currentTimeMillis());
        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
        initRequest(param, jsonObject);
        DigestAlgorithm digestAlgorithm = DigestAlgorithm.MD5;
        String sign = SecureUtil.signParams(digestAlgorithm, jsonObject, "&", "=", true, new String[0]).toUpperCase();
        jsonObject.set(KEY_SIGN, sign);
        System.out.println("url: " + REQUEST_URL);
        System.out.println("method: " + jsonObject.get(KEY_METHOD));
        System.out.println("request: " + jsonObject);
        if (true) {
            return null;
        }
        HttpResponse response = HttpRequest.post(REQUEST_URL).timeout(6000).form(jsonObject).execute();
        return response;
    }

    private static void initRequest(Map<String, Object> param, cn.hutool.json.JSONObject jsonObject) {
        Iterator<String> iterator = field.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (param.containsKey(key)) {
                jsonObject.set(key, param.get(key));
            }
        }
    }

    private static JSONArray sortItems(JSONArray items) {
        JSONArray res = new JSONArray();
        Set<String> existkey = new HashSet<>();
        items.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date finishTime1 = simpleDateFormat.parse(getValue((Map)o1, KEY_ESTIMATE_FINISH_TIME));
                    Date finishTime2 = simpleDateFormat.parse(getValue((Map)o2, KEY_ESTIMATE_FINISH_TIME));
                    if (finishTime1.getTime() != finishTime2.getTime()) {
                        return finishTime1.compareTo(finishTime2);
                    }
                    String taskName1 = getValue((Map)o1, KEY_NAME);
                    String taskName2 = getValue((Map)o2, KEY_NAME);
                    if (taskName1.contains(SYMBOL_BRACKETS_2_RIGHT)) {
                        taskName1 = taskName1.split(SYMBOL_BRACKETS_2_RIGHT)[1];
                    }
                    if (taskName2.contains(SYMBOL_BRACKETS_2_RIGHT)) {
                        taskName2 = taskName2.split(SYMBOL_BRACKETS_2_RIGHT)[1];
                    }
                    return taskName1.compareTo(taskName2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        for (int i=0; i<items.size(); i++) {
            Map item = (Map)items.get(i);
            String taskNumber = getValue(item, KEY_TASK_NUMBER);
            String taskName = getValue(item, KEY_NAME);
            if (existkey.contains(taskNumber)) {
                continue;
            }
            existkey.add(taskNumber);
            res.add(item);
            for (int j=0; j<items.size(); j++) {
                Map itemTmp = (Map)items.get(j);
                String taskNumberTmp = getValue(itemTmp, KEY_TASK_NUMBER);
                String taskNameTmp = getValue(itemTmp, KEY_NAME);
                if (existkey.contains(taskNumberTmp)) {
                    continue;
                }
                if (taskNameTmp.equals(taskName)) {
                    res.add(itemTmp);
                    existkey.add(taskNumberTmp);
                }
            }
        }
        return res;
    }


    private static String getValue(Map<String, Object> item, String key) {
        if (item.containsKey(key)) {
            return String.valueOf(item.get(key));
        }
        return SYMBOL_EMPTY;
    }
}
