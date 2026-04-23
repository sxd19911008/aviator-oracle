package com.eredar.aviatororacle.runtime.uitls.oracle;

import com.eredar.aviatororacle.number.OraDecimal;

import java.math.RoundingMode;

public class OracleFunctionUtils {

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
        return OracleConditionalFunctionUtils.decode(args);
    }

    /**
     * 取整数，小数位直接舍去
     */
    public static Number floor(Number n) {
        return OracleNumberFunctionUtils.floor(n);
    }

    /**
     * 模拟 Oracle {@code CEIL(n)}：返回大于或等于 {@code n} 的最小整数（向正无穷方向取整）。
     * <p>{@link java.math.RoundingMode#CEILING} 一致：正数小数部分进位，负数向零靠近（例如 {@code ceil(-2.1) = -2}）。
     *
     * @param n 目标数字；为 {@code null} 时返回 {@code null}
     * @return 上取整后的数字
     */
    public static Number ceil(Number n) {
        return OracleNumberFunctionUtils.ceil(n);
    }

    /**
     * 模拟 Oracle {@code ROUND(number)}：四舍五入保留整数，等价于 {@link #round(Number, Number) round(n, 0)}。
     *
     * @param n 待舍入的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    public static Number round(Number n) {
        return round(n, 0);
    }

    /**
     * 模拟 Oracle {@code ROUND(number, integer)}：按指定位数四舍五入（{@link RoundingMode#HALF_UP}）。
     * <p>{@code newScale > 0} 表示保留的小数位数；
     * <p>{@code newScale = 0} 表示保留整数；
     * <p>{@code newScale < 0} 表示在小数点左侧按数量级舍入（如 -1 表示十位）。
     *
     * @param number   待舍入的值；为 {@code null} 时返回 {@code null}
     * @param newScale 目标标度（可为负）
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    public static Number round(Number number, Number newScale) {
        return OracleNumberFunctionUtils.round(number, newScale);
    }

    /**
     * 模拟 Oracle {@code TRUNC(number)}：向零方向截断，等价于 {@link #trunc(Number, Number) truncDate(n, 0)}。
     * <p>与 {@link #floor(Object) floor} 的区别：{@code floor} 向负无穷方向取整，而 {@code truncDate} 向零方向截断。
     * 例如 {@code truncDate(-2.9) = -2}，而 {@code floor(-2.9) = -3}。
     *
     * @param number 待截断的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code n} 本身
     */
    public static Number trunc(Number number) {
        return trunc(number, 0);
    }

    /**
     * 模拟 Oracle {@code TRUNC(number, integer)}：按指定位数向零方向截断（{@link RoundingMode#DOWN}）。
     * <p>{@code newScale > 0} 表示保留的小数位数，多余部分直接丢弃；
     * <p>{@code newScale = 0} 表示仅保留整数部分；
     * <p>{@code newScale < 0} 表示在小数点左侧按数量级截断（如 -1 对十位截断）。
     * <p>示例：
     * <pre>
     *   truncDate(15.79)      = 15
     *   truncDate(15.79,  1)  = 15.7
     *   truncDate(15.79, -1)  = 10
     *   truncDate(-2.9,   0)  = -2   （向零，非向负无穷）
     * </pre>
     *
     * @param number   待截断的值；为 {@code null} 时返回 {@code null}
     * @param newScale 目标标度（可为负）；支持 Long、Integer、Double、BigInteger、BigDecimal、OraDecimal
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    public static Number trunc(Number number, Number newScale) {
        return OracleNumberFunctionUtils.truncNumber(number, newScale);
    }
}
