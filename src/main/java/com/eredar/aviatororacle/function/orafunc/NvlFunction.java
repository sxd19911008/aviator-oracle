package com.eredar.aviatororacle.function.orafunc;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code nvl()} 方法
 */
public class NvlFunction extends AbstractFunction {

    private static final long serialVersionUID = 6487483675829260178L;

    @Override
    public String getName() {
        return "nvl";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        if (arg1 instanceof AviatorNil) {
            return arg2;
        } else if (arg1.getValue(env) == null) {
            return arg2;
        }
        return arg1;
    }
}
