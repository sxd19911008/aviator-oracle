package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.constants.AviatorOracleConstants;
import com.eredar.aviatororacle.utils.AOUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

/**
 * 基于 {@link Instant} 实现Oracle数据库日期操作
 */
public class OracleInstantUtils {

    /**
     * 模拟 Oracle 数据库: Date对象 + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return Instant类型的日期对象
     */
    public static Instant instantPlusDays(Instant date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }

        // 计算总秒数
        long seconds = daysToSeconds(days);
        // 添加总秒数并返回新日期对象
        return date.plus(seconds, ChronoUnit.SECONDS);
    }

    /**
     * 模拟 Oracle 数据库: Date对象 - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return Instant类型的日期对象
     */
    public static Instant instantMinusDays(Instant date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }
        // 计算总秒数
        long seconds = daysToSeconds(days);
        // 添加总秒数并返回新日期对象
        return date.minus(seconds, ChronoUnit.SECONDS);
    }

    /**
     * 将天数换算成 {@code Long} 型的秒数
     * <p>必定返回整数，如果有小数部分，则四舍五入
     *
     * @param days 天数，可以带小数
     * @return 天数对应的秒数
     */
    private static long daysToSeconds(Number days) {
        if (days instanceof Long || days instanceof Integer || days instanceof BigInteger) {
            return days.longValue() * AviatorOracleConstants.SECONDS_OF_DAY_LONG;
        } else if (days instanceof OraDecimal) {
            return ((OraDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        } else if (days instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        } else {
            return OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        }
    }

    /**
     * 计算两个 {@code Instant} 之间的天数差 (date2 - date1)
     *
     * @param endDate   被减数 (结束时间)
     * @param beginDate 减数 (起始时间)
     * @return 差值天数 ({@code OraDecimal})
     */
    public static OraDecimal daysBetween(Instant endDate, Instant beginDate) {
        // 校验参数，为 null 直接报错
        if (endDate == null || beginDate == null) {
            throw new IllegalArgumentException(String.format("endDate[%s] and beginDate[%s] cannot be null", endDate, beginDate));
        }
        // 获取秒数
        long endSeconds = endDate.getEpochSecond();
        long beginSeconds = beginDate.getEpochSecond();
        // 获取总秒数差
        OraDecimal secondsDiff = OraDecimal.valueOf(endSeconds - beginSeconds);
        // 计算天数，Oracle日期相减场景违反正常的精度逻辑，强行保留40位小数
        return secondsDiff.divide(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL, 40);
        /*
         * 【存疑】计算逻辑为先按照 Oracle 的 number 类型四舍五入保留小数，再强行保留40位小数。
         * 无法验证 Oracle 处理方式是否相同，因为找不到合适的临界小数。
         * 由于 Oracle 计算时一定除以一天的秒数 86400，且 number 的无整数位的小数如果开头有0，
         * 一定是偶数0不占用数字位数（number是20位的内存单位，每一位内存单位代表2位数字），
         * 导致我用任何办法都无法找出临界数字。我尝试过 11、29 等数字，都不行。
         * 由于这里的精度差异可以忽略不计，且目前的逻辑未必错误，所以等遇到这个临界数字时再做处理。
         */
    }

    public static OraDecimal monthsBetween(Instant endDate, Instant beginDate) {
        return monthsBetween(endDate, beginDate, ZoneOffset.UTC);
    }

    /**
     * 2个日期之间间隔的月份
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @param zoneId    时区
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
     * 是否是所在月份的最后1天
     */
    private static boolean isLastDayOfMonth(ZonedDateTime date) {
        return date.getDayOfMonth() == date.toLocalDate().lengthOfMonth();
    }

    // =====================================================================
    //  ADD_MONTHS(date, months)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code ADD_MONTHS(date, months)} 函数：为日期加上指定月数。
     * <p>使用 UTC 时区。
     * <p>
     * Oracle 的 {@code ADD_MONTHS} 行为规则：
     * <ol>
     *   <li>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）</li>
     *   <li>如果原始日期是所在月份的最后一天，则结果一定是目标月份的最后一天
     *       （例如 2024-02-29 + 1个月 → 2024-03-31）</li>
     *   <li>如果原始日期的日号大于目标月份的最大天数，则结果为目标月份的最后一天
     *       （例如 2024-01-31 + 1个月 → 2024-02-29）</li>
     *   <li>时分秒及纳秒部分保持不变</li>
     *   <li>月数可以为负数，表示向前回退月份</li>
     * </ol>
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数（Number 类型），小数部分会被截断
     * @return 加上指定月数后的新 {@link Instant} 对象
     * @throws IllegalArgumentException 如果 {@code date} 或 {@code months} 为 {@code null}
     */
    public static Instant addMonths(Instant date, Number months) {
        return addMonths(date, months, ZoneOffset.UTC);
    }

    /**
     * 模拟 Oracle 数据库的 {@code ADD_MONTHS(date, months)} 函数：为日期加上指定月数。
     * <p>
     * Oracle 的 {@code ADD_MONTHS} 行为规则：
     * <ol>
     *   <li>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）</li>
     *   <li>如果原始日期是所在月份的最后一天，则结果一定是目标月份的最后一天
     *       （例如 2024-02-29 + 1个月 → 2024-03-31）</li>
     *   <li>如果原始日期的日号大于目标月份的最大天数，则结果为目标月份的最后一天
     *       （例如 2024-01-31 + 1个月 → 2024-02-29）</li>
     *   <li>时分秒及纳秒部分保持不变</li>
     *   <li>月数可以为负数，表示向前回退月份</li>
     * </ol>
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数（Number 类型），小数部分会被截断
     * @param zoneId 时区，不可为 {@code null}
     * @return 加上指定月数后的新 {@link Instant} 对象
     * @throws IllegalArgumentException 如果参数为 {@code null}
     */
    public static Instant addMonths(Instant date, Number months, ZoneId zoneId) {
        /* 入参校验 */
        if (date == null || months == null) {
            throw new IllegalArgumentException(
                    String.format("参数不能为空: date=%s; months=%s", date, months));
        }
        if (zoneId == null) {
            throw new IllegalArgumentException("时区zoneId可以不传入，如果传入不能为空");
        }

        // Oracle 会将 months 截断为整数（不是四舍五入）
        int monthsToAdd = months.intValue();

        // 为0不用计算，直接返回
        if (monthsToAdd == 0) {
            return date;
        }

        ZonedDateTime zdt = date.atZone(zoneId);

        // 记录原始日期的日号
        int originalDayOfMonth = zdt.getDayOfMonth();
        // 判断原始日期是否是所在月份的最后一天
        boolean wasLastDayOfMonth = isLastDayOfMonth(zdt);

        // 增加月份（plusMonths 会自动处理年份进位/借位）
        zdt = zdt.plusMonths(monthsToAdd);

        if (wasLastDayOfMonth) {
            // 规则2：原始日期是月末，结果强制设为目标月份的最后一天
            zdt = zdt.withDayOfMonth(zdt.toLocalDate().lengthOfMonth());
        } else {
            // 规则3：原始日号超过目标月的最大天数时，设为目标月的最后一天
            int maxDayOfTargetMonth = zdt.toLocalDate().lengthOfMonth();
            if (originalDayOfMonth > maxDayOfTargetMonth) {
                zdt = zdt.withDayOfMonth(maxDayOfTargetMonth);
            } else {
                // 正常情况：保持原始日号（plusMonths 可能会自动调整日号，需要还原）
                zdt = zdt.withDayOfMonth(originalDayOfMonth);
            }
        }

        return zdt.toInstant();
    }

    // =====================================================================
    //  LAST_DAY(date)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code LAST_DAY(date)} 函数：返回给定日期所在月份的最后一天。
     * <p>使用 UTC 时区。
     * <p>
     * Oracle 的 {@code LAST_DAY} 行为规则：
     * <ul>
     *   <li>返回日期所在月份的最后一天，会正确处理闰年（如2月28日/29日）</li>
     *   <li>时分秒及纳秒部分保持不变</li>
     * </ul>
     *
     * @param date 日期对象；不允许为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link Instant} 对象（保留原始时分秒及纳秒）
     * @throws IllegalArgumentException 如果 {@code date} 为 {@code null}
     */
    public static Instant lastDay(Instant date) {
        return lastDay(date, ZoneOffset.UTC);
    }

    /**
     * 模拟 Oracle 数据库的 {@code LAST_DAY(date)} 函数：返回给定日期所在月份的最后一天。
     * <p>
     * Oracle 的 {@code LAST_DAY} 行为规则：
     * <ul>
     *   <li>返回日期所在月份的最后一天，会正确处理闰年（如2月28日/29日）</li>
     *   <li>时分秒及纳秒部分保持不变</li>
     * </ul>
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param zoneId 时区，不可为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link Instant} 对象（保留原始时分秒及纳秒）
     * @throws IllegalArgumentException 如果参数为 {@code null}
     */
    public static Instant lastDay(Instant date, ZoneId zoneId) {
        /* 入参校验 */
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空: date=null");
        }
        if (zoneId == null) {
            throw new IllegalArgumentException("时区zoneId不能为空");
        }

        ZonedDateTime zdt = date.atZone(zoneId);

        // 将日号设为当月的最大天数（lengthOfMonth 会自动处理闰年和不同月份的天数差异）
        zdt = zdt.withDayOfMonth(zdt.toLocalDate().lengthOfMonth());

        return zdt.toInstant();
    }

    // =====================================================================
    //  TRUNC(date [, format])
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date)} 函数：将日期截断到天（当天午夜零点）。
     * <p>等价于 {@code TRUNC(date, 'DD')}，使用 UTC 时区。
     *
     * @param date 日期对象；为 {@code null} 时返回 {@code null}
     * @return 截断到天的新 {@link Instant} 对象
     */
    public static Instant truncInstant(Instant date) {
        return truncInstantWithZone(date, ZoneOffset.UTC);
    }

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date, format)} 函数：按指定格式模型截断日期，使用 UTC 时区。
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写）
     * @return 截断后的新 {@link Instant} 对象
     */
    public static Instant truncInstant(Instant date, String format) {
        return truncInstantWithZone(date, format, ZoneOffset.UTC);
    }

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date)} 函数：将日期截断到天（当天午夜零点）。
     * <p>等价于 {@code TRUNC(date, 'DD')}
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param zoneId 时区，不可为 {@code null}
     * @return 截断到天的新 {@link Instant} 对象
     */
    public static Instant truncInstantWithZone(Instant date, ZoneId zoneId) {
        return truncInstantWithZone(date, "DD", zoneId);
    }

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date, format)} 函数：按指定格式模型截断日期。
     * <p>
     * 支持的格式模型（不区分大小写）：
     * <ul>
     *   <li>{@code CC / SCC}：世纪——截断到当前世纪首年的 1 月 1 日（如 2026 年 → 2001-01-01）</li>
     *   <li>{@code SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y}：年——截断到当年 1 月 1 日</li>
     *   <li>{@code IYYY / IYY / IY / I}：ISO 年——截断到 ISO 第1周的周一</li>
     *   <li>{@code Q}：季度——截断到本季度第1天</li>
     *   <li>{@code MONTH / MON / MM / RM}：月——截断到本月第1天</li>
     *   <li>{@code WW}：年内周——截断到与本年 1 月 1 日同星期的最近日期</li>
     *   <li>{@code W}：月内周——截断到与本月 1 日同星期的最近日期</li>
     *   <li>{@code IW}：ISO 周——截断到 ISO 周的第一天（周一）</li>
     *   <li>{@code DDD / DD / J}：天——截断到当天零点（默认）</li>
     *   <li>{@code DAY / DY / D}：周——截断到本周第一天（Oracle 以周日为一周起始）</li>
     *   <li>{@code HH / HH12 / HH24}：小时——分钟与秒清零</li>
     *   <li>{@code MI}：分钟——秒清零</li>
     * </ul>
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写）；为 {@code null} 或空串时等价于 {@code "DD"}
     * @param zoneId 时区，不可为 {@code null}
     * @return 截断后的新 {@link Instant} 对象
     * @throws IllegalArgumentException 如果 {@code zoneId} 为 {@code null}，或 {@code format} 是不支持的格式模型
     */
    public static Instant truncInstantWithZone(Instant date, String format, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        if (zoneId == null) {
            throw new IllegalArgumentException("时区zoneId不能为空");
        }
        if (AOUtils.isBlank(format)) {
            throw new IllegalArgumentException("format不能为空");
        }
        // 转大写，统一处理
        String fmtUpper = format.trim().toUpperCase();
        // 将 Instant 转换为带时区的日期时间对象，便于按日历字段操作
        ZonedDateTime zdt = date.atZone(zoneId);

        switch (fmtUpper) {

            /* ---- 世纪 ---- */
            case "CC":
            case "SCC": {
                int year = zdt.getYear();
                // Oracle 世纪从 1, 101, 201, ..., 1901, 2001 ... 开始
                // 公式：世纪首年 = floor((year - 1) / 100) * 100 + 1
                int centuryStartYear = ((year - 1) / 100) * 100 + 1;
                zdt = ZonedDateTime.of(centuryStartYear, 1, 1, 0, 0, 0, 0, zoneId);
                break;
            }

            /* ---- 年 ---- */
            case "SYYYY":
            case "YYYY":
            case "YEAR":
            case "SYEAR":
            case "YYY":
            case "YY":
            case "Y":
                // 截断到当年 1 月 1 日 00:00:00
                zdt = ZonedDateTime.of(zdt.getYear(), 1, 1, 0, 0, 0, 0, zoneId);
                break;

            /* ---- ISO 年 ---- */
            case "IYYY":
            case "IYY":
            case "IY":
            case "I":
                // 截断到 ISO 年第1周的周一
                zdt = truncZdtToIsoYear(zdt);
                break;

            /* ---- 季度 ---- */
            case "Q": {
                // getMonthValue() 返回 1~12，季度首月为 1、4、7、10
                int month = zdt.getMonthValue();
                int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
                zdt = ZonedDateTime.of(zdt.getYear(), quarterStartMonth, 1, 0, 0, 0, 0, zoneId);
                break;
            }

            /* ---- 月 ---- */
            case "MONTH":
            case "MON":
            case "MM":
            case "RM":
                // 截断到本月第1天 00:00:00
                zdt = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), 1, 0, 0, 0, 0, zoneId);
                break;

            /* ---- 年内周 (WW)：与本年 1 月 1 日同星期的最近日期 ---- */
            case "WW": {
                // getDayOfYear() 从 1 起，减 1 得到 0-indexed 的年内偏移天数
                // 该偏移对 7 取余，即为距上一个与 1 月 1 日同星期日期的天数差
                int dayOfYearOffset = zdt.getDayOfYear() - 1;
                int daysBack = dayOfYearOffset % 7;
                zdt = zdt.minusDays(daysBack).truncatedTo(ChronoUnit.DAYS);
                break;
            }

            /* ---- 月内周 (W)：与本月 1 日同星期的最近日期 ---- */
            case "W": {
                // getDayOfMonth() 从 1 起，减 1 得到 0-indexed 的月内偏移天数
                int dayOfMonthOffset = zdt.getDayOfMonth() - 1;
                int daysBack = dayOfMonthOffset % 7;
                zdt = zdt.minusDays(daysBack).truncatedTo(ChronoUnit.DAYS);
                break;
            }

            /* ---- ISO 周 (IW)：ISO 周的第一天（周一）---- */
            case "IW":
                // ISO 8601 规定 ISO 周从周一开始，回退到最近的周一（含当天）
                zdt = zdt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                         .truncatedTo(ChronoUnit.DAYS);
                break;

            /* ---- 天（默认）---- */
            case "DDD":
            case "DD":
            case "J":
                zdt = zdt.truncatedTo(ChronoUnit.DAYS);
                break;

            /* ---- 周（Oracle 以周日作为一周的第一天）---- */
            case "DAY":
            case "DY":
            case "D":
                // Oracle 以周日为一周起始，回退到最近的周日（含当天）
                zdt = zdt.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                         .truncatedTo(ChronoUnit.DAYS);
                break;

            /* ---- 小时 ---- */
            case "HH":
            case "HH12":
            case "HH24":
                // 保留小时，将分钟、秒、纳秒清零
                zdt = zdt.truncatedTo(ChronoUnit.HOURS);
                break;

            /* ---- 分钟 ---- */
            case "MI":
                // 保留分钟，将秒和纳秒清零
                zdt = zdt.truncatedTo(ChronoUnit.MINUTES);
                break;

            default:
                throw new IllegalArgumentException(String.format("trunc方法不支持传入format为[%s]", format));
        }

        // 将带时区的日期时间转回 Instant
        return zdt.toInstant();
    }

    /**
     * 截断到 ISO 年的第一天（ISO 第1周的周一）。
     * <p>
     * ISO 8601 规定：包含该年第一个周四的那一周是第1周，且 ISO 周以周一为起始。
     * 因此 ISO 年的第一天可能早于日历年的 1 月 1 日（最多早 3 天）。
     * <p>
     * 算法：
     * <ol>
     *   <li>通过 {@link IsoFields#WEEK_BASED_YEAR} 字段获取 ISO 年编号</li>
     *   <li>取该 ISO 年的 1 月 4 日——根据 ISO 8601，1 月 4 日必在第1周内</li>
     *   <li>回退到该周的周一，即为 ISO 年的第一天</li>
     * </ol>
     */
    private static ZonedDateTime truncZdtToIsoYear(ZonedDateTime zdt) {
        // 获取 ISO 年编号（在 1 月初或 12 月末可能与日历年不同）
        int isoYear = zdt.get(IsoFields.WEEK_BASED_YEAR);
        // ISO 第1周必包含 isoYear 年的 1 月 4 日
        ZonedDateTime jan4 = ZonedDateTime.of(isoYear, 1, 4, 0, 0, 0, 0, zdt.getZone());
        // DayOfWeek.getValue() 返回 ISO 编号：周一=1, ..., 周日=7
        // 回退 (isoDayOfWeek - 1) 天即可到达本周周一
        int isoDayOfWeek = jan4.getDayOfWeek().getValue();
        return jan4.minusDays(isoDayOfWeek - 1);
    }
}
