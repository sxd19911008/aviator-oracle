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
                        "complex_expression_1.av",
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
                        "complex_expression_1.av",
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
                        "complex_expression_1.av",
                        HashMapBuilder.<String, Object>builder()
                                .put("startDate", Instant.parse("2024-02-28T08:00:00Z"))
                                .put("endDate", Instant.parse("2024-03-01T14:30:00Z"))
                                .put("category", "OTHER")
                                .put("baseValue", new OraDecimal("12500.885923"))
                                .put("ratioLimit", new BigDecimal("85.50"))
                                .put("extraOffset", 2.75)
                                .build(),
                        new OraDecimal("12503.3838396666666666666666666666666667")
                ),

                // ── complex_expression_2.av ──────────────────────────────────────
                // productType=BOND
                Arguments.of(
                        "complex_expression_2.av",
                        HashMapBuilder.<String, Object>builder()
                                .put("tradeDate", Instant.parse("2024-01-15T02:30:00Z"))
                                .put("settleDate", Instant.parse("2024-04-20T08:45:00Z"))
                                .put("productType", "BOND")
                                .put("faceValue", new OraDecimal("100000.567"))
                                .put("adjustFactor", -2.35)
                                .put("discountRate", null)
                                .put("backupRate", null)
                                .build(),
                        new OraDecimal("72711.69")
                ),

                // productType=STOCK
                Arguments.of(
                        "complex_expression_2.av",
                        HashMapBuilder.<String, Object>builder()
                                .put("tradeDate", Instant.parse("2024-01-15T02:30:00Z"))
                                .put("settleDate", Instant.parse("2024-04-20T08:45:00Z"))
                                .put("productType", "STOCK")
                                .put("faceValue", new OraDecimal("100000.567"))
                                .put("adjustFactor", -2.35)
                                .put("discountRate", null)
                                .put("backupRate", null)
                                .build(),
                        new OraDecimal("72738.69")
                ),

                // productType=FUND
                Arguments.of(
                        "complex_expression_2.av",
                        HashMapBuilder.<String, Object>builder()
                                .put("tradeDate", Instant.parse("2024-01-15T02:30:00Z"))
                                .put("settleDate", Instant.parse("2024-04-20T08:45:00Z"))
                                .put("productType", "FUND")
                                .put("faceValue", new OraDecimal("100000.567"))
                                .put("adjustFactor", -2.35)
                                .put("discountRate", null)
                                .put("backupRate", null)
                                .build(),
                        new OraDecimal("72695.69")
                )
        );
    }

    @DisplayName("复杂表达式测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testComplexExpressionProvider")
    public void testComplexExpression(String fileName, Map<String, Object> vars, Object expected) {
        String expression = FileUtils.readFileAsString(fileName);
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
