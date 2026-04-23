package com.eredar.aviatororacle.utils;

import com.eredar.aviatororacle.object.AOAviatorNumber;
import com.eredar.aviatororacle.object.AOAviatorRuntimeJavaType;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

public class AORuntimeUtils {

    /**
     * 包装成正确的 {@code AviatorObject} 类型
     * @param object 目标对象
     * @return {@code AviatorObject} 对象
     */
    public static AviatorObject wrapAviatorObject(Object object) {
        // 选择正确的包装类型并返回
        if (object == null) {
            return AviatorNil.NIL;
        } else if (object instanceof Number){
            return AOAviatorNumber.valueOf(object);
        } else {
            return AOAviatorRuntimeJavaType.valueOf(object);
        }
    }

    /**
     * 获取一个对象的 {@code Class}，如果对象为null则返回null
     *
     * @param object 目标对象
     * @return {@code Class} 对象
     */
    public static Class<?> getClass(Object object) {
        if (object == null) {
            return null;
        } else {
            return object.getClass();
        }
    }

    /**
     * 获取一个对象的 {@code Class}，如果对象为null则返回null
     *
     * @param object 目标对象
     * @param functionName 调用 {@code toNumber} 方法的方法名
     * @return {@code Class} 对象
     */
    public static Number toNumber(Object object, String functionName) {
        if (object == null) {
            return null;
        } else if (object instanceof Number) {
            return (Number) object;
        } else {
            throw new IllegalArgumentException(String.format("%s方法不能传入[%s]类型", functionName, getClass(object)));
        }
    }
}
