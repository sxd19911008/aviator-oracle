package com.eredar.aviatororacle.runtime.uitls.oracle;

import com.eredar.aviatororacle.number.OraDecimal;

import java.time.Instant;
import java.util.Objects;

/**
 * Oracle 数据库 条件函数
 */
public class OracleConditionalFunctionUtils {

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
