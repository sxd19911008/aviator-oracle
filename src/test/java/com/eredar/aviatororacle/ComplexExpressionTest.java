package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.testUtils.FileUtils;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

@DisplayName("复杂表达式测试")
public class ComplexExpressionTest {

    static Stream<Arguments> testComplexExpressionProvider() {
        return Stream.of(
                // category=STRATEGIC_PROJECT
                Arguments.of(
                        FileUtils.readFileAsString("complex_expression_1.av"),
                        HashMapBuilder.<String, Object>builder()
                                .put("startDate", Instant.parse("2024-02-28T08:00:00Z"))
                                .put("endDate", Instant.parse("2024-03-01T14:30:00Z"))
                                .put("category", "STRATEGIC_PROJECT")
                                .put("baseValue", new OraDecimal("12500.885923"))
                                .put("ratioLimit", new BigDecimal("85.50"))
                                .put("extraOffset", 2.75)
                                .build(),
                        new OraDecimal("470678.5580549724770642201834862385321108")
                ),
                // category=NORMAL
                Arguments.of(
                        FileUtils.readFileAsString("complex_expression_1.av"),
                        HashMapBuilder.<String, Object>builder()
                                .put("startDate", Instant.parse("2024-02-28T08:00:00Z"))
                                .put("endDate", Instant.parse("2024-03-01T14:30:00Z"))
                                .put("category", "NORMAL")
                                .put("baseValue", new OraDecimal("12500.885923"))
                                .put("ratioLimit", new BigDecimal("85.50"))
                                .put("extraOffset", 2.75)
                                .build(),
                        new OraDecimal("12458.135923")
                ),
                // category=OTHER
                Arguments.of(
                        FileUtils.readFileAsString("complex_expression_1.av"),
                        HashMapBuilder.<String, Object>builder()
                                .put("startDate", Instant.parse("2024-02-28T08:00:00Z"))
                                .put("endDate", Instant.parse("2024-03-01T14:30:00Z"))
                                .put("category", "OTHER")
                                .put("baseValue", new OraDecimal("12500.885923"))
                                .put("ratioLimit", new BigDecimal("85.50"))
                                .put("extraOffset", 2.75)
                                .build(),
                        new OraDecimal("12503.3838396666666666666666666666666667")
                )
        );
    }

    @DisplayName("复杂表达式测试")
    @ParameterizedTest(name = "【{index}】")
    @MethodSource("testComplexExpressionProvider")
    public void testComplexExpression(String expression, Map<String, Object> vars, Object expected) {
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
