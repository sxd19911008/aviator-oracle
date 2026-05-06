# aviator-oracle
Oracle-compatible expression engine powered by Aviator.

### 支持的日期类型

- `java.time.Instant`
- `java.time.LocalDateTime`
- `java.util.Date`

### 添加计算逻辑：

1. 支持`日期 - 日期`，得到`OraDecimal`类型的天数，可以为负数。
2. 支持`日期`与`Number`类型相加（谁在前谁在后都一样），`Number`类型为天数（支持小数），计算得到`日期`对象。
3. 支持`日期`与`Number`类型相减（必须`日期`类型在前），`Number`类型为天数（支持小数），计算得到`日期`对象。

### 阉割掉的功能

不再支持通过`aviator.importFunctions(Utils.class)`添加工具方法，因为不方便重写`ClassMethodFunction`。只能通过`aviator.addFunction(new XxxFunction())`新增工具方法。
