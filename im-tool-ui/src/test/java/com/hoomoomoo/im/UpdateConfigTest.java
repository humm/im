package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.CommonUtils;
import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2021/05/09
 */

public class UpdateConfigTest {

    /**
     * 去除敏感信息
     *
     * @param
     * @author: humm23693
     * @date: 2021/05/10
     * @return:
     */
    @Test
    public void updateAppConfig() {
        try {
            List<String> keys = new ArrayList<>(16);
            keys.add("svn.username");
            keys.add("svn.password");

            keys.add("svn.url");
            keys.add("svn.default.append.path");

            keys.add("svn.update.ta6");

            keys.add("fund.excel.path");
            keys.add("fund.generate.path");

            keys.add("process.excel.path");
            keys.add("process.generate.path");

            String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
            List<String> content = FileUtils.readNormalFile(confPath, false);
            if (CollectionUtils.isNotEmpty(content)) {
                for (int i = 0; i < content.size(); i++) {
                    String item = content.get(i);
                    if (isUpdate(item, keys)) {
                        int index = item.indexOf(STR_EQUALS) + 1;
                        item = item.substring(0, index) + STR_EMPTY;
                        content.set(i, item);
                    }
                }
            }
            FileUtils.writeFile(confPath, content, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isUpdate(String item, List<String> keys) {
        if (!item.contains(STR_EQUALS)) {
            return false;
        }
        for (String key : keys) {
            if (item.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新版本信息
     *
     * @param
     * @author: humm23693
     * @date: 2021/05/10
     * @return:
     */
    @Test
    public void updateVersionConfig() {
        try {
            String versionFilePath = FileUtils.getFilePath("/conf/version.conf").getPath();
            String versionFilePathSource = versionFilePath.replace("/target/classes", "/src/main/resources");
            StringBuilder statLog = new StringBuilder();
            statLog.append("版本号: ").append(CommonUtils.getCurrentDateTime4().replace(STR_HYPHEN, STR_POINTER)).append(STR_NEXT_LINE);
            statLog.append("发布时间: ").append(CommonUtils.getCurrentDateTime1()).append(STR_NEXT_LINE);
            FileUtils.writeFile(versionFilePath, statLog.toString(), false);
            FileUtils.writeFile(versionFilePathSource, statLog.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
