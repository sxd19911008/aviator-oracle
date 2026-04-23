package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.object.AOAviatorJavaType;
import com.eredar.aviatororacle.object.AOAviatorNumber;
import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 自定义取余数函数
 */
public class ModFunction extends AbstractFunction {

    private static final long serialVersionUID = 3596031714905686303L;

    @Override
    public String getName() {
        return OperatorType.MOD.token;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        // AviatorJavaType 代表从上下文map获取数据后，刚刚进入计算
        if (arg1.getClass() == AviatorJavaType.class) {
            // 类型转换
            AOAviatorJavaType AOAviatorJavaType = new AOAviatorJavaType(((AviatorJavaType) arg1).getName());
            // 计算
            return AOAviatorJavaType.mod(arg2, env);
        } else {
            // AviatorNumber 需要转换成自定义的 AOAviatorNumber 计算
            if (arg1 instanceof AviatorNumber) {
                AOAviatorNumber aoArg1 = AOAviatorNumber.toAOAviatorNumber(arg1, env);
                return aoArg1.mod(arg2, env);
            } else { // 其他场景直接计算
                return arg1.mod(arg2, env);
            }
        }
    }
}

