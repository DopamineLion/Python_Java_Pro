package org.dopamine.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DopamineUtils {
    public Boolean isEmpty(String tag){
        return tag == null || tag.isEmpty();
    }

    /**
     * 格式化字段
     * @param inputFormat 输入时间格式化模板，如果未Null则输出格式MM月dd日HH时mm分
     * @return 返回时间格式化后的结果
     */
    public static String formatDate(String inputFormat){
        /*默认格式*/
        String defaultTime = "MM月dd日HH时mm分";
        SimpleDateFormat simple;
        simple = inputFormat !=null ? new SimpleDateFormat(inputFormat) : new SimpleDateFormat(defaultTime);
        return simple.format(System.currentTimeMillis());
    }

    /**
     * 正则判断
     * @param regex 正则表达式
     * @param text 需要判断的字符串
     * @return
     */
    public static boolean search(String regex,String text){
        return Pattern.compile(regex).matcher(text).find();
    }
}
