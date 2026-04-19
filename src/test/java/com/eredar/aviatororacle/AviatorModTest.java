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
 * AviatorOracle 取模运算单元测试，对应 {@code OperatorType.MOD}（即 {@code %} 号）。
 */
@DisplayName("AviatorOracle 取模测试")
public class AviatorModTest {

    // ========================= Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) % b(Long) → Long
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 3L).build(), 1L),
                // a(Long) % b(Integer) → Long
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 3).build(), 1L),
                // a(Long) % b(BigInteger) → BigInteger
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigInteger("3")).build(), new BigInteger("1")),
                // a(Long) % b(Double) → OraDecimal
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", 3.45789247523476456452677653758679837427d).build(),
                        new OraDecimal("3.0842150495304708")
                ),
                // a(Long) % b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new BigDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                // a(Long) % b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", new OraDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                // a(Long) % b(String) → 抛出异常
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", "3").build(), ExpressionRuntimeException.class),
                // a(Long) % b(Instant) → 抛出异常
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                // a(Long) % b(Boolean) → 抛出异常
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10L).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("Long")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testLongProvider")
    public void testLong(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= Integer =========================

    static Stream<Arguments> testIntegerProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 3L).build(), 1L),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 9).put("b", 3).build(), 0L),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigInteger("3")).build(), new BigInteger("1")),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", 3.45789247523476456452677653758679837427d).build(),
                        new OraDecimal("3.0842150495304708")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new BigDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10).put("b", new OraDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("Integer")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testIntegerProvider")
    public void testInteger(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= BigInteger =========================

    static Stream<Arguments> testBigIntegerProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 3L).build(), new BigInteger("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 3).build(), new BigInteger("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigInteger("3")).build(), new BigInteger("1")),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", 3.45789247523476456452677653758679837427d).build(),
                        new OraDecimal("3.0842150495304708")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new BigDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", new OraDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigInteger("10")).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("BigInteger")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBigIntegerProvider")
    public void testBigInteger(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= Double =========================

    static Stream<Arguments> testDoubleProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", 3L).build(), new OraDecimal("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10.5428574392d).put("b", 3).build(), new OraDecimal("1.5428574392")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", new BigInteger("3")).build(), new OraDecimal("1")),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", 3.45789247523476456452677653758679837427d).build(),
                        new OraDecimal("3.0842150495304708")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", new BigDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", new OraDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", 10.0d).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("Double")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testDoubleProvider")
    public void testDouble(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= BigDecimal =========================

    static Stream<Arguments> testBigDecimalProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 3L).build(), new OraDecimal("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 3).build(), new OraDecimal("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new BigInteger("3")).build(), new OraDecimal("1")),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", 3.45789247523476456452677653758679837427d).build(),
                        new OraDecimal("3.0842150495304708")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new BigDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", new OraDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("10")).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("BigDecimal")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBigDecimalProvider")
    public void testBigDecimal(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= OraDecimal =========================

    static Stream<Arguments> testOraDecimalProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 3L).build(), new OraDecimal("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 3).build(), new OraDecimal("1")),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new BigInteger("3")).build(), new OraDecimal("1")),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", 3.45789247523476456452677653758679837427d).build(),
                        new OraDecimal("3.0842150495304708")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new BigDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of(
                        "a % b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", new OraDecimal("3.45789247523476456452677653758679837427")).build(),
                        new OraDecimal("3.08421504953047087094644692482640325146")
                ),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("10")).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("OraDecimal")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testOraDecimalProvider")
    public void testOraDecimal(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= String =========================

    static Stream<Arguments> testStringProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", 3L).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", 3).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", new BigInteger("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", 3.0d).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", new BigDecimal("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", new OraDecimal("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", "10").put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("String")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testStringProvider")
    public void testString(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= Instant =========================

    static Stream<Arguments> testInstantProvider() {
        Instant now = Instant.now();
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", 3L).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", 3).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", new BigInteger("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", 3.0d).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", new BigDecimal("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", new OraDecimal("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", now).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", now).put("b", true).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("Instant")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testInstantProvider")
    public void testInstant(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    // ========================= Boolean =========================

    static Stream<Arguments> testBooleanProvider() {
        return Stream.of(
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3L).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", 3.0d).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", new OraDecimal("3")).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", "3").build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.now()).build(), ExpressionRuntimeException.class),
                Arguments.of("a % b", HashMapBuilder.<String, Object>builder().put("a", true).put("b", false).build(), ExpressionRuntimeException.class)
        );
    }

    @DisplayName("Boolean")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBooleanProvider")
    public void testBoolean(String expression, Map<String, Object> vars, Object expected) {
        executeAndAssert(expression, vars, expected);
    }

    private void executeAndAssert(String expression, Map<String, Object> vars, Object expected) {
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
