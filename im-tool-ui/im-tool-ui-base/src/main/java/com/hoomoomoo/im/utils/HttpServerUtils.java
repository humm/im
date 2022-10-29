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

    public static void initServer(String serverUrl, int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(serverUrl, new HttpResponseUtils());
            server.start();
        } catch (IOException e) {
            LoggerUtils.info(e);
        }
    }
}
