package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.utils.AODateTimeFormatCache;
import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.AOUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * local_datetime_to_string function
 * <p>将 LocalDateTime 对象格式化为字符串，不涉及时区转换
 * <p>函数签名：{@code local_datetime_to_string(localDateTime, format)}
 * <ul>
 *   <li>arg1 —— LocalDateTime 对象</li>
 *   <li>arg2 —— 字符串日期格式，如 "yyyy-MM-dd HH:mm:ss"</li>
 * </ul>
 */
public class LocalDateTimeToStringFunction extends AbstractFunction {

    private static final long serialVersionUID = 6312847093541706821L;

    @Override
    public String getName() {
        return "local_datetime_to_string";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);
        if (obj1 == null || obj2 == null) {
            throw new IllegalArgumentException(String.format("local_datetime_to_string入参1[%s]、入参2[%s]不能为空", obj1, obj2));
        }
        // 准备入参
        LocalDateTime dateTime;
        if (obj1 instanceof LocalDateTime) {
            dateTime = (LocalDateTime) obj1;
        } else {
            throw new IllegalArgumentException(String.format(
                    "local_datetime_to_string入参1必须是LocalDateTime，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj1)
            ));
        }
        String format;
        if (obj2 instanceof String) {
            format = (String) obj2;
            if (AOUtils.isBlank(format)) {
                throw new IllegalArgumentException("local_datetime_to_string入参2必须是合法的字符串日期格式，不支持空字符串");
            }
        } else {
            throw new IllegalArgumentException(String.format(
                    "local_datetime_to_string入参2必须是合法的字符串日期格式，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj2)
            ));
        }
        // 获取无时区的 DateTimeFormatter（LocalDateTime 不需要时区信息）
        DateTimeFormatter dtf = AODateTimeFormatCache.getOrCreateDateFormat(format);
        // 将 LocalDateTime 格式化为字符串
        String dateString = dtf.format(dateTime);
        // 返回结果
        return new AviatorString(dateString);
    }
}
