package com.hoomoomoo.im.utils;

import com.hoomoomoo.im.cache.ConfigCache;
import com.hoomoomoo.im.dto.AppConfigDto;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        AppConfigDto appConfigDto = ConfigCache.getAppConfigDtoCache();
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

    public static List<Map<String, String>> executeQuery(String sql) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, String> item = new LinkedHashMap<>();
            result.add(item);
            for (int i=1; i<=columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i);
                String columnValue = resultSet.getString(i);
                item.put(columnName.toLowerCase(), columnValue);
            }
        }
        resultSet.close();
        statement.close();
        closeConnection();
        return result;
    }
}
