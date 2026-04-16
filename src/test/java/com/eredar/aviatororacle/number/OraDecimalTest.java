package com.eredar.aviatororacle.number;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@DisplayName("OraDecimal 测试")
public class OraDecimalTest {

    // -------------------------------------------------------------------------
    // divide
    // -------------------------------------------------------------------------

    static Stream<Arguments> divideProvider() {
        return Stream.of(
                Arguments.of(
                        "res有1位整数部分",
                        "678",
                        "99",
                        "6.84848484848484848484848484848484848485"
                ),
                Arguments.of(
                        "res无整数部分，小数开头没有0",
                        "2710",
                        "2880.6",
                        "0.9407762271748941192807054085954315073249"
                ),
                Arguments.of(
                        "res无整数部分，小数开头3个0",
                        "2.71",
                        "2880.6",
                        "0.000940776227174894119280705408595431507325"
                ),
                Arguments.of(
                        "res无整数部分，小数开头4个0",
                        "0.271",
                        "2880.6",
                        "0.00009407762271748941192807054085954315073249"
                ),
                Arguments.of(
                        "指定精度",
                        "0.271",
                        "2880.6",
                        "0.00009407762271748941192807054085954315073249"
                )
        );
    }

    @DisplayName("divide 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: {1} / {2}")
    @MethodSource("divideProvider")
    public void divideTest(String caseId, String decimal1, String decimal2, String expected) {
        OraDecimal actual = new OraDecimal(decimal1).divide(new OraDecimal(decimal2));
        Assertions.assertEquals(0, actual.compareTo(new OraDecimal(expected)), actual + " / " + expected);
    }

    // -------------------------------------------------------------------------
    // divide 指定精度
    // -------------------------------------------------------------------------

    static Stream<Arguments> divideScaleTestProvider() {
        return Stream.of(
                Arguments.of(
                        "指定精度",
                        "22222222222222222222222222222222223.9989",
                        "2",
                        3,
                        "11111111111111111111111111111111112"
                )
        );
    }

    @DisplayName("divide 指定精度测试")
    @ParameterizedTest(name = "【{index}】{0}: {1} / {2}; scale={3}")
    @MethodSource("divideScaleTestProvider")
    public void divideScaleTest(String caseId, String decimal1, String decimal2, Integer scale, String expected) {
        OraDecimal actual = new OraDecimal(decimal1).divide(new OraDecimal(decimal2), scale);
        Assertions.assertEquals(new OraDecimal(expected), actual);
    }
}
