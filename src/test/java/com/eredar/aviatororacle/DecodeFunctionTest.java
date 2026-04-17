package com.eredar.aviatororacle;

import com.eredar.aviatororacle.dto.DecodeParamTestDTO;
import com.eredar.aviatororacle.testUtils.FileUtils;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.eredar.aviatororacle.testUtils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 测试：模拟Oracle数据库 {@code decode()} 方法
 */
@DisplayName("decode 方法测试")
public class DecodeFunctionTest {

    static Stream<Arguments> testErrorProvider() {
        return Stream.of(
                // 1个入参
                Arguments.of(
                        "decode(a1)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a1", 2L)
                                .build(),
                        IllegalArgumentException.class
                ),
                // 2个入参
                Arguments.of(
                        "decode(a1, a2)",
                        HashMapBuilder.<String, Object>builder()
                                .put("a1", new BigInteger("2"))
                                .put("a2", 2)
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
        return FileUtils.readFileAsLines("testParamsOrderData.dat").stream().map(line -> {
            DecodeParamTestDTO dto = JsonUtils.readValue(line, new TypeReference<DecodeParamTestDTO>() {
            });
            return Arguments.of(
                    dto.getExpression(),
                    dto.getEnv(),
                    dto.getExpected()
            );
        });
    }

    @DisplayName("测试call传参到decode时顺序是否正确")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("testParamsOrderProvider")
    public void testParamsOrder(String expression, Map<String, Object> vars, Object expected) {
        if (expected instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) expected;
            Assertions.assertThrows(exceptionClass, () -> AviatorInstance.execute(expression, vars));
        } else {
            Object actual = AviatorInstance.execute(expression, vars);
            Assertions.assertEquals(expected, actual);
        }
    }

    @Disabled
    @Test
    public void buildParamsOrderTestCase() {
        StringBuilder sb = new StringBuilder();
        for (int times = 1; times <= 12; times++) {
            for (int value = 1; value <= times + 1; value++) {
                // 有默认值
                this.buildCase(sb, times, value, false);
                // 无默认值
                this.buildCase(sb, times, value, true);
            }
        }
        System.err.println(sb);
    }

    private void buildCase(StringBuilder sb, int times, int value, boolean hasDefault) {
        sb.append("{\"expression\":");
        sb.append(this.getExpression(times, hasDefault));
        sb.append(",\"env\":{\"i\":").append(value);
        sb.append(this.getPutSb(times, hasDefault));
        sb.append("},\"expected\":");
        sb.append(this.getExpected(value, times, hasDefault));
        sb.append("}\n");
    }

    private StringBuilder getExpression(int times, boolean hasDefault) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"decode(i");

        for (int i = 1; i <= times; i++) {
            sb.append(String.format(", c%s, r%s", i, i));
        }

        if (hasDefault) {
            sb.append(", d");
        }

        sb.append(")\"");
        return sb;
    }

    private StringBuilder getPutSb(int times, boolean hasDefault) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= times; i++) {
            sb.append(String.format(",\"c%s\":%s,\"r%s\":\"R%s\"", i, i, i, i));
        }
        if (hasDefault) {
            sb.append(",\"d\":\"default\"");
        }
        return sb;
    }

    private String getExpected(int value, int times, boolean hasDefault) {
        if (value <= times) {
            return String.format("\"R%s\"", value);
        } else {
            if (hasDefault) {
                return "\"default\"";
            } else {
                return "null";
            }
        }
    }
}
