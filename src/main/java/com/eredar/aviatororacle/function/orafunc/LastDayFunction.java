package com.eredar.aviatororacle.function.orafunc;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.AOUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * Oracle数据库的 {@code last_day()} 方法
 */
public class LastDayFunction extends AbstractFunction {

    private static final long serialVersionUID = 6183924750218463975L;

    @Override
    public String getName() {
        return "last_day";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        Object obj1 = arg1.getValue(env);

        if (obj1 == null) {
            throw new IllegalArgumentException("last_day方法date不能为null");
        }

        if (obj1 instanceof Date) {
            Date res = OraFuncUtils.lastDay((Date) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof LocalDateTime) {
            LocalDateTime res = OraFuncUtils.lastDay((LocalDateTime) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof Instant) {
            Instant res = OraFuncUtils.lastDay((Instant) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }

        throw new IllegalArgumentException(String.format(
                "last_day方法不传入时区zoneId的场景，入参不支持[%s]类型，仅支持Date、LocalDateTime、Instant",
                AORuntimeUtils.getClass(obj1)));
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);

        if (obj1 == null || obj2 == null) {
            throw new IllegalArgumentException(String.format(
                    "last_day方法date[%s]不能为null。zoneId[%s]可以不传入，传入则不能为null。", obj1, obj2));
        }

        if (!(obj1 instanceof Instant)) {
            throw new IllegalArgumentException(String.format(
                    "last_day方法传入时区zoneId时，第1个入参必须是Instant类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj1)));
        }
        if (!(obj2 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "last_day方法zoneId必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj2)));
        }

        String zoneIdStr = (String) obj2;
        if (AOUtils.isBlank(zoneIdStr)) {
            throw new IllegalArgumentException(String.format(
                    "last_day方法zoneId[%s]可以不传入，传入则不能为空字符串", zoneIdStr));
        }

        Instant res = OraFuncUtils.lastDay((Instant) obj1, ZoneId.of(zoneIdStr));
        return AORuntimeUtils.wrapAviatorObject(res);
    }
}
