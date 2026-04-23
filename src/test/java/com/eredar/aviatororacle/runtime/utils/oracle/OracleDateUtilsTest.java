package com.eredar.aviatororacle.runtime.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.utils.AODateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("基于Date模拟Oracle日期计算测试")
public class OracleDateUtilsTest {

    /**
     * 将 UTC 时间字符串解析为 {@link Date}，方便复用 Instant 的测试数据
     */
    private static Date date(String dateStr) {
        return AODateUtils.strToDate(dateStr);
    }

    // -------------------------------------------------------------------------
    // daysBetween
    // -------------------------------------------------------------------------

    /**
     * daysBetween 场景数据：caseId 为可读说明，便于参数化测试报告展示。
     */
    static Stream<Arguments> testDaysBetweenProvider() {
        return Stream.of(
                Arguments.of(
                        "结果为正，跨年多日",
                        date("2025-10-20 22:11:17"),
                        date("2023-03-11 10:43:26"),
                        new OraDecimal("954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为正，1秒",
                        date("2025-10-21 00:00:00"),
                        date("2025-10-20 23:59:59"),
                        new OraDecimal("0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为负，与正序对称取负",
                        date("2023-03-11 10:43:26"),
                        date("2025-10-20 22:11:17"),
                        new OraDecimal("-954.477673611111111111111111111111111111")
                ),
                Arguments.of( // 该场景违反正常的精度逻辑，强行保留40位小数
                        "结果为负，1秒",
                        date("2025-10-20 23:59:59"),
                        date("2025-10-21 00:00:00"),
                        new OraDecimal("-0.0000115740740740740740740740740740740741")
                ),
                Arguments.of(
                        "结果为正，2位整数",
                        date("2025-10-22 00:00:00"),
                        date("2025-10-10 00:00:37"),
                        new OraDecimal("11.99957175925925925925925925925925925926")
                ),
                Arguments.of(
                        "结果为正，3位整数",
                        date("2025-10-22 00:00:00"),
                        date("2025-07-10 00:00:37"),
                        new OraDecimal("103.999571759259259259259259259259259259")
                )
        );
    }

    @DisplayName("daysBetween 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testDaysBetweenProvider")
    public void testDaysBetween(String caseId, Date endDate, Date beginDate, OraDecimal expected) {
        OraDecimal actual = OracleDateUtils.daysBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    // -------------------------------------------------------------------------
    // monthsBetween
    // -------------------------------------------------------------------------

    static Stream<Arguments> testMonthsBetweenPlusProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        date("2023-03-15 01:14:22"),
                        date("2023-01-15 15:47:39"),
                        new OraDecimal("2")
                ),
                Arguments.of(
                        "均为月末",
                        date("2023-02-28 01:14:22"),
                        date("2023-01-31 15:47:39"),
                        new OraDecimal("1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        date("2024-02-28 01:14:22"),
                        date("2024-01-31 15:47:39"),
                        new OraDecimal("0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        date("2023-01-31 15:47:39"),
                        date("2023-01-31 01:14:22"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        date("2024-02-29 15:50:39"),
                        date("2024-02-23 11:02:39"),
                        new OraDecimal("0.2")
                ),
                Arguments.of(
                        "含0小数",
                        date("2024-02-29 15:11:53"),
                        date("2024-02-28 14:50:39"),
                        new OraDecimal("0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "含0小数，1秒是零点几月",
                        date("2024-02-29 00:00:00"),
                        date("2024-02-28 23:59:59"),
                        new OraDecimal("0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        date("2033-10-28 01:14:11"),
                        date("2013-01-31 23:24:39"),
                        new OraDecimal("248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        date("2033-10-01 00:00:00"),
                        date("2013-01-31 23:59:59"),
                        new OraDecimal("248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("monthsBetween 方法正数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenPlusProvider")
    public void testMonthsBetweenPlus(String caseId, Date endDate, Date beginDate, OraDecimal expected) {
        OraDecimal actual = OracleDateUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> testMonthsBetweenNegProvider() {
        return Stream.of(
                Arguments.of(
                        "不同月份，同一天",
                        date("2023-01-15 15:47:39"),
                        date("2023-03-15 01:14:22"),
                        new OraDecimal("-2")
                ),
                Arguments.of(
                        "均为月末",
                        date("2023-01-31 15:47:39"),
                        date("2023-02-28 01:14:22"),
                        new OraDecimal("-1")
                ),
                Arguments.of(
                        "闰年，2月28日不是月末",
                        date("2024-01-31 15:47:39"),
                        date("2024-02-28 01:14:22"),
                        new OraDecimal("-0.8836630077658303464755077658303464755078")
                ),
                Arguments.of(
                        "同一天",
                        date("2023-01-31 01:14:22"),
                        date("2023-01-31 15:47:39"),
                        new OraDecimal("0")
                ),
                Arguments.of(
                        "小数",
                        date("2024-02-23 11:02:39"),
                        date("2024-02-29 15:50:39"),
                        new OraDecimal("-0.2")
                ),
                Arguments.of(
                        "含0小数",
                        date("2024-02-28 14:50:39"),
                        date("2024-02-29 15:11:53"),
                        new OraDecimal("-0.0327337216248506571087216248506571087216")
                ),
                Arguments.of(
                        "含0小数，1秒是零点几月",
                        date("2024-02-28 23:59:59"),
                        date("2024-02-29 00:00:00"),
                        new OraDecimal("-0.0000003733572281959378733572281959378733572282")
                ),
                Arguments.of(
                        "3位整数+小数",
                        date("2013-01-31 23:24:39"),
                        date("2033-10-28 01:14:11"),
                        new OraDecimal("-248.873421445639187574671445639187574671")
                ),
                Arguments.of(
                        "整数+1秒",
                        date("2013-01-31 23:59:59"),
                        date("2033-10-01 00:00:00"),
                        new OraDecimal("-248.000000373357228195937873357228195938")
                )
        );
    }

    @DisplayName("monthsBetween 方法负数场景测试")
    @ParameterizedTest(name = "【{index}】{0}: endDate={1}, beginDate={2}")
    @MethodSource("testMonthsBetweenNegProvider")
    public void testMonthsBetweenNeg(String caseId, Date endDate, Date beginDate, OraDecimal expected) {
        OraDecimal actual = OracleDateUtils.monthsBetween(endDate, beginDate);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * 异常场景：仅测试 null 参数校验。
     * 带时区参数的重载在 OracleDateUtils 中不存在（Date 自动适配系统时区），故不测试。
     */
    @Test
    @DisplayName("monthsBetween 方法异常场景测试")
    public void testMonthsBetweenExceptions() {
        Date now = new Date();
        assertThrows(IllegalArgumentException.class, () -> OracleDateUtils.monthsBetween(now, null));
        assertThrows(IllegalArgumentException.class, () -> OracleDateUtils.monthsBetween(null, now));
    }

    // -------------------------------------------------------------------------
    // truncDate
    // -------------------------------------------------------------------------

    /**
     * truncDate(date, format) 场景数据，覆盖 Oracle TRUNC(date, fmt) 所有支持的格式模型。
     * <p>期望值均通过在 Oracle 数据库执行对应 SQL 验证得出
     * <p>
         -- CC / SCC：世纪
         select '世纪截断-CC' as descr, to_date('2026-04-22 10:14:06', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-22 10:14:06', 'YYYY-MM-DD HH24:MI:SS'), 'CC') as test_result from dual
         union all
         select '世纪截断-SCC' as descr, to_date('2026-04-22 10:14:06', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-22 10:14:06', 'YYYY-MM-DD HH24:MI:SS'), 'SCC') as test_result from dual
         union all
         -- SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y：年
         select '年份截断-SYYYY' as descr, to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'SYYYY') as test_result from dual
         union all
         select '年份截断-YYYY' as descr, to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY') as test_result from dual
         union all
         select '年份截断-YEAR' as descr, to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'YEAR') as test_result from dual
         union all
         select '年份截断-SYEAR' as descr, to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'SYEAR') as test_result from dual
         union all
         select '年份截断-YYY' as descr, to_date('2021-01-01 10:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2021-01-01 10:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'YYY') as test_result from dual
         union all
         select '年份截断-YY' as descr, to_date('2022-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2022-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'YY') as test_result from dual
         union all
         select '年份截断-Y' as descr, to_date('2023-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2023-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'Y') as test_result from dual
         union all
         -- IYYY / IYY / IY / I：ISO 年
         select 'ISO年截断-IYYY' as descr, to_date('2024-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'IYYY') as test_result from dual
         union all
         select 'ISO年截断-IYY' as descr, to_date('2021-01-01 10:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2021-01-01 10:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'IYY') as test_result from dual
         union all
         select 'ISO年截断-IY' as descr, to_date('2022-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2022-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'IY') as test_result from dual
         union all
         select 'ISO年截断-I' as descr, to_date('2023-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2023-01-01 12:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'I') as test_result from dual
         union all
         -- Q：季度
         select '季度截断-Q' as descr, to_date('2024-05-15 08:30:00', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-05-15 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'Q') as test_result from dual
         union all
         -- MONTH / MON / MM / RM：月
         select '月份截断-MONTH' as descr, to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'MONTH') as test_result from dual
         union all
         select '月份截断-MON' as descr, to_date('2024-10-30 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-10-30 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'MON') as test_result from dual
         union all
         select '月份截断-MM' as descr, to_date('2024-02-29 23:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-02-29 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'MM') as test_result from dual
         union all
         select '月份截断-RM' as descr, to_date('2024-01-01 00:00:01', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-01-01 00:00:01', 'YYYY-MM-DD HH24:MI:SS'), 'RM') as test_result from dual
         union all
         -- WW：年内周
         select '年内周截断-WW' as descr, to_date('2026-04-22 15:20:30', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-22 15:20:30', 'YYYY-MM-DD HH24:MI:SS'), 'WW') as test_result from dual
         union all
         -- W：月内周
         select '月内周截断-W' as descr, to_date('2026-04-22 20:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-22 20:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'W') as test_result from dual
         union all
         select '月内周截断-W' as descr, to_date('2026-04-26 09:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-26 09:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'W') as test_result from dual
         union all
         -- IW：ISO 周
         select 'ISO周截断-IW' as descr, to_date('2026-04-26 09:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-26 09:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'IW') as test_result from dual
         union all
         -- DDD / DD / J：天
         select '天截断-DDD' as descr, to_date('2026-04-26 14:45:12', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-26 14:45:12', 'YYYY-MM-DD HH24:MI:SS'), 'DDD') as test_result from dual
         union all
         select '天截断-DD' as descr, to_date('2026-04-25 14:45:12', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-25 14:45:12', 'YYYY-MM-DD HH24:MI:SS'), 'DD') as test_result from dual
         union all
         select '天截断-J' as descr, to_date('2026-04-20 00:00:01', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-20 00:00:01', 'YYYY-MM-DD HH24:MI:SS'), 'J') as test_result from dual
         union all
         -- DAY / DY / D：周
         select '周截断-DAY' as descr, to_date('2026-04-26 16:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-26 16:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'DAY') as test_result from dual
         union all
         select '周截断-DY' as descr, to_date('2026-04-25 16:19:19', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-25 16:19:19', 'YYYY-MM-DD HH24:MI:SS'), 'DY') as test_result from dual
         union all
         select '周截断-D' as descr, to_date('2026-04-20 00:00:01', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2026-04-20 00:00:01', 'YYYY-MM-DD HH24:MI:SS'), 'D') as test_result from dual
         union all
         -- HH / HH12 / HH24：小时
         select '小时截断-HH' as descr, to_date('2024-05-23 18:59:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-05-23 18:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'HH') as test_result from dual
         union all
         select '小时截断-HH12' as descr, to_date('2024-05-23 18:00:01', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-05-23 18:00:01', 'YYYY-MM-DD HH24:MI:SS'), 'HH12') as test_result from dual
         union all
         select '小时截断-HH24' as descr, to_date('2024-05-23 18:31:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-05-23 18:31:59', 'YYYY-MM-DD HH24:MI:SS'), 'HH24') as test_result from dual
         union all
         -- MI：分钟
         select '分钟截断-MI' as descr, to_date('2024-05-23 18:55:59', 'YYYY-MM-DD HH24:MI:SS') as origin_date, truncNumber(to_date('2024-05-23 18:55:59', 'YYYY-MM-DD HH24:MI:SS'), 'MI') as test_result from dual;
     */
    static Stream<Arguments> testTruncDateProvider() {
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                // 2026 年属于第 21 世纪（2001-2100），截断到 2001-01-01
                Arguments.of("世纪截断-CC",   date("2001-04-22 10:14:06"), "CC",    date("2001-01-01 00:00:00")),
                Arguments.of("世纪截断-SCC",  date("2000-04-22 10:14:06"), "SCC",   date("1901-01-01 00:00:00")),

                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                // 截断到当年 1 月 1 日零点
                Arguments.of("年份截断-SYYYY", date("2024-12-31 23:59:59"), "SYYYY", date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYYY",  date("2024-12-31 23:59:59"), "YYYY",  date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YEAR",  date("2024-12-31 23:59:59"), "YEAR",  date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-SYEAR", date("2024-12-31 23:59:59"), "SYEAR", date("2024-01-01 00:00:00")),
                Arguments.of("年份截断-YYY",   date("2021-01-01 10:19:19"), "YYY",   date("2021-01-01 00:00:00")),
                Arguments.of("年份截断-YY",    date("2022-01-01 12:19:19"), "YY",    date("2022-01-01 00:00:00")),
                Arguments.of("年份截断-Y",     date("2023-01-01 12:19:19"), "Y",     date("2023-01-01 00:00:00")),

                // ---- ISO 年 IYYY / IYY / IY / I ----
                // 2024-01-01（周一）属于 ISO 2024 年，ISO 2024 年首日 = 2024-01-01
                Arguments.of("ISO年截断-IYYY", date("2024-01-01 12:19:19"), "IYYY",  date("2024-01-01 00:00:00")),
                // 2021-01-01（周五）属于 ISO 2020 年，ISO 2020 年首日 = 2019-12-30
                Arguments.of("ISO年截断-IYY",  date("2021-01-01 10:19:19"), "IYY",   date("2019-12-30 00:00:00")),
                // 2022-01-01（周六）属于 ISO 2021 年，ISO 2021 年首日 = 2021-01-04
                Arguments.of("ISO年截断-IY",   date("2022-01-01 12:19:19"), "IY",    date("2021-01-04 00:00:00")),
                // 2023-01-01（周日）属于 ISO 2022 年，ISO 2022 年首日 = 2022-01-03
                Arguments.of("ISO年截断-I",    date("2023-01-01 12:19:19"), "I",     date("2022-01-03 00:00:00")),

                // ---- 季度 Q ----
                // 5 月属于 Q2，Q2 首日 = 4 月 1 日
                Arguments.of("季度截断-Q",     date("2024-05-15 08:30:00"), "Q",     date("2024-04-01 00:00:00")),

                // ---- 月份 MONTH / MON / MM / RM ----
                // 截断到本月 1 日零点
                Arguments.of("月份截断-MONTH", date("2024-12-31 23:59:59"), "MONTH", date("2024-12-01 00:00:00")),
                Arguments.of("月份截断-MON",   date("2024-10-30 23:59:59"), "MON",   date("2024-10-01 00:00:00")),
                Arguments.of("月份截断-MM",    date("2024-02-29 23:59:59"), "MM",    date("2024-02-01 00:00:00")),
                Arguments.of("月份截断-RM",    date("2024-01-01 00:00:01"), "RM",    date("2024-01-01 00:00:00")),

                // ---- 年内周 WW ----
                // 2026-01-01 为周四，2026-04-22（周三）往前 6 天对齐到上一个周四 2026-04-16
                Arguments.of("年内周截断-WW",  date("2026-04-22 15:20:30"), "WW",    date("2026-04-16 00:00:00")),

                // ---- 月内周 W ----
                // 2026-04-01 为周三，2026-04-22 也是周三（间隔恰好 3 周），偏移量整除 7，截断到当天零点
                Arguments.of("月内周截断-W",   date("2026-04-22 20:19:19"), "W",     date("2026-04-22 00:00:00")),
                // 2026-04-01 为周三，2026-04-26 是周日，本周第一天为 2026-04-22 周三
                Arguments.of("月内周截断-W",   date("2026-04-26 09:19:19"), "W",     date("2026-04-22 00:00:00")),

                // ---- ISO 周 IW ----
                // 2026-04-26（周日）对应 ISO 周的周一 = 2026-04-20
                Arguments.of("ISO周截断-IW",   date("2026-04-26 09:19:19"), "IW",    date("2026-04-20 00:00:00")),

                // ---- 天 DDD / DD / J ----
                // 截断到当天零点
                Arguments.of("天截断-DDD",     date("2026-04-26 14:45:12"), "DDD",   date("2026-04-26 00:00:00")),
                Arguments.of("天截断-DD",      date("2026-04-25 14:45:12"), "DD",    date("2026-04-25 00:00:00")),
                Arguments.of("天截断-J",       date("2026-04-20 00:00:01"), "J",     date("2026-04-20 00:00:00")),

                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                // 2026-04-26 为周日，本身即周首日
                Arguments.of("周截断-DAY",     date("2026-04-26 16:19:19"), "DAY",   date("2026-04-26 00:00:00")),
                // 2026-04-25（周六）往前 6 天到周日 2026-04-19
                Arguments.of("周截断-DY",      date("2026-04-25 16:19:19"), "DY",    date("2026-04-19 00:00:00")),
                // 2026-04-20（周一）往前 1 天到周日 2026-04-19
                Arguments.of("周截断-D",       date("2026-04-20 00:00:01"), "D",     date("2026-04-19 00:00:00")),

                // ---- 小时 HH / HH12 / HH24 ----
                // 保留小时，分钟和秒清零
                Arguments.of("小时截断-HH",    date("2024-05-23 18:59:59"), "HH",    date("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH12",  date("2024-05-23 18:00:01"), "HH12",  date("2024-05-23 18:00:00")),
                Arguments.of("小时截断-HH24",  date("2024-05-23 18:31:59"), "HH24",  date("2024-05-23 18:00:00")),

                // ---- 分钟 MI ----
                // 保留分钟，秒清零
                Arguments.of("分钟截断-MI",    date("2024-05-23 18:55:59"), "MI",    date("2024-05-23 18:55:00"))
        );
    }

    @DisplayName("truncDate(date, format) 方法测试")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncDateProvider")
    public void testTruncDate(String caseId, Date input, String format, Date expected) {
        Date actual = OracleDateUtils.truncDate(input, format);
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("truncDate(date) 方法测试")
    @Test
    public void testTruncDateNoFormat() {
        Date actual = OracleDateUtils.truncDate(date("2026-04-25 14:45:12"));
        Assertions.assertEquals(date("2026-04-25 00:00:00"), actual);
    }

    /**
     * 异常场景：传入 Oracle 不支持的格式（如 "XX"）时，应抛出 {@link IllegalArgumentException}。
     */
    @Test
    @DisplayName("truncDate 方法异常场景测试：不支持的格式")
    public void testTruncDateInvalidFormat() {
        Date now = date("2026-04-22 10:14:06");
        assertThrows(IllegalArgumentException.class, () -> OracleDateUtils.truncDate(now, "XX"));
    }
}
