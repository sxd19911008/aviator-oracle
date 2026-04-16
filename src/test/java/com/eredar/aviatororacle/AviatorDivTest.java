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
 * AviatorOracle 除法运算单元测试，对应 {@code OperatorType.DIV}（即 {@code /} 号）。
 */
@DisplayName("AviatorOracle 除法测试")
public class AviatorDivTest {

    private final AviatorEvaluatorInstance aviator = AviatorOracleBuilder.builder().build();

    // ========================= Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) / b(Long) → Long（整数除法，截断余数）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 2L).build(),
                        5L
                ),
                // a(Long) / b(Integer) → Long（Aviator 内部将 Integer 提升为 Long）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 2).build(),
                        5L
                ),
                // a(Long) / b(BigInteger) → BigInteger（BigInteger 优先级高于 Long）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigInteger("2")).build(),
                        new BigInteger("5")
                ),
                // a(Long) / b(Double) → OraDecimal（Double 优先级高于 Long）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("20.67796610169491612341281241022698281464")
                ),
                // a(Long) / b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Long) / b(OraDecimal) → OraDecimal（最高优先级）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Long) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", true).build(),
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
                // a(Integer) / b(Long) → Long
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 2L).build(),
                        5L
                ),
                // a(Integer) / b(Integer) → Long（Aviator 将两个 Integer 统一存储为 Long）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 2).build(),
                        5L
                ),
                // a(Integer) / b(BigInteger) → BigInteger
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigInteger("2")).build(),
                        new BigInteger("5")
                ),
                // a(Integer) / b(Double) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("20.67796610169491612341281241022698281464")
                ),
                // a(Integer) / b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Integer) / b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Integer) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", true).build(),
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
                // a(BigInteger) / b(Long) → BigInteger
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 2L).build(),
                        new BigInteger("5")
                ),
                // a(BigInteger) / b(Integer) → BigInteger
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 2).build(),
                        new BigInteger("5")
                ),
                // a(BigInteger) / b(BigInteger) → BigInteger
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigInteger("2")).build(),
                        new BigInteger("5")
                ),
                // a(BigInteger) / b(Double) → OraDecimal（Double 优先级高于 BigInteger）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("20.67796610169491612341281241022698281464")
                ),
                // a(BigInteger) / b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(BigInteger) / b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(BigInteger) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", true).build(),
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
        return Stream.of(
                // a(Double) / b(Long) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", 4L).build(),
                        new OraDecimal("2.5")
                ),
                // a(Double) / b(Integer) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", 4).build(),
                        new OraDecimal("2.5")
                ),
                // a(Double) / b(BigInteger) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", new BigInteger("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Double) / b(Double) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("20.67796610169491612341281241022698281464")
                ),
                // a(Double) / b(BigDecimal) → OraDecimal（BigDecimal 优先级高于 Double）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Double) / b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(Double) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", true).build(),
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
                // a(BigDecimal) / b(Long) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 2L).build(),
                        new OraDecimal("5")
                ),
                // a(BigDecimal) / b(Integer) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 2).build(),
                        new OraDecimal("5")
                ),
                // a(BigDecimal) / b(BigInteger) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new BigInteger("2")).build(),
                        new OraDecimal("5")
                ),
                // a(BigDecimal) / b(Double) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("20.67796610169491612341281241022698281464")
                ),
                // a(BigDecimal) / b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(BigDecimal) / b(OraDecimal) → OraDecimal（OraDecimal 优先级最高）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(BigDecimal) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", true).build(),
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
                // a(OraDecimal) / b(Long) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 2L).build(),
                        new OraDecimal("5")
                ),
                // a(OraDecimal) / b(Integer) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 2).build(),
                        new OraDecimal("5")
                ),
                // a(OraDecimal) / b(BigInteger) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new BigInteger("2")).build(),
                        new OraDecimal("5")
                ),
                // a(OraDecimal) / b(Double) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("20.67796610169491612341281241022698281464")
                ),
                // a(OraDecimal) / b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new BigDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(OraDecimal) / b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new OraDecimal("4")).build(),
                        new OraDecimal("2.5")
                ),
                // a(OraDecimal) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", true).build(),
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
                // a(String) / b(Long) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", 2L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(Integer) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", 2).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(BigInteger) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", new BigInteger("2")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(Double) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", 0.4836065573770491803278688524590163934426).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", new BigDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", new OraDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", true).build(),
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
        Instant aInstant = Instant.parse("2024-01-01T00:00:10Z");
        Instant bInstant = Instant.parse("2024-01-01T00:00:02Z");
        return Stream.of(
                // a(Instant) / b(Long) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 2L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(Integer) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 2).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(BigInteger) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new BigInteger("2")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(Double) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 0.4836065573770491803278688524590163934426).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new BigDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new OraDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", bInstant).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) / b(Boolean) → 抛出异常（Boolean 不支持算术运算）
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", true).build(),
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
                // a(Boolean) / b(Long) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 2L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(Integer) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 2).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(BigInteger) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("2")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(Double) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 0.4836065573770491803278688524590163934426).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new OraDecimal("4")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(String) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(Instant) → 抛出异常
                Arguments.of(
                        "a / b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2024-01-01T00:00:02Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) / b(Boolean) → 抛出异常
                Arguments.of(
                        "a / b",
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
