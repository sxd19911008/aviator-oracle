DECLARE
    ------------------------------------------------------------------
    -- 入参数据（统一列出，方便修改）
    --
    -- 业务场景：债券/股票/基金产品的结算计算
    -- 涉及 Oracle 函数：MONTHS_BETWEEN、ADD_MONTHS、LAST_DAY、TRUNC、
    --                   DECODE（SQL层）、NVL、COALESCE、ABS、ROUND、CEIL、FLOOR
    ------------------------------------------------------------------
    -- 日期类型入参（UTC+8 本地时间，对应 Java Instant: 2024-01-15T02:30:00Z / 2024-04-20T08:45:00Z）
    i_trade_date    DATE          := TO_DATE('2024-01-15 10:30:00', 'YYYY-MM-DD HH24:MI:SS'); -- 交易日
    i_settle_date   DATE          := TO_DATE('2024-04-20 16:45:00', 'YYYY-MM-DD HH24:MI:SS'); -- 结算日
    -- 字符串类型入参（产品类型：BOND / STOCK / FUND）
    i_product_type  VARCHAR2(50)  := 'BOND';
    -- 数字类型入参
    i_face_value    NUMBER        := 100000.567;     -- 票面价值（不限精度）
    i_adj_factor    NUMBER        := -2.35;           -- 调整系数（负数，测试 ABS）
    i_discount_rate NUMBER        := NULL;            -- 折扣率（可为 NULL，测试 NVL）
    i_backup_rate   NUMBER        := NULL;            -- 备用费率（可为 NULL，测试 COALESCE）

    ------------------------------------------------------------------
    -- 内部变量定义
    ------------------------------------------------------------------
    v_months_diff   NUMBER;        -- MONTHS_BETWEEN: 结算日与交易日的月份差（含小数）
    v_added_date    DATE;          -- ADD_MONTHS: 交易日 + 3个月的参考日期
    v_last_day_date DATE;          -- LAST_DAY: 结算日所在月的最后一天
    v_trunc_date    DATE;          -- TRUNC: 交易日截断到天（去掉时分秒）
    v_days_gap      NUMBER;        -- 日期差（天数含小数）= last_day - trunc_trade
    v_product_coef  NUMBER;        -- DECODE: 产品类型对应系数（从 SQL 层获取）
    v_eff_rate      NUMBER;        -- NVL: 有效折扣率（null 时默认 5.0）
    v_final_rate    NUMBER;        -- COALESCE: 最终费率（备用率优先）
    v_final_result  NUMBER;        -- 最终计算结果

BEGIN
    ------------------------------------------------------------------
    -- 1. MONTHS_BETWEEN：计算结算日与交易日之间的月份数（含小数）
    --    结果 ≈ 3.16969086021505376344086021505376344086
    ------------------------------------------------------------------
    v_months_diff := MONTHS_BETWEEN(i_settle_date, i_trade_date);

    ------------------------------------------------------------------
    -- 2. ADD_MONTHS：在交易日基础上加 3 个月，得到参考日期
    --    2024-01-15 10:30:00 + 3个月 = 2024-04-15 10:30:00
    ------------------------------------------------------------------
    v_added_date := ADD_MONTHS(i_trade_date, 3);

    ------------------------------------------------------------------
    -- 3. LAST_DAY：取结算日所在月的最后一天
    --    LAST_DAY('2024-04-20 16:45:00') = '2024-04-30 16:45:00'
    ------------------------------------------------------------------
    v_last_day_date := LAST_DAY(i_settle_date);

    ------------------------------------------------------------------
    -- 4. TRUNC（日期）：将交易日截断到天（去掉时分秒）
    --    TRUNC('2024-01-15 10:30:00') = '2024-01-15 00:00:00'
    ------------------------------------------------------------------
    v_trunc_date := TRUNC(i_trade_date);

    ------------------------------------------------------------------
    -- 5. 日期差：结算日月末 - 截断后交易日 = 天数（含小数）
    --    '2024-04-30 16:45:00' - '2024-01-15 00:00:00' ≈ 106.69791666...
    ------------------------------------------------------------------
    v_days_gap := v_last_day_date - v_trunc_date;

    ------------------------------------------------------------------
    -- 6. DECODE（SQL 层）：根据产品类型取系数
    --    BOND→1.25, STOCK→1.50, FUND→1.10, 默认→1.00
    --    注：PL/SQL 块内不能直接调用 DECODE，需通过 SELECT INTO
    ------------------------------------------------------------------
    SELECT DECODE(i_product_type, 'BOND', 1.25, 'STOCK', 1.50, 'FUND', 1.10, 1.00)
    INTO v_product_coef
    FROM DUAL;

    ------------------------------------------------------------------
    -- 7. NVL：有效折扣率，NULL 时使用默认值 5.0
    ------------------------------------------------------------------
    v_eff_rate := NVL(i_discount_rate, 5.0);

    ------------------------------------------------------------------
    -- 8. COALESCE：优先使用备用费率，否则使用有效折扣率
    ------------------------------------------------------------------
    v_final_rate := COALESCE(i_backup_rate, v_eff_rate);

    ------------------------------------------------------------------
    -- 9. 主逻辑分支（对应 Aviator 的 if/elsif/else）：
    --    条件1 (BOND)：v_added_date < i_settle_date（即 Apr 15 < Apr 20，为真）
    --    条件2 (STOCK)：v_months_diff > 0（月份差大于0，为真）
    --    其余 (FUND等)：进入 ELSE 分支
    --
    -- 核心公式：
    --   ROUND(TRUNC(ABS(调整系数), 1) * 票面价值 / 月份差, 2)
    --   + CEIL(天数差 * 产品系数)
    --   + FLOOR(最终费率 * 月份差)
    --
    -- 共同使用的公式，分支仅用于演示三路分支结构
    ------------------------------------------------------------------
    IF i_product_type = 'BOND' AND v_added_date < i_settle_date THEN
        -- BOND 分支
        -- TRUNC(ABS(-2.35), 1) = 2.3
        -- ROUND(2.3 * 100000.567 / 3.16969..., 2) = 72562.69
        -- CEIL(106.6979... * 1.25) = CEIL(133.3723...) = 134
        -- FLOOR(5.0 * 3.16969...) = FLOOR(15.8484...) = 15
        -- 合计 = 72562.69 + 134 + 15 = 72711.69
        v_final_result := ROUND(TRUNC(ABS(i_adj_factor), 1) * i_face_value / v_months_diff, 2)
                        + CEIL(v_days_gap * v_product_coef)
                        + FLOOR(v_final_rate * v_months_diff);

    ELSIF i_product_type = 'STOCK' AND v_months_diff > 0 THEN
        -- STOCK 分支（discountRate=3.5 → nvl→3.5 → coalesce(NULL,3.5)=3.5）
        -- CEIL(106.6979... * 1.50) = CEIL(160.0468...) = 161
        -- FLOOR(3.5 * 3.16969...) = FLOOR(11.0939...) = 11
        -- 合计 = 72562.69 + 161 + 11 = 72734.69
        v_final_result := ROUND(TRUNC(ABS(i_adj_factor), 1) * i_face_value / v_months_diff, 2)
                        + CEIL(v_days_gap * v_product_coef)
                        + FLOOR(v_final_rate * v_months_diff);

    ELSE
        -- FUND 及其他分支（backupRate=4.2 → coalesce(4.2,5.0)=4.2）
        -- CEIL(106.6979... * 1.10) = CEIL(117.3677...) = 118
        -- FLOOR(4.2 * 3.16969...) = FLOOR(13.3127...) = 13
        -- 合计 = 72562.69 + 118 + 13 = 72693.69
        v_final_result := ROUND(TRUNC(ABS(i_adj_factor), 1) * i_face_value / v_months_diff, 2)
                        + CEIL(v_days_gap * v_product_coef)
                        + FLOOR(v_final_rate * v_months_diff);
    END IF;

    DBMS_OUTPUT.PUT_LINE('--- 中间变量 ---');
    DBMS_OUTPUT.PUT_LINE('months_diff       = ' || v_months_diff);
    DBMS_OUTPUT.PUT_LINE('added_date        = ' || TO_CHAR(v_added_date,    'YYYY-MM-DD HH24:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('last_day_date     = ' || TO_CHAR(v_last_day_date, 'YYYY-MM-DD HH24:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('trunc_trade_date  = ' || TO_CHAR(v_trunc_date,    'YYYY-MM-DD HH24:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('days_gap          = ' || v_days_gap);
    DBMS_OUTPUT.PUT_LINE('product_coef      = ' || v_product_coef);
    DBMS_OUTPUT.PUT_LINE('eff_rate          = ' || v_eff_rate);
    DBMS_OUTPUT.PUT_LINE('final_rate        = ' || v_final_rate);
    DBMS_OUTPUT.PUT_LINE('--- 最终结果 ---');
    DBMS_OUTPUT.PUT_LINE(v_final_result);
END;
/
/*
======================================================================
SELECT 语句验证（三个案例一次性输出，Oracle 执行结果如下）：

months_diff   = 3.16969086021505376344086021505376344086
added_date    = 2024-04-15 10:30:00  (ADD_MONTHS +3)
last_day_date = 2024-04-30 16:45:00  (LAST_DAY)
trunc_date    = 2024-01-15 00:00:00  (TRUNC to day)
days_gap      = 106.697916666666666666666666666666666667

Case 1: BOND,  discountRate=NULL→nvl→5.0,  backupRate=NULL→coalesce→5.0,  result = 72711.69
Case 2: STOCK, discountRate=3.5→nvl→3.5,   backupRate=NULL→coalesce→3.5,  result = 72734.69
Case 3: FUND,  discountRate=NULL→nvl→5.0,  backupRate=4.2→coalesce→4.2,   result = 72693.69
======================================================================
*/
