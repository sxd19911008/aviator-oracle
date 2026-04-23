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
 * Oracle数据库的 {@code add_months()} 方法
 */
public class AddMonthsFunction extends AbstractFunction {

    private static final long serialVersionUID = 3295817462015839741L;

    @Override
    public String getName() {
        return "add_months";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);

        if (obj1 == null || obj2 == null) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法date[%s]、months[%s]都不能为null", obj1, obj2));
        }
        if (!(obj2 instanceof Number)) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法第2个入参(months)必须是Number类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj2)));
        }
        Number months = (Number) obj2;

        if (obj1 instanceof Date) {
            Date res = OraFuncUtils.addMonths((Date) obj1, months);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof LocalDateTime) {
            LocalDateTime res = OraFuncUtils.addMonths((LocalDateTime) obj1, months);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof Instant) {
            Instant res = OraFuncUtils.addMonths((Instant) obj1, months);
            return AORuntimeUtils.wrapAviatorObject(res);
        }

        throw new IllegalArgumentException(String.format(
                "add_months方法不传入时区zoneId的场景，第1个入参不支持[%s]类型，仅支持Date、LocalDateTime、Instant",
                obj1.getClass().getName()));
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);
        Object obj3 = arg3.getValue(env);

        if (obj1 == null || obj2 == null || obj3 == null) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法date[%s]、months[%s]都不能为null。zoneId[%s]可以不传入，传入则不能为null。",
                    obj1, obj2, obj3));
        }

        if (!(obj1 instanceof Instant)) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法传入3个参数时，第1个入参必须是Instant类型，不支持[%s]",
                    obj1.getClass().getName()));
        }
        if (!(obj2 instanceof Number)) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法第2个入参(months)必须是Number类型，不支持[%s]",
                    obj2.getClass().getName()));
        }
        if (!(obj3 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法zoneId必须是String类型，不支持[%s]",
                    obj3.getClass().getName()));
        }

        String zoneIdStr = (String) obj3;
        if (AOUtils.isBlank(zoneIdStr)) {
            throw new IllegalArgumentException(String.format(
                    "add_months方法zoneId[%s]可以不传入，传入则不能为空字符串", zoneIdStr));
        }

        Instant res = OraFuncUtils.addMonths((Instant) obj1, (Number) obj2, ZoneId.of(zoneIdStr));
        return AORuntimeUtils.wrapAviatorObject(res);
    }
}
