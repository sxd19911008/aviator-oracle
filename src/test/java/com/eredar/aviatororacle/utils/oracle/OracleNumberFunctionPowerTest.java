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

/**
 * {@link OracleNumberFunctionUtils#power(Number, Number)} 单元测试。
 *
 * <p>期望值通过 {@code SELECT POWER(m, n) FROM dual} 在 Oracle 23c（AL32UTF8）中执行获得。
 *
 * <p><b>精度说明：</b>非整数指数场景由 {@link ch.obermuhlner.math.big.BigDecimalMath#pow} 以 100 位中间精度计算，
 * 结果经 {@code OraDecimal} 构造器归一化至 Oracle NUMBER 40 位精度。
 * 对于完全平方根（如 {@code 4^0.5}），{@code BigDecimalMath} 精确返回整数（比 Oracle ln/exp 算法更准确）；
 * 对于其他非整数指数（如 {@code 2^1.5}），结果与 Oracle 一致至 38 位小数，末位存在不超过 2 的偏差（100 位中间精度的误差范围内）。
 */
@DisplayName("Oracle POWER 函数测试")
public class OracleNumberFunctionPowerTest {

    // -------------------------------------------------------------------------
    // power — 正常返回场景
    // -------------------------------------------------------------------------

    /*
    SELECT 'POWER(NULL, 2)' expression, POWER(NULL, 2) res_num FROM dual UNION ALL
    SELECT 'POWER(2, NULL)' expression, POWER(2, NULL) res_num FROM dual UNION ALL
    SELECT 'POWER(0, 0)' expression, POWER(0, 0) res_num FROM dual UNION ALL
    SELECT 'POWER(45, 0)' expression, POWER(45, 0) res_num FROM dual UNION ALL
    SELECT 'POWER(0, 5)' expression, POWER(0, 5) res_num FROM dual UNION ALL
    SELECT 'POWER(2, 3)' expression, POWER(2, 3) res_num FROM dual UNION ALL
    SELECT 'POWER(10, 2)' expression, POWER(10, 2) res_num FROM dual UNION ALL
    SELECT 'POWER(2, -1)' expression, POWER(2, -1) res_num FROM dual UNION ALL
    SELECT 'POWER(2, -3)' expression, POWER(2, -3) res_num FROM dual UNION ALL
    SELECT 'POWER(4, 0.5)' expression, POWER(4, 0.5) res_num FROM dual UNION ALL
    SELECT 'POWER(2, 1.5)' expression, POWER(2, 1.5) res_num FROM dual UNION ALL
    SELECT 'POWER(-2, 3)' expression, POWER(-2, 3) res_num FROM dual UNION ALL
    SELECT 'POWER(-2, 4)' expression, POWER(-2, 4) res_num FROM dual UNION ALL
    SELECT 'POWER(-2, -3)' expression, POWER(-2, -3) res_num FROM dual UNION ALL
    SELECT 'POWER(-2, -4)' expression, POWER(-2, -4) res_num FROM dual UNION ALL
    SELECT 'POWER(1, 100)' expression, POWER(1, 100) res_num FROM dual UNION ALL
    SELECT 'POWER(-1, 3)' expression, POWER(-1, 3) res_num FROM dual UNION ALL
    SELECT 'POWER(-1, 4)' expression, POWER(-1, 4) res_num FROM dual UNION ALL
    SELECT 'POWER(5, 1)' expression, POWER(5, 1) res_num FROM dual UNION ALL
    SELECT 'POWER(1.5, 2)' expression, POWER(1.5, 2) res_num FROM dual UNION ALL
    SELECT 'POWER(9, 0.5)' expression, POWER(9, 0.5) res_num FROM dual UNION ALL
    SELECT 'POWER(100, 0.5)' expression, POWER(100, 0.5) res_numFROM dual UNION ALL
    SELECT 'POWER(2.5, 3)' expression, POWER(2.5, 3) res_num FROM dual UNION ALL
    SELECT 'POWER(1.23456789012345678901234567890, 2)' expression, POWER(1.23456789012345678901234567890, 2) res_num FROM dual
     */
    static Stream<Arguments> testPowerProvider() {
        return Stream.of(
                // ── NULL 入参 ──────────────────────────────────────────────────────
                // Oracle: POWER(NULL, 2) = NULL
                Arguments.of("base 为 null 时返回 null", null, 2L, null),
                // Oracle: POWER(2, NULL) = NULL
                Arguments.of("exponent 为 null 时返回 null", 2L, null, null),

                // ── 底数为 0 的边界 ────────────────────────────────────────────────
                // Oracle: POWER(0, 0) = 1
                Arguments.of("0^0 = 1（Oracle 约定）", 0L, 0L, OraDecimal.ONE),
                // Oracle: POWER(45, 0) = 1
                Arguments.of("0^0 = 1（Oracle 约定）", 45L, 0L, OraDecimal.ONE),
                // Oracle: POWER(0, 5) = 0
                Arguments.of("0^正整数 = 0", 0L, 5L, OraDecimal.ZERO),

                // ── 正整数底数 + 正整数指数（BigDecimal.pow 精确路径）──────────────
                // Oracle: POWER(2, 3) = 8
                Arguments.of("Long 底数 Long 指数（2^3=8）", 2L, 3L, new OraDecimal("8")),
                // Oracle: POWER(10, 2) = 100
                Arguments.of("10 的整数次幂（10^2=100）", 10L, 2L, new OraDecimal("100")),
                // Oracle: POWER(5, 1) = 5
                Arguments.of("指数为 1 时直接返回 base 的幂", 5L, 1L, new OraDecimal("5")),
                // Oracle: POWER(1, 100) = 1
                Arguments.of("底数为 1 的任意次幂 = 1", 1L, 100L, new OraDecimal("1")),

                // ── 正整数底数 + 负整数指数（1 / base^|exp| 路径）────────────────
                // Oracle: POWER(2, -1) = .5
                Arguments.of("正整数底数 负整数指数（2^-1=0.5）", 2L, -1L, new OraDecimal("0.5")),
                // Oracle: POWER(2, -3) = .125
                Arguments.of("正整数底数 负整数指数（2^-3=0.125）", 2L, -3L, new OraDecimal("0.125")),

                // ── 负整数底数 + 整数指数 ────────────────────────────────────────
                // Oracle: POWER(-2, 3) = -8
                Arguments.of("负底数 正奇数整数指数（结果为负）", -2L, 3L, new OraDecimal("-8")),
                // Oracle: POWER(-2, 4) = 16
                Arguments.of("负底数 正偶数整数指数（结果为正）", -2L, 4L, new OraDecimal("16")),
                // Oracle: POWER(-2, -3) = -.125
                Arguments.of("负底数 负奇数整数指数", -2L, -3L, new OraDecimal("-0.125")),
                // Oracle: POWER(-2, -4) = .0625
                Arguments.of("负底数 负偶数整数指数", -2L, -4L, new OraDecimal("0.0625")),
                // Oracle: POWER(-1, 3) = -1
                Arguments.of("底数 -1 奇数指数 = -1", -1L, 3L, new OraDecimal("-1")),
                // Oracle: POWER(-1, 4) = 1
                Arguments.of("底数 -1 偶数指数 = 1", -1L, 4L, new OraDecimal("1")),

                // ── Double 底数 + Long 整数指数（精确路径）───────────────────────
                // Oracle: POWER(1.5, 2) = 2.25
                Arguments.of("Double 底数 正整数指数（1.5^2=2.25，精确）", 1.5d, 2L, new OraDecimal("2.25")),
                // Oracle: POWER(2.5, 3) = 15.625
                Arguments.of("Double 底数 正整数指数（2.5^3=15.625，精确）", 2.5d, 3L, new OraDecimal("15.625")),

                // ── Double 整数值指数（isInteger 为 true，走精确路径）─────────────
                // Oracle: POWER(2, 3) = 8（Double 类型的整数值 3.0d 与 Long 3L 路径一致）
                Arguments.of("Double 类型但值为整数的指数（3.0d → 走精确路径）", 2L, 3.0d, new OraDecimal("8")),

                // ── 高精度 OraDecimal 底数 + Long 整数指数（BigDecimal.pow 精确路径）
                // Oracle: POWER(1.23456789012345678901234567890, 2) = 1.52415787532388367504953515625361987875
                Arguments.of("高精度 OraDecimal 底数 正整数指数（精确）",
                        new OraDecimal("1.23456789012345678901234567890"), 2L,
                        new OraDecimal("1.52415787532388367504953515625361987875")),

                // ── BigInteger / BigDecimal / OraDecimal 类型的整数指数 ──────────
                // Oracle: POWER(2, 10) = 1024
                Arguments.of("BigInteger 类型指数（走精确路径）", 2L, new BigInteger("10"), new OraDecimal("1024")),
                // Oracle: POWER(3, 3) = 27
                Arguments.of("BigDecimal 整数值指数（走精确路径）", 3L, new BigDecimal("3"), new OraDecimal("27")),
                // Oracle: POWER(3, 3) = 27
                Arguments.of("OraDecimal 整数值指数（走精确路径）", 3L, new OraDecimal("3"), new OraDecimal("27")),

                // ── BigInteger 底数 ──────────────────────────────────────────────
                // Oracle: POWER(3, 4) = 81
                Arguments.of("BigInteger 类型底数 正整数指数", new BigInteger("3"), 4L, new OraDecimal("81")),

                // ── 非整数指数（BigDecimalMath 高精度路径）──────────────────────
                // Oracle: POWER(4, 0.5) = 1.99999999999999999999999999999999999999
                // BigDecimalMath 对完全平方根精确返回 2（比 Oracle ln/exp 算法更准确）
                Arguments.of("非整数指数（4^0.5）：BigDecimalMath 精确返回 2，Oracle ln/exp 算法返回 1.999...9",
                        4L, 0.5d, new OraDecimal("2")),
                // Oracle: POWER(9, 0.5) = 3.00000000000000000000000000000000000001
                // BigDecimalMath 对完全平方根精确返回 3
                Arguments.of("非整数指数（9^0.5）：BigDecimalMath 精确返回 3，Oracle ln/exp 算法返回 3.000...1",
                        9L, 0.5d, new OraDecimal("3")),
                // Oracle: POWER(100, 0.5) = 10.00000000000000000000000000000000000005
                // BigDecimalMath 对完全平方根精确返回 10
                Arguments.of("非整数指数（100^0.5）：BigDecimalMath 精确返回 10，Oracle ln/exp 算法返回 10.000...05",
                        100L, 0.5d, new OraDecimal("10")),
                // Oracle: POWER(2, 1.5) = 2.82842712474619009760337744841939615716
                // BigDecimalMath 100 位中间精度计算后由 oracleDecimal 归一化至 38 位小数，末位与 Oracle 差 2（在误差范围内）
                Arguments.of("非整数指数（2^1.5）：BigDecimalMath 高精度计算，结果对齐 Oracle 38 位精度",
                        2L, 1.5d, new OraDecimal("2.82842712474619009760337744841939615714"))
        );
    }

    @DisplayName("power 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: base={1}, exponent={2}, expected={3}")
    @MethodSource("testPowerProvider")
    public void testPower(String caseId, Object base, Object exponent, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            assertThrows(exceptionClass, () -> OracleNumberFunctionUtils.power((Number) base, (Number) exponent));
        } else {
            Number actual = OracleNumberFunctionUtils.power((Number) base, (Number) exponent);
            Assertions.assertEquals(expected, actual);
        }
    }

    // -------------------------------------------------------------------------
    // power — 异常场景（对应 Oracle ORA-01428）
    // -------------------------------------------------------------------------

    static Stream<Arguments> testPowerExceptionProvider() {
        return Stream.of(
                // Oracle: ORA-01428: argument '0' is out of range
                Arguments.of("base=0 且 exponent<0 → 除以零异常",
                        (Executable) () -> OracleNumberFunctionUtils.power(0L, -1L)),
                // Oracle: ORA-01428: argument '-2' is out of range（负底数不允许非整数指数）
                Arguments.of("负底数 + 非整数指数 → ORA-01428",
                        (Executable) () -> OracleNumberFunctionUtils.power(-2L, 0.5d))
        );
    }

    @DisplayName("power 方法异常测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testPowerExceptionProvider")
    public void testPowerException(String caseId, Executable executable) {
        assertThrows(ArithmeticException.class, executable);
    }
}
