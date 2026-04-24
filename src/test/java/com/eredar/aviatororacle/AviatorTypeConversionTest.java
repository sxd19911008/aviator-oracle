package com.eredar.aviatororacle;

import com.eredar.aviatororacle.dto.DateFormatCacheKey;
import com.eredar.aviatororacle.number.OraDecimal;
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
 * AviatorOracle数据转换Function 测试，包括：
 * <p>1. {@code AODecimalFunction}，对应 {@code decimal()}。
 * <p>2. {@code InstantToStringFunction}，对应 {@code instant_to_string()}。
 * <p>3. {@code StringToInstantFunction}，对应 {@code string_to_instant()}。
 */
@DisplayName("AviatorOracle数据转换Function 测试")
public class AviatorTypeConversionTest {

    static Stream<Arguments> testTypeConversionProvider() {
        return Stream.of(
                Arguments.of(
                        "decimal(31.57349857284537940384752204323255406344) + string_to_instant(\"2020-02-14 13:50:29\", \"yyyy-MM-dd HH:mm:ss\", \"UTC\")",
                        HashMapBuilder.<String, Object>builder().build(),
                        Instant.parse("2020-03-17T03:36:19Z")
                ),
                Arguments.of(
                        "decimal(true) < 0",
                        HashMapBuilder.<String, Object>builder().build(),
                        false
                ),
                Arguments.of( // decimal(false)
                        "0 == decimal(1 > 2)",
                        HashMapBuilder.<String, Object>builder().build(),
                        true
                ),
                Arguments.of(
                        "decimal(\"31.57349857284537940384752204323255406344\") + string_to_instant(\"2020-02-14 13:50:29\", \"yyyy-MM-dd HH:mm:ss\", \"UTC\")",
                        HashMapBuilder.<String, Object>builder().build(),
                        Instant.parse("2020-03-17T03:36:19Z")
                ),
                Arguments.of(
                        "decimal(a) + string_to_instant(\"2020-02-14 13:50:29\", \"yyyy-MM-dd HH:mm:ss\", \"UTC\")",
                        HashMapBuilder.<String, Object>builder().put("a", 31L).build(),
                        Instant.parse("2020-03-16T13:50:29Z")
                ),
                Arguments.of(
                        "decimal(a) + string_to_instant(\"2020-02-14 13:50:29\", \"yyyy-MM-dd HH:mm:ss\", \"UTC\")",
                        HashMapBuilder.<String, Object>builder().put("a", 31).build(),
                        Instant.parse("2020-03-16T13:50:29Z")
                ),
                Arguments.of(
                        "31.57349857284537940384752204323255406344 + decimal(a)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("31")).build(),
                        new OraDecimal("62.57349857284537940384752204323255406344")
                ),
                Arguments.of(
                        "decimal(a) + string_to_instant(\"2020-02-14 13:50:29\", \"yyyy-MM-dd HH:mm:ss\", \"UTC\")",
                        HashMapBuilder.<String, Object>builder().put("a", 31.57349857284537940384752204323255406344d).build(),
                        Instant.parse("2020-03-17T03:36:19Z")
                ),
                Arguments.of(
                        "decimal(a) + 2",
                        HashMapBuilder.<String, Object>builder().put("a", new BigDecimal("31")).build(),
                        new OraDecimal("33")
                ),
                Arguments.of(
                        "decimal(a) + instant_to_string(b, \"HH:mm:ss\", \"Asia/Tokyo\")",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("31"))
                                .put("b", Instant.parse("2020-03-16T13:50:29Z"))
                                .build(),
                        "3122:50:29"
                ),
                Arguments.of(
                        "decimal(a) + string_to_instant(\"2020-02-14 13:50:29\", \"yyyy-MM-dd HH:mm:ss\", \"UTC\")",
                        HashMapBuilder.<String, Object>builder().put("a", new DateFormatCacheKey("1", "2")).build(),
                        ClassCastException.class
                ),
                Arguments.of(
                        "decimal(1.4354265246 + 2)",
                        HashMapBuilder.<String, Object>builder().build(),
                        new OraDecimal("3.4354265246")
                ),
                Arguments.of(
                        "decimal(1 + 2)",
                        HashMapBuilder.<String, Object>builder().build(),
                        new OraDecimal("3")
                )
        );
    }

    @DisplayName("数据转换测试")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testTypeConversionProvider")
    public void testTypeConversion(String expression, Map<String, Object> vars, Object expected) {
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
