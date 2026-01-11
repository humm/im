package com.hoomoomoo.im.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hoomoomoo.im.consts.MenuFunctionConfig;
import com.hoomoomoo.im.dto.FunctionDto;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.dto
 * @date 2022/4/3
 */
public class HttpResponseUtils implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> requestParam = paramGet(exchange);
        requestParam.putAll(paramPost(exchange));
        String functionCode = requestParam.get(KEY_FUNCTION_CODE);
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put(KEY_MESSAGE, "功能不匹配");
        if (StringUtils.equals(functionCode, KEY_FUNCTION_CODE_CHECK_VERSION)) {
            try {
                responseMessage = checkVersion(exchange, requestParam);
            } catch (Exception e) {
                LoggerUtils.error(e);
                throw new IOException("版本校验异常");
            }
        }
        exchange.sendResponseHeaders(200, 0);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(JSONObject.toJSONString(responseMessage).getBytes(ENCODING_UTF8));
        outputStream.close();
    }

    private Map<String, String> checkVersion(HttpExchange exchange, Map<String, String> requestParam) throws Exception {
        String requestVer = requestParam.get(KEY_VERSION);
        String finalVer = CommonUtils.getVersion();
        String ipAddress = exchange.getRemoteAddress().toString().substring(1);
        LoggerUtils.info(ipAddress + " 请求版本校验 开始");
        LoggerUtils.info(ipAddress + " 请求参数: " +  requestParam);
        LoggerUtils.info(ipAddress + " 请求版本信息: " + requestVer);
        boolean checkSame = true;
        if (!StringUtils.equals(requestVer, finalVer)) {
            Map<String, Map<String, String>> checkFile = JSONObject.parseObject(requestParam.get(KEY_CHECK_FILE), Map.class);
            if (MapUtils.isNotEmpty(checkFile)) {
                List<FunctionDto> functionDtoList = CommonUtils.getRealAuthFunction();
                if (CollectionUtils.isNotEmpty(functionDtoList)) {
                    for (FunctionDto functionDto : functionDtoList) {
                        MenuFunctionConfig.FunctionConfig functionConfig = MenuFunctionConfig.FunctionConfig.getFunctionConfig(functionDto.getFunctionCode());
                        String relateFile = functionConfig.getRelateFile();
                        if (StringUtils.isNotBlank(relateFile)) {
                            String functionCode = functionDto.getFunctionCode();
                            List<String> relateFileList = Arrays.asList(relateFile.split(STR_COMMA));
                            Map<String, String> checkFileList = checkFile.get(functionCode);
                            for (String file : relateFileList) {
                                if (!checkFileList.containsKey(file)) {
                                    checkSame = false;
                                    break;
                                }
                            }
                            for (String file : checkFileList.keySet()) {
                                if (!relateFileList.contains(file)) {
                                    checkSame = false;
                                    break;
                                }
                            }
                            List<String> content = FileUtils.readNormalFile(FileUtils.getFilePath(PATH_FILE));
                            List<String> fileList = new ArrayList<>();
                            for (Map<String, String> file : checkFile.values()) {
                                fileList.addAll(file.values());
                            }
                            for (String file : fileList) {
                                if (!content.contains(file)) {
                                    checkSame = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String, String> message = new HashMap<>(2);
        LoggerUtils.info(ipAddress + " 最新版本信息: " + finalVer);
        if (!checkSame) {
            message.put(KEY_VERSION, finalVer);
            LoggerUtils.info(ipAddress + " 版本校验结果: 需要进行版本更新");
        } else {
            LoggerUtils.info(ipAddress + " 版本校验结果: 不需要进行版本更新");
        }
        LoggerUtils.info(ipAddress + " 请求版本校验 结束");
        return message;
    }

    private static Map<String, String> paramGet(HttpExchange exchange) {
        return convertDataToMap(exchange.getRequestURI().getQuery());
    }

    private static Map<String, String> paramPost(HttpExchange exchange) {
        String result = STR_BLANK;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), ENCODING_UTF8));;
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LoggerUtils.error(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                LoggerUtils.error(e2);
            }

        }
        return convertDataToMap(result);
    }

    private static Map<String, String> convertDataToMap(String formData) {
        Map<String, String> result = new HashMap<>(16);
        if (formData == null || formData.trim().length() == 0) {
            return result;
        }
        String[] items = formData.split(STR_AND);
        Arrays.stream(items).forEach(item -> {
            final String[] keyAndVal = item.split(STR_EQUAL);
            if (keyAndVal.length == 2) {
                try {
                    final String key = URLDecoder.decode(keyAndVal[0], ENCODING_UTF8);
                    final String val = URLDecoder.decode(keyAndVal[1], ENCODING_UTF8);
                    result.put(key, val);
                } catch (UnsupportedEncodingException e) {
                    LoggerUtils.error(e);
                }
            }
        });
        return result;
    }
}
