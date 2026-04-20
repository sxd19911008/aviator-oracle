package com.eredar.aviatororacle.runtime.utils;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.utils.AODateUtils;
import com.eredar.aviatororacle.runtime.uitls.OracleDateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("基于Date模拟Oracle日期计算测试")
public class OracleDateUtilsTest {

    /**
     * 将 UTC 时间字符串解析为 {@link Date}，方便复用 Instant 的测试数据
     */
    private static Date date(String dateStr) {
        return AODateUtils.strToDate(dateStr);
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
                        date("2025-10-20 22:11:17"),
                        date("2023-03-11 10:43:26"),
                        new OraDecimal("954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为正，1秒",
                        date("2025-10-21 00:00:00"),
                        date("2025-10-20 23:59:59"),
                        new OraDecimal("0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为负，与正序对称取负",
                        date("2023-03-11 10:43:26"),
                        date("2025-10-20 22:11:17"),
                        new OraDecimal("-954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为负，1秒",
                        date("2025-10-20 23:59:59"),
                        date("2025-10-21 00:00:00"),
                        new OraDecimal("-0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为正，2位整数",
                        date("2025-10-22 00:00:00"),
                        date("2025-10-10 00:00:37"),
                        new OraDecimal("11.99957175925925925925925925925925925926")
                ),
                Arguments.of(
                        "结果为正，3位整数",
                        date("2025-10-22 00:00:00"),
                        date("2025-07-10 00:00:37"),
                        new OraDecimal("103.999571759259259259259259259259259259")
                )
        );
    }

    @DisplayName("daysBetween 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testDaysBetweenProvider")
    public void testDaysBetween(String caseId, Date endDate, Date beginDate, OraDecimal expected) {
        OraDecimal actual = OracleDateUtils.daysBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    // -------------------------------------------------------------------------
    // monthsBetween
    // -------------------------------------------------------------------------

    static Stream<Arguments> testMonthsBetweenPlusProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        date("2023-03-15 01:14:22"),
                        date("2023-01-15 15:47:39"),
                        new OraDecimal("2")
                ),
                Arguments.of(
                        "均为月末",
                        date("2023-02-28 01:14:22"),
                        date("2023-01-31 15:47:39"),
                        new OraDecimal("1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        date("2024-02-28 01:14:22"),
                        date("2024-01-31 15:47:39"),
                        new OraDecimal("0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        date("2023-01-31 15:47:39"),
                        date("2023-01-31 01:14:22"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        date("2024-02-29 15:50:39"),
                        date("2024-02-23 11:02:39"),
                        new OraDecimal("0.2")
                ),
                Arguments.of(
                        "含0小数",
                        date("2024-02-29 15:11:53"),
                        date("2024-02-28 14:50:39"),
                        new OraDecimal("0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "含0小数，1秒是零点几月",
                        date("2024-02-29 00:00:00"),
                        date("2024-02-28 23:59:59"),
                        new OraDecimal("0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        date("2033-10-28 01:14:11"),
                        date("2013-01-31 23:24:39"),
                        new OraDecimal("248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        date("2033-10-01 00:00:00"),
                        date("2013-01-31 23:59:59"),
                        new OraDecimal("248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("monthsBetween 方法正数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenPlusProvider")
    public void testMonthsBetweenPlus(String caseId, Date endDate, Date beginDate, OraDecimal expected) {
        OraDecimal actual = OracleDateUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> testMonthsBetweenNegProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        date("2023-01-15 15:47:39"),
                        date("2023-03-15 01:14:22"),
                        new OraDecimal("-2")
                ),
                Arguments.of(
                        "均为月末",
                        date("2023-01-31 15:47:39"),
                        date("2023-02-28 01:14:22"),
                        new OraDecimal("-1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        date("2024-01-31 15:47:39"),
                        date("2024-02-28 01:14:22"),
                        new OraDecimal("-0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        date("2023-01-31 01:14:22"),
                        date("2023-01-31 15:47:39"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        date("2024-02-23 11:02:39"),
                        date("2024-02-29 15:50:39"),
                        new OraDecimal("-0.2")
                ),
                Arguments.of(
                        "含0小数",
                        date("2024-02-28 14:50:39"),
                        date("2024-02-29 15:11:53"),
                        new OraDecimal("-0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "含0小数，1秒是零点几月",
                        date("2024-02-28 23:59:59"),
                        date("2024-02-29 00:00:00"),
                        new OraDecimal("-0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        date("2013-01-31 23:24:39"),
                        date("2033-10-28 01:14:11"),
                        new OraDecimal("-248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        date("2013-01-31 23:59:59"),
                        date("2033-10-01 00:00:00"),
                        new OraDecimal("-248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("monthsBetween 方法负数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenNegProvider")
    public void testMonthsBetweenNeg(String caseId, Date endDate, Date beginDate, OraDecimal expected) {
        OraDecimal actual = OracleDateUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * 异常场景：仅测试 null 参数校验。
     * 带时区参数的重载在 OracleDateUtils 中不存在（Date 自动适配系统时区），故不测试。
     */
    @Test
    @DisplayName("monthsBetween 方法异常场景测试")
    public void testMonthsBetweenExceptions() {
        Date now = new Date();
        assertThrows(IllegalArgumentException.class, () -> OracleDateUtils.monthsBetween(now, null));
        assertThrows(IllegalArgumentException.class, () -> OracleDateUtils.monthsBetween(null, now));
    }
}
