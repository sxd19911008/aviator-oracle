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
import java.util.Map;
import java.util.stream.Stream;

/**
 * AviatorOracle 位运算单元测试，覆盖 {@code &}（与）、{@code |}（或）、{@code ^}（异或）。
 *
 * <p><b>类型支持规则说明：</b></p>
 * <ul>
 *   <li>{@code BigInteger} 作为左操作数时，右操作数可以是任意 Number 类型
 *       （内部通过 {@code toBigInt()} 转换，不做类型检查），结果为 {@code BigInteger}。</li>
 *   <li>{@code Long} / {@code Integer} 作为左操作数时，右操作数只能是 {@code Long} 或
 *       {@code Integer}（内部 {@code ensureLong} 检查），否则抛出异常。</li>
 *   <li>{@code Double} / {@code BigDecimal} / {@code OraDecimal} 作为左操作数时不支持
 *       位运算，直接抛出异常。</li>
 * </ul>
 *
 * <p><b>表达式设计策略：</b>将 {@code BigInteger} 置于表达式最左侧，确保链式运算中后续
 * 所有类型（Long、Integer、Double、BigDecimal、OraDecimal）均以右操作数的身份参与运算，
 * 从而在单个表达式内覆盖全部六种类型。</p>
 */
@DisplayName("AviatorOracle 位运算（与 & / 或 | / 异或 ^）测试")
public class AviatorBitwiseTest {

    // ========================= 与运算 (&) =========================

    /**
     * 与运算（&）测试数据。
     *
     * <p>表达式：{@code a & b & c & d & e & f}，左结合求值：</p>
     * <pre>
     *   a = BigInteger("15") = 0b1111
     *   b = Long(7L)         = 0b0111
     *   c = Integer(3)       = 0b0011
     *   d = Double(5.0)      → toBigInt() = 5 = 0b0101
     *   e = BigDecimal("13") → toBigInt() = 13 = 0b1101
     *   f = OraDecimal("9")  → toBigInt() = 9  = 0b1001
     *
     *   15 & 7  = 7   (0b1111 & 0b0111 = 0b0111)
     *   7  & 3  = 3   (0b0111 & 0b0011 = 0b0011)
     *   3  & 5  = 1   (0b0011 & 0b0101 = 0b0001)
     *   1  & 13 = 1   (0b0001 & 0b1101 = 0b0001)
     *   1  & 9  = 1   (0b0001 & 0b1001 = 0b0001)
     *
     *   期望结果：BigInteger("1")
     * </pre>
     */
    static Stream<Arguments> testBitAndProvider() {
        return Stream.of(
                Arguments.of(
                        "a & b & c & d & e & f",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("15"))  // BigInteger，作为首个左操作数，兼容所有右操作数类型
                                .put("b", 7L)                    // Long
                                .put("c", 3)                     // Integer
                                .put("d", 5.0)                   // Double，toBigInt() 截断为整数 5
                                .put("e", new BigDecimal("13"))  // BigDecimal，toBigInt() 截断为整数 13
                                .put("f", new OraDecimal("9"))   // OraDecimal，toBigInt() 截断为整数 9
                                .build(),
                        new BigInteger("1")
                )
        );
    }

    @DisplayName("与运算 (&)")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBitAndProvider")
    public void testBitAnd(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= 或运算 (|) =========================

    /**
     * 或运算（|）测试数据。
     *
     * <p>表达式：{@code a | b | c | d | e | f}，左结合求值：</p>
     * <pre>
     *   a = BigInteger("8")  = 0b00_1000
     *   b = Long(4L)         = 0b00_0100
     *   c = Integer(2)       = 0b00_0010
     *   d = Double(1.0)      → toBigInt() = 1  = 0b00_0001
     *   e = BigDecimal("16") → toBigInt() = 16 = 0b01_0000
     *   f = OraDecimal("32") → toBigInt() = 32 = 0b10_0000
     *
     *   8  | 4  = 12  (0b001100)
     *   12 | 2  = 14  (0b001110)
     *   14 | 1  = 15  (0b001111)
     *   15 | 16 = 31  (0b011111)
     *   31 | 32 = 63  (0b111111)
     *
     *   期望结果：BigInteger("63")
     * </pre>
     */
    static Stream<Arguments> testBitOrProvider() {
        return Stream.of(
                Arguments.of(
                        "a | b | c | d | e | f",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("8"))   // BigInteger，作为首个左操作数
                                .put("b", 4L)                        // Long
                                .put("c", 2)                         // Integer
                                .put("d", 1.0)                       // Double，toBigInt() = 1
                                .put("e", new BigDecimal("16"))  // BigDecimal，toBigInt() = 16
                                .put("f", new OraDecimal("32"))  // OraDecimal，toBigInt() = 32
                                .build(),
                        new BigInteger("63")
                )
        );
    }

    @DisplayName("或运算 (|)")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBitOrProvider")
    public void testBitOr(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    // ========================= 异或运算 (^) =========================

    /**
     * 异或运算（^）测试数据。
     *
     * <p>表达式：{@code a ^ b ^ c ^ d ^ e ^ f}，左结合求值：</p>
     * <pre>
     *   a = BigInteger("12") = 0b1100
     *   b = Long(10L)        = 0b1010
     *   c = Integer(6)       = 0b0110
     *   d = Double(3.0)      → toBigInt() = 3  = 0b0011
     *   e = BigDecimal("5")  → toBigInt() = 5  = 0b0101
     *   f = OraDecimal("9")  → toBigInt() = 9  = 0b1001
     *
     *   12 ^ 10 = 6   (0b1100 ^ 0b1010 = 0b0110)
     *   6  ^ 6  = 0   (0b0110 ^ 0b0110 = 0b0000)
     *   0  ^ 3  = 3   (0b0000 ^ 0b0011 = 0b0011)
     *   3  ^ 5  = 6   (0b0011 ^ 0b0101 = 0b0110)
     *   6  ^ 9  = 15  (0b0110 ^ 0b1001 = 0b1111)
     *
     *   期望结果：BigInteger("15")
     * </pre>
     */
    static Stream<Arguments> testBitXorProvider() {
        return Stream.of(
                Arguments.of(
                        "a ^ b ^ c ^ d ^ e ^ f",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("12"))  // BigInteger，作为首个左操作数
                                .put("b", 10L)                   // Long
                                .put("c", 6)                     // Integer
                                .put("d", 3.0)                   // Double，toBigInt() = 3
                                .put("e", new BigDecimal("5"))   // BigDecimal，toBigInt() = 5
                                .put("f", new OraDecimal("9"))   // OraDecimal，toBigInt() = 9
                                .build(),
                        new BigInteger("15")
                )
        );
    }

    @DisplayName("异或运算 (^)")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBitXorProvider")
    public void testBitXor(String expression, Map<String, Object> vars, Object expected) {
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
