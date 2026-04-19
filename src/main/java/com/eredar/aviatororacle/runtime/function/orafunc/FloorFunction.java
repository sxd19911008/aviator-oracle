package com.eredar.aviatororacle.runtime.function.orafunc;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.uitls.AORuntimeUtils;
import com.eredar.aviatororacle.runtime.uitls.OracleFunctionUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code floor()} 方法
 */
public class FloorFunction extends AbstractFunction {

    private static final long serialVersionUID = -7028857170933522482L;

    @Override
    public String getName() {
        return "floor";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        Object obj = arg1.getValue(env);
        OraDecimal decimal = OracleFunctionUtils.floor(obj);
        return AORuntimeUtils.wrapAviatorObject(decimal);
    }
}
