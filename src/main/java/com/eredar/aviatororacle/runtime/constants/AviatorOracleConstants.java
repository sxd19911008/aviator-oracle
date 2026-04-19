package com.eredar.aviatororacle.runtime.constants;

import com.eredar.aviatororacle.number.OraDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AviatorOracleConstants {

    // 1天的秒数
    public static final OraDecimal SECONDS_OF_DAY_ORA_DECIMAL = new OraDecimal("86400");
    public static final long SECONDS_OF_DAY_LONG = 86400L;

    // Oracle round() 方法，精度极限值常量
    public static final BigInteger ROUND_SCALE__BIG_INTEGER_POS = new BigInteger("40");
    public static final BigInteger ROUND_SCALE__BIG_INTEGER_NEG = new BigInteger("-40");
    public static final BigDecimal ROUND_SCALE__BIG_DECIMAL_POS = new BigDecimal("40");
    public static final BigDecimal ROUND_SCALE__BIG_DECIMAL_NEG = new BigDecimal("-40");
    public static final OraDecimal ROUND_SCALE__ORA_DECIMAL_POS = new OraDecimal("40");
    public static final OraDecimal ROUND_SCALE__ORA_DECIMAL_NEG = new OraDecimal("-40");
}
