package com.hoomoomoo.im;

import com.hoomoomoo.im.controller.ChangeToolController;
import com.hoomoomoo.im.utils.SecurityUtils;
import org.junit.Test;

import static com.hoomoomoo.im.consts.BaseConst.STR_COMMA;
import static com.hoomoomoo.im.consts.BaseConst.SUPER_MAC_ADDRESS;

public class DevTest {

    @Test
    public void build() throws Exception {
        // 9F:DE:06:A0:6D:0D
        String mac = SecurityUtils.getEncryptString("9F:DE:06:A0:6D:1D");
        System.out.println(mac);
    }
}
