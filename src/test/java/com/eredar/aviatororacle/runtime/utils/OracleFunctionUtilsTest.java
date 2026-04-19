package com.eredar.aviatororacle.runtime.utils;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.uitls.OracleFunctionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Oracle方法测试")
public class OracleFunctionUtilsTest {

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
        OraDecimal actual = OracleFunctionUtils.daysBetween(beginDate, endDate);
        Assertions.assertEquals(expected, actual);
    }

    // -------------------------------------------------------------------------
    // decode
    // -------------------------------------------------------------------------

    /**
     * decode 正常返回值场景：最后一列 {@code null} 表示期望 {@link OracleFunctionUtils#decode} 返回 null。
     */
    static Stream<Arguments> decodeMatchProvider() {
        return Stream.of(
                Arguments.of("匹配第一个 search", (Supplier<Object>) () -> OracleFunctionUtils.decode("1", "1", "A", "2", "B", "C"), "A"),
                Arguments.of("匹配第二个 search", (Supplier<Object>) () -> OracleFunctionUtils.decode("2", "1", "A", "2", "B", "C"), "B"),
                Arguments.of("无匹配返回默认值", (Supplier<Object>) () -> OracleFunctionUtils.decode("3", "1", "A", "2", "B", "C"), "C"),
                Arguments.of("无匹配且无默认值返回 null", (Supplier<Object>) () -> OracleFunctionUtils.decode("3", "1", "A", "2", "B"), null),
                Arguments.of("null 与 null 匹配", (Supplier<Object>) () -> OracleFunctionUtils.decode(null, null, "Result", "Other"), "Result"),
                Arguments.of("表达式为 null 且 search 非 null", (Supplier<Object>) () -> OracleFunctionUtils.decode(null, "1", "Result", "Default"), "Default"),
                Arguments.of("Integer 与 Long 数值相等", (Supplier<Object>) () -> OracleFunctionUtils.decode(100, 100L, "Match", "No Match"), "Match"),
                Arguments.of("Long 与 OraDecimal 数值相等", (Supplier<Object>) () -> OracleFunctionUtils.decode(200L, new OraDecimal("200.00"), "Match", "No Match"), "Match"),
                Arguments.of("OraDecimal 不同精度数值相等", (Supplier<Object>) () -> OracleFunctionUtils.decode(new OraDecimal("3.14"), new OraDecimal("3.1400"), "Match", "No Match"), "Match")
        );
    }

    @DisplayName("decode 方法匹配与返回值测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeMatchProvider")
    public void testDecodeMatch(String caseId, Supplier<Object> decodeCall, Object expected) {
        if (expected == null) {
            Assertions.assertNull(decodeCall.get());
        } else {
            Assertions.assertEquals(expected, decodeCall.get());
        }
    }

    static Stream<Arguments> decodeInvalidArgsProvider() {
        //noinspection Convert2MethodRef
        return Stream.of(
                Arguments.of("入参仅2个", (Executable) () -> OracleFunctionUtils.decode("1", "2")),
                Arguments.of("入参仅1个", (Executable) () -> OracleFunctionUtils.decode("1")),
                Arguments.of("入参0个", (Executable) () -> OracleFunctionUtils.decode())
        );
    }

    @DisplayName("decode 方法非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeInvalidArgsProvider")
    public void testDecodeInvalidArgs(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }


    // -------------------------------------------------------------------------
    // floor
    // -------------------------------------------------------------------------

    static Stream<Arguments> testFloorProvider() {
        return Stream.of(
                Arguments.of("Long", 1342534967873799582L, new OraDecimal("1342534967873799582")),
                Arguments.of("Integer", 143262, new OraDecimal("143262")),
                Arguments.of("BigInteger", new BigInteger("1342534967873799582"), new OraDecimal("1342534967873799582")),
                Arguments.of("Double", 1.993565624, new OraDecimal("1")),
                Arguments.of("BigDecimal", new BigDecimal("1.9999431565624544763765735"), new OraDecimal("1")),
                Arguments.of("OraDecimal", new OraDecimal("1.9999431565624544763765735"), new OraDecimal("1")),
                Arguments.of("String", "1.9999431565624544763765735", IllegalArgumentException.class),
                Arguments.of("Instant", Instant.parse("2020-02-01T03:36:19Z"), IllegalArgumentException.class),
                Arguments.of("Boolean", true, IllegalArgumentException.class)
        );
    }

    @DisplayName("floor 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testFloorProvider")
    public void testFloor(String caseId, Object n, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> OracleFunctionUtils.floor(n));
        } else {
            OraDecimal actual = OracleFunctionUtils.floor(n);
            Assertions.assertEquals(expected, actual);
        }
    }
}
