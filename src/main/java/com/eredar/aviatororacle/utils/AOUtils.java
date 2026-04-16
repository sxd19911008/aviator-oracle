package com.eredar.aviatororacle.utils;


import com.eredar.aviatororacle.number.OraDecimal;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class AOUtils {

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串是否非空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 驼峰命名转下划线大写
     * <p>例：userName -> USER_NAME
     */
    public static String humpToUpper(String str) {
        if (isBlank(str)) return str;
        return str.replaceAll("(\\p{Upper})", "_$1").toUpperCase();
    }

    /**
     * 驼峰命名转下划线大写
     * 例：userName -> user_name
     */
    public static String humpToLower(String str) {
        if (isBlank(str)) return str;
        return str.replaceAll("(\\p{Upper})", "_$1").toLowerCase();
    }

    /**
     * 比较两个字符串是否相等
     * <p>特殊规则：
     * <p>1. 如果两个字符串都为null，视为不相等
     * <p>2. 如果只有一个字符串为null，视为不相等
     * <p>3. 都不为null时，使用String.equals进行比对
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return true:相等; false:不相等
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) return false;
        return str1.equals(str2);
    }

    public static boolean notEquals(String str1, String str2) {
        return !equals(str1, str2);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 默认值
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }

    /**
     * Number 类型预处理，将带有小数的情况都转换成 {@code OraDecimal} 类型
     */
    public static Number preprocessNumber(Number n) {
        if (n instanceof Double || n instanceof Float) {
            return new OraDecimal(String.valueOf(n));
        } else if (n instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) n);
        } else {
            return n;
        }
    }
}
