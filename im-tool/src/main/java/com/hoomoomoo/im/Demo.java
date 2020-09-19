package com.hoomoomoo.im;

import org.hswebframework.utils.file.EncodingDetect;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/09/12
 */
public class Demo {

    public static void main(String[] args) {
        String fileEncode = EncodingDetect.getJavaEncode("D:\\download\\alter_TA5.0V202005B.01.000_oracle_主库 (1).sql");
        System.out.println(fileEncode);
    }
}
