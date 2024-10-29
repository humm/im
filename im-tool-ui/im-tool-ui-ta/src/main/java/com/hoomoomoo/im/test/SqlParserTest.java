package com.hoomoomoo.im.test;

import com.hoomoomoo.im.extend.ScriptSqlUtils;
import com.hoomoomoo.im.utils.FileUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlParserTest {

    public static void main(String[] args) throws IOException {
        List<String> sql = FileUtils.readNormalFile("E:\\workspace\\ta6\\sql\\pub\\001initdata\\basedata\\07console-fund-ta-vue-menu-new-ued.sql", false);
        for (String ele : sql) {
            if (ele.startsWith("-- ") && (ele.contains("delete") || ele.contains("insert") || ele.contains("values"))) {
                ele = ele.replace("-- ", "");
            }
            if (ele.startsWith("values (")) {
                String reserve = ScriptSqlUtils.getMenuReserve(ele);
                if (StringUtils.isNotBlank(reserve) && !StringUtils.equals(reserve, "0") && !StringUtils.equals(reserve, " ")) {
                    System.out.println(ele);
                    continue;
                }
                boolean isMenu = ele.split(",").length > 10;
                if (!isMenu) {
                    System.out.println(ele);
                    continue;
                }
                String menuCode = ScriptSqlUtils.getMenuCode(ele);
                ele = ele.replace("values (", "select ").replace(");", " from (select count(1) param_exists from tsys_menu t where t.menu_code = '" + menuCode + "') a where a.param_exists = 1;");
                System.out.println(ele);
                continue;
            }
            System.out.println(ele);
        }

    }
}
