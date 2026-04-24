package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.object.AOAviatorRuntimeJavaType;
import com.eredar.aviatororacle.utils.AODateTimeFormatCache;
import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.AOUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * string_to_local_datetime function
 * <p>将字符串解析为 LocalDateTime 对象，不涉及时区转换
 * <p>函数签名：{@code string_to_local_datetime(source, format)}
 * <ul>
 *   <li>arg1 —— 待解析的日期字符串</li>
 *   <li>arg2 —— 字符串日期格式，如 "yyyy-MM-dd HH:mm:ss"</li>
 * </ul>
 */
public class StringToLocalDateTimeFunction extends AbstractFunction {

    private static final long serialVersionUID = 2047563901834726514L;

    @Override
    public String getName() {
        return "string_to_local_datetime";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);
        if (obj1 == null || obj2 == null) {
            throw new IllegalArgumentException(String.format("string_to_local_datetime入参1[%s]、入参2[%s]不能为空", obj1, obj2));
        }
        // 准备入参
        String dateStr;
        if (obj1 instanceof String) {
            dateStr = (String) obj1;
            if (AOUtils.isBlank(dateStr)) {
                throw new IllegalArgumentException("string_to_local_datetime入参1必须是合法的日期字符串，不支持空字符串");
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "string_to_local_datetime入参1必须是合法的日期字符串，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj1)
            ));
        }
        String format;
        if (obj2 instanceof String) {
            format = (String) obj2;
            if (AOUtils.isBlank(format)) {
                throw new IllegalArgumentException("string_to_local_datetime入参2必须是合法的字符串日期格式，不支持空字符串");
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "string_to_local_datetime入参2必须是合法的字符串日期格式，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj2)
            ));
        }
        // 获取无时区的 DateTimeFormatter（LocalDateTime 不需要时区信息）
        DateTimeFormatter dtf = AODateTimeFormatCache.getOrCreateDateFormat(format);
        // 将字符串解析为 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.from(dtf.parse(dateStr));
        // 返回结果
        return AOAviatorRuntimeJavaType.valueOf(dateTime);
    }
}
