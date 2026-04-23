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

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        if (obj1 == null) {
            return AviatorNil.NIL;
        }

        if (!(obj1 instanceof Instant)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法第1个入参必须是Instant，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj1)));
        }

        Object obj2 = arg2.getValue(env);
        if (!(obj2 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj2)));
        }
        String zoneIdStr = (String) obj2;
        if (AOUtils.isBlank(zoneIdStr)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId[%s]不能为空字符串", zoneIdStr));
        }

        Instant res = OraFuncUtils.truncWithZone((Instant) obj1, ZoneId.of(zoneIdStr));
        return AORuntimeUtils.wrapAviatorObject(res);
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3) {
        Object obj1 = arg1.getValue(env);
        if (obj1 == null) {
            return AviatorNil.NIL;
        }

        if (!(obj1 instanceof Instant)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法第1个入参必须是Instant，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj1)));
        }

        Object obj2 = arg2.getValue(env);
        if (!(obj2 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法format必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj2)));
        }

        Object obj3 = arg3.getValue(env);
        if (!(obj3 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId必须是String类型，不支持[%s]",
                    AORuntimeUtils.getClass(obj3)));
        }
        String zoneIdStr = (String) obj3;
        if (AOUtils.isBlank(zoneIdStr)) {
            throw new IllegalArgumentException(String.format(
                    "truncWithZone方法zoneId[%s]不能为空字符串", zoneIdStr));
        }

        Instant res = OraFuncUtils.truncWithZone((Instant) obj1, (String) obj2, ZoneId.of(zoneIdStr));
        return AORuntimeUtils.wrapAviatorObject(res);
    }
}
