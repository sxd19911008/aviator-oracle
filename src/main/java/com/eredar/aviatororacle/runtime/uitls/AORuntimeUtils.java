package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.runtime.object.AOAviatorNumber;
import com.eredar.aviatororacle.runtime.object.AOAviatorRuntimeJavaType;
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
}
