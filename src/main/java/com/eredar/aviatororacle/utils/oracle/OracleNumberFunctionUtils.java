package com.eredar.aviatororacle.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.constants.AviatorOracleConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Oracle 数据库 操作 {@code NUMBER} 的函数
 */
public class OracleNumberFunctionUtils {

    private static final String NEW_SCALE_RES_THIS = "this";
    private static final String NEW_SCALE_RES_ZERO = "zero";

    /**
     * 取整数，小数位直接舍去
     */
    protected static Number floor(Number n) {
        if (n == null) {
            return null;
        }
        if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte
                || n instanceof BigInteger) {
            // 整数类型，直接返回
            return n;
        }
        if (n instanceof OraDecimal || n instanceof BigDecimal || n instanceof Double || n instanceof Float) {
            return OraDecimal.valueOf(n).setScale(0, RoundingMode.FLOOR);
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
    protected static Number ceil(Number n) {
        if (n == null) {
            return null;
        }
        if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte
                || n instanceof BigInteger) {
            // 整数类型，直接返回
            return n;
        }
        if (n instanceof OraDecimal || n instanceof BigDecimal || n instanceof Double || n instanceof Float) {
            return OraDecimal.valueOf(n).setScale(0, RoundingMode.CEILING);
        }
        throw new IllegalArgumentException(String.format("ceil方法不能传入[%s]类型", n.getClass().getName()));
    }

    /**
     * 模拟 Oracle {@code ROUND(number)}：四舍五入保留整数，等价于 {@link #round(Number, Number) round(n, 0)}。
     *
     * @param n 待舍入的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code number} 本身
     */
    protected static Number round(Number n) {
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
    protected static Number round(Number number, Number newScale) {
        if (number == null) {
            return null;
        }

        if (newScale == null) {
            // 与 Oracle 的 round 方法不同，第2个入参不允许为null
            throw new IllegalArgumentException("[newScale] cannot be null");
        }

        /* 校验并与处理 newScale */
        String scaleRes = resolveNewScale(newScale);

        // 极限值快速返回
        if (NEW_SCALE_RES_THIS.equals(scaleRes)) {
            // newScale >= 40，精度足够，数值无需截断，直接返回原值
            return number;
        } else if (NEW_SCALE_RES_ZERO.equals(scaleRes)) {
            // newScale <= -40，全部有效数字均被截断，结果为 0
            return 0;
        }

        // 需要计算，得到 int 类型的 scale
        int scale = Integer.parseInt(scaleRes);

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
            return OraDecimal.valueOf(number.longValue()).setScale(scale);
        }
        if (number instanceof BigInteger) {
            // 整数无小数部分，非负 newScale 不改变数值
            if (scale >= 0) {
                return number;
            }
            return new OraDecimal((BigInteger) number).setScale(scale);
        }

        /* 其余 Number 类型：统一走 OraDecimal */
        return OraDecimal.valueOf(number).setScale(scale);
    }

    /**
     * 模拟 Oracle {@code TRUNC(number)}：向零方向截断，等价于 {@link #truncNumber(Number, Number) truncDate(n, 0)}。
     * <p>与 {@link #floor(Number) floor} 的区别：{@code floor} 向负无穷方向取整，而 {@code truncDate} 向零方向截断。
     * <p>例如 {@code truncDate(-2.9) = -2}，而 {@code floor(-2.9) = -3}。
     *
     * @param number 待截断的 {@link Number}；为 {@code null} 时返回 {@code null}
     * @return 如果经过计算，一定返回 {@link OraDecimal} 类型；无需计算的场景返回 {@code n} 本身
     */
    protected static Number truncNumber(Number number) {
        return truncNumber(number, 0);
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
    protected static Number truncNumber(Number number, Number newScale) {
        if (number == null) {
            return null;
        }

        if (newScale == null) {
            /* 第 2 个入参不允许为 null */
            throw new IllegalArgumentException("[newScale] cannot be null");
        }

        /* 解析 newScale 为 int，同时判断极限值 */
        String scaleRes = resolveNewScale(newScale);

        // 极限值快速返回
        if (NEW_SCALE_RES_THIS.equals(scaleRes)) {
            // newScale >= 40，精度足够，数值无需截断，直接返回原值
            return number;
        } else if (NEW_SCALE_RES_ZERO.equals(scaleRes)) {
            // newScale <= -40，全部有效数字均被截断，结果为 0
            return 0;
        }

        // 需要计算，得到 int 类型的 scale
        int scale = Integer.parseInt(scaleRes);

        /* 正常范围内，使用 RoundingMode.DOWN 向零截断 */

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
     * 处理 {@code newScale}
     *
     * @param newScale 新精度
     * @return 处理结果，调用者需要根据结果判断接下来的动作
     */
    private static String resolveNewScale(Number newScale) {
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

        /* ---- 极限值快速返回 ---- */
        if (isGE_40) {
            // newScale >= 40，精度足够，数值无需截断，直接返回原值
            return NEW_SCALE_RES_THIS;
        } else if (isLE_NEG40) {
            // newScale <= -40，全部有效数字均被截断，结果为 0
            return NEW_SCALE_RES_ZERO;
        } else {
            // 需要计算
            return String.valueOf(scale);
        }
    }
}
