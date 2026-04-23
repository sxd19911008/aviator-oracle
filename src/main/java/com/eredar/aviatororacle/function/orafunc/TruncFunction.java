package com.eredar.aviatororacle.function.orafunc;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * Oracle数据库的 {@code trunc()} 方法
 */
public class TruncFunction extends AbstractFunction {

    private static final long serialVersionUID = -4518276391023847562L;

    @Override
    public String getName() {
        return "trunc";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        if (arg1 instanceof AviatorNil) {
            return arg1;
        }
        Object obj1 = arg1.getValue(env);
        if (obj1 == null) {
            return AviatorNil.NIL;
        }

        if (obj1 instanceof Number) {
            Number res = OraFuncUtils.trunc((Number) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof Date) {
            Date res = OraFuncUtils.trunc((Date) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof LocalDateTime) {
            LocalDateTime res = OraFuncUtils.trunc((LocalDateTime) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof Instant) {
            Instant res = OraFuncUtils.trunc((Instant) obj1);
            return AORuntimeUtils.wrapAviatorObject(res);
        }

        throw new IllegalArgumentException(String.format(
                "trunc方法第1个入参不支持[%s]类型，仅支持Number、Date、LocalDateTime、Instant",
                AORuntimeUtils.getClass(obj1)));
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        if (arg1 instanceof AviatorNil) {
            return arg1;
        }
        Object obj1 = arg1.getValue(env);
        if (obj1 == null) {
            return AviatorNil.NIL;
        }
        Object obj2 = arg2.getValue(env);

        // 数字截断: trunc(number, scale)
        if (obj1 instanceof Number) {
            if (!(obj2 instanceof Number)) {
                throw new IllegalArgumentException(String.format(
                        "trunc方法对数字操作时，第2个入参必须为Number，不支持[%s]类型",
                        AORuntimeUtils.getClass(obj2)));
            }
            Number res = OraFuncUtils.trunc((Number) obj1, (Number) obj2);
            return AORuntimeUtils.wrapAviatorObject(res);
        }

        // 日期截断: trunc(date, format)
        if (!(obj2 instanceof String)) {
            throw new IllegalArgumentException(String.format(
                    "trunc方法对日期操作时，第2个入参必须为String，不支持[%s]类型",
                    AORuntimeUtils.getClass(obj2)));
        }
        String format = (String) obj2;

        if (obj1 instanceof Date) {
            Date res = OraFuncUtils.trunc((Date) obj1, format);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof LocalDateTime) {
            LocalDateTime res = OraFuncUtils.trunc((LocalDateTime) obj1, format);
            return AORuntimeUtils.wrapAviatorObject(res);
        }
        if (obj1 instanceof Instant) {
            Instant res = OraFuncUtils.trunc((Instant) obj1, format);
            return AORuntimeUtils.wrapAviatorObject(res);
        }

        throw new IllegalArgumentException(String.format(
                "trunc方法第1个入参不支持[%s]类型，仅支持Number、Date、LocalDateTime、Instant",
                AORuntimeUtils.getClass(obj1)));
    }

}
