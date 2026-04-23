package com.eredar.aviatororacle.function.orafunc;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.AOUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

/**
 * Oracle数据库的 {@code trunc()} 方法 — 带时区版本（仅支持 Instant）
 *
 * @see TruncFunction
 */
public class TruncWithZoneFunction extends AbstractFunction {

    private static final long serialVersionUID = 2781493605724813956L;

    @Override
    public String getName() {
        return "truncWithZone";
    }

    /**
     * 两参数重载：{@code truncWithZone(zoneId, instant)}
     * <p>将 {@link Instant} 在指定时区下截断到天（当天午夜零点）。
     */
    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        // arg1 = zoneId
        Object obj1 = arg1.getValue(env);
        if (!(obj1 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj1)));
        }
        String zoneIdStr = (String) obj1;
        if (AOUtils.isBlank(zoneIdStr)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId[%s]不能为空字符串", zoneIdStr));
        }

        // arg2 = instant
        Object obj2 = arg2.getValue(env);
        if (obj2 == null) {
            return AviatorNil.NIL;
        }
        if (!(obj2 instanceof Instant)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法instant入参必须是Instant，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj2)));
        }

        Instant res = OraFuncUtils.truncWithZone(ZoneId.of(zoneIdStr), (Instant) obj2);
        return AORuntimeUtils.wrapAviatorObject(res);
    }

    /**
     * 三参数重载：{@code truncWithZone(zoneId, instant, format)}
     * <p>将 {@link Instant} 在指定时区下按格式模型截断。
     * <p>参数设计：zoneId 放首位，后面的 {@code (instant, format)} 与 {@code trunc(instant, format)} 保持一致，
     * 方便使用者从 trunc 迁移到 truncWithZone 时只需在前面加上时区参数。
     */
    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3) {
        // arg1 = zoneId
        Object obj1 = arg1.getValue(env);
        if (!(obj1 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj1)));
        }
        String zoneIdStr = (String) obj1;
        if (AOUtils.isBlank(zoneIdStr)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId[%s]不能为空字符串", zoneIdStr));
        }

        // arg2 = instant
        Object obj2 = arg2.getValue(env);
        if (obj2 == null) {
            return AviatorNil.NIL;
        }
        if (!(obj2 instanceof Instant)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法instant入参必须是Instant，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj2)));
        }

        // arg3 = format
        Object obj3 = arg3.getValue(env);
        if (!(obj3 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法format必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj3)));
        }

        Instant res = OraFuncUtils.truncWithZone(ZoneId.of(zoneIdStr), (Instant) obj2, (String) obj3);
        return AORuntimeUtils.wrapAviatorObject(res);
    }
}
