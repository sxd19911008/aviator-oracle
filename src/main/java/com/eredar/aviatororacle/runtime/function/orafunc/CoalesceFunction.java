package com.eredar.aviatororacle.runtime.function.orafunc;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * Oracle数据库的 {@code coalesce()} 方法
 * <p>
 * 返回参数列表中第一个非 {@code null} 的值。
 * 如果所有参数都为 {@code null}，则返回 {@code null}。
 * 该方法至少需要 2 个参数，仅传入 1 个参数时将抛出异常。
 * </p>
 */
public class CoalesceFunction extends AbstractFunction {

    private static final long serialVersionUID = 2748193056482917365L;

    @Override
    public String getName() {
        return "coalesce";
    }

    /**
     * 执行 {@code coalesce} 方法：返回第一个非 {@code null} 的参数
     *
     * @param env  参数上下文
     * @param args AviatorObject 可变参数数组
     * @return 第一个非 {@code null} 的 {@code AviatorObject}；全部为 {@code null} 时返回 {@link AviatorNil#NIL}
     */
    private AviatorObject callCoalesce(Map<String, Object> env, AviatorObject... args) {
        for (AviatorObject arg : args) {
            // AviatorNil 类型代表 null
            if (arg instanceof AviatorNil) {
                continue;
            }
            // 实际取值判断是否为 null
            if (arg.getValue(env) != null) {
                return arg;
            }
        }
        return AviatorNil.NIL;
    }

    /**
     * 仅 1 个参数时抛出异常，coalesce 至少需要 2 个参数
     */
    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        throw new IllegalArgumentException("coalesce方法最少需要2个入参");
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2) {
        return callCoalesce(env, arg1, arg2);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3) {
        return callCoalesce(env, arg1, arg2, arg3);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4) {
        return callCoalesce(env, arg1, arg2, arg3, arg4);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17, final AviatorObject arg18) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17, final AviatorObject arg18, final AviatorObject arg19) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17, final AviatorObject arg18, final AviatorObject arg19,
                              final AviatorObject arg20) {
        return callCoalesce(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20);
    }


    /**
     * 超过 20 个参数时，将固定参数与可变参数合并后统一处理
     */
    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17, final AviatorObject arg18, final AviatorObject arg19,
                              final AviatorObject arg20, final AviatorObject... args) {
        AviatorObject[] allArgs = new AviatorObject[20 + args.length];
        allArgs[0] = arg1;
        allArgs[1] = arg2;
        allArgs[2] = arg3;
        allArgs[3] = arg4;
        allArgs[4] = arg5;
        allArgs[5] = arg6;
        allArgs[6] = arg7;
        allArgs[7] = arg8;
        allArgs[8] = arg9;
        allArgs[9] = arg10;
        allArgs[10] = arg11;
        allArgs[11] = arg12;
        allArgs[12] = arg13;
        allArgs[13] = arg14;
        allArgs[14] = arg15;
        allArgs[15] = arg16;
        allArgs[16] = arg17;
        allArgs[17] = arg18;
        allArgs[18] = arg19;
        allArgs[19] = arg20;
        System.arraycopy(args, 0, allArgs, 20, args.length);
        return callCoalesce(env, allArgs);
    }
}
