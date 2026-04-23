package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;

import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Oracle 函数统一入口工具类。
 */
public class OraFuncUtils {



    /**
     * 模拟 Oracle 数据库的 DECODE 函数实现。
     * <p>
     * <b>语法：</b><br/>
     * {@code decode(expression, search1, result1, search2, result2, ..., [default])}
     * </p>
     * <p>
     * <b>功能说明：</b><br/>
     * 该函数逐个比较 {@code expression} 和 {@code search}。
     * 如果 {@code expression} 等于某个 {@code search}，则返回对应的 {@code result}。
     * 如果没有匹配项，则返回 {@code default}。
     * 如果没有匹配项且未指定 {@code default}，则返回 {@code null}。
     * </p>
     * <p>
     * <b>特殊处理：</b><br/>
     * 1. <b>Null 值比较：</b> 与 Oracle 的 DECODE 一致，本方法认为 {@code null} 等于 {@code null}。<br/>
     * 2. <b>数值比较：</b> 内部使用 {@link OraDecimal} 进行数值比较，以确保不同类型数字（如 Integer、Long、OraDecimal）在数值相等时能正确匹配。
     * </p>
     *
     * @param args 变长参数。
     *             args[0]: 待比较的表达式。
     *             args[1, 2, ...]: 成对出现的 search 和 result。
     *             最后一位（可选）: 默认值（当参数总数为偶数时存在）。
     * @return 匹配到的结果对象，或者默认值，或者 null。
     */
    public static Object decode(Object... args) {
        return OracleConditionalFunctionUtils.decode(args);
    }

    /**
     * 取整数，小数位直接舍去
     */
    public static Number floor(Number n) {
        return OracleNumberFunctionUtils.floor(n);
    }

    /**
     * 模拟 Oracle {@code CEIL(n)}：返回大于或等于 {@code n} 的最小整数（向正无穷方向取整）。
     * <p>{@link java.math.RoundingMode#CEILING} 一致：正数小数部分进位，负数向零靠近（例如 {@code ceil(-2.1) = -2}）。
     *
     * @param n 目标数字；为 {@code null} 时返回 {@code null}
     * @return 上取整后的数字
     */
    public static Number ceil(Number n) {
        return OracleNumberFunctionUtils.ceil(n);
    }

    /**
     * 模拟 Oracle {@code ROUND(number)}：四舍五入保留整数，等价于 {@link #round(Number, Number) round(n, 0)}。
     *
     * @param n 待舍入的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    public static Number round(Number n) {
        return OracleNumberFunctionUtils.round(n);
    }

    /**
     * 模拟 Oracle {@code ROUND(number, integer)}：按指定位数四舍五入（{@link RoundingMode#HALF_UP}）。
     * <p>{@code newScale > 0} 表示保留的小数位数；
     * <p>{@code newScale = 0} 表示保留整数；
     * <p>{@code newScale < 0} 表示在小数点左侧按数量级舍入（如 -1 表示十位）。
     *
     * @param number   待舍入的值；为 {@code null} 时返回 {@code null}
     * @param newScale 目标标度（可为负）
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    public static Number round(Number number, Number newScale) {
        return OracleNumberFunctionUtils.round(number, newScale);
    }

    /**
     * 模拟 Oracle {@code TRUNC(number)}：向零方向截断，等价于 {@link #trunc(Number, Number) truncDate(n, 0)}。
     * <p>与 {@link #floor(Number) floor} 的区别：{@code floor} 向负无穷方向取整，而 {@code truncDate} 向零方向截断。
     * 例如 {@code truncDate(-2.9) = -2}，而 {@code floor(-2.9) = -3}。
     *
     * @param number 待截断的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code n} 本身
     */
    public static Number trunc(Number number) {
        return OracleNumberFunctionUtils.truncNumber(number, 0);
    }

    /**
     * 模拟 Oracle {@code TRUNC(number, integer)}：按指定位数向零方向截断（{@link RoundingMode#DOWN}）。
     * <p>{@code newScale > 0} 表示保留的小数位数，多余部分直接丢弃；
     * <p>{@code newScale = 0} 表示仅保留整数部分；
     * <p>{@code newScale < 0} 表示在小数点左侧按数量级截断（如 -1 对十位截断）。
     * <p>示例：
     * <pre>
     *   truncDate(15.79)      = 15
     *   truncDate(15.79,  1)  = 15.7
     *   truncDate(15.79, -1)  = 10
     *   truncDate(-2.9,   0)  = -2   （向零，非向负无穷）
     * </pre>
     *
     * @param number   待截断的值；为 {@code null} 时返回 {@code null}
     * @param newScale 目标标度（可为负）；支持 Long、Integer、Double、BigInteger、BigDecimal、OraDecimal
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    public static Number trunc(Number number, Number newScale) {
        return OracleNumberFunctionUtils.truncNumber(number, newScale);
    }

    // =====================================================================
    //  日期 + 数字 (Oracle: date + number)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库: {@link Date} + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link Date} 类型的日期对象
     * @see OracleDateUtils#datePlusDays(Date, Number)
     */
    public static Date datePlusDays(Date date, Number days) {
        return OracleDateUtils.datePlusDays(date, days);
    }

    /**
     * 模拟 Oracle 数据库: {@link LocalDateTime} + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link LocalDateTime} 类型的日期对象
     * @see OracleLocalDateTimeUtils#localDateTimePlusDays(LocalDateTime, Number)
     */
    public static LocalDateTime datePlusDays(LocalDateTime date, Number days) {
        return OracleLocalDateTimeUtils.localDateTimePlusDays(date, days);
    }

    /**
     * 模拟 Oracle 数据库: {@link Instant} + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link Instant} 类型的日期对象
     * @see OracleInstantUtils#instantPlusDays(Instant, Number)
     */
    public static Instant datePlusDays(Instant date, Number days) {
        return OracleInstantUtils.instantPlusDays(date, days);
    }

    // =====================================================================
    //  日期 - 数字 (Oracle: date - number)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库: {@link Date} - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link Date} 类型的日期对象
     * @see OracleDateUtils#dateMinusDays(Date, Number)
     */
    public static Date dateMinusDays(Date date, Number days) {
        return OracleDateUtils.dateMinusDays(date, days);
    }

    /**
     * 模拟 Oracle 数据库: {@link LocalDateTime} - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link LocalDateTime} 类型的日期对象
     * @see OracleLocalDateTimeUtils#localDateTimeMinusDays(LocalDateTime, Number)
     */
    public static LocalDateTime dateMinusDays(LocalDateTime date, Number days) {
        return OracleLocalDateTimeUtils.localDateTimeMinusDays(date, days);
    }

    /**
     * 模拟 Oracle 数据库: {@link Instant} - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link Instant} 类型的日期对象
     * @see OracleInstantUtils#instantMinusDays(Instant, Number)
     */
    public static Instant dateMinusDays(Instant date, Number days) {
        return OracleInstantUtils.instantMinusDays(date, days);
    }

    // =====================================================================
    //  日期 - 日期 = 天数差 (Oracle: date - date)
    // =====================================================================

    /**
     * 计算两个 {@link Date} 之间的天数差 (endDate - beginDate)
     *
     * @param endDate   被减数 (结束时间)
     * @param beginDate 减数 (起始时间)
     * @return 差值天数 ({@code OraDecimal})
     * @see OracleDateUtils#daysBetween(Date, Date)
     */
    public static OraDecimal daysBetween(Date endDate, Date beginDate) {
        return OracleDateUtils.daysBetween(endDate, beginDate);
    }

    /**
     * 计算两个 {@link LocalDateTime} 之间的天数差 (endDate - beginDate)
     *
     * @param endDate   被减数 (结束时间)
     * @param beginDate 减数 (起始时间)
     * @return 差值天数 ({@code OraDecimal})
     * @see OracleLocalDateTimeUtils#daysBetween(LocalDateTime, LocalDateTime)
     */
    public static OraDecimal daysBetween(LocalDateTime endDate, LocalDateTime beginDate) {
        return OracleLocalDateTimeUtils.daysBetween(endDate, beginDate);
    }

    /**
     * 计算两个 {@link Instant} 之间的天数差 (endDate - beginDate)
     *
     * @param endDate   被减数 (结束时间)
     * @param beginDate 减数 (起始时间)
     * @return 差值天数 ({@code OraDecimal})
     * @see OracleInstantUtils#daysBetween(Instant, Instant)
     */
    public static OraDecimal daysBetween(Instant endDate, Instant beginDate) {
        return OracleInstantUtils.daysBetween(endDate, beginDate);
    }

    // =====================================================================
    //  MONTHS_BETWEEN(date1, date2)
    // =====================================================================

    /**
     * 模拟 Oracle {@code MONTHS_BETWEEN}：计算两个 {@link Date} 之间间隔的月份数
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @return 间隔月份 ({@code OraDecimal})
     * @see OracleDateUtils#monthsBetween(Date, Date)
     */
    public static OraDecimal monthsBetween(Date endDate, Date beginDate) {
        return OracleDateUtils.monthsBetween(endDate, beginDate);
    }

    /**
     * 模拟 Oracle {@code MONTHS_BETWEEN}：计算两个 {@link LocalDateTime} 之间间隔的月份数
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @return 间隔月份 ({@code OraDecimal})
     * @see OracleLocalDateTimeUtils#monthsBetween(LocalDateTime, LocalDateTime)
     */
    public static OraDecimal monthsBetween(LocalDateTime endDate, LocalDateTime beginDate) {
        return OracleLocalDateTimeUtils.monthsBetween(endDate, beginDate);
    }

    /**
     * 模拟 Oracle {@code MONTHS_BETWEEN}：计算两个 {@link Instant} 之间间隔的月份数（使用 UTC 时区）
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @return 间隔月份 ({@code OraDecimal})
     * @see OracleInstantUtils#monthsBetween(Instant, Instant)
     */
    public static OraDecimal monthsBetween(Instant endDate, Instant beginDate) {
        return OracleInstantUtils.monthsBetween(endDate, beginDate);
    }

    /**
     * 模拟 Oracle {@code MONTHS_BETWEEN}：计算两个 {@link Instant} 之间间隔的月份数（指定时区）
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @param zoneId    时区
     * @return 间隔月份 ({@code OraDecimal})
     * @see OracleInstantUtils#monthsBetween(Instant, Instant, ZoneId)
     */
    public static OraDecimal monthsBetween(Instant endDate, Instant beginDate, ZoneId zoneId) {
        return OracleInstantUtils.monthsBetween(endDate, beginDate, zoneId);
    }

    // =====================================================================
    //  ADD_MONTHS(date, months)
    // =====================================================================

    /**
     * 模拟 Oracle {@code ADD_MONTHS}：为 {@link Date} 加上指定月数
     * <p>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数，小数部分会被截断
     * @return 加上指定月数后的新 {@link Date} 对象
     * @see OracleDateUtils#addMonths(Date, Number)
     */
    public static Date addMonths(Date date, Number months) {
        return OracleDateUtils.addMonths(date, months);
    }

    /**
     * 模拟 Oracle {@code ADD_MONTHS}：为 {@link LocalDateTime} 加上指定月数
     * <p>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数，小数部分会被截断
     * @return 加上指定月数后的新 {@link LocalDateTime} 对象
     * @see OracleLocalDateTimeUtils#addMonths(LocalDateTime, Number)
     */
    public static LocalDateTime addMonths(LocalDateTime date, Number months) {
        return OracleLocalDateTimeUtils.addMonths(date, months);
    }

    /**
     * 模拟 Oracle {@code ADD_MONTHS}：为 {@link Instant} 加上指定月数（使用 UTC 时区）
     * <p>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数，小数部分会被截断
     * @return 加上指定月数后的新 {@link Instant} 对象
     * @see OracleInstantUtils#addMonths(Instant, Number)
     */
    public static Instant addMonths(Instant date, Number months) {
        return OracleInstantUtils.addMonths(date, months);
    }

    /**
     * 模拟 Oracle {@code ADD_MONTHS}：为 {@link Instant} 加上指定月数（指定时区）
     * <p>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数，小数部分会被截断
     * @param zoneId 时区，不可为 {@code null}
     * @return 加上指定月数后的新 {@link Instant} 对象
     * @see OracleInstantUtils#addMonths(Instant, Number, ZoneId)
     */
    public static Instant addMonths(Instant date, Number months, ZoneId zoneId) {
        return OracleInstantUtils.addMonths(date, months, zoneId);
    }

    // =====================================================================
    //  LAST_DAY(date)
    // =====================================================================

    /**
     * 模拟 Oracle {@code LAST_DAY}：返回 {@link Date} 所在月份的最后一天
     *
     * @param date 日期对象；不允许为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link Date} 对象（保留原始时分秒）
     * @see OracleDateUtils#lastDay(Date)
     */
    public static Date lastDay(Date date) {
        return OracleDateUtils.lastDay(date);
    }

    /**
     * 模拟 Oracle {@code LAST_DAY}：返回 {@link LocalDateTime} 所在月份的最后一天
     *
     * @param date 日期对象；不允许为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link LocalDateTime} 对象（保留原始时分秒及纳秒）
     * @see OracleLocalDateTimeUtils#lastDay(LocalDateTime)
     */
    public static LocalDateTime lastDay(LocalDateTime date) {
        return OracleLocalDateTimeUtils.lastDay(date);
    }

    /**
     * 模拟 Oracle {@code LAST_DAY}：返回 {@link Instant} 所在月份的最后一天（使用 UTC 时区）
     *
     * @param date 日期对象；不允许为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link Instant} 对象（保留原始时分秒及纳秒）
     * @see OracleInstantUtils#lastDay(Instant)
     */
    public static Instant lastDay(Instant date) {
        return OracleInstantUtils.lastDay(date);
    }

    /**
     * 模拟 Oracle {@code LAST_DAY}：返回 {@link Instant} 所在月份的最后一天（指定时区）
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param zoneId 时区，不可为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link Instant} 对象（保留原始时分秒及纳秒）
     * @see OracleInstantUtils#lastDay(Instant, ZoneId)
     */
    public static Instant lastDay(Instant date, ZoneId zoneId) {
        return OracleInstantUtils.lastDay(date, zoneId);
    }

    // =====================================================================
    //  TRUNC(date [, format]) — 日期截断
    // =====================================================================

    /**
     * 模拟 Oracle {@code TRUNC(date)}：将 {@link Date} 截断到天（当天午夜零点）
     *
     * @param date 日期对象；为 {@code null} 时返回 {@code null}
     * @return 截断到天的新 {@link Date} 对象
     * @see OracleDateUtils#truncDate(Date)
     */
    public static Date trunc(Date date) {
        return OracleDateUtils.truncDate(date);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date, format)}：按指定格式模型截断 {@link Date}
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写），如 CC、YYYY、Q、MM、DD、HH、MI 等
     * @return 截断后的新 {@link Date} 对象
     * @see OracleDateUtils#truncDate(Date, String)
     */
    public static Date trunc(Date date, String format) {
        return OracleDateUtils.truncDate(date, format);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date)}：将 {@link LocalDateTime} 截断到天（当天午夜零点）
     *
     * @param date 日期对象；为 {@code null} 时返回 {@code null}
     * @return 截断到天的新 {@link LocalDateTime} 对象
     * @see OracleLocalDateTimeUtils#truncLocalDateTime(LocalDateTime)
     */
    public static LocalDateTime trunc(LocalDateTime date) {
        return OracleLocalDateTimeUtils.truncLocalDateTime(date);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date, format)}：按指定格式模型截断 {@link LocalDateTime}
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写），如 CC、YYYY、Q、MM、DD、HH、MI 等
     * @return 截断后的新 {@link LocalDateTime} 对象
     * @see OracleLocalDateTimeUtils#truncLocalDateTime(LocalDateTime, String)
     */
    public static LocalDateTime trunc(LocalDateTime date, String format) {
        return OracleLocalDateTimeUtils.truncLocalDateTime(date, format);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date)}：将 {@link Instant} 截断到天（使用 UTC 时区）
     *
     * @param date 日期对象；为 {@code null} 时返回 {@code null}
     * @return 截断到天的新 {@link Instant} 对象
     * @see OracleInstantUtils#truncInstant(Instant)
     */
    public static Instant trunc(Instant date) {
        return OracleInstantUtils.truncInstant(date);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date, format)}：按指定格式模型截断 {@link Instant}（使用 UTC 时区）
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写），如 CC、YYYY、Q、MM、DD、HH、MI 等
     * @return 截断后的新 {@link Instant} 对象
     * @see OracleInstantUtils#truncInstant(Instant, String)
     */
    public static Instant trunc(Instant date, String format) {
        return OracleInstantUtils.truncInstant(date, format);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date)}：将 {@link Instant} 截断到天（指定时区）
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param zoneId 时区，不可为 {@code null}
     * @return 截断到天的新 {@link Instant} 对象
     * @see OracleInstantUtils#truncInstantWithZone(Instant, ZoneId)
     */
    public static Instant truncWithZone(Instant date, ZoneId zoneId) {
        return OracleInstantUtils.truncInstantWithZone(date, zoneId);
    }

    /**
     * 模拟 Oracle {@code TRUNC(date, format)}：按指定格式模型截断 {@link Instant}（指定时区）
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写），如 CC、YYYY、Q、MM、DD、HH、MI 等
     * @param zoneId 时区，不可为 {@code null}
     * @return 截断后的新 {@link Instant} 对象
     * @see OracleInstantUtils#truncInstantWithZone(Instant, String, ZoneId)
     */
    public static Instant truncWithZone(Instant date, String format, ZoneId zoneId) {
        return OracleInstantUtils.truncInstantWithZone(date, format, zoneId);
    }

}
