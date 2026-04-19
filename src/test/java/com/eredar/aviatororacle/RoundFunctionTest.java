package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.constants.AviatorOracleConstants;
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
 * 测试：模拟Oracle数据库 {@code round()} 方法
 */
@DisplayName("round 方法测试")
public class RoundFunctionTest {

    /**
     * 单参数 {@code round(n)}：等价于 {@code round(n, 0)}。
     * <p>第三列期望为 {@code null} 表示表达式结果为 {@code null}。
     */
    static Stream<Arguments> testRoundOneArgProvider() {
        return Stream.of(
                Arguments.of(
                        "入参为 null",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", null).build(),
                        null
                ),
                Arguments.of(
                        "Long 已为整数且 scale=0 时直接返回原装箱对象",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", 42L).build(),
                        42L
                ),
                Arguments.of(
                        "Integer 已为整数且 scale=0 时直接返回原装箱对象",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", 7).build(),
                        7L
                ),
                Arguments.of(
                        "Short 已为整数且 scale=0 时直接返回原装箱对象",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", (short) 3).build(),
                        3L
                ),
                Arguments.of(
                        "Byte 已为整数且 scale=0 时直接返回原装箱对象",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", (byte) 9).build(),
                        9L
                ),
                Arguments.of(
                        "Double 按 HALF_UP 四舍五入到整数",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", 2.5d).build(),
                        new OraDecimal("3")
                ),
                Arguments.of(
                        "BigDecimal 按 HALF_UP 四舍五入到整数",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("3.4159")).build(),
                        new OraDecimal("3")
                ),
                Arguments.of(
                        "OraDecimal 按 HALF_UP 四舍五入到整数",
                        "round(a)",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("9.576")).build(),
                        new OraDecimal("10")
                )
        );
    }

    @DisplayName("round(number) 表达式测试（等价于 newScale=0）")
    @ParameterizedTest(name = "【{index}】{0}: expr={1}, vars={2}")
    @MethodSource("testRoundOneArgProvider")
    public void testRoundOneArg(String caseId, String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    /**
     * 双参数 {@code round(number, newScale)}：与 {@link com.eredar.aviatororacle.runtime.uitls.OracleFunctionUtils#round(Number, Number)} 一致。
     */
    static Stream<Arguments> testRoundTwoArgsProvider() {
        return Stream.of(
                Arguments.of(
                        "number 为 null 时返回 null",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", null).put("b", 2).build(),
                        null
                ),
                Arguments.of(
                        "primitive newScale>=40 时原样返回 Long",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 999L).put("b", 40).build(),
                        999L
                ),
                Arguments.of(
                        "Double 形式的 newScale>=40 时原样返回 Double",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 12.3d).put("b", 40.0d).build(),
                        new OraDecimal("12.3")
                ),
                Arguments.of(
                        "Integer 入参且 OraDecimal newScale>=40 时原样返回",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 1).put("b", new OraDecimal("40")).build(),
                        1L
                ),
                Arguments.of(
                        "BigInteger 入参且 BigInteger newScale>=40 时原样返回 BigInteger 入参",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))
                                .put("b", new BigInteger("40"))
                                .build(),
                        new BigInteger("5")
                ),
                Arguments.of(
                        "OraDecimal 入参与 newScale>=40 时原样返回（见 testRoundTwoArgs 的 assertSame 分支）",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("2.71"))
                                .put("b", new OraDecimal("40"))
                                .build(),
                        new OraDecimal("2.71")
                ),
                Arguments.of(
                        "Long newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", Long.MAX_VALUE).put("b", -40).build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", -40)
                                .build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", -39)
                                .build(),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", -40L)
                                .build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", -39L)
                                .build(),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("1234567890123456789012345678901234567890"))
                                .put("b", new BigInteger("-40"))
                                .build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("1234567890123456789012345678901234567890"))
                                .put("b", new BigInteger("-39"))
                                .build(),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", -40.9d)
                                .build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", -39.9d)
                                .build(),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("1234567890123456789012345678901234567890"))
                                .put("b", new BigDecimal("-40"))
                                .build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("1234567890123456789012345678901234567890"))
                                .put("b", new BigDecimal("-39"))
                                .build(),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", new OraDecimal("-40"))
                                .build(),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1234567890123456789012345678901234567890"))
                                .put("b", new OraDecimal("-39"))
                                .build(),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal 指定小数位 HALF_UP",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1.23456"))
                                .put("b", (byte) 3)
                                .build(),
                        new OraDecimal("1.235")
                ),
                Arguments.of(
                        "OraDecimal 指定小数位 HALF_UP",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1.23456"))
                                .put("b", 3)
                                .build(),
                        new OraDecimal("1.235")
                ),
                Arguments.of(
                        "BigDecimal 先转 OraDecimal 再 setScale",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("2.3456"))
                                .put("b", 2L)
                                .build(),
                        new OraDecimal("2.35")
                ),
                Arguments.of(
                        "Long 且 newScale>=0 时直接返回原 Long",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 77L).put("b", 5).build(),
                        77L
                ),
                Arguments.of(
                        "Long 且 newScale<0 时在左侧数量级上 HALF_UP",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 12345L)
                                .put("b", new BigInteger("-1"))
                                .build(),
                        new OraDecimal("12350")
                ),
                Arguments.of(
                        "Integer 且 newScale<0 时在左侧数量级上 HALF_UP",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 154)
                                .put("b", new BigDecimal("-2"))
                                .build(),
                        new OraDecimal("200")
                ),
                Arguments.of(
                        "BigInteger 且 newScale>=0 时直接返回原 BigInteger",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("999"))
                                .put("b", new OraDecimal("2"))
                                .build(),
                        new BigInteger("999")
                ),
                Arguments.of(
                        "BigInteger 且 newScale<0 时转为 OraDecimal 舍入",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("12345"))
                                .put("b", -1.0d)
                                .build(),
                        new OraDecimal("12350")
                ),
                Arguments.of(
                        "其余 Number 走 OraDecimal.valueOf 分支（避免 float 二进制误差，使用字符串构造）",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("10.56"))
                                .put("b", (short) 1)
                                .build(),
                        new OraDecimal("10.6")
                ),
                Arguments.of(
                        "newScale 为带小数的 Double 时先截断为 long 再转 int 作为 scale",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("9.12345678901234567890123456789012345675"))
                                .put("b", 37.9d)
                                .build(),
                        new OraDecimal("9.1234567890123456789012345678901234568")
                ),
                Arguments.of(
                        "newScale 等于小数位",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("9.12345678901234567890123456789012345675"))
                                .put("b", new BigInteger("38"))
                                .build(),
                        new OraDecimal("9.12345678901234567890123456789012345675")
                ),
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("3.1445926"))
                                .put("b", new BigDecimal("2.9"))
                                .build(),
                        new OraDecimal("3.14")
                ),
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("3.1445926"))
                                .put("b", new OraDecimal("2.9"))
                                .build(),
                        new OraDecimal("3.14")
                ),
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("3.1445926"))
                                .put("b", new OraDecimal("2.9"))
                                .build(),
                        new OraDecimal("3.14")
                )
        );
    }

    @DisplayName("round(number, newScale) 表达式测试")
    @ParameterizedTest(name = "【{index}】{0}: expr={1}, vars={2}")
    @MethodSource("testRoundTwoArgsProvider")
    public void testRoundTwoArgs(String caseId, String expression, Map<String, Object> vars, Object expected) {
        Object number = vars.get("a");
        Object newScale = vars.get("b");
        if (number instanceof OraDecimal && newScale instanceof OraDecimal
                && ((OraDecimal) newScale).compareTo(AviatorOracleConstants.ROUND_SCALE__ORA_DECIMAL_POS) >= 0) {
            // newScale>=40 时工具方法直接返回入参 number 的引用；表达式路径须锁定同一引用契约
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertSame(number, actual);
        } else if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    /**
     * 非法入参：与工具类测试语义一致——{@code newScale==null} 或非 {@link Number} 的第二实参。
     * <p>此处通过表达式变量传入 {@code null} / {@link Instant}，覆盖 {@link com.eredar.aviatororacle.runtime.function.orafunc.RoundFunction} 的运行期校验。
     */
    static Stream<Arguments> testRoundTwoArgsInvalidProvider() {
        return Stream.of(
                Arguments.of(
                        "newScale 为 null",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder().put("a", 1.0).put("b", null).build(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        "newScale 运行期类型非支持的 Number 子类型",
                        "round(a, b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1"))
                                .put("b", Instant.parse("2020-02-01T03:36:19Z"))
                                .build(),
                        IllegalArgumentException.class
                )
        );
    }

    @DisplayName("round(number, newScale) 非法入参（表达式）")
    @ParameterizedTest(name = "【{index}】{0}: expr={1}, vars={2}")
    @MethodSource("testRoundTwoArgsInvalidProvider")
    public void testRoundTwoArgsInvalid(String caseId, String expression, Map<String, Object> vars, Object expected) {
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
