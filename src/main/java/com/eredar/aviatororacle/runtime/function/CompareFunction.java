package com.eredar.aviatororacle.runtime.function;

import com.eredar.aviatororacle.runtime.object.AOAviatorJavaType;
import com.eredar.aviatororacle.runtime.object.AOAviatorNumber;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 小于、小于等于、大于、大于等于，4种比对类型的父类
 * <p>由于比对本质上返回的是 -1 0 1，分别表示 小于、等于、大于
 * <p>所以统一先在此类中比对2个对象得到数字，然后在每个实现类中根据不同的符号做不同的判断
 */
public abstract class CompareFunction extends AbstractFunction {

    /**
     * 比对2个对象，得到 -1 0 1
     *
     * @param env 参数上下文
     * @param arg1 左边的参数
     * @param arg2 右边的参数
     * @return -1(小于)  0(等于)  1(大于)
     */
    public int compareReturnInt(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        // AviatorJavaType 代表从上下文map获取数据后，刚刚进入计算
        if (arg1.getClass() == AviatorJavaType.class) {
            // 类型转换
            AOAviatorJavaType AOAviatorJavaType = new AOAviatorJavaType(((AviatorJavaType) arg1).getName());
            // 比对
            return AOAviatorJavaType.compare(arg2, env);
        } else {
            // AviatorNumber 需要转换成自定义的 AOAviatorNumber 计算
            if (arg1 instanceof AviatorNumber) {
                AOAviatorNumber aoArg1 = AOAviatorNumber.toAOAviatorNumber(arg1, env);
                // 比对
                return aoArg1.compare(arg2, env);
            } else { // 其他场景直接计算
                // 比对
                return arg1.compare(arg2, env);
            }
        }
    }
}
