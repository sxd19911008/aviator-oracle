package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.dto.DateFormatCacheKey;
import com.eredar.aviatororacle.utils.AOUtils;
import com.googlecode.aviator.utils.LRUMap;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
            dtf = DateTimeFormatter.ofPattern(format).withZone(ZoneId.of(zoneID));
            cache.put(key, dtf);
        }
        return dtf;
    }
}
