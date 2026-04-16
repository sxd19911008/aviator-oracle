DECLARE
    ------------------------------------------------------------------
    -- 6. 入参数据 (统一列出，方便修改)
    ------------------------------------------------------------------
    i_start_date    DATE          := TO_DATE('2024-02-28 08:00:00', 'YYYY-MM-DD HH24:MI:SS'); -- 起始日期（2024是闰年）
    i_end_date      DATE          := TO_DATE('2024-03-01 14:30:00', 'YYYY-MM-DD HH24:MI:SS'); -- 结束日期（跨越2月29日）
    i_category      VARCHAR2(50)  := 'STRATEGIC_PROJECT';                                     -- 字符串类型入参
    i_base_value    NUMBER        := 12500.885923;                                            -- 1. 不限制精度的Number
    i_ratio_limit   NUMBER(12, 2) := 85.50;                                                   -- 2. 限制精度的Number (12,2)
    i_extra_offset  NUMBER        := 2.75;                                                    -- 用于日期加法的偏移量（2天18小时）

    ------------------------------------------------------------------
    -- 内部变量定义
    ------------------------------------------------------------------
    v_days_diff     NUMBER;               -- 存储两个日期相减得到的days（带有小数）
    v_days_int      INTEGER;              -- 存储天数的整数部分
    v_new_date      DATE;                 -- 存储计算后的新日期
    v_leap_factor   NUMBER(10, 4) := 0;   -- 闰年修正系数
    v_final_result  NUMBER;               -- 7. 最终结果（数字）

BEGIN
    ------------------------------------------------------------------
    -- 3. 日期相减逻辑 (涉及闰年2月、整数与小数)
    ------------------------------------------------------------------
    -- Oracle日期相减默认得到天数，包含小数部分（由时分秒决定）
    v_days_diff := i_end_date - i_start_date;
    v_days_int  := FLOOR(v_days_diff); -- 提取整数天数

    -- 逻辑判断：如果跨越了闰年2月29日
    IF TO_CHAR(i_start_date, 'MMDD') <= '0229' AND TO_CHAR(i_end_date, 'MMDD') >= '0229'
        AND MOD(TO_NUMBER(TO_CHAR(i_start_date, 'YYYY')), 4) = 0 THEN
        v_leap_factor := 1.5; -- 发现闰年2月29日，给予额外权重
    ELSE
        v_leap_factor := 1.0;
    END IF;

    ------------------------------------------------------------------
    -- 4. 日期加法逻辑 (涉及新日期、整数与小数、闰年)
    ------------------------------------------------------------------
    -- 将起始日期加上一个带有小数的天数（1.75天），观察是否正确跳过或进入闰年日期
    v_new_date := i_start_date + i_extra_offset;

    ------------------------------------------------------------------
    -- 1 & 5. 复杂 IF-ELSE 判断及四则运算
    ------------------------------------------------------------------
    -- 判断涉及：字符串匹配、日期比较、数字大小比较
    IF i_category = 'STRATEGIC_PROJECT' AND v_new_date > TO_DATE('2024-02-29', 'YYYY-MM-DD') AND i_base_value > 10000 THEN
        -- 执行四则运算
        -- 逻辑：(基础值 * 比例) / 实际间隔天数 + (整数天数 * 权重)
        v_final_result := (i_base_value * i_ratio_limit) / v_days_diff + (v_days_int * v_leap_factor);
    ELSIF i_category = 'NORMAL' OR v_days_diff < 1 THEN
        -- 简单的减法和除法
        v_final_result := i_base_value - (i_ratio_limit / 2);

    ELSE
        -- 默认逻辑：乘法和加法
        v_final_result := i_base_value + v_days_diff * 1.1;
    END IF;

    DBMS_OUTPUT.PUT_LINE('--- 最终结果 ---');
    DBMS_OUTPUT.PUT_LINE(v_final_result);
END;