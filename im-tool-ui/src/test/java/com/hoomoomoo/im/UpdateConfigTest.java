package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.STR_EMPTY;
import static com.hoomoomoo.im.consts.BaseConst.STR_EQUALS;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2021/05/09
 */

public class UpdateConfigTest {

    @Test
    public void updateAppConfig() {
        try {
            List<String> keys = new ArrayList<>(16);
            keys.add("svn.username");
            keys.add("svn.password");
            // 去除敏感信息
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

}
