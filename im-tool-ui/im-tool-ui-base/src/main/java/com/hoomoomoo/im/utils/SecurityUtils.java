package com.hoomoomoo.im.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @author humm23693
 * @description TODO
 * @package com.hoomoomoo.im.utils
 * @date 2021/04/28
 */
public class SecurityUtils {

    private static Key key;
    private final static String KEY_STR = "im";
    private final static String ENCODING_UTF8 = "UTF-8";
    private final static String ALGORITHM = "DES";
    private final static String SECURE_RANDOM = "SHA1PRNG";

    static {
        try {
            //生成DES算法对象
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            //运用SHA1安全策略
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM);
            //设置上密钥种子
            secureRandom.setSeed(KEY_STR.getBytes());
            //初始化基于SHA1的算法对象
            generator.init(secureRandom);
            //生成密钥对象
            key = generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param str
     * @return
     */
    public static String getEncryptString(String str) {
        //基于BASE64编码，接收byte[]并转换成String
        BASE64Encoder base64Encoder = new BASE64Encoder();
        try {
            // 按UTF8编码
            byte[] bytes = str.getBytes(ENCODING_UTF8);
            // 获取加密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化密码信息
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 加密
            byte[] doFinal = cipher.doFinal(bytes);
            // byte[]to encode好的String并返回
            return base64Encoder.encode(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param str
     * @return
     */
    public static String getDecryptString(String str) {
        // 基于BASE64编码，接收byte[]并转换成String
        BASE64Decoder base64decoder = new BASE64Decoder();
        try {
            // 将字符串decode成byte[]
            byte[] bytes = base64decoder.decodeBuffer(str);
            // 获取解密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化解密信息
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 解密
            byte[] doFinal = cipher.doFinal(bytes);
            // 返回解密之后的信息
            return new String(doFinal, ENCODING_UTF8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}