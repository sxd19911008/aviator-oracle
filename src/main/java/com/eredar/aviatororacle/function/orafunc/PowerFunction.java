package com.eredar.aviatororacle.function.orafunc;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.object.AOAviatorDecimal;
import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code power()} 方法
 */
public class PowerFunction extends AbstractFunction {

    @Override
    public String getName() {
        return "power";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        /* 非空判断 */
        // 第1个参数非空判断
        if (arg1 instanceof AviatorNil) {
            return AviatorNil.NIL;
        }
        Object obj1 = arg1.getValue(env);
        if (obj1 == null) {
            return AviatorNil.NIL;
        }
        // 第2个参数非空判断
        if (arg2 instanceof AviatorNil) {
            return AviatorNil.NIL;
        }
        Object obj2 = arg2.getValue(env);
        if (obj2 == null) {
            return AviatorNil.NIL;
        }

        /* 类型转换 */
        Number number = AORuntimeUtils.toNumber(obj1, this.getName());
        Number exponent = AORuntimeUtils.toNumber(obj2, this.getName());

        /* 计算 */
        OraDecimal res = OraFuncUtils.power(number, exponent);

        /* 返回结果 */
        return AOAviatorDecimal.valueOf(res);
    }


}
