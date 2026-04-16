package com.eredar.aviatororacle.runtime.object;

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
        return valueOf(this.toBigInt().and(((AOAviatorNumber) other).toBigInt()));
    }


    @Override
    protected AviatorObject innerBitOr(AviatorObject other) {
        return valueOf(this.toBigInt().or(((AOAviatorNumber) other).toBigInt()));
    }


    @Override
    protected AviatorObject innerBitXor(AviatorObject other) {
        return valueOf(this.toBigInt().xor(((AOAviatorNumber) other).toBigInt()));
    }


    @Override
    protected AviatorObject innerShiftLeft(AviatorObject other) {
        this.ensureLong(other);
        return valueOf(this.toBigInt().shiftLeft((int) ((AOAviatorNumber) other).longValue()));
    }


    @Override
    protected AviatorObject innerShiftRight(AviatorObject other) {
        this.ensureLong(other);
        return valueOf(this.toBigInt().shiftRight((int) ((AOAviatorNumber) other).longValue()));
    }


    @Override
    protected AviatorObject innerUnsignedShiftRight(AviatorObject other) {
        return this.innerShiftRight(other);
    }


    @Override
    public AviatorType getAviatorType() {
        return AviatorType.BigInt;
    }

}
