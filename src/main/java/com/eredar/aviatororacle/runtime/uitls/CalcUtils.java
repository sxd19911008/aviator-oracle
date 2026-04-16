package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.constants.AviatorOracleConstants;
import com.eredar.aviatororacle.number.OraDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CalcUtils {


    /**
     * 模拟 Oracle 数据库: Date对象 + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return Instant类型的日期对象
     */
    public static Instant oraclePlusDays(Instant date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }

        // 计算总秒数
        long seconds = daysToSeconds(days);
        // 添加总秒数并返回新日期对象
        return date.plus(seconds, ChronoUnit.SECONDS);
    }

    /**
     * 模拟 Oracle 数据库: Date对象 - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return Instant类型的日期对象
     */
    public static Instant oracleMinusDays(Instant date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }
        // 计算总秒数
        long seconds = daysToSeconds(days);
        // 添加总秒数并返回新日期对象
        return date.minus(seconds, ChronoUnit.SECONDS);
    }

    /**
     * 将天数换算成 {@code Long} 型的秒数
     * <p>必定返回整数，如果有小数部分，则四舍五入
     *
     * @param days 天数，可以带小数
     * @return 天数对应的秒数
     */
    private static long daysToSeconds(Number days) {
        if (days instanceof Long || days instanceof Integer || days instanceof BigInteger) {
            return days.longValue() * AviatorOracleConstants.SECONDS_OF_DAY_LONG;
        } else if (days instanceof OraDecimal) {
            return ((OraDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        } else if (days instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        } else {
            return new OraDecimal(String.valueOf(days)).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        }
    }

    /**
     * 计算两个 {@code Instant} 之间的天数差 (date2 - date1)
     *
     * @param beginDate 减数 (起始时间)
     * @param endDate   被减数 (结束时间)
     * @return 差值天数 ({@code OraDecimal})
     */
    public static OraDecimal oracleDaysBetween(Instant beginDate, Instant endDate) {
        // 校验参数，为 null 直接报错
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("Date parameters cannot be null");
        }
        // 获取秒数
        long beginSeconds = beginDate.getEpochSecond();
        long endSeconds = endDate.getEpochSecond();
        // 获取总秒数差
        OraDecimal secondsDiff = OraDecimal.valueOf(endSeconds - beginSeconds);
        // 计算天数，Oracle日期相减场景违反正常的精度逻辑，强行保留40位小数
        return secondsDiff.divide(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL, 40);
        /*
         * 【存疑】计算逻辑为先按照 Oracle 的 number 类型四舍五入保留小数，再强行保留40位小数。
         * 无法验证 Oracle 处理方式是否相同，因为找不到合适的临界小数。
         * 由于 Oracle 计算时一定除以一天的秒数 86400，且 number 的无整数位的小数如果开头有0，
         * 一定是偶数0不占用数字位数（number是20位的内存单位，每一位内存单位代表2位数字），
         * 导致我用任何办法都无法找出临界数字。我尝试过 11、29 等数字，都不行。
         * 由于这里的精度差异可以忽略不计，且目前的逻辑未必错误，所以等遇到这个临界数字时再做处理。
         */
    }
}
