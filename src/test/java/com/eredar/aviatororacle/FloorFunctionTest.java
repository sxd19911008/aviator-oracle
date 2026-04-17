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
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 测试：模拟Oracle数据库 {@code floor()} 方法
 */
@DisplayName("floor 方法测试")
public class FloorFunctionTest {

    static Stream<Arguments> testFloorProvider() {
        return Stream.of(
                // Long
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder().put("a", 2L).build(),
                        new OraDecimal("2")
                ),
                // Integer
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder().put("a", 2).build(),
                        new OraDecimal("2")
                ),
                // BigInteger
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder().put("a", new BigInteger("2")).build(),
                        new OraDecimal("2")
                ),
                // Double
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", 1.993565624)
                                .build(),
                        new OraDecimal("1")
                ),
                // BigDecimal
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new BigDecimal("1.9999431565624544763765735"))
                                .build(),
                        new OraDecimal("1")
                ),
                // OraDecimal
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", new OraDecimal("1.9999431565624544763765735"))
                                .build(),
                        new OraDecimal("1")
                ),
                // String：非数值，无法 floor
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder().put("a", "2").build(),
                        IllegalArgumentException.class
                ),
                // Instant：非数值，无法 floor
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a", Instant.parse("2020-02-01T03:36:19Z"))
                                .build(),
                        IllegalArgumentException.class
                ),
                // Boolean：非数值，无法 floor
                Arguments.of(
                        "floor(a)",
                        HashMapBuilder.<String, Object>builder().put("a", true).build(),
                        IllegalArgumentException.class
                )
        );
    }

    @DisplayName("testFloor")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testFloorProvider")
    public void testFloor(String expression, Map<String, Object> vars, Object expected) {
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
