# aviator-oracle
基于 Aviator 的兼容 Oracle 数据库的精度与函数的表达式引擎。

## 新增数据类型`OraDecimal`，用于保持与Oracle相同的精度

`OraDecimal`与`BieDecimal`一样，都是不可变类型。内持有了一个`BieDecimal`对象，每次计算都使用该`BieDecimal`对象。

通过每次计算完成后，根据Oracle的逻辑保留`BieDecimal`对象的精度，实现与Oracle的`NUMBER`类型的精度的统一。

### Oracle数据库NUMBER类型精度逻辑

- 不指定精度时，则默认整数+小数共40位。
- 如果整数为0，则整数不占位，小数部分可以达到40位。
- 整数部分占位一定是偶数。比如整数3位或4位，小数部分最多都是36位。
- 如果整数部分为0，小数位开头的所有0都不算在40位中。
- 负号不占位，不影响以上判断，直接去掉负号当正数判断即可。

### 将新增数据类型`OraDecimal`接入aviator

由于aviator框架将类型与计算符号之间的关联全部写死，根本没有留下新增数据类型的方案，所以如果简单的新增一个Number类型，只会被作为一个java对象来操作，无法具备Number类型的各种特性。

解决这个难题需要分2步：

1. 重写部分继承`AviatorObject`的基本类型：通过将原本的基本类型复制出来，然后对其稍作修改，保留其原本功能的前提下增加对`OraDecimal`类型的支持，删除不需要的逻辑。
2. 重新实现涉及上述基本类型的计算符号：通过`addOpFunction`重写所有涉及到的符号（目前共20个），能够用基本类型实现的全部通过基本类型实现，这样可以保留原本框架的全部特性；不能用基本类型实现的则自己实现。

## 支持的日期类型

- `java.time.Instant`
- `java.time.LocalDateTime`
- `java.util.Date`

由于aviator本身支持`java.util.Date`类型，所以新增了另外两个日期类型与字符串之间，相互转换的工具方法。

### 函数`instant_to_string(instant, format, zoneId)`

`instant`根据日期格式字符串`format`和时区`zoneId`，转换成字符串。

### 函数`string_to_instant(string, format, zoneId)`

字符串`string`根据日期格式字符串`format`和时区`zoneId`，转换`java.time.Instant`对象。

### 函数`local_datetime_to_string(dateTime, format, zoneId)`

`dateTime`根据日期格式字符串`format`和时区`zoneId`，转换成字符串。

### 函数`string_to_local_datetime(string, format, zoneId)`

字符串`string`根据日期格式字符串`format`和时区`zoneId`，转换`java.time.LocalDateTime`对象。

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
