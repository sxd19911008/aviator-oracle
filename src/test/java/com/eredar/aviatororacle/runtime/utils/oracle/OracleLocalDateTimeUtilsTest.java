package com.eredar.aviatororacle.runtime.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("基于LocalDateTime模拟Oracle日期计算测试")
public class OracleLocalDateTimeUtilsTest {

    /** 日期格式化器，匹配 "yyyy-MM-dd HH:mm:ss" */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 将日期时间字符串解析为 {@link LocalDateTime}
     */
    private static LocalDateTime ldt(String dateStr) {
        return LocalDateTime.parse(dateStr, FMT);
    }

    // -------------------------------------------------------------------------
    // daysBetween
    // -------------------------------------------------------------------------

    /**
     * daysBetween 场景数据：caseId 为可读说明，便于参数化测试报告展示。
     */
    static Stream<Arguments> testDaysBetweenProvider() {
        return Stream.of(
                Arguments.of(
                        "结果为正，跨年多日",
                        ldt("2025-10-20 22:11:17"),
                        ldt("2023-03-11 10:43:26"),
                        new OraDecimal("954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为正，1秒",
                        ldt("2025-10-21 00:00:00"),
                        ldt("2025-10-20 23:59:59"),
                        new OraDecimal("0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为负，与正序对称取负",
                        ldt("2023-03-11 10:43:26"),
                        ldt("2025-10-20 22:11:17"),
                        new OraDecimal("-954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为负，1秒",
                        ldt("2025-10-20 23:59:59"),
                        ldt("2025-10-21 00:00:00"),
                        new OraDecimal("-0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为正，2位整数",
                        ldt("2025-10-22 00:00:00"),
                        ldt("2025-10-10 00:00:37"),
                        new OraDecimal("11.99957175925925925925925925925925925926")
                ),
                Arguments.of(
                        "结果为正，3位整数",
                        ldt("2025-10-22 00:00:00"),
                        ldt("2025-07-10 00:00:37"),
                        new OraDecimal("103.999571759259259259259259259259259259")
                )
        );
    }

    @DisplayName("daysBetween 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testDaysBetweenProvider")
    public void testDaysBetween(String caseId, LocalDateTime endDate, LocalDateTime beginDate, OraDecimal expected) {
        OraDecimal actual = OracleLocalDateTimeUtils.daysBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    // -------------------------------------------------------------------------
    // monthsBetween
    // -------------------------------------------------------------------------

    static Stream<Arguments> testMonthsBetweenPlusProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        ldt("2023-03-15 01:14:22"),
                        ldt("2023-01-15 15:47:39"),
                        new OraDecimal("2")
                ),
                Arguments.of(
                        "均为月末",
                        ldt("2023-02-28 01:14:22"),
                        ldt("2023-01-31 15:47:39"),
                        new OraDecimal("1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        ldt("2024-02-28 01:14:22"),
                        ldt("2024-01-31 15:47:39"),
                        new OraDecimal("0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        ldt("2023-01-31 15:47:39"),
                        ldt("2023-01-31 01:14:22"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        ldt("2024-02-29 15:50:39"),
                        ldt("2024-02-23 11:02:39"),
                        new OraDecimal("0.2")
                ),
                Arguments.of(
                        "含0小数",
                        ldt("2024-02-29 15:11:53"),
                        ldt("2024-02-28 14:50:39"),
                        new OraDecimal("0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "含0小数，1秒是零点几月",
                        ldt("2024-02-29 00:00:00"),
                        ldt("2024-02-28 23:59:59"),
                        new OraDecimal("0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        ldt("2033-10-28 01:14:11"),
                        ldt("2013-01-31 23:24:39"),
                        new OraDecimal("248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        ldt("2033-10-01 00:00:00"),
                        ldt("2013-01-31 23:59:59"),
                        new OraDecimal("248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("monthsBetween 方法正数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenPlusProvider")
    public void testMonthsBetweenPlus(String caseId, LocalDateTime endDate, LocalDateTime beginDate, OraDecimal expected) {
        OraDecimal actual = OracleLocalDateTimeUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> testMonthsBetweenNegProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        ldt("2023-01-15 15:47:39"),
                        ldt("2023-03-15 01:14:22"),
                        new OraDecimal("-2")
                ),
                Arguments.of(
                        "均为月末",
                        ldt("2023-01-31 15:47:39"),
                        ldt("2023-02-28 01:14:22"),
                        new OraDecimal("-1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        ldt("2024-01-31 15:47:39"),
                        ldt("2024-02-28 01:14:22"),
                        new OraDecimal("-0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        ldt("2023-01-31 01:14:22"),
                        ldt("2023-01-31 15:47:39"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        ldt("2024-02-23 11:02:39"),
                        ldt("2024-02-29 15:50:39"),
                        new OraDecimal("-0.2")
                ),
                Arguments.of(
                        "含0小数",
                        ldt("2024-02-28 14:50:39"),
                        ldt("2024-02-29 15:11:53"),
                        new OraDecimal("-0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "含0小数，1秒是零点几月",
                        ldt("2024-02-28 23:59:59"),
                        ldt("2024-02-29 00:00:00"),
                        new OraDecimal("-0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        ldt("2013-01-31 23:24:39"),
                        ldt("2033-10-28 01:14:11"),
                        new OraDecimal("-248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        ldt("2013-01-31 23:59:59"),
                        ldt("2033-10-01 00:00:00"),
                        new OraDecimal("-248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("monthsBetween 方法负数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenNegProvider")
    public void testMonthsBetweenNeg(String caseId, LocalDateTime endDate, LocalDateTime beginDate, OraDecimal expected) {
        OraDecimal actual = OracleLocalDateTimeUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * 异常场景：仅测试 null 参数校验。
     */
    @Test
    @DisplayName("monthsBetween 方法异常场景测试")
    public void testMonthsBetweenExceptions() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.monthsBetween(now, null));
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.monthsBetween(null, now));
    }

    // -------------------------------------------------------------------------
    // truncLocalDateTime
    // -------------------------------------------------------------------------

    /**
     * truncLocalDateTime(date, format) 场景数据，覆盖 Oracle TRUNC(date, fmt) 所有支持的格式模型。
     * <p>期望值均通过在 Oracle 数据库执行对应 SQL 验证得出
     */
    static Stream<Arguments> testTruncLocalDateTimeProvider() {
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                // 2001 年属于第 21 世纪（2001-2100），截断到 2001-01-01
                Arguments.of("世纪截断-CC",   ldt("2001-04-22 10:14:06"), "CC",    ldt("2001-01-01 00:00:00")),
                Arguments.of("世纪截断-SCC",  ldt("2000-04-22 10:14:06"), "SCC",   ldt("1901-01-01 00:00:00")),

                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                // 截断到当年 1 月 1 日零点
                Arguments.of("年份截断-SYYYY", ldt("2024-12-31 23:59:59"), "SYYYY", ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYYY",  ldt("2024-12-31 23:59:59"), "YYYY",  ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YEAR",  ldt("2024-12-31 23:59:59"), "YEAR",  ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-SYEAR", ldt("2024-12-31 23:59:59"), "SYEAR", ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYY",   ldt("2021-01-01 10:19:19"), "YYY",   ldt("2021-01-01 00:00:00")),
                Arguments.of("年份截断-YY",    ldt("2022-01-01 12:19:19"), "YY",    ldt("2022-01-01 00:00:00")),
                Arguments.of("年份截断-Y",     ldt("2023-01-01 12:19:19"), "Y",     ldt("2023-01-01 00:00:00")),

                // ---- ISO 年 IYYY / IYY / IY / I ----
                // 2024-01-01（周一）属于 ISO 2024 年，ISO 2024 年首日 = 2024-01-01
                Arguments.of("ISO年截断-IYYY", ldt("2024-01-01 12:19:19"), "IYYY",  ldt("2024-01-01 00:00:00")),
                // 2021-01-01（周五）属于 ISO 2020 年，ISO 2020 年首日 = 2019-12-30
                Arguments.of("ISO年截断-IYY",  ldt("2021-01-01 10:19:19"), "IYY",   ldt("2019-12-30 00:00:00")),
                // 2022-01-01（周六）属于 ISO 2021 年，ISO 2021 年首日 = 2021-01-04
                Arguments.of("ISO年截断-IY",   ldt("2022-01-01 12:19:19"), "IY",    ldt("2021-01-04 00:00:00")),
                // 2023-01-01（周日）属于 ISO 2022 年，ISO 2022 年首日 = 2022-01-03
                Arguments.of("ISO年截断-I",    ldt("2023-01-01 12:19:19"), "I",     ldt("2022-01-03 00:00:00")),

                // ---- 季度 Q ----
                // 5 月属于 Q2，Q2 首日 = 4 月 1 日
                Arguments.of("季度截断-Q",     ldt("2024-05-15 08:30:00"), "Q",     ldt("2024-04-01 00:00:00")),

                // ---- 月份 MONTH / MON / MM / RM ----
                // 截断到本月 1 日零点
                Arguments.of("月份截断-MONTH", ldt("2024-12-31 23:59:59"), "MONTH", ldt("2024-12-01 00:00:00")),
                Arguments.of("月份截断-MON",   ldt("2024-10-30 23:59:59"), "MON",   ldt("2024-10-01 00:00:00")),
                Arguments.of("月份截断-MM",    ldt("2024-02-29 23:59:59"), "MM",    ldt("2024-02-01 00:00:00")),
                Arguments.of("月份截断-RM",    ldt("2024-01-01 00:00:01"), "RM",    ldt("2024-01-01 00:00:00")),

                // ---- 年内周 WW ----
                // 2026-01-01 为周四，2026-04-22（周三）往前 6 天对齐到上一个周四 2026-04-16
                Arguments.of("年内周截断-WW",  ldt("2026-04-22 15:20:30"), "WW",    ldt("2026-04-16 00:00:00")),

                // ---- 月内周 W ----
                // 2026-04-01 为周三，2026-04-22 也是周三（间隔恰好 3 周），偏移量整除 7，截断到当天零点
                Arguments.of("月内周截断-W",   ldt("2026-04-22 20:19:19"), "W",     ldt("2026-04-22 00:00:00")),
                // 2026-04-01 为周三，2026-04-26 是周日，本周第一天为 2026-04-22 周三
                Arguments.of("月内周截断-W",   ldt("2026-04-26 09:19:19"), "W",     ldt("2026-04-22 00:00:00")),

                // ---- ISO 周 IW ----
                // 2026-04-26（周日）对应 ISO 周的周一 = 2026-04-20
                Arguments.of("ISO周截断-IW",   ldt("2026-04-26 09:19:19"), "IW",    ldt("2026-04-20 00:00:00")),

                // ---- 天 DDD / DD / J ----
                // 截断到当天零点
                Arguments.of("天截断-DDD",     ldt("2026-04-26 14:45:12"), "DDD",   ldt("2026-04-26 00:00:00")),
                Arguments.of("天截断-DD",      ldt("2026-04-25 14:45:12"), "DD",    ldt("2026-04-25 00:00:00")),
                Arguments.of("天截断-J",       ldt("2026-04-20 00:00:01"), "J",     ldt("2026-04-20 00:00:00")),

                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                // 2026-04-26 为周日，本身即周首日
                Arguments.of("周截断-DAY",     ldt("2026-04-26 16:19:19"), "DAY",   ldt("2026-04-26 00:00:00")),
                // 2026-04-25（周六）往前 6 天到周日 2026-04-19
                Arguments.of("周截断-DY",      ldt("2026-04-25 16:19:19"), "DY",    ldt("2026-04-19 00:00:00")),
                // 2026-04-20（周一）往前 1 天到周日 2026-04-19
                Arguments.of("周截断-D",       ldt("2026-04-20 00:00:01"), "D",     ldt("2026-04-19 00:00:00")),

                // ---- 小时 HH / HH12 / HH24 ----
                // 保留小时，分钟和秒清零
                Arguments.of("小时截断-HH",    ldt("2024-05-23 18:59:59"), "HH",    ldt("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH12",  ldt("2024-05-23 18:00:01"), "HH12",  ldt("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH24",  ldt("2024-05-23 18:31:59"), "HH24",  ldt("2024-05-23 18:00:00")),

                // ---- 分钟 MI ----
                // 保留分钟，秒清零
                Arguments.of("分钟截断-MI",    ldt("2024-05-23 18:55:59"), "MI",    ldt("2024-05-23 18:55:00"))
        );
    }

    @DisplayName("truncLocalDateTime(date, format) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncLocalDateTimeProvider")
    public void testTruncLocalDateTime(String caseId, LocalDateTime input, String format, LocalDateTime expected) {
        LocalDateTime actual = OracleLocalDateTimeUtils.truncLocalDateTime(input, format);
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("truncLocalDateTime(date) 方法测试")
    @Test
    public void testTruncLocalDateTimeNoFormat() {
        LocalDateTime actual = OracleLocalDateTimeUtils.truncLocalDateTime(ldt("2026-04-25 14:45:12"));
        Assertions.assertEquals(ldt("2026-04-25 00:00:00"), actual);
    }

    /**
     * 异常场景：传入 Oracle 不支持的格式（如 "XX"）时，应抛出 {@link IllegalArgumentException}。
     */
    @Test
    @DisplayName("truncLocalDateTime 方法异常场景测试：不支持的格式")
    public void testTruncLocalDateTimeInvalidFormat() {
        LocalDateTime now = ldt("2026-04-22 10:14:06");
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.truncLocalDateTime(now, "XX"));
    }

    // -------------------------------------------------------------------------
    // addMonths
    // -------------------------------------------------------------------------

    /**
     * addMonths 场景数据，覆盖 Oracle ADD_MONTHS(date, months) 所有行为规则。
     * <p>期望值均通过在 Oracle 数据库执行对应 SQL 验证得出
     */
    static Stream<Arguments> testAddMonthsProvider() {
        return Stream.of(
                // ---- 正常场景：日号在目标月份范围内，日号和时分秒均保持不变 ----
                Arguments.of("正常场景：月中日期+正数月份",
                        ldt("2024-03-15 10:30:45"), 2, ldt("2024-05-15 10:30:45")),
                Arguments.of("月数为0，日期不变",
                        ldt("2024-01-15 23:59:59"), 0, ldt("2024-01-15 23:59:59")),
                Arguments.of("29日+1月到闰年2月，日号不变",
                        ldt("2024-01-29 10:00:00"), 1, ldt("2024-02-29 10:00:00")),
                Arguments.of("大跨度：+24个月跨2年",
                        ldt("2024-06-15 08:30:00"), 24, ldt("2026-06-15 08:30:00")),

                // ---- 月末→月末：原始日期是所在月份最后一天，结果一定是目标月份最后一天 ----
                Arguments.of("月末→月末：闰年2月29日+1月→3月31日",
                        ldt("2024-02-29 14:22:33"), 1, ldt("2024-03-31 14:22:33")),
                Arguments.of("月末→月末：1月31日+1月→闰年2月29日",
                        ldt("2024-01-31 08:15:00"), 1, ldt("2024-02-29 08:15:00")),
                Arguments.of("月末→月末：非闰年1月31日+1月→2月28日",
                        ldt("2023-01-31 08:15:00"), 1, ldt("2023-02-28 08:15:00")),
                Arguments.of("月末→月末：4月30日-2月→非闰年2月28日",
                        ldt("2023-04-30 12:00:00"), -2, ldt("2023-02-28 12:00:00")),
                Arguments.of("月末→月末：跨年11月30日+3月→2月28日",
                        ldt("2024-11-30 10:00:00"), 3, ldt("2025-02-28 10:00:00")),
                Arguments.of("月末→月末：3月31日-1月→闰年2月29日",
                        ldt("2024-03-31 10:00:00"), -1, ldt("2024-02-29 10:00:00")),
                Arguments.of("月末→月末：非闰年2月28日+12月→闰年2月29日",
                        ldt("2023-02-28 18:00:00"), 12, ldt("2024-02-29 18:00:00")),
                Arguments.of("月末→月末：闰年2月29日-12月→非闰年2月28日",
                        ldt("2024-02-29 18:00:00"), -12, ldt("2023-02-28 18:00:00")),

                // ---- 日号超限：原始日号 > 目标月份最大天数，设为目标月份最后一天 ----
                Arguments.of("日号超限：29日+1月到非闰年2月→28日",
                        ldt("2023-01-29 10:00:00"), 1, ldt("2023-02-28 10:00:00")),

                // ---- 负数月份：向前回退月份 ----
                Arguments.of("负数月份：月中日期-3月",
                        ldt("2024-05-20 16:45:30"), -3, ldt("2024-02-20 16:45:30")),

                // ---- 小数月份：小数部分被截断为整数 ----
                Arguments.of("小数月份被截断：2.7→2",
                        ldt("2024-06-15 12:00:00"), 2.7, ldt("2024-08-15 12:00:00")),
                Arguments.of("负数小数月份被截断：-2.9→-2",
                        ldt("2024-06-15 12:00:00"), -2.9, ldt("2024-04-15 12:00:00"))
        );
    }

    @DisplayName("addMonths 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsProvider")
    public void testAddMonths(String caseId, LocalDateTime input, Number months, LocalDateTime expected) {
        LocalDateTime actual = OracleLocalDateTimeUtils.addMonths(input, months);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addMonths 方法异常场景测试：null 参数")
    public void testAddMonthsExceptions() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.addMonths(null, 1));
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.addMonths(now, null));
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.addMonths(null, null));
    }

    // -------------------------------------------------------------------------
    // lastDay
    // -------------------------------------------------------------------------

    /**
     * lastDay 场景数据，覆盖 Oracle LAST_DAY(date) 所有行为规则。
     * <p>期望值均通过在 Oracle 数据库执行对应 SQL 验证得出
     */
    static Stream<Arguments> testLastDayProvider() {
        return Stream.of(
                // ---- 31天月份 ----
                Arguments.of("31天月份(1月)",
                        ldt("2024-01-15 10:30:45"), ldt("2024-01-31 10:30:45")),

                // ---- 30天月份 ----
                Arguments.of("30天月份(4月)",
                        ldt("2024-04-10 14:22:33"), ldt("2024-04-30 14:22:33")),

                // ---- 闰年2月 → 29日 ----
                Arguments.of("闰年2月",
                        ldt("2024-02-15 08:15:00"), ldt("2024-02-29 08:15:00")),

                // ---- 非闰年2月 → 28日 ----
                Arguments.of("非闰年2月",
                        ldt("2023-02-15 08:15:00"), ldt("2023-02-28 08:15:00")),

                // ---- 已是月末，返回自身（时分秒不变） ----
                Arguments.of("已是月末(3月31日)",
                        ldt("2024-03-31 23:59:59"), ldt("2024-03-31 23:59:59")),

                // ---- 12月（年末边界） ----
                Arguments.of("12月(年末)",
                        ldt("2024-12-01 00:00:00"), ldt("2024-12-31 00:00:00")),

                // ---- 月初第1天 ----
                Arguments.of("月初第1天",
                        ldt("2024-07-01 06:30:00"), ldt("2024-07-31 06:30:00")),

                // ---- 闰年2月29日已是月末 ----
                Arguments.of("闰年2月29日(已是月末)",
                        ldt("2024-02-29 12:00:00"), ldt("2024-02-29 12:00:00"))
        );
    }

    @DisplayName("lastDay 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayProvider")
    public void testLastDay(String caseId, LocalDateTime input, LocalDateTime expected) {
        LocalDateTime actual = OracleLocalDateTimeUtils.lastDay(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("lastDay 方法异常场景测试：null 参数")
    public void testLastDayExceptions() {
        assertThrows(IllegalArgumentException.class, () -> OracleLocalDateTimeUtils.lastDay(null));
    }
}
