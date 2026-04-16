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
 * AviatorOracle 一元取负运算单元测试，对应 {@code OperatorType.NEG}（即一元 {@code -} 号）。
 */
@DisplayName("AviatorOracle 一元取负测试")
public class AviatorNegTest {

    private final AviatorEvaluatorInstance aviator = AviatorOracleBuilder.builder().build();

    static Stream<Arguments> testNegProvider() {
        return Stream.of(
                // Long：取负仍为 Long
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).build(),
                        -2L
                ),
                // Integer：作为 Number 参与运算，Aviator 内部按 Long 存储，结果为 Long
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", 2).build(),
                        -2L
                ),
                // BigInteger：取负为 BigInteger
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).build(),
                        new BigInteger("-2")
                ),
                // Double：经 OraDecimal 规则取负
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 0.4836065573770491803278688524590163934426)
                                .build(),
                        new OraDecimal("-0.48360655737704916")
                ),
                // BigDecimal：取负为 OraDecimal
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("-0.4836065573770491803278688524590163934426")
                ),
                // OraDecimal：取负仍为 OraDecimal
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", new OraDecimal("0.4836065573770491803278688524590163934426")).build(),
                        new OraDecimal("-0.4836065573770491803278688524590163934426")
                ),
                // String：非数值，无法取负，抛出 ExpressionRuntimeException
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", "2").build(),
                        ExpressionRuntimeException.class
                ),
                // Instant：非数值，无法取负
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", Instant.parse("2020-02-01T03:36:19Z"))
                                .build(),
                        ExpressionRuntimeException.class
                ),
                // Boolean：非数值，无法取负
                Arguments.of(
                        "-a",
                        HashMapBuilder.<String, Object>builder().put("a", true).build(),
                        ExpressionRuntimeException.class
                )
        );
    }

    @DisplayName("testNeg")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testNegProvider")
    public void testNeg(String expression, Map<String, Object> vars, Object excepted) {
        if (excepted instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) excepted;
            Assertions.assertThrows(exceptionClass, () -> aviator.execute(expression, vars));
        } else {
            Object actual = aviator.execute(expression, vars);
            Assertions.assertEquals(excepted, actual);
        }
    }
}
