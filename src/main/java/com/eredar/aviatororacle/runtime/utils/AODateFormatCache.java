package com.eredar.aviatororacle.runtime.utils;

import com.eredar.aviatororacle.runtime.dto.DateFormatCacheKey;
import com.eredar.aviatororacle.utils.AOUtils;
import com.googlecode.aviator.utils.LRUMap;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * DateFormat cache
 */
public class AODateFormatCache {

    private static final int maxSize = Integer.parseInt(System.getProperty("aviator.date_format.cache.max", "256"));

    private static final ThreadLocal<LRUMap<DateFormatCacheKey, DateTimeFormatter>> formatCache = new ThreadLocal<>();

    public static DateTimeFormatter getOrCreateDateFormat(String format, String zoneID) {
        LRUMap<DateFormatCacheKey, DateTimeFormatter> cache = formatCache.get();
        if (cache == null) {
            cache = new LRUMap<>(maxSize);
            formatCache.set(cache);
        }
        if (AOUtils.isBlank(format)) {
            throw new IllegalArgumentException("日期格式不能为空");
        }
        if (AOUtils.isBlank(zoneID)) {
            throw new IllegalArgumentException("zoneID不能为空");
        }
        // 为了防止因字符串拼接导致的碰撞风险，创建对象key
        DateFormatCacheKey key = new DateFormatCacheKey(format, zoneID);
        DateTimeFormatter dtf = cache.get(key);
        if (dtf == null) {
            dtf = new DateTimeFormatterBuilder()
                    .appendPattern(format)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)   // 默认 1号
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)    // 默认 0点
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0) // 默认 0分
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0) // 默认 0秒
                    .toFormatter()
                    .withZone(ZoneId.of(zoneID));
            cache.put(key, dtf);
        }
        return dtf;
    }
}
