# Rules

**Tip: Use `Ctrl+F` to quickly find the rule you need**

---

## Explosion Ray Initial Strength (explosionRayInit)

&emsp;Fixed multiplier for explosion ray initial strength. Range: 0 to 16 (vanilla default is a random value between 0.7 and 1.3). Set to any value outside this range to use vanilla behavior

&emsp;- Type: `double`

&emsp;- Default: `-1.0`

&emsp;- Category: `CTA`, `FEATURE`


## Disable Amethyst Bud Growth in Water (`disableAmethystWaterGrowth`)

Prevents amethyst buds from growing into full water blocks.

- Type: `boolean`
- Default value: `false`
- Categories: `CTA`, `FEATURE`

---

## Prevent Extreme Initial TNT Momentum (`preventExtremeTntMomentum`)

Prevents TNT from generating with initial momentum values that can cause it to become stuck inside block collision boxes.

- Type: `boolean`
- Default value: `false`
- Categories: `CTA`, `FEATURE`

---

## Set TNT Initial X Velocity (`tntInitialXVelocity`)

Sets the initial X-axis velocity of TNT.  
The value must be between `-0.02` and `0.02`. Set it to `-1` to use the vanilla default behavior.

- Type: `double`
- Default value: `-1.0`
- Categories: `CTA`, `FEATURE`

---

## Water Wall Lava Protection (`waterWallLavaProtection`)

Prevents cobblestone or obsidian generation when lava comes into contact with the bottom face of a waterlogged stone stair above it.

- Type: `boolean`
- Default value: `false`
- Categories: `CTA`, `FEATURE`