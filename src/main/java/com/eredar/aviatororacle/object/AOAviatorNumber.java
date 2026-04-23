package com.eredar.aviatororacle.object;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.utils.AORuntimeUtils;
import com.eredar.aviatororacle.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.exception.CompareNotSupportedException;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.runtime.type.AviatorNumber;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.utils.TypeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public abstract class AOAviatorNumber extends AviatorObject {

    private static final long serialVersionUID = -5301860468644344555L;
    /**
     * Number union
     */
    // Only for bigint/decimal
    protected Number number;
    // Only valid for AviatorLong
    protected long longValue;

    public AOAviatorNumber(final long longValue) {
        super();
        this.longValue = longValue;
    }

    public AOAviatorNumber(final Number number) {
        super();
        if (number instanceof Double || number instanceof Float) {
            this.number = OraDecimal.valueOf(number);
        } else {
            this.number = number;
        }
    }

    @Override
    public Object getValue(final Map<String, Object> env) {
        return this.number;
    }


    public static AOAviatorNumber valueOf(final Object value) {
        if (TypeUtils.isLong(value)) {
            return AOAviatorLong.valueOf(((Number) value).longValue());
        } else if (TypeUtils.isDouble(value)) {
            return new AOAviatorDecimal(OraDecimal.valueOf((double) value));
        } else if (TypeUtils.isBigInt(value)) {
            return AOAviatorBigInt.valueOf((BigInteger) value);
        } else if (TypeUtils.isDecimal(value)) {
            return AOAviatorDecimal.valueOf(new OraDecimal((BigDecimal) value));
        } else if (value instanceof OraDecimal) {
            return AOAviatorDecimal.valueOf((OraDecimal) value);
        } else {
            throw new ClassCastException(String.format("Could not cast %s to Number", AORuntimeUtils.getClass(value)));
        }
    }

    @Override
    public AviatorObject add(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case String:
                return new AviatorString(getValue(env).toString() + ((AviatorString) other).getLexeme(env));
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerAdd(env, toAOAviatorNumber(other, env));
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerAdd(env, AOAviatorNumber.valueOf(otherValue));
                } else if (TypeUtils.isString(otherValue)) {
                    return new AviatorString(getValue(env).toString() + otherValue);
                } else if (otherValue instanceof Date) {
                    Number thisValue = this.number;
                    if (thisValue == null) {
                        thisValue = this.longValue;
                    }
                    return AOAviatorRuntimeJavaType.valueOf(OraFuncUtils.datePlusDays((Date) otherValue, thisValue));
                } else if (otherValue instanceof LocalDateTime) {
                    Number thisValue = this.number;
                    if (thisValue == null) {
                        thisValue = this.longValue;
                    }
                    return AOAviatorRuntimeJavaType.valueOf(OraFuncUtils.datePlusDays((LocalDateTime) otherValue, thisValue));
                } else if (otherValue instanceof Instant) {
                    Number thisValue = this.number;
                    if (thisValue == null) {
                        thisValue = this.longValue;
                    }
                    return AOAviatorRuntimeJavaType.valueOf(OraFuncUtils.datePlusDays((Instant) otherValue, thisValue));
                } else {
                    return super.add(other, env);
                }
            default:
                return super.add(other, env);
        }

    }


    @Override
    public AviatorObject sub(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerSub(env, toAOAviatorNumber(other, env));
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerSub(env, AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.sub(other, env);
                }
            default:
                return super.sub(other, env);
        }

    }


    @Override
    public AviatorObject mod(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerMod(env, toAOAviatorNumber(other, env));
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerMod(env, AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.mod(other, env);
                }
            default:
                return super.mod(other, env);
        }
    }


    @Override
    public AviatorObject div(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerDiv(env, toAOAviatorNumber(other, env));
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerDiv(env, AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.div(other, env);
                }
            default:
                return super.div(other, env);
        }

    }


    @Override
    public AviatorObject mult(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerMult(env, toAOAviatorNumber(other, env));
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue instanceof Number) {
                    return innerMult(env, AOAviatorNumber.valueOf(otherValue));
                } else {
                    return super.mult(other, env);
                }
            default:
                return super.mult(other, env);
        }

    }


    @Override
    public int innerCompare(final AviatorObject other, final Map<String, Object> env) {
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                return innerCompare(env, toAOAviatorNumber(other, env));
            case JavaType:
                final Object otherValue = other.getValue(env);
                if (otherValue == null) {
                    throw new CompareNotSupportedException(
                            "Could not compare " + desc(env) + " with null value " + other.desc(env));
                }
                if (otherValue instanceof Number) {
                    return innerCompare(env, AOAviatorNumber.valueOf(otherValue));
                } else {
                    throw new CompareNotSupportedException(
                            "Could not compare " + desc(env) + " with " + other.desc(env));
                }
            case Nil:
                throw new CompareNotSupportedException(
                        "Could not compare " + desc(env) + " with null value " + other.desc(env));
            default:
                throw new CompareNotSupportedException(
                        "Could not compare " + desc(env) + " with " + other.desc(env));

        }
    }


    public abstract AviatorObject innerSub(Map<String, Object> env, AOAviatorNumber other);


    public abstract AviatorObject innerMult(Map<String, Object> env, AOAviatorNumber other);


    public abstract AviatorObject innerMod(Map<String, Object> env, AOAviatorNumber other);


    public abstract AviatorObject innerDiv(Map<String, Object> env, AOAviatorNumber other);


    public abstract AviatorObject innerAdd(Map<String, Object> env, AOAviatorNumber other);


    public abstract int innerCompare(Map<String, Object> env, AOAviatorNumber other);


    public long longValue() {
        return this.number.longValue();
    }


    public final BigInteger toBigInt() {
        if (TypeUtils.isBigInt(this.number)) {
            return (BigInteger) this.number;
        } else {
            return new BigInteger(String.valueOf(longValue()));
        }
    }


    public final OraDecimal toDecimal() {
        if (this.number instanceof OraDecimal) {
            return (OraDecimal) this.number;
        } else if (this.number instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) this.number);
        } else if (this.number != null) {
            return OraDecimal.valueOf(this.number);
        } else {
            return OraDecimal.valueOf(this.longValue);
        }
    }

    public static AOAviatorNumber toAOAviatorNumber(AviatorObject other, Map<String, Object> env) {
        if (other instanceof AOAviatorNumber) {
            return (AOAviatorNumber) other;
        } else if (other instanceof AviatorNumber) {
            AviatorNumber otherNumber = (AviatorNumber) other;
            switch (other.getAviatorType()) {
                case BigInt:
                    BigInteger bigInt = otherNumber.toBigInt();
                    return AOAviatorBigInt.valueOf(bigInt);
                case Decimal:
                    BigDecimal decimal = otherNumber.toDecimal(env);
                    return AOAviatorDecimal.valueOf(new OraDecimal(decimal));
                case Long:
                    Long l = otherNumber.longValue();
                    return AOAviatorLong.valueOf(l);
                default:
                    double doubleValue = otherNumber.doubleValue();
                    return AOAviatorDecimal.valueOf(OraDecimal.valueOf(doubleValue));
            }
        }

        throw new ExpressionRuntimeException(String.format("Unexpected type %s", other.getClass().getName()));
    }
}
