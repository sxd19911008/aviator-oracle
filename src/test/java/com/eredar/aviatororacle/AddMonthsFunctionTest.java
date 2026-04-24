package com.eredar.aviatororacle;

import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.eredar.aviatororacle.utils.AODateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
 * 测试：模拟Oracle数据库 {@code add_months()} 方法。
 */
@DisplayName("add_months 方法测试")
public class AddMonthsFunctionTest {

    /** 两参数表达式：add_months(date, months) */
    private static final String EXPR_TWO_ARGS = "add_months(d, m)";
    /** 三参数表达式：add_months(instant, months, zoneId) */
    private static final String EXPR_THREE_ARGS = "add_months(d, m, z)";

    /** 上海时区（UTC+8），用于 addMonths with zone 系列测试 */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    /** LocalDateTime 格式化器 */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── 辅助方法 ─────────────────────────────────────────────────────────────

    /** 将日期时间字符串（格式 "yyyy-MM-dd HH:mm:ss"）解析为 {@link Date} */
    private static Date date(String s) {
        return AODateUtils.strToDate(s);
    }

    /** 将日期时间字符串（格式 "yyyy-MM-dd HH:mm:ss"）解析为 {@link LocalDateTime} */
    private static LocalDateTime ldt(String s) {
        return LocalDateTime.parse(s, FMT);
    }

    /**
     * 以上海时区构造 {@link Instant}（纳秒固定为 0）
     */
    private static Instant sh(int year, int month, int day, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, SHANGHAI).toInstant();
    }

    /**
     * 以上海时区构造 {@link Instant}（可指定纳秒），用于携带亚秒精度的输入和期望值
     */
    private static Instant sh(int year, int month, int day, int hour, int minute, int second, int nano) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, nano, SHANGHAI).toInstant();
    }

    // ── 两参数变量构造辅助方法 ────────────────────────────────────────────────

    private static Map<String, Object> vars2(Object d, Object m) {
        return HashMapBuilder.<String, Object>builder()
                .put("d", d).put("m", m)
                .build();
    }

    private static Map<String, Object> vars3(Object d, Object m, String z) {
        return HashMapBuilder.<String, Object>builder()
                .put("d", d).put("m", m).put("z", z)
                .build();
    }

    // =========================================================================
    // Date 类型
    // =========================================================================

    static Stream<Arguments> testAddMonthsDateProvider() {
        return Stream.of(
                // ---- 正常场景：日号在目标月份范围内，日号和时分秒均保持不变 ----
                Arguments.of("正常场景：月中日期+正数月份",
                        date("2024-03-15 10:30:45"), 2, date("2024-05-15 10:30:45")),
                Arguments.of("月数为0，日期不变",
                        date("2024-01-15 23:59:59"), 0, date("2024-01-15 23:59:59")),
                Arguments.of("29日+1月到闰年2月，日号不变",
                        date("2024-01-29 10:00:00"), 1, date("2024-02-29 10:00:00")),
                Arguments.of("大跨度：+24个月跨2年",
                        date("2024-06-15 08:30:00"), 24, date("2026-06-15 08:30:00")),

                // ---- 月末→月末：原始日期是所在月份最后一天，结果一定是目标月份最后一天 ----
                Arguments.of("月末→月末：闰年2月29日+1月→3月31日",
                        date("2024-02-29 14:22:33"), 1, date("2024-03-31 14:22:33")),
                Arguments.of("月末→月末：1月31日+1月→闰年2月29日",
                        date("2024-01-31 08:15:00"), 1, date("2024-02-29 08:15:00")),
                Arguments.of("月末→月末：非闰年1月31日+1月→2月28日",
                        date("2023-01-31 08:15:00"), 1, date("2023-02-28 08:15:00")),
                Arguments.of("月末→月末：4月30日-2月→非闰年2月28日",
                        date("2023-04-30 12:00:00"), -2, date("2023-02-28 12:00:00")),
                Arguments.of("月末→月末：跨年11月30日+3月→2月28日",
                        date("2024-11-30 10:00:00"), 3, date("2025-02-28 10:00:00")),
                Arguments.of("月末→月末：3月31日-1月→闰年2月29日",
                        date("2024-03-31 10:00:00"), -1, date("2024-02-29 10:00:00")),
                Arguments.of("月末→月末：非闰年2月28日+12月→闰年2月29日",
                        date("2023-02-28 18:00:00"), 12, date("2024-02-29 18:00:00")),
                Arguments.of("月末→月末：闰年2月29日-12月→非闰年2月28日",
                        date("2024-02-29 18:00:00"), -12, date("2023-02-28 18:00:00")),

                // ---- 日号超限：原始日号 > 目标月份最大天数，设为目标月份最后一天 ----
                Arguments.of("日号超限：29日+1月到非闰年2月→28日",
                        date("2023-01-29 10:00:00"), 1, date("2023-02-28 10:00:00")),

                // ---- 负数月份：向前回退月份 ----
                Arguments.of("负数月份：月中日期-3月",
                        date("2024-05-20 16:45:30"), -3, date("2024-02-20 16:45:30")),

                // ---- 小数月份：小数部分被截断为整数 ----
                Arguments.of("小数月份被截断：2.7→2",
                        date("2024-06-15 12:00:00"), 2.7, date("2024-08-15 12:00:00")),
                Arguments.of("负数小数月份被截断：-2.9→-2",
                        date("2024-06-15 12:00:00"), -2.9, date("2024-04-15 12:00:00"))
        );
    }

    @DisplayName("add_months(date, months)：Date 场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsDateProvider")
    public void testAddMonthsDate(String caseId, Date input, Number months, Date expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, months));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // LocalDateTime 类型
    // =========================================================================

    static Stream<Arguments> testAddMonthsLocalDateTimeProvider() {
        return Stream.of(
                // ---- 正常场景 ----
                Arguments.of("正常场景：月中日期+正数月份",
                        ldt("2024-03-15 10:30:45"), 2, ldt("2024-05-15 10:30:45")),
                Arguments.of("月数为0，日期不变",
                        ldt("2024-01-15 23:59:59"), 0, ldt("2024-01-15 23:59:59")),
                Arguments.of("29日+1月到闰年2月，日号不变",
                        ldt("2024-01-29 10:00:00"), 1, ldt("2024-02-29 10:00:00")),
                Arguments.of("大跨度：+24个月跨2年",
                        ldt("2024-06-15 08:30:00"), 24, ldt("2026-06-15 08:30:00")),

                // ---- 月末→月末 ----
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

                // ---- 日号超限 ----
                Arguments.of("日号超限：29日+1月到非闰年2月→28日",
                        ldt("2023-01-29 10:00:00"), 1, ldt("2023-02-28 10:00:00")),

                // ---- 负数月份 ----
                Arguments.of("负数月份：月中日期-3月",
                        ldt("2024-05-20 16:45:30"), -3, ldt("2024-02-20 16:45:30")),

                // ---- 小数月份被截断 ----
                Arguments.of("小数月份被截断：2.7→2",
                        ldt("2024-06-15 12:00:00"), 2.7, ldt("2024-08-15 12:00:00")),
                Arguments.of("负数小数月份被截断：-2.9→-2",
                        ldt("2024-06-15 12:00:00"), -2.9, ldt("2024-04-15 12:00:00"))
        );
    }

    @DisplayName("add_months(date, months)：LocalDateTime 场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsLocalDateTimeProvider")
    public void testAddMonthsLocalDateTime(String caseId, LocalDateTime input, Number months, LocalDateTime expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, months));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // Instant 类型（UTC）
    // =========================================================================

    static Stream<Arguments> testAddMonthsInstantProvider() {
        return Stream.of(
                // ---- 正常场景 ----
                Arguments.of("正常场景：月中日期+正数月份",
                        Instant.parse("2024-03-15T10:30:45Z"), 2, Instant.parse("2024-05-15T10:30:45Z")),
                Arguments.of("月数为0，日期不变",
                        Instant.parse("2024-01-15T23:59:59Z"), 0, Instant.parse("2024-01-15T23:59:59Z")),
                Arguments.of("29日+1月到闰年2月，日号不变",
                        Instant.parse("2024-01-29T10:00:00Z"), 1, Instant.parse("2024-02-29T10:00:00Z")),
                Arguments.of("大跨度：+24个月跨2年",
                        Instant.parse("2024-06-15T08:30:00Z"), 24, Instant.parse("2026-06-15T08:30:00Z")),

                // ---- 月末→月末 ----
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

                // ---- 日号超限 ----
                Arguments.of("日号超限：29日+1月到非闰年2月→28日",
                        Instant.parse("2023-01-29T10:00:00Z"), 1, Instant.parse("2023-02-28T10:00:00Z")),

                // ---- 负数月份 ----
                Arguments.of("负数月份：月中日期-3月",
                        Instant.parse("2024-05-20T16:45:30Z"), -3, Instant.parse("2024-02-20T16:45:30Z")),

                // ---- 小数月份被截断 ----
                Arguments.of("小数月份被截断：2.7→2",
                        Instant.parse("2024-06-15T12:00:00Z"), 2.7, Instant.parse("2024-08-15T12:00:00Z")),
                Arguments.of("负数小数月份被截断：-2.9→-2",
                        Instant.parse("2024-06-15T12:00:00Z"), -2.9, Instant.parse("2024-04-15T12:00:00Z"))
        );
    }

    @DisplayName("add_months(instant, months)：Instant UTC 场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsInstantProvider")
    public void testAddMonthsInstant(String caseId, Instant input, Number months, Instant expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, months));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // Instant 类型（上海时区）
    // =========================================================================

    static Stream<Arguments> testAddMonthsInstantWithZoneProvider() {
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

    @DisplayName("add_months(instant, months, zoneId)：Instant 上海时区场景")
    @ParameterizedTest(name = "【{index}】{0}: date={1}, months={2}")
    @MethodSource("testAddMonthsInstantWithZoneProvider")
    public void testAddMonthsInstantWithZone(String caseId, Instant input, Number months, Instant expected) {
        Object actual = AviatorInstance.execute(EXPR_THREE_ARGS, vars3(input, months, "Asia/Shanghai"));
        assertEquals(expected, actual);
    }

    // =========================================================================
    // 异常场景
    // =========================================================================

    @Test
    @DisplayName("add_months(date, months)：Date 非法入参（null）")
    public void testAddMonthsDateExceptions() {
        Date now = new Date();
        // date 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(null, 1)));
        // months 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(now, null)));
        // 两者均为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(null, null)));
    }

    @Test
    @DisplayName("add_months(date, months)：LocalDateTime 非法入参（null）")
    public void testAddMonthsLocalDateTimeExceptions() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(null, 1)));
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(now, null)));
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(null, null)));
    }

    @Test
    @DisplayName("add_months：Instant 非法入参（null）")
    public void testAddMonthsInstantExceptions() {
        Instant now = Instant.now();
        // date 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(null, 1)));
        // months 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(now, null)));
        // 两者均为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(null, null)));
        // 带时区版本：zoneId 为 null
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_THREE_ARGS, vars3(now, 1, null)));
    }
}
