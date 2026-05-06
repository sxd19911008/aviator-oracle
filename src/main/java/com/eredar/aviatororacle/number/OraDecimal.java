package com.eredar.aviatororacle.number;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class OraDecimal extends Number implements Comparable<OraDecimal> {

    private final BigDecimal decimal;

    public static final OraDecimal ZERO = new OraDecimal(BigDecimal.ZERO);
    public static final OraDecimal ONE = new OraDecimal(BigDecimal.ONE);

    /**
     * 用于 {@link #pow(OraDecimal)} 的中间计算精度上下文。
     * <p>保留 100 位有效数字，为 {@code BigDecimalMath.pow} 的 ln/exp 迭代提供足够精度余量，
     */
    private static final MathContext POW_MATH_CONTEXT;

    static {
        POW_MATH_CONTEXT = new MathContext(100, RoundingMode.DOWN);
    }

    public OraDecimal(int val) {
        this(new BigDecimal(val));
    }

    public OraDecimal(long val) {
        this(new BigDecimal(val));
    }

    public OraDecimal(BigInteger bigInteger) {
        this(new BigDecimal(bigInteger));
    }

    public OraDecimal(BigInteger bigInteger, int scale) {
        this(new BigDecimal(bigInteger, scale));
    }

    public OraDecimal(String val) {
        this(new BigDecimal(val));
    }

    public OraDecimal(BigDecimal decimal) {
        this.decimal = this.oracleDecimal(decimal);
    }

    public static OraDecimal valueOf(long unscaledVal, int scale) {
        BigDecimal bigDecimal = BigDecimal.valueOf(unscaledVal, scale);
        return new OraDecimal(bigDecimal);
    }

    public static OraDecimal valueOf(long val) {
        BigDecimal bigDecimal = BigDecimal.valueOf(val);
        return new OraDecimal(bigDecimal);
    }

    public static OraDecimal valueOf(double val) {
        BigDecimal bigDecimal = BigDecimal.valueOf(val);
        return new OraDecimal(bigDecimal);
    }

    /**
     * 将 {@link Number} 转换为 {@code OraDecimal}
     *
     * @param n 数字对象
     * @return 对应的 {@code OraDecimal} 对象
     */
    public static OraDecimal valueOf(Number n) {
        if (n == null) {
            return null;
        } else if (n instanceof OraDecimal) {
            return (OraDecimal) n;
        } else if (n instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) n);
        } else if (n instanceof Byte || n instanceof Short) {
            return OraDecimal.valueOf(n.longValue());
        } else {
            return new OraDecimal(String.valueOf(n));
        }
    }

    public OraDecimal add(OraDecimal augend) {
        BigDecimal result = this.decimal.add(augend.getDecimal());
        return new OraDecimal(result);
    }

    public OraDecimal subtract(OraDecimal subtrahend) {
        BigDecimal result = this.decimal.subtract(subtrahend.getDecimal());
        return new OraDecimal(result);
    }

    public OraDecimal multiply(OraDecimal multiplicand) {
        BigDecimal result = this.decimal.multiply(multiplicand.getDecimal());
        return new OraDecimal(result);
    }

    /**
     * 对应 Oracle 数据库中，未指定精度的 number 类型变量来承载除法结果
     */
    public OraDecimal divide(OraDecimal divisor) {
        // 保留100位小数，但是舍弃多余小数，方便后面的 oracleDecimal 计算
        BigDecimal result = this.decimal.divide(divisor.getDecimal(), 100, RoundingMode.DOWN);
        return new OraDecimal(result);
    }

    /**
     * 对应 Oracle 数据库中，指定精度的变量来承载除法结果。必定四舍五入。
     * 示例：number(12,2)，保留2位小数。
     */
    public OraDecimal divide(OraDecimal divisor, int scale) {
        return this.divide(divisor).setScale(scale);
    }

    /**
     * Returns a {@code OraDecimal} whose value is
     * <tt>(this<sup>n</sup>)</tt>, The power is computed exactly, to
     * unlimited precision.
     *
     * <p>The parameter {@code n} must be in the range 0 through
     * 999999999, inclusive.  {@code ZERO.pow(0)} returns {@link
     * #ONE}.
     *
     * Note that future releases may expand the allowable exponent
     * range of this method.
     *
     * @param  n power to raise this {@code OraDecimal} to.
     * @return <tt>this<sup>n</sup></tt>
     * @throws ArithmeticException if {@code n} is out of range.
     */
    public OraDecimal pow(int n) {
        return new OraDecimal(this.decimal.pow(n));
    }

    /**
     * 返回 {@code this} 的 {@code exponent} 次幂，支持非整数指数。
     *
     * @param exponent 指数
     * @return {@code this ^ exponent}
     */
    public OraDecimal pow(OraDecimal exponent) {
        BigDecimal result = BigDecimalMath.pow(this.decimal, exponent.getDecimal(), POW_MATH_CONTEXT);
        return new OraDecimal(result);
    }

    /**
     * 取余数
     * <p>this % divisor
     */
    public OraDecimal remainder(OraDecimal divisor) {
        return new OraDecimal(this.decimal.remainder(divisor.getDecimal()));

    }

    /**
     * 取绝对值
     */
    public OraDecimal abs() {
        return new OraDecimal(this.decimal.abs());
    }

    /**
     * 返回其数值乘以 -1 的结果
     */
    public OraDecimal negate() {
        return new OraDecimal(this.decimal.negate());
    }

    public OraDecimal[] divideAndRemainder(OraDecimal divisor) {
        BigDecimal[] decimals = this.decimal.divideAndRemainder(divisor.getDecimal());
        return new OraDecimal[]{new OraDecimal(decimals[0]), new OraDecimal(decimals[1])};
    }

    /**
     * 设置精度，默认四舍五入
     *
     * @param newScale 新精度
     * @return 新的OraDecimal对象
     */
    public OraDecimal setScale(int newScale) {
        return this.setScale(newScale, RoundingMode.HALF_UP);
    }

    /**
     * 设置精度
     *
     * @param newScale     新精度
     * @param roundingMode 舍弃规则
     * @return 新的OraDecimal对象
     */
    public OraDecimal setScale(int newScale, RoundingMode roundingMode) {
        return new OraDecimal(this.decimal.setScale(newScale, roundingMode));
    }

    @Override
    public int compareTo(OraDecimal o) {
        return this.decimal.compareTo(o.getDecimal());
    }

    @Override
    public int intValue() {
        return this.decimal.intValue();
    }

    @Override
    public long longValue() {
        return this.decimal.longValue();
    }

    @Override
    public float floatValue() {
        return this.decimal.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.decimal.doubleValue();
    }

    public BigInteger toBigInteger() {
        return this.decimal.toBigInteger();
    }

    @Override
    public String toString() {
        return this.decimal.stripTrailingZeros().toPlainString();
    }

    /**
     * Converts this {@code OraDecimal} to an {@code int}, checking
     * for lost information.  If this {@code OraDecimal} has a
     * nonzero fractional part or is out of the possible range for an
     * {@code int} result then an {@code ArithmeticException} is
     * thrown.
     *
     * @return this {@code OraDecimal} converted to an {@code int}.
     * @throws ArithmeticException if {@code this} has a nonzero
     *         fractional part, or will not fit in an {@code int}.
     */
    public int intValueExact() {
        return this.decimal.intValueExact();
    }

    /**
     * Converts this {@code OraDecimal} to a {@code long}, checking
     * for lost information.  If this {@code OraDecimal} has a
     * nonzero fractional part or is out of the possible range for a
     * {@code long} result then an {@code ArithmeticException} is
     * thrown.
     *
     * @return this {@code OraDecimal} converted to a {@code long}.
     * @throws ArithmeticException if {@code this} has a nonzero
     *                             fractional part, or will not fit in a {@code long}.
     */
    public long longValueExact() {
        return this.decimal.longValueExact();
    }

    /**
     * Converts this {@code OraDecimal} to a {@code BigInteger},
     * checking for lost information.  An exception is thrown if this
     * {@code OraDecimal} has a nonzero fractional part.
     *
     * @return this {@code OraDecimal} converted to a {@code BigInteger}.
     * @throws ArithmeticException if {@code this} has a nonzero
     *         fractional part.
     */
    public BigInteger toBigIntegerExact() {
        return this.decimal.toBigIntegerExact();
    }

    /**
     * 模拟 Oracle 数据库 number 类型保留小数位的逻辑
     * <p>1. Oracle 数字类型为 number，不指定精度，则默认整数+小数共40位</p>
     * <p>2. 如果整数为0，则整数不占位，小数部分可以达到40位</p>
     * <p>3. 整数部分占位一定是偶数。比如整数3位或4位，小数部分最多都是36位</p>
     * <p>4. 如果整数部分为0，小数位开头的所有0都不算在40位中</p>
     * <p>5. 负号不占位，不影响以上判断，直接去掉负号当正数判断即可</p>
     *
     * @return 按照 Oracle 数据库 number 类型保留小数位后得到的数字
     */
    private BigDecimal oracleDecimal(BigDecimal decimal) {
        /* 根据小数点拆分，得到整数部分 */
        decimal = decimal.stripTrailingZeros();
        String[] arr = decimal.toPlainString().split("\\.");
        // 没有小数部分，不用计算
        if (arr.length == 1) {
            return decimal;
        }

        /* 计算整数部分长度 */
        String integerStr = arr[0];
        if (integerStr.contains("-")) { // 去除负号，负号没有用处
            integerStr = integerStr.replace("-", "");
        }
        int integerLength = integerStr.length(); // 整数部分长度
        // 整数部分为0时，整数部分长度设置为0
        if ("0".equals(integerStr)) {
            integerLength = 0;
        }
        // 整数部分长度为奇数时，长度加1
        if (integerLength % 2 == 1) {
            integerLength += 1;
        }
        /*
         * 整数部分长度加小数部分长度本身就小于等于40，不用计算
         * 小数位数是奇数不影响这里的结果，因为整数位数一定是偶数，
         * 所以仅仅小数位数为奇数不会出现2个奇数相加等于40的情况。
         */
        if (integerLength + arr[1].length() <= 40) {
            return decimal;
        }

        /* 计算小数部分长度 */
        // 小数部分开头0的个数
        int leadingZeroNum = 0;
        // 如果整数位为0
        if (integerLength == 0) {
            // 小数部分
            String decimalsStr = arr[1];
            // 由于开头 stripTrailingZeros 方法已经去除了多余的0，所以这里小数部分不可能全是0
            leadingZeroNum = this.countLeadingZeros(decimalsStr);
            // 长度为奇数时，长度-1
            if (leadingZeroNum % 2 == 1) {
                leadingZeroNum -= 1;
            }
        }
        // 最终计算出小数位位数
        int decimalLength = 40 - integerLength + leadingZeroNum;
        /* 重新保留小数位数，返回结果 */
        return decimal.setScale(decimalLength, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 获取字符串开头0的个数
     */
    private int countLeadingZeros(String decimalsStr) {
        int count = 0;
        if (decimalsStr == null || decimalsStr.isEmpty()) {
            return count;
        }
        // 遍历字符串中的每个字符
        for (int i = 0; i < decimalsStr.length(); i++) {
            // 如果当前字符是'0'，计数器加1
            if (decimalsStr.charAt(i) == '0') {
                count++;
            } else {
                // 遇到第一个不是0的字符，停止遍历
                break;
            }
        }

        return count;
    }

    /**
     * Returns the signum function of this {@code OraDecimal}.
     *
     * @return -1, 0, or 1 as the value of this {@code OraDecimal}
     *         is negative, zero, or positive.
     */
    public int signum() {
        return this.decimal.signum();
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OraDecimal)) return false;
        if (obj == this) return true;
        OraDecimal other = (OraDecimal) obj;
        return this.decimal.equals(other.decimal);
    }

    @Override
    public int hashCode() {
        // 两个 equals() 返回 true 的对象必须有相同的 hashCode
        return this.decimal.hashCode();
    }

    /**
     * 是否是整数
     *
     * @return true-是整数；false-不是
     */
    public boolean isInteger() {
        /*
         * 如果 scale <= 0，它在物理存储上就是一个整数
         * 由于 decimal 必定执行过 stripTrailingZeros，所以不需要重复执行。
         * 科学计数法会导致 scale 为负数
         * 例如 10000，scale 为 0；stripTrailingZeros 之后为 1E+4，scale 为 -4
         */
        return this.decimal.scale() <= 0;
    }
}
