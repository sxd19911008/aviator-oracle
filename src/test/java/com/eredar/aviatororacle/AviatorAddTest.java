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
 * AviatorOracle 加法运算单元测试，对应 {@code OperatorType.ADD}（即 {@code +} 号）。
 */
@DisplayName("AviatorOracle 加法测试")
public class AviatorAddTest {

    // ========================= Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) + b(Long) → Long
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 10L).build(),
                        12L
                ),
                // a(Long) + b(Integer) → Long（Aviator 内部将 Integer 提升为 Long）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 10).build(),
                        12L),
                // a(Long) + b(BigInteger) → BigInteger（BigInteger 优先级高于 Long）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", new BigInteger("10")).build(),
                        new BigInteger("12")
                ),
                // a(Long) + b(Double) → Double（Double 优先级高于 Long）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Long) + b(BigDecimal) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", new BigDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(Long) + b(OraDecimal) → OraDecimal（最高优先级）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(Long) + b(String) → String（字符串拼接）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", "10").build(),
                        "210"
                ),
                // a(Long) + b(Instant) → String（Instant.toString() + 数字字符串）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        Instant.parse("2020-02-03T03:36:19Z")
                ),
                // a(Long) + b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", true).build(),
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
                // a(Integer) + b(Long) → Long
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 10L).build(),
                        12L
                ),
                // a(Integer) + b(Integer) → Long（Aviator 将两个 Integer 统一存储为 Long）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 10).build(),
                        12L
                ),
                // a(Integer) + b(BigInteger) → BigInteger
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new BigInteger("10")).build(),
                        new BigInteger("12")
                ),
                // a(Integer) + b(Double) → Double
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Integer) + b(BigDecimal) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new BigDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(Integer) + b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(Integer) + b(String) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", "10").build(),
                        "210"
                ),
                // a(Integer) + b(Instant) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        Instant.parse("2020-02-03T03:36:19Z")
                ),
                // a(Integer) + b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", true).build(),
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
                // a(BigInteger) + b(Long) → BigInteger
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", 10L).build(),
                        new BigInteger("12")
                ),
                // a(BigInteger) + b(Integer) → BigInteger
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", 10).build(),
                        new BigInteger("12")
                ),
                // a(BigInteger) + b(BigInteger) → BigInteger
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", new BigInteger("10")).build(),
                        new BigInteger("12")
                ),
                // a(BigInteger) + b(Double) → Double（Double 优先级高于 BigInteger）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(BigInteger) + b(BigDecimal) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", new BigDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigInteger) + b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigInteger) + b(String) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", "10").build(),
                        "210"
                ),
                // a(BigInteger) + b(Instant) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        Instant.parse("2020-02-03T03:36:19Z")
                ),
                // a(BigInteger) + b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", true).build(),
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
                // a(Double) + b(Long) → Double
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", 2L).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Double) + b(Integer) → Double
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", 2).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Double) + b(BigInteger) → Double
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", new BigInteger("2")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Double) + b(Double) → Double
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("0.96721311475409832")
                ),
                // a(Double) + b(BigDecimal) → BigDecimal（BigDecimal 优先级高于 Double）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", new BigDecimal("2")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Double) + b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", new com.eredar.aviatororacle.number.OraDecimal("2")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(Double) + b(String) → String（Double.toString() = "2.0"）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", "2").build(),
                        "0.483606557377049162"
                ),
                // a(Double) + b(Instant) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 31.57349857284537940384752204323255406344).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        Instant.parse("2020-03-03T13:45:50Z")
                ),
                // a(Double) + b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.4836065573770491803278688524590163934426).put("b", true).build(),
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
                // a(BigDecimal) + b(Long) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", 10L).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigDecimal) + b(Integer) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", 10).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigDecimal) + b(BigInteger) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", new BigInteger("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigDecimal) + b(Double) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(BigDecimal) + b(BigDecimal) → BigDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", new BigDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigDecimal) + b(OraDecimal) → OraDecimal（OraDecimal 优先级最高）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(BigDecimal) + b(String) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", "10").build(),
                        "210"
                ),
                // a(BigDecimal) + b(Instant) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("31.57349857284537940384752204323255406344")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        Instant.parse("2020-03-03T13:45:50Z")
                ),
                // a(BigDecimal) + b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", true).build(),
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
                // a(OraDecimal) + b(Long) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", 10L).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(OraDecimal) + b(Integer) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", 10).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(OraDecimal) + b(BigInteger) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", new BigInteger("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(OraDecimal) + b(Double) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("2.48360655737704916")
                ),
                // a(OraDecimal) + b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", new BigDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(OraDecimal) + b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        new com.eredar.aviatororacle.number.OraDecimal("12")
                ),
                // a(OraDecimal) + b(String) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", "10").build(),
                        "210"
                ),
                // a(OraDecimal) + b(Instant) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("31.57349857284537940384752204323255406344")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        Instant.parse("2020-03-03T13:45:50Z")
                ),
                // a(OraDecimal) + b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", new com.eredar.aviatororacle.number.OraDecimal("2")).put("b", true).build(),
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
                // a(String) + b(Long) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", 10L).build(),
                        "210"
                ),
                // a(String) + b(Integer) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", 10).build(),
                        "210"
                ),
                // a(String) + b(BigInteger) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", new BigInteger("10")).build(),
                        "210"
                ),
                // a(String) + b(Double) → String（Double.toString() = "10.0"）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", 0.4836065573770491803278688524590163934426).build(),
                        "20.48360655737704916"
                ),
                // a(String) + b(BigDecimal) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", new BigDecimal("10")).build(),
                        "210"
                ),
                // a(String) + b(OraDecimal) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        "210"
                ),
                // a(String) + b(String) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", "10").build(),
                        "210"
                ),
                // a(String) + b(Instant) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", Instant.parse("2024-01-01T00:00:10Z")).build(),
                        "2" + Instant.parse("2024-01-01T00:00:10Z")
                ),
                // a(String) + b(Boolean) → String（字符串拼接，Boolean.toString() = "true"）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", "2").put("b", true).build(),
                        "2true"
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
        // a 统一为 2024-01-01T00:00:02Z，b(Instant) 为 2024-01-01T00:00:10Z
        Instant aInstant = Instant.parse("2020-02-25T03:36:19Z");
        return Stream.of(
                // a(Instant) + b(Long) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 10L).build(),
                        Instant.parse("2020-03-06T03:36:19Z")
                ),
                // a(Instant) + b(Integer) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 10).build(),
                        Instant.parse("2020-03-06T03:36:19Z")
                ),
                // a(Instant) + b(BigInteger) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new BigInteger("10")).build(),
                        Instant.parse("2020-03-06T03:36:19Z")
                ),
                // a(Instant) + b(Double) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", 31.57349857284537940384752204323255406344).build(),
                        Instant.parse("2020-03-27T17:22:09Z")
                ),
                // a(Instant) + b(BigDecimal) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new BigDecimal("31.57349857284537940384752204323255406344")).build(),
                        Instant.parse("2020-03-27T17:22:09Z")
                ),
                // a(Instant) + b(OraDecimal) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", new com.eredar.aviatororacle.number.OraDecimal("31.57349857284537940384752204323255406344")).build(),
                        Instant.parse("2020-03-27T17:22:09Z")
                ),
                // a(Instant) + b(String) → String
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) + b(Instant) → String（两个 Instant 的 toString 拼接）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", aInstant).put("b", Instant.parse("2024-01-01T07:39:18Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) + b(Boolean) → 抛出异常（Boolean 既无法与 Instant 做数值运算，也无法做日期偏移）
                Arguments.of(
                        "a + b",
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
                // a(Boolean) + b(Long) → 抛出异常
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(Integer) → 抛出异常
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(BigInteger) → 抛出异常
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(Double) → 抛出异常
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 0.4836065573770491803278688524590163934426).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new com.eredar.aviatororacle.number.OraDecimal("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(String) → String（Boolean.toString() = "true"，字符串拼接）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "10").build(),
                        "true10"
                ),
                // a(Boolean) + b(Instant) → 抛出异常（Boolean 无日期偏移语义）
                Arguments.of(
                        "a + b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) + b(Boolean) → 抛出异常（两个布尔值不能做加法）
                Arguments.of(
                        "a + b",
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
