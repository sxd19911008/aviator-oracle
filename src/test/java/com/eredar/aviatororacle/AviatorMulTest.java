package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.googlecode.aviator.AviatorEvaluatorInstance;
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
 * AviatorOracle 乘法运算单元测试，对应 {@code OperatorType.MULT}（即 {@code *} 号）。
 */
@DisplayName("AviatorOracle 乘法测试")
public class AviatorMulTest {

    private final AviatorEvaluatorInstance aviator = AviatorOracleBuilder.builder().build();

    // ========================= Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) * b(Long) → Long
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", 4L).build(),
                        12L
                ),
                // a(Long) * b(Integer) → Long（Aviator 内部将 Integer 提升为 Long）
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", 4).build(),
                        12L
                ),
                // a(Long) * b(BigInteger) → BigInteger（BigInteger 优先级高于 Long）
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new BigInteger("4")).build(),
                        new BigInteger("12")
                ),
                // a(Long) * b(Double) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", 0.5).build(),
                        new OraDecimal("1.5")
                ),
                // a(Long) * b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new BigDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(Long) * b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new OraDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(Long) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Long")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testLongProvider")
    public void testLong(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= Integer =========================

    static Stream<Arguments> testIntegerProvider() {
        return Stream.of(
                // a(Integer) * b(Long) → Long
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", 4L).build(),
                        12L
                ),
                // a(Integer) * b(Integer) → Long
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", 4).build(),
                        12L
                ),
                // a(Integer) * b(BigInteger) → BigInteger
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", new BigInteger("4")).build(),
                        new BigInteger("12")
                ),
                // a(Integer) * b(Double) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", 0.5).build(),
                        new OraDecimal("1.5")
                ),
                // a(Integer) * b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", new BigDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(Integer) * b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", new OraDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(Integer) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Integer")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testIntegerProvider")
    public void testInteger(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= BigInteger =========================

    static Stream<Arguments> testBigIntegerProvider() {
        return Stream.of(
                // a(BigInteger) * b(Long) → BigInteger
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 4L).build(),
                        new BigInteger("12")
                ),
                // a(BigInteger) * b(Integer) → BigInteger
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 4).build(),
                        new BigInteger("12")
                ),
                // a(BigInteger) * b(BigInteger) → BigInteger
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new BigInteger("4")).build(),
                        new BigInteger("12")
                ),
                // a(BigInteger) * b(Double) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 0.5).build(),
                        new OraDecimal("1.5")
                ),
                // a(BigInteger) * b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new BigDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(BigInteger) * b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new OraDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(BigInteger) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("BigInteger")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBigIntegerProvider")
    public void testBigInteger(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= Double =========================

    static Stream<Arguments> testDoubleProvider() {
        final double d0 = 0.5;
        return Stream.of(
                // a(Double) * b(Long) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", 4L).build(),
                        new OraDecimal("2")
                ),
                // a(Double) * b(Integer) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", 4).build(),
                        new OraDecimal("2")
                ),
                // a(Double) * b(BigInteger) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", new BigInteger("4")).build(),
                        new OraDecimal("2")
                ),
                // a(Double) * b(Double) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", 0.5d).build(),
                        new OraDecimal("0.25")
                ),
                // a(Double) * b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2")
                ),
                // a(Double) * b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2")
                ),
                // a(Double) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) * b(Instant) → 抛出异常（乘法不支持 Instant）
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Double")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testDoubleProvider")
    public void testDouble(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= BigDecimal =========================

    static Stream<Arguments> testBigDecimalProvider() {
        return Stream.of(
                // a(BigDecimal) * b(Long) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", 4L).build(),
                        new OraDecimal("12")
                ),
                // a(BigDecimal) * b(Integer) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", 4).build(),
                        new OraDecimal("12")
                ),
                // a(BigDecimal) * b(BigInteger) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", new BigInteger("4")).build(),
                        new OraDecimal("12")
                ),
                // a(BigDecimal) * b(Double) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", 0.5).build(),
                        new OraDecimal("1.5")
                ),
                // a(BigDecimal) * b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", new BigDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(BigDecimal) * b(OraDecimal) → OraDecimal）
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", new OraDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(BigDecimal) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("BigDecimal")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBigDecimalProvider")
    public void testBigDecimal(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= OraDecimal =========================

    static Stream<Arguments> testOraDecimalProvider() {
        return Stream.of(
                // a(OraDecimal) * b(Long) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", 4L).build(),
                        new OraDecimal("12")
                ),
                // a(OraDecimal) * b(Integer) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", 4).build(),
                        new OraDecimal("12")
                ),
                // a(OraDecimal) * b(BigInteger) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", new BigInteger("4")).build(),
                        new OraDecimal("12")
                ),
                // a(OraDecimal) * b(Double) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", 0.5).build(),
                        new OraDecimal("1.5")
                ),
                // a(OraDecimal) * b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", new BigDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(OraDecimal) * b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", new OraDecimal("0.5")).build(),
                        new OraDecimal("1.5")
                ),
                // a(OraDecimal) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("OraDecimal")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testOraDecimalProvider")
    public void testOraDecimal(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= String =========================

    static Stream<Arguments> testStringProvider() {
        return Stream.of(
                // 左操作数为 String 时，乘法走 Aviator 默认实现，不支持与任意类型相乘
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", 4L).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", 4).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", new BigInteger("4")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", 0.5).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", new BigDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", new OraDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", Instant.parse("2024-01-01T00:00:10Z")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", "3").put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("String")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testStringProvider")
    public void testString(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= Instant =========================

    static Stream<Arguments> testInstantProvider() {
        return Stream.of(
                // a(Instant) * b(Long) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", 4L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(Integer) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", 4).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(BigInteger) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", new BigInteger("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(Double) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", 0.5).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", new BigDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", new OraDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(String) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Instant")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testInstantProvider")
    public void testInstant(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }

    // ========================= Boolean =========================

    static Stream<Arguments> testBooleanProvider() {
        return Stream.of(
                // a(Boolean) * b(Long) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 4L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(Integer) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 4).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(BigInteger) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(Double) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 0.5).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("4.091987349502")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new OraDecimal("4.483925798")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(String) → 抛出异常（乘法无字符串拼接语义）
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(Instant) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) * b(Boolean) → 抛出异常
                Arguments.of(
                        "a * b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", false).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("Boolean")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBooleanProvider")
    public void testBoolean(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }
}
