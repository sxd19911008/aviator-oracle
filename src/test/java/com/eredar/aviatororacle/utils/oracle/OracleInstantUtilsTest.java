package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("基于Instant模拟Oracle日期计算测试")
public class OracleInstantUtilsTest {

    /** 上海时区（UTC+8），用于 truncInstantWithZone 系列测试 */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    /**
     * 以上海时区构造 {@link Instant}（纳秒固定为 0），用于构造截断后的期望值。
     */
    private static Instant sh(int year, int month, int day, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, SHANGHAI).toInstant();
    }

    /**
     * 以上海时区构造 {@link Instant}（可指定纳秒），用于构造含亚秒精度的输入值，
     * 以验证截断方法能够正确清除纳秒部分。
     */
    private static Instant sh(int year, int month, int day, int hour, int minute, int second, int nano) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, nano, SHANGHAI).toInstant();
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
    // addMonths
    // -------------------------------------------------------------------------

    /**
     * addMonths(instant, months) 场景数据（UTC），覆盖 Oracle ADD_MONTHS(date, months) 所有行为规则。
     * <p>案例与 {@link OracleDateUtilsTest#testAddMonthsProvider()} 使用完全相同的日期和期望值。
     */
    static Stream<Arguments> testAddMonthsProvider() {
        return Stream.of(
                // ---- 正常场景：日号在目标月份范围内，日号和时分秒均保持不变 ----
                Arguments.of("正常场景：月中日期+正数月份",
                        Instant.parse("2024-03-15T10:30:45Z"), 2, Instant.parse("2024-05-15T10:30:45Z")),
                Arguments.of("月数为0，日期不变",
                        Instant.parse("2024-01-15T23:59:59Z"), 0, Instant.parse("2024-01-15T23:59:59Z")),
                Arguments.of("29日+1月到闰年2月，日号不变",
                        Instant.parse("2024-01-29T10:00:00Z"), 1, Instant.parse("2024-02-29T10:00:00Z")),
                Arguments.of("大跨度：+24个月跨2年",
                        Instant.parse("2024-06-15T08:30:00Z"), 24, Instant.parse("2026-06-15T08:30:00Z")),

                // ---- 月末→月末：原始日期是所在月份最后一天，结果一定是目标月份最后一天 ----
                Arguments.of("月末→月末：闰年2月29日+1月→3月31日",
                        Instant.parse("2024-02-29T14:22:33Z"), 1, Instant.parse("2024-03-31T14:22:33Z")),
                Arguments.of("月末→月末：1月31日+1月→闰年2月29日",
                        Instant.parse("2024-01-31T08:15:00Z"), 1, Instant.parse("2024-02-29T08:15:00Z")),
                Arguments.of("月末→月末：非闰年1月31日+1月→2月28日",
                        Instant.parse("2023-01-31T08:15:00Z"), 1, Instant.parse("2023-02-28T08:15:00Z")),
                Arguments.of("月末→月末：4月30日-2月→非闰年2月28日",
                        Instant.parse("2023-04-30T12:00:00Z"), -2, Instant.parse("2023-02-28T12:00:00Z")),
                Arguments.of("月末→月末：跨年11月30日+3月→2月28日",
                        Instant.parse("2024-11-30T10:00:00Z"), 3, Instant.parse("2025-02-28T10:00:00Z")),
                Arguments.of("月末→月末：3月31日-1月→闰年2月29日",
                        Instant.parse("2024-03-31T10:00:00Z"), -1, Instant.parse("2024-02-29T10:00:00Z")),
                Arguments.of("月末→月末：非闰年2月28日+12月→闰年2月29日",
                        Instant.parse("2023-02-28T18:00:00Z"), 12, Instant.parse("2024-02-29T18:00:00Z")),
                Arguments.of("月末→月末：闰年2月29日-12月→非闰年2月28日",
                        Instant.parse("2024-02-29T18:00:00Z"), -12, Instant.parse("2023-02-28T18:00:00Z")),

                // ---- 日号超限：原始日号 > 目标月份最大天数，设为目标月份最后一天 ----
                Arguments.of("日号超限：29日+1月到非闰年2月→28日",
                        Instant.parse("2023-01-29T10:00:00Z"), 1, Instant.parse("2023-02-28T10:00:00Z")),

                // ---- 负数月份：向前回退月份 ----
                Arguments.of("负数月份：月中日期-3月",
                        Instant.parse("2024-05-20T16:45:30Z"), -3, Instant.parse("2024-02-20T16:45:30Z")),

                // ---- 小数月份：小数部分被截断为整数 ----
                Arguments.of("小数月份被截断：2.7→2",
                        Instant.parse("2024-06-15T12:00:00Z"), 2.7, Instant.parse("2024-08-15T12:00:00Z")),
                Arguments.of("负数小数月份被截断：-2.9→-2",
                        Instant.parse("2024-06-15T12:00:00Z"), -2.9, Instant.parse("2024-04-15T12:00:00Z"))
        );
    }

    @DisplayName("addMonths(instant, months) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsProvider")
    public void testAddMonths(String caseId, Instant input, Number months, Instant expected) {
        Instant actual = OracleInstantUtils.addMonths(input, months);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * addMonths(instant, months, zoneId) 场景数据（上海时区 UTC+8）。
     * <p>案例日期与 {@link #testAddMonthsProvider()} 完全相同（年/月/日/时/分/秒数字不变），
     * 但以上海时区（UTC+8）解释该本地时间后再转为 Instant，验证方法能够在指定时区下正确执行。
     * <p>输入和期望均通过 {@code sh(...)} 构造，所有输入均携带非零纳秒（123_456_789），
     * 期望值也携带相同纳秒，以验证 addMonths 能够正确保留亚秒精度。
     */
    static Stream<Arguments> testAddMonthsWithZoneProvider() {
        final int nano = 123_456_789;
        return Stream.of(
                // ---- 正常场景 ----
                Arguments.of("正常场景：月中日期+正数月份",
                        sh(2024, 3, 15, 10, 30, 45, 12), 2, sh(2024, 5, 15, 10, 30, 45, 12)),
                Arguments.of("月数为0，日期不变",
                        sh(2024, 1, 15, 23, 59, 59, nano), 0, sh(2024, 1, 15, 23, 59, 59, nano)),
                Arguments.of("29日+1月到闰年2月，日号不变",
                        sh(2024, 1, 29, 10, 0, 0, nano), 1, sh(2024, 2, 29, 10, 0, 0, nano)),
                Arguments.of("大跨度：+24个月跨2年",
                        sh(2024, 6, 15, 8, 30, 0, nano), 24, sh(2026, 6, 15, 8, 30, 0, nano)),

                // ---- 月末→月末 ----
                Arguments.of("月末→月末：闰年2月29日+1月→3月31日",
                        sh(2024, 2, 29, 14, 22, 33, nano), 1, sh(2024, 3, 31, 14, 22, 33, nano)),
                Arguments.of("月末→月末：1月31日+1月→闰年2月29日",
                        sh(2024, 1, 31, 8, 15, 0, nano), 1, sh(2024, 2, 29, 8, 15, 0, nano)),
                Arguments.of("月末→月末：非闰年1月31日+1月→2月28日",
                        sh(2023, 1, 31, 8, 15, 0, nano), 1, sh(2023, 2, 28, 8, 15, 0, nano)),
                Arguments.of("月末→月末：4月30日-2月→非闰年2月28日",
                        sh(2023, 4, 30, 12, 0, 0, nano), -2, sh(2023, 2, 28, 12, 0, 0, nano)),
                Arguments.of("月末→月末：跨年11月30日+3月→2月28日",
                        sh(2024, 11, 30, 10, 0, 0, nano), 3, sh(2025, 2, 28, 10, 0, 0, nano)),
                Arguments.of("月末→月末：3月31日-1月→闰年2月29日",
                        sh(2024, 3, 31, 10, 0, 0, nano), -1, sh(2024, 2, 29, 10, 0, 0, nano)),
                Arguments.of("月末→月末：非闰年2月28日+12月→闰年2月29日",
                        sh(2023, 2, 28, 18, 0, 0, nano), 12, sh(2024, 2, 29, 18, 0, 0, nano)),
                Arguments.of("月末→月末：闰年2月29日-12月→非闰年2月28日",
                        sh(2024, 2, 29, 18, 0, 0, nano), -12, sh(2023, 2, 28, 18, 0, 0, nano)),

                // ---- 日号超限 ----
                Arguments.of("日号超限：29日+1月到非闰年2月→28日",
                        sh(2023, 1, 29, 10, 0, 0, nano), 1, sh(2023, 2, 28, 10, 0, 0, nano)),

                // ---- 负数月份 ----
                Arguments.of("负数月份：月中日期-3月",
                        sh(2024, 5, 20, 16, 45, 30, nano), -3, sh(2024, 2, 20, 16, 45, 30, nano)),

                // ---- 小数月份被截断 ----
                Arguments.of("小数月份被截断：2.7→2",
                        sh(2024, 6, 15, 12, 0, 0, nano), 2.7, sh(2024, 8, 15, 12, 0, 0, nano)),
                Arguments.of("负数小数月份被截断：-2.9→-2",
                        sh(2024, 6, 15, 12, 0, 0, nano), -2.9, sh(2024, 4, 15, 12, 0, 0, nano))
        );
    }

    @DisplayName("addMonths(instant, months, zoneId) 方法测试（上海时区）")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsWithZoneProvider")
    public void testAddMonthsWithZone(String caseId, Instant input, Number months, Instant expected) {
        Instant actual = OracleInstantUtils.addMonths(input, months, SHANGHAI);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addMonths 方法异常场景测试：null 参数")
    public void testAddMonthsExceptions() {
        Instant now = Instant.now();
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.addMonths(null, 1));
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.addMonths(now, null));
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.addMonths(null, null));
        // 带时区版本：时区为 null
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.addMonths(now, 1, null));
    }

    // -------------------------------------------------------------------------
    // lastDay
    // -------------------------------------------------------------------------

    /**
     * lastDay(instant) 场景数据（UTC），覆盖 Oracle LAST_DAY(date) 所有行为规则。
     * <p>案例与 {@link OracleDateUtilsTest#testLastDayProvider()} 使用完全相同的日期和期望值。
     */
    static Stream<Arguments> testLastDayProvider() {
        return Stream.of(
                // ---- 31天月份 ----
                Arguments.of("31天月份(1月)",
                        Instant.parse("2024-01-15T10:30:45Z"), Instant.parse("2024-01-31T10:30:45Z")),

                // ---- 30天月份 ----
                Arguments.of("30天月份(4月)",
                        Instant.parse("2024-04-10T14:22:33Z"), Instant.parse("2024-04-30T14:22:33Z")),

                // ---- 闰年2月 → 29日 ----
                Arguments.of("闰年2月",
                        Instant.parse("2024-02-15T08:15:00Z"), Instant.parse("2024-02-29T08:15:00Z")),

                // ---- 非闰年2月 → 28日 ----
                Arguments.of("非闰年2月",
                        Instant.parse("2023-02-15T08:15:00Z"), Instant.parse("2023-02-28T08:15:00Z")),

                // ---- 已是月末，返回自身（时分秒不变） ----
                Arguments.of("已是月末(3月31日)",
                        Instant.parse("2024-03-31T23:59:59Z"), Instant.parse("2024-03-31T23:59:59Z")),

                // ---- 12月（年末边界） ----
                Arguments.of("12月(年末)",
                        Instant.parse("2024-12-01T00:00:00Z"), Instant.parse("2024-12-31T00:00:00Z")),

                // ---- 月初第1天 ----
                Arguments.of("月初第1天",
                        Instant.parse("2024-07-01T06:30:00Z"), Instant.parse("2024-07-31T06:30:00Z")),

                // ---- 闰年2月29日已是月末 ----
                Arguments.of("闰年2月29日(已是月末)",
                        Instant.parse("2024-02-29T12:00:00Z"), Instant.parse("2024-02-29T12:00:00Z"))
        );
    }

    @DisplayName("lastDay(instant) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayProvider")
    public void testLastDay(String caseId, Instant input, Instant expected) {
        Instant actual = OracleInstantUtils.lastDay(input);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * lastDay(instant, zoneId) 场景数据（上海时区 UTC+8）。
     * <p>案例日期与 {@link #testLastDayProvider()} 完全相同（年/月/日/时/分/秒数字不变），
     * 但以上海时区（UTC+8）解释该本地时间后再转为 Instant，验证方法能够在指定时区下正确执行。
     * <p>输入和期望均通过 {@code sh(...)} 构造，所有输入均携带非零纳秒（123_456_789），
     * 期望值也携带相同纳秒，以验证 lastDay 能够正确保留亚秒精度。
     */
    static Stream<Arguments> testLastDayWithZoneProvider() {
        final int nano = 123_456_789;
        return Stream.of(
                Arguments.of("31天月份(1月)",
                        sh(2024, 1, 15, 10, 30, 45, nano), sh(2024, 1, 31, 10, 30, 45, nano)),
                Arguments.of("30天月份(4月)",
                        sh(2024, 4, 10, 14, 22, 33, nano), sh(2024, 4, 30, 14, 22, 33, nano)),
                Arguments.of("闰年2月",
                        sh(2024, 2, 15, 8, 15, 0, nano), sh(2024, 2, 29, 8, 15, 0, nano)),
                Arguments.of("非闰年2月",
                        sh(2023, 2, 15, 8, 15, 0, nano), sh(2023, 2, 28, 8, 15, 0, nano)),
                Arguments.of("已是月末(3月31日)",
                        sh(2024, 3, 31, 23, 59, 59, nano), sh(2024, 3, 31, 23, 59, 59, nano)),
                Arguments.of("12月(年末)",
                        sh(2024, 12, 1, 0, 0, 0, nano), sh(2024, 12, 31, 0, 0, 0, nano)),
                Arguments.of("月初第1天",
                        sh(2024, 7, 1, 6, 30, 0, nano), sh(2024, 7, 31, 6, 30, 0, nano)),
                Arguments.of("闰年2月29日(已是月末)",
                        sh(2024, 2, 29, 12, 0, 0, nano), sh(2024, 2, 29, 12, 0, 0, nano))
        );
    }

    @DisplayName("lastDay(instant, zoneId) 方法测试（上海时区）")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayWithZoneProvider")
    public void testLastDayWithZone(String caseId, Instant input, Instant expected) {
        Instant actual = OracleInstantUtils.lastDay(input, SHANGHAI);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("lastDay 方法异常场景测试：null 参数")
    public void testLastDayExceptions() {
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.lastDay(null));
        // 带时区版本：时区为 null
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.lastDay(Instant.now(), null));
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

    // -------------------------------------------------------------------------
    // truncInstantWithZone（上海时区 UTC+8）
    // -------------------------------------------------------------------------

    /**
     * truncInstantWithZone(zoneId, instant, format) 场景数据。
     * <p>案例日期与 {@link #testTruncInstantProvider()} 完全相同（年/月/日/时/分/秒数字不变），
     * 但以上海时区（UTC+8）解释该本地时间后再转为 Instant，验证方法能够在指定时区下正确执行截断。
     * <p>输入和期望均通过 {@code ZonedDateTime.of(..., SHANGHAI).toInstant()} 构造，
     * 确保截断逻辑与时区挂钩，而非直接在 UTC 上操作。
     */
    static Stream<Arguments> testTruncInstantWithZoneProvider() {
        // 所有输入均携带非零纳秒（123_456_789），期望值纳秒为 0，
        // 以验证截断方法能够正确清除亚秒精度。
        final int nano = 123_456_789;
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                // 2001 年属于第 21 世纪（2001-2100），截断到 2001-01-01
                Arguments.of("世纪截断-CC",    sh(2001, 4, 22, 10, 14,  6, nano), "CC",    sh(2001, 1,  1,  0,  0,  0)),
                Arguments.of("世纪截断-SCC",   sh(2000, 4, 22, 10, 14,  6, nano), "SCC",   sh(1901, 1,  1,  0,  0,  0)),

                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                // 截断到当年 1 月 1 日零点
                Arguments.of("年份截断-SYYYY", sh(2024, 12, 31, 23, 59, 59, nano), "SYYYY", sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YYYY",  sh(2024, 12, 31, 23, 59, 59, nano), "YYYY",  sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YEAR",  sh(2024, 12, 31, 23, 59, 59, nano), "YEAR",  sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-SYEAR", sh(2024, 12, 31, 23, 59, 59, nano), "SYEAR", sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YYY",   sh(2021,  1,  1, 10, 19, 19, nano), "YYY",   sh(2021, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YY",    sh(2022,  1,  1, 12, 19, 19, nano), "YY",    sh(2022, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-Y",     sh(2023,  1,  1, 12, 19, 19, nano), "Y",     sh(2023, 1,  1,  0,  0,  0)),

                // ---- ISO 年 IYYY / IYY / IY / I ----
                // 2024-01-01（周一）属于 ISO 2024 年，ISO 2024 年首日 = 2024-01-01
                Arguments.of("ISO年截断-IYYY", sh(2024,  1,  1, 12, 19, 19, nano), "IYYY",  sh(2024, 1,  1,  0,  0,  0)),
                // 2021-01-01（周五）属于 ISO 2020 年，ISO 2020 年首日 = 2019-12-30
                Arguments.of("ISO年截断-IYY",  sh(2021,  1,  1, 10, 19, 19, nano), "IYY",   sh(2019, 12, 30,  0,  0,  0)),
                // 2022-01-01（周六）属于 ISO 2021 年，ISO 2021 年首日 = 2021-01-04
                Arguments.of("ISO年截断-IY",   sh(2022,  1,  1, 12, 19, 19, nano), "IY",    sh(2021, 1,  4,  0,  0,  0)),
                // 2023-01-01（周日）属于 ISO 2022 年，ISO 2022 年首日 = 2022-01-03
                Arguments.of("ISO年截断-I",    sh(2023,  1,  1, 12, 19, 19, nano), "I",     sh(2022, 1,  3,  0,  0,  0)),

                // ---- 季度 Q ----
                // 5 月属于 Q2，Q2 首日 = 4 月 1 日
                Arguments.of("季度截断-Q",     sh(2024,  5, 15,  8, 30,  0, nano), "Q",     sh(2024, 4,  1,  0,  0,  0)),

                // ---- 月份 MONTH / MON / MM / RM ----
                // 截断到本月 1 日零点
                Arguments.of("月份截断-MONTH", sh(2024, 12, 31, 23, 59, 59, nano), "MONTH", sh(2024, 12,  1,  0,  0,  0)),
                Arguments.of("月份截断-MON",   sh(2024, 10, 30, 23, 59, 59, nano), "MON",   sh(2024, 10,  1,  0,  0,  0)),
                Arguments.of("月份截断-MM",    sh(2024,  2, 29, 23, 59, 59, nano), "MM",    sh(2024,  2,  1,  0,  0,  0)),
                Arguments.of("月份截断-RM",    sh(2024,  1,  1,  0,  0,  1, nano), "RM",    sh(2024,  1,  1,  0,  0,  0)),

                // ---- 年内周 WW ----
                // 2026-01-01 为周四，2026-04-22（周三）往前 6 天对齐到上一个周四 2026-04-16
                Arguments.of("年内周截断-WW",  sh(2026,  4, 22, 15, 20, 30, nano), "WW",    sh(2026,  4, 16,  0,  0,  0)),

                // ---- 月内周 W ----
                // 2026-04-01 为周三，2026-04-22 也是周三（间隔恰好 3 周），偏移量整除 7，截断到当天零点
                Arguments.of("月内周截断-W",   sh(2026,  4, 22, 20, 19, 19, nano), "W",     sh(2026,  4, 22,  0,  0,  0)),
                // 2026-04-01 为周三，2026-04-26 是周日，本周第一天为 2026-04-22 周三
                Arguments.of("月内周截断-W",   sh(2026,  4, 26,  9, 19, 19, nano), "W",     sh(2026,  4, 22,  0,  0,  0)),

                // ---- ISO 周 IW ----
                // 2026-04-26（周日）对应 ISO 周的周一 = 2026-04-20
                Arguments.of("ISO周截断-IW",   sh(2026,  4, 26,  9, 19, 19, nano), "IW",    sh(2026,  4, 20,  0,  0,  0)),

                // ---- 天 DDD / DD / J ----
                // 截断到当天零点
                Arguments.of("天截断-DDD",     sh(2026,  4, 26, 14, 45, 12, nano), "DDD",   sh(2026,  4, 26,  0,  0,  0)),
                Arguments.of("天截断-DD",      sh(2026,  4, 25, 14, 45, 12, nano), "DD",    sh(2026,  4, 25,  0,  0,  0)),
                Arguments.of("天截断-J",       sh(2026,  4, 20,  0,  0,  1, nano), "J",     sh(2026,  4, 20,  0,  0,  0)),

                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                // 2026-04-26 为周日，本身即周首日
                Arguments.of("周截断-DAY",     sh(2026,  4, 26, 16, 19, 19, nano), "DAY",   sh(2026,  4, 26,  0,  0,  0)),
                // 2026-04-25（周六）往前 6 天到周日 2026-04-19
                Arguments.of("周截断-DY",      sh(2026,  4, 25, 16, 19, 19, nano), "DY",    sh(2026,  4, 19,  0,  0,  0)),
                // 2026-04-20（周一）往前 1 天到周日 2026-04-19
                Arguments.of("周截断-D",       sh(2026,  4, 20,  0,  0,  1, nano), "D",     sh(2026,  4, 19,  0,  0,  0)),

                // ---- 小时 HH / HH12 / HH24 ----
                // 保留小时，分钟和秒清零
                Arguments.of("小时截断-HH",    sh(2024,  5, 23, 18, 59, 59, nano), "HH",    sh(2024,  5, 23, 18,  0,  0)),
                Arguments.of("小时截断-HH12",  sh(2024,  5, 23, 18,  0,  1, nano), "HH12",  sh(2024,  5, 23, 18,  0,  0)),
                Arguments.of("小时截断-HH24",  sh(2024,  5, 23, 18, 31, 59, nano), "HH24",  sh(2024,  5, 23, 18,  0,  0)),

                // ---- 分钟 MI ----
                // 保留分钟，秒清零
                Arguments.of("分钟截断-MI",    sh(2024,  5, 23, 18, 55, 59, nano), "MI",    sh(2024,  5, 23, 18, 55,  0))
        );
    }

    @DisplayName("truncInstantWithZone(zoneId, instant, format) 方法测试（上海时区）")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncInstantWithZoneProvider")
    public void testTruncInstantWithZone(String caseId, Instant input, String format, Instant expected) {
        Instant actual = OracleInstantUtils.truncInstantWithZone(SHANGHAI, input, format);
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("truncInstantWithZone(zoneId, instant) 方法测试（上海时区，无格式）")
    @Test
    public void testTruncInstantWithZoneNoFormat() {
        Instant actual = OracleInstantUtils.truncInstantWithZone(
                SHANGHAI, sh(2026, 4, 25, 14, 45, 12, 123));
        Assertions.assertEquals(sh(2026, 4, 25, 0, 0, 0), actual);
    }

    /**
     * 异常场景：传入 Oracle 不支持的格式（如 "XX"）时，应抛出 {@link IllegalArgumentException}。
     */
    @Test
    @DisplayName("truncInstantWithZone 方法异常场景测试：不支持的格式（上海时区）")
    public void testTruncInstantWithZoneInvalidFormat() {
        Instant now = sh(2026, 4, 22, 10, 14, 6, 123_456);
        assertThrows(IllegalArgumentException.class, () -> OracleInstantUtils.truncInstantWithZone(SHANGHAI, now, "XX"));
    }
}
