package com.geely.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * <p>Title: 校讯通教师版</p>
 * <p>Description:字符串处理工具类</p>
 * <p>创建日期:2011-12-22</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class StringUtil {
    /**
     * 判断改字符是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if ((str != null) && !"".equals(str)) {
            return false;
        } else {
            return true;
        }
    }

    public static String mobileHide(String mobile) {
        if ((null != mobile) && !"".equals(mobile) && (11 == mobile.length())) {
            String m = "****";
            String s = mobile.substring(0, 4);
            String e = mobile.substring(8);

            return s + m + e;
        } else {
            return mobile;
        }
    }

    public static String makeUpPosition(String num) {
        if (10 > Integer.parseInt(num)) {
            return "0" + num;
        }

        return num + "";
    }

    /**
     * 判断字符是否为中文汉字 cherry
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) ||
                (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) ||
                (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) ||
                (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) ||
                (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) ||
                (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
            return true;
        }

        return false;
    }

    /**
     * 判断中文汉字的数量（一个汉字为2） cherry
     * @param c
     * @return
     */
    public static int strCount(String strName) {
        int count = 0;

        if ((null == strName) || "".equals(strName)) {
            return 0;
        }

        strName.trim();

        char[] ch = strName.toCharArray();

        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];

            if (isChinese(c) == true) {
                count += 2;
            } else {
                count++;
            }
        }

        return count;
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();

        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) { // 全角空格为12288，半角空格为32  
                c[i] = (char) 32;

                continue;
            }

            if ((c[i] > 65280) && (c[i] < 65375)) { // 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248  
                c[i] = (char) (c[i] - 65248);
            }
        }

        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!")
                 .replaceAll("：", ":"); // 替换中文标号  

        String regEx = "[『』]"; // 清除掉特殊字符  
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.replaceAll("").trim();
    }
}
