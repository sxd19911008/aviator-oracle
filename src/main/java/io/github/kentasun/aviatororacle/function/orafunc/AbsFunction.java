package io.github.kentasun.aviatororacle.function.orafunc;

import io.github.kentasun.aviatororacle.utils.AORuntimeUtils;
import io.github.kentasun.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code abs()} 方法
 */
public class AbsFunction extends AbstractFunction {

    private static final long serialVersionUID = -3967345035228152531L;

    @Override
    public String getName() {
        return "abs";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        Object obj = arg1.getValue(env);
        Number number = AORuntimeUtils.toNumber(obj, this.getName());
        Number res = OraFuncUtils.abs(number);
        return AORuntimeUtils.wrapAviatorObject(res);
    }
}
