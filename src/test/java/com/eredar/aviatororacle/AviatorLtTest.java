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
 * AviatorOracle 小于运算单元测试，对应 {@code OperatorType.LT}（即 {@code <} 号）。
 */
@DisplayName("AviatorOracle 小于测试")
public class AviatorLtTest {

    private final AviatorEvaluatorInstance aviator = AviatorOracleBuilder.builder().build();

    // ========================= Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) < b(Long) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", 10L).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 12L).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 10L).build(),
                        false
                ),
                // a(Long) < b(Integer) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", 10).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 12).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 10).build(),
                        false
                ),
                // a(Long) < b(BigInteger) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", new BigInteger("10")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigInteger("12")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigInteger("10")).build(),
                        false
                ),
                // a(Long) < b(Double) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", 10.0609458784987394).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 10.0432579867245).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 10.0).build(),
                        false
                ),
                // a(Long) < b(BigDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", new BigDecimal("10.054769375987983")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigDecimal("10.000000000000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigDecimal("10.0")).build(),
                        false
                ),
                // a(Long) < b(OraDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", new OraDecimal("11.9654798672894365892")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new OraDecimal("10.000000000000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new OraDecimal("10.0")).build(),
                        false
                ),
                // a(Long) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", true).build(),
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
                // a(Integer) < b(Long) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", 10L).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 12L).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 10L).build(),
                        false
                ),
                // a(Integer) < b(Integer) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", 10).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 12).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 10).build(),
                        false
                ),
                // a(Integer) < b(BigInteger) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", new BigInteger("10")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigInteger("12")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigInteger("10")).build(),
                        false
                ),
                // a(Integer) < b(Double) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", 11.99999999).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 10.000000001).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 10.0).build(),
                        false
                ),
                // a(Integer) < b(BigDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", new BigDecimal("11.999999999999999")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigDecimal("10.00000000000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigDecimal("10.0")).build(),
                        false
                ),
                // a(Integer) < b(OraDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", new OraDecimal("11.999999999999999")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new OraDecimal("10.0000000000000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new OraDecimal("10.0")).build(),
                        false
                ),
                // a(Integer) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", true).build(),
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
                // a(BigInteger) < b(Long) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", 10L).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 12L).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 10L).build(),
                        false
                ),
                // a(BigInteger) < b(Integer) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", 10).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 12).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 10).build(),
                        false
                ),
                // a(BigInteger) < b(BigInteger) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", new BigInteger("10")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigInteger("12")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigInteger("10")).build(),
                        false
                ),
                // a(BigInteger) < b(Double) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", 11.9999999999).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 10.0000000001).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 10.0).build(),
                        false
                ),
                // a(BigInteger) < b(BigDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", new BigDecimal("11.9999999999999")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigDecimal("10.00000000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigDecimal("10.0")).build(),
                        false
                ),
                // a(BigInteger) < b(OraDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", new OraDecimal("11.9999999999999")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new OraDecimal("10.00000000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new OraDecimal("10.0")).build(),
                        false
                ),
                // a(BigInteger) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", true).build(),
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
                // a(Double) < b(Long) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12.0).put("b", 10L).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0).put("b", 12L).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0).put("b", 10L).build(),
                        false
                ),
                // a(Double) < b(Integer) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12.0).put("b", 10).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0).put("b", 12).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0).put("b", 10).build(),
                        false
                ),
                // a(Double) < b(BigInteger) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12.0).put("b", new BigInteger("10")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0).put("b", new BigInteger("12")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0).put("b", new BigInteger("10")).build(),
                        false
                ),
                // a(Double) < b(Double) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.00000001).put("b", 10.000000009).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.000000009).put("b", 10.00000001).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.000000009).put("b", 10.000000009).build(),
                        false
                ),
                // a(Double) < b(BigDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.00000001).put("b", new BigDecimal("10.000000009")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.000000009).put("b", new BigDecimal("10.00000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.000000009).put("b", new BigDecimal("10.000000009")).build(),
                        false
                ),
                // a(Double) < b(OraDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.00000001).put("b", new OraDecimal("10.000000009")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.000000009).put("b", new OraDecimal("10.00000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.000000009).put("b", new OraDecimal("10.000000009")).build(),
                        false
                ),
                // a(Double) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12.0).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12.0).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", 12.0).put("b", true).build(),
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
                // a(BigDecimal) < b(Long) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", 10L).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 12L).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 10L).build(),
                        false
                ),
                // a(BigDecimal) < b(Integer) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", 10).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 12).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 10).build(),
                        false
                ),
                // a(BigDecimal) < b(BigInteger) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", new BigInteger("10")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new BigInteger("12")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new BigInteger("10")).build(),
                        false
                ),
                // a(BigDecimal) < b(Double) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.00000001")).put("b", 10.000000009).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.000000009")).put("b", 10.00000001).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.000000009")).put("b", 10.000000009).build(),
                        false
                ),
                // a(BigDecimal) < b(BigDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.00000001")).put("b", new BigDecimal("10.000000009")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.000000009")).put("b", new BigDecimal("10.00000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.000000009")).put("b", new BigDecimal("10.000000009")).build(),
                        false
                ),
                // a(BigDecimal) < b(OraDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.00000001")).put("b", new OraDecimal("10.000000009")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.000000009")).put("b", new OraDecimal("10.00000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10.000000009")).put("b", new OraDecimal("10.000000009")).build(),
                        false
                ),
                // a(BigDecimal) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", true).build(),
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
                // a(OraDecimal) < b(Long) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", 10L).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 12L).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 10L).build(),
                        false
                ),
                // a(OraDecimal) < b(Integer) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", 10).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 12).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 10).build(),
                        false
                ),
                // a(OraDecimal) < b(BigInteger) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", new BigInteger("10")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new BigInteger("12")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new BigInteger("10")).build(),
                        false
                ),
                // a(OraDecimal) < b(Double) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.00000001")).put("b", 10.000000009).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.000000009")).put("b", 10.00000001).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.000000009")).put("b", 10.000000009).build(),
                        false
                ),
                // a(OraDecimal) < b(BigDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.00000001")).put("b", new BigDecimal("10.000000009")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.000000009")).put("b", new BigDecimal("10.00000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.45324523467245")).put("b", new BigDecimal("10.45324523467245")).build(),
                        false
                ),
                // a(OraDecimal) < b(OraDecimal) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("0.00000001")).put("b", new OraDecimal("0.000000009")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("0.000000009")).put("b", new OraDecimal("0.00000001")).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10.45324523467245")).put("b", new OraDecimal("10.45324523467245")).build(),
                        false
                ),
                // a(OraDecimal) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", true).build(),
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
                // 左操作数为 String 时，小于运算不支持与数值直接比较
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", 10L).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", 10).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", new BigInteger("10")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", 10.0).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", new BigDecimal("10")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", new OraDecimal("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) < b(String) → Boolean (Lexicographical comparison)
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", "10").build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", "12").build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "10").put("b", "10").build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "apple").put("b", "banana").build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "banana").put("b", "apple").build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "banana").put("b", "banana").build(),
                        false
                ),
                // a(String) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", Instant.parse("2024-01-01T00:00:10Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", true).build(),
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
        Instant instant1 = Instant.parse("2020-03-06T03:36:19Z");
        Instant instant2 = Instant.parse("2020-02-25T03:36:19Z");
        return Stream.of(
                // a(Instant) < b(Long) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", 10L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(Integer) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", 10).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(BigInteger) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", new BigInteger("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(Double) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", 10.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", new BigDecimal("10.0")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", new OraDecimal("10.0")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) < b(Instant) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", instant2).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant2).put("b", instant1).build(),
                        true
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", Instant.parse("2020-03-06T03:36:19Z")).build(),
                        false
                ),
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", instant1).build(),
                        false
                ),
                // a(Instant) < b(Boolean) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", instant1).put("b", true).build(),
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
                // a(Boolean) < b(Long) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(Integer) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(BigInteger) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(Double) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10.0).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("10.0")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new OraDecimal("10.0")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(String) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(Instant) → 抛出异常
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) < b(Boolean) → Boolean
                Arguments.of(
                        "a < b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", false).build(),
                        false
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
