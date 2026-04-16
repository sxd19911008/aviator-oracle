package com.eredar.aviatororacle.runtime.function;

import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 大于
 */
public class GeFunction extends CompareFunction {

    private static final long serialVersionUID = -798106212151596800L;

    @Override
    public String getName() {
        return OperatorType.GE.token;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        // 比对
        boolean bool = this.compareReturnInt(env, arg1, arg2) >= 0;
        // 返回结果
        return AviatorBoolean.valueOf(bool);
    }
}
