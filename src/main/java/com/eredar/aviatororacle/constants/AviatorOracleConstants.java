package com.eredar.aviatororacle.constants;

import com.eredar.aviatororacle.number.OraDecimal;

public class AviatorOracleConstants {

    // 1天的秒数
    public static final OraDecimal SECONDS_OF_DAY_ORA_DECIMAL = new OraDecimal("86400");
    public static final long SECONDS_OF_DAY_LONG = 86400L;

    // 1个月的秒数
    public static final OraDecimal SECONDS_OF_MONTH = new OraDecimal("2678400");
    // 负一
    public static final OraDecimal NEG = new OraDecimal("-1");
}
