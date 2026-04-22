package com.eredar.aviatororacle.runtime.utils;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.uitls.OracleInstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("基于Instant模拟Oracle日期计算测试")
public class OracleInstantUtilsTest {

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
                        Instant.parse("2025-10-20T22:11:17Z"),
                        Instant.parse("2023-03-11T10:43:26Z"),
                        new OraDecimal("954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为正，1秒",
                        Instant.parse("2025-10-21T00:00:00Z"),
                        Instant.parse("2025-10-20T23:59:59Z"),
                        new OraDecimal("0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为负，与正序对称取负",
                        Instant.parse("2023-03-11T10:43:26Z"),
                        Instant.parse("2025-10-20T22:11:17Z"),
                        new OraDecimal("-954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为负，1秒",
                        Instant.parse("2025-10-20T23:59:59Z"),
                        Instant.parse("2025-10-21T00:00:00Z"),
                        new OraDecimal("-0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为正，2位整数",
                        Instant.parse("2025-10-22T00:00:00Z"),
                        Instant.parse("2025-10-10T00:00:37Z"),
                        new OraDecimal("11.99957175925925925925925925925925925926")
                ),
                Arguments.of(
                        "结果为正，3位整数",
                        Instant.parse("2025-10-22T00:00:00Z"),
                        Instant.parse("2025-07-10T00:00:37Z"),
                        new OraDecimal("103.999571759259259259259259259259259259")
                )
        );
    }

    @DisplayName("daysBetween 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testDaysBetweenProvider")
    public void testDaysBetween(String caseId, Instant endDate, Instant beginDate, OraDecimal expected) {
        OraDecimal actual = OracleInstantUtils.daysBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    // -------------------------------------------------------------------------
    // monthsBetween
    // -------------------------------------------------------------------------

    static Stream<Arguments> testMonthsBetweenPlusProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        Instant.parse("2023-03-15T01:14:22Z"),
                        Instant.parse("2023-01-15T15:47:39Z"),
                        new OraDecimal("2")
                ),
                Arguments.of(
                        "均为月末",
                        Instant.parse("2023-02-28T01:14:22Z"),
                        Instant.parse("2023-01-31T15:47:39Z"),
                        new OraDecimal("1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        Instant.parse("2024-02-28T01:14:22Z"),
                        Instant.parse("2024-01-31T15:47:39Z"),
                        new OraDecimal("0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        Instant.parse("2023-01-31T15:47:39Z"),
                        Instant.parse("2023-01-31T01:14:22Z"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        Instant.parse("2024-02-29T15:50:39Z"),
                        Instant.parse("2024-02-23T11:02:39Z"),
                        new OraDecimal("0.2")
                ),
                Arguments.of(
                        "含0小数",
                        Instant.parse("2024-02-29T15:11:53Z"),
                        Instant.parse("2024-02-28T14:50:39Z"),
                        new OraDecimal("0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "1秒是零点几月",
                        Instant.parse("2024-02-29T00:00:00Z"),
                        Instant.parse("2024-02-28T23:59:59Z"),
                        new OraDecimal("0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        Instant.parse("2033-10-28T01:14:11Z"),
                        Instant.parse("2013-01-31T23:24:39Z"),
                        new OraDecimal("248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        Instant.parse("2033-10-01T00:00:00Z"),
                        Instant.parse("2013-01-31T23:59:59Z"),
                        new OraDecimal("248.000000373357228195937873357228195938")
                )

        );
    }

    @DisplayName("monthsBetween 方法正数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenPlusProvider")
    public void testMonthsBetweenPlus(String caseId, Instant endDate, Instant beginDate, OraDecimal expected) {
        OraDecimal actual = OracleInstantUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> testMonthsBetweenNegProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        Instant.parse("2023-01-15T15:47:39Z"),
                        Instant.parse("2023-03-15T01:14:22Z"),
                        new OraDecimal("-2")
                ),
                Arguments.of(
                        "均为月末",
                        Instant.parse("2023-01-31T15:47:39Z"),
                        Instant.parse("2023-02-28T01:14:22Z"),
                        new OraDecimal("-1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        Instant.parse("2024-01-31T15:47:39Z"),
                        Instant.parse("2024-02-28T01:14:22Z"),
                        new OraDecimal("-0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        Instant.parse("2023-01-31T01:14:22Z"),
                        Instant.parse("2023-01-31T15:47:39Z"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        Instant.parse("2024-02-23T11:02:39Z"),
                        Instant.parse("2024-02-29T15:50:39Z"),
                        new OraDecimal("-0.2")
                ),
                Arguments.of(
                        "含0小数",
                        Instant.parse("2024-02-28T14:50:39Z"),
                        Instant.parse("2024-02-29T15:11:53Z"),
                        new OraDecimal("-0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "1秒是零点几月",
                        Instant.parse("2024-02-28T23:59:59Z"),
                        Instant.parse("2024-02-29T00:00:00Z"),
                        new OraDecimal("-0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        Instant.parse("2013-01-31T23:24:39Z"),
                        Instant.parse("2033-10-28T01:14:11Z"),
                        new OraDecimal("-248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        Instant.parse("2013-01-31T23:59:59Z"),
                        Instant.parse("2033-10-01T00:00:00Z"),
                        new OraDecimal("-248.000000373357228195937873357228195938")
                )

        );
    }

    @DisplayName("monthsBetween 方法负数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenNegProvider")
    public void testMonthsBetweenNeg(String caseId, Instant endDate, Instant beginDate, OraDecimal expected) {
        OraDecimal actual = OracleInstantUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("monthsBetween 方法异常场景测试")
    public void testMonthsBetweenExceptions() {
        Instant now = Instant.now();
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.monthsBetween(now, null));
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.monthsBetween(now, now.plus(1, ChronoUnit.DAYS), null));
    }

    // -------------------------------------------------------------------------
    // truncInstant
    // -------------------------------------------------------------------------

    /**
     * truncInstant(instant, format) 场景数据，覆盖 Oracle TRUNC(date, fmt) 所有支持的格式模型。
     * <p>期望值均通过在 Oracle 数据库执行对应 SQL 验证得出，与 {@link OracleDateUtilsTest#testTruncDateProvider()} 使用完全相同的日期和期望值。
     * <p>所有 Instant 均以 UTC（Z）解析，与 Oracle 默认行为一致。
     */
    static Stream<Arguments> testTruncInstantProvider() {
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                // 2001 年属于第 21 世纪（2001-2100），截断到 2001-01-01
                Arguments.of("世纪截断-CC",    Instant.parse("2001-04-22T10:14:06Z"), "CC",    Instant.parse("2001-01-01T00:00:00Z")),
                Arguments.of("世纪截断-SCC",   Instant.parse("2000-04-22T10:14:06Z"), "SCC",   Instant.parse("1901-01-01T00:00:00Z")),

                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                // 截断到当年 1 月 1 日零点
                Arguments.of("年份截断-SYYYY", Instant.parse("2024-12-31T23:59:59Z"), "SYYYY", Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-YYYY",  Instant.parse("2024-12-31T23:59:59Z"), "YYYY",  Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-YEAR",  Instant.parse("2024-12-31T23:59:59Z"), "YEAR",  Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-SYEAR", Instant.parse("2024-12-31T23:59:59Z"), "SYEAR", Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-YYY",   Instant.parse("2021-01-01T10:19:19Z"), "YYY",   Instant.parse("2021-01-01T00:00:00Z")),
                Arguments.of("年份截断-YY",    Instant.parse("2022-01-01T12:19:19Z"), "YY",    Instant.parse("2022-01-01T00:00:00Z")),
                Arguments.of("年份截断-Y",     Instant.parse("2023-01-01T12:19:19Z"), "Y",     Instant.parse("2023-01-01T00:00:00Z")),

                // ---- ISO 年 IYYY / IYY / IY / I ----
                // 2024-01-01（周一）属于 ISO 2024 年，ISO 2024 年首日 = 2024-01-01
                Arguments.of("ISO年截断-IYYY", Instant.parse("2024-01-01T12:19:19Z"), "IYYY",  Instant.parse("2024-01-01T00:00:00Z")),
                // 2021-01-01（周五）属于 ISO 2020 年，ISO 2020 年首日 = 2019-12-30
                Arguments.of("ISO年截断-IYY",  Instant.parse("2021-01-01T10:19:19Z"), "IYY",   Instant.parse("2019-12-30T00:00:00Z")),
                // 2022-01-01（周六）属于 ISO 2021 年，ISO 2021 年首日 = 2021-01-04
                Arguments.of("ISO年截断-IY",   Instant.parse("2022-01-01T12:19:19Z"), "IY",    Instant.parse("2021-01-04T00:00:00Z")),
                // 2023-01-01（周日）属于 ISO 2022 年，ISO 2022 年首日 = 2022-01-03
                Arguments.of("ISO年截断-I",    Instant.parse("2023-01-01T12:19:19Z"), "I",     Instant.parse("2022-01-03T00:00:00Z")),

                // ---- 季度 Q ----
                // 5 月属于 Q2，Q2 首日 = 4 月 1 日
                Arguments.of("季度截断-Q",     Instant.parse("2024-05-15T08:30:00Z"), "Q",     Instant.parse("2024-04-01T00:00:00Z")),

                // ---- 月份 MONTH / MON / MM / RM ----
                // 截断到本月 1 日零点
                Arguments.of("月份截断-MONTH", Instant.parse("2024-12-31T23:59:59Z"), "MONTH", Instant.parse("2024-12-01T00:00:00Z")),
                Arguments.of("月份截断-MON",   Instant.parse("2024-10-30T23:59:59Z"), "MON",   Instant.parse("2024-10-01T00:00:00Z")),
                Arguments.of("月份截断-MM",    Instant.parse("2024-02-29T23:59:59Z"), "MM",    Instant.parse("2024-02-01T00:00:00Z")),
                Arguments.of("月份截断-RM",    Instant.parse("2024-01-01T00:00:01Z"), "RM",    Instant.parse("2024-01-01T00:00:00Z")),

                // ---- 年内周 WW ----
                // 2026-01-01 为周四，2026-04-22（周三）往前 6 天对齐到上一个周四 2026-04-16
                Arguments.of("年内周截断-WW",  Instant.parse("2026-04-22T15:20:30Z"), "WW",    Instant.parse("2026-04-16T00:00:00Z")),

                // ---- 月内周 W ----
                // 2026-04-01 为周三，2026-04-22 也是周三（间隔恰好 3 周），偏移量整除 7，截断到当天零点
                Arguments.of("月内周截断-W",   Instant.parse("2026-04-22T20:19:19Z"), "W",     Instant.parse("2026-04-22T00:00:00Z")),
                // 2026-04-01 为周三，2026-04-26 是周日，本周第一天为 2026-04-22 周三
                Arguments.of("月内周截断-W",   Instant.parse("2026-04-26T09:19:19Z"), "W",     Instant.parse("2026-04-22T00:00:00Z")),

                // ---- ISO 周 IW ----
                // 2026-04-26（周日）对应 ISO 周的周一 = 2026-04-20
                Arguments.of("ISO周截断-IW",   Instant.parse("2026-04-26T09:19:19Z"), "IW",    Instant.parse("2026-04-20T00:00:00Z")),

                // ---- 天 DDD / DD / J ----
                // 截断到当天零点
                Arguments.of("天截断-DDD",     Instant.parse("2026-04-26T14:45:12Z"), "DDD",   Instant.parse("2026-04-26T00:00:00Z")),
                Arguments.of("天截断-DD",      Instant.parse("2026-04-25T14:45:12Z"), "DD",    Instant.parse("2026-04-25T00:00:00Z")),
                Arguments.of("天截断-J",       Instant.parse("2026-04-20T00:00:01Z"), "J",     Instant.parse("2026-04-20T00:00:00Z")),

                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                // 2026-04-26 为周日，本身即周首日
                Arguments.of("周截断-DAY",     Instant.parse("2026-04-26T16:19:19Z"), "DAY",   Instant.parse("2026-04-26T00:00:00Z")),
                // 2026-04-25（周六）往前 6 天到周日 2026-04-19
                Arguments.of("周截断-DY",      Instant.parse("2026-04-25T16:19:19Z"), "DY",    Instant.parse("2026-04-19T00:00:00Z")),
                // 2026-04-20（周一）往前 1 天到周日 2026-04-19
                Arguments.of("周截断-D",       Instant.parse("2026-04-20T00:00:01Z"), "D",     Instant.parse("2026-04-19T00:00:00Z")),

                // ---- 小时 HH / HH12 / HH24 ----
                // 保留小时，分钟和秒清零
                Arguments.of("小时截断-HH",    Instant.parse("2024-05-23T18:59:59Z"), "HH",    Instant.parse("2024-05-23T18:00:00Z")),
                Arguments.of("小时截断-HH12",  Instant.parse("2024-05-23T18:00:01Z"), "HH12",  Instant.parse("2024-05-23T18:00:00Z")),
                Arguments.of("小时截断-HH24",  Instant.parse("2024-05-23T18:31:59Z"), "HH24",  Instant.parse("2024-05-23T18:00:00Z")),

                // ---- 分钟 MI ----
                // 保留分钟，秒清零
                Arguments.of("分钟截断-MI",    Instant.parse("2024-05-23T18:55:59Z"), "MI",    Instant.parse("2024-05-23T18:55:00Z"))
        );
    }

    @DisplayName("truncInstant(instant, format) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncInstantProvider")
    public void testTruncInstant(String caseId, Instant input, String format, Instant expected) {
        Instant actual = OracleInstantUtils.truncInstant(input, format);
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("truncInstant(instant) 方法测试")
    @Test
    public void testTruncInstantNoFormat() {
        Instant actual = OracleInstantUtils.truncInstant(Instant.parse("2026-04-25T14:45:12Z"));
        Assertions.assertEquals(Instant.parse("2026-04-25T00:00:00Z"), actual);
    }

    /**
     * 异常场景：传入 Oracle 不支持的格式（如 "XX"）时，应抛出 {@link IllegalArgumentException}。
     */
    @Test
    @DisplayName("truncInstant 方法异常场景测试：不支持的格式")
    public void testTruncInstantInvalidFormat() {
        Instant now = Instant.parse("2026-04-22T10:14:06Z");
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.truncInstant(now, "XX"));
    }
}
