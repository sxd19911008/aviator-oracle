package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Oracle 条件函数测试")
public class OracleConditionalFunctionUtilsTest {

    // -------------------------------------------------------------------------
    // decode
    // -------------------------------------------------------------------------

    /**
     * decode 正常返回值场景：最后一列 {@code null} 表示期望 {@link OracleConditionalFunctionUtils#decode} 返回 null。
     */
    static Stream<Arguments> decodeMatchProvider() {
        return Stream.of(
                Arguments.of("匹配第一个 search", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode("1", "1", "A", "2", "B", "C"), "A"),
                Arguments.of("匹配第二个 search", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode("2", "1", "A", "2", "B", "C"), "B"),
                Arguments.of("无匹配返回默认值", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode("3", "1", "A", "2", "B", "C"), "C"),
                Arguments.of("无匹配且无默认值返回 null", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode("3", "1", "A", "2", "B"), null),
                Arguments.of("null 与 null 匹配", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode(null, null, "Result", "Other"), "Result"),
                Arguments.of("表达式为 null 且 search 非 null", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode(null, "1", "Result", "Default"), "Default"),
                Arguments.of("Integer 与 Long 数值相等", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode(100, 100L, "Match", "No Match"), "Match"),
                Arguments.of("Long 与 OraDecimal 数值相等", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode(200L, new OraDecimal("200.00"), "Match", "No Match"), "Match"),
                Arguments.of("OraDecimal 不同精度数值相等", (Supplier<Object>) () -> OracleConditionalFunctionUtils.decode(new OraDecimal("3.14"), new OraDecimal("3.1400"), "Match", "No Match"), "Match")
        );
    }

    @DisplayName("decode 方法匹配与返回值测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeMatchProvider")
    public void testDecodeMatch(String caseId, Supplier<Object> decodeCall, Object expected) {
        if (expected == null) {
            Assertions.assertNull(decodeCall.get());
        } else {
            Assertions.assertEquals(expected, decodeCall.get());
        }
    }

    static Stream<Arguments> decodeInvalidArgsProvider() {
        //noinspection Convert2MethodRef
        return Stream.of(
                Arguments.of("入参仅2个", (Executable) () -> OracleConditionalFunctionUtils.decode("1", "2")),
                Arguments.of("入参仅1个", (Executable) () -> OracleConditionalFunctionUtils.decode("1")),
                Arguments.of("入参0个", (Executable) () -> OracleConditionalFunctionUtils.decode())
        );
    }

    @DisplayName("decode 方法非法入参测试")
    @ParameterizedTest(name = "【{index}】{0}")
    @MethodSource("decodeInvalidArgsProvider")
    public void testDecodeInvalidArgs(String caseId, Executable executable) {
        assertThrows(IllegalArgumentException.class, executable);
    }
}
