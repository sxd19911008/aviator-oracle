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
                        Instant.parse("2023-03-11T10:43:26Z"),
                        Instant.parse("2025-10-20T22:11:17Z"),
                        new OraDecimal("954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为正，1秒",
                        Instant.parse("2025-10-20T23:59:59Z"),
                        Instant.parse("2025-10-21T00:00:00Z"),
                        new OraDecimal("0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为负，与正序对称取负",
                        Instant.parse("2025-10-20T22:11:17Z"),
                        Instant.parse("2023-03-11T10:43:26Z"),
                        new OraDecimal("-954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为负，1秒",
                        Instant.parse("2025-10-21T00:00:00Z"),
                        Instant.parse("2025-10-20T23:59:59Z"),
                        new OraDecimal("-0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为正，2位整数",
                        Instant.parse("2025-10-10T00:00:37Z"),
                        Instant.parse("2025-10-22T00:00:00Z"),
                        new OraDecimal("11.99957175925925925925925925925925925926")
                ),
                Arguments.of(
                        "结果为正，3位整数",
                        Instant.parse("2025-07-10T00:00:37Z"),
                        Instant.parse("2025-10-22T00:00:00Z"),
                        new OraDecimal("103.999571759259259259259259259259259259")
                )
        );
    }

    @DisplayName("daysBetween 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: beginDate={1}, endDate={2}")
    @MethodSource("testDaysBetweenProvider")
    public void testDaysBetween(String caseId, Instant beginDate, Instant endDate, OraDecimal expected) {
        OraDecimal actual = OracleInstantUtils.daysBetween(beginDate, endDate);
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
}
