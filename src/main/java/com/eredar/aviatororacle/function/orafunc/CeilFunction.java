package com.eredar.aviatororacle.function.orafunc;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code ceil()} 方法
 */
public class CeilFunction extends AbstractFunction {

    private static final long serialVersionUID = -3672366972743210403L;

    @Override
    public String getName() {
        return "ceil";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        Object obj = arg1.getValue(env);
        Number number = AORuntimeUtils.toNumber(obj, this.getName());
        Number res = OraFuncUtils.ceil(number);
        return AORuntimeUtils.wrapAviatorObject(res);
    }
}
