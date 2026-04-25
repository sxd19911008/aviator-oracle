package com.eredar.aviatororacle.object;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorType;

import java.math.BigInteger;
import java.util.Map;


/**
 * AviatorOracle Big Integer
 */
public class AOAviatorBigInt extends AOAviatorLong {

    private static final long serialVersionUID = 3431817954508387226L;

    private static class BigIntCache {
        private BigIntCache() {
        }

        static final AOAviatorBigInt[] cache = new AOAviatorBigInt[256];

        static {
            for (long i = 0; i < cache.length; i++) {
                cache[(int) i] = new AOAviatorBigInt(BigInteger.valueOf(i - 128));
            }
        }
    }


    @Override
    public Object getValue(Map<String, Object> env) {
        return this.number;
    }


    @Override
    public long longValue() {
        return this.number.longValue();
    }


    public AOAviatorBigInt(Number number) {
        super(number);
    }


    public static AOAviatorBigInt valueOf(BigInteger v) {
        return new AOAviatorBigInt(v);
    }


    public static AOAviatorBigInt valueOf(String v) {
        return new AOAviatorBigInt(new BigInteger(v));
    }


    public static AOAviatorBigInt valueOf(long l) {
        final int offset = 128;
        if (l >= -128 && l <= 127) {
            return BigIntCache.cache[(int) l + offset];
        }
        return valueOf(BigInteger.valueOf(l));
    }


    @Override
    public AviatorObject neg(Map<String, Object> env) {
        return valueOf(this.toBigInt().negate());
    }


    @Override
    public AviatorObject innerSub(Map<String, Object> env, AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case Decimal:
            case Double:
                return AOAviatorDecimal.valueOf(this.toDecimal().subtract(other.toDecimal()));
            default:
                return valueOf(this.toBigInt().subtract(other.toBigInt()));
        }
    }


    @Override
    public AviatorObject innerMult(Map<String, Object> env, AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case Decimal:
            case Double:
                return AOAviatorDecimal.valueOf(this.toDecimal().multiply(other.toDecimal()));
            default:
                return valueOf(this.toBigInt().multiply(other.toBigInt()));
        }
    }


    @Override
    public AviatorObject innerMod(Map<String, Object> env, AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case Decimal:
            case Double:
                return AOAviatorDecimal.valueOf(this.toDecimal().remainder(other.toDecimal()));
            default:
                return valueOf(this.toBigInt().mod(other.toBigInt()));
        }
    }


    @Override
    public AviatorObject innerDiv(Map<String, Object> env, AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case Decimal:
            case Double:
                return AOAviatorDecimal.valueOf(this.toDecimal().divide(other.toDecimal()));
            default:
                return valueOf(this.toBigInt().divide(other.toBigInt()));
        }
    }


    @Override
    public AOAviatorNumber innerAdd(Map<String, Object> env, AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case Decimal:
            case Double:
                return AOAviatorDecimal.valueOf(this.toDecimal().add(other.toDecimal()));
            default:
                return valueOf(this.toBigInt().add(other.toBigInt()));
        }
    }


    @Override
    public int innerCompare(Map<String, Object> env, AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case Decimal:
            case Double:
                return this.toDecimal().compareTo(other.toDecimal());
            default:
                return this.toBigInt().compareTo(other.toBigInt());
        }
    }


    @Override
    protected AviatorObject innerBitAnd(AviatorObject other) {
        BigInteger otherValue = this.getOtherBigIntegerValue(other);
        return valueOf(this.toBigInt().and(otherValue));
    }


    @Override
    protected AviatorObject innerBitOr(AviatorObject other) {
        BigInteger otherValue = this.getOtherBigIntegerValue(other);
        return valueOf(this.toBigInt().or(otherValue));
    }


    @Override
    protected AviatorObject innerBitXor(AviatorObject other) {
        BigInteger otherValue = this.getOtherBigIntegerValue(other);
        return valueOf(this.toBigInt().xor(otherValue));
    }


    @Override
    protected AviatorObject innerShiftLeft(AviatorObject other) {
        this.ensureLong(other);
        int otherValue = Math.toIntExact(this.getOtherLongValue(other));
        return valueOf(this.toBigInt().shiftLeft(otherValue));
    }


    @Override
    protected AviatorObject innerShiftRight(AviatorObject other) {
        this.ensureLong(other);
        int otherValue = Math.toIntExact(this.getOtherLongValue(other));
        return valueOf(this.toBigInt().shiftRight(otherValue));
    }


    @Override
    protected AviatorObject innerUnsignedShiftRight(AviatorObject other) {
        // BigInteger 没有无符号右移API，报错防止误导开发者，强制开发者使用普通右移操作
        throw new ExpressionRuntimeException("BigInteger不能执行[>>>]运算");
    }

    @Override
    public AviatorObject bitNot(final Map<String, Object> env) {
        BigInteger bi = (BigInteger) this.number;
        return AOAviatorBigInt.valueOf(bi.not());
    }


    @Override
    public AviatorType getAviatorType() {
        return AviatorType.BigInt;
    }

    protected BigInteger getOtherBigIntegerValue(AviatorObject other) {
        if (other instanceof AviatorNumber) {
            return ((AviatorNumber) other).toBigInt();
        } else if (other instanceof AOAviatorNumber) {
            return ((AOAviatorNumber) other).toBigInt();
        } else {
            throw new ExpressionRuntimeException(String.format("Unknown AviatorObject type [%s]", AORuntimeUtils.getClass(other)));
        }
    }
}
