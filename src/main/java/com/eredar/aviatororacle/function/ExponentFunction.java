package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.object.AOAviatorBigInt;
import com.eredar.aviatororacle.object.AOAviatorDecimal;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.math.BigInteger;
import java.util.Map;

/**
 * 自定义 指数运算函数
 */
public class ExponentFunction extends AbstractFunction {

    private static final long serialVersionUID = 3776849429977003801L;

    @Override
    public String getName() {
        return OperatorType.Exponent.token;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);

        if (!(obj1 instanceof Number) || !(obj2 instanceof Number)) {
            throw new ExpressionRuntimeException(
                    "Could not exponent " + desc(env) + " with " + arg2.desc(env));
        }

        Number n1 = (Number) obj1;
        Number n2 = (Number) obj2;
        final int expInt = n2.intValue();
        if (n1 instanceof BigInteger) {
            return new AOAviatorBigInt(((BigInteger) n1).pow(expInt));
        } else {
            return new AOAviatorDecimal(OraDecimal.valueOf(n1).pow(expInt));
        }
    }
}
