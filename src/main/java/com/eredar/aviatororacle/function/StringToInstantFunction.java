package com.eredar.aviatororacle.function;

import com.eredar.aviatororacle.object.AOAviatorRuntimeJavaType;
import com.eredar.aviatororacle.utils.AODateTimeFormatCache;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * string_to_instant function
 */
public class StringToInstantFunction extends AbstractFunction {

    private static final long serialVersionUID = -8780463814840818949L;

    @Override
    public String getName() {
        return "string_to_instant";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        // 准备入参
        String source = FunctionUtils.getStringValue(arg1, env);
        String format = FunctionUtils.getStringValue(arg2, env);
        String zoneId = FunctionUtils.getStringValue(arg3, env);
        // 获取 DateTimeFormatter 对象
        DateTimeFormatter dtf = AODateTimeFormatCache.getOrCreateDateFormat(format, zoneId);
        // 解析日期字符串
        Instant from = Instant.from(dtf.parse(source));
        // 返回结果
        return AOAviatorRuntimeJavaType.valueOf(from);
    }
}
