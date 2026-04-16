package com.eredar.aviatororacle;

import com.googlecode.aviator.AviatorEvaluatorInstance;

import java.util.Map;

public class AviatorInstance {

    private static final AviatorEvaluatorInstance aviator = AviatorOracleBuilder.builder().build();

    public static Object execute(String expression, Map<String, Object> env) {
        return aviator.execute(expression, env);
    }
}
