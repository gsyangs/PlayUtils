package com.app.playutils;

import java.math.BigDecimal;

/**
 * @author:create by ys
 * 时间:2021/12/1 14
 * 邮箱 894417048@qq.com
 */
public class NumberUtils {

    public static float dtol(double v1){
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Float.toString(1));
            return b1.multiply(b2).floatValue();
        } catch (Exception e) {
            return 0;
        }
    }

} 