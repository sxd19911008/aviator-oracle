package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.constants.AviatorOracleConstants;
import com.eredar.aviatororacle.utils.AOUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

/**
 * 基于 {@link LocalDateTime} 实现Oracle数据库日期操作
 */
public class OracleLocalDateTimeUtils {

    /**
     * 模拟 Oracle 数据库: Date对象 + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link LocalDateTime} 类型的日期对象
     */
    protected static LocalDateTime localDateTimePlusDays(LocalDateTime date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }
        return date.plusSeconds(daysToSeconds(days));
    }

    /**
     * 模拟 Oracle 数据库: Date对象 - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link LocalDateTime} 类型的日期对象
     */
    protected static LocalDateTime localDateTimeMinusDays(LocalDateTime date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }
        return date.minusSeconds(daysToSeconds(days));
    }

    /**
     * 将天数换算成 {@code Long} 型的秒数
     * <p>必定返回整数，如果有小数部分，则四舍五入
     *
     * @param days 天数，可以带小数
     * @return 天数对应的秒数
     */
    private static long daysToSeconds(Number days) {
        // 整数类型直接乘，避免 OraDecimal 构造开销
        if (days instanceof Long || days instanceof Integer || days instanceof BigInteger) {
            return days.longValue() * AviatorOracleConstants.SECONDS_OF_DAY_LONG;
        } else if (days instanceof OraDecimal) {
            return ((OraDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL)
                    .setScale(0)
                    .longValueExact();
        } else if (days instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL)
                    .setScale(0)
                    .longValueExact();
        } else {
            return OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL)
                    .setScale(0)
                    .longValueExact();
        }
    }

    /**
     * 计算两个 {@link LocalDateTime} 之间的天数差 (endDate - beginDate)
     *
     * @param endDate   被减数 (结束时间)
     * @param beginDate 减数 (起始时间)
     * @return 差值天数 ({@code OraDecimal})
     */
    protected static OraDecimal daysBetween(LocalDateTime endDate, LocalDateTime beginDate) {
        if (endDate == null || beginDate == null) {
            throw new IllegalArgumentException(String.format("endDate[%s] and beginDate[%s] cannot be null", endDate, beginDate));
        }
        // ChronoUnit.SECONDS.between 直接计算两个 LocalDateTime 之间的总秒数差，无需时区转换
        long secondsDiff = ChronoUnit.SECONDS.between(beginDate, endDate);
        // 计算天数，Oracle日期相减场景违反正常的精度逻辑，强行保留40位小数
        return OraDecimal.valueOf(secondsDiff).divide(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL, 40);
        /*
         * 【存疑】计算逻辑为先按照 Oracle 的 number 类型四舍五入保留小数，再强行保留40位小数。
         * 无法验证 Oracle 处理方式是否相同，因为找不到合适的临界小数。
         * 由于 Oracle 计算时一定除以一天的秒数 86400，且 number 的无整数位的小数如果开头有0，
         * 一定是偶数0不占用数字位数（number是20位的内存单位，每一位内存单位代表2位数字），
         * 导致我用任何办法都无法找出临界数字。我尝试过 11、29 等数字，都不行。
         * 由于这里的精度差异可以忽略不计，且目前的逻辑未必错误，所以等遇到这个临界数字时再做处理。
         */
    }

    /**
     * 2个日期之间间隔的月份
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @return 间隔月份
     */
    protected static OraDecimal monthsBetween(LocalDateTime endDate, LocalDateTime beginDate) {
        /* 入参校验 */
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }

        // 支持负数计算：确保 begin <= end，若反向则最终结果取反
        OraDecimal sign = OraDecimal.ONE;
        if (beginDate.isAfter(endDate)) {
            LocalDateTime temp = beginDate;
            beginDate = endDate;
            endDate = temp;
            sign = AviatorOracleConstants.NEG;
        }

        /* 计算基础月份差 (年差 * 12 + 月差) */
        int totalMonths = (endDate.getYear() - beginDate.getYear()) * 12
                + (endDate.getMonthValue() - beginDate.getMonthValue());
        OraDecimal months = OraDecimal.valueOf(totalMonths);

        /* 判断是否"同日"或"均为月末" */
        boolean sameDayOfMonth = beginDate.getDayOfMonth() == endDate.getDayOfMonth();
        boolean bothLastDayOfMonth = isLastDayOfMonth(beginDate) && isLastDayOfMonth(endDate);
        if (sameDayOfMonth || bothLastDayOfMonth) {
            return months.multiply(sign);
        }

        // 不满足上述条件，需要计算小数部分

        /* 计算时分秒各自折算成的秒数之差 */
        long secondsByHours = endDate.toLocalTime().toSecondOfDay()
                - beginDate.toLocalTime().toSecondOfDay();

        /* 计算日期差，然后换算成秒 */
        long days = endDate.getDayOfMonth() - beginDate.getDayOfMonth();
        OraDecimal secondsByDays = OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL);
        // 汇总时间差秒数（时分秒差 + 天数差换算成的秒数）
        OraDecimal seconds = OraDecimal.valueOf(secondsByHours).add(secondsByDays);

        /* 根据Oracle数据库规则，一个月强行视为31天。这里用剩余时间的总秒数，除以一个月的总秒数 */
        OraDecimal monthsFraction = seconds.divide(AviatorOracleConstants.SECONDS_OF_MONTH);

        /* 汇总计算结果并返回 */
        return months.add(monthsFraction).multiply(sign);
    }

    /**
     * 判断 {@link LocalDateTime} 是否是所在月份的最后一天
     */
    private static boolean isLastDayOfMonth(LocalDateTime date) {
        // lengthOfMonth() 返回该月总天数（自动处理闰年），与当前日号比较即可判断是否月末
        return date.getDayOfMonth() == date.toLocalDate().lengthOfMonth();
    }

    // =====================================================================
    //  ADD_MONTHS(date, months)
    // =====================================================================

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
     * @return 加上指定月数后的新 {@link LocalDateTime} 对象
     * @throws IllegalArgumentException 如果 {@code date} 或 {@code months} 为 {@code null}
     */
    protected static LocalDateTime addMonths(LocalDateTime date, Number months) {
        /* 入参校验 */
        if (date == null || months == null) {
            throw new IllegalArgumentException(
                    String.format("参数不能为空: date=%s; months=%s", date, months));
        }

        // Oracle 会将 months 截断为整数（不是四舍五入）
        int monthsToAdd = months.intValue();

        if (monthsToAdd == 0) {
            return date;
        }

        int originalDayOfMonth = date.getDayOfMonth();
        boolean wasLastDayOfMonth = isLastDayOfMonth(date);

        // plusMonths 自动处理年份进位/借位，并在日号溢出时调整为目标月的最后一天
        LocalDateTime result = date.plusMonths(monthsToAdd);
        int maxDayOfTargetMonth = result.toLocalDate().lengthOfMonth();

        if (wasLastDayOfMonth) {
            // 规则2：原始日期是月末，结果强制设为目标月份的最后一天
            result = result.withDayOfMonth(maxDayOfTargetMonth);
        } else if (originalDayOfMonth > maxDayOfTargetMonth) {
            // 规则3：原始日号超过目标月的最大天数时，设为目标月的最后一天
            result = result.withDayOfMonth(maxDayOfTargetMonth);
        } else {
            // 正常情况：保持原始日号（plusMonths 可能会自动调整日号，需要还原）
            result = result.withDayOfMonth(originalDayOfMonth);
        }

        return result;
    }

    // =====================================================================
    //  LAST_DAY(date)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code LAST_DAY(date)} 函数：返回给定日期所在月份的最后一天。
     * <p>
     * Oracle 的 {@code LAST_DAY} 行为规则：
     * <ul>
     *   <li>返回日期所在月份的最后一天，会正确处理闰年（如2月28日/29日）</li>
     *   <li>时分秒及纳秒部分保持不变</li>
     * </ul>
     *
     * @param date 日期对象；不允许为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link LocalDateTime} 对象（保留原始时分秒及纳秒）
     * @throws IllegalArgumentException 如果 {@code date} 为 {@code null}
     */
    protected static LocalDateTime lastDay(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空: date=null");
        }
        // with(TemporalAdjusters.lastDayOfMonth()) 直接返回当月最后一天，保留时分秒
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    // =====================================================================
    //  TRUNC(date [, format])
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date)} 函数：将日期截断到天（当天午夜零点）。
     * <p>等价于 {@code TRUNC(date, 'DD')}
     *
     * @param date 日期对象；为 {@code null} 时返回 {@code null}
     * @return 截断到天的新 {@link LocalDateTime} 对象
     */
    protected static LocalDateTime truncLocalDateTime(LocalDateTime date) {
        return truncLocalDateTime(date, "DD");
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
     * @param format 格式模型（不区分大小写）；为 {@code null} 或空串时抛出异常
     * @return 截断后的新 {@link LocalDateTime} 对象
     * @throws IllegalArgumentException 如果 {@code format} 为空或不支持的格式模型
     */
    protected static LocalDateTime truncLocalDateTime(LocalDateTime date, String format) {
        if (date == null) {
            return null;
        }
        if (AOUtils.isBlank(format)) {
            throw new IllegalArgumentException("");
        }
        String fmtUpper = format.trim().toUpperCase();

        switch (fmtUpper) {

            /* ---- 世纪 ---- */
            case "CC":
            case "SCC": {
                int year = date.getYear();
                // Oracle 世纪从 1, 101, 201, ..., 1901, 2001 ... 开始
                int centuryStartYear = ((year - 1) / 100) * 100 + 1;
                return LocalDateTime.of(centuryStartYear, 1, 1, 0, 0);
            }

            /* ---- 年 ---- */
            case "SYYYY":
            case "YYYY":
            case "YEAR":
            case "SYEAR":
            case "YYY":
            case "YY":
            case "Y":
                return LocalDateTime.of(date.getYear(), 1, 1, 0, 0);

            /* ---- ISO 年 ---- */
            case "IYYY":
            case "IYY":
            case "IY":
            case "I":
                return truncToIsoYear(date);

            /* ---- 季度 ---- */
            case "Q": {
                // getMonthValue() 返回 1~12，季度首月为 1、4、7、10
                int quarterStartMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
                return LocalDateTime.of(date.getYear(), quarterStartMonth, 1, 0, 0);
            }

            /* ---- 月 ---- */
            case "MONTH":
            case "MON":
            case "MM":
            case "RM":
                return LocalDateTime.of(date.getYear(), date.getMonthValue(), 1, 0, 0);

            /* ---- 年内周 (WW)：与本年 1 月 1 日同星期的最近日期 ---- */
            case "WW": {
                int daysBack = (date.getDayOfYear() - 1) % 7;
                return date.minusDays(daysBack).truncatedTo(ChronoUnit.DAYS);
            }

            /* ---- 月内周 (W)：与本月 1 日同星期的最近日期 ---- */
            case "W": {
                int daysBack = (date.getDayOfMonth() - 1) % 7;
                return date.minusDays(daysBack).truncatedTo(ChronoUnit.DAYS);
            }

            /* ---- ISO 周 (IW)：ISO 周的第一天（周一）---- */
            case "IW":
                return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                           .truncatedTo(ChronoUnit.DAYS);

            /* ---- 天（默认）---- */
            case "DDD":
            case "DD":
            case "J":
                return date.truncatedTo(ChronoUnit.DAYS);

            /* ---- 周（Oracle 以周日作为一周的第一天）---- */
            case "DAY":
            case "DY":
            case "D":
                return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                           .truncatedTo(ChronoUnit.DAYS);

            /* ---- 小时 ---- */
            case "HH":
            case "HH12":
            case "HH24":
                return date.truncatedTo(ChronoUnit.HOURS);

            /* ---- 分钟 ---- */
            case "MI":
                return date.truncatedTo(ChronoUnit.MINUTES);

            default:
                throw new IllegalArgumentException(String.format("trunc方法不支持传入format为[%s]", format));
        }
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
    private static LocalDateTime truncToIsoYear(LocalDateTime date) {
        int isoYear = date.get(IsoFields.WEEK_BASED_YEAR);
        // ISO 第1周必包含 isoYear 年的 1 月 4 日
        LocalDate jan4 = LocalDate.of(isoYear, 1, 4);
        // DayOfWeek.getValue() 返回 ISO 编号：周一=1, ..., 周日=7，回退到本周周一
        LocalDate isoYearStart = jan4.minusDays(jan4.getDayOfWeek().getValue() - 1L);
        return isoYearStart.atStartOfDay();
    }
}
