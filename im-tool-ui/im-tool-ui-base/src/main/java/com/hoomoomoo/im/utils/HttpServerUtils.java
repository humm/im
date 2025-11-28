package com.hoomoomoo.im.utils;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022/4/3
 */
public class HttpServerUtils {

    public static void initServer(String serverName, int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(serverName, new HttpResponseUtils());
            server.start();
            LoggerUtils.info("服务【 " + serverName.substring(1) + " 】" + "使用端口【 " + port + " 】初始化成功 ......");
        } catch (IOException e) {
            LoggerUtils.error(e);
        }
    }
}
