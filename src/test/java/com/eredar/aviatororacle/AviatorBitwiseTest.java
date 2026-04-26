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
 * AviatorOracle 位运算单元测试
 */
@DisplayName("AviatorOracle 位运算（与 & / 或 | / 异或 ^ / ~ 非）测试")
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
                // 正常用例：BigInteger & Long & Integer & Double & BigDecimal & OraDecimal 混合与运算
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
                ),
                // 正常用例：BigInteger & Double —— Double 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) & 3.9(截断为3, 0b011) = 1(0b001)
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，左操作数
                                .put("b", 3.9)                   // Double，toBigInt() 截断为整数 3
                                .build(),
                        new BigInteger("1")
                ),
                // 正常用例：BigInteger & BigDecimal —— BigDecimal 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) & 3.7(截断为3, 0b011) = 1(0b001)
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))       // BigInteger，左操作数
                                .put("b", new BigDecimal("3.7"))     // BigDecimal，toBigInt() 截断为整数 3
                                .build(),
                        new BigInteger("1")
                ),
                // 正常用例：BigInteger & OraDecimal —— OraDecimal 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) & 3.7(截断为3, 0b011) = 1(0b001)
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))       // BigInteger，左操作数
                                .put("b", new OraDecimal("3.7"))     // OraDecimal，toBigInt() 截断为整数 3
                                .build(),
                        new BigInteger("1")
                ),
                // 报错用例：Double 作左操作数（AOAviatorDecimal 未继承 bitAnd 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 5.0)                   // Double → AOAviatorDecimal，左操作数不支持 &
                                .put("b", new BigInteger("3"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：BigDecimal 作左操作数（AOAviatorDecimal 未继承 bitAnd 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("5"))   // BigDecimal → AOAviatorDecimal，左操作数不支持 &
                                .put("b", new BigInteger("3"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：OraDecimal 作左操作数（AOAviatorDecimal 未继承 bitAnd 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("5"))   // OraDecimal → AOAviatorDecimal，左操作数不支持 &
                                .put("b", new BigInteger("3"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，合法左操作数
                                .put("b", true)                  // Boolean，不是数值类型，不支持 &
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，合法左操作数
                                .put("b", "hello")               // String，不是数值类型，不支持 &
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))                        // BigInteger，合法左操作数
                                .put("b", Instant.parse("2024-01-01T00:00:00Z"))      // Instant，不是数值类型，不支持 &
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", true)                  // Boolean，不是数值类型，不支持 &
                                .put("b", new BigInteger("5"))   // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", "hello")               // String，不是数值类型，不支持 &
                                .put("b", new BigInteger("5"))   // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a & b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", Instant.parse("2024-01-01T00:00:00Z"))      // Instant，不是数值类型，不支持 &
                                .put("b", new BigInteger("5"))                        // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
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
                // 正常用例：BigInteger | Long | Integer | Double | BigDecimal | OraDecimal 混合或运算
                Arguments.of(
                        "a | b | c | d | e | f",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("8"))   // BigInteger，作为首个左操作数
                                .put("b", 4L)                    // Long
                                .put("c", 2)                     // Integer
                                .put("d", 1.0)                   // Double，toBigInt() = 1
                                .put("e", new BigDecimal("16"))  // BigDecimal，toBigInt() = 16
                                .put("f", new OraDecimal("32"))  // OraDecimal，toBigInt() = 32
                                .build(),
                        new BigInteger("63")
                ),
                // 正常用例：BigInteger | Double —— Double 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) | 2.9(截断为2, 0b010) = 7(0b111)
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，左操作数
                                .put("b", 2.9)                   // Double，toBigInt() 截断为整数 2
                                .build(),
                        new BigInteger("7")
                ),
                // 正常用例：BigInteger | BigDecimal —— BigDecimal 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) | 2.8(截断为2, 0b010) = 7(0b111)
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))       // BigInteger，左操作数
                                .put("b", new BigDecimal("2.8"))     // BigDecimal，toBigInt() 截断为整数 2
                                .build(),
                        new BigInteger("7")
                ),
                // 正常用例：BigInteger | OraDecimal —— OraDecimal 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) | 2.8(截断为2, 0b010) = 7(0b111)
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))       // BigInteger，左操作数
                                .put("b", new OraDecimal("2.8"))     // OraDecimal，toBigInt() 截断为整数 2
                                .build(),
                        new BigInteger("7")
                ),
                // 报错用例：Double 作左操作数（AOAviatorDecimal 未继承 bitOr 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 5.0)                   // Double → AOAviatorDecimal，左操作数不支持 |
                                .put("b", new BigInteger("2"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：BigDecimal 作左操作数（AOAviatorDecimal 未继承 bitOr 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("5"))   // BigDecimal → AOAviatorDecimal，左操作数不支持 |
                                .put("b", new BigInteger("2"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：OraDecimal 作左操作数（AOAviatorDecimal 未继承 bitOr 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("5"))   // OraDecimal → AOAviatorDecimal，左操作数不支持 |
                                .put("b", new BigInteger("2"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，合法左操作数
                                .put("b", false)                 // Boolean，不是数值类型，不支持 |
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，合法左操作数
                                .put("b", "world")               // String，不是数值类型，不支持 |
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))                        // BigInteger，合法左操作数
                                .put("b", Instant.parse("2024-06-01T00:00:00Z"))      // Instant，不是数值类型，不支持 |
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", false)                 // Boolean，不是数值类型，不支持 |
                                .put("b", new BigInteger("5"))   // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", "world")               // String，不是数值类型，不支持 |
                                .put("b", new BigInteger("5"))   // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a | b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", Instant.parse("2024-06-01T00:00:00Z"))      // Instant，不是数值类型，不支持 |
                                .put("b", new BigInteger("5"))                        // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
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
                // 正常用例：BigInteger ^ Long ^ Integer ^ Double ^ BigDecimal ^ OraDecimal 混合异或运算
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
                ),
                // 正常用例：BigInteger ^ Double —— Double 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) ^ 3.9(截断为3, 0b011) = 6(0b110)
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，左操作数
                                .put("b", 3.9)                   // Double，toBigInt() 截断为整数 3
                                .build(),
                        new BigInteger("6")
                ),
                // 正常用例：BigInteger ^ BigDecimal —— BigDecimal 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) ^ 3.6(截断为3, 0b011) = 6(0b110)
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))       // BigInteger，左操作数
                                .put("b", new BigDecimal("3.6"))     // BigDecimal，toBigInt() 截断为整数 3
                                .build(),
                        new BigInteger("6")
                ),
                // 正常用例：BigInteger ^ OraDecimal —— OraDecimal 作右操作数时经 toBigInt() 截断小数位后参与运算
                // 5(0b101) ^ 3.6(截断为3, 0b011) = 6(0b110)
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))       // BigInteger，左操作数
                                .put("b", new OraDecimal("3.6"))     // OraDecimal，toBigInt() 截断为整数 3
                                .build(),
                        new BigInteger("6")
                ),
                // 报错用例：Double 作左操作数（AOAviatorDecimal 未继承 bitXor 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 5.0)                   // Double → AOAviatorDecimal，左操作数不支持 ^
                                .put("b", new BigInteger("3"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：BigDecimal 作左操作数（AOAviatorDecimal 未继承 bitXor 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("5"))   // BigDecimal → AOAviatorDecimal，左操作数不支持 ^
                                .put("b", new BigInteger("3"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：OraDecimal 作左操作数（AOAviatorDecimal 未继承 bitXor 实现），抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("5"))   // OraDecimal → AOAviatorDecimal，左操作数不支持 ^
                                .put("b", new BigInteger("3"))   // BigInteger，右操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，合法左操作数
                                .put("b", true)                  // Boolean，不是数值类型，不支持 ^
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，合法左操作数
                                .put("b", "foo")                 // String，不是数值类型，不支持 ^
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))                        // BigInteger，合法左操作数
                                .put("b", Instant.parse("2024-12-01T00:00:00Z"))      // Instant，不是数值类型，不支持 ^
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", true)                  // Boolean，不是数值类型，不支持 ^
                                .put("b", new BigInteger("5"))   // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", "foo")                 // String，不是数值类型，不支持 ^
                                .put("b", new BigInteger("5"))   // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不支持位运算，抛出 ExpressionRuntimeException
                Arguments.of(
                        "a ^ b",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", Instant.parse("2024-12-01T00:00:00Z"))      // Instant，不是数值类型，不支持 ^
                                .put("b", new BigInteger("5"))                        // BigInteger，合法左操作数
                                .build(),
                        ExpressionRuntimeException.class
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

    // ========================= 非运算 (~) =========================

    /**
     * 非运算（~，按位取反）测试数据。
     *
     * <p>表达式：{@code ~a}，一元前缀运算符：</p>
     * <pre>
     *   BigInteger 取反（java.math.BigInteger.not()，按二进制补码取反）：
     *     ~BigInteger("5")  → BigInteger("-6")   (0b...0101 → 0b...1010 补码 = -6)
     *
     *   Long/Integer 取反（Java 按位取反，结果仍为 long）：
     *     ~Long(5L)         → -6L                (~0b...0101 = 0b...1010 = -6)
     *     ~Integer(5)       → -6L                (Integer 视为 Long 处理)
     *
     *   报错场景（Double/BigDecimal/OraDecimal 的 AOAviatorDecimal 未实现 bitNot；
     *            String/Instant/Boolean 不是数值类型，均抛出 ExpressionRuntimeException）：
     *     ~Double(5.0)          → ExpressionRuntimeException
     *     ~BigDecimal("5")      → ExpressionRuntimeException
     *     ~OraDecimal("5")      → ExpressionRuntimeException
     *     ~String("5")          → ExpressionRuntimeException
     *     ~Instant              → ExpressionRuntimeException
     *     ~Boolean(true)        → ExpressionRuntimeException
     * </pre>
     */
    static Stream<Arguments> testBitNotProvider() {
        return Stream.of(
                // 正常用例：~BigInteger("5") → BigInteger("-6")
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("5"))   // BigInteger，not() 按补码取反
                                .build(),
                        new BigInteger("-6")
                ),
                Arguments.of(
                        "~(a + b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigInteger("2"))
                                .put("b", new BigInteger("3"))
                                .build(),
                        new BigInteger("-6")
                ),
                // 正常用例：~Long(5L) → -6L
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 5L)                    // Long，Java ~5L = -6L
                                .build(),
                        -6L
                ),
                Arguments.of(
                        "~(a + b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 2L)
                                .put("b", 3L)
                                .build(),
                        -6L
                ),
                // 正常用例：~Integer(5) → -6L（Integer 在框架中被提升为 Long 处理）
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 5)                     // Integer，被提升为 Long，~5L = -6L
                                .build(),
                        -6L
                ),
                Arguments.of(
                        "~(a + b)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 2)
                                .put("b", 3)
                                .build(),
                        -6L
                ),
                // 报错用例：Double 对应 AOAviatorDecimal，未实现 bitNot，抛出 ExpressionRuntimeException
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 5.0)                   // Double → AOAviatorDecimal，不支持 ~
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：BigDecimal 对应 AOAviatorDecimal，未实现 bitNot，抛出 ExpressionRuntimeException
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("5"))   // BigDecimal → AOAviatorDecimal，不支持 ~
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：OraDecimal 对应 AOAviatorDecimal，未实现 bitNot，抛出 ExpressionRuntimeException
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("5"))   // OraDecimal → AOAviatorDecimal，不支持 ~
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：字符串（String）不是数值类型，不支持 ~，抛出 ExpressionRuntimeException
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", "5")                   // String，不是数值，不支持 ~
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：日期类型（Instant）不是数值类型，不支持 ~，抛出 ExpressionRuntimeException
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", Instant.parse("2024-01-01T00:00:00Z"))   // Instant，不是数值，不支持 ~
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // 报错用例：布尔型（Boolean）不是数值类型，不支持 ~，抛出 ExpressionRuntimeException
                Arguments.of(
                        "~a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", true)                  // Boolean，不是数值，不支持 ~
                                .build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("非运算 (~)")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testBitNotProvider")
    public void testBitNot(String expression, Map<String, Object> vars, Object expected) {
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
