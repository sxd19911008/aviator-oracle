package com.eredar.aviatororacle.function;

import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 小于
 */
public class LtFunction extends CompareFunction {

    private static final long serialVersionUID = 5354064276630346905L;

    @Override
    public String getName() {
        return OperatorType.LT.token;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        // 比对
        boolean bool = this.compareReturnInt(env, arg1, arg2) < 0;
        // 返回结果
        return AviatorBoolean.valueOf(bool);
    }
}
