# aviator-oracle
Oracle-compatible expression engine powered by Aviator.

### 添加计算逻辑：

1. 支持`Instant - Instant`，得到`OraDecimal`类型的天数，可以为负数。
2. 支持`Instant`与`Number`类型相加（谁在前谁在后都一样），`Number`类型为天数（支持小数），计算得到`Instant`对象
3. 支持`Instant`与`Number`类型相减（必须`Instant`类型在前），`Number`类型为天数（支持小数），计算得到`Instant`对象
4. 不支持通过`aviator.importFunctions(Utils.class)`添加工具方法，因为不方便重写`ClassMethodFunction`。


TODO

1. 做复杂公式的单测，尽量用到aviator语法。该单测属于aviator单测
2. 添加单测案例，针对LocalDateTime与String的转换