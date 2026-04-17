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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 测试：模拟Oracle数据库 {@code nvl()} 方法
 */
@DisplayName("nvl 方法测试")
public class NvlFunctionTest {

    /**
     * {@code b} 参数类型与取值固定顺序：Long → Integer → BigInteger → Double → BigDecimal → OraDecimal → String → Instant → Boolean。
     * 每个单测里对「a 非 null」「a 为 null」两组场景都按此顺序依次与 {@code b} 配对。
     */
    private static final Instant NVL_B_INSTANT = Instant.parse("2021-06-15T12:30:00Z");

    private static final Object[] NVL_B_SEQUENCE = new Object[]{
            3L,
            3,
            new BigInteger("3"),
            3.1d,
            new BigDecimal("3.1"),
            new OraDecimal("3.1"),
            "y",
            NVL_B_INSTANT,
            Boolean.FALSE
    };

    /**
     * 构造 {@code nvl(a, b)} 的变量表
     */
    private static Map<String, Object> vars(Object a, Object b) {
        return HashMapBuilder.<String, Object>builder().put("a", a).put("b", b).build();
    }

    /**
     * 生成参数流：先「a 恒为非 null 的 a0，b 按 NVL_B_SEQUENCE 遍历」，再「a 为 null，b 按 NVL_B_SEQUENCE 遍历」；期望分别为 a0 与当前 b。
     */
    private static Stream<Arguments> nvlProviderWhenAThenBSequence(Object aNotNull) {
        Stream<Arguments> whenAIsNotNull = Arrays.stream(NVL_B_SEQUENCE).map(b -> Arguments.of(
                "nvl(a, b)",
                vars(aNotNull, b),
                aNotNull
        ));
        Stream<Arguments> whenAIsNull = Arrays.stream(NVL_B_SEQUENCE).map(b -> Arguments.of(
                "nvl(a, b)",
                vars(null, b),
                b
        ));
        return Stream.concat(whenAIsNotNull, whenAIsNull);
    }

    static Stream<Arguments> testLongProvider() {
        return nvlProviderWhenAThenBSequence(2L);
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

    static Stream<Arguments> testIntegerProvider() {
        return nvlProviderWhenAThenBSequence(2);
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

    static Stream<Arguments> testBigIntegerProvider() {
        return nvlProviderWhenAThenBSequence(new BigInteger("2"));
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

    static Stream<Arguments> testDoubleProvider() {
        return nvlProviderWhenAThenBSequence(2.5d);
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

    static Stream<Arguments> testBigDecimalProvider() {
        return nvlProviderWhenAThenBSequence(new BigDecimal("2.5"));
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

    static Stream<Arguments> testOraDecimalProvider() {
        return nvlProviderWhenAThenBSequence(new OraDecimal("2.5"));
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

    static Stream<Arguments> testStringProvider() {
        return nvlProviderWhenAThenBSequence("x");
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

    static Stream<Arguments> testInstantProvider() {
        Instant aNotNull = Instant.parse("2020-01-01T00:00:00Z");
        return nvlProviderWhenAThenBSequence(aNotNull);
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

    static Stream<Arguments> testBooleanProvider() {
        return nvlProviderWhenAThenBSequence(Boolean.TRUE);
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
