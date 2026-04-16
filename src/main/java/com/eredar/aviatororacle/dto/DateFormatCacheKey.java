package com.eredar.aviatororacle.dto;

import java.util.Objects;

/**
 * 为了缓存用于日期-字符串转换的 DateTimeFormatter，创建该类作为缓存key
 * <p>可防止因字符串拼接导致的哈希碰撞风险
 */
public final class DateFormatCacheKey {

    // 日期格式字符串
    private final String format;
    // 时区字符串
    private final String zoneId;

    public DateFormatCacheKey(String format, String zoneId) {
        this.format = format;
        this.zoneId = zoneId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DateFormatCacheKey other = (DateFormatCacheKey) obj;
        return Objects.equals(this.format, other.format) &&
                Objects.equals(this.zoneId, other.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, zoneId);
    }
}
