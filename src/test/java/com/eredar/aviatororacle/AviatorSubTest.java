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
 * AviatorOracle 减法运算单元测试，对应 {@code OperatorType.SUB}（即 {@code -} 号）。
 */
@DisplayName("AviatorOracle 减法测试")
public class AviatorSubTest {

    // ========================= Long =========================

    /**
     * 左操作数为 {@link Long} 时，{@code a - b} 与各类右操作数组合。
     */
    static Stream<Arguments> testLongProvider() {
        return Stream.of(
                // a(Long) - b(Long) → Long
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", 10L).build(),
                        2L
                ),
                // a(Long) - b(Integer) → Long（Aviator 内部将 Integer 提升为 Long）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", 10).build(),
                        2L),
                // a(Long) - b(BigInteger) → BigInteger（BigInteger 优先级高于 Long）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", new BigInteger("10")).build(),
                        new BigInteger("2")
                ),
                // a(Long) - b(Double) → OraDecimal（Double 优先级高于 Long；与 Long 的 decimal 路径一致）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("1.51639344262295084")
                ),
                // a(Long) - b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", new BigDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(Long) - b(OraDecimal) → OraDecimal（最高优先级）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", new OraDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(Long) - b(String) → 抛出异常（减法不支持字符串拼接语义）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) - b(Instant) → Instant（AOAviatorNumber#sub：instantMinusDays(Instant, Long)）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Long) - b(Boolean) → 抛出异常（Boolean 无法参与数值运算）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12L).put("b", true).build(),
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
                // a(Integer) - b(Long) → Long
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", 10L).build(),
                        2L
                ),
                // a(Integer) - b(Integer) → Long（Aviator 将两个 Integer 统一存储为 Long）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", 10).build(),
                        2L
                ),
                // a(Integer) - b(BigInteger) → BigInteger
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", new BigInteger("10")).build(),
                        new BigInteger("2")
                ),
                // a(Integer) - b(Double) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("1.51639344262295084")
                ),
                // a(Integer) - b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", new BigDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(Integer) - b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", new OraDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(Integer) - b(String) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) - b(Instant) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 2).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Integer) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 12).put("b", true).build(),
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
                // a(BigInteger) - b(Long) → BigInteger
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", 10L).build(),
                        new BigInteger("2")
                ),
                // a(BigInteger) - b(Integer) → BigInteger
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", 10).build(),
                        new BigInteger("2")
                ),
                // a(BigInteger) - b(BigInteger) → BigInteger
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", new BigInteger("10")).build(),
                        new BigInteger("2")
                ),
                // a(BigInteger) - b(Double) → OraDecimal（Double 优先级高于 BigInteger）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("1.51639344262295084")
                ),
                // a(BigInteger) - b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", new BigDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(BigInteger) - b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", new OraDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(BigInteger) - b(String) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) - b(Instant) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).put("b", Instant.parse("2020-02-01T03:36:19Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigInteger) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("12")).put("b", true).build(),
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
        // 左操作数均为 double 字面量（与 AviatorAddTest 的 Double 节对齐；逆运算由「和 − 加数」构造）。
        // 期望值须与引擎经 IEEE double 与 OraDecimal#oracleDecimal 归一化后的字符串一致（可能与字面量小数位不完全相同）。
        final double d0 = 2.4836065573770491803278688524590163934426;
        return Stream.of(
                // a(Double) - b(Long) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", 2L).build(),
                        new OraDecimal("0.4836065573770494")
                ),
                // a(Double) - b(Integer) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", 2).build(),
                        new OraDecimal("0.4836065573770494")
                ),
                // a(Double) - b(BigInteger) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", new BigInteger("2")).build(),
                        new OraDecimal("0.4836065573770494")
                ),
                // a(Double) - b(Double) → OraDecimal（两 double 相减时二进制舍入与上述路径可能差末位）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 0.96721311475409832d).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("0.48360655737704914")
                ),
                // a(Double) - b(BigDecimal) → OraDecimal（BigDecimal 优先级高于 Double）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", new BigDecimal("2")).build(),
                        new OraDecimal("0.4836065573770494")
                ),
                // a(Double) - b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", new OraDecimal("2")).build(),
                        new OraDecimal("0.4836065573770494")
                ),
                // a(Double) - b(String) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", d0).put("b", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) - b(Instant) → Instant（instantMinusDays(Instant, Double)）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", 31.57349857284537940384752204323255406344).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Double) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
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
                // a(BigDecimal) - b(Long) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", 10L).build(),
                        new OraDecimal("2")
                ),
                // a(BigDecimal) - b(Integer) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", 10).build(),
                        new OraDecimal("2")
                ),
                // a(BigDecimal) - b(BigInteger) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", new BigInteger("10")).build(),
                        new OraDecimal("2")
                ),
                // a(BigDecimal) - b(Double) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("2.48360655737704916")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("2")
                ),
                // a(BigDecimal) - b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", new BigDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(BigDecimal) - b(OraDecimal) → OraDecimal（OraDecimal 优先级最高）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", new OraDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(BigDecimal) - b(String) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) - b(Instant) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("31.57349857284537940384752204323255406344")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(BigDecimal) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("12")).put("b", true).build(),
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
                // a(OraDecimal) - b(Long) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", 10L).build(),
                        new OraDecimal("2")
                ),
                // a(OraDecimal) - b(Integer) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", 10).build(),
                        new OraDecimal("2")
                ),
                // a(OraDecimal) - b(BigInteger) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", new BigInteger("10")).build(),
                        new OraDecimal("2")
                ),
                // a(OraDecimal) - b(Double) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("2.48360655737704916")).put("b", 0.4836065573770491803278688524590163934426).build(),
                        new OraDecimal("2")
                ),
                // a(OraDecimal) - b(BigDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", new BigDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(OraDecimal) - b(OraDecimal) → OraDecimal
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", new OraDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("11.51639344262295081967213114754098360656")
                ),
                // a(OraDecimal) - b(String) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) - b(Instant) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("31.57349857284537940384752204323255406344")).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(OraDecimal) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("12")).put("b", true).build(),
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
                // 左操作数为 String 时，减法走 Aviator 默认实现，不支持与数值直接相减
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", 10L).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", 10).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", new BigInteger("10")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", 0.4836065573770491803278688524590163934426).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", new BigDecimal("10")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", new OraDecimal("10")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", Instant.parse("2024-01-01T00:00:10Z")).build(),
                        ExpressionRuntimeException.class
                ),
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", "12").put("b", true).build(),
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
        return Stream.of(
                // a(Instant) - b(Long) → Instant（减去整天偏移）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", 10L).build(),
                        Instant.parse("2020-02-25T03:36:19Z")
                ),
                // a(Instant) - b(Integer) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", 10).build(),
                        Instant.parse("2020-02-25T03:36:19Z")
                ),
                // a(Instant) - b(BigInteger) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-06T03:36:19Z")).put("b", new BigInteger("10")).build(),
                        Instant.parse("2020-02-25T03:36:19Z")
                ),
                // a(Instant) - b(Double) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-17T03:36:19Z")).put("b", 31.57349857284537940384752204323255406344).build(),
                        Instant.parse("2020-02-14T13:50:29Z")
                ),
                // a(Instant) - b(BigDecimal) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-17T03:36:19Z")).put("b", new BigDecimal("31.57349857284537940384752204323255406344")).build(),
                        Instant.parse("2020-02-14T13:50:29Z")
                ),
                // a(Instant) - b(OraDecimal) → Instant
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-17T03:36:19Z")).put("b", new OraDecimal("31.57349857284537940384752204323255406344")).build(),
                        Instant.parse("2020-02-14T13:50:29Z")
                ),
                // a(Instant) - b(String) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-17T03:36:19Z")).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Instant) - b(Instant) → OraDecimal（间隔天数，非字符串拼接）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-02-14T13:50:29Z")).put("b", Instant.parse("2020-03-17T03:36:19Z")).build(),
                        new OraDecimal("-31.57349537037037037037037037037037037037")
                ),
                // a(Instant) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", Instant.parse("2020-03-17T03:36:19Z")).put("b", true).build(),
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
                // a(Boolean) - b(Long) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10L).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(Integer) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 10).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(BigInteger) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigInteger("10")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(Double) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", 0.4836065573770491803278688524590163934426).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(BigDecimal) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new BigDecimal("10.091987349502")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(OraDecimal) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", new OraDecimal("10.483925798")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(String) → 抛出异常（减法无字符串拼接）
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", "10").build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(Instant) → 抛出异常
                Arguments.of(
                        "a - b",
                        HashMapBuilder.<String, Object>builder().put("a", true).put("b", Instant.parse("2020-02-01T00:00:00Z")).build(),
                        ExpressionRuntimeException.class
                ),
                // a(Boolean) - b(Boolean) → 抛出异常
                Arguments.of(
                        "a - b",
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
