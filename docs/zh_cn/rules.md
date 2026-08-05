# 规则

**提示：可以使用 `Ctrl+F` 快速查找自己想要的规则**

---

## 禁止紫水晶芽在水中生长 (disableAmethystWaterGrowth)

&emsp;阻止紫水晶芽长入满格的水中

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `CTA`, `FEATURE`


## 爆炸射线初始强度 (explosionRayInit)

&emsp;固定设置爆炸射线初始强度倍率。取值范围 0 到 16（原版为 0.7 到 1.3 之间的随机值），设为其他值则使用原版随机行为

&emsp;- 类型: `double`

&emsp;- 默认值: `-1.0`

&emsp;- 分类: `CTA`, `FEATURE`


## 防止极端初始动量TNT生成 (preventExtremeTntMomentum)

&emsp;防止TNT生成时初始动量为能够使其卡入方块碰撞箱的值

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `CTA`, `FEATURE`


## 设置TNT初速度x分量 (tntInitialXVelocity)

&emsp;设置TNT的x方向初速度，取值在-0.02到0.02之间，设为-1即为原版默认情况

&emsp;- 类型: `double`

&emsp;- 默认值: `-1.0`

&emsp;- 分类: `CTA`, `FEATURE`

## 水墙防岩浆 (waterWallLavaProtection)


&emsp;防止岩浆在上方一格或两格内有含水石头楼梯且下表面为完整面时生成圆石或黑曜石

&emsp;- 类型: `boolean`

&emsp;- 默认值: `false`

&emsp;- 分类: `CTA`, `FEATURE`