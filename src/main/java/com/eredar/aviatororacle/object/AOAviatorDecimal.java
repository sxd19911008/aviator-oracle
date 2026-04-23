package com.eredar.aviatororacle.object;

import com.eredar.aviatororacle.number.OraDecimal;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorType;

import java.util.Map;

public class AOAviatorDecimal extends AOAviatorNumber {

    private static final long serialVersionUID = -5451818688834249245L;

    public AOAviatorDecimal(final OraDecimal number) {
        super(number);
    }


    public static AOAviatorDecimal valueOf(final OraDecimal d) {
        return new AOAviatorDecimal(d);
    }


    @Override
    public AviatorObject innerSub(final Map<String, Object> env, final AOAviatorNumber other) {
        return AOAviatorDecimal.valueOf(toDecimal().subtract(other.toDecimal()));
    }


    @Override
    public AviatorObject neg(final Map<String, Object> env) {
        return AOAviatorDecimal.valueOf(toDecimal().negate());
    }


    @Override
    public AviatorObject innerMult(final Map<String, Object> env, final AOAviatorNumber other) {
        return AOAviatorDecimal.valueOf(toDecimal().multiply(other.toDecimal()));
    }


    @Override
    public AviatorObject innerMod(final Map<String, Object> env, final AOAviatorNumber other) {
        return AOAviatorDecimal.valueOf(toDecimal().remainder(other.toDecimal()));
    }


    @Override
    public AviatorObject innerDiv(final Map<String, Object> env, final AOAviatorNumber other) {
        return AOAviatorDecimal.valueOf(toDecimal().divide(other.toDecimal()));
    }


    @Override
    public AOAviatorNumber innerAdd(final Map<String, Object> env, final AOAviatorNumber other) {
        return AOAviatorDecimal.valueOf(toDecimal().add(other.toDecimal()));
    }


    @Override
    public int innerCompare(final Map<String, Object> env, final AOAviatorNumber other) {
        return toDecimal().compareTo(other.toDecimal());
    }


    @Override
    public AviatorType getAviatorType() {
        return AviatorType.Decimal;
    }
}
