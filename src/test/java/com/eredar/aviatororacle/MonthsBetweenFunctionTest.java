package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 测试：模拟Oracle数据库 {@code months_between()} 方法
 */
@DisplayName("months_between 方法测试")
public class MonthsBetweenFunctionTest {

    /*
     * Aviator 表达式：两参数形式，语义为 months_between(endDate, beginDate)。
     * <p>注意：变量名避免使用 {@code begin}，否则可能被 Aviator 词法当作关键字解析导致语法错误。
     */
    private static final String EXPR_TWO_ARGS = "months_between(endDate, beginDate)";

    /*
     * Aviator 表达式：三参数形式，第三参为 ZoneId 字符串（如 {@code Europe/Paris}）。
     */
    private static final String EXPR_THREE_ARGS = "months_between(endDate, beginDate, zoneId)";

    /**
     * 构造两参变量表：endDate 为结束时刻，beginDate 为起始时刻。
     */
    private static Map<String, Object> varsTwoArgs(Instant endDate, Instant beginDate) {
        return HashMapBuilder.<String, Object>builder()
                .put("endDate", endDate)
                .put("beginDate", beginDate)
                .build();
    }

    /**
     * 构造三参变量表：在相同时刻上指定解释用的时区字符串。
     */
    private static Map<String, Object> varsThreeArgs(Instant endDate, Instant beginDate, String zoneId) {
        return HashMapBuilder.<String, Object>builder()
                .put("endDate", endDate)
                .put("beginDate", beginDate)
                .put("zoneId", zoneId)
                .build();
    }

    static Stream<Arguments> monthsBetweenPlusProvider() {
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

    @DisplayName("months_between(end,begin)：正数场景")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("monthsBetweenPlusProvider")
    public void testMonthsBetweenPlus(String caseId, Instant endDate, Instant beginDate, OraDecimal expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, varsTwoArgs(endDate, beginDate));
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> monthsBetweenPlusWithZoneProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        Instant.parse("2023-03-15T15:14:22Z"),
                        Instant.parse("2023-01-15T15:47:39Z"),
                        "Asia/Tokyo",
                        new OraDecimal("2")
                ),
                Arguments.of(
                        "均为月末",
                        Instant.parse("2023-02-28T15:14:22Z"),
                        Instant.parse("2023-01-31T15:47:39Z"),
                        "Europe/Berlin",
                        new OraDecimal("1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        Instant.parse("2024-02-28T15:14:22Z"),
                        Instant.parse("2024-01-31T15:47:39Z"),
                        "Europe/Berlin",
                        new OraDecimal("0.9024802120669056152927120669056152927121")
                ),
                Arguments.of(
                        "同一天",
                        Instant.parse("2023-01-31T15:47:39Z"),
                        Instant.parse("2023-01-31T15:14:22Z"),
                        "America/Chicago",
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        Instant.parse("2024-02-29T02:50:39Z"),
                        Instant.parse("2024-02-22T22:02:39Z"),
                        "Pacific/Auckland",
                        new OraDecimal("0.2")
                ),
                Arguments.of(
                        "含0小数",
                        Instant.parse("2024-02-29T15:11:53Z"),
                        Instant.parse("2024-02-28T14:50:39Z"),
                        "Asia/Dubai",
                        new OraDecimal("0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "1秒是零点几月",
                        Instant.parse("2024-02-29T07:00:00Z"),
                        Instant.parse("2024-02-29T06:59:59Z"),
                        "America/Denver",
                        new OraDecimal("0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数，夏令时",
                        Instant.parse("2033-10-28T01:14:11Z"),
                        Instant.parse("2013-01-31T23:24:39Z"), // 夏令时
                        "Europe/Rome",
                        new OraDecimal("248.874765531660692951015531660692951016")
                ),
                Arguments.of(
                        "整数+1秒",
                        Instant.parse("2033-09-30T15:00:00Z"),
                        Instant.parse("2013-01-31T14:59:59Z"),
                        "Asia/Seoul",
                        new OraDecimal("248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("months_between(end,begin,zone)：正数场景")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}, zoneId={3}")
    @MethodSource("monthsBetweenPlusWithZoneProvider")
    public void testMonthsBetweenPlusWithZone(String caseId, Instant endDate, Instant beginDate, String zoneId, OraDecimal expected) {
        Object actual = AviatorInstance.execute(EXPR_THREE_ARGS, varsThreeArgs(endDate, beginDate, zoneId));
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> monthsBetweenNegProvider() {
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

    @DisplayName("months_between(end,begin)：负数场景")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("monthsBetweenNegProvider")
    public void testMonthsBetweenNeg(String caseId, Instant endDate, Instant beginDate, OraDecimal expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, varsTwoArgs(endDate, beginDate));
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> monthsBetweenNegWithZoneProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        Instant.parse("2023-01-15T15:47:39Z"),
                        Instant.parse("2023-03-15T15:14:22Z"),
                        "Asia/Tokyo",
                        new OraDecimal("-2")
                ),
                Arguments.of(
                        "均为月末",
                        Instant.parse("2023-01-31T15:47:39Z"),
                        Instant.parse("2023-02-28T15:14:22Z"),
                        "Europe/Berlin",
                        new OraDecimal("-1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        Instant.parse("2024-01-31T15:47:39Z"),
                        Instant.parse("2024-02-28T15:14:22Z"),
                        "Europe/Berlin",
                        new OraDecimal("-0.9024802120669056152927120669056152927121")
                ),
                Arguments.of(
                        "同一天",
                        Instant.parse("2023-01-31T15:14:22Z"),
                        Instant.parse("2023-01-31T15:47:39Z"),
                        "America/Chicago",
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        Instant.parse("2024-02-22T22:02:39Z"),
                        Instant.parse("2024-02-29T02:50:39Z"),
                        "Pacific/Auckland",
                        new OraDecimal("-0.2")
                ),
                Arguments.of(
                        "含0小数",
                        Instant.parse("2024-02-28T14:50:39Z"),
                        Instant.parse("2024-02-29T15:11:53Z"),
                        "Asia/Dubai",
                        new OraDecimal("-0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "1秒是零点几月",
                        Instant.parse("2024-02-29T06:59:59Z"),
                        Instant.parse("2024-02-29T07:00:00Z"),
                        "America/Denver",
                        new OraDecimal("-0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数，夏令时",
                        Instant.parse("2013-01-31T23:24:39Z"),
                        Instant.parse("2033-10-28T01:14:11Z"), // 夏令时
                        "Europe/Rome",
                        new OraDecimal("-248.874765531660692951015531660692951016")
                ),
                Arguments.of(
                        "整数+1秒",
                        Instant.parse("2013-01-31T14:59:59Z"),
                        Instant.parse("2033-09-30T15:00:00Z"),
                        "Asia/Seoul",
                        new OraDecimal("-248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("months_between(end,begin,zone)：负数场景")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}, zoneId={3}")
    @MethodSource("monthsBetweenNegWithZoneProvider")
    public void testMonthsBetweenNegWithZone(String caseId, Instant endDate, Instant beginDate, String zoneId, OraDecimal expected) {
        Object actual = AviatorInstance.execute(EXPR_THREE_ARGS, varsThreeArgs(endDate, beginDate, zoneId));
        Assertions.assertEquals(expected, actual);
    }

    // -------------------------------------------------------------------------
    // 与 OraFuncUtilsTest#testMonthsBetweenExceptions 场景对应（不做时区变体）
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("months_between：与工具类一致的非法入参（null）")
    public void testMonthsBetweenExpressionExceptions() {
        Instant now = Instant.now();
        // beginDate 为 null：与 OraFuncUtils.monthsBetween(now, null) 同属非法
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, varsTwoArgs(now, null))
        );
        // 三参形式下 zoneId 为 null：与 OraFuncUtils.monthsBetween(end, begin, null) 同属非法
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_THREE_ARGS, varsThreeArgs(now, now.plus(1, ChronoUnit.DAYS), null))
        );
    }
}
