DECLARE
    ------------------------------------------------------------------
    -- 业务场景：投资凭证提前到期偿付金额计算
    --
    -- 说明：Oracle SQL 使用 DATE 类型；对应的三个 Java 测试案例分别传入
    --       Instant / LocalDateTime / java.util.Date 三种不同的日期类型。
    --       日期均代表同一逻辑时刻 (Asia/Shanghai 时区)：
    --         发行日  = 2023-04-01 00:00:00 CST
    --         到期日  = 2025-10-01 00:00:00 CST
    --
    -- 涉及 Oracle 函数：MONTHS_BETWEEN、ADD_MONTHS、LAST_DAY、TRUNC（日期）、
    --                   TRUNC（数字）、ROUND、CEIL、FLOOR、ABS、MOD、
    --                   DECODE、NVL、COALESCE
    --
    -- 对应 Aviator 额外演示：
    --   • 日期字符串互转：instant_to_string / string_to_instant（Instant 分支）
    --                    local_datetime_to_string / string_to_local_datetime（LocalDateTime 分支）
    --                    Aviator 内置 date_to_string / string_to_date（Date 分支）
    --   • 位移运算符：>>（右移）、<<（左移）、>>>（无符号右移）
    --   • 一元负号运算符（NEG）：-penaltyBasis
    --   • decimal() 函数：将 Long 转换为精确的 OraDecimal
    --   • 各比较运算符：==、!=、<、<=、>、>=
    ------------------------------------------------------------------
    -- 入参数据（统一列出，方便修改）
    i_issue_date      DATE          := TO_DATE('2023-04-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'); -- 发行日（月初整点，两端日期同为1日，months_between 返回整数）
    i_maturity_date   DATE          := TO_DATE('2025-10-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'); -- 到期日（同为月初）
    i_grade_type      VARCHAR2(10)  := 'PRIME';    -- 等级类型：PRIME / STANDARD / BASIC
    i_principal       NUMBER        := 200000.256; -- 本金（不限精度 NUMBER）
    i_coupon_rate     NUMBER        := NULL;        -- 票息率（NULL → 触发 NVL 默认值 4.5）
    i_discount_factor NUMBER        := NULL;        -- 折扣系数（NULL → 触发 COALESCE 默认值）
    i_penalty_basis   NUMBER        := -3.25;       -- 违约基数（负数 → 触发 ABS）

    ------------------------------------------------------------------
    -- 内部变量定义
    ------------------------------------------------------------------
    v_months_diff     NUMBER;         -- MONTHS_BETWEEN 结果（整月数 = 30）
    v_year_str        VARCHAR2(4);    -- 发行年份字符串（'2023'）
    v_year_val        NUMBER;         -- 发行年份整数（2023）
    v_year_parity     NUMBER;         -- 年份奇偶性：MOD(year, 2)，0=偶数年，1=奇数年
                                      -- （Aviator 中用位移运算符演示相同效果）
    v_one_year_date   DATE;           -- 发行日 +12 个月（2024-04-01）
    v_last_day_date   DATE;           -- 到期月最后一天（2025-10-31，保留原时分秒 00:00:00）
    v_trunc_issue     DATE;           -- 发行日截断到天（已为零点，结果不变：2023-04-01）
    v_days_gap        NUMBER;         -- 日期差天数（2025-10-31 − 2023-04-01 = 944 天）
    v_grade_coef      NUMBER;         -- DECODE 等级系数
    v_eff_rate        NUMBER;         -- NVL 有效票息率（默认 4.5）
    v_final_rate      NUMBER;         -- COALESCE 最终费率
    v_abs_penalty     NUMBER;         -- ABS 违约基数绝对值（3.25）
    v_final_result    NUMBER;         -- 最终偿付金额

BEGIN
    ------------------------------------------------------------------
    -- 1. MONTHS_BETWEEN：计算到期日与发行日之间的月份数
    --    两端日期均为月初（1日），sameDayOfMonth 条件成立，返回精确整数。
    --    MONTHS_BETWEEN('2025-10-01', '2023-04-01') = 30
    ------------------------------------------------------------------
    v_months_diff := MONTHS_BETWEEN(i_maturity_date, i_issue_date); -- 30

    ------------------------------------------------------------------
    -- 2. TO_CHAR + TO_NUMBER + MOD：提取年份并计算奇偶性
    --    TO_CHAR('2023-04-01', 'YYYY')    = '2023'
    --    TO_NUMBER('2023')                = 2023
    --    MOD(2023, 2)                     = 1（奇数年）
    --
    --    Aviator 对应实现：通过右移/左移操作符（>> / <<）计算年份与原值的差
    --      yearHalf     = 2023 >> 1  = 1011
    --      yearRestored = 1011 << 1  = 2022
    --      uYearHalf    = 2023 >>> 1 = 1011（无符号右移，正数结果相同）
    --      yearParity   = 2023 − 2022 = 1  （等价于 MOD(2023, 2)）
    --    leapCheck = yearVal % 4 = 2023 % 4 = 3（非闰年，用于条件判断 leapCheck != 0）
    ------------------------------------------------------------------
    v_year_str    := TO_CHAR(i_issue_date, 'YYYY');      -- '2023'
    v_year_val    := TO_NUMBER(v_year_str);              -- 2023
    v_year_parity := MOD(v_year_val, 2);                 -- 1（奇数年）

    ------------------------------------------------------------------
    -- 3. ADD_MONTHS：发行日加 12 个月，作为参考截止日
    --    ADD_MONTHS('2023-04-01', 12) = '2024-04-01'
    ------------------------------------------------------------------
    v_one_year_date := ADD_MONTHS(i_issue_date, 12); -- 2024-04-01

    ------------------------------------------------------------------
    -- 4. LAST_DAY：取到期日所在月的最后一天（保留原时分秒）
    --    LAST_DAY('2025-10-01 00:00:00') = '2025-10-31 00:00:00'
    ------------------------------------------------------------------
    v_last_day_date := LAST_DAY(i_maturity_date); -- 2025-10-31

    ------------------------------------------------------------------
    -- 5. TRUNC（日期）：将发行日截断到天零点（已为 00:00:00，结果不变）
    --    TRUNC('2023-04-01 00:00:00') = '2023-04-01 00:00:00'
    ------------------------------------------------------------------
    v_trunc_issue := TRUNC(i_issue_date); -- 2023-04-01 00:00:00

    ------------------------------------------------------------------
    -- 6. 日期相减（整数天数）
    --    '2025-10-31 00:00:00' − '2023-04-01 00:00:00' = 944 天
    --
    --    验算：
    --      2023 年剩余（4月1日 → 12月31日）= 274 天
    --      2024 年全年（闰年）              = 366 天
    --      2025 年（1月1日 → 10月31日）    = 304 天
    --      合计                            = 944 天
    ------------------------------------------------------------------
    v_days_gap := v_last_day_date - v_trunc_issue; -- 944

    ------------------------------------------------------------------
    -- 7. DECODE：根据等级类型映射系数
    --    PRIME→1.30, STANDARD→1.15, BASIC→1.05, 默认→1.00
    --    注：PL/SQL 块内 DECODE 须通过 SELECT INTO 调用
    ------------------------------------------------------------------
    SELECT DECODE(i_grade_type, 'PRIME', 1.30, 'STANDARD', 1.15, 'BASIC', 1.05, 1.00)
    INTO v_grade_coef FROM DUAL;

    ------------------------------------------------------------------
    -- 8. NVL：票息率为 NULL 时取默认值 4.5
    ------------------------------------------------------------------
    v_eff_rate := NVL(i_coupon_rate, 4.5); -- 4.5

    ------------------------------------------------------------------
    -- 9. COALESCE：优先取折扣系数，两者均为 NULL 则取有效票息率
    ------------------------------------------------------------------
    v_final_rate := COALESCE(i_discount_factor, v_eff_rate); -- 4.5

    ------------------------------------------------------------------
    -- 10. ABS：取违约基数的绝对值
    --     ABS(-3.25) = 3.25
    --     Aviator 中额外用一元负号运算符（NEG）：-(-3.25) = 3.25
    ------------------------------------------------------------------
    v_abs_penalty := ABS(i_penalty_basis); -- 3.25

    ------------------------------------------------------------------
    -- 11. 主逻辑分支（对应 Aviator 的 if/elsif/else）
    --
    -- 核心公式（三个分支共用，仅系数不同）：
    --   ROUND(TRUNC(ABS(penalty), 1) × principal ÷ months, 2)
    --   + CEIL(days × gradeCoef)
    --   + FLOOR(finalRate × months + yearParity)
    --
    -- 共同计算过程：
    --   TRUNC(3.25, 1)            = 3.2
    --   ROUND(3.2 × 200000.256 ÷ 30, 2) = ROUND(21333.36064, 2) = 21333.36
    --   FLOOR(4.5 × 30 + 1)      = FLOOR(136) = 136
    --
    -- 分支1 (PRIME)：v_one_year_date(2024-04-01) < v_last_day_date(2025-10-31) → 真
    --   CEIL(944 × 1.30) = CEIL(1227.2) = 1228
    --   结果 = 21333.36 + 1228 + 136 = 22697.36
    --
    -- 分支2 (STANDARD)：v_months_diff(30) > 0 → 真
    --   CEIL(944 × 1.15) = CEIL(1085.6) = 1086
    --   结果 = 21333.36 + 1086 + 136 = 22555.36
    --
    -- 分支3 (BASIC)：进入 ELSE
    --   CEIL(944 × 1.05) = CEIL(991.2) = 992
    --   结果 = 21333.36 + 992 + 136 = 22461.36
    ------------------------------------------------------------------
    IF i_grade_type = 'PRIME' AND v_one_year_date < v_last_day_date THEN
        -- PRIME 分支
        v_final_result := ROUND(TRUNC(v_abs_penalty, 1) * i_principal / v_months_diff, 2)
                        + CEIL(v_days_gap * v_grade_coef)
                        + FLOOR(v_final_rate * v_months_diff + v_year_parity);

    ELSIF i_grade_type = 'STANDARD' AND v_months_diff > 0 THEN
        -- STANDARD 分支
        v_final_result := ROUND(TRUNC(v_abs_penalty, 1) * i_principal / v_months_diff, 2)
                        + CEIL(v_days_gap * v_grade_coef)
                        + FLOOR(v_final_rate * v_months_diff + v_year_parity);

    ELSE
        -- BASIC 及其他分支
        v_final_result := ROUND(TRUNC(v_abs_penalty, 1) * i_principal / v_months_diff, 2)
                        + CEIL(v_days_gap * v_grade_coef)
                        + FLOOR(v_final_rate * v_months_diff + v_year_parity);
    END IF;

    DBMS_OUTPUT.PUT_LINE('--- 中间变量 ---');
    DBMS_OUTPUT.PUT_LINE('months_diff   = ' || v_months_diff);
    DBMS_OUTPUT.PUT_LINE('year_val      = ' || v_year_val);
    DBMS_OUTPUT.PUT_LINE('year_parity   = ' || v_year_parity);
    DBMS_OUTPUT.PUT_LINE('one_year_date = ' || TO_CHAR(v_one_year_date,   'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE('last_day_date = ' || TO_CHAR(v_last_day_date,   'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE('trunc_issue   = ' || TO_CHAR(v_trunc_issue,     'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE('days_gap      = ' || v_days_gap);
    DBMS_OUTPUT.PUT_LINE('grade_coef    = ' || v_grade_coef);
    DBMS_OUTPUT.PUT_LINE('eff_rate      = ' || v_eff_rate);
    DBMS_OUTPUT.PUT_LINE('final_rate    = ' || v_final_rate);
    DBMS_OUTPUT.PUT_LINE('abs_penalty   = ' || v_abs_penalty);
    DBMS_OUTPUT.PUT_LINE('--- 最终结果 ---');
    DBMS_OUTPUT.PUT_LINE(v_final_result);
END;
/
/*
======================================================================
三个案例的执行结果（修改 i_grade_type 分别运行）：

months_diff   = 30
year_val      = 2023
year_parity   = 1
one_year_date = 2024-04-01
last_day_date = 2025-10-31
trunc_issue   = 2023-04-01
days_gap      = 944
eff_rate      = 4.5
final_rate    = 4.5
abs_penalty   = 3.25

Case 1: i_grade_type='PRIME',    grade_coef=1.30, CEIL(944×1.30)=1228, result = 22697.36
Case 2: i_grade_type='STANDARD', grade_coef=1.15, CEIL(944×1.15)=1086, result = 22555.36
Case 3: i_grade_type='BASIC',    grade_coef=1.05, CEIL(944×1.05)=992,  result = 22461.36
======================================================================
*/
