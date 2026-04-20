package com.eredar.aviatororacle.runtime.object;

import com.eredar.aviatororacle.runtime.uitls.OracleInstantUtils;
import com.googlecode.aviator.exception.CompareNotSupportedException;
import com.googlecode.aviator.lexer.SymbolTable;
import com.googlecode.aviator.runtime.type.*;
import com.googlecode.aviator.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

/**
 * AviatorOracle 框架计算过程总入口
 */
@Slf4j
public class AOAviatorJavaType extends AviatorJavaType {

    private static final long serialVersionUID = 4742012682922854365L;

    public AOAviatorJavaType(final String name) {
        this(name, null);
    }

    public AOAviatorJavaType(final String name, final SymbolTable symbolTable) {
        super(name, symbolTable);
    }

    @Override
    public AviatorObject deref(final Map<String, Object> env) {
        return AOAviatorRuntimeJavaType.valueOf(getValue(env));
    }

    @Override
    public AviatorObject div(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.div(other, env);
                } else {
                    return super.div(other, env);
                }
            default:
                return super.div(other, env);
        }
    }

    @Override
    public AviatorObject bitAnd(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.bitAnd(other, env);
                } else {
                    return super.bitAnd(other, env);
                }
            default:
                return super.bitAnd(other, env);
        }
    }

    @Override
    public AviatorObject bitNot(final Map<String, Object> env) {
        final Object value = getValue(env);
        if (value instanceof Number) {
            return AOAviatorNumber.valueOf(value).bitNot(env);
        } else {
            return super.bitNot(env);
        }
    }

    @Override
    public AviatorObject bitOr(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.bitOr(other, env);
                } else {
                    return super.bitOr(other, env);
                }
            default:
                return super.bitOr(other, env);
        }
    }

    @Override
    public AviatorObject bitXor(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.bitXor(other, env);
                } else {
                    return super.bitXor(other, env);
                }
            default:
                return super.bitXor(other, env);
        }
    }

    @Override
    public AviatorObject shiftLeft(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.shiftLeft(other, env);
                } else {
                    return super.shiftLeft(other, env);
                }
            default:
                return super.shiftLeft(other, env);
        }
    }

    @Override
    public AviatorObject shiftRight(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.shiftRight(other, env);
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
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.unsignedShiftRight(other, env);
                } else {
                    return super.unsignedShiftRight(other, env);
                }
            default:
                return super.unsignedShiftRight(other, env);
        }
    }

    @Override
    public AviatorObject mod(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.mod(other, env);
                } else {
                    return super.mod(other, env);
                }
            default:
                return super.mod(other, env);
        }
    }

    @Override
    public AviatorObject sub(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.sub(other, env);
                } else if (value instanceof Instant) {
                    Object otherValue = other.getValue(env);
                    if (otherValue instanceof Instant) { // 2个日期相减得到间隔天数
                        // a - b，a 作为 endDate
                        Instant endDate = (Instant) value;
                        Instant beginDate = (Instant) otherValue;
                        return AOAviatorDecimal.valueOf(OracleInstantUtils.daysBetween(beginDate, endDate));
                    } else if (otherValue instanceof Number) { // 日期减数字，减去对应的天数，得到新的日期
                        return AOAviatorRuntimeJavaType.valueOf(OracleInstantUtils.instantMinusDays((Instant) value, (Number) otherValue));
                    } else {
                        // 类型错误，抛出异常
                        super.sub(other, env);
                    }
                } else {
                    return super.sub(other, env);
                }
            default:
                return super.sub(other, env);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public int innerCompare(final AviatorObject other, final Map<String, Object> env) {
        if (this == other) {
            return 0;
        }
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
                AOAviatorNumber aviatorNumber = AOAviatorNumber.toAOAviatorNumber(other, env);
                return -aviatorNumber.innerCompare(this, env);
            case String:
                AviatorString aviatorString = (AviatorString) other;
                return -aviatorString.innerCompare(this, env);
            case Boolean:
                AviatorBoolean aviatorBoolean = (AviatorBoolean) other;
                return -aviatorBoolean.innerCompare(this, env);
            case JavaType:
                final Object thisValue = getValue(env);
                final Object otherValue = other.getValue(env);
                if (thisValue == null) {
                    return AviatorNil.NIL.innerCompare(other, env);
                }
                if (thisValue.equals(otherValue)) {
                    return 0;
                } else {
                    if (thisValue instanceof Number) {
                        AOAviatorNumber thisAviatorNumber = AOAviatorNumber.valueOf(thisValue);
                        return thisAviatorNumber.innerCompare(other, env);
                    } else if (TypeUtils.isString(thisValue)) {
                        AviatorString thisAviatorString = new AviatorString(String.valueOf(thisValue));
                        return thisAviatorString.innerCompare(other, env);
                    } else if (thisValue instanceof Boolean) {
                        AviatorBoolean thisAviatorBoolean = AviatorBoolean.valueOf((Boolean) thisValue);
                        return thisAviatorBoolean.innerCompare(other, env);
                    } else if (thisValue instanceof Instant && otherValue instanceof String) {
                        // 关闭日期字符串之间的比对，必须自己转换后比对
                        throw new CompareNotSupportedException(
                                "Compare " + desc(env) + " with " + other.desc(env) + " error, can't compare Instant and String");
                    } else if (otherValue == null) {
                        throw new CompareNotSupportedException(
                                "Compare " + desc(env) + " with " + other.desc(env) + " error, can't compare whit null");
                    } else {
                        try {
                            return ((Comparable<Object>) thisValue).compareTo(otherValue);
                        } catch (ClassCastException e) {
                            throw new CompareNotSupportedException(
                                    "Compare " + desc(env) + " with " + other.desc(env) + " error", e);
                        }
                    }
                }
            case Nil:
                throw new CompareNotSupportedException(
                        "Compare " + desc(env) + " with " + other.desc(env) + " error, can't compare whit null");
            default:
                throw new CompareNotSupportedException("Unknow aviator type");
        }
    }

    @Override
    public AviatorObject mult(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        switch (other.getAviatorType()) {
            case BigInt:
            case Decimal:
            case Long:
            case Double:
            case JavaType:
                if (value instanceof Number) {
                    AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
                    return aviatorNumber.mult(other, env);
                } else {
                    return super.mult(other, env);
                }
            default:
                return super.mult(other, env);
        }
    }

    @Override
    public AviatorObject neg(final Map<String, Object> env) {
        final Object value = getValue(env);
        if (value instanceof Number) {
            return AOAviatorNumber.valueOf(value).neg(env);
        } else {
            return super.neg(env);
        }
    }

    @Override
    public AviatorObject add(final AviatorObject other, final Map<String, Object> env) {
        final Object value = getValue(env);
        Object otherValue;
        if (value instanceof Number) {
            AOAviatorNumber aviatorNumber = AOAviatorNumber.valueOf(value);
            return aviatorNumber.add(other, env);
        } else if (value instanceof Instant) {
            otherValue = other.getValue(env);
            if (otherValue instanceof Number) { // 日期加数字，加上对应的天数，得到新的日期
                return AOAviatorRuntimeJavaType.valueOf(OracleInstantUtils.instantPlusDays((Instant) value, (Number) otherValue));
            } else {
                // 类型错误，抛出异常
                return super.sub(other, env);
            }
        } else if (TypeUtils.isString(value)) {
            AviatorString aviatorString = new AviatorString(String.valueOf(value));
            return aviatorString.add(other, env);
        } else if (value instanceof Boolean) {
            return AviatorBoolean.valueOf((Boolean) value).add(other, env);
        } else if (value == null && (otherValue = other.getValue(env)) instanceof CharSequence) {
            return new AviatorString("null" + otherValue);
        } else {
            return super.add(other, env);
        }
    }
}
