package com.eredar.aviatororacle.runtime.utils;

import com.googlecode.aviator.runtime.function.system.DateFormatCache;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date 类型工具类
 */
public class AODateUtils {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * {@code String} 转换为 {@code Date} 对象，使用默认的 {@code yyyy-MM-dd HH:mm:ss} 格式
     *
     * @param dateStr 日期字符串
     * @return {@code Date} 对象
     */
    public static Date strToDate(String dateStr) {
        return strToDate(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * {@code String} 转换为 {@code Date} 对象
     *
     * @param dateStr 日期字符串
     * @param format 日期格式字符串
     * @return {@code Date} 对象
     */
    @SneakyThrows
    public static Date strToDate(String dateStr, String format) {
        SimpleDateFormat sdf = DateFormatCache.getOrCreateDateFormat(format);
        return sdf.parse(dateStr);
    }

    /**
     * {@code Date} 对象转换为 {@code String}，使用默认的 {@code yyyy-MM-dd HH:mm:ss} 格式
     *
     * @param date {@code Date} 对象
     * @return 日期字符串
     */
    public static String dateToStr(Date date) {
        return dateToStr(date, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * {@code Date} 对象转换为 {@code String}
     *
     * @param date {@code Date} 对象
     * @param format 日期格式字符串
     * @return 日期字符串
     */
    @SneakyThrows
    public static String dateToStr(Date date, String format) {
        SimpleDateFormat sdf = DateFormatCache.getOrCreateDateFormat(format);
        return sdf.format(date);
    }
}
