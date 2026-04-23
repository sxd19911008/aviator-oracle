package com.eredar.aviatororacle.runtime.utils.oracle;

import com.eredar.aviatororacle.number.OraDecimal;
import com.eredar.aviatororacle.runtime.constants.AviatorOracleConstants;
import com.eredar.aviatororacle.utils.AOUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 * 基于 {@link Date} 实现Oracle数据库日期操作
 */
public class OracleDateUtils {

    /**
     * 模拟 Oracle 数据库: Date对象 + 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link Date} 类型的日期对象
     */
    public static Date datePlusDays(Date date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }
        // 计算总秒数
        long seconds = daysToSeconds(days);
        // 在原始毫秒时间戳上加上总秒数对应的毫秒数，返回新的 Date 对象
        return new Date(date.getTime() + seconds * 1000L);
    }

    /**
     * 模拟 Oracle 数据库: Date对象 - 数字
     * <p>小数部分会根据一天有多少秒换算成秒数，四舍五入精确到秒数
     *
     * @param date 日期对象
     * @param days 天数，可以带小数
     * @return {@link Date} 类型的日期对象
     */
    public static Date dateMinusDays(Date date, Number days) {
        if (date == null || days == null) {
            throw new IllegalArgumentException(String.format("Params cannot be null: date=%s; days=%s", date, days));
        }
        // 计算总秒数
        long seconds = daysToSeconds(days);
        // 在原始毫秒时间戳上减去总秒数对应的毫秒数，返回新的 Date 对象
        return new Date(date.getTime() - seconds * 1000L);
    }

    /**
     * 将天数换算成 {@code Long} 型的秒数
     * <p>必定返回整数，如果有小数部分，则四舍五入
     *
     * @param days 天数，可以带小数
     * @return 天数对应的秒数
     */
    private static long daysToSeconds(Number days) {
        if (days instanceof Long || days instanceof Integer || days instanceof BigInteger) {
            return days.longValue() * AviatorOracleConstants.SECONDS_OF_DAY_LONG;
        } else if (days instanceof OraDecimal) {
            return ((OraDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        } else if (days instanceof BigDecimal) {
            return new OraDecimal((BigDecimal) days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        } else {
            return OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL) // 乘以1天的秒数，计算总秒数
                    .setScale(0) // 四舍五入保留整数
                    .longValueExact(); // 转换成long类型，如果溢出则报错
        }
    }

    /**
     * 计算两个 {@link Date} 之间的天数差 (endDate - beginDate)
     *
     * @param endDate   被减数 (结束时间)
     * @param beginDate 减数 (起始时间)
     * @return 差值天数 ({@code OraDecimal})
     */
    public static OraDecimal daysBetween(Date endDate, Date beginDate) {
        // 校验参数，为 null 直接报错
        if (endDate == null || beginDate == null) {
            throw new IllegalArgumentException(String.format("endDate[%s] and beginDate[%s] cannot be null", endDate, beginDate));
        }
        // 将毫秒时间戳转换为秒
        long endSeconds = endDate.getTime() / 1000;
        long beginSeconds = beginDate.getTime() / 1000;
        // 获取总秒数差
        OraDecimal secondsDiff = OraDecimal.valueOf(endSeconds - beginSeconds);
        // 计算天数，Oracle日期相减场景违反正常的精度逻辑，强行保留40位小数
        return secondsDiff.divide(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL, 40);
        /*
         * 【存疑】计算逻辑为先按照 Oracle 的 number 类型四舍五入保留小数，再强行保留40位小数。
         * 无法验证 Oracle 处理方式是否相同，因为找不到合适的临界小数。
         * 由于 Oracle 计算时一定除以一天的秒数 86400，且 number 的无整数位的小数如果开头有0，
         * 一定是偶数0不占用数字位数（number是20位的内存单位，每一位内存单位代表2位数字），
         * 导致我用任何办法都无法找出临界数字。我尝试过 11、29 等数字，都不行。
         * 由于这里的精度差异可以忽略不计，且目前的逻辑未必错误，所以等遇到这个临界数字时再做处理。
         */
    }

    /**
     * 2个日期之间间隔的月份
     *
     * @param endDate   结束日期
     * @param beginDate 起始日期
     * @return 间隔月份
     */
    public static OraDecimal monthsBetween(Date endDate, Date beginDate) {
        /* 入参校验 */
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("日期参数不能为空");
        }

        // 支持负数计算
        OraDecimal sign = OraDecimal.ONE; // 正负号
        if (beginDate.after(endDate)) {
            // 交换两个日期，确保 beginDate <= endDate，并将最终结果取反
            Date tempDate = beginDate;
            beginDate = endDate;
            endDate = tempDate;
            sign = AviatorOracleConstants.NEG; // 设置为 -1
        }

        /* 使用 Calendar 解析日历字段，自动适配系统时区 */
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        /* 计算基础月份差 (年差 * 12 + 月差) */
        int yearsDiff = end.get(Calendar.YEAR) - begin.get(Calendar.YEAR);
        int monthsDiff = end.get(Calendar.MONTH) - begin.get(Calendar.MONTH);
        int totalMonths = yearsDiff * 12 + monthsDiff;
        OraDecimal months = OraDecimal.valueOf(totalMonths);

        /* 判断是否"同日"或"均为月末" */
        // 判断是否"同日"，比如1月12日与2月12日属于"同日"
        boolean sameDayOfMonth = begin.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH);
        // 判断是否"均为月末"，比如1月31日与2月28日属于"均为月末"
        boolean bothLastDayOfMonth = isLastDayOfMonth(begin) && isLastDayOfMonth(end);
        if (sameDayOfMonth || bothLastDayOfMonth) {
            // 乘以正负号
            return months.multiply(sign);
        }

        // 如果不满足上述条件，计算小数部分

        /* 计算时分秒各自折算成的秒数之差 */
        long secondOfBegin = begin.get(Calendar.HOUR_OF_DAY) * 3600L
                + begin.get(Calendar.MINUTE) * 60L
                + begin.get(Calendar.SECOND);
        long secondOfEnd = end.get(Calendar.HOUR_OF_DAY) * 3600L
                + end.get(Calendar.MINUTE) * 60L
                + end.get(Calendar.SECOND);
        long secondsByHours = secondOfEnd - secondOfBegin;

        /* 计算日期差，然后换算成秒 */
        long dayOfBegin = begin.get(Calendar.DAY_OF_MONTH);
        long dayOfEnd = end.get(Calendar.DAY_OF_MONTH);
        // 相差天数整数部分
        long days = dayOfEnd - dayOfBegin;
        // 换算成秒
        OraDecimal secondsByDays = OraDecimal.valueOf(days).multiply(AviatorOracleConstants.SECONDS_OF_DAY_ORA_DECIMAL);
        // 汇总时间差秒数（时分秒差 + 天数差换算成的秒数）
        OraDecimal seconds = OraDecimal.valueOf(secondsByHours).add(secondsByDays);

        /* 根据Oracle数据库规则，一个月强行视为31天。这里用剩余时间的总秒数，除以一个月的总秒数 */
        // 非整数部分的月份数
        OraDecimal monthsFraction = seconds.divide(AviatorOracleConstants.SECONDS_OF_MONTH);

        /* 汇总计算结果并返回 */
        // 汇总相加后乘以正负号
        months = months.add(monthsFraction).multiply(sign);
        return months;
    }

    /**
     * 判断 {@link Calendar} 是否是所在月份的最后一天
     */
    private static boolean isLastDayOfMonth(Calendar date) {
        // getActualMaximum 会根据当前年份/月份动态计算该月的最大天数（含闰年判断）
        return date.get(Calendar.DAY_OF_MONTH) == date.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // =====================================================================
    //  ADD_MONTHS(date, months)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code ADD_MONTHS(date, months)} 函数：为日期加上指定月数。
     * <p>
     * Oracle 的 {@code ADD_MONTHS} 行为规则：
     * <ol>
     *   <li>月数参数会被截断为整数（小数部分直接丢弃，与 Oracle 行为一致）</li>
     *   <li>如果原始日期是所在月份的最后一天，则结果一定是目标月份的最后一天
     *       （例如 2024-02-29 + 1个月 → 2024-03-31）</li>
     *   <li>如果原始日期的日号大于目标月份的最大天数，则结果为目标月份的最后一天
     *       （例如 2024-01-31 + 1个月 → 2024-02-29）</li>
     *   <li>时分秒部分保持不变</li>
     *   <li>月数可以为负数，表示向前回退月份</li>
     * </ol>
     *
     * @param date   日期对象；不允许为 {@code null}
     * @param months 要增加的月数（Number 类型），小数部分会被截断
     * @return 加上指定月数后的新 {@link Date} 对象
     * @throws IllegalArgumentException 如果 {@code date} 或 {@code months} 为 {@code null}
     */
    public static Date addMonths(Date date, Number months) {
        /* 入参校验 */
        if (date == null || months == null) {
            throw new IllegalArgumentException(
                    String.format("参数不能为空: date=%s; months=%s", date, months));
        }

        // Oracle 会将 months 截断为整数（不是四舍五入）
        int monthsToAdd = months.intValue();

        // 为0不用计算，直接返回
        if (monthsToAdd == 0) {
            return new Date(date.getTime());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // 记录原始日期的日号
        int originalDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        // 判断原始日期是否是所在月份的最后一天
        boolean wasLastDayOfMonth = isLastDayOfMonth(cal);

        // 增加月份（Calendar.add 会自动处理年份进位/借位）
        cal.add(Calendar.MONTH, monthsToAdd);

        if (wasLastDayOfMonth) {
            // 规则2：原始日期是月末，结果强制设为目标月份的最后一天
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            // 规则3：原始日号超过目标月的最大天数时，设为目标月的最后一天
            int maxDayOfTargetMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (originalDayOfMonth > maxDayOfTargetMonth) {
                cal.set(Calendar.DAY_OF_MONTH, maxDayOfTargetMonth);
            } else {
                // 正常情况：保持原始日号（Calendar.add 可能会自动调整日号，需要还原）
                cal.set(Calendar.DAY_OF_MONTH, originalDayOfMonth);
            }
        }

        return cal.getTime();
    }

    // =====================================================================
    //  LAST_DAY(date)
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code LAST_DAY(date)} 函数：返回给定日期所在月份的最后一天。
     * <p>
     * Oracle 的 {@code LAST_DAY} 行为规则：
     * <ul>
     *   <li>返回日期所在月份的最后一天，会正确处理闰年（如2月28日/29日）</li>
     *   <li>时分秒部分保持不变</li>
     * </ul>
     *
     * @param date 日期对象；不允许为 {@code null}
     * @return 该日期所在月份最后一天的新 {@link Date} 对象（保留原始时分秒）
     * @throws IllegalArgumentException 如果 {@code date} 为 {@code null}
     */
    public static Date lastDay(Date date) {
        /* 入参校验 */
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空: date=null");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // 将日号设为当月的最大天数（getActualMaximum 会自动处理闰年和不同月份的天数差异）
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return cal.getTime();
    }

    // =====================================================================
    //  TRUNC(date [, format])
    // =====================================================================

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date)} 函数：将日期截断到天（当天午夜零点）。
     * <p>等价于 {@code TRUNC(date, 'DD')}
     *
     * @param date 日期对象；为 {@code null} 时返回 {@code null}
     * @return 截断到天的新 {@link Date} 对象
     */
    public static Date truncDate(Date date) {
        return truncDate(date, "DD");
    }

    /**
     * 模拟 Oracle 数据库的 {@code TRUNC(date, format)} 函数：按指定格式模型截断日期。
     * <p>
     * 支持的格式模型（不区分大小写）：
     * <ul>
     *   <li>{@code CC / SCC}：世纪——截断到当前世纪首年的 1 月 1 日（如 2026 年 → 2001-01-01）</li>
     *   <li>{@code SYYYY / YYYY / YEAR / SYEAR / YYY / YY / Y}：年——截断到当年 1 月 1 日</li>
     *   <li>{@code IYYY / IYY / IY / I}：ISO 年——截断到 ISO 第1周的周一</li>
     *   <li>{@code Q}：季度——截断到本季度第1天</li>
     *   <li>{@code MONTH / MON / MM / RM}：月——截断到本月第1天</li>
     *   <li>{@code WW}：年内周——截断到与本年 1 月 1 日同星期的最近日期</li>
     *   <li>{@code W}：月内周——截断到与本月 1 日同星期的最近日期</li>
     *   <li>{@code IW}：ISO 周——截断到 ISO 周的第一天（周一）</li>
     *   <li>{@code DDD / DD / J}：天——截断到当天零点（默认）</li>
     *   <li>{@code DAY / DY / D}：周——截断到本周第一天（Oracle 以周日为一周起始）</li>
     *   <li>{@code HH / HH12 / HH24}：小时——分钟与秒清零</li>
     *   <li>{@code MI}：分钟——秒清零</li>
     * </ul>
     *
     * @param date   日期对象；为 {@code null} 时返回 {@code null}
     * @param format 格式模型（不区分大小写）；为 {@code null} 或空串时等价于 {@code "DD"}
     * @return 截断后的新 {@link Date} 对象
     * @throws IllegalArgumentException 如果 {@code format} 是不支持的格式模型
     */
    public static Date truncDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        // format 为空时默认截断到天
        if (AOUtils.isBlank(format)) {
            throw new IllegalArgumentException("");
        }
        // 转大写，统一处理
        String fmtUpper = format.trim().toUpperCase();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        switch (fmtUpper) {

            /* ---- 世纪 ---- */
            case "CC":
            case "SCC": {
                int year = cal.get(Calendar.YEAR);
                // Oracle 世纪从 1, 101, 201, ..., 1901, 2001 ... 开始
                // 公式：世纪首年 = floor((year - 1) / 100) * 100 + 1
                int centuryStartYear = ((year - 1) / 100) * 100 + 1;
                cal.set(centuryStartYear, Calendar.JANUARY, 1);
                clearTimeFields(cal);
                break;
            }

            /* ---- 年 ---- */
            case "SYYYY":
            case "YYYY":
            case "YEAR":
            case "SYEAR":
            case "YYY":
            case "YY":
            case "Y":
                // 截断到当年 1 月 1 日 00:00:00
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                clearTimeFields(cal);
                break;

            /* ---- ISO 年 ---- */
            case "IYYY":
            case "IYY":
            case "IY":
            case "I":
                // 截断到 ISO 年第1周的周一
                cal = truncToIsoYear(cal);
                break;

            /* ---- 季度 ---- */
            case "Q": {
                // Calendar.MONTH 从 0 开始，季度首月为 0、3、6、9
                int month = cal.get(Calendar.MONTH);
                int quarterStartMonth = (month / 3) * 3;
                cal.set(Calendar.MONTH, quarterStartMonth);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                clearTimeFields(cal);
                break;
            }

            /* ---- 月 ---- */
            case "MONTH":
            case "MON":
            case "MM":
            case "RM":
                // 截断到本月第1天 00:00:00
                cal.set(Calendar.DAY_OF_MONTH, 1);
                clearTimeFields(cal);
                break;

            /* ---- 年内周 (WW)：与本年 1 月 1 日同星期的最近日期 ---- */
            case "WW": {
                // DAY_OF_YEAR 从 1 起，减 1 得到 0-indexed 的年内偏移天数
                // 该偏移对 7 取余，即为距上一个与 1 月 1 日同星期日期的天数差
                int dayOfYearOffset = cal.get(Calendar.DAY_OF_YEAR) - 1;
                int daysBack = dayOfYearOffset % 7;
                cal.add(Calendar.DAY_OF_MONTH, -daysBack);
                clearTimeFields(cal);
                break;
            }

            /* ---- 月内周 (W)：与本月 1 日同星期的最近日期 ---- */
            case "W": {
                // DAY_OF_MONTH 从 1 起，减 1 得到 0-indexed 的月内偏移天数
                int dayOfMonthOffset = cal.get(Calendar.DAY_OF_MONTH) - 1;
                int daysBack = dayOfMonthOffset % 7;
                cal.add(Calendar.DAY_OF_MONTH, -daysBack);
                clearTimeFields(cal);
                break;
            }

            /* ---- ISO 周 (IW)：ISO 周的第一天（周一）---- */
            case "IW":
                cal = truncToIsoWeek(cal);
                break;

            /* ---- 天（默认）---- */
            case "DDD":
            case "DD":
            case "J":
                clearTimeFields(cal);
                break;

            /* ---- 周（Oracle 以周日作为一周的第一天）---- */
            case "DAY":
            case "DY":
            case "D": {
                // Calendar.SUNDAY = 1，SATURDAY = 7
                // 将星期偏移量减去 Calendar.SUNDAY(1) 即可回退到本周周日
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                cal.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - Calendar.SUNDAY));
                clearTimeFields(cal);
                break;
            }

            /* ---- 小时 ---- */
            case "HH":
            case "HH12":
            case "HH24":
                // 保留小时，将分钟、秒、毫秒清零
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;

            /* ---- 分钟 ---- */
            case "MI":
                // 保留分钟，将秒和毫秒清零
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;

            default:
                throw new IllegalArgumentException(String.format("trunc方法不支持传入format为[%s]", format));
        }

        return cal.getTime();
    }

    /**
     * 截断到 ISO 年的第一天（ISO 第1周的周一）。
     * <p>
     * ISO 8601 规定：包含该年第一个周四的那一周是第1周，且 ISO 周以周一为起始。
     * 因此 ISO 年的第一天可能早于日历年的 1 月 1 日（最多早 3 天）。
     * <p>
     * 算法：
     * <ol>
     *   <li>确定目标日期所属的 ISO 年（仅 1 月和 12 月边界处与日历年不同）</li>
     *   <li>取该 ISO 年的 1 月 4 日——根据 ISO 8601，1 月 4 日必在第1周内</li>
     *   <li>回退到该周的周一，即为 ISO 年的第一天</li>
     * </ol>
     */
    private static Calendar truncToIsoYear(Calendar cal) {
        int isoYear = getIsoYear(cal);

        // ISO 第1周必包含 isoYear 年的 1 月 4 日
        Calendar jan4 = Calendar.getInstance();
        jan4.set(isoYear, Calendar.JANUARY, 4);
        clearTimeFields(jan4);

        // 计算 jan4 是 ISO 周的周几
        int isoDayOfWeek = getIsoDayOfWeek(jan4);
        // 回退 (isoDay - 1) 天即可到达本周周一
        jan4.add(Calendar.DAY_OF_MONTH, -(isoDayOfWeek - 1));

        return jan4;
    }

    /**
     * 截断到 ISO 周的第一天（周一）。
     * <p>ISO 8601 规定 ISO 周从周一开始，周日是一周的最后一天。
     */
    private static Calendar truncToIsoWeek(Calendar cal) {
        Calendar result = (Calendar) cal.clone();
        // 计算 result 是 ISO 周的周几
        int isoDayOfWeek = getIsoDayOfWeek(result);
        // 回退到本周周一
        result.add(Calendar.DAY_OF_MONTH, -(isoDayOfWeek - 1));
        clearTimeFields(result);
        return result;
    }

    /**
     * 获取日期所属的 ISO 年编号。
     * <p>
     * ISO 年与日历年在 1 月和 12 月的边界处可能不同：
     * <ul>
     *   <li>12 月末：若所属 ISO 周的周四在下一年 1 月，则该日期属于下一 ISO 年</li>
     *   <li>1 月初：若所属 ISO 周的周四在上一年 12 月，则该日期属于上一 ISO 年</li>
     * </ul>
     * 判断依据：<b>ISO 年 = 该日期所在 ISO 周的周四所在的日历年</b>
     */
    private static int getIsoYear(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        /* 只有 1 月和 12 月才可能属于不同的 ISO 年 */
        if (month != Calendar.JANUARY && month != Calendar.DECEMBER) {
            return year;
        }

        /* 计算 cal 所在 ISO 周的周四 */
        // 计算 cal 是 ISO 周的周几
        int isoDayOfWeek = getIsoDayOfWeek(cal);
        // 计算 ISO 周四: 先克隆入参日期
        Calendar thursday = (Calendar) cal.clone();
        // 计算 ISO 周四: 通过[4 - isoDayOfWeek]，得到 cal 相对 周四 的偏移量，然后相加得到周四日期
        thursday.add(Calendar.DAY_OF_MONTH, 4 - isoDayOfWeek);

        /* ISO 年 = 周四所在的日历年 */
        return thursday.get(Calendar.YEAR);
    }

    /**
     * 计算该日期，属于所在 ISO 周的周几
     * <p>ISO 周的周一到周日与数字1到7是对应的
     */
    private static int getIsoDayOfWeek(Calendar cal) {
        // Java: 周一到周日是[2,3,4,5,6,7,1]
        int javaDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // 转换为 ISO: 周一到周日是[1~7]
        return (javaDayOfWeek == Calendar.SUNDAY) ? 7 : (javaDayOfWeek - 1);
    }

    /**
     * 将 {@link Calendar} 的时、分、秒、毫秒全部清零（即设为当天 00:00:00.000）。
     */
    private static void clearTimeFields(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
