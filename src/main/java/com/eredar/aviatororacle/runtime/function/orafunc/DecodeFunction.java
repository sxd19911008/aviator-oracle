package com.eredar.aviatororacle.runtime.function.orafunc;

import com.eredar.aviatororacle.runtime.utils.AORuntimeUtils;
import com.eredar.aviatororacle.runtime.utils.oracle.OraFuncUtils;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Arrays;
import java.util.Map;

/**
 * Oracle数据库的 {@code decode()} 方法
 */
public class DecodeFunction extends AbstractFunction {

    private static final long serialVersionUID = -3681077878460241439L;

    @Override
    public String getName() {
        return "decode";
    }

    /**
     * 执行 {@code decode} 方法
     *
     * @param env  参数上下文
     * @param args AviatorObject数组
     * @return {@code AviatorObject} 包装后的计算结果
     */
    private AviatorObject callDecode(Map<String, Object> env, AviatorObject... args) {
        // 获取所有数据，组成数组
        Object[] arr = Arrays.stream(args).map(arg -> arg.getValue(env)).toArray();
        // 执行 decode 方法
        Object res = OraFuncUtils.decode(arr);
        // 选择正确的包装类型并返回
        return AORuntimeUtils.wrapAviatorObject(res);
    }

    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1) {
        throw new IllegalArgumentException("decode方法入参数量只有1个，少于3个");
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2) {
        throw new IllegalArgumentException("decode方法入参数量只有2个，少于3个");
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3) {
        return callDecode(env, arg1, arg2, arg3);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4) {
        return callDecode(env, arg1, arg2, arg3, arg4);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17, final AviatorObject arg18) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18);
    }


    @Override
    public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
                              final AviatorObject arg2, final AviatorObject arg3, final AviatorObject arg4,
                              final AviatorObject arg5, final AviatorObject arg6, final AviatorObject arg7,
                              final AviatorObject arg8, final AviatorObject arg9, final AviatorObject arg10,
                              final AviatorObject arg11, final AviatorObject arg12, final AviatorObject arg13,
                              final AviatorObject arg14, final AviatorObject arg15, final AviatorObject arg16,
                              final AviatorObject arg17, final AviatorObject arg18, final AviatorObject arg19) {
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19);
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
        return callDecode(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20);
    }


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
        return callDecode(env, allArgs);
    }
}
