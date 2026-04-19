package com.eredar.aviatororacle.runtime.uitls;

import com.eredar.aviatororacle.runtime.constants.AviatorOracleConstants;
import com.eredar.aviatororacle.number.OraDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class OracleFunctionUtils {


    /**
     * 模拟 Oracle 数据库: Date对象 + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return Instant类型的日期对象
     */
    public static Instant instantPlusDays(Instant date, Number days) {
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
    public static Instant instantMinusDays(Instant date, Number days) {
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
            return OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
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
    public static OraDecimal daysBetween(Instant beginDate, Instant endDate) {
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
    public static OraDecimal floor(Object n) {
        if (n == null) {
            return null;
        } else if (n instanceof Number) {
            OraDecimal oraDecimal = OraDecimal.valueOf((Number) n);
            return oraDecimal.setScale(0, RoundingMode.FLOOR);
        } else {
            throw new IllegalArgumentException(String.format("floor方法不能传入[%s]类型", n.getClass().getName()));
        }
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
