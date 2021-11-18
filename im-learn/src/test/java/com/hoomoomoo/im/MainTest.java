package com.hoomoomoo.im;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author
 * @description TODO
 * @package com.hoomoomoo.im
 * @date 2020/11/14
 */
public class MainTest {

    @Test
    public void bigDecimal() {
        // 浮点数构造用字符串 避免精度问题 非浮点数没关系
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


    @Test
    public void date() {
        LocalDate date = LocalDate.now();
        System.out.println("当前日期：" + date);

        LocalDate date1 = LocalDate.of(2000, 1, 1);
        System.out.println("千禧年：" + date1);

        LocalDate date2 = LocalDate.now();
        System.out.printf("年：%d 月：%d 日：%d\n", date2.getYear(), date2.getMonthValue(), date2.getDayOfMonth());

        LocalDate now = LocalDate.now();
        LocalDate date3 = LocalDate.of(2018, 9, 24);
        System.out.println("日期是否相等：" + now.equals(date3));

        LocalTime time = LocalTime.now();
        System.out.println("当前时间：" + time);

        // 时间增量
        LocalTime time2 = LocalTime.now();
        LocalTime newTime = time2.plusHours(2);
        System.out.println("newTime：" + newTime);

        // 日期增量
        LocalDate date4 = LocalDate.now();
        LocalDate newDate = date4.plus(1, ChronoUnit.WEEKS);
        System.out.println("newDate：" + newDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 日期时间转字符串
        LocalDateTime now1 = LocalDateTime.now();
        String nowText = now1.format(formatter);
        System.out.println("nowText：" + nowText);

        // 字符串转日期时间
        String datetimeText = "1999-12-31 23:59:59";
        LocalDateTime datetime = LocalDateTime.parse(datetimeText, formatter);
        System.out.println(datetime);
    }

    @Test
    public void test() {
        System.out.println(new BigDecimal("-1").compareTo(BigDecimal.ZERO) >= 0);
    }
}
