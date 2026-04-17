package com.eredar.aviatororacle;

import com.eredar.aviatororacle.dto.DecodeParamTestDTO;
import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.uitls.OracleFunctionUtils;
import com.eredar.aviatororacle.testUtils.FileUtils;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.eredar.aviatororacle.testUtils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
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
 * 测试：模拟Oracle数据库 {@code decode()} 方法
 */
@DisplayName("decode 方法测试")
public class DecodeFunctionTest {

    /**
     * 固定 4 个入参：decode(expr, search, 匹配分支, 默认分支)。
     * 变量 a 为表达式（由各 test* 限定类型），b 为 search，覆盖 9 种类型 ×（匹配 / 不匹配）。
     */
    private static final String DECODE_AB_4 = "decode(a, b, \"MATCH\", \"NO_MATCH\")";

    /** 与 epochSecond=1 的 Instant 对应的 ISO-8601 字符串，用于 String↔Instant 交叉用例。 */
    private static final String ISO_INSTANT_1S = "1970-01-01T00:00:01Z";

    /** 与 {@link OracleFunctionUtils} 中 Boolean↔Instant 约定一致：true ↔ epochSecond=1。 */
    private static final Instant INSTANT_FOR_TRUE = Instant.ofEpochSecond(1);

    /**
     * 构造仅含 a、b 的变量表，减少重复代码。
     *
     * @param aVal 表达式取值（类型由所在 test* 方法语义决定）
     * @param bVal search 取值，循环覆盖 Long、Integer 等 9 类
     */
    private static Map<String, Object> ab(Object aVal, Object bVal) {
        return HashMapBuilder.<String, Object>builder().put("a", aVal).put("b", bVal).build();
    }

    // ========================= Long：表达式类型为 Long =========================

    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // Long
                Arguments.of(DECODE_AB_4, ab(1L, 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, 99L), "NO_MATCH"),
                // Integer
                Arguments.of(DECODE_AB_4, ab(1L, 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, 99), "NO_MATCH"),
                // BigInteger
                Arguments.of(DECODE_AB_4, ab(1L, new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, new BigInteger("99")), "NO_MATCH"),
                // Double
                Arguments.of(DECODE_AB_4, ab(1L, 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, 99.0d), "NO_MATCH"),
                // BigDecimal
                Arguments.of(DECODE_AB_4, ab(1L, new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, new BigDecimal("99")), "NO_MATCH"),
                // OraDecimal
                Arguments.of(DECODE_AB_4, ab(1L, new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, new OraDecimal("99")), "NO_MATCH"),
                // String（与数值 1 解析一致 / 不一致 / 异常）
                Arguments.of(DECODE_AB_4, ab(1L, "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, "99"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1L, "Y"), IllegalArgumentException.class),
                // Instant（异常）
                Arguments.of(DECODE_AB_4, ab(1L, Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                // Boolean（异常）
                Arguments.of(DECODE_AB_4, ab(1L, true), IllegalArgumentException.class)
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
                Arguments.of(DECODE_AB_4, ab(1, 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, 99L), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, 99), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, new BigInteger("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, 99.0d), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, new BigDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, new OraDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, "99"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1, "Y"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(1, Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(1, true), IllegalArgumentException.class)
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
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), 99L), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), 99), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), new BigInteger("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), 99.0d), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), new BigDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), new OraDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), "99"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), "Y"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(new BigInteger("1"), true), IllegalArgumentException.class)
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
                Arguments.of(DECODE_AB_4, ab(1.0d, 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, 99L), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, 99), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, new BigInteger("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, 99.0d), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, new BigDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, new OraDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, "99"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(1.0d, "Y"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(1.0d, Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(1.0d, true), IllegalArgumentException.class)
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
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), 99L), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), 99), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), new BigInteger("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), 99.0d), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), new BigDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), new OraDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), "99"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), "Y"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(new BigDecimal("1"), true), IllegalArgumentException.class)
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
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), 99L), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), 99), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), new BigInteger("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), 99.0d), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), new BigDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), new OraDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), "99"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), "Y"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(new OraDecimal("1"), true), IllegalArgumentException.class)
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

    // ========================= String（与 Instant 交叉时使用 ISO-8601 字符串） =========================

    static Stream<Arguments> testStringProvider() {
        return Stream.of(
                Arguments.of(DECODE_AB_4, ab("1", 1L), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", 99L), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", 1), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", 99), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", new BigInteger("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", new BigInteger("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", 1.0d), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", 99.0d), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", new BigDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", new BigDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", new OraDecimal("1")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", new OraDecimal("99")), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("1", "1"), "MATCH"),
                Arguments.of(DECODE_AB_4, ab("hello", "world"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab("Y", "1"), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(ISO_INSTANT_1S, Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab("true", true), IllegalArgumentException.class)
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
        return Stream.of(
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, 1L), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, 1), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, new BigInteger("1")), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, 1.0d), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, new BigDecimal("1")), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, new OraDecimal("1")), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, "1970-01-01T00:00:02Z"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(Instant.parse("2020-02-01T03:36:19Z"), Instant.parse("2020-02-01T03:36:19Z")), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, Instant.ofEpochSecond(2)), "NO_MATCH"),
                Arguments.of(DECODE_AB_4, ab(INSTANT_FOR_TRUE, true), IllegalArgumentException.class)
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
                Arguments.of(DECODE_AB_4, ab(true, 1L), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, 1), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, new BigInteger("1")), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, 1.0d), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, new BigDecimal("1")), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, new OraDecimal("1")), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, "true"), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, Instant.ofEpochSecond(1)), IllegalArgumentException.class),
                Arguments.of(DECODE_AB_4, ab(true, true), "MATCH"),
                Arguments.of(DECODE_AB_4, ab(true, false), "NO_MATCH")
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

    static Stream<Arguments> testErrorProvider() {
        return Stream.of(
                // 1个入参
                Arguments.of(
                        "decode(a1)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a1", 2L)
                                .build(),
                        IllegalArgumentException.class
                ),
                // 2个入参
                Arguments.of(
                        "decode(a1, a2)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a1", new BigInteger("2"))
                                .put("a2", 2)
                                .build(),
                        IllegalArgumentException.class
                )
        );
    }

    @DisplayName("异常场景测试")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testErrorProvider")
    public void testError(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    static Stream<Arguments> testParamsOrderProvider() {
        return FileUtils.readFileAsLines("testParamsOrderData.dat").stream().map(line -> {
            DecodeParamTestDTO dto = JsonUtils.readValue(line, new TypeReference<DecodeParamTestDTO>() {
            });
            return Arguments.of(
                    dto.getExpression(),
                    dto.getEnv(),
                    dto.getExpected()
            );
        });
    }

    @DisplayName("测试call传参到decode时顺序是否正确")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testParamsOrderProvider")
    public void testParamsOrder(String expression, Map<String, Object> vars, Object expected) {
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
