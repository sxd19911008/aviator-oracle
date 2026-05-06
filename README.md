# aviator-oracle
Oracle-compatible expression engine powered by Aviator.

## 支持的日期类型

- `java.time.Instant`
- `java.time.LocalDateTime`
- `java.util.Date`

## 添加计算逻辑：

1. 支持`日期 - 日期`，得到`OraDecimal`类型的天数，可以为负数。
2. 支持`日期`与`Number`类型相加（谁在前谁在后都一样），`Number`类型为天数（支持小数），计算得到`日期`对象。
3. 支持`日期`与`Number`类型相减（必须`日期`类型在前），`Number`类型为天数（支持小数），计算得到`日期`对象。

## 阉割掉的功能

不再支持通过`aviator.importFunctions(Utils.class)`添加工具方法，因为不方便重写`ClassMethodFunction`。只能通过`aviator.addFunction(new XxxFunction())`新增工具方法。

## 支持的Oracle函数

### 函数`add_months(date, months[, zoneId])`

对应 Oracle 的 `ADD_MONTHS(date, integer)` 函数，为日期加上指定月数。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 日期参数类型 | Oracle `DATE` / `TIMESTAMP` | `Date`、`LocalDateTime`、`Instant` |
| 额外参数 `zoneId` | 不支持 | 仅当第一个参数为 `Instant` 时，可传入第三个参数（字符串形式的时区 ID，如 `"Asia/Shanghai"`），不传则默认使用 UTC |

### 函数`months_between(date1, date2[, zoneId])`

对应 Oracle 的 `MONTHS_BETWEEN(date1, date2)` 函数，计算两个日期之间间隔的月份数，返回 `OraDecimal`。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 日期参数类型 | Oracle `DATE` / `TIMESTAMP` | `Date`、`LocalDateTime`、`Instant`（两个日期参数必须为相同类型） |
| 额外参数 `zoneId` | 不支持 | 仅当两个日期参数都为 `Instant` 时，可传入第三个参数（字符串形式的时区 ID），不传则默认使用 UTC |

### 函数`last_day(date[, zoneId])`

对应 Oracle 的 `LAST_DAY(date)` 函数，返回给定日期所在月份的最后一天，时分秒保持不变。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 日期参数类型 | Oracle `DATE` / `TIMESTAMP` | `Date`、`LocalDateTime`、`Instant` |
| 额外参数 `zoneId` | 不支持 | 仅当第一个参数为 `Instant` 时，可传入第二个参数（字符串形式的时区 ID），不传则默认使用 UTC |

### 函数`trunc(value[, param])`

对应 Oracle 的 `TRUNC` 函数，同时支持数字截断和日期截断两种用法。根据第一个参数的类型自动判断行为：

**数字截断** — `trunc(number[, scale])`：向零方向截断到指定小数位数（`RoundingMode.DOWN`）。`scale` 不传时默认为 0。

**日期截断** — `trunc(date[, format])`：按格式模型截断日期。`format` 不传时默认为 `'DD'`（截断到天）。支持的格式模型（不区分大小写）：`CC`、`SCC`、`SYYYY`、`YYYY`、`YEAR`、`SYEAR`、`YYY`、`YY`、`Y`、`IYYY`、`IYY`、`IY`、`I`、`Q`、`MONTH`、`MON`、`MM`、`RM`、`WW`、`W`、`IW`、`DDD`、`DD`、`J`、`DAY`、`DY`、`D`、`HH`、`HH12`、`HH24`、`MI`。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 数字参数类型 | Oracle `NUMBER` | Java `Number`（含 `OraDecimal`、`BigDecimal`、`Long` 等） |
| 日期参数类型 | Oracle `DATE` / `TIMESTAMP` | `Date`、`LocalDateTime`、`Instant` |
| `Instant` 日期截断的时区 | 不涉及 | 固定使用 UTC，如需指定时区请使用 `trunc_with_zone` |

### 函数`trunc_with_zone(zoneId, instant[, format])`

`trunc` 的时区版本，仅支持 `Instant` 类型。在指定时区下对日期进行截断，`format` 支持的值与 `trunc` 一致。该函数在 Oracle 中不存在，是本项目新增的扩展函数。

### 函数`round(number[, scale])`

对应 Oracle 的 `ROUND(number[, integer])` 函数，按指定位数四舍五入（`RoundingMode.HALF_UP`）。`scale` 不传时默认为 0。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 参数类型 | Oracle `NUMBER` | Java `Number`（含 `OraDecimal`、`BigDecimal`、`Long` 等） |
| `scale` 为 `null` | 等同于 `scale = 0` | 抛出 `IllegalArgumentException` |

### 函数`ceil(number)`

对应 Oracle 的 `CEIL(n)` 函数，返回大于或等于 `n` 的最小整数（向正无穷方向取整）。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 参数类型 | Oracle `NUMBER` | Java `Number`（含 `OraDecimal`、`BigDecimal`、`Long` 等） |

### 函数`floor(number)`

对应 Oracle 的 `FLOOR(n)` 函数，返回小于或等于 `n` 的最大整数（向负无穷方向取整）。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 参数类型 | Oracle `NUMBER` | Java `Number`（含 `OraDecimal`、`BigDecimal`、`Long` 等） |

### 函数`abs(number)`

对应 Oracle 的 `ABS(n)` 函数，返回 `n` 的绝对值。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 参数类型 | Oracle `NUMBER` | Java `Number`（含 `OraDecimal`、`BigDecimal`、`Long` 等） |

### 函数`power(number, exponent)`

对应 Oracle 的 `POWER(m, n)` 函数，返回 `m` 的 `n` 次幂，结果为 `OraDecimal` 类型。任一入参为 `null` 时返回 `null`。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 参数类型 | Oracle `NUMBER` | Java `Number`（含 `OraDecimal`、`BigDecimal`、`Long` 等） |

### 函数`nvl(expr1, expr2)`

对应 Oracle 的 `NVL(expr1, expr2)` 函数，如果 `expr1` 为 `null` 则返回 `expr2`，否则返回 `expr1`。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 类型约束 | 两个参数必须类型兼容 | 不强制要求参数类型一致（但不建议混用不同类型，会导致返回值类型不确定） |

### 函数`decode(expr, search1, result1[, search2, result2, ...][, default])`

对应 Oracle 的 `DECODE` 函数。逐个比较 `expr` 与各 `search` 值，匹配则返回对应 `result`；都不匹配时返回 `default`（未指定则返回 `null`）。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 不同类型比较 | 支持字符串与数字、字符串与日期互相比较 | 仅支持 `Number` 与可解析为数字的 `String` 之间的比较，不支持 `String` 与日期对象比较 |

### 函数`coalesce(expr1, expr2[, expr3, ...])`

对应 Oracle 的 `COALESCE` 函数，返回参数列表中第一个非 `null` 的值；全部为 `null` 时返回 `null`。最少需要 2 个参数。

| 差异点 | Oracle | aviator-oracle |
|---|---|---|
| 类型约束 | 所有参数必须类型兼容 | 不强制要求参数类型一致（但不建议混用不同类型，会导致返回值类型不确定） |
