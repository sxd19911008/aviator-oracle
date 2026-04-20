package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.constants.AviatorOracleConstants;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 基于 {@link Instant} 实现Oracle数据库日期操作
 */
public class OracleInstantUtils {

    public static OraDecimal monthsBetween(Instant endDate, Instant beginDate) {
        return monthsBetween(endDate, beginDate, ZoneOffset.UTC);
    }

    /**
     * 2个日期之间间隔的月份
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @param zoneId 时区
     * @return 间隔月份
     */
    public static OraDecimal monthsBetween(Instant endDate, Instant beginDate, ZoneId zoneId) {
        /* 入参校验 */
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }
        if (zoneId == null) {
            throw new IllegalArgumentException("时区zoneId不能为空");
        }
        // 支持负数计算
        OraDecimal sign = OraDecimal.ONE; // 正负号
        if (beginDate.isAfter(endDate)) {
            Instant tempDate = beginDate;
            beginDate = endDate;
            endDate = tempDate;
            sign = AviatorOracleConstants.NEG; // 设置为 -1
        }

        /* 转换为 ZonedDateTime */
        ZonedDateTime begin = beginDate.atZone(zoneId);
        ZonedDateTime end = endDate.atZone(zoneId);

        /* 计算基础月份差 (年差 * 12 + 月差) */
        int yearsDiff = end.getYear() - begin.getYear();
        int monthsDiff = end.getMonthValue() - begin.getMonthValue();
        int totalMonths = yearsDiff * 12 + monthsDiff;
        OraDecimal months = OraDecimal.valueOf(totalMonths);

        /* 判断是否“同日”或“均为月末” */
        // 判断是否“同日”，比如1月12日与2月12日属于“同日”
        boolean sameDayOfMonth = begin.getDayOfMonth() == end.getDayOfMonth();
        // 判断是否“均为月末”，比如1月31日与2月28日属于“均为月末”
        boolean bothLastDayOfMonth = isLastDayOfMonth(begin) && isLastDayOfMonth(end);
        if (sameDayOfMonth || bothLastDayOfMonth) {
            // 乘以正负号
            months = months.multiply(sign);
            return months;
        }

        // 如果不满足上述条件，计算小数部分

        /* 计算秒数 */
        long secondOfBegin = begin.toLocalTime().toSecondOfDay();
        long secondOfEnd = end.toLocalTime().toSecondOfDay();
        long secondsByHours = secondOfEnd - secondOfBegin;

        /* 计算天数，然后换算成秒 */
        long dayOfBegin = begin.getDayOfMonth();
        long dayOfEnd = end.getDayOfMonth();
        // 相差天数整数部分
        long days = dayOfEnd - dayOfBegin;
        // 换算成秒
        OraDecimal secondsByDays = OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL);
        // 汇总
        OraDecimal seconds = OraDecimal.valueOf(secondsByHours).add(secondsByDays);

        /* 根据Oracle数据库规则，一个月强行视为31天。这里用剩余时间的总秒数，除以一个月的总秒数 */
        // 非整数部分的月份数
        OraDecimal monthsFraction = seconds.divide(AviatorOracleConstants.SECONDS_OF_MONTH);

        /* 汇总计算结果并返回 */
        // 汇总相加后乘以正负号
        months = months.add(monthsFraction).multiply(sign);
        return months;
    }

    /**
     * 是否是所在月份的最后1填
     */
    private static boolean isLastDayOfMonth(ZonedDateTime date) {
        return date.getDayOfMonth() == date.toLocalDate().lengthOfMonth();
    }
}
