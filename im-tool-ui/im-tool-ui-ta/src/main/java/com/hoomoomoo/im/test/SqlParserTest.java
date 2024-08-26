package com.hoomoomoo.im.test;

import com.hoomoomoo.im.utils.FileUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.io.IOException;
import java.util.List;

public class SqlParserTest {

    public static void main(String[] args) throws IOException, JSQLParserException {
        String sql = FileUtils.readNormalFileToString("E:\\workspace\\ta6\\Sources\\final\\ta\\fund\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu-new-ued.sql");
        Statement statement = CCJSqlParserUtil.parse(sql);
    }
}
