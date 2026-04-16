package com.eredar.aviatororacle.runtime.object;

import com.eredar.aviatororacle.runtime.uitls.CalcUtils;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.exception.CompareNotSupportedException;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.lexer.SymbolTable;
import com.googlecode.aviator.runtime.RuntimeFunctionDelegator;
import com.googlecode.aviator.runtime.RuntimeUtils;
import com.googlecode.aviator.runtime.function.DispatchFunction;
import com.googlecode.aviator.runtime.function.LambdaFunction;
import com.googlecode.aviator.runtime.type.*;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaElementType.ContainerType;
import com.googlecode.aviator.utils.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * AviatorOracle 框架计算过程总入口
 */
@Slf4j
public class AOAviatorJavaType extends AviatorObject {

    private static final long serialVersionUID = 4742012682922854365L;

    @Getter
    protected String name;
    private boolean containsDot;
    private String[] subNames;
    private SymbolTable symbolTable;

    private void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
        String name = (String) input.readObject();
        SymbolTable symbolTable = (SymbolTable) input.readObject();
        init(name, symbolTable);
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        output.writeObject(this.name);
        output.writeObject(this.symbolTable);
    }

    @Override
    public AviatorType getAviatorType() {
        return AviatorType.JavaType;
    }

    public AOAviatorJavaType(final String name) {
        this(name, null);
    }

    public AOAviatorJavaType(final String name, final SymbolTable symbolTable) {
        super();
        init(name, symbolTable);
    }

    private void init(final String name, final SymbolTable symbolTable) {
        if (name != null) {
            String rName = reserveName(name);
            if (rName != null) {
                this.name = rName;
            } else {
                if (symbolTable != null) {
                    this.name = symbolTable.reserve(name).getLexeme();
                } else {
                    this.name = name;
                }
            }
            this.containsDot = this.name.contains(".");
        } else {
            this.name = null;
            this.containsDot = false;
        }
        this.symbolTable = symbolTable;
    }

    /**
     * Reserved special var names, return null if not successes.
     */
    public static String reserveName(final String name) {
        if (Constants.ENV_VAR.equals(name)) {
            return Constants.ENV_VAR;
        } else if (Constants.ReducerEmptyVal.getLexeme().equals(name)) {
            return Constants.ReducerEmptyVal.getLexeme();
        } else if (Constants.FUNC_ARGS_VAR.equals(name)) {
            return Constants.FUNC_ARGS_VAR;
        } else if (Constants.REDUCER_LOOP_VAR.equals(name)) {
            return Constants.REDUCER_LOOP_VAR;
        } else if (Constants.INSTANCE_VAR.equals(name)) {
            return Constants.INSTANCE_VAR;
        } else if (Constants.EXP_VAR.equals(name)) {
            return Constants.EXP_VAR;
        } else {
            return null;
        }
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
    public AviatorObject match(final AviatorObject other, final Map<String, Object> env) {
        Object val = getValue(env);
        if (val instanceof Pattern) {
            return new AviatorPattern((Pattern) val).match(other, env);
        } else {
            return super.match(other, env);
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
    public Object getValue(final Map<String, Object> env) {
        Object value = this.getValueFromEnv(this.name, this.containsDot, env, true);
        // If value is a function delegator,try to get the real value
        if (value instanceof RuntimeFunctionDelegator) {
            value = ((RuntimeFunctionDelegator) value).getValue(env);
        }
        return value;
    }

    public Object getValueFromEnv(final String name, final boolean nameContainsDot,
                                  final Map<String, Object> env, final boolean throwExceptionNotFound) {
        if (env != null) {
            if (nameContainsDot && !env.containsKey(name) && RuntimeUtils.getInstance(env)
                    .getOptionValue(Options.ENABLE_PROPERTY_SYNTAX_SUGAR).bool) {
                if (this.subNames == null) {
                    // cache the result
                    this.subNames = Constants.SPLIT_PAT.split(name);
                }
                return getProperty(name, this.subNames, env, throwExceptionNotFound, false);
            }
            return env.get(name);
        }
        return null;
    }

    public static Object getValueFromEnv(final String name, final boolean nameContainsDot,
                                         final String[] names, final Map<String, Object> env, final boolean throwExceptionNotFound,
                                         final boolean tryResolveStaticMethod) {
        if (env != null) {
            if (nameContainsDot && RuntimeUtils.getInstance(env)
                    .getOptionValue(Options.ENABLE_PROPERTY_SYNTAX_SUGAR).bool) {
                return getProperty(name, names, env, throwExceptionNotFound, tryResolveStaticMethod);
            }
            return env.get(name);
        }
        return null;
    }

    @Override
    public AviatorObject defineValue(final AviatorObject value, final Map<String, Object> env) {
        if (this.containsDot) {
            return setProperty(value, env);
        }

        Object v = getAssignedValue(value, env);

        // special processing for define functions.
        if (v instanceof LambdaFunction) {
            // try to define a function
            Object existsFn = getValue(env);
            if (existsFn instanceof DispatchFunction) {
                // It's already an overload function, install the new branch.
                ((DispatchFunction) existsFn).install((LambdaFunction) v);
                return AOAviatorRuntimeJavaType.valueOf(existsFn);
            } else if (existsFn instanceof LambdaFunction) {
                // cast it to an overload function
                DispatchFunction newFn = new DispatchFunction(this.name);
                // install the exists branch
                newFn.install((LambdaFunction) existsFn);
                // and the new branch.
                newFn.install(((LambdaFunction) v));
                v = newFn;
            } else if (existsFn == null && ((LambdaFunction) v).isVariadic()) {
                // cast variadic function to overload function
                DispatchFunction newFn = new DispatchFunction(this.name);
                newFn.install(((LambdaFunction) v));
                v = newFn;
            }
        }

        ((Env) env).override(this.name, v);
        return AOAviatorRuntimeJavaType.valueOf(v);
    }

    private Object getAssignedValue(final AviatorObject value, final Map<String, Object> env) {
        Object v = value.getValue(env);
        if (v instanceof AviatorObject) {
            v = ((AviatorObject) v).deref(env);
        }
        return v;
    }

    @Override
    public AviatorObject setValue(final AviatorObject value, final Map<String, Object> env) {
        if (this.containsDot) {
            return setProperty(value, env);
        }

        Object v = getAssignedValue(value, env);
        env.put(this.name, v);
        return AOAviatorRuntimeJavaType.valueOf(v);
    }

    private AviatorObject setProperty(final AviatorObject value, final Map<String, Object> env) {
        if (RuntimeUtils.getInstance(env).getOptionValue(Options.ENABLE_PROPERTY_SYNTAX_SUGAR).bool) {
            Object v = value.getValue(env);
            try {
                Reflector.setProperty(env, this.name, value.getValue(env));
            } catch (Throwable t) {
                if (RuntimeUtils.getInstance(env).getOptionValue(Options.TRACE_EVAL).bool) {
                    log.error("Aviator Reflector exception", t);
                }
                //noinspection DataFlowIssue
                throw Reflector.sneakyThrow(t);
            }
            return AOAviatorRuntimeJavaType.valueOf(v);
        } else {
            throw new ExpressionRuntimeException("Can't assign value to " + this.name
                    + ", Options.ENABLE_PROPERTY_SYNTAX_SUGAR is disabled.");
        }
    }

    public static Object getProperty(final String name, String[] names, final Map<String, Object> env,
                                     final boolean throwExceptionNotFound, final boolean tryResolveStaticMethod) {
        try {
            if (names == null) {
                names = Constants.SPLIT_PAT.split(name);
            }
            return Reflector.fastGetProperty(name, names, env, Reflector.Target.withEnv(env),
                    tryResolveStaticMethod, 0, names.length);

        } catch (Throwable t) {
            if (RuntimeUtils.getInstance(env).getOptionValue(Options.TRACE_EVAL).bool) {
                log.error("Aviator Reflector exception", t);
            }
            if (RuntimeUtils.getInstance(env).getOptionValue(Options.NIL_WHEN_PROPERTY_NOT_FOUND).bool) {
                return null;
            } else if (throwExceptionNotFound) {
                throw new ExpressionRuntimeException("Could not find variable " + name, t);
            } else {
                return null;
            }
        }
    }

    public static Object tryResolveAsClass(final Map<String, Object> env, final String rName) {
        try {
            return ((Env) env).resolveClassSymbol(rName);
        } catch (ClassNotFoundException e) {
            return null;
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
                        return AOAviatorDecimal.valueOf(CalcUtils.oracleDaysBetween(beginDate, endDate));
                    } else if (otherValue instanceof Number) { // 日期减数字，减去对应的天数，得到新的日期
                        return AOAviatorRuntimeJavaType.valueOf(CalcUtils.oracleMinusDays((Instant) value, (Number) otherValue));
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
    public AviatorObject not(final Map<String, Object> env) {
        final Object value = getValue(env);
        if (value instanceof Boolean) {
            return AviatorBoolean.valueOf((Boolean) value).not(env);
        } else {
            return super.not(env);
        }
    }

    /**
     * Access array or list element
     */
    @Override
    public AviatorObject getElement(final Map<String, Object> env, final AviatorObject indexObject) {
        final Object thisValue = getValue(env);
        final Object indexValue = indexObject.getValue(env);

        if (thisValue.getClass().isArray()) {
            final int index = ((Number) indexValue).intValue();
            return new AviatorRuntimeJavaElementType(ContainerType.Array, thisValue, index,
                    () -> ArrayUtils.get(thisValue, index));
        } else if (thisValue instanceof List) {
            final int index = ((Number) indexValue).intValue();
            return new AviatorRuntimeJavaElementType(ContainerType.List, thisValue, index,
                    () -> ((List<?>) thisValue).get(index));
        } else if (thisValue instanceof Map) {
            return new AviatorRuntimeJavaElementType(ContainerType.Map, thisValue, indexValue,
                    () -> ((Map<?, ?>) thisValue).get(indexValue));
        } else {
            throw new ExpressionRuntimeException(
                    desc(env) + " is not an array, list or map,could not use [] to get element");
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
                return AOAviatorRuntimeJavaType.valueOf(CalcUtils.oraclePlusDays((Instant) value, (Number) otherValue));
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

    @Override
    public String desc(final Map<String, Object> env) {
        Object value =
                getValueFromEnv(this.name, this.name != null && this.name.contains("."), env, false);
        return "<" + getAviatorType() + ", " + getName() + ", " + value + ", "
                + (value == null ? "null" : value.getClass().getName()) + ">";
    }
}
