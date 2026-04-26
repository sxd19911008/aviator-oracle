package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
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
 * AviatorOracle 指数运算单元测试，对应 {@code OperatorType.Exponent}（即 {@code **} 号）。
 */
@DisplayName("AviatorOracle 指数运算测试")
public class AviatorExponentTest {

    // ========================= Long =========================

    /**
     * 第一个操作数为 {@code Long}（3L），第二个操作数循环全部类型。
     * <p>小数类型的第二操作数均使用带小数的值，其 intValue() = 4，结果 3^4 = 81。
     */
    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) ** b(Long) → 3^4=81 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", 4L).build(),
                        new OraDecimal("81")
                ),
                // a(Long) ** b(Integer) → 3^4=81 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", 4).build(),
                        new OraDecimal("81")
                ),
                // a(Long) ** b(BigInteger) → 3^4=81 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new BigInteger("4")).build(),
                        new OraDecimal("81")
                ),
                // a(Long) ** b(Double=4.7) → intValue=4 → 3^4=81 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", 4.7).build(),
                        new OraDecimal("81")
                ),
                // a(Long) ** b(BigDecimal="4.9") → intValue=4 → 3^4=81 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new BigDecimal("4.9")).build(),
                        new OraDecimal("81")
                ),
                // a(Long) ** b(OraDecimal="4.2") → intValue=4 → 3^4=81 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new OraDecimal("4.2")).build(),
                        new OraDecimal("81")
                ),
                // a(Long) ** b(String) → 抛出异常（String 不是 Number）
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) ** b(Instant) → 抛出异常（Instant 不是 Number）
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) ** b(Boolean) → 抛出异常（Boolean 不是 Number）
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code Integer}（2），第二个操作数循环全部类型。
     * <p>小数类型的第二操作数均使用带小数的值，其 intValue() = 5，结果 2^5 = 32。
     */
    static Stream<Arguments> testIntegerProvider() {
        return Stream.of(
                // a(Integer) ** b(Long) → 2^5=32 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 5L).build(),
                        new OraDecimal("32")
                ),
                // a(Integer) ** b(Integer) → 2^5=32 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 5).build(),
                        new OraDecimal("32")
                ),
                // a(Integer) ** b(BigInteger) → 2^5=32 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new BigInteger("5")).build(),
                        new OraDecimal("32")
                ),
                // a(Integer) ** b(Double=5.8) → intValue=5 → 2^5=32 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 5.8).build(),
                        new OraDecimal("32")
                ),
                // a(Integer) ** b(BigDecimal="5.6") → intValue=5 → 2^5=32 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new BigDecimal("5.6")).build(),
                        new OraDecimal("32")
                ),
                // a(Integer) ** b(OraDecimal="5.3") → intValue=5 → 2^5=32 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new OraDecimal("5.3")).build(),
                        new OraDecimal("32")
                ),
                // a(Integer) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", "5").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
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

    /**
     * 第一个操作数为 {@code BigInteger}（3），第二个操作数循环全部类型。
     * <p>n1 为 BigInteger 时，结果类型为 {@link BigInteger}（而非 OraDecimal）。
     * <p>小数类型的第二操作数均使用带小数的值，其 intValue() = 4，结果 3^4 = 81。
     */
    static Stream<Arguments> testBigIntegerProvider() {
        return Stream.of(
                // a(BigInteger) ** b(Long) → 3^4=81 → BigInteger
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 4L).build(),
                        new BigInteger("81")
                ),
                // a(BigInteger) ** b(Integer) → 3^4=81 → BigInteger
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 4).build(),
                        new BigInteger("81")
                ),
                // a(BigInteger) ** b(BigInteger) → 3^4=81 → BigInteger
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new BigInteger("4")).build(),
                        new BigInteger("81")
                ),
                // a(BigInteger) ** b(Double=4.6) → intValue=4 → 3^4=81 → BigInteger
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 4.6).build(),
                        new BigInteger("81")
                ),
                // a(BigInteger) ** b(BigDecimal="4.3") → intValue=4 → 3^4=81 → BigInteger
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new BigDecimal("4.3")).build(),
                        new BigInteger("81")
                ),
                // a(BigInteger) ** b(OraDecimal="4.8") → intValue=4 → 3^4=81 → BigInteger
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new OraDecimal("4.8")).build(),
                        new BigInteger("81")
                ),
                // a(BigInteger) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", "4").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code Double}（2.5），第二个操作数循环全部类型。
     * <p>2.5 通过 {@code String.valueOf} 路径转为 OraDecimal("2.5")，结果精确。
     * <p>小数类型的第二操作数均使用带小数的值，其 intValue() = 2，结果 2.5^2 = 6.25。
     */
    static Stream<Arguments> testDoubleProvider() {
        return Stream.of(
                // a(Double=2.5) ** b(Long=2) → 2.5^2=6.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", 2L).build(),
                        new OraDecimal("6.25")
                ),
                // a(Double=2.5) ** b(Integer=2) → 2.5^2=6.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", 2).build(),
                        new OraDecimal("6.25")
                ),
                // a(Double=2.5) ** b(BigInteger=2) → 2.5^2=6.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", new BigInteger("2")).build(),
                        new OraDecimal("6.25")
                ),
                // a(Double=2.5) ** b(Double=2.9) → intValue=2 → 2.5^2=6.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", 2.9).build(),
                        new OraDecimal("6.25")
                ),
                // a(Double=2.5) ** b(BigDecimal="2.7") → intValue=2 → 2.5^2=6.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", new BigDecimal("2.7")).build(),
                        new OraDecimal("6.25")
                ),
                // a(Double=2.5) ** b(OraDecimal="2.4") → intValue=2 → 2.5^2=6.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", new OraDecimal("2.4")).build(),
                        new OraDecimal("6.25")
                ),
                // a(Double) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5).put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code BigDecimal}（3.5），第二个操作数循环全部类型。
     * <p>小数类型的第二操作数均使用带小数的值，其 intValue() = 2，结果 3.5^2 = 12.25。
     */
    static Stream<Arguments> testBigDecimalProvider() {
        return Stream.of(
                // a(BigDecimal=3.5) ** b(Long=2) → 3.5^2=12.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", 2L).build(),
                        new OraDecimal("12.25")
                ),
                // a(BigDecimal=3.5) ** b(Integer=2) → 3.5^2=12.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", 2).build(),
                        new OraDecimal("12.25")
                ),
                // a(BigDecimal=3.5) ** b(BigInteger=2) → 3.5^2=12.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", new BigInteger("2")).build(),
                        new OraDecimal("12.25")
                ),
                // a(BigDecimal=3.5) ** b(Double=2.9) → intValue=2 → 3.5^2=12.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", 2.9).build(),
                        new OraDecimal("12.25")
                ),
                // a(BigDecimal=3.5) ** b(BigDecimal="2.7") → intValue=2 → 3.5^2=12.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", new BigDecimal("2.7")).build(),
                        new OraDecimal("12.25")
                ),
                // a(BigDecimal=3.5) ** b(OraDecimal="2.4") → intValue=2 → 3.5^2=12.25 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", new OraDecimal("2.4")).build(),
                        new OraDecimal("12.25")
                ),
                // a(BigDecimal) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.5")).put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code OraDecimal}（2.5），第二个操作数循环全部类型。
     * <p>小数类型的第二操作数均使用带小数的值，其 intValue() = 3，结果 2.5^3 = 15.625。
     */
    static Stream<Arguments> testOraDecimalProvider() {
        return Stream.of(
                // a(OraDecimal=2.5) ** b(Long=3) → 2.5^3=15.625 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", 3L).build(),
                        new OraDecimal("15.625")
                ),
                // a(OraDecimal=2.5) ** b(Integer=3) → 2.5^3=15.625 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", 3).build(),
                        new OraDecimal("15.625")
                ),
                // a(OraDecimal=2.5) ** b(BigInteger=3) → 2.5^3=15.625 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", new BigInteger("3")).build(),
                        new OraDecimal("15.625")
                ),
                // a(OraDecimal=2.5) ** b(Double=3.8) → intValue=3 → 2.5^3=15.625 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", 3.8).build(),
                        new OraDecimal("15.625")
                ),
                // a(OraDecimal=2.5) ** b(BigDecimal="3.6") → intValue=3 → 2.5^3=15.625 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", new BigDecimal("3.6")).build(),
                        new OraDecimal("15.625")
                ),
                // a(OraDecimal=2.5) ** b(OraDecimal="3.2") → intValue=3 → 2.5^3=15.625 → OraDecimal
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", new OraDecimal("3.2")).build(),
                        new OraDecimal("15.625")
                ),
                // a(OraDecimal) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.5")).put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code String}，所有组合均抛出异常（String 不是 Number）。
     */
    static Stream<Arguments> testStringProvider() {
        return Stream.of(
                // a(String) ** b(Long) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(Integer) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(BigInteger) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(Double) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", 3.5).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", new BigDecimal("3.5")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", new OraDecimal("3.5")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(String) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", "abc").put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code Instant}，所有组合均抛出异常（Instant 不是 Number）。
     */
    static Stream<Arguments> testInstantProvider() {
        return Stream.of(
                // a(Instant) ** b(Long) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(Integer) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(BigInteger) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(Double) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", 3.5).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", new BigDecimal("3.5")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", new OraDecimal("3.5")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", Instant.parse("2020-06-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", true).build(),
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

    /**
     * 第一个操作数为 {@code Boolean}，所有组合均抛出异常（Boolean 不是 Number）。
     */
    static Stream<Arguments> testBooleanProvider() {
        return Stream.of(
                // a(Boolean) ** b(Long) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(Integer) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(BigInteger) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("3")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(Double) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3.5).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("3.5")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new OraDecimal("3.5")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(String) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "3").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(Instant) → 抛出异常
                Arguments.of(
                        "a ** b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) ** b(Boolean) → 抛出异常
                Arguments.of(
                        "a ** b",
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

    // ========================= 复杂公式 =========================

    /**
     * 连续三次指数运算：{@code ((a ** b) ** c) ** d}。
     *
     * <p>计算过程（全为 Long 类型，结果为 OraDecimal）：
     * <pre>
     *   a=2, b=2 → step1 = 2^2 = 4
     *   step1=4, c=3 → step2 = 4^3 = 64
     *   step2=64, d=2 → result = 64^2 = 4096
     * </pre>
     */
    static Stream<Arguments> testComplexExponentProvider() {
        return Stream.of(
                // ((2L ** 2L) ** 3L) ** 2L = ((4)^3)^2 = 64^2 = 4096
                Arguments.of(
                        "(( a ** b ) ** c ) ** d",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 2L).put("b", 2L).put("c", 3L).put("d", 2L)
                                .build(),
                        new OraDecimal("4096")
                )
        );
    }

    @DisplayName("复杂公式（三次指数运算）")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testComplexExponentProvider")
    public void testComplexExponent(String expression, Map<String, Object> vars, Object expected) {
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
