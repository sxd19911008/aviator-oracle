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
 * 自定义 按位取反（~）运算 函数
 */
public class BitNotFunction extends AbstractFunction {

    private static final long serialVersionUID = 5509381924576014803L;

    @Override
    public String getName() {
        return OperatorType.BIT_NOT.token;
    }

    /**
     * BIT_NOT 为一元运算符，只接收单个操作数。
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        // AviatorJavaType 表示变量刚从上下文 Map 中取出，需要包装为自定义类型后进行计算
        if (arg1.getClass() == AviatorJavaType.class) {
            // 包装为 AOAviatorJavaType，其 bitNot 内部会根据实际值类型分发到对应的位运算逻辑
            AOAviatorJavaType aoArg1 = new AOAviatorJavaType(((AviatorJavaType) arg1).getName());
            return aoArg1.bitNot(env);
        } else {
            // AviatorNumber 需转换为自定义 AOAviatorNumber
            if (arg1 instanceof AviatorNumber) {
                AOAviatorNumber aoArg1 = AOAviatorNumber.toAOAviatorNumber(arg1, env);
                return aoArg1.bitNot(env);
            } else {
                // 已是自定义类型或其他，直接调用
                return arg1.bitNot(env);
            }
        }
    }
}
