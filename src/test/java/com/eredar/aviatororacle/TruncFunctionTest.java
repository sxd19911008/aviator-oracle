package com.eredar.aviatororacle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import com.eredar.aviatororacle.utils.AODateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试：模拟Oracle数据库 {@code trunc()} 方法。
 */
@DisplayName("trunc 方法测试")
public class TruncFunctionTest {

    /** 单参数表达式 */
    private static final String EXPR_ONE_ARG  = "trunc(a)";
    /** 双参数表达式 */
    private static final String EXPR_TWO_ARGS = "trunc(a, b)";

    /** LocalDateTime 格式化器 */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── 辅助方法 ─────────────────────────────────────────────────────────────

    private static Date date(String s) {
        return AODateUtils.strToDate(s);
    }

    private static LocalDateTime ldt(String s) {
        return LocalDateTime.parse(s, FMT);
    }

    private static Map<String, Object> vars1(Object a) {
        return HashMapBuilder.<String, Object>builder().put("a", a).build();
    }

    private static Map<String, Object> vars2(Object a, Object b) {
        return HashMapBuilder.<String, Object>builder().put("a", a).put("b", b).build();
    }

    // =========================================================================
    // 数字截断 — 单参数
    // =========================================================================

    static Stream<Arguments> testTruncNumberOneArgProvider() {
        return Stream.of(
                Arguments.of("入参为 null", null, null),
                Arguments.of("Long 已为整数且 scale=0 时直接返回原装箱对象", 42L, 42L),
                Arguments.of("Integer 已为整数且 scale=0 时直接返回原装箱对象", 7, 7L),
                Arguments.of("Short 已为整数且 scale=0 时直接返回原装箱对象", (short) 3, 3L),
                Arguments.of("Byte 已为整数且 scale=0 时直接返回原装箱对象", (byte) 9, 9L),
                // Oracle: TRUNC(2.5) = 2（向零截断，非 HALF_UP 的 3）
                Arguments.of("Double 正数向零截断到整数", 2.5d, new OraDecimal("2")),
                // Oracle: TRUNC(3.4159) = 3
                Arguments.of("BigDecimal 正数向零截断到整数", new BigDecimal("3.4159"), new OraDecimal("3")),
                // Oracle: TRUNC(9.576) = 9
                Arguments.of("OraDecimal 正数向零截断到整数", new OraDecimal("9.576"), new OraDecimal("9")),
                // Oracle: TRUNC(-2.5) = -2（向零，非向负无穷的 -3）
                Arguments.of("Double 负数向零截断到整数", -2.5d, new OraDecimal("-2")),
                // Oracle: TRUNC(-3.9999) = -3
                Arguments.of("BigDecimal 负数向零截断到整数", new BigDecimal("-3.9999"), new OraDecimal("-3")),
                // Oracle: TRUNC(-9.999) = -9
                Arguments.of("OraDecimal 负数向零截断到整数", new OraDecimal("-9.999"), new OraDecimal("-9"))
        );
    }

    @DisplayName("trunc(number) 表达式测试（等价于 scale=0）")
    @ParameterizedTest(name = "【{index}】{0}: a={1}, expected={2}")
    @MethodSource("testTruncNumberOneArgProvider")
    public void testTruncNumberOneArg(String caseId, Object input, Object expected) {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(input));
        Assertions.assertEquals(expected, actual);
    }

    // =========================================================================
    // 数字截断 — 双参数
    // =========================================================================

    static Stream<Arguments> testTruncNumberTwoArgsProvider() {
        return Stream.of(
                Arguments.of("number 为 null 时返回 null", null, 2, null),
                // newScale 极限区间：>= 40 原样返回 number；<= -40 返回 0
                // Oracle: TRUNC(999, 40) = 999
                Arguments.of("primitive newScale>=40 时原样返回 Long", 999L, 40, 999L),
                // Oracle: TRUNC(12.3, 40) = 12.3
                Arguments.of("Double 形式的 newScale>=40 时原样返回 Double", 12.3d, 40.0d, new OraDecimal("12.3")),
                Arguments.of("Integer 入参且 OraDecimal newScale>=40 时原样返回", 1, new OraDecimal("40"), 1L),
                Arguments.of(
                        "BigInteger 入参且 BigInteger newScale>=40 时原样返回 BigInteger 入参",
                        new BigInteger("5"),
                        new BigInteger("40"),
                        new BigInteger("5")
                ),
                Arguments.of(
                        "OraDecimal 入参与 newScale>=40 时原样返回（assertSame 语义）",
                        new OraDecimal("2.71"),
                        new OraDecimal("40"),
                        new OraDecimal("2.71")
                ),
                // Oracle: TRUNC(大数, -40) = 0
                Arguments.of("Long newScale<=-40 时结果为 0", Long.MAX_VALUE, -40, 0L),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（int newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40,
                        0L
                ),
                // Oracle: TRUNC(大数, -39) = 1000000000000000000000000000000000000000
                Arguments.of(
                        "OraDecimal newScale=-39（int newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（Long newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40L,
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39（Long newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39L,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "BigInteger 入参 newScale<=-40 时结果为 0（BigInteger newScale）",
                        new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("-40"),
                        0L
                ),
                Arguments.of(
                        "BigInteger 入参 newScale=-39（BigInteger newScale）",
                        new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（Double newScale 带小数）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -40.9d,
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39（Double newScale 带小数，丢弃小数部分）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        -39.9d,
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "BigDecimal 入参 newScale<=-40 时结果为 0（BigDecimal newScale）",
                        new BigDecimal("1234567890123456789012345678901234567890"),
                        new BigDecimal("-40"),
                        0L
                ),
                Arguments.of(
                        "BigDecimal 入参 newScale=-39（BigDecimal newScale）",
                        new BigDecimal("1234567890123456789012345678901234567890"),
                        new BigDecimal("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                Arguments.of(
                        "OraDecimal newScale<=-40 时结果为 0（OraDecimal newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        new OraDecimal("-40"),
                        0L
                ),
                Arguments.of(
                        "OraDecimal newScale=-39（OraDecimal newScale）",
                        new OraDecimal("1234567890123456789012345678901234567890"),
                        new OraDecimal("-39"),
                        new OraDecimal("1000000000000000000000000000000000000000")
                ),
                // 各 Number 分支上的正常向零截断（Oracle: TRUNC(1.23456, 1) = 1.2）
                Arguments.of(
                        "OraDecimal 保留1位小数 DOWN（Byte newScale）",
                        new OraDecimal("1.23456"),
                        (byte) 1,
                        new OraDecimal("1.2")
                ),
                // Oracle: TRUNC(1.23456, 3) = 1.234
                Arguments.of(
                        "OraDecimal 保留3位小数 DOWN（Integer newScale）",
                        new OraDecimal("1.23456"),
                        3,
                        new OraDecimal("1.234")
                ),
                // Oracle: TRUNC(2.3456, 2) = 2.34
                Arguments.of(
                        "BigDecimal 先转 OraDecimal 再 setScale DOWN（Long newScale）",
                        new BigDecimal("2.3456"),
                        2L,
                        new OraDecimal("2.34")
                ),
                // Oracle: TRUNC(77, 5) = 77（整数 newScale>=0 直接返回）
                Arguments.of("Long 且 newScale>=0 时直接返回原 Long", 77L, 5, 77L),
                // Oracle: TRUNC(12345, -1) = 12340（向零截断，与 round 的 12350 不同）
                Arguments.of(
                        "Long 且 newScale<0 时在左侧数量级上 DOWN（BigInteger newScale）",
                        12345L,
                        new BigInteger("-1"),
                        new OraDecimal("12340")
                ),
                // Oracle: TRUNC(154, -2) = 100（向零截断）
                Arguments.of(
                        "Integer 且 newScale<0 时在左侧数量级上 DOWN（BigDecimal newScale）",
                        154,
                        new BigDecimal("-2"),
                        new OraDecimal("100")
                ),
                // Oracle: TRUNC(999, 2) = 999（BigInteger newScale>=0 直接返回）
                Arguments.of(
                        "BigInteger 且 newScale>=0 时直接返回原 BigInteger（OraDecimal newScale）",
                        new BigInteger("999"),
                        new OraDecimal("2"),
                        new BigInteger("999")
                ),
                // Oracle: TRUNC(12345, -1) = 12340
                Arguments.of(
                        "BigInteger 且 newScale<0 时转为 OraDecimal 向零截断（Double newScale）",
                        new BigInteger("12345"),
                        -1.0d,
                        new OraDecimal("12340")
                ),
                // Oracle: TRUNC(10.56, 1) = 10.5（OraDecimal，Short newScale）
                Arguments.of(
                        "OraDecimal 保留1位小数 DOWN（Short newScale）",
                        new OraDecimal("10.56"),
                        (short) 1,
                        new OraDecimal("10.5")
                ),
                // Oracle: TRUNC(9.12345678901234567890123456789012345675, 37) = 9.1234567890123456789012345678901234567
                Arguments.of(
                        "newScale 为带小数的 Double 时先截断为 long 再转 int 作为 scale",
                        new OraDecimal("9.12345678901234567890123456789012345675"),
                        37.9d,
                        new OraDecimal("9.1234567890123456789012345678901234567")
                ),
                // Oracle: TRUNC(9.12345678901234567890123456789012345675, 38) = 9.12345678901234567890123456789012345675
                Arguments.of(
                        "newScale 等于小数位数时无截断原样返回（BigInteger newScale）",
                        new OraDecimal("9.12345678901234567890123456789012345675"),
                        new BigInteger("38"),
                        new OraDecimal("9.12345678901234567890123456789012345675")
                ),
                // Oracle: TRUNC(3.1445926, 2) = 3.14（BigDecimal newScale 丢弃小数部分后 scale=2）
                Arguments.of(
                        "newScale 为 BigDecimal 时用 intValue 丢弃小数部分（BigDecimal number）",
                        new BigDecimal("3.1445926"),
                        new BigDecimal("2.9"),
                        new OraDecimal("3.14")
                ),
                Arguments.of(
                        "newScale 为 OraDecimal 时用 intValue 丢弃小数部分（OraDecimal number）",
                        new OraDecimal("3.1445926"),
                        new OraDecimal("2.9"),
                        new OraDecimal("3.14")
                ),
                // 负数向零截断（区别于 floor 的向负无穷方向）
                // Oracle: TRUNC(-15.79, 1) = -15.7
                Arguments.of("负数保留正 newScale 位小数向零截断", new OraDecimal("-15.79"), 1, new OraDecimal("-15.7")),
                // Oracle: TRUNC(-2.9, 0) = -2
                Arguments.of("负数 newScale=0 向零截断", new OraDecimal("-2.9"), 0, new OraDecimal("-2")),
                // Oracle: TRUNC(-15.79, -1) = -10
                Arguments.of("负数 newScale<0 在左侧数量级向零截断", new OraDecimal("-15.79"), -1, new OraDecimal("-10")),
                // Double number 的正、负截断分支
                // Oracle: TRUNC(3.14159, 3) = 3.141
                Arguments.of("Double number 正数保留3位小数向零截断", new OraDecimal("3.14159"), 3, new OraDecimal("3.141")),
                // Oracle: TRUNC(-3.14159, 3) = -3.141
                Arguments.of("Double number 负数保留3位小数向零截断", new OraDecimal("-3.14159"), 3, new OraDecimal("-3.141"))
        );
    }

    @DisplayName("trunc(number, scale) 表达式测试")
    @ParameterizedTest(name = "【{index}】{0}: number={1}, scale={2}, expected={3}")
    @MethodSource("testTruncNumberTwoArgsProvider")
    public void testTruncNumberTwoArgs(String caseId, Object number, Object scale, Object expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(number, scale));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("trunc(number, scale) 非法入参测试：scale 为 null")
    public void testTruncNumberTwoArgsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(1.0, null)));
    }

    // =========================================================================
    // Date 截断
    // =========================================================================

    static Stream<Arguments> testTruncDateProvider() {
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                Arguments.of("世纪截断-CC",   date("2001-04-22 10:14:06"), "CC",    date("2001-01-01 00:00:00")),
                Arguments.of("世纪截断-SCC",  date("2000-04-22 10:14:06"), "SCC",   date("1901-01-01 00:00:00")),
                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                Arguments.of("年份截断-SYYYY", date("2024-12-31 23:59:59"), "SYYYY", date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYYY",  date("2024-12-31 23:59:59"), "YYYY",  date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YEAR",  date("2024-12-31 23:59:59"), "YEAR",  date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-SYEAR", date("2024-12-31 23:59:59"), "SYEAR", date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYY",   date("2021-01-01 10:19:19"), "YYY",   date("2021-01-01 00:00:00")),
                Arguments.of("年份截断-YY",    date("2022-01-01 12:19:19"), "YY",    date("2022-01-01 00:00:00")),
                Arguments.of("年份截断-Y",     date("2023-01-01 12:19:19"), "Y",     date("2023-01-01 00:00:00")),
                // ---- ISO 年 IYYY / IYY / IY / I ----
                Arguments.of("ISO年截断-IYYY", date("2024-01-01 12:19:19"), "IYYY",  date("2024-01-01 00:00:00")),
                Arguments.of("ISO年截断-IYY",  date("2021-01-01 10:19:19"), "IYY",   date("2019-12-30 00:00:00")),
                Arguments.of("ISO年截断-IY",   date("2022-01-01 12:19:19"), "IY",    date("2021-01-04 00:00:00")),
                Arguments.of("ISO年截断-I",    date("2023-01-01 12:19:19"), "I",     date("2022-01-03 00:00:00")),
                // ---- 季度 Q ----
                Arguments.of("季度截断-Q",     date("2024-05-15 08:30:00"), "Q",     date("2024-04-01 00:00:00")),
                // ---- 月份 MONTH / MON / MM / RM ----
                Arguments.of("月份截断-MONTH", date("2024-12-31 23:59:59"), "MONTH", date("2024-12-01 00:00:00")),
                Arguments.of("月份截断-MON",   date("2024-10-30 23:59:59"), "MON",   date("2024-10-01 00:00:00")),
                Arguments.of("月份截断-MM",    date("2024-02-29 23:59:59"), "MM",    date("2024-02-01 00:00:00")),
                Arguments.of("月份截断-RM",    date("2024-01-01 00:00:01"), "RM",    date("2024-01-01 00:00:00")),
                // ---- 年内周 WW ----
                Arguments.of("年内周截断-WW",  date("2026-04-22 15:20:30"), "WW",    date("2026-04-16 00:00:00")),
                // ---- 月内周 W ----
                Arguments.of("月内周截断-W",   date("2026-04-22 20:19:19"), "W",     date("2026-04-22 00:00:00")),
                Arguments.of("月内周截断-W",   date("2026-04-26 09:19:19"), "W",     date("2026-04-22 00:00:00")),
                // ---- ISO 周 IW ----
                Arguments.of("ISO周截断-IW",   date("2026-04-26 09:19:19"), "IW",    date("2026-04-20 00:00:00")),
                // ---- 天 DDD / DD / J ----
                Arguments.of("天截断-DDD",     date("2026-04-26 14:45:12"), "DDD",   date("2026-04-26 00:00:00")),
                Arguments.of("天截断-DD",      date("2026-04-25 14:45:12"), "DD",    date("2026-04-25 00:00:00")),
                Arguments.of("天截断-J",       date("2026-04-20 00:00:01"), "J",     date("2026-04-20 00:00:00")),
                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                Arguments.of("周截断-DAY",     date("2026-04-26 16:19:19"), "DAY",   date("2026-04-26 00:00:00")),
                Arguments.of("周截断-DY",      date("2026-04-25 16:19:19"), "DY",    date("2026-04-19 00:00:00")),
                Arguments.of("周截断-D",       date("2026-04-20 00:00:01"), "D",     date("2026-04-19 00:00:00")),
                // ---- 小时 HH / HH12 / HH24 ----
                Arguments.of("小时截断-HH",    date("2024-05-23 18:59:59"), "HH",    date("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH12",  date("2024-05-23 18:00:01"), "HH12",  date("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH24",  date("2024-05-23 18:31:59"), "HH24",  date("2024-05-23 18:00:00")),
                // ---- 分钟 MI ----
                Arguments.of("分钟截断-MI",    date("2024-05-23 18:55:59"), "MI",    date("2024-05-23 18:55:00"))
        );
    }

    @DisplayName("trunc(date, format) 表达式测试：Date")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncDateProvider")
    public void testTruncDate(String caseId, Date input, String format, Date expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, format));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("trunc(date) 表达式测试：Date 无格式截断到天")
    public void testTruncDateNoFormat() {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(date("2026-04-25 14:45:12")));
        Assertions.assertEquals(date("2026-04-25 00:00:00"), actual);
    }

    @Test
    @DisplayName("trunc(date, format) 表达式测试：Date 不支持的格式")
    public void testTruncDateInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(date("2026-04-22 10:14:06"), "XX")));
    }

    // =========================================================================
    // LocalDateTime 截断
    // =========================================================================

    static Stream<Arguments> testTruncLocalDateTimeProvider() {
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                Arguments.of("世纪截断-CC",   ldt("2001-04-22 10:14:06"), "CC",    ldt("2001-01-01 00:00:00")),
                Arguments.of("世纪截断-SCC",  ldt("2000-04-22 10:14:06"), "SCC",   ldt("1901-01-01 00:00:00")),
                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                Arguments.of("年份截断-SYYYY", ldt("2024-12-31 23:59:59"), "SYYYY", ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYYY",  ldt("2024-12-31 23:59:59"), "YYYY",  ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YEAR",  ldt("2024-12-31 23:59:59"), "YEAR",  ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-SYEAR", ldt("2024-12-31 23:59:59"), "SYEAR", ldt("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYY",   ldt("2021-01-01 10:19:19"), "YYY",   ldt("2021-01-01 00:00:00")),
                Arguments.of("年份截断-YY",    ldt("2022-01-01 12:19:19"), "YY",    ldt("2022-01-01 00:00:00")),
                Arguments.of("年份截断-Y",     ldt("2023-01-01 12:19:19"), "Y",     ldt("2023-01-01 00:00:00")),
                // ---- ISO 年 IYYY / IYY / IY / I ----
                Arguments.of("ISO年截断-IYYY", ldt("2024-01-01 12:19:19"), "IYYY",  ldt("2024-01-01 00:00:00")),
                Arguments.of("ISO年截断-IYY",  ldt("2021-01-01 10:19:19"), "IYY",   ldt("2019-12-30 00:00:00")),
                Arguments.of("ISO年截断-IY",   ldt("2022-01-01 12:19:19"), "IY",    ldt("2021-01-04 00:00:00")),
                Arguments.of("ISO年截断-I",    ldt("2023-01-01 12:19:19"), "I",     ldt("2022-01-03 00:00:00")),
                // ---- 季度 Q ----
                Arguments.of("季度截断-Q",     ldt("2024-05-15 08:30:00"), "Q",     ldt("2024-04-01 00:00:00")),
                // ---- 月份 MONTH / MON / MM / RM ----
                Arguments.of("月份截断-MONTH", ldt("2024-12-31 23:59:59"), "MONTH", ldt("2024-12-01 00:00:00")),
                Arguments.of("月份截断-MON",   ldt("2024-10-30 23:59:59"), "MON",   ldt("2024-10-01 00:00:00")),
                Arguments.of("月份截断-MM",    ldt("2024-02-29 23:59:59"), "MM",    ldt("2024-02-01 00:00:00")),
                Arguments.of("月份截断-RM",    ldt("2024-01-01 00:00:01"), "RM",    ldt("2024-01-01 00:00:00")),
                // ---- 年内周 WW ----
                Arguments.of("年内周截断-WW",  ldt("2026-04-22 15:20:30"), "WW",    ldt("2026-04-16 00:00:00")),
                // ---- 月内周 W ----
                Arguments.of("月内周截断-W",   ldt("2026-04-22 20:19:19"), "W",     ldt("2026-04-22 00:00:00")),
                Arguments.of("月内周截断-W",   ldt("2026-04-26 09:19:19"), "W",     ldt("2026-04-22 00:00:00")),
                // ---- ISO 周 IW ----
                Arguments.of("ISO周截断-IW",   ldt("2026-04-26 09:19:19"), "IW",    ldt("2026-04-20 00:00:00")),
                // ---- 天 DDD / DD / J ----
                Arguments.of("天截断-DDD",     ldt("2026-04-26 14:45:12"), "DDD",   ldt("2026-04-26 00:00:00")),
                Arguments.of("天截断-DD",      ldt("2026-04-25 14:45:12"), "DD",    ldt("2026-04-25 00:00:00")),
                Arguments.of("天截断-J",       ldt("2026-04-20 00:00:01"), "J",     ldt("2026-04-20 00:00:00")),
                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                Arguments.of("周截断-DAY",     ldt("2026-04-26 16:19:19"), "DAY",   ldt("2026-04-26 00:00:00")),
                Arguments.of("周截断-DY",      ldt("2026-04-25 16:19:19"), "DY",    ldt("2026-04-19 00:00:00")),
                Arguments.of("周截断-D",       ldt("2026-04-20 00:00:01"), "D",     ldt("2026-04-19 00:00:00")),
                // ---- 小时 HH / HH12 / HH24 ----
                Arguments.of("小时截断-HH",    ldt("2024-05-23 18:59:59"), "HH",    ldt("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH12",  ldt("2024-05-23 18:00:01"), "HH12",  ldt("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH24",  ldt("2024-05-23 18:31:59"), "HH24",  ldt("2024-05-23 18:00:00")),
                // ---- 分钟 MI ----
                Arguments.of("分钟截断-MI",    ldt("2024-05-23 18:55:59"), "MI",    ldt("2024-05-23 18:55:00"))
        );
    }

    @DisplayName("trunc(date, format) 表达式测试：LocalDateTime")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncLocalDateTimeProvider")
    public void testTruncLocalDateTime(String caseId, LocalDateTime input, String format, LocalDateTime expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, format));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("trunc(date) 表达式测试：LocalDateTime 无格式截断到天")
    public void testTruncLocalDateTimeNoFormat() {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(ldt("2026-04-25 14:45:12")));
        Assertions.assertEquals(ldt("2026-04-25 00:00:00"), actual);
    }

    @Test
    @DisplayName("trunc(date, format) 表达式测试：LocalDateTime 不支持的格式")
    public void testTruncLocalDateTimeInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(ldt("2026-04-22 10:14:06"), "XX")));
    }

    // =========================================================================
    // Instant 截断
    // =========================================================================

    static Stream<Arguments> testTruncInstantProvider() {
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                Arguments.of("世纪截断-CC",    Instant.parse("2001-04-22T10:14:06Z"), "CC",    Instant.parse("2001-01-01T00:00:00Z")),
                Arguments.of("世纪截断-SCC",   Instant.parse("2000-04-22T10:14:06Z"), "SCC",   Instant.parse("1901-01-01T00:00:00Z")),
                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                Arguments.of("年份截断-SYYYY", Instant.parse("2024-12-31T23:59:59Z"), "SYYYY", Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-YYYY",  Instant.parse("2024-12-31T23:59:59Z"), "YYYY",  Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-YEAR",  Instant.parse("2024-12-31T23:59:59Z"), "YEAR",  Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-SYEAR", Instant.parse("2024-12-31T23:59:59Z"), "SYEAR", Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("年份截断-YYY",   Instant.parse("2021-01-01T10:19:19Z"), "YYY",   Instant.parse("2021-01-01T00:00:00Z")),
                Arguments.of("年份截断-YY",    Instant.parse("2022-01-01T12:19:19Z"), "YY",    Instant.parse("2022-01-01T00:00:00Z")),
                Arguments.of("年份截断-Y",     Instant.parse("2023-01-01T12:19:19Z"), "Y",     Instant.parse("2023-01-01T00:00:00Z")),
                // ---- ISO 年 IYYY / IYY / IY / I ----
                Arguments.of("ISO年截断-IYYY", Instant.parse("2024-01-01T12:19:19Z"), "IYYY",  Instant.parse("2024-01-01T00:00:00Z")),
                Arguments.of("ISO年截断-IYY",  Instant.parse("2021-01-01T10:19:19Z"), "IYY",   Instant.parse("2019-12-30T00:00:00Z")),
                Arguments.of("ISO年截断-IY",   Instant.parse("2022-01-01T12:19:19Z"), "IY",    Instant.parse("2021-01-04T00:00:00Z")),
                Arguments.of("ISO年截断-I",    Instant.parse("2023-01-01T12:19:19Z"), "I",     Instant.parse("2022-01-03T00:00:00Z")),
                // ---- 季度 Q ----
                Arguments.of("季度截断-Q",     Instant.parse("2024-05-15T08:30:00Z"), "Q",     Instant.parse("2024-04-01T00:00:00Z")),
                // ---- 月份 MONTH / MON / MM / RM ----
                Arguments.of("月份截断-MONTH", Instant.parse("2024-12-31T23:59:59Z"), "MONTH", Instant.parse("2024-12-01T00:00:00Z")),
                Arguments.of("月份截断-MON",   Instant.parse("2024-10-30T23:59:59Z"), "MON",   Instant.parse("2024-10-01T00:00:00Z")),
                Arguments.of("月份截断-MM",    Instant.parse("2024-02-29T23:59:59Z"), "MM",    Instant.parse("2024-02-01T00:00:00Z")),
                Arguments.of("月份截断-RM",    Instant.parse("2024-01-01T00:00:01Z"), "RM",    Instant.parse("2024-01-01T00:00:00Z")),
                // ---- 年内周 WW ----
                Arguments.of("年内周截断-WW",  Instant.parse("2026-04-22T15:20:30Z"), "WW",    Instant.parse("2026-04-16T00:00:00Z")),
                // ---- 月内周 W ----
                Arguments.of("月内周截断-W",   Instant.parse("2026-04-22T20:19:19Z"), "W",     Instant.parse("2026-04-22T00:00:00Z")),
                Arguments.of("月内周截断-W",   Instant.parse("2026-04-26T09:19:19Z"), "W",     Instant.parse("2026-04-22T00:00:00Z")),
                // ---- ISO 周 IW ----
                Arguments.of("ISO周截断-IW",   Instant.parse("2026-04-26T09:19:19Z"), "IW",    Instant.parse("2026-04-20T00:00:00Z")),
                // ---- 天 DDD / DD / J ----
                Arguments.of("天截断-DDD",     Instant.parse("2026-04-26T14:45:12Z"), "DDD",   Instant.parse("2026-04-26T00:00:00Z")),
                Arguments.of("天截断-DD",      Instant.parse("2026-04-25T14:45:12Z"), "DD",    Instant.parse("2026-04-25T00:00:00Z")),
                Arguments.of("天截断-J",       Instant.parse("2026-04-20T00:00:01Z"), "J",     Instant.parse("2026-04-20T00:00:00Z")),
                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                Arguments.of("周截断-DAY",     Instant.parse("2026-04-26T16:19:19Z"), "DAY",   Instant.parse("2026-04-26T00:00:00Z")),
                Arguments.of("周截断-DY",      Instant.parse("2026-04-25T16:19:19Z"), "DY",    Instant.parse("2026-04-19T00:00:00Z")),
                Arguments.of("周截断-D",       Instant.parse("2026-04-20T00:00:01Z"), "D",     Instant.parse("2026-04-19T00:00:00Z")),
                // ---- 小时 HH / HH12 / HH24 ----
                Arguments.of("小时截断-HH",    Instant.parse("2024-05-23T18:59:59Z"), "HH",    Instant.parse("2024-05-23T18:00:00Z")),
                Arguments.of("小时截断-HH12",  Instant.parse("2024-05-23T18:00:01Z"), "HH12",  Instant.parse("2024-05-23T18:00:00Z")),
                Arguments.of("小时截断-HH24",  Instant.parse("2024-05-23T18:31:59Z"), "HH24",  Instant.parse("2024-05-23T18:00:00Z")),
                // ---- 分钟 MI ----
                Arguments.of("分钟截断-MI",    Instant.parse("2024-05-23T18:55:59Z"), "MI",    Instant.parse("2024-05-23T18:55:00Z"))
        );
    }

    @DisplayName("trunc(instant, format) 表达式测试：Instant UTC")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncInstantProvider")
    public void testTruncInstant(String caseId, Instant input, String format, Instant expected) {
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2(input, format));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("trunc(instant) 表达式测试：Instant 无格式截断到天")
    public void testTruncInstantNoFormat() {
        Object actual = AviatorInstance.execute(EXPR_ONE_ARG, vars1(Instant.parse("2026-04-25T14:45:12Z")));
        Assertions.assertEquals(Instant.parse("2026-04-25T00:00:00Z"), actual);
    }

    @Test
    @DisplayName("trunc(instant, format) 表达式测试：Instant 不支持的格式")
    public void testTruncInstantInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_TWO_ARGS, vars2(Instant.parse("2026-04-22T10:14:06Z"), "XX")));
    }
}
