package com.eredar.aviatororacle;

import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

/**
 * AviatorOracle 无符号右移运算单元测试，对应 {@code OperatorType.U_SHIFT_RIGHT}（即 {@code >>>} 号）。
 */
@DisplayName("AviatorOracle 无符号右移测试")
public class AviatorUnsignedShiftRightTest {

    // ========================= Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) >>> b(Long) → Long
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", 3L).build(),
                        2L
                ),
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", -16L).put("b", 3L).build(),
                        2305843009213693950L
                ),
                // a(Long) >>> b(Integer) → Long
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", 3).build(),
                        2L
                ),
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", -16L).put("b", 3).build(),
                        2305843009213693950L
                ),
                // a(Long) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16L).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Long")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testLongProvider")
    public void testLong(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= Integer =========================

    static Stream<Arguments> testIntegerProvider() {
        return Stream.of(
                // a(Integer) >>> b(Long) → Long
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", 3L).build(),
                        2L
                ),
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", -16).put("b", 3L).build(),
                        2305843009213693950L
                ),
                // a(Integer) >>> b(Integer) → Long
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", 3).build(),
                        2L
                ),
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", -16).put("b", 3).build(),
                        2305843009213693950L
                ),
                // a(Integer) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Integer")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testIntegerProvider")
    public void testInteger(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= BigInteger =========================

    static Stream<Arguments> testBigIntegerProvider() {
        return Stream.of(
                // a(BigInteger) >>> b(Long) → BigInteger
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(Integer) → BigInteger
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("16")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("BigInteger")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBigIntegerProvider")
    public void testBigInteger(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= Double =========================

    static Stream<Arguments> testDoubleProvider() {
        return Stream.of(
                // a(Double) >>> b(Long) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(Integer) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", 16.0).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Double")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testDoubleProvider")
    public void testDouble(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= BigDecimal =========================

    static Stream<Arguments> testBigDecimalProvider() {
        return Stream.of(
                // a(BigDecimal) >>> b(Long) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(Integer) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("16")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("BigDecimal")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBigDecimalProvider")
    public void testBigDecimal(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= OraDecimal =========================

    static Stream<Arguments> testOraDecimalProvider() {
        return Stream.of(
                // a(OraDecimal) >>> b(Long) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(Integer) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("16")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("OraDecimal")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testOraDecimalProvider")
    public void testOraDecimal(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= String =========================

    static Stream<Arguments> testStringProvider() {
        return Stream.of(
                // a(String) >>> b(Long) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(Integer) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", "16").put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("String")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testStringProvider")
    public void testString(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= Instant =========================

    static Stream<Arguments> testInstantProvider() {
        Instant aInstant = Instant.parse("2024-01-01T00:00:00Z");
        return Stream.of(
                // a(Instant) >>> b(Long) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(Integer) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", Instant.parse("2024-01-01T00:00:03Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Instant")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testInstantProvider")
    public void testInstant(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= Boolean =========================

    static Stream<Arguments> testBooleanProvider() {
        return Stream.of(
                // a(Boolean) >>> b(Long) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(Integer) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(BigInteger) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(Double) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new com.eredar.aviatororacle.number.OraDecimal("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(String) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(Instant) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2024-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) >>> b(Boolean) → 抛出异常
                Arguments.of(
                        "a >>> b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", false).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Boolean")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBooleanProvider")
    public void testBoolean(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }
}
