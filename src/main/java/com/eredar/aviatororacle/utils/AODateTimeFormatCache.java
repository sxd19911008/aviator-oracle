package com.eredar.aviatororacle.utils;

import com.eredar.aviatororacle.dto.DateFormatCacheKey;
import com.googlecode.aviator.utils.LRUMap;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * DateFormat 缓存工具类
 * <p>提供两种重载：
 * <ul>
 *   <li>{@link #getOrCreateDateFormat(String, String)} —— 带时区，适用于 Instant 的格式化/解析</li>
 *   <li>{@link #getOrCreateDateFormat(String)} —— 不带时区，适用于 LocalDateTime 的格式化/解析</li>
 * </ul>
 * 内部共用同一个 ThreadLocal LRU 缓存，key 为 {@link DateFormatCacheKey}（zoneId 为 null 时仍可正确命中）
 */
public class AODateTimeFormatCache {

    private static final int maxSize = Integer.parseInt(System.getProperty("aviator.date_format.cache.max", "256"));

    private static final ThreadLocal<LRUMap<DateFormatCacheKey, DateTimeFormatter>> formatCache = new ThreadLocal<>();

    /**
     * 获取或创建带时区的 DateTimeFormatter，适用于 Instant 场景
     *
     * @param format 字符串日期格式，不能为空
     * @param zoneID 时区 ID 字符串（如 "Asia/Shanghai"），不能为空
     * @return 带时区的 DateTimeFormatter
     */
    public static DateTimeFormatter getOrCreateDateFormat(String format, String zoneID) {
        if (AOUtils.isBlank(format)) {
            throw new IllegalArgumentException("日期格式不能为空");
        }
        if (AOUtils.isBlank(zoneID)) {
            throw new IllegalArgumentException("zoneID不能为空");
        }
        LRUMap<DateFormatCacheKey, DateTimeFormatter> cache = getOrInitCache();
        // 为了防止因字符串拼接导致的碰撞风险，创建对象 key
        DateFormatCacheKey key = new DateFormatCacheKey(format, zoneID);
        DateTimeFormatter dtf = cache.get(key);
        if (dtf == null) {
            dtf = new DateTimeFormatterBuilder()
                    .appendPattern(format)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)     // 默认 1号
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)      // 默认 0点
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)   // 默认 0分
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0) // 默认 0秒
                    .toFormatter()
                    .withZone(ZoneId.of(zoneID));
            cache.put(key, dtf);
        }
        return dtf;
    }

    /**
     * 获取或创建不带时区的 DateTimeFormatter，适用于 LocalDateTime 场景
     * <p>不调用 {@code withZone()}，保留 LocalDateTime 的时区无关性
     *
     * @param format 字符串日期格式，不能为空
     * @return 不带时区的 DateTimeFormatter
     */
    public static DateTimeFormatter getOrCreateDateFormat(String format) {
        if (AOUtils.isBlank(format)) {
            throw new IllegalArgumentException("日期格式不能为空");
        }
        LRUMap<DateFormatCacheKey, DateTimeFormatter> cache = getOrInitCache();
        // zoneId 传 null，与带时区的 key 不会碰撞
        DateFormatCacheKey key = new DateFormatCacheKey(format, null);
        DateTimeFormatter dtf = cache.get(key);
        if (dtf == null) {
            dtf = new DateTimeFormatterBuilder()
                    .appendPattern(format)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)     // 默认 1号
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)      // 默认 0点
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)   // 默认 0分
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0) // 默认 0秒
                    .toFormatter();
            cache.put(key, dtf);
        }
        return dtf;
    }

    /**
     * 获取当前线程的缓存，不存在则初始化
     */
    private static LRUMap<DateFormatCacheKey, DateTimeFormatter> getOrInitCache() {
        LRUMap<DateFormatCacheKey, DateTimeFormatter> cache = formatCache.get();
        if (cache == null) {
            cache = new LRUMap<>(maxSize);
            formatCache.set(cache);
        }
        return cache;
    }
}
