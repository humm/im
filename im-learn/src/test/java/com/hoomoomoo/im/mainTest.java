package com.hoomoomoo.im;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/11/14
 */
public class mainTest {

    @Test
    public void bigDecimal() {
        BigDecimal a = new BigDecimal("12.86");
        BigDecimal b = new BigDecimal("12.881");
        System.out.println("a " + a);
        System.out.println("b " + b);
        System.out.println("a+b " + a.add(b));
        System.out.println("a-b " + a.subtract(b));
        System.out.println("a*b " + a.multiply(b));
        System.out.println("a/b " + a.divide(b, 3, BigDecimal.ROUND_HALF_UP));
        // 四舍五入
        System.out.println("a%b 四舍五入 " + a.remainder(b).setScale(1, BigDecimal.ROUND_HALF_UP));
        // 五舍六入
        System.out.println("a%b 五舍六入 " + a.remainder(b).setScale(1, BigDecimal.ROUND_HALF_DOWN));
        // 截位
        System.out.println("a%b 截位 " + a.remainder(b).setScale(1, BigDecimal.ROUND_DOWN));
        // 进位
        System.out.println("a%b 进位 " + a.remainder(b).setScale(1, BigDecimal.ROUND_UP));
        System.out.println("equals " + a.equals(b));
        System.out.println("compareTo " + a.compareTo(b));
    }
}
