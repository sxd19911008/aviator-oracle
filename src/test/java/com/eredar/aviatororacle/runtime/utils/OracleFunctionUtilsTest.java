package com.eredar.aviatororacle.runtime.utils;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.uitls.OracleFunctionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Oracle方法测试")
public class OracleFunctionUtilsTest {

    // -------------------------------------------------------------------------
    // decode
    // -------------------------------------------------------------------------

    /**
     * decode 正常返回值场景：最后一列 {@code null} 表示期望 {@link OracleFunctionUtils#decode} 返回 null。
     */
    static Stream<Arguments> decodeMatchProvider() {
        return Stream.of(
                Arguments.of("匹配第一个 search", (Supplier<Object>) () -> OracleFunctionUtils.decode("1", "1", "A", "2", "B", "C"), "A"),
                Arguments.of("匹配第二个 search", (Supplier<Object>) () -> OracleFunctionUtils.decode("2", "1", "A", "2", "B", "C"), "B"),
                Arguments.of("无匹配返回默认值", (Supplier<Object>) () -> OracleFunctionUtils.decode("3", "1", "A", "2", "B", "C"), "C"),
                Arguments.of("无匹配且无默认值返回 null", (Supplier<Object>) () -> OracleFunctionUtils.decode("3", "1", "A", "2", "B"), null),
                Arguments.of("null 与 null 匹配", (Supplier<Object>) () -> OracleFunctionUtils.decode(null, null, "Result", "Other"), "Result"),
                Arguments.of("表达式为 null 且 search 非 null", (Supplier<Object>) () -> OracleFunctionUtils.decode(null, "1", "Result", "Default"), "Default"),
                Arguments.of("Integer 与 Long 数值相等", (Supplier<Object>) () -> OracleFunctionUtils.decode(100, 100L, "Match", "No Match"), "Match"),
                Arguments.of("Long 与 OraDecimal 数值相等", (Supplier<Object>) () -> OracleFunctionUtils.decode(200L, new OraDecimal("200.00"), "Match", "No Match"), "Match"),
                Arguments.of("OraDecimal 不同精度数值相等", (Supplier<Object>) () -> OracleFunctionUtils.decode(new OraDecimal("3.14"), new OraDecimal("3.1400"), "Match", "No Match"), "Match")
        );
    }

    @DisplayName("decode 方法匹配与返回值测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeMatchProvider")
    public void testDecodeMatch(String caseId, Supplier<Object> decodeCall, Object expected) {
        if (expected == null) {
            Assertions.assertNull(decodeCall.get());
        } else {
            Assertions.assertEquals(expected, decodeCall.get());
        }
    }

    static Stream<Arguments> decodeInvalidArgsProvider() {
        //noinspection Convert2MethodRef
        return Stream.of(
                Arguments.of("入参仅2个", (Executable) () -> OracleFunctionUtils.decode("1", "2")),
                Arguments.of("入参仅1个", (Executable) () -> OracleFunctionUtils.decode("1")),
                Arguments.of("入参0个", (Executable) () -> OracleFunctionUtils.decode())
        );
    }

    @DisplayName("decode 方法非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeInvalidArgsProvider")
    public void testDecodeInvalidArgs(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }


    // -------------------------------------------------------------------------
    // floor
    // -------------------------------------------------------------------------

    static Stream<Arguments> testFloorProvider() {
        return Stream.of(
                Arguments.of("Long", 1342534967873799582L, 1342534967873799582L),
                Arguments.of("Integer", 143262, 143262),
                Arguments.of("BigInteger", new BigInteger("1342534967873799582"), new BigInteger("1342534967873799582")),
                Arguments.of("Double", 1.993565624, new OraDecimal("1")),
                Arguments.of("BigDecimal", new BigDecimal("1.9999431565624544763765735"), new OraDecimal("1")),
                Arguments.of("OraDecimal", new OraDecimal("1.9999431565624544763765735"), new OraDecimal("1")),
                Arguments.of("Long", -1342534967873799582L, -1342534967873799582L),
                Arguments.of("Integer", -143262, -143262),
                Arguments.of("BigInteger", new BigInteger("-1342534967873799582"), new BigInteger("-1342534967873799582")),
                Arguments.of("Double", -1.113565624, new OraDecimal("-2")),
                Arguments.of("BigDecimal", new BigDecimal("-1.1199431565624544763765735"), new OraDecimal("-2")),
                Arguments.of("OraDecimal", new OraDecimal("-1.1199431565624544763765735"), new OraDecimal("-2")),
                Arguments.of("String，异常", "1.9999431565624544763765735", IllegalArgumentException.class),
                Arguments.of("Instant，异常", Instant.parse("2020-02-01T03:36:19Z"), IllegalArgumentException.class),
                Arguments.of("Boolean，异常", true, IllegalArgumentException.class)
        );
    }

    @DisplayName("floor 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testFloorProvider")
    public void testFloor(String caseId, Object n, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> OracleFunctionUtils.floor(n));
        } else {
            Number actual = OracleFunctionUtils.floor(n);
            Assertions.assertEquals(expected, actual);
        }
    }

    // -------------------------------------------------------------------------
    // ceil
    // -------------------------------------------------------------------------

    static Stream<Arguments> testCeilProvider() {
        return Stream.of(
                Arguments.of( "null", null, null),
                Arguments.of( "Byte", (byte) 2, (byte) 2),
                Arguments.of( "Short", (short) 2, (short) 2),
                Arguments.of( "Long", 2L, 2L),
                Arguments.of( "Integer", 2, 2),
                Arguments.of( "BigInteger", new BigInteger("2"), new BigInteger("2")),
                Arguments.of( "Double", 1.193565624, new OraDecimal("2")),
                Arguments.of( "BigDecimal", new BigDecimal("1.1999431565624544763765735"), new OraDecimal("2")),
                Arguments.of( "OraDecimal", new OraDecimal("1.1999431565624544763765735"), new OraDecimal("2")),
                Arguments.of( "-Byte", (byte) -2, (byte) -2),
                Arguments.of( "-Short", (short) -2, (short) -2),
                Arguments.of( "-Long", -2L, -2L),
                Arguments.of( "-Integer", -2, -2),
                Arguments.of( "-BigInteger", new BigInteger("-2"), new BigInteger("-2")),
                Arguments.of( "-Double", -1.993565624, new OraDecimal("-1")),
                Arguments.of( "-BigDecimal", new BigDecimal("-1.9999431565624544763765735"), new OraDecimal("-1")),
                Arguments.of( "-OraDecimal", new OraDecimal("-1.9999431565624544763765735"), new OraDecimal("-1")),
                Arguments.of( "String，异常", "2", IllegalArgumentException.class),
                Arguments.of( "Instant，异常", Instant.parse("2020-02-01T03:36:19Z"), IllegalArgumentException.class),
                Arguments.of( "Boolean，异常", true, IllegalArgumentException.class)
        );
    }

    @DisplayName("ceil 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testCeilProvider")
    public void testCeil(String caseId, Object n, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> OracleFunctionUtils.ceil(n));
        } else {
            Number actual = OracleFunctionUtils.ceil(n);
            Assertions.assertEquals(expected, actual);
        }
    }

    // -------------------------------------------------------------------------
    // round
    // -------------------------------------------------------------------------

    /**
     * {@link OracleFunctionUtils#round(Number)} 场景数据：等价于 {@code round(n, 0)}。
     * <p>第三列期望值为 {@code null} 表示返回 {@code null}；为 {@link OraDecimal} 时表示经 Oracle NUMBER 规则舍入后的结果。
     */
    static Stream<Arguments> testRoundOneArgProvider() {
        return Stream.of(
                Arguments.of("入参为 null", null, null),
                Arguments.of("Long 已为整数且 scale=0 时直接返回原装箱对象", 42L, 42L),
                Arguments.of("Integer 已为整数且 scale=0 时直接返回原装箱对象", 7, 7),
                Arguments.of("Short 已为整数且 scale=0 时直接返回原装箱对象", (short) 3, (short) 3),
                Arguments.of("Byte 已为整数且 scale=0 时直接返回原装箱对象", (byte) 9, (byte) 9),
                Arguments.of("Double 按 HALF_UP 四舍五入到整数", 2.5d, new OraDecimal("3")),
                Arguments.of("BigDecimal 按 HALF_UP 四舍五入到整数", new BigDecimal("3.4159"), new OraDecimal("3")),
                Arguments.of("OraDecimal 按 HALF_UP 四舍五入到整数", new OraDecimal("9.576"), new OraDecimal("10"))
        );
    }

    @DisplayName("round(number) 方法测试（等价于 newScale=0）")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testRoundOneArgProvider")
    public void testRoundOneArg(String caseId, Object n, Object expected) {
        Number actual = OracleFunctionUtils.round((Number) n);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * {@link OracleFunctionUtils#round(Number, Number)} 场景数据（均为正常返回或 {@code number == null}）。
     * <p>与 Oracle 极限标度一致：
     * <p>{@code newScale >= 40} 时直接返回入参 {@code number}；
     * <p>{@code newScale <= -40} 时结果为 {@code 0}；
     * <p>其余列与 {@link OraDecimal} 舍入行为一致。
     */
    static Stream<Arguments> testRoundTwoArgsProvider() {
        return Stream.of(
                Arguments.of("number 为 null 时返回 null", null, 2, null),
                // newScale 与极限区间：>=40 原样返回 number；<=-40 返回 0
                Arguments.of("primitive newScale>=40 时原样返回 Long", 999L, 40, 999L),
                Arguments.of("Double 形式的 newScale>=40 时原样返回 Double", 12.3d, 40.0d, 12.3d),
                Arguments.of("Integer 入参且 OraDecimal newScale>=40 时原样返回", 1, new OraDecimal("40"), 1),
                Arguments.of(
                        "BigInteger 入参且 BigInteger newScale>=40 时原样返回 BigInteger 入参",
                        new BigInteger("5"),
                        new BigInteger("40"),
                        new BigInteger("5")
                ),
                Arguments.of(
                        "OraDecimal 入参与 newScale>=40 时原样返回（见 testRoundTwoArgs 的 assertSame 分支）",
                        new OraDecimal("2.71"),
                        new OraDecimal("40"),
                        new OraDecimal("2.71")
                ),
                Arguments.of("Long newScale<=-40 时结果为 0", Long.MAX_VALUE, -40, 0),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40,
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40L,
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39L,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("-40"),
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40.9d,
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39.9d,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        new BigDecimal("1234567890123456789012345678901234567890"),
                        new BigDecimal("-40"),
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        new BigDecimal("1234567890123456789012345678901234567890"),
                        new BigDecimal("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        new OraDecimal("-40"),
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        new OraDecimal("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                // 各 Number 分支上的正常舍入
                Arguments.of(
                        "OraDecimal 指定小数位 HALF_UP",
                        new OraDecimal("1.23456"),
                        (byte) 3,
                        new OraDecimal("1.235")
                ),
                Arguments.of(
                        "OraDecimal 指定小数位 HALF_UP",
                        new OraDecimal("1.23456"),
                        3,
                        new OraDecimal("1.235")
                ),
                Arguments.of(
                        "BigDecimal 先转 OraDecimal 再 setScale",
                        new BigDecimal("2.3456"),
                        2L,
                        new OraDecimal("2.35")
                ),
                Arguments.of("Long 且 newScale>=0 时直接返回原 Long", 77L, 5, 77L),
                Arguments.of(
                        "Long 且 newScale<0 时在左侧数量级上 HALF_UP",
                        12345L,
                        new BigInteger("-1"),
                        new OraDecimal("12350")
                ),
                Arguments.of(
                        "Integer 且 newScale<0 时在左侧数量级上 HALF_UP",
                        154,
                        new BigDecimal("-2"),
                        new OraDecimal("200")
                ),
                Arguments.of(
                        "BigInteger 且 newScale>=0 时直接返回原 BigInteger",
                        new BigInteger("999"),
                        new OraDecimal("2"),
                        new BigInteger("999")
                ),
                Arguments.of(
                        "BigInteger 且 newScale<0 时转为 OraDecimal 舍入",
                        new BigInteger("12345"),
                        -1.0d,
                        new OraDecimal("12350")
                ),
                Arguments.of(
                        "其余 Number 走 OraDecimal.valueOf 分支（避免 float 二进制误差，使用字符串构造）",
                        new OraDecimal("10.56"),
                        (short) 1,
                        new OraDecimal("10.6")
                ),
                Arguments.of(
                        "newScale 为带小数的 Double 时先截断为 long 再转 int 作为 scale",
                        new OraDecimal("9.12345678901234567890123456789012345675"),
                        37.9d,
                        new OraDecimal("9.1234567890123456789012345678901234568")
                ),
                Arguments.of(
                        "newScale 等于小数位",
                        new OraDecimal("9.12345678901234567890123456789012345675"),
                        new BigInteger("38"),
                        new OraDecimal("9.12345678901234567890123456789012345675")
                ),
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分",
                        new BigDecimal("3.1445926"),
                        new BigDecimal("2.9"),
                        new OraDecimal("3.14")
                ),
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分",
                        new OraDecimal("3.1445926"),
                        new OraDecimal("2.9"),
                        new OraDecimal("3.14")
                ),
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分",
                        new OraDecimal("3.1445926"),
                        new OraDecimal("2.9"),
                        new OraDecimal("3.14")
                )
        );
    }

    @DisplayName("round(number, newScale) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: number={1}, newScale={2}, expected={3}")
    @MethodSource("testRoundTwoArgsProvider")
    public void testRoundTwoArgs(String caseId, Object number, Object newScale, Object expected) {
        Number actual = OracleFunctionUtils.round((Number) number, (Number) newScale);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * round 非法入参：{@code newScale==null} 或运行期类型不在实现支持范围内。
     * <p>后者无法通过 Java 源码直接传入非 {@link Number}，故用反射调用模拟字节码层面的实参类型。
     */
    static Stream<Arguments> testRoundTwoArgsInvalidProvider() {
        //noinspection DataFlowIssue
        return Stream.of(
                Arguments.of("newScale 为 null", (Executable) () -> OracleFunctionUtils.round(1.0, null)),
                Arguments.of(
                        "newScale 运行期类型非支持的 Number 子类型",
                        (Executable) () -> invokeRoundReflectSecondArg(new OraDecimal("1"), Instant.parse("2020-02-01T03:36:19Z"))
                )
        );
    }

    @DisplayName("round(number, newScale) 非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testRoundTwoArgsInvalidProvider")
    public void testRoundTwoArgsInvalid(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }

    /**
     * 通过反射调用 {@link OracleFunctionUtils#round(Number, Number)}，第二形参在运行期可为任意对象，
     * 用于模拟调用方以非 Number 实参调用时实现内的类型校验分支。
     *
     * @param number          第一个实参
     * @param newScaleRuntime 第二个实参的运行期类型可自由指定
     */
    private static void invokeRoundReflectSecondArg(Number number, Object newScaleRuntime) {
        try {
            Method m = OracleFunctionUtils.class.getMethod("round", Number.class, Number.class);
            //noinspection JavaReflectionInvocation
            m.invoke(null, number, newScaleRuntime);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(cause);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
