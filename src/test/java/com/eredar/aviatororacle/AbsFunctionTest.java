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
 * 测试：模拟Oracle数据库 {@code abs()} 方法。
 */
@DisplayName("abs 方法测试")
public class AbsFunctionTest {

    /** 所有案例使用同一表达式；第 2 个参数 0 为占位符，会被 AbsFunction 忽略 */
    private static final String EXPR = "abs(a, 0)";

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
                Arguments.of("Integer 正数直接返回原对象", 2147483647, 2147483647L),
                // Oracle: ABS(-2147483647) = 2147483647（-val 表达式类型为 long，返回 Long 而非 Integer）
                Arguments.of("Integer 负数取反返回 Long", -2147483647, 2147483647L),
                // Oracle: ABS(5) = 5
                Arguments.of("Short 正数直接返回原对象", (short) 5, 5L),
                // Oracle: ABS(-5) = 5（返回 Long）
                Arguments.of("Short 负数取反返回 Long", (short) -5, 5L),
                // Oracle: ABS(9) = 9
                Arguments.of("Byte 正数直接返回原对象", (byte) 9, 9L),
                // Oracle: ABS(-9) = 9（返回 Long）
                Arguments.of("Byte 负数取反返回 Long", (byte) -9, 9L),
                // Oracle: ABS(99999999999999999999999999999) = 99999999999999999999999999999
                Arguments.of("BigInteger 正数直接返回原对象",
                        new BigInteger("99999999999999999999999999999"),
                        new BigInteger("99999999999999999999999999999")),
                // Oracle: ABS(-99999999999999999999999999999) = 99999999999999999999999999999
                Arguments.of("BigInteger 负数返回 BigInteger.abs()",
                        new BigInteger("-99999999999999999999999999999"),
                        new BigInteger("99999999999999999999999999999")),
                // Oracle: ABS(0) = 0（BigInteger 零值 signum=0，直接返回原对象）
                Arguments.of("BigInteger 零值直接返回原对象", BigInteger.ZERO, BigInteger.ZERO),
                // Oracle: ABS(3.14) = 3.14（Double 正数经 OraDecimal.valueOf 转换后返回）
                Arguments.of("Double 正数转 OraDecimal 返回", 3.14d, new OraDecimal("3.14")),
                // Oracle: ABS(-3.14) = 3.14（Double 负数取 abs 后返回 OraDecimal）
                Arguments.of("Double 负数取 OraDecimal.abs() 返回", -3.14d, new OraDecimal("3.14")),
                // Oracle: ABS(1.2345678901234567890123456789) = 1.2345678901234567890123456789
                Arguments.of("BigDecimal 高精度正数转 OraDecimal 返回",
                        new BigDecimal("1.2345678901234567890123456789"),
                        new OraDecimal("1.2345678901234567890123456789")),
                // Oracle: ABS(-1.2345678901234567890123456789) = 1.2345678901234567890123456789
                Arguments.of("BigDecimal 高精度负数取 OraDecimal.abs() 返回",
                        new BigDecimal("-1.2345678901234567890123456789"),
                        new OraDecimal("1.2345678901234567890123456789")),
                // Oracle: ABS(9.999999999999999999999999999999999999) = 9.999999999999999999999999999999999999
                Arguments.of("OraDecimal 高精度正数直接返回 OraDecimal",
                        new OraDecimal("9.999999999999999999999999999999999999"),
                        new OraDecimal("9.999999999999999999999999999999999999")),
                // Oracle: ABS(-9.999999999999999999999999999999999999) = 9.999999999999999999999999999999999999
                Arguments.of("OraDecimal 高精度负数取 OraDecimal.abs() 返回",
                        new OraDecimal("-9.999999999999999999999999999999999999"),
                        new OraDecimal("9.999999999999999999999999999999999999"))
        );
    }

    @DisplayName("abs 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: a={1}, expected={2}")
    @MethodSource("testAbsProvider")
    public void testAbs(String caseId, Object input, Object expected) {
        Map<String, Object> vars = HashMapBuilder.<String, Object>builder()
                .put("a", input)
                .build();
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(EXPR, vars));
        } else {
            Object actual = AviatorInstance.execute(EXPR, vars);
            Assertions.assertEquals(expected, actual);
        }
    }
}
