package com.eredar.aviatororacle.runtime.utils;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.uitls.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Utils} 单元测试类
 */
public class UtilsTest {

    // -------------------------------------------------------------------------
    // monthsBetween
    // -------------------------------------------------------------------------

    static Stream<Arguments> testMonthsBetweenPlusProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        Instant.parse("2023-01-15T15:47:39Z"),
                        Instant.parse("2023-03-15T01:14:22Z"),
                        new OraDecimal("2")
                ),
                Arguments.of(
                        "均为月末",
                        Instant.parse("2023-01-31T15:47:39Z"),
                        Instant.parse("2023-02-28T01:14:22Z"),
                        new OraDecimal("1")
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
                        new OraDecimal("0.2")
                ),
                Arguments.of(
                        "含0小数",
                        Instant.parse("2024-02-28T14:50:39Z"),
                        Instant.parse("2024-02-29T15:11:53Z"),
                        new OraDecimal("0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "1秒是零点几月",
                        Instant.parse("2024-02-28T23:59:59Z"),
                        Instant.parse("2024-02-29T00:00:00Z"),
                        new OraDecimal("0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        Instant.parse("2013-01-31T23:24:39Z"),
                        Instant.parse("2033-10-28T01:14:11Z"),
                        new OraDecimal("248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        Instant.parse("2013-01-31T23:59:59Z"),
                        Instant.parse("2033-10-01T00:00:00Z"),
                        new OraDecimal("248.000000373357228195937873357228195938")
                )

        );
    }

    @DisplayName("monthsBetween 方法正数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: beginDate={1}, endDate={2}")
    @MethodSource("testMonthsBetweenPlusProvider")
    void testMonthsBetweenPlus(String caseId, Instant beginDate, Instant endDate, OraDecimal expected) {
        OraDecimal actual = Utils.monthsBetween(beginDate, endDate);
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> testMonthsBetweenNegProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        Instant.parse("2023-03-15T01:14:22Z"),
                        Instant.parse("2023-01-15T15:47:39Z"),
                        new OraDecimal("-2")
                ),
                Arguments.of(
                        "均为月末",
                        Instant.parse("2023-02-28T01:14:22Z"),
                        Instant.parse("2023-01-31T15:47:39Z"),
                        new OraDecimal("-1")
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
                        new OraDecimal("-0.2")
                ),
                Arguments.of(
                        "含0小数",
                        Instant.parse("2024-02-29T15:11:53Z"),
                        Instant.parse("2024-02-28T14:50:39Z"),
                        new OraDecimal("-0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "1秒是零点几月",
                        Instant.parse("2024-02-29T00:00:00Z"),
                        Instant.parse("2024-02-28T23:59:59Z"),
                        new OraDecimal("-0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        Instant.parse("2033-10-28T01:14:11Z"),
                        Instant.parse("2013-01-31T23:24:39Z"),
                        new OraDecimal("-248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        Instant.parse("2033-10-01T00:00:00Z"),
                        Instant.parse("2013-01-31T23:59:59Z"),
                        new OraDecimal("-248.000000373357228195937873357228195938")
                )

        );
    }

    @DisplayName("monthsBetween 方法负数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: beginDate={1}, endDate={2}")
    @MethodSource("testMonthsBetweenNegProvider")
    void testMonthsBetweenNeg(String caseId, Instant beginDate, Instant endDate, OraDecimal expected) {
        OraDecimal actual = Utils.monthsBetween(beginDate, endDate);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("monthsBetween 方法异常场景测试")
    void testMonthsBetweenExceptions() {
        Instant now = Instant.now();
        assertThrows(IllegalArgumentException.class, () -> Utils.monthsBetween(null, now));
        assertThrows(IllegalArgumentException.class, () -> Utils.monthsBetween(now.plus(1, ChronoUnit.DAYS), now, null));
    }

    // -------------------------------------------------------------------------
    // decode：使用 Supplier 封装调用，避免把 Object[] 误当成 decode 的单个变参元素
    // -------------------------------------------------------------------------

    /**
     * decode 正常返回值场景：最后一列 {@code null} 表示期望 {@link Utils#decode} 返回 null。
     */
    static Stream<Arguments> decodeMatchProvider() {
        return Stream.of(
                Arguments.of("匹配第一个 search", (Supplier<Object>) () -> Utils.decode("1", "1", "A", "2", "B", "C"), "A"),
                Arguments.of("匹配第二个 search", (Supplier<Object>) () -> Utils.decode("2", "1", "A", "2", "B", "C"), "B"),
                Arguments.of("无匹配返回默认值", (Supplier<Object>) () -> Utils.decode("3", "1", "A", "2", "B", "C"), "C"),
                Arguments.of("无匹配且无默认值返回 null", (Supplier<Object>) () -> Utils.decode("3", "1", "A", "2", "B"), null),
                Arguments.of("null 与 null 匹配", (Supplier<Object>) () -> Utils.decode(null, null, "Result", "Other"), "Result"),
                Arguments.of("表达式为 null 且 search 非 null", (Supplier<Object>) () -> Utils.decode(null, "1", "Result", "Default"), "Default"),
                Arguments.of("Integer 与 Long 数值相等", (Supplier<Object>) () -> Utils.decode(100, 100L, "Match", "No Match"), "Match"),
                Arguments.of("Long 与 OraDecimal 数值相等", (Supplier<Object>) () -> Utils.decode(200L, new OraDecimal("200.00"), "Match", "No Match"), "Match"),
                Arguments.of("OraDecimal 不同精度数值相等", (Supplier<Object>) () -> Utils.decode(new OraDecimal("3.14"), new OraDecimal("3.1400"), "Match", "No Match"), "Match")
        );
    }

    @DisplayName("decode 方法匹配与返回值测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeMatchProvider")
    void testDecodeMatch(String caseId, Supplier<Object> decodeCall, Object expected) {
        if (expected == null) {
            Assertions.assertNull(decodeCall.get());
        } else {
            Assertions.assertEquals(expected, decodeCall.get());
        }
    }

    static Stream<Arguments> decodeInvalidArgsProvider() {
        //noinspection Convert2MethodRef
        return Stream.of(
                Arguments.of("入参仅2个", (Executable) () -> Utils.decode("1", "2")),
                Arguments.of("入参仅1个", (Executable) () -> Utils.decode("1")),
                Arguments.of("入参0个", (Executable) () -> Utils.decode())
        );
    }

    @DisplayName("decode 方法非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeInvalidArgsProvider")
    void testDecodeInvalidArgs(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }

    // -------------------------------------------------------------------------
    // nvl
    // -------------------------------------------------------------------------

    static Stream<Arguments> nvlProvider() {
        return Stream.of(
                Arguments.of("expr1 为 null 时返回替换值", (Supplier<Object>) () -> Utils.nvl(null, "Default"), "Default"),
                Arguments.of("expr1 非 null 时返回自身", (Supplier<Object>) () -> Utils.nvl("Value", "Default"), "Value")
        );
    }

    @DisplayName("nvl 方法测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("nvlProvider")
    void testNvl(String caseId, Supplier<Object> nvlCall, Object expected) {
        Assertions.assertEquals(expected, nvlCall.get());
    }

    // -------------------------------------------------------------------------
    // yearsBetween
    // -------------------------------------------------------------------------

    static Stream<Arguments> yearsBetweenLProvider() {
        Instant beginDate = Instant.parse("2020-01-01T00:00:00Z");
        return Stream.of(
                Arguments.of("正好1年", beginDate, Instant.parse("2021-01-01T00:00:00Z"), 1L),
                Arguments.of("不满1年舍去为 0", beginDate, Instant.parse("2020-12-31T23:59:00Z"), 0L),
                Arguments.of("超过1年仍为1", beginDate, Instant.parse("2021-01-01T00:00:01Z"), 1L),
                Arguments.of("相同日期", Instant.parse("2021-03-21T16:35:29Z"), Instant.parse("2021-03-21T16:35:29Z"), 0L)
        );
    }

    @DisplayName("yearsBetween 方法 L 类型（不满 1 年舍去）测试")
    @ParameterizedTest(name = "【{index}】{0}: beginDate={1}, endDate={2}")
    @MethodSource("yearsBetweenLProvider")
    void testYearsBetweenL(String caseId, Instant beginDate, Instant endDate, long expected) {
        Assertions.assertEquals(expected, Utils.yearsBetween(beginDate, endDate, "L"));
    }

    static Stream<Arguments> yearsBetweenYProvider() {
        Instant beginDate = Instant.parse("2020-01-01T00:00:00Z");
        return Stream.of(
                Arguments.of("正好1年", beginDate, Instant.parse("2021-01-01T00:00:00Z"), 1L),
                Arguments.of("不满1年算1年", beginDate, Instant.parse("2020-01-02T00:00:00Z"), 1L),
                Arguments.of("略超1年算2年", beginDate, Instant.parse("2021-01-01T00:00:01Z"), 2L),
                Arguments.of("相同日期", Instant.parse("2021-03-21T16:35:29Z"), Instant.parse("2021-03-21T16:35:29Z"), 0L)
        );
    }

    @DisplayName("yearsBetween 方法 Y 类型（不满 1 年算 1 年）测试")
    @ParameterizedTest(name = "【{index}】{0}: beginDate={1}, endDate={2}")
    @MethodSource("yearsBetweenYProvider")
    void testYearsBetweenY(String caseId, Instant beginDate, Instant endDate, long expected) {
        Assertions.assertEquals(expected, Utils.yearsBetween(beginDate, endDate, "Y"));
    }

    @Test
    @DisplayName("yearsBetween 方法异常场景测试")
    void testYearsBetweenExceptions() {
        Instant now = Instant.now();
        assertThrows(IllegalArgumentException.class, () -> Utils.yearsBetween(null, now, "L"));
        assertThrows(IllegalArgumentException.class, () -> Utils.yearsBetween(now.plus(1, ChronoUnit.DAYS), now, "L"));
        assertThrows(IllegalArgumentException.class, () -> Utils.yearsBetween(now, now.plus(10, ChronoUnit.DAYS), "X"));
    }
}
