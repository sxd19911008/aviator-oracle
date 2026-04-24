package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.utils.AODateTimeFormatCache;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * instant_to_string function
 */
public class InstantToStringFunction extends AbstractFunction {

    private static final long serialVersionUID = -4079240612701467123L;

    @Override
    public String getName() {
        return "instant_to_string";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        // 准备入参
        Instant date = (Instant) arg1.getValue(env);
        String format = FunctionUtils.getStringValue(arg2, env);
        String zoneId = FunctionUtils.getStringValue(arg3, env);
        // 获取 DateTimeFormatter 对象
        DateTimeFormatter dtf = AODateTimeFormatCache.getOrCreateDateFormat(format, zoneId);
        // 转换日期对象为字符串
        String dateString = dtf.format(date);
        // 返回结果
        return new AviatorString(dateString);
    }
}
