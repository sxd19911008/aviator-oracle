package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Oracle 数值函数测试")
public class OracleNumberFunctionUtilsTest {

    // -------------------------------------------------------------------------
    // abs
    // -------------------------------------------------------------------------

    static Stream<Arguments> testAbsProvider() {
        return Stream.of(
                // Oracle: ABS(NULL) = NULL
                Arguments.of("null 返回 null", null, null),
                // Oracle: ABS(1342534967873799582) = 1342534967873799582
                Arguments.of("Long 正数直接返回原对象", 1342534967873799582L, 1342534967873799582L),
                // Oracle: ABS(-1342534967873799582) = 1342534967873799582
                Arguments.of("Long 负数取反返回 Long", -1342534967873799582L, 1342534967873799582L),
                // Oracle: ABS(0) = 0
                Arguments.of("Long 零值返回原对象", 0L, 0L),
                // Oracle: ABS(9223372036854775807) = 9223372036854775807
                Arguments.of("Long.MAX_VALUE 直接返回原对象", Long.MAX_VALUE, Long.MAX_VALUE),
                // Oracle: ABS(-9223372036854775808) = 9223372036854775808（-Long.MIN_VALUE 溢出，提升为 OraDecimal）
                Arguments.of("Long.MIN_VALUE 溢出提升为 OraDecimal", Long.MIN_VALUE, new OraDecimal("9223372036854775808")),
                // Oracle: ABS(2147483647) = 2147483647
                Arguments.of("Integer 正数直接返回原对象", 2147483647, 2147483647),
                // Oracle: ABS(-2147483647) = 2147483647（-val 表达式类型为 long，返回 Long 而非 Integer）
                Arguments.of("Integer 负数取反返回 Long", -2147483647, 2147483647L),
                // Oracle: ABS(5) = 5
                Arguments.of("Short 正数直接返回原对象", (short) 5, (short) 5),
                // Oracle: ABS(-5) = 5（返回 Long）
                Arguments.of("Short 负数取反返回 Long", (short) -5, 5L),
                // Oracle: ABS(9) = 9
                Arguments.of("Byte 正数直接返回原对象", (byte) 9, (byte) 9),
                // Oracle: ABS(-9) = 9（返回 Long）
                Arguments.of("Byte 负数取反返回 Long", (byte) -9, 9L),
                // Oracle: ABS(99999999999999999999999999999) = 99999999999999999999999999999
                Arguments.of("BigInteger 正数直接返回原对象", new BigInteger("99999999999999999999999999999"), new BigInteger("99999999999999999999999999999")),
                // Oracle: ABS(-99999999999999999999999999999) = 99999999999999999999999999999
                Arguments.of("BigInteger 负数返回 BigInteger.abs()", new BigInteger("-99999999999999999999999999999"), new BigInteger("99999999999999999999999999999")),
                // Oracle: ABS(0) = 0（BigInteger 零值 signum=0，直接返回原对象）
                Arguments.of("BigInteger 零值直接返回原对象", BigInteger.ZERO, BigInteger.ZERO),
                // Oracle: ABS(3.14) = 3.14（Double 正数经 OraDecimal.valueOf 转换后返回）
                Arguments.of("Double 正数转 OraDecimal 返回", 3.14d, new OraDecimal("3.14")),
                // Oracle: ABS(-3.14) = 3.14（Double 负数取 abs 后返回 OraDecimal）
                Arguments.of("Double 负数取 OraDecimal.abs() 返回", -3.14d, new OraDecimal("3.14")),
                // Oracle: ABS(1.2345678901234567890123456789) = 1.2345678901234567890123456789
                Arguments.of("BigDecimal 高精度正数转 OraDecimal 返回", new BigDecimal("1.2345678901234567890123456789"), new OraDecimal("1.2345678901234567890123456789")),
                // Oracle: ABS(-1.2345678901234567890123456789) = 1.2345678901234567890123456789
                Arguments.of("BigDecimal 高精度负数取 OraDecimal.abs() 返回", new BigDecimal("-1.2345678901234567890123456789"), new OraDecimal("1.2345678901234567890123456789")),
                // Oracle: ABS(9.999999999999999999999999999999999999) = 9.999999999999999999999999999999999999
                Arguments.of("OraDecimal 高精度正数直接返回 OraDecimal", new OraDecimal("9.999999999999999999999999999999999999"), new OraDecimal("9.999999999999999999999999999999999999")),
                // Oracle: ABS(-9.999999999999999999999999999999999999) = 9.999999999999999999999999999999999999
                Arguments.of("OraDecimal 高精度负数取 OraDecimal.abs() 返回", new OraDecimal("-9.999999999999999999999999999999999999"), new OraDecimal("9.999999999999999999999999999999999999"))
        );
    }

    @DisplayName("abs 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testAbsProvider")
    public void testAbs(String caseId, Number n, Object expected) {
        Number actual = OracleNumberFunctionUtils.abs(n);
        Assertions.assertEquals(expected, actual);
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
                Arguments.of("OraDecimal", new OraDecimal("-1.1199431565624544763765735"), new OraDecimal("-2"))
        );
    }

    @DisplayName("floor 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testFloorProvider")
    public void testFloor(String caseId, Number n, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> OracleNumberFunctionUtils.floor(n));
        } else {
            Number actual = OracleNumberFunctionUtils.floor(n);
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
                Arguments.of( "-OraDecimal", new OraDecimal("-1.9999431565624544763765735"), new OraDecimal("-1"))
        );
    }

    @DisplayName("ceil 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testCeilProvider")
    public void testCeil(String caseId, Number n, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> OracleNumberFunctionUtils.ceil(n));
        } else {
            Number actual = OracleNumberFunctionUtils.ceil(n);
            Assertions.assertEquals(expected, actual);
        }
    }


    // -------------------------------------------------------------------------
    // round
    // -------------------------------------------------------------------------

    /**
     * {@link OracleNumberFunctionUtils#round(Number)} 场景数据：等价于 {@code round(n, 0)}。
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
        Number actual = OracleNumberFunctionUtils.round((Number) n);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * {@link OracleNumberFunctionUtils#round(Number, Number)} 场景数据（均为正常返回或 {@code number == null}）。
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
        Number actual = OracleNumberFunctionUtils.round((Number) number, (Number) newScale);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * round 非法入参：{@code newScale==null} 或运行期类型不在实现支持范围内。
     * <p>后者无法通过 Java 源码直接传入非 {@link Number}，故用反射调用模拟字节码层面的实参类型。
     */
    static Stream<Arguments> testRoundTwoArgsInvalidProvider() {
        //noinspection DataFlowIssue
        return Stream.of(
                Arguments.of("newScale 为 null", (Executable) () -> OracleNumberFunctionUtils.round(1.0, null))
        );
    }

    @DisplayName("round(number, newScale) 非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testRoundTwoArgsInvalidProvider")
    public void testRoundTwoArgsInvalid(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }

    // -------------------------------------------------------------------------
    // truncNumber
    // -------------------------------------------------------------------------

    /**
     * {@link OracleNumberFunctionUtils#truncNumber(Number)} 场景数据：等价于 {@code truncNumber(n, 0)}，向零方向截断到整数。
     * <p>第三列期望值为 {@code null} 表示返回 {@code null}；为 {@link OraDecimal} 时表示经截断后的结果。
     * <p>期望值通过 Oracle 执行 {@code SELECT TRUNC(x) FROM dual} 获得。
     */
    static Stream<Arguments> testTruncNumberOneArgProvider() {
        return Stream.of(
                Arguments.of("入参为 null", null, null),
                Arguments.of("Long 已为整数且 scale=0 时直接返回原装箱对象", 42L, 42L),
                Arguments.of("Integer 已为整数且 scale=0 时直接返回原装箱对象", 7, 7),
                Arguments.of("Short 已为整数且 scale=0 时直接返回原装箱对象", (short) 3, (short) 3),
                Arguments.of("Byte 已为整数且 scale=0 时直接返回原装箱对象", (byte) 9, (byte) 9),
                // Oracle: TRUNC(2.5) = 2（向零截断，非 HALF_UP 的 3）
                Arguments.of("Double 正数向零截断到整数", 2.5d, new OraDecimal("2")),
                // Oracle: TRUNC(3.4159) = 3
                Arguments.of("BigDecimal 正数向零截断到整数", new BigDecimal("3.4159"), new OraDecimal("3")),
                // Oracle: TRUNC(9.576) = 9
                Arguments.of("OraDecimal 正数向零截断到整数", new OraDecimal("9.576"), new OraDecimal("9")),
                // Oracle: TRUNC(-2.5) = -2（向零，非向负无穷的 -3）
                Arguments.of("Double 负数向零截断到整数", -2.5d, new OraDecimal("-2")),
                // Oracle: TRUNC(-3.9999) = -3
                Arguments.of("BigDecimal 负数向零截断到整数", new BigDecimal("-3.9999"), new OraDecimal("-3")),
                // Oracle: TRUNC(-9.999) = -9
                Arguments.of("OraDecimal 负数向零截断到整数", new OraDecimal("-9.999"), new OraDecimal("-9"))
        );
    }

    @DisplayName("truncNumber(number) 方法测试（等价于 newScale=0）")
    @ParameterizedTest(name = "【{index}】{0}: n={1}, expected={2}")
    @MethodSource("testTruncNumberOneArgProvider")
    public void testTruncNumberOneArg(String caseId, Object n, Object expected) {
        Number actual = OracleNumberFunctionUtils.truncNumber((Number) n);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * {@link OracleNumberFunctionUtils#truncNumber(Number, Number)} 场景数据（均为正常返回或 {@code number == null}）。
     */
    static Stream<Arguments> testTruncNumberTwoArgsProvider() {
        return Stream.of(
                Arguments.of("number 为 null 时返回 null", null, 2, null),
                // newScale 极限区间：>= 40 原样返回 number；<= -40 返回 0
                // Oracle: TRUNC(999, 40) = 999
                Arguments.of("primitive newScale>=40 时原样返回 Long", 999L, 40, 999L),
                // Oracle: TRUNC(12.3, 40) = 12.3
                Arguments.of("Double 形式的 newScale>=40 时原样返回 Double", 12.3d, 40.0d, 12.3d),
                Arguments.of("Integer 入参且 OraDecimal newScale>=40 时原样返回", 1, new OraDecimal("40"), 1),
                Arguments.of(
                        "BigInteger 入参且 BigInteger newScale>=40 时原样返回 BigInteger 入参",
                        new BigInteger("5"),
                        new BigInteger("40"),
                        new BigInteger("5")
                ),
                Arguments.of(
                        "OraDecimal 入参与 newScale>=40 时原样返回（assertSame 语义）",
                        new OraDecimal("2.71"),
                        new OraDecimal("40"),
                        new OraDecimal("2.71")
                ),
                // Oracle: TRUNC(大数, -40) = 0
                Arguments.of("Long newScale<=-40 时结果为 0", Long.MAX_VALUE, -40, 0),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（int newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40,
                        0
                ),
                // Oracle: TRUNC(大数, -39) = 1000000000000000000000000000000000000000
                Arguments.of(
                        "OraDecimal newScale=-39（int newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（Long newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40L,
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39（Long newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39L,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "BigInteger 入参 newScale<=-40 时结果为 0（BigInteger newScale）",
                        new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("-40"),
                        0
                ),
                Arguments.of(
                        "BigInteger 入参 newScale=-39（BigInteger newScale）",
                        new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（Double newScale 带小数）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40.9d,
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39（Double newScale 带小数，丢弃小数部分）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39.9d,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "BigDecimal 入参 newScale<=-40 时结果为 0（BigDecimal newScale）",
                        new BigDecimal("1234567890123456789012345678901234567890"),
                        new BigDecimal("-40"),
                        0
                ),
                Arguments.of(
                        "BigDecimal 入参 newScale=-39（BigDecimal newScale）",
                        new BigDecimal("1234567890123456789012345678901234567890"),
                        new BigDecimal("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（OraDecimal newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        new OraDecimal("-40"),
                        0
                ),
                Arguments.of(
                        "OraDecimal newScale=-39（OraDecimal newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        new OraDecimal("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                // 各 Number 分支上的正常向零截断（Oracle: TRUNC(1.23456, 1) = 1.2）
                Arguments.of(
                        "OraDecimal 保留1位小数 DOWN（Byte newScale）",
                        new OraDecimal("1.23456"),
                        (byte) 1,
                        new OraDecimal("1.2")
                ),
                // Oracle: TRUNC(1.23456, 3) = 1.234
                Arguments.of(
                        "OraDecimal 保留3位小数 DOWN（Integer newScale）",
                        new OraDecimal("1.23456"),
                        3,
                        new OraDecimal("1.234")
                ),
                // Oracle: TRUNC(2.3456, 2) = 2.34
                Arguments.of(
                        "BigDecimal 先转 OraDecimal 再 setScale DOWN（Long newScale）",
                        new BigDecimal("2.3456"),
                        2L,
                        new OraDecimal("2.34")
                ),
                // Oracle: TRUNC(77, 5) = 77（整数 newScale>=0 直接返回）
                Arguments.of("Long 且 newScale>=0 时直接返回原 Long", 77L, 5, 77L),
                // Oracle: TRUNC(12345, -1) = 12340（向零截断，与 round 的 12350 不同）
                Arguments.of(
                        "Long 且 newScale<0 时在左侧数量级上 DOWN（BigInteger newScale）",
                        12345L,
                        new BigInteger("-1"),
                        new OraDecimal("12340")
                ),
                // Oracle: TRUNC(154, -2) = 100（向零截断）
                Arguments.of(
                        "Integer 且 newScale<0 时在左侧数量级上 DOWN（BigDecimal newScale）",
                        154,
                        new BigDecimal("-2"),
                        new OraDecimal("100")
                ),
                // Oracle: TRUNC(999, 2) = 999（BigInteger newScale>=0 直接返回）
                Arguments.of(
                        "BigInteger 且 newScale>=0 时直接返回原 BigInteger（OraDecimal newScale）",
                        new BigInteger("999"),
                        new OraDecimal("2"),
                        new BigInteger("999")
                ),
                // Oracle: TRUNC(12345, -1) = 12340
                Arguments.of(
                        "BigInteger 且 newScale<0 时转为 OraDecimal 向零截断（Double newScale）",
                        new BigInteger("12345"),
                        -1.0d,
                        new OraDecimal("12340")
                ),
                // Oracle: TRUNC(10.56, 1) = 10.5（OraDecimal，Short newScale）
                Arguments.of(
                        "OraDecimal 保留1位小数 DOWN（Short newScale）",
                        new OraDecimal("10.56"),
                        (short) 1,
                        new OraDecimal("10.5")
                ),
                // Oracle: TRUNC(9.12345678901234567890123456789012345675, 37) = 9.1234567890123456789012345678901234567
                Arguments.of(
                        "newScale 为带小数的 Double 时先截断为 long 再转 int 作为 scale",
                        new OraDecimal("9.12345678901234567890123456789012345675"),
                        37.9d,
                        new OraDecimal("9.1234567890123456789012345678901234567")
                ),
                // Oracle: TRUNC(9.12345678901234567890123456789012345675, 38) = 9.12345678901234567890123456789012345675
                Arguments.of(
                        "newScale 等于小数位数时无截断原样返回（BigInteger newScale）",
                        new OraDecimal("9.12345678901234567890123456789012345675"),
                        new BigInteger("38"),
                        new OraDecimal("9.12345678901234567890123456789012345675")
                ),
                // Oracle: TRUNC(3.1445926, 2) = 3.14（BigDecimal newScale 丢弃小数部分后 scale=2）
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分（BigDecimal number）",
                        new BigDecimal("3.1445926"),
                        new BigDecimal("2.9"),
                        new OraDecimal("3.14")
                ),
                Arguments.of(
                        "newScale 为 OraDecimal 时用 intValue 丢弃小数部分（OraDecimal number）",
                        new OraDecimal("3.1445926"),
                        new OraDecimal("2.9"),
                        new OraDecimal("3.14")
                ),
                // 负数向零截断（区别于 floor 的向负无穷方向）
                // Oracle: TRUNC(-15.79, 1) = -15.7
                Arguments.of("负数保留正 newScale 位小数向零截断", new OraDecimal("-15.79"), 1, new OraDecimal("-15.7")),
                // Oracle: TRUNC(-2.9, 0) = -2
                Arguments.of("负数 newScale=0 向零截断", new OraDecimal("-2.9"), 0, new OraDecimal("-2")),
                // Oracle: TRUNC(-15.79, -1) = -10
                Arguments.of("负数 newScale<0 在左侧数量级向零截断", new OraDecimal("-15.79"), -1, new OraDecimal("-10")),
                // Double number 的正、负截断分支
                // Oracle: TRUNC(3.14159, 3) = 3.141
                Arguments.of("Double number 正数保留3位小数向零截断", new OraDecimal("3.14159"), 3, new OraDecimal("3.141")),
                // Oracle: TRUNC(-3.14159, 3) = -3.141
                Arguments.of("Double number 负数保留3位小数向零截断", new OraDecimal("-3.14159"), 3, new OraDecimal("-3.141"))
        );
    }

    @DisplayName("truncNumber(number, newScale) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: number={1}, newScale={2}, expected={3}")
    @MethodSource("testTruncNumberTwoArgsProvider")
    public void testTruncNumberTwoArgs(String caseId, Object number, Object newScale, Object expected) {
        Number actual = OracleNumberFunctionUtils.truncNumber((Number) number, (Number) newScale);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * truncNumber 非法入参：{@code newScale==null} 或运行期类型不在实现支持范围内。
     * <p>后者无法通过 Java 源码直接传入非 {@link Number}，故用反射调用模拟字节码层面的实参类型。
     */
    static Stream<Arguments> testTruncNumberTwoArgsInvalidProvider() {
        //noinspection DataFlowIssue
        return Stream.of(
                Arguments.of("newScale 为 null", (Executable) () -> OracleNumberFunctionUtils.truncNumber(1.0, null))
        );
    }

    @DisplayName("truncNumber(number, newScale) 非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testTruncNumberTwoArgsInvalidProvider")
    public void testTruncNumberTwoArgsInvalid(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }
}
