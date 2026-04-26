package com.eredar.aviatororacle.function.orafunc;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code round()} 方法
 */
public class RoundFunction extends AbstractFunction {

    private static final long serialVersionUID = -7627540740038629988L;

    @Override
    public String getName() {
        return "round";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        return this.round(env, arg1, null);
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        return this.round(env, arg1, arg2);
    }

    private AviatorObject round(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        if (arg1 instanceof AviatorNil) {
            return AviatorNil.NIL;
        }
        Object obj1 = arg1.getValue(env);
        if (obj1 == null) {
            return AviatorNil.NIL;
        }
        if (obj1 instanceof Number) {
            Number res;
            if (arg2 == null) {
                res = OraFuncUtils.round((Number) obj1);
            } else {
                Object obj2 = arg2.getValue(env);
                if (obj2 instanceof Number) {
                    res = OraFuncUtils.round((Number) obj1, (Number) obj2);
                } else {
                    // 与 Oracle 的 round 方法不同，第2个入参不允许为null
                    throw new IllegalArgumentException(String.format("round 方法第2个入参不能为[%s]类型", AORuntimeUtils.getClass(obj2)));
                }
            }
            return AORuntimeUtils.wrapAviatorObject(res);
        } else {
            throw new IllegalArgumentException(String.format("round 方法首个入参不能为[%s]类型", AORuntimeUtils.getClass(obj1)));
        }
    }
}
