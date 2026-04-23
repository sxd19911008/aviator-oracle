package com.eredar.aviatororacle;

import com.eredar.aviatororacle.function.*;
import com.eredar.aviatororacle.function.orafunc.*;
import com.eredar.aviatororacle.utils.AOUtils;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.lexer.token.OperatorType;

import java.math.MathContext;
import java.math.RoundingMode;

/**
 * AviatorOracle 实例构建器
 */
public class AviatorOracleBuilder {

    // 最大缓存表达式数量
    private Integer useLRUExpressionCache;
    // 最大循环次数，防止死循环
    private Integer maxLoopCount;
    // 调试日志
    private Boolean traceEval;

    /**
     * AviatorOracleBuilder 构建方法
     *
     * @return AviatorOracleBuilder 对象
     */
    public static AviatorOracleBuilder builder() {
        return new AviatorOracleBuilder();
    }

    public AviatorOracleBuilder useLRUExpressionCache(Integer useLRUExpressionCache) {
        this.useLRUExpressionCache = useLRUExpressionCache;
        return this;
    }

    public AviatorOracleBuilder maxLoopCount(Integer maxLoopCount) {
        this.maxLoopCount = maxLoopCount;
        return this;
    }

    public AviatorOracleBuilder traceEval(Boolean traceEval) {
        this.traceEval = traceEval;
        return this;
    }

    public AviatorEvaluatorInstance build() {

        // 创建新的实例
        AviatorEvaluatorInstance aviator = AviatorEvaluator.newInstance();

        // 编译模式，表达式被直接翻译成 Java 字节码
        aviator.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.COMPILE);

        // 开启 LRU 缓存策略
        aviator.setCachedExpressionByDefault(true);

        // 设置最大缓存表达式数量
        aviator.useLRUExpressionCache(AOUtils.defaultIfNull(this.useLRUExpressionCache, 2048));

        // 设置decimal数字精度，四舍五入
        aviator.setOption(Options.MATH_CONTEXT, new MathContext(100, RoundingMode.HALF_UP));

        // 浮点数使用 decimal 类型
        aviator.setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL, true);

        // 语法糖配置：允许 policyInfo.applyDate 这种写法
        aviator.setOption(Options.ENABLE_PROPERTY_SYNTAX_SUGAR, true);

        // 容错配置：true：当访问对象属性为 null 时不抛异常，返回 null；false：直接报错。
        aviator.setOption(Options.NIL_WHEN_PROPERTY_NOT_FOUND, false);

        // 设置全局脚本执行超时时间为 5 秒。默认值为 0，表示永不超时
        aviator.setOption(Options.EVAL_TIMEOUT_MS, 5000L);

        // 最大循环次数，防止死循环
        aviator.setOption(Options.MAX_LOOP_COUNT, AOUtils.defaultIfNull(this.maxLoopCount, 10000));

        // 自定义算术运算
        aviator.addOpFunction(OperatorType.ADD, new AddFunction());
        aviator.addOpFunction(OperatorType.SUB, new SubFunction());
        aviator.addOpFunction(OperatorType.MULT, new MultFunction());
        aviator.addOpFunction(OperatorType.DIV, new DivFunction());
        aviator.addOpFunction(OperatorType.MOD, new ModFunction());
        aviator.addOpFunction(OperatorType.NEG, new NegFunction());
        aviator.addOpFunction(OperatorType.LT, new LtFunction());
        aviator.addOpFunction(OperatorType.LE, new LeFunction());
        aviator.addOpFunction(OperatorType.GT, new GtFunction());
        aviator.addOpFunction(OperatorType.GE, new GeFunction());
        aviator.addOpFunction(OperatorType.EQ, new EqFunction());
        aviator.addOpFunction(OperatorType.NEQ, new NeqFunction());
        aviator.addOpFunction(OperatorType.SHIFT_LEFT, new ShiftLeftFunction());
        aviator.addOpFunction(OperatorType.SHIFT_RIGHT, new ShiftRightFunction());
        aviator.addOpFunction(OperatorType.U_SHIFT_RIGHT, new UnsignedShiftRightFunction());

        // 使用自定义方法，替换框架方法
        aviator.addFunction(new AODecimalFunction());
        aviator.addFunction(new AOString2DateFunction());
        aviator.addFunction(new AODate2StringFunction());

        // 新增模拟Oracle数据库的方法
        aviator.addFunction(new DecodeFunction());
        aviator.addFunction(new NvlFunction());
        aviator.addFunction(new CoalesceFunction());
        aviator.addFunction(new AbsFunction());
        aviator.addFunction(new FloorFunction());
        aviator.addFunction(new CeilFunction());
        aviator.addFunction(new RoundFunction());
        aviator.addFunction(new TruncFunction());
        aviator.addFunction(new TruncWithZoneFunction());
        aviator.addFunction(new MonthsBetweenFunction());
        aviator.addFunction(new AddMonthsFunction());
        aviator.addFunction(new LastDayFunction());

        // 调试日志
        aviator.setOption(Options.TRACE_EVAL, AOUtils.defaultIfNull(this.traceEval, Boolean.FALSE));

        return aviator;
    }
}
