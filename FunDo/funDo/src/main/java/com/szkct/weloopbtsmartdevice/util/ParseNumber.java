package com.szkct.weloopbtsmartdevice.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${Justin} on 2018/6/16.
 * 转型,系统的方法因为代码里面调用之前有很多都没做判断.如传"10,2"等的数据
 */

public class ParseNumber {

    public static float parseFloat(String numberString) {
        if (numberString.contains(",")) {
            numberString = numberString.replace(",", ".");
        }

        if (Utils.isNumeric(numberString)) {
            return Float.parseFloat(numberString);
        } else {
            return 10;
        }
    }

    public static int parseInt(String numberString) {

        if (numberString.contains("cm")) {
            numberString = numberString.replace("cm", "");
        }

        if (numberString.contains("kg")) {
            numberString = numberString.replace("kg", "");
        }

        if (numberString.contains(",")) {
            numberString = numberString.split(",")[0];
        } else if (numberString.contains(".")) {
            numberString = numberString.split("\\.")[0];
        }

        if (Utils.isNumeric(numberString)) {
            int temp = 10;
            try {
                temp = Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return temp;
        } else {
            return 10;
        }
    }
}
