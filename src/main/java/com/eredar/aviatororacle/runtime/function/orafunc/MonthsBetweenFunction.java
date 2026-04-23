package com.eredar.aviatororacle.runtime.function.orafunc;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.object.AOAviatorDecimal;
import com.eredar.aviatororacle.runtime.uitls.oracle.OracleInstantUtils;
import com.eredar.aviatororacle.utils.AOUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * Oracle数据库的 {@code months_between()} 方法
 */
public class MonthsBetweenFunction extends AbstractFunction {

    private static final long serialVersionUID = 7797011540299154671L;

    @Override
    public String getName() {
        return "months_between";
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);

        if (obj1 == null || obj2 == null) {
            throw new IllegalArgumentException(String.format("months_between方法endDate[%s]、beginDate[%s]，都不能为null", obj1, obj2));
        }

        if (obj1 instanceof Instant && obj2 instanceof Instant) {
            Instant endDate = (Instant) obj1;
            Instant beginDate = (Instant) obj2;

            OraDecimal months = OracleInstantUtils.monthsBetween(endDate, beginDate);
            return AOAviatorDecimal.valueOf(months);
        }

        if (obj1 instanceof LocalDateTime && obj2 instanceof LocalDateTime ) {
            // TODO 完成LocalDateTime类型
        }

        if (obj1 instanceof Date && obj2 instanceof Date ) {
            // TODO 完成Date类型
        }

        throw new IllegalArgumentException(String.format(
                "months_between方法不传入时区zoneId的场景，入参endDate[%s]和beginDate[%s]必须满足其中一个条件：\n" +
                        "1. 都是Instant\n" +
                        "2. 都是LocalDateTime\n" +
                        "3. 都是Date",
                obj1.getClass().getName(),
                obj2.getClass().getName()
        ));
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2, final AviatorObject arg3) {
        Object obj1 = arg1.getValue(env);
        Object obj2 = arg2.getValue(env);
        Object obj3 = arg3.getValue(env);

        if (obj1 == null || obj2 == null || obj3 == null) {
            throw new IllegalArgumentException(String.format("months_between方法endDate[%s]、beginDate[%s]都不能为null。zoneId[%s]可以不传入，传入则不能为null", obj1, obj2, obj3));
        }

        if (obj1 instanceof Instant && obj2 instanceof Instant) {
            if (!(obj3 instanceof String)) {
                throw new IllegalArgumentException(String.format("months_between方法zoneId必须是String，不支持[%s]类型", obj3.getClass().getName()));
            }
            Instant endDate = (Instant) obj1;
            Instant beginDate = (Instant) obj2;
            String zoneId = (String) obj3;
            if (AOUtils.isBlank(zoneId)) {
                throw new IllegalArgumentException(String.format("months_between方法zoneId[%s]可以不传入，传入则不能为空字符串", zoneId));
            }

            OraDecimal months = OracleInstantUtils.monthsBetween(endDate, beginDate, ZoneId.of(zoneId));
            return AOAviatorDecimal.valueOf(months);
        }

        throw new IllegalArgumentException(String.format(
                "months_between方法传入时区zoneId的场景，入参endDate[%s]和beginDate[%s]必须都是Instant",
                obj1.getClass().getName(),
                obj2.getClass().getName()
        ));
    }
}
