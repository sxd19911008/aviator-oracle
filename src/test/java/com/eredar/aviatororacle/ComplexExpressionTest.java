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
import java.time.LocalDateTime;
import java.util.Date;
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
                ),

                // ── complex_expression_3.av ──────────────────────────────────────
                // 业务场景：投资凭证提前到期偿付金额计算
                // 发行日 2023-04-01 00:00:00 CST / 到期日 2025-10-01 00:00:00 CST
                // months_between = 30（整月），days_gap = 944，yearParity = 1（奇数年 2023）
                // 公式：round(trunc(abs(-3.25),1)×200000.256÷30, 2) + ceil(944×coef) + floor(4.5×30+1)
                //       = 21333.36 + ceil(...) + 136
                //
                // 案例A：dateType=INSTANT，gradeType=PRIME → ceil(944×1.30)=1228 → 22697.36
                Arguments.of(
                        "complex_expression_3.av",
                        HashMapBuilder.<String, Object>builder()
                                // issueDate = 2023-04-01 00:00:00 CST
                                .put("issueDate", Instant.parse("2023-03-31T16:00:00Z"))
                                // maturityDate = 2025-10-01 00:00:00 CST
                                .put("maturityDate", Instant.parse("2025-09-30T16:00:00Z"))
                                .put("gradeType", "PRIME")
                                .put("principal", new OraDecimal("200000.256"))
                                .put("couponRate", null)
                                .put("discountFactor", null)
                                .put("penaltyBasis", new OraDecimal("-3.25"))
                                .put("dateType", "INSTANT")
                                .build(),
                        new OraDecimal("22697.36")
                ),

                // 案例B：dateType=LOCAL，gradeType=STANDARD → ceil(944×1.15)=1086 → 22555.36
                Arguments.of(
                        "complex_expression_3.av",
                        HashMapBuilder.<String, Object>builder()
                                // issueDate = 2023-04-01 00:00:00（LocalDateTime 无时区）
                                .put("issueDate", LocalDateTime.of(2023, 4, 1, 0, 0, 0))
                                // maturityDate = 2025-10-01 00:00:00
                                .put("maturityDate", LocalDateTime.of(2025, 10, 1, 0, 0, 0))
                                .put("gradeType", "STANDARD")
                                .put("principal", new OraDecimal("200000.256"))
                                .put("couponRate", null)
                                .put("discountFactor", null)
                                .put("penaltyBasis", new OraDecimal("-3.25"))
                                .put("dateType", "LOCAL")
                                .build(),
                        new OraDecimal("22555.36")
                ),

                // 案例C：dateType=DATE，gradeType=BASIC → ceil(944×1.05)=992 → 22461.36
                // java.util.Date 使用 Aviator 内置 date_to_string / string_to_date 进行字符串互转
                // Calendar 在系统时区（Asia/Shanghai）下与 Instant 等效
                Arguments.of(
                        "complex_expression_3.av",
                        HashMapBuilder.<String, Object>builder()
                                // issueDate = 2023-04-01 00:00:00 CST（与 Instant 案例等效）
                                .put("issueDate", Date.from(Instant.parse("2023-03-31T16:00:00Z")))
                                // maturityDate = 2025-10-01 00:00:00 CST
                                .put("maturityDate", Date.from(Instant.parse("2025-09-30T16:00:00Z")))
                                .put("gradeType", "BASIC")
                                .put("principal", new OraDecimal("200000.256"))
                                .put("couponRate", null)
                                .put("discountFactor", null)
                                .put("penaltyBasis", new OraDecimal("-3.25"))
                                .put("dateType", "DATE")
                                .build(),
                        new OraDecimal("22461.36")
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
