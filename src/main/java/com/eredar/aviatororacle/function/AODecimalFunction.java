package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.object.AOAviatorDecimal;
import com.eredar.aviatororacle.object.AOAviatorNumber;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.math.BigDecimal;
import java.util.Map;

public class AODecimalFunction extends AbstractFunction {

    private static final long serialVersionUID = 8344353851619052364L;

    @Override
    public String getName() {
        return "decimal";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        switch (arg1.getAviatorType()) {
            case Boolean:
                return AOAviatorDecimal.valueOf(arg1.booleanValue(env) ? OraDecimal.ONE : OraDecimal.ZERO);
            case JavaType:
                Object obj = arg1.getValue(env);
                if (obj instanceof OraDecimal) {
                    return AOAviatorDecimal.valueOf((OraDecimal) obj);
                } else if (obj instanceof Number || obj instanceof String || obj instanceof Character) {
                    return AOAviatorDecimal.valueOf(new OraDecimal(String.valueOf(obj)));
                } else {
                    throw new ClassCastException(
                            "Could not cast " + obj.getClass().getName() + " to decimal");
                }
            case String:
                return AOAviatorDecimal.valueOf(new OraDecimal((String) arg1.getValue(env)));
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                OraDecimal decimal;
                if (arg1 instanceof AviatorNumber) {
                    BigDecimal bigDecimal = ((AviatorNumber) arg1).toDecimal(env);
                    decimal = new OraDecimal(bigDecimal);
                } else if (arg1 instanceof AOAviatorDecimal) {
                    return arg1;
                } else {
                    decimal = ((AOAviatorNumber) arg1).toDecimal();
                }
                return AOAviatorDecimal.valueOf(decimal);
            default:
                throw new ClassCastException("Could not cast " + arg1 + " to decimal");
        }
    }
}
