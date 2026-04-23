package com.eredar.aviatororacle;

import com.eredar.aviatororacle.dto.DecodeParamTestDTO;
import com.eredar.aviatororacle.testUtils.FileUtils;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.eredar.aviatororacle.testUtils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 测试：模拟Oracle数据库 {@code coalesce()} 方法
 */
@DisplayName("coalesce 方法测试")
public class CoalesceFunctionTest {

    private static final Map<String, Object> EMPTY_ENV = new HashMap<>();

    static Stream<Arguments> testErrorProvider() {
        return Stream.of(
                // 1个入参
                Arguments.of(
                        "coalesce(a1)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a1", 2L)
                                .build(),
                        IllegalArgumentException.class
                )
        );
    }

    @DisplayName("异常场景测试")
    @ParameterizedTest(name = "【{index}】{0}: vars={1}")
    @MethodSource("testErrorProvider")
    public void testError(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    static Stream<Arguments> testParamsOrderProvider() {
        return FileUtils.readFileAsLines("testCoalesceParamsOrderData.dat").stream().map(line -> {
            DecodeParamTestDTO dto = JsonUtils.readValue(line, new TypeReference<DecodeParamTestDTO>() {
            });
            return Arguments.of(
                    dto.getExpression(),
                    Long.parseLong(dto.getExpected())
            );
        });
    }

    @DisplayName("测试call传参到coalesce时顺序是否正确")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testParamsOrderProvider")
    public void testParamsOrder(String expression, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, EMPTY_ENV));
        } else {
            Object actual = AviatorInstance.execute(expression, EMPTY_ENV);
            Assertions.assertEquals(expected, actual);
        }
    }
}
