package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2022-09-26
 */
public class DatabaseUtils {

    public static Connection connection;

    public static ScriptRunner scriptRunner;

    private static Connection getConnection() throws Exception {
        if (connection != null) {
            return connection;
        }
        AppConfigDto appConfigDto = ConfigCache.getConfigCache().getAppConfigDto();
        connection = DriverManager.getConnection(appConfigDto.getDatabaseScriptUrl(), appConfigDto.getDatabaseScriptUsername(),
            appConfigDto.getDatabaseScriptPassword());
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (scriptRunner != null) {
            scriptRunner.closeConnection();
        }
        if (connection != null) {
            connection.close();
        }
        scriptRunner = null;
        connection = null;
    }

    private static ScriptRunner getScriptRunner() throws Exception {
        if (scriptRunner != null) {
            return scriptRunner;
        }
        ScriptRunner scriptRunner = new ScriptRunner(getConnection());
        scriptRunner.setSendFullScript(true);
        return scriptRunner;
    }

    public static void executeSql(String sql, String encode) throws Exception {
        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(sql.getBytes()));
        getScriptRunner().runScript(inputStreamReader);
    }
}
