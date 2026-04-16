package com.eredar.aviatororacle.runtime.function;

import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class EqFunction extends CompareEqFunction{

    private static final long serialVersionUID = 6781088673492959004L;

    @Override
    public String getName() {
        return OperatorType.EQ.token;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        // 比对
        boolean bool = this.compareEqReturnInt(env, arg1, arg2) == 0;
        // 返回结果
        return AviatorBoolean.valueOf(bool);
    }
}
