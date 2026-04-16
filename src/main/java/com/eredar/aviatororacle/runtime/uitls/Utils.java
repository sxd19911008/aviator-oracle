package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.constants.AviatorOracleConstants;
import com.eredar.aviatororacle.number.OraDecimal;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * AviatorOracle 工具类
 * <p>提供可供 Aviator 表达式引擎调用的静态方法。通过 {@code AviatorEvaluator.importFunctions(AviatorUtils.class)} 导入。</p>
 */
public class Utils {

    /**
     * {@link Utils#yearsBetween} 方法常量
     * Y: 不满1年的部分算做1年
     * L: 舍去不满1年的部分
     */
    public static final String YEARS_BETWEEN_Y = "Y";
    public static final String YEARS_BETWEEN_L = "L";
    private static final List<String> YEARS_BETWEEN_LIST = new ArrayList<>(Arrays.asList(YEARS_BETWEEN_Y, YEARS_BETWEEN_L));

    /**
     * 模拟 Oracle 数据库的 DECODE 函数实现。
     * <p>
     * <b>语法：</b><br/>
     * {@code decode(expression, search1, result1, search2, result2, ..., [default])}
     * </p>
     * <p>
     * <b>功能说明：</b><br/>
     * 该函数逐个比较 {@code expression} 和 {@code search}。
     * 如果 {@code expression} 等于某个 {@code search}，则返回对应的 {@code result}。
     * 如果没有匹配项，则返回 {@code default}。
     * 如果没有匹配项且未指定 {@code default}，则返回 {@code null}。
     * </p>
     * <p>
     * <b>特殊处理：</b><br/>
     * 1. <b>Null 值比较：</b> 与 Oracle 的 DECODE 一致，本方法认为 {@code null} 等于 {@code null}。<br/>
     * 2. <b>数值比较：</b> 内部使用 {@link OraDecimal} 进行数值比较，以确保不同类型数字（如 Integer、Long、OraDecimal）在数值相等时能正确匹配。
     * </p>
     *
     * @param args 变长参数。
     *             args[0]: 待比较的表达式。
     *             args[1, 2, ...]: 成对出现的 search 和 result。
     *             最后一位（可选）: 默认值（当参数总数为偶数时存在）。
     * @return 匹配到的结果对象，或者默认值，或者 null。
     */
    public static Object decode(Object... args) {
        // 至少需要 3 个参数。
        if (args == null || args.length < 3) {
            int argsLength = 0;
            if (args != null) {
                argsLength = args.length;
            }
            throw new IllegalArgumentException("入参数量只有" + argsLength + "个，少于3个");
        }

        Object expression = args[0];
        int length = args.length;

        /*
         * 遍历参数列表，寻找匹配项。
         * i 从 1 开始，步长为 2。
         * args[i] 是 search 值，args[i+1] 是对应的 result 值。
         */
        for (int i = 1; i < length - 1; i += 2) {
            Object search = args[i];
            Object result = args[i + 1];

            /* 调用内部相等判断逻辑 */
            if (isEqualForDecode(expression, search)) {
                return result;
            }
        }

        /*
         * 如果未匹配到任何 search 项，则尝试返回默认值。
         * 根据 Oracle 规范，如果参数总数是偶数（例如 4, 6, 8...），
         * 则最后一个参数就是默认值。
         */
        if (length % 2 == 0) {
            return args[length - 1];
        }

        /* 无匹配项且无默认值，返回 null */
        return null;
    }

    /**
     * 模拟 Oracle 数据库的 NVL 函数实现。
     * <p>
     * <b>语法：</b><br/>
     * {@code nvl(expr1, replace_with)}
     * </p>
     * <p>
     * <b>功能说明：</b><br/>
     * 如果 {@code expr1} 为 null，则返回 {@code replace_with}；否则返回 {@code expr1}。
     * </p>
     *
     * @param expr1        待检查的表达式
     * @param replace_with 当 expr1 为 null 时返回的替换值
     * @return 结果对象
     */
    public static Object nvl(Object expr1, Object replace_with) {
        return expr1 == null ? replace_with : expr1;
    }

    public static Long yearsBetween(Instant beginDate, Instant endDate, String type) {
        return yearsBetween(beginDate, endDate, type, ZoneOffset.UTC);
    }

    /**
     * 2个日期之间间隔的年份
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param type      类型：Y-不满1年算1年；L-不满1年舍去。
     * @param zoneId    时区（若为 null 则默认使用 UTC）
     * @return 间隔年份
     */
    public static Long yearsBetween(Instant beginDate, Instant endDate, String type, ZoneId zoneId) {
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }
        if (beginDate.isAfter(endDate)) {
            throw new IllegalArgumentException("起始日期晚于结束日期");
        }
        if (!YEARS_BETWEEN_LIST.contains(type)) {
            throw new IllegalArgumentException("不支持该类型：" + type);
        }

        // 转换为 ZonedDateTime
        ZonedDateTime begin = beginDate.atZone(zoneId);
        ZonedDateTime end = endDate.atZone(zoneId);

        long years = ChronoUnit.YEARS.between(begin, end);
        // 如果 type 为 Y，则不满1年的部分算做1年
        if (YEARS_BETWEEN_Y.equals(type) && end.isAfter(begin.plusYears(years))) {
            years++;
        }

        return years;
    }

    public static OraDecimal monthsBetween(Instant beginDate, Instant endDate) {
        return monthsBetween(beginDate, endDate, ZoneOffset.UTC);
    }

    /**
     * 2个日期之间间隔的月份
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 间隔月份
     */
    public static OraDecimal monthsBetween(Instant beginDate, Instant endDate, ZoneId zoneId) {
        /* 入参校验 */
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }
        if (zoneId == null) {
            throw new IllegalArgumentException("时区zoneId不能为空");
        }
        // 支持负数计算
        OraDecimal sign = OraDecimal.ONE; // 正负号
        if (beginDate.isAfter(endDate)) {
            Instant tempDate = beginDate;
            beginDate = endDate;
            endDate = tempDate;
            sign = AviatorOracleConstants.NEG; // 设置为 -1
        }

        /* 转换为 ZonedDateTime */
        ZonedDateTime begin = beginDate.atZone(zoneId);
        ZonedDateTime end = endDate.atZone(zoneId);

        /* 计算基础月份差 (年差 * 12 + 月差) */
        int yearsDiff = end.getYear() - begin.getYear();
        int monthsDiff = end.getMonthValue() - begin.getMonthValue();
        int totalMonths = yearsDiff * 12 + monthsDiff;
        OraDecimal months = OraDecimal.valueOf(totalMonths);

        /* 判断是否“同日”或“均为月末” */
        // 判断是否“同日”，比如1月12日与2月12日属于“同日”
        boolean sameDayOfMonth = begin.getDayOfMonth() == end.getDayOfMonth();
        // 判断是否“均为月末”，比如1月31日与2月28日属于“均为月末”
        boolean bothLastDayOfMonth = isLastDayOfMonth(begin) && isLastDayOfMonth(end);
        if (sameDayOfMonth || bothLastDayOfMonth) {
            // 乘以正负号
            months = months.multiply(sign);
            return months;
        }

        // 如果不满足上述条件，计算小数部分

        /* 计算秒数 */
        long secondOfBegin = begin.toLocalTime().toSecondOfDay();
        long secondOfEnd = end.toLocalTime().toSecondOfDay();
        long secondsByHours = secondOfEnd - secondOfBegin;

        /* 计算天数，然后换算成秒 */
        long dayOfBegin = begin.getDayOfMonth();
        long dayOfEnd = end.getDayOfMonth();
        // 相差天数整数部分
        long days = dayOfEnd - dayOfBegin;
        // 换算成秒
        OraDecimal secondsByDays = OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL);
        // 汇总
        OraDecimal seconds = OraDecimal.valueOf(secondsByHours).add(secondsByDays);

        /* 根据Oracle数据库规则，一个月强行视为31天。这里用剩余时间的总秒数，除以一个月的总秒数 */
        // 非整数部分的月份数
        OraDecimal monthsFraction = seconds.divide(AviatorOracleConstants.SECONDS_OF_MONTH);

        /* 汇总计算结果并返回 */
        // 汇总相加后乘以正负号
        months = months.add(monthsFraction).multiply(sign);
        return months;
    }

    /**
     * 对象相等比较逻辑，增强对数字类型和 Null 的支持。
     *
     * @param o1 第一个对象
     * @param o2 第二个对象
     * @return 是否逻辑相等
     */
    private static boolean isEqualForDecode(Object o1, Object o2) {
        /* 处理 Null：如果两者皆为 Null，认为相等；如果其一为 Null，不相等 */
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }

        /* 引用相等判断 */
        if (o1 == o2) {
            return true;
        }


        /* 数值类型增强比较：仅处理 OraDecimal, Integer, Long */
        if (o1 instanceof Number && o2 instanceof Number) {
            Number n1 = (Number) o1;
            Number n2 = (Number) o2;
            OraDecimal b1 = toOraDecimal(n1);
            OraDecimal b2 = toOraDecimal(n2);
            // 使用 compareTo 而非 equals，因为 compareTo 忽略 Scale（例如 1.0 等于 1.00）
            return b1.compareTo(b2) == 0;
        }

        /* 4. 其他类型使用标准 equals 比较 */
        return Objects.equals(o1, o2);
    }

    /**
     * 将 Number 统一转换为 OraDecimal 以便进行高精度比较。
     *
     * @param n 数字对象（仅限 Integer, Long, OraDecimal）
     * @return 对应的 OraDecimal 对象
     */
    private static OraDecimal toOraDecimal(Number n) {
        if (n instanceof OraDecimal) {
            return (OraDecimal) n;
        }
        // 对于 Integer 和 Long，通过字符串构造以最大程度保证精度准确性。
        if (n instanceof Integer || n instanceof Long) {
            return new OraDecimal(n.toString());
        }
        throw new IllegalArgumentException("不支持的数字类型: " + n.getClass().getName() + "，仅支持 Integer, Long 和 OraDecimal");
    }

    /**
     * 是否是所在月份的最后1填
     */
    private static boolean isLastDayOfMonth(ZonedDateTime date) {
        return date.getDayOfMonth() == date.toLocalDate().lengthOfMonth();
    }
}
