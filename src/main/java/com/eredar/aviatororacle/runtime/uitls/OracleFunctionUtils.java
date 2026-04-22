package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.constants.AviatorOracleConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

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
        // 至少需要 3 个参数。
        if (args == null || args.length < 3) {
            int argsLength = 0;
            if (args != null) {
                argsLength = args.length;
            }
            throw new IllegalArgumentException("decode方法入参数量只有" + argsLength + "个，少于3个");
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
     * 取整数，小数位直接舍去
     */
    public static Number floor(Object n) {
        if (n == null) {
            return null;
        }
        if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte
                || n instanceof BigInteger) {
            // 整数类型，直接返回
            return (Number) n;
        }
        if (n instanceof OraDecimal || n instanceof BigDecimal || n instanceof Double || n instanceof Float) {
            return OraDecimal.valueOf((Number) n).setScale(0, RoundingMode.FLOOR);
        }
        throw new IllegalArgumentException(String.format("floor方法不能传入[%s]类型", n.getClass().getName()));
    }

    /**
     * 模拟 Oracle {@code CEIL(n)}：返回大于或等于 {@code n} 的最小整数（向正无穷方向取整）。
     * <p>{@link java.math.RoundingMode#CEILING} 一致：正数小数部分进位，负数向零靠近（例如 {@code ceil(-2.1) = -2}）。
     *
     * @param n 目标数字；为 {@code null} 时返回 {@code null}
     * @return 上取整后的数字
     */
    public static Number ceil(Object n) {
        if (n == null) {
            return null;
        }
        if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte
                || n instanceof BigInteger) {
            // 整数类型，直接返回
            return (Number) n;
        }
        if (n instanceof OraDecimal || n instanceof BigDecimal || n instanceof Double || n instanceof Float) {
            return OraDecimal.valueOf((Number) n).setScale(0, RoundingMode.CEILING);
        }
        throw new IllegalArgumentException(String.format("ceil方法不能传入[%s]类型", n.getClass().getName()));
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
        if (number == null) {
            return null;
        }

        if (newScale == null) {
            // 与 Oracle 的 round 方法不同，第2个入参不允许为null
            throw new IllegalArgumentException("[newScale] cannot be null");
        }

        /* 校验并与处理 newScale */
        // newScale >= 40，直接返回0
        boolean isGE_40 = false;
        // newScale <= -40，直接返回 n 本身
        boolean isLE_NEG40 = false;
        // scale = newScale的int类型
        int scale = 0;

        if (newScale instanceof Long || newScale instanceof Integer || newScale instanceof Short
                || newScale instanceof Byte || newScale instanceof Double || newScale instanceof Float) {
            long l = newScale.longValue();
            if (l >= 40) {
                isGE_40 = true;
            } else if (l <= -40) {
                isLE_NEG40 = true;
            } else {
                // 强行转换成整数，丢弃小数部分
                scale = (int) l;
            }
        } else if (newScale instanceof BigInteger) {
            BigInteger bi = (BigInteger) newScale;
            if (bi.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_INTEGER_POS) >= 0) {
                // newScale >= 40
                isGE_40 = true;
            } else if (bi.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_INTEGER_NEG) <= 0) {
                // newScale <= -40
                isLE_NEG40 = true;
            } else {
                scale = bi.intValue();
            }
        } else if (newScale instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) newScale;
            if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_DECIMAL_POS) >= 0) {
                // newScale >= 40
                isGE_40 = true;
            } else if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_DECIMAL_NEG) <= 0) {
                // newScale <= -40
                isLE_NEG40 = true;
            } else {
                // 强行转换成整数，丢弃小数部分
                scale = decimal.intValue();
            }
        } else if (newScale instanceof OraDecimal) {
            OraDecimal decimal = (OraDecimal) newScale;
            if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__ORA_DECIMAL_POS) >= 0) {
                // newScale >= 40
                isGE_40 = true;
            } else if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__ORA_DECIMAL_NEG) <= 0) {
                // newScale <= -40
                isLE_NEG40 = true;
            } else {
                // 强行转换成整数，丢弃小数部分
                scale = decimal.intValue();
            }
        } else {
            throw new IllegalArgumentException(String.format("newScale 是未知类型[%s]", newScale.getClass().getName()));
        }

        /* newScale 超过极限值，直接返回对应的值 */
        if (isGE_40) {
            // newScale >= 40，返回 number，不需要处理
            return number;
        } else if (isLE_NEG40) {
            // newScale <= -40，返回 0
            return 0;
        }

        /* newScale 处于合理范围内，正常计算 */
        // OraDecimal：直接走 Oracle NUMBER 舍入与规范化（setScale 默认为 HALF_UP）
        if (number instanceof OraDecimal) {
            return ((OraDecimal) number).setScale(scale);
        }
        // BigDecimal：必须转为 OraDecimal 再计算
        if (number instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) number).setScale(scale);
        }
        // Byte / Short / Integer / Long
        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
            if (scale >= 0) {
                return number;
            }
            return OraDecimal.valueOf(number.longValue()).setScale(scale, RoundingMode.HALF_UP);
        }
        if (number instanceof BigInteger) {
            // 整数无小数部分，非负 newScale 不改变数值
            if (scale >= 0) {
                return number;
            }
            return new OraDecimal((BigInteger) number).setScale(scale, RoundingMode.HALF_UP);
        }

        /* 其余 Number 类型：统一走 OraDecimal */
        return OraDecimal.valueOf(number).setScale(scale);
    }

    /**
     * 模拟 Oracle {@code TRUNC(number)}：向零方向截断，等价于 {@link #trunc(Number, Number) truncDate(n, 0)}。
     * <p>与 {@link #floor(Object) floor} 的区别：{@code floor} 向负无穷方向取整，而 {@code truncDate} 向零方向截断。
     * 例如 {@code truncDate(-2.9) = -2}，而 {@code floor(-2.9) = -3}。
     *
     * @param n 待截断的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code n} 本身
     */
    public static Number trunc(Number n) {
        return trunc(n, 0);
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
        if (number == null) {
            return null;
        }

        if (newScale == null) {
            /* 与 Oracle TRUNC 行为对齐，第 2 个入参不允许为 null */
            throw new IllegalArgumentException("[newScale] cannot be null");
        }

        /* ---- 解析 newScale 为 int，同时判断极限值 ---- */
        // newScale >= 40：精度已超过 Oracle NUMBER 最大有效位数，数值无需变化
        boolean isGE_40 = false;
        // newScale <= -40：截断位数已超过所有有效数字，结果为 0
        boolean isLE_NEG40 = false;
        // 正常范围内的 scale 整数值
        int scale = 0;

        if (newScale instanceof Long || newScale instanceof Integer || newScale instanceof Short
                || newScale instanceof Byte || newScale instanceof Double || newScale instanceof Float) {
            long l = newScale.longValue();
            if (l >= 40) {
                isGE_40 = true;
            } else if (l <= -40) {
                isLE_NEG40 = true;
            } else {
                /* Double/Float 的小数部分按 Oracle 行为直接丢弃 */
                scale = (int) l;
            }
        } else if (newScale instanceof BigInteger) {
            BigInteger bi = (BigInteger) newScale;
            if (bi.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_INTEGER_POS) >= 0) {
                isGE_40 = true;
            } else if (bi.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_INTEGER_NEG) <= 0) {
                isLE_NEG40 = true;
            } else {
                scale = bi.intValue();
            }
        } else if (newScale instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) newScale;
            if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_DECIMAL_POS) >= 0) {
                isGE_40 = true;
            } else if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__BIG_DECIMAL_NEG) <= 0) {
                isLE_NEG40 = true;
            } else {
                /* BigDecimal 的小数部分按 Oracle 行为直接丢弃 */
                scale = decimal.intValue();
            }
        } else if (newScale instanceof OraDecimal) {
            OraDecimal decimal = (OraDecimal) newScale;
            if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__ORA_DECIMAL_POS) >= 0) {
                isGE_40 = true;
            } else if (decimal.compareTo(AviatorOracleConstants.ROUND_SCALE__ORA_DECIMAL_NEG) <= 0) {
                isLE_NEG40 = true;
            } else {
                /* OraDecimal 的小数部分按 Oracle 行为直接丢弃 */
                scale = decimal.intValue();
            }
        } else {
            throw new IllegalArgumentException(String.format("newScale 是未知类型[%s]", newScale.getClass().getName()));
        }

        /* ---- 极限值快速返回 ---- */
        if (isGE_40) {
            /* newScale >= 40，精度足够，数值无需截断，直接返回原值 */
            return number;
        } else if (isLE_NEG40) {
            /* newScale <= -40，全部有效数字均被截断，结果为 0 */
            return 0;
        }

        /* ---- 正常范围内，使用 RoundingMode.DOWN 向零截断 ---- */

        // Long / Integer / Short / Byte：整数类型无小数部分
        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
            if (scale >= 0) {
                /* 保留位数 >= 0，整数本身无小数，直接返回 */
                return number;
            }
            /* scale < 0：对整数部分按位截断（向零，非四舍五入） */
            return OraDecimal.valueOf(number.longValue()).setScale(scale, RoundingMode.DOWN);
        }

        // BigInteger：同整数类型逻辑
        if (number instanceof BigInteger) {
            if (scale >= 0) {
                return number;
            }
            return new OraDecimal((BigInteger) number).setScale(scale, RoundingMode.DOWN);
        }

        // Double：先通过 OraDecimal.valueOf 精确表示，再截断
        if (number instanceof Double) {
            return OraDecimal.valueOf(number).setScale(scale, RoundingMode.DOWN);
        }

        // BigDecimal：包装为 OraDecimal 后截断
        if (number instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) number).setScale(scale, RoundingMode.DOWN);
        }

        // OraDecimal：直接截断
        if (number instanceof OraDecimal) {
            return ((OraDecimal) number).setScale(scale, RoundingMode.DOWN);
        }

        /* 其余未知 Number 子类：统一先转为 OraDecimal 再截断 */
        return OraDecimal.valueOf(number).setScale(scale, RoundingMode.DOWN);
    }

    /**
     * 对象相等比较逻辑，增强对数字类型和 Null 的支持。
     * <p>比对2个不支持的类型，会抛出异常。</p>
     *
     * @param o1 第一个对象
     * @param o2 第二个对象
     * @return 是否相等
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

        /* 数值类型增强比较 */
        if (o1 instanceof Number && o2 instanceof Number) {
            Number n1 = (Number) o1;
            Number n2 = (Number) o2;
            OraDecimal b1 = OraDecimal.valueOf(n1);
            OraDecimal b2 = OraDecimal.valueOf(n2);
            // 使用 compareTo 而非 equals，因为 compareTo 忽略 Scale（例如 1.0 等于 1.00）
            return b1.compareTo(b2) == 0;
        }

        /* Number 与 String: 转换成Number比较 */
        if (o1 instanceof Number && o2 instanceof String) {
            return numberEqualsString((Number) o1, (String) o2);
        }
        if (o1 instanceof String && o2 instanceof Number) {
            return numberEqualsString((Number) o2, (String) o1);
        }

        /* String、Boolean、Instant: 直接比较 */
        if (o1 instanceof String && o2 instanceof String
                || o1 instanceof Boolean && o2 instanceof Boolean
                || o1 instanceof Instant && o2 instanceof Instant) {
            return Objects.equals(o1, o2);
        }

        throw new IllegalArgumentException(String.format(
                "无法比较[%s]类型与[%s]类型",
                o1.getClass().getName(),
                o2.getClass().getName()
        ));
    }

    /**
     * 可解析为数字的字符串与 Number 是否数值相等
     */
    private static boolean numberEqualsString(Number n, String s) {
        try {
            OraDecimal on = OraDecimal.valueOf(n);
            OraDecimal os = new OraDecimal(s.trim());
            return on.compareTo(os) == 0;
        } catch (Throwable t) {
            throw new IllegalArgumentException(String.format("数字[%s]和字符串[%s]不能比较", n, s), t);
        }
    }
}
