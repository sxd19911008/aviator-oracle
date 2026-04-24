package com.eredar.aviatororacle;

import com.eredar.aviatororacle.testUtils.HashMapBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 测试：模拟Oracle数据库 {@code trunc()} 方法 — 带时区版本（仅支持 Instant）。
 */
@DisplayName("truncWithZone 方法测试")
public class TruncWithZoneFunctionTest {

    /** 两参数表达式：truncWithZone(zoneId, instant) */
    private static final String EXPR_TWO_ARGS   = "truncWithZone(z, d)";
    /** 三参数表达式：truncWithZone(zoneId, instant, format) */
    private static final String EXPR_THREE_ARGS = "truncWithZone(z, d, f)";

    /** 上海时区（UTC+8） */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    // ── 辅助方法 ─────────────────────────────────────────────────────────────

    /**
     * 以上海时区构造 {@link Instant}（纳秒固定为 0），用于构造截断后的期望值。
     */
    private static Instant sh(int year, int month, int day, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, SHANGHAI).toInstant();
    }

    /**
     * 以上海时区构造 {@link Instant}（可指定纳秒），用于携带亚秒精度的输入值，
     * 以验证截断方法能够正确清除纳秒部分。
     */
    private static Instant sh(int year, int month, int day, int hour, int minute, int second, int nano) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, nano, SHANGHAI).toInstant();
    }

    // ── 变量构造辅助方法 ──────────────────────────────────────────────────────

    private static Map<String, Object> vars2(String z, Object d) {
        return HashMapBuilder.<String, Object>builder().put("z", z).put("d", d).build();
    }

    private static Map<String, Object> vars3(String z, Object d, String f) {
        return HashMapBuilder.<String, Object>builder().put("z", z).put("d", d).put("f", f).build();
    }

    // =========================================================================
    // 带格式参数的截断
    // =========================================================================

    static Stream<Arguments> testTruncWithZoneProvider() {
        final int nano = 123_456_789;
        return Stream.of(
                // ---- 世纪 CC / SCC ----
                Arguments.of("世纪截断-CC",    sh(2001, 4, 22, 10, 14,  6, nano), "CC",    sh(2001, 1,  1,  0,  0,  0)),
                Arguments.of("世纪截断-SCC",   sh(2000, 4, 22, 10, 14,  6, nano), "SCC",   sh(1901, 1,  1,  0,  0,  0)),

                // ---- 年份 SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y ----
                Arguments.of("年份截断-SYYYY", sh(2024, 12, 31, 23, 59, 59, nano), "SYYYY", sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YYYY",  sh(2024, 12, 31, 23, 59, 59, nano), "YYYY",  sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YEAR",  sh(2024, 12, 31, 23, 59, 59, nano), "YEAR",  sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-SYEAR", sh(2024, 12, 31, 23, 59, 59, nano), "SYEAR", sh(2024, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YYY",   sh(2021,  1,  1, 10, 19, 19, nano), "YYY",   sh(2021, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-YY",    sh(2022,  1,  1, 12, 19, 19, nano), "YY",    sh(2022, 1,  1,  0,  0,  0)),
                Arguments.of("年份截断-Y",     sh(2023,  1,  1, 12, 19, 19, nano), "Y",     sh(2023, 1,  1,  0,  0,  0)),

                // ---- ISO 年 IYYY / IYY / IY / I ----
                // 2024-01-01（周一）属于 ISO 2024 年，ISO 2024 年首日 = 2024-01-01
                Arguments.of("ISO年截断-IYYY", sh(2024,  1,  1, 12, 19, 19, nano), "IYYY",  sh(2024, 1,  1,  0,  0,  0)),
                // 2021-01-01（周五）属于 ISO 2020 年，ISO 2020 年首日 = 2019-12-30
                Arguments.of("ISO年截断-IYY",  sh(2021,  1,  1, 10, 19, 19, nano), "IYY",   sh(2019, 12, 30,  0,  0,  0)),
                // 2022-01-01（周六）属于 ISO 2021 年，ISO 2021 年首日 = 2021-01-04
                Arguments.of("ISO年截断-IY",   sh(2022,  1,  1, 12, 19, 19, nano), "IY",    sh(2021, 1,  4,  0,  0,  0)),
                // 2023-01-01（周日）属于 ISO 2022 年，ISO 2022 年首日 = 2022-01-03
                Arguments.of("ISO年截断-I",    sh(2023,  1,  1, 12, 19, 19, nano), "I",     sh(2022, 1,  3,  0,  0,  0)),

                // ---- 季度 Q ----
                // 5 月属于 Q2，Q2 首日 = 4 月 1 日
                Arguments.of("季度截断-Q",     sh(2024,  5, 15,  8, 30,  0, nano), "Q",     sh(2024, 4,  1,  0,  0,  0)),

                // ---- 月份 MONTH / MON / MM / RM ----
                Arguments.of("月份截断-MONTH", sh(2024, 12, 31, 23, 59, 59, nano), "MONTH", sh(2024, 12,  1,  0,  0,  0)),
                Arguments.of("月份截断-MON",   sh(2024, 10, 30, 23, 59, 59, nano), "MON",   sh(2024, 10,  1,  0,  0,  0)),
                Arguments.of("月份截断-MM",    sh(2024,  2, 29, 23, 59, 59, nano), "MM",    sh(2024,  2,  1,  0,  0,  0)),
                Arguments.of("月份截断-RM",    sh(2024,  1,  1,  0,  0,  1, nano), "RM",    sh(2024,  1,  1,  0,  0,  0)),

                // ---- 年内周 WW ----
                // 2026-01-01 为周四，2026-04-22（周三）往前 6 天对齐到上一个周四 2026-04-16
                Arguments.of("年内周截断-WW",  sh(2026,  4, 22, 15, 20, 30, nano), "WW",    sh(2026,  4, 16,  0,  0,  0)),

                // ---- 月内周 W ----
                // 2026-04-01 为周三，2026-04-22 也是周三（间隔恰好 3 周），偏移量整除 7，截断到当天零点
                Arguments.of("月内周截断-W",   sh(2026,  4, 22, 20, 19, 19, nano), "W",     sh(2026,  4, 22,  0,  0,  0)),
                // 2026-04-01 为周三，2026-04-26 是周日，本周第一天为 2026-04-22 周三
                Arguments.of("月内周截断-W",   sh(2026,  4, 26,  9, 19, 19, nano), "W",     sh(2026,  4, 22,  0,  0,  0)),

                // ---- ISO 周 IW ----
                // 2026-04-26（周日）对应 ISO 周的周一 = 2026-04-20
                Arguments.of("ISO周截断-IW",   sh(2026,  4, 26,  9, 19, 19, nano), "IW",    sh(2026,  4, 20,  0,  0,  0)),

                // ---- 天 DDD / DD / J ----
                Arguments.of("天截断-DDD",     sh(2026,  4, 26, 14, 45, 12, nano), "DDD",   sh(2026,  4, 26,  0,  0,  0)),
                Arguments.of("天截断-DD",      sh(2026,  4, 25, 14, 45, 12, nano), "DD",    sh(2026,  4, 25,  0,  0,  0)),
                Arguments.of("天截断-J",       sh(2026,  4, 20,  0,  0,  1, nano), "J",     sh(2026,  4, 20,  0,  0,  0)),

                // ---- 周 DAY / DY / D（Oracle 以周日为一周第一天）----
                // 2026-04-26 为周日，本身即周首日
                Arguments.of("周截断-DAY",     sh(2026,  4, 26, 16, 19, 19, nano), "DAY",   sh(2026,  4, 26,  0,  0,  0)),
                // 2026-04-25（周六）往前 6 天到周日 2026-04-19
                Arguments.of("周截断-DY",      sh(2026,  4, 25, 16, 19, 19, nano), "DY",    sh(2026,  4, 19,  0,  0,  0)),
                // 2026-04-20（周一）往前 1 天到周日 2026-04-19
                Arguments.of("周截断-D",       sh(2026,  4, 20,  0,  0,  1, nano), "D",     sh(2026,  4, 19,  0,  0,  0)),

                // ---- 小时 HH / HH12 / HH24 ----
                Arguments.of("小时截断-HH",    sh(2024,  5, 23, 18, 59, 59, nano), "HH",    sh(2024,  5, 23, 18,  0,  0)),
                Arguments.of("小时截断-HH12",  sh(2024,  5, 23, 18,  0,  1, nano), "HH12",  sh(2024,  5, 23, 18,  0,  0)),
                Arguments.of("小时截断-HH24",  sh(2024,  5, 23, 18, 31, 59, nano), "HH24",  sh(2024,  5, 23, 18,  0,  0)),

                // ---- 分钟 MI ----
                Arguments.of("分钟截断-MI",    sh(2024,  5, 23, 18, 55, 59, nano), "MI",    sh(2024,  5, 23, 18, 55,  0))
        );
    }

    @DisplayName("truncWithZone(zoneId, instant, format) 表达式测试（上海时区）")
    @ParameterizedTest(name = "【{index}】{0}: input={1}, format={2}")
    @MethodSource("testTruncWithZoneProvider")
    public void testTruncWithZone(String caseId, Instant input, String format, Instant expected) {
        Object actual = AviatorInstance.execute(EXPR_THREE_ARGS, vars3("Asia/Shanghai", input, format));
        Assertions.assertEquals(expected, actual);
    }

    // =========================================================================
    // 无格式参数（截断到天）
    // =========================================================================

    @Test
    @DisplayName("truncWithZone(zoneId, instant) 表达式测试：无格式截断到天（上海时区）")
    public void testTruncWithZoneNoFormat() {
        Instant input    = sh(2026, 4, 25, 14, 45, 12, 123);
        Instant expected = sh(2026, 4, 25,  0,  0,  0);
        Object actual = AviatorInstance.execute(EXPR_TWO_ARGS, vars2("Asia/Shanghai", input));
        Assertions.assertEquals(expected, actual);
    }

    // =========================================================================
    // 异常场景：不支持的格式模型
    // =========================================================================

    @Test
    @DisplayName("truncWithZone(zoneId, instant, format) 表达式测试：不支持的格式（上海时区）")
    public void testTruncWithZoneInvalidFormat() {
        Instant input = sh(2026, 4, 22, 10, 14, 6, 123_456);
        assertThrows(IllegalArgumentException.class, () ->
                AviatorInstance.execute(EXPR_THREE_ARGS, vars3("Asia/Shanghai", input, "XX")));
    }
}
