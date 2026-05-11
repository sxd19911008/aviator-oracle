package io.github.kentasun.aviatororacle.dto;

import java.util.HashMap;

public class DecodeParamTestDTO {

    // 表达式
    private String expression;
    // 参数集合
    private HashMap<String, Object> env;
    // 期望值
    private String expected;

    public DecodeParamTestDTO() {
    }

    public DecodeParamTestDTO(String expression, HashMap<String, Object> env, String expected) {
        this.expression = expression;
        this.env = env;
        this.expected = expected;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public HashMap<String, Object> getEnv() {
        return env;
    }

    public void setEnv(HashMap<String, Object> env) {
        this.env = env;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }
}
