/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 **/
package com.eredar.aviatororacle.object;

import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.googlecode.aviator.exception.CompareNotSupportedException;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.runtime.type.*;
import com.googlecode.aviator.utils.TypeUtils;

import java.util.Map;


/**
 * AviatorOracle long type
 */
public class AOAviatorLong extends AOAviatorNumber {


    private static final long serialVersionUID = -3021194615848429190L;

    private static class LongCache {
        private LongCache() {
        }

        static final AOAviatorLong[] cache = new AOAviatorLong[256];

        static {
            for (long i = 0; i < cache.length; i++) {
                cache[(int) i] = new AOAviatorLong(i - 128);
            }
        }
    }

    AOAviatorLong(final long i) {
        super(i);
    }


    AOAviatorLong(final Number number) {
        super(number);

    }


    public static AOAviatorLong valueOf(final long l) {
        final int offset = 128;
        if (l >= -128 && l <= 127) { // will cache
            return LongCache.cache[(int) l + offset];
        }
        return new AOAviatorLong(l);
    }


    public static AOAviatorLong valueOf(final Long l) {
        return valueOf(l.longValue());
    }


    @Override
    public AviatorObject neg(final Map<String, Object> env) {
        return AOAviatorLong.valueOf(-this.longValue);
    }


    @Override
    public int innerCompare(final Map<String, Object> env, final AOAviatorNumber other) {
        if (other.getAviatorType() == AviatorType.Long) {
            return TypeUtils.comapreLong(longValue(), other.longValue());
        }

        switch (other.getAviatorType()) {
            case BigInt:
                return toBigInt().compareTo(other.toBigInt());
            case Decimal:
            case Double:
                return toDecimal().compareTo(other.toDecimal());
            default:
                throw new CompareNotSupportedException(
                        "Could not compare " + desc(env) + " with " + other.desc(env));
        }
    }


    @Override
    public AviatorObject innerDiv(final Map<String, Object> env, final AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case BigInt:
                return AOAviatorBigInt.valueOf(toBigInt().divide(other.toBigInt()));
            case Long:
                return AOAviatorLong.valueOf(this.longValue / other.longValue());
//            case Decimal:
            default:
                return AOAviatorDecimal.valueOf(toDecimal().divide(other.toDecimal()));
        }
    }


    @Override
    public AviatorObject innerAdd(final Map<String, Object> env, final AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case BigInt:
                return AOAviatorBigInt.valueOf(toBigInt().add(other.toBigInt()));
            case Long:
                return AOAviatorLong.valueOf(this.longValue + other.longValue());
//            case Decimal:
            default:
                return AOAviatorDecimal.valueOf(toDecimal().add(other.toDecimal()));
        }
    }


    @Override
    public AviatorObject innerMod(final Map<String, Object> env, final AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case BigInt:
                return AOAviatorBigInt.valueOf(toBigInt().mod(other.toBigInt()));
            case Long:
                return AOAviatorLong.valueOf(this.longValue % other.longValue());
//            case Decimal:
            default:
                return AOAviatorDecimal.valueOf(toDecimal().remainder(other.toDecimal()));
        }
    }


    @Override
    public AviatorObject innerMult(final Map<String, Object> env, final AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case BigInt:
                return AOAviatorBigInt.valueOf(toBigInt().multiply(other.toBigInt()));
            case Long:
                return AOAviatorLong.valueOf(this.longValue * other.longValue());
//            case Decimal:
            default:
                return AOAviatorDecimal.valueOf(toDecimal().multiply(other.toDecimal()));
        }
    }


    protected void ensureLong(final AviatorObject other) {
        if (other.getAviatorType() != AviatorType.Long) {
            throw new ExpressionRuntimeException(
                    other + " is not long type,could not be used as a bit operand.");
        }
    }


    @Override
    public AviatorObject bitAnd(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerBitAnd(other);
            case JavaType:
                AviatorJavaType otherJavaType = (AviatorJavaType) other;
                final Object otherValue = otherJavaType.getValue(env);
                if (otherValue instanceof Number) {
                    return innerBitAnd(AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.bitAnd(other, env);
                }
            default:
                return super.bitAnd(other, env);
        }
    }


    protected AviatorObject innerBitAnd(final AviatorObject other) {
        ensureLong(other);
        long otherLong = this.getOtherLongValue(other);
        return AOAviatorLong.valueOf(this.longValue & otherLong);
    }


    protected AviatorObject innerBitOr(final AviatorObject other) {
        ensureLong(other);
        long otherLong = this.getOtherLongValue(other);
        return AOAviatorLong.valueOf(this.longValue | otherLong);
    }


    protected AviatorObject innerBitXor(final AviatorObject other) {
        ensureLong(other);
        long otherLong = this.getOtherLongValue(other);
        return AOAviatorLong.valueOf(this.longValue ^ otherLong);
    }


    protected AviatorObject innerShiftLeft(final AviatorObject other) {
        ensureLong(other);
        long otherLong = this.getOtherLongValue(other);
        return AOAviatorLong.valueOf(this.longValue << otherLong);
    }


    protected AviatorObject innerShiftRight(final AviatorObject other) {
        ensureLong(other);
        long otherLong = this.getOtherLongValue(other);
        return AOAviatorLong.valueOf(this.longValue >> otherLong);
    }


    protected AviatorObject innerUnsignedShiftRight(final AviatorObject other) {
        ensureLong(other);
        long otherLong = this.getOtherLongValue(other);
        return AOAviatorLong.valueOf(this.longValue >>> otherLong);
    }


    @Override
    public AviatorObject bitNot(final Map<String, Object> env) {
        return AOAviatorLong.valueOf(~this.longValue);
    }


    @Override
    public Object getValue(final Map<String, Object> env) {
        return this.longValue;
    }


    @Override
    public long longValue() {
        return this.longValue;
    }


    @Override
    public AviatorObject bitOr(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerBitOr(other);
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerBitOr(AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.bitOr(other, env);
                }
            default:
                return super.bitOr(other, env);
        }
    }


    @Override
    public AviatorObject bitXor(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerBitXor(other);
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerBitXor(AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.bitXor(other, env);
                }
            default:
                return super.bitXor(other, env);
        }
    }


    @Override
    public AviatorObject shiftLeft(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerShiftLeft(other);
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerShiftLeft(AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.shiftLeft(other, env);
                }
            default:
                return super.shiftLeft(other, env);
        }
    }


    @Override
    public AviatorObject shiftRight(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerShiftRight(other);
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerShiftRight(AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.shiftRight(other, env);
                }
            default:
                return super.shiftRight(other, env);
        }
    }


    @Override
    public AviatorObject unsignedShiftRight(final AviatorObject other,
                                            final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerUnsignedShiftRight(other);
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerUnsignedShiftRight(AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.unsignedShiftRight(other, env);
                }
            default:
                return super.unsignedShiftRight(other, env);
        }
    }


    @Override
    public AviatorObject innerSub(final Map<String, Object> env, final AOAviatorNumber other) {
        switch (other.getAviatorType()) {
            case BigInt:
                return AOAviatorBigInt.valueOf(toBigInt().subtract(other.toBigInt()));
            case Long:
                return AOAviatorLong.valueOf(this.longValue - other.longValue());
//            case Decimal:
            default:
                return AOAviatorDecimal.valueOf(toDecimal().subtract(other.toDecimal()));
        }
    }


    @Override
    public AviatorType getAviatorType() {
        return AviatorType.Long;
    }

    protected long getOtherLongValue(AviatorObject other) {
        if (other instanceof AviatorNumber) {
            return ((AviatorLong) other).longValue();
        } else if (other instanceof AOAviatorNumber) {
            return ((AOAviatorLong) other).longValue();
        } else {
            throw new ExpressionRuntimeException(String.format("Unknown AviatorObject type [%s]", AORuntimeUtils.getClass(other)));
        }
    }
}
