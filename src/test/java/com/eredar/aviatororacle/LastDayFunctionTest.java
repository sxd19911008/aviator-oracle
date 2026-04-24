package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.eredar.aviatororacle.utils.AODateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试：模拟Oracle数据库 {@code last_day()} 方法。
 */
@DisplayName("last_day 方法测试")
public class LastDayFunctionTest {

    /** 单参数表达式：last_day(date) */
    private static final String EXPR_ONE_ARG = "last_day(d)";
    /** 双参数表达式：last_day(instant, zoneId) */
    private static final String EXPR_TWO_ARGS = "last_day(d, z)";

    /** 上海时区（UTC+8） */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    /** LocalDateTime 格式化器 */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── 辅助方法 ─────────────────────────────────────────────────────────────

    private static Date date(String s) {
        return AODateUtils.strToDate(s);
    }

    private static LocalDateTime ldt(String s) {
        return LocalDateTime.parse(s, FMT);
    }

    private static Instant sh(int year, int month, int day, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, SHANGHAI).toInstant();
    }

    private static Instant sh(int year, int month, int day, int hour, int minute, int second, int nano) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, nano, SHANGHAI).toInstant();
    }

    // ── 变量构造辅助方法 ──────────────────────────────────────────────────────

    private static Map<String, Object> vars1(Object d) {
        return HashMapBuilder.<String, Object>builder().put("d", d).build();
    }

    private static Map<String, Object> vars2(Object d, Object z) {
        return HashMapBuilder.<String, Object>builder().put("d", d).put("z", z).build();
    }

    // =========================================================================
    // Date 类型
    // =========================================================================

    static Stream<Arguments> testLastDayDateProvider() {
        return Stream.of(
                // ---- 31天月份 ----
                Arguments.of("31天月份(1月)",
                        date("2024-01-15 10:30:45"), date("2024-01-31 10:30:45")),
                // ---- 30天月份 ----
                Arguments.of("30天月份(4月)",
                        date("2024-04-10 14:22:33"), date("2024-04-30 14:22:33")),
                // ---- 闰年2月 → 29日 ----
                Arguments.of("闰年2月",
                        date("2024-02-15 08:15:00"), date("2024-02-29 08:15:00")),
                // ---- 非闰年2月 → 28日 ----
                Arguments.of("非闰年2月",
                        date("2023-02-15 08:15:00"), date("2023-02-28 08:15:00")),
                // ---- 已是月末，返回自身（时分秒不变） ----
                Arguments.of("已是月末(3月31日)",
                        date("2024-03-31 23:59:59"), date("2024-03-31 23:59:59")),
                // ---- 12月（年末边界） ----
                Arguments.of("12月(年末)",
                        date("2024-12-01 00:00:00"), date("2024-12-31 00:00:00")),
                // ---- 月初第1天 ----
                Arguments.of("月初第1天",
                        date("2024-07-01 06:30:00"), date("2024-07-31 06:30:00")),
                // ---- 闰年2月29日已是月末 ----
                Arguments.of("闰年2月29日(已是月末)",
                        date("2024-02-29 12:00:00"), date("2024-02-29 12:00:00"))
        );
    }

    @DisplayName("last_day(date)：Date 场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayDateProvider")
    public void testLastDayDate(String caseId, Date input, Date expected) {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(input));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // LocalDateTime 类型
    // =========================================================================

    static Stream<Arguments> testLastDayLocalDateTimeProvider() {
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

    @DisplayName("last_day(date)：LocalDateTime 场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayLocalDateTimeProvider")
    public void testLastDayLocalDateTime(String caseId, LocalDateTime input, LocalDateTime expected) {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(input));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // Instant 类型（UTC）
    // =========================================================================

    static Stream<Arguments> testLastDayInstantProvider() {
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

    @DisplayName("last_day(instant)：Instant UTC 场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayInstantProvider")
    public void testLastDayInstant(String caseId, Instant input, Instant expected) {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(input));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // Instant 类型（上海时区）
    // =========================================================================

    static Stream<Arguments> testLastDayInstantWithZoneProvider() {
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

    @DisplayName("last_day(instant, zoneId)：Instant 上海时区场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}")
    @MethodSource("testLastDayInstantWithZoneProvider")
    public void testLastDayInstantWithZone(String caseId, Instant input, Instant expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, "Asia/Shanghai"));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // 类型错误异常场景：单参数公式 last_day(d)
    // =========================================================================

    static Stream<Arguments> testLastDay1ErrorProvider() {
        return Stream.of(
                // last_day 单参数版本入参仅支持 Date/LocalDateTime/Instant，
                // 以下8种类型均不在支持范围内，应抛出 IllegalArgumentException
                Arguments.of("Long 类型作为date入参",       1234567890L),
                Arguments.of("Integer 类型作为date入参",    100),
                Arguments.of("BigInteger 类型作为date入参", new BigInteger("99999999999999")),
                Arguments.of("Double 类型作为date入参",     3.14d),
                Arguments.of("BigDecimal 类型作为date入参", new BigDecimal("1.23456")),
                Arguments.of("OraDecimal 类型作为date入参", new OraDecimal("9.99")),
                Arguments.of("String 类型作为date入参",     "2024-01-01"),
                Arguments.of("Boolean 类型作为date入参",    Boolean.TRUE)
        );
    }

    @DisplayName("last_day(d)：日期入参类型错误抛出 IllegalArgumentException")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testLastDay1ErrorProvider")
    public void testLastDay1Error(String caseId, Object invalidDate) {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_ONE_ARG, vars1(invalidDate)));
    }

    // =========================================================================
    // 类型错误异常场景：双参数公式 last_day(d, z)
    // =========================================================================

    static Stream<Arguments> testLastDay2ErrorProvider() {
        return Stream.of(
                // last_day 双参数版本第1个入参必须是 Instant，
                // 以下8种类型均不合法，应抛出 IllegalArgumentException
                Arguments.of("Long 类型作为date入参",       1234567890L),
                Arguments.of("Integer 类型作为date入参",    100),
                Arguments.of("BigInteger 类型作为date入参", new BigInteger("99999999999999")),
                Arguments.of("Double 类型作为date入参",     3.14d),
                Arguments.of("BigDecimal 类型作为date入参", new BigDecimal("1.23456")),
                Arguments.of("OraDecimal 类型作为date入参", new OraDecimal("9.99")),
                Arguments.of("String 类型作为date入参",     "2024-01-01"),
                Arguments.of("Boolean 类型作为date入参",    Boolean.TRUE)
        );
    }

    @DisplayName("last_day(d, z)：日期入参类型错误抛出 IllegalArgumentException")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testLastDay2ErrorProvider")
    public void testLastDay2Error(String caseId, Object invalidDate) {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(invalidDate, "Asia/Shanghai")));
    }

    // =========================================================================
    // 异常场景
    // =========================================================================

    @Test
    @DisplayName("last_day(date)：Date 非法入参（null）")
    public void testLastDayDateExceptions() {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_ONE_ARG, vars1(null)));
    }

    @Test
    @DisplayName("last_day(date)：LocalDateTime 非法入参（null）")
    public void testLastDayLocalDateTimeExceptions() {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_ONE_ARG, vars1(null)));
    }

    @Test
    @DisplayName("last_day：Instant 非法入参（null）")
    public void testLastDayInstantExceptions() {
        // date 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_ONE_ARG, vars1(null)));
        // 带时区版本：zoneId 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(Instant.now(), null)));
        // 带时区版本：zoneId 为 Number
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(Instant.now(), 1)));
    }
}
