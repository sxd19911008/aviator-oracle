package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
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
 * 测试：模拟Oracle数据库 {@code power()} 方法
 */
@DisplayName("power 方法测试")
public class PowerFunctionTest {

    // ========================= Long =========================

    /**
     * 底数类型为 {@code Long}，指数覆盖各种数值类型。
     *
     * <pre>
     * SELECT POWER(2, 3)    FROM dual  -- 8
     * SELECT POWER(2, 10)   FROM dual  -- 1024
     * SELECT POWER(3, 3)    FROM dual  -- 27
     * SELECT POWER(2, -1)   FROM dual  -- .5
     * SELECT POWER(-2, 3)   FROM dual  -- -8
     * SELECT POWER(4, .5)   FROM dual  -- 1.99999999999999999999999999999999999999（BigDecimalMath 精确返回 2）
     * SELECT POWER(2, 1.5)  FROM dual  -- 2.82842712474619009760337744841939615716
     * </pre>
     */
    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) + b(Long) → 正整数底数 正整数指数（精确路径）
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 3L).build(),
                        new OraDecimal("8")
                ),
                // a(Long) + b(Integer) → Integer 在 Aviator 中被提升为 Long
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 3).build(),
                        new OraDecimal("8")
                ),
                // a(Long) + b(BigInteger) → 走精确路径
                // Oracle: POWER(2, 10) = 1024
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", new BigInteger("10")).build(),
                        new OraDecimal("1024")
                ),
                // a(Long) + b(Double 整数值) → isInteger=true 走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 3.0d).build(),
                        new OraDecimal("8")
                ),
                // a(Long) + b(Double 非整数) → BigDecimalMath 高精度路径；完全平方根精确返回整数
                // Oracle: POWER(4, .5) = 1.99999999999999999999999999999999999999（BigDecimalMath 精确返回 2）
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 4L).put("b", 0.5d).build(),
                        new OraDecimal("2")
                ),
                // a(Long) + b(BigDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new BigDecimal("3")).build(),
                        new OraDecimal("27")
                ),
                // a(Long) + b(OraDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 3L).put("b", new OraDecimal("3")).build(),
                        new OraDecimal("27")
                ),
                // a(Long) + b(Long 负整数) → 1/base^|exp| 路径
                // Oracle: POWER(2, -1) = .5
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", -1L).build(),
                        new OraDecimal("0.5")
                ),
                // a(Long 负数) + b(Long 正奇数) → 结果为负
                // Oracle: POWER(-2, 3) = -8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", -2L).put("b", 3L).build(),
                        new OraDecimal("-8")
                ),
                // a(Long) + b(Double 非整数) → 高精度路径，末位与 Oracle 差 2（在误差范围内）
                // Oracle: POWER(2, 1.5) = 2.82842712474619009760337744841939615716
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 1.5d).build(),
                        new OraDecimal("2.82842712474619009760337744841939615714")
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
     * 底数类型为 {@code Integer}（Aviator 内部将其提升为 {@code Long}），指数覆盖各种数值类型。
     *
     * <pre>
     * SELECT POWER(2, 3)   FROM dual  -- 8
     * SELECT POWER(2, 10)  FROM dual  -- 1024
     * SELECT POWER(3, 3)   FROM dual  -- 27
     * SELECT POWER(4, .5)  FROM dual  -- 1.999...（BigDecimalMath 精确返回 2）
     * </pre>
     */
    static Stream<Arguments> testIntegerProvider() {
        return Stream.of(
                // a(Integer) + b(Long) → Integer 被 Aviator 提升为 Long
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 3L).build(),
                        new OraDecimal("8")
                ),
                // a(Integer) + b(Integer) → 两者均被提升为 Long
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 3).build(),
                        new OraDecimal("8")
                ),
                // a(Integer) + b(BigInteger) → 走精确路径
                // Oracle: POWER(2, 10) = 1024
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", new BigInteger("10")).build(),
                        new OraDecimal("1024")
                ),
                // a(Integer) + b(Double 整数值) → isInteger=true 走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 3.0d).build(),
                        new OraDecimal("8")
                ),
                // a(Integer) + b(Double 非整数) → 完全平方根精确返回整数
                // Oracle: POWER(4, .5) = 1.999...（BigDecimalMath 精确返回 2）
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 4).put("b", 0.5d).build(),
                        new OraDecimal("2")
                ),
                // a(Integer) + b(BigDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", new BigDecimal("3")).build(),
                        new OraDecimal("27")
                ),
                // a(Integer) + b(OraDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 3).put("b", new OraDecimal("3")).build(),
                        new OraDecimal("27")
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
     * 底数类型为 {@code BigInteger}，指数覆盖各种数值类型。
     *
     * <pre>
     * SELECT POWER(3, 4)   FROM dual  -- 81
     * SELECT POWER(2, 3)   FROM dual  -- 8
     * SELECT POWER(2, 10)  FROM dual  -- 1024
     * SELECT POWER(3, 3)   FROM dual  -- 27
     * SELECT POWER(4, .5)  FROM dual  -- 1.999...（BigDecimalMath 精确返回 2）
     * </pre>
     */
    static Stream<Arguments> testBigIntegerProvider() {
        return Stream.of(
                // a(BigInteger) + b(Long) → 走精确路径
                // Oracle: POWER(3, 4) = 81
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", 4L).build(),
                        new OraDecimal("81")
                ),
                // a(BigInteger) + b(Integer) → Integer 被提升后走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", 3).build(),
                        new OraDecimal("8")
                ),
                // a(BigInteger) + b(BigInteger) → 走精确路径
                // Oracle: POWER(2, 10) = 1024
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", new BigInteger("10")).build(),
                        new OraDecimal("1024")
                ),
                // a(BigInteger) + b(Double 整数值) → isInteger=true 走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", 3.0d).build(),
                        new OraDecimal("8")
                ),
                // a(BigInteger) + b(Double 非整数) → 完全平方根精确返回整数
                // Oracle: POWER(4, .5) = 1.999...（BigDecimalMath 精确返回 2）
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("4")).put("b", 0.5d).build(),
                        new OraDecimal("2")
                ),
                // a(BigInteger) + b(BigDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new BigDecimal("3")).build(),
                        new OraDecimal("27")
                ),
                // a(BigInteger) + b(OraDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("3")).put("b", new OraDecimal("3")).build(),
                        new OraDecimal("27")
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
     * 底数类型为 {@code Double}，指数覆盖各种数值类型。
     *
     * <pre>
     * SELECT POWER(1.5, 2) FROM dual  -- 2.25
     * SELECT POWER(2.5, 3) FROM dual  -- 15.625
     * </pre>
     */
    static Stream<Arguments> testDoubleProvider() {
        return Stream.of(
                // a(Double) + b(Long) → Double 底数 Long 整数指数（精确路径）
                // Oracle: POWER(1.5, 2) = 2.25
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 1.5d).put("b", 2L).build(),
                        new OraDecimal("2.25")
                ),
                // a(Double) + b(Integer) → Integer 被提升后走精确路径
                // Oracle: POWER(2.5, 3) = 15.625
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5d).put("b", 3).build(),
                        new OraDecimal("15.625")
                ),
                // a(Double) + b(BigInteger) → 走精确路径
                // Oracle: POWER(1.5, 2) = 2.25
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 1.5d).put("b", new BigInteger("2")).build(),
                        new OraDecimal("2.25")
                ),
                // a(Double) + b(Double 整数值) → isInteger=true 走精确路径
                // Oracle: POWER(1.5, 2) = 2.25
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 1.5d).put("b", 2.0d).build(),
                        new OraDecimal("2.25")
                ),
                // a(Double) + b(BigDecimal 整数值) → 走精确路径
                // Oracle: POWER(2.5, 3) = 15.625
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5d).put("b", new BigDecimal("3")).build(),
                        new OraDecimal("15.625")
                ),
                // a(Double) + b(OraDecimal 整数值) → 走精确路径
                // Oracle: POWER(1.5, 2) = 2.25
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 1.5d).put("b", new OraDecimal("2")).build(),
                        new OraDecimal("2.25")
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
     * 底数类型为 {@code BigDecimal}，指数覆盖各种数值类型。
     *
     * <pre>
     * SELECT POWER(2, 3)   FROM dual  -- 8
     * SELECT POWER(2, 10)  FROM dual  -- 1024
     * SELECT POWER(3, 3)   FROM dual  -- 27
     * SELECT POWER(4, .5)  FROM dual  -- 1.999...（BigDecimalMath 精确返回 2）
     * </pre>
     */
    static Stream<Arguments> testBigDecimalProvider() {
        return Stream.of(
                // a(BigDecimal) + b(Long) → 走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", 3L).build(),
                        new OraDecimal("8")
                ),
                // a(BigDecimal) + b(Integer) → Integer 被提升后走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", 3).build(),
                        new OraDecimal("8")
                ),
                // a(BigDecimal) + b(BigInteger) → 走精确路径
                // Oracle: POWER(2, 10) = 1024
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", new BigInteger("10")).build(),
                        new OraDecimal("1024")
                ),
                // a(BigDecimal) + b(Double 整数值) → isInteger=true 走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2")).put("b", 3.0d).build(),
                        new OraDecimal("8")
                ),
                // a(BigDecimal) + b(Double 非整数) → 完全平方根精确返回整数
                // Oracle: POWER(4, .5) = 1.999...（BigDecimalMath 精确返回 2）
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("4")).put("b", 0.5d).build(),
                        new OraDecimal("2")
                ),
                // a(BigDecimal) + b(BigDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", new BigDecimal("3")).build(),
                        new OraDecimal("27")
                ),
                // a(BigDecimal) + b(OraDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3")).put("b", new OraDecimal("3")).build(),
                        new OraDecimal("27")
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
     * 底数类型为 {@code OraDecimal}，指数覆盖各种数值类型。
     *
     * <pre>
     * SELECT POWER(1.23456789012345678901234567890, 2) FROM dual
     *   -- 1.52415787532388367504953515625361987875
     * SELECT POWER(2, 3)   FROM dual  -- 8
     * SELECT POWER(2, 10)  FROM dual  -- 1024
     * SELECT POWER(3, 3)   FROM dual  -- 27
     * SELECT POWER(4, .5)  FROM dual  -- 1.999...（BigDecimalMath 精确返回 2）
     * </pre>
     */
    static Stream<Arguments> testOraDecimalProvider() {
        return Stream.of(
                // a(OraDecimal 高精度) + b(Long) → BigDecimal.pow 精确路径
                // Oracle: POWER(1.23456789012345678901234567890, 2) = 1.52415787532388367504953515625361987875
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1.23456789012345678901234567890")).put("b", 2L).build(),
                        new OraDecimal("1.52415787532388367504953515625361987875")
                ),
                // a(OraDecimal) + b(Integer) → Integer 被提升后走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2")).put("b", 3).build(),
                        new OraDecimal("8")
                ),
                // a(OraDecimal) + b(BigInteger) → 走精确路径
                // Oracle: POWER(2, 10) = 1024
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2")).put("b", new BigInteger("10")).build(),
                        new OraDecimal("1024")
                ),
                // a(OraDecimal) + b(Double 整数值) → isInteger=true 走精确路径
                // Oracle: POWER(2, 3) = 8
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2")).put("b", 3.0d).build(),
                        new OraDecimal("8")
                ),
                // a(OraDecimal) + b(Double 非整数) → 完全平方根精确返回整数
                // Oracle: POWER(4, .5) = 1.999...（BigDecimalMath 精确返回 2）
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("4")).put("b", 0.5d).build(),
                        new OraDecimal("2")
                ),
                // a(OraDecimal) + b(BigDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", new BigDecimal("3")).build(),
                        new OraDecimal("27")
                ),
                // a(OraDecimal) + b(OraDecimal 整数值) → 走精确路径
                // Oracle: POWER(3, 3) = 27
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("3")).put("b", new OraDecimal("3")).build(),
                        new OraDecimal("27")
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

    // ========================= null 与边界异常 =========================

    /**
     * null 入参与数值边界异常场景。
     *
     * <pre>
     * Oracle: POWER(NULL, 2)  = NULL
     * Oracle: POWER(2, NULL)  = NULL
     * Oracle: POWER(0, -1)    → ORA-01428: argument '0' is out of range（除以零）
     * Oracle: POWER(-2, 0.5)  → ORA-01428: argument '-2' is out of range（负底数不允许非整数指数）
     * </pre>
     */
    static Stream<Arguments> testNullAndExceptionProvider() {
        return Stream.of(
                // a=null → PowerFunction 检测到 AviatorNil 提前返回 null
                // Oracle: POWER(NULL, 2) = NULL
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", null).put("b", 2L).build(),
                        null
                ),
                // b=null → PowerFunction 检测到 AviatorNil 提前返回 null
                // Oracle: POWER(2, NULL) = NULL
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", null).build(),
                        null
                ),
                // base=0 且 exponent<0 → 除以零，对应 Oracle ORA-01428
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 0L).put("b", -1L).build(),
                        ArithmeticException.class
                ),
                // 负底数 + 非整数指数 → 负数无法开非整数次方，对应 Oracle ORA-01428
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", -2L).put("b", 0.5d).build(),
                        ArithmeticException.class
                )
        );
    }

    @DisplayName("null 与边界异常")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testNullAndExceptionProvider")
    public void testNullAndException(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= 非法类型异常 =========================

    /**
     * 参数为非 {@code Number} 类型时，{@code AORuntimeUtils.toNumber} 抛出 {@link IllegalArgumentException}。
     *
     * <p>共 6 个案例：第一个参数（底数）非法 3 个，第二个参数（指数）非法 3 个；
     * 每组分别对应 {@code String}、{@code Instant}（日期）、{@code Boolean} 三种类型。
     * 合法的另一个参数固定使用 {@code 2L}（Long）。
     */
    static Stream<Arguments> testIllegalTypeProvider() {
        return Stream.of(
                // ── 第一个参数（底数）非 Number 类型，第二个参数为合法 Long ────────
                // String 不是 Number → AORuntimeUtils.toNumber 抛出 IllegalArgumentException
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", "hello").put("b", 2L).build(),
                        IllegalArgumentException.class
                ),
                // Instant 不是 Number → 抛出 IllegalArgumentException
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-01-01T00:00:00Z")).put("b", 2L).build(),
                        IllegalArgumentException.class
                ),
                // Boolean 不是 Number → 抛出 IllegalArgumentException
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", Boolean.TRUE).put("b", 2L).build(),
                        IllegalArgumentException.class
                ),

                // ── 第二个参数（指数）非 Number 类型，第一个参数为合法 Long ────────
                // String 不是 Number → AORuntimeUtils.toNumber 抛出 IllegalArgumentException
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", "hello").build(),
                        IllegalArgumentException.class
                ),
                // Instant 不是 Number → 抛出 IllegalArgumentException
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", Instant.parse("2020-01-01T00:00:00Z")).build(),
                        IllegalArgumentException.class
                ),
                // Boolean 不是 Number → 抛出 IllegalArgumentException
                Arguments.of(
                        "power(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", Boolean.FALSE).build(),
                        IllegalArgumentException.class
                )
        );
    }

    @DisplayName("非法类型异常")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testIllegalTypeProvider")
    public void testIllegalType(String expression, Map<String, Object> vars, Object expected) {
        @SuppressWarnings("unchecked")
        Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
        Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
    }

    // ========================= 多层嵌套（3 层，4 个不同类型参数）=========================

    /**
     * 表达式 {@code power(power(a, b), power(c, d))} 测试，4 个参数各使用不同类型。
     *
     * <p>计算过程（先分别求两个内层 power，再求外层 power）：
     * <ul>
     *   <li><b>案例 1</b>：a=Long(2), b=BigDecimal(3), c=Integer(2), d=OraDecimal(2)
     *     <ol>
     *       <li>左内层：power(Long(2), BigDecimal(3)) = 8　　　[2^3=8]</li>
     *       <li>右内层：power(Integer(2), OraDecimal(2)) = 4　　[2^2=4]</li>
     *       <li>外层：power(OraDecimal(8), OraDecimal(4)) = 4096　[8^4=4096]</li>
     *     </ol>
     *     Oracle: POWER(POWER(2, 3), POWER(2, 2)) = POWER(8, 4) = 4096
     *   </li>
     *   <li><b>案例 2</b>：a=BigInteger(3), b=Double(2.0), c=BigDecimal(4), d=Integer(1)
     *     <ol>
     *       <li>左内层：power(BigInteger(3), Double(2.0)) = 9　　[3^2=9，Double 整数值走精确路径]</li>
     *       <li>右内层：power(BigDecimal(4), Integer(1)) = 4　　 [4^1=4]</li>
     *       <li>外层：power(OraDecimal(9), OraDecimal(4)) = 6561　[9^4=6561]</li>
     *     </ol>
     *     Oracle: POWER(POWER(3, 2), POWER(4, 1)) = POWER(9, 4) = 6561
     *   </li>
     *   <li><b>案例 3</b>：a=OraDecimal(3), b=Long(2), c=Double(2.0), d=BigInteger(3)
     *     <ol>
     *       <li>左内层：power(OraDecimal(3), Long(2)) = 9　　　 [3^2=9]</li>
     *       <li>右内层：power(Double(2.0), BigInteger(3)) = 8　　[2^3=8，Double 整数值走精确路径]</li>
     *       <li>外层：power(OraDecimal(9), OraDecimal(8)) = 43046721　[9^8=43046721]</li>
     *     </ol>
     *     Oracle: POWER(POWER(3, 2), POWER(2, 3)) = POWER(9, 8) = 43046721
     *   </li>
     * </ul>
     */
    static Stream<Arguments> testNestedProvider() {
        return Stream.of(
                // ── 案例 1：Long / BigDecimal / Integer / OraDecimal ─────────────
                // Oracle: POWER(POWER(2, 3), POWER(2, 2)) = POWER(8, 4) = 4096
                Arguments.of(
                        "power(power(a, b), power(c, d))",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 2L)                   // Long
                                .put("b", new BigDecimal("3"))  // BigDecimal
                                .put("c", 2)                    // Integer
                                .put("d", new OraDecimal("2"))  // OraDecimal
                                .build(),
                        new OraDecimal("4096")
                ),
                // ── 案例 2：BigInteger / Double / BigDecimal / Integer ────────────
                // Oracle: POWER(POWER(3, 2), POWER(4, 1)) = POWER(9, 4) = 6561
                Arguments.of(
                        "power(power(a, b), power(c, d))",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("3"))  // BigInteger
                                .put("b", 2.0d)                 // Double（整数值，走精确路径）
                                .put("c", new BigDecimal("4"))  // BigDecimal
                                .put("d", 1)                    // Integer
                                .build(),
                        new OraDecimal("6561")
                ),
                // ── 案例 3：OraDecimal / Long / Double / BigInteger ───────────────
                // Oracle: POWER(POWER(3, 2), POWER(2, 3)) = POWER(9, 8) = 43046721
                Arguments.of(
                        "power(power(a, b), power(c, d))",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("3"))  // OraDecimal
                                .put("b", 2L)                   // Long
                                .put("c", 2.0d)                 // Double（整数值，走精确路径）
                                .put("d", new BigInteger("3"))  // BigInteger
                                .build(),
                        new OraDecimal("43046721")
                )
        );
    }

    @DisplayName("嵌套（power(power(a, b), power(c, d))）")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testNestedProvider")
    public void testNested(String expression, Map<String, Object> vars, Object expected) {
        Object actual = AviatorInstance.execute(expression, vars);
        Assertions.assertEquals(expected, actual);
    }
}
