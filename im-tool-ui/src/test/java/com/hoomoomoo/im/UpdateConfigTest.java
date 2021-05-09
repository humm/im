package com.hoomoomoo.im;

import com.hoomoomoo.im.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.List;

import static com.hoomoomoo.im.consts.BaseConst.*;

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
            // 去除敏感密码信息
            String confPath = FileUtils.getFilePath("/conf/app.conf").getPath();
            List<String> content = FileUtils.readNormalFile(confPath, false);
            if (CollectionUtils.isNotEmpty(content)) {
                for (int i = 0; i < content.size(); i++) {
                    String item = content.get(i);
                    if (item.contains(STR_EQUALS) && item.contains(KEY_PASSWORD)) {
                        int index = item.indexOf(STR_EQUALS) + 1;
                        String password = STR_EMPTY;
                        item = item.substring(0, index) + password;
                        content.set(i, item);
                    }
                }
            }
            FileUtils.writeFile(confPath, content, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
