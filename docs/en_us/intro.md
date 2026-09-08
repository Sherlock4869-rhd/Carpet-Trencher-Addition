# Introduction

## Rules

**Tip: Use `Ctrl+F` to quickly find the rule you need**

### Explosion Ray Initial Strength (explosionRayInit)

&emsp;Fixed multiplier for explosion ray initial strength. Range: 0 to 16 (vanilla default is a random value between 0.7 and 1.3). Set to any value outside this range to use vanilla behavior

&emsp;- Type: `double`

&emsp;- Default: `-1.0`

&emsp;- Category: `CTA`, `FEATURE`


### Disable Amethyst Bud Growth in Water (`disableAmethystWaterGrowth`)

Prevents amethyst buds from growing into full water blocks.

&emsp;- Type: `boolean`
&emsp;- Default value: `false`
&emsp;- Categories: `CTA`, `FEATURE`

### Prevent Extreme Initial TNT Momentum (`preventExtremeTntMomentum`)

Prevents TNT from generating with initial momentum values that can cause it to become stuck inside block collision boxes.

&emsp;- Type: `boolean`
&emsp;- Default value: `false`
&emsp;- Categories: `CTA`, `FEATURE`

### Set TNT Initial X Velocity (`tntInitialXVelocity`)

Sets the initial X-axis velocity of TNT.  
The value must be between `-0.02` and `0.02`. Set it to `-1` to use the vanilla default behavior.

&emsp;- Type: `double`
&emsp;- Default value: `-1.0`
&emsp;- Categories: `CTA`, `FEATURE`

### Water Wall Lava Protection (`waterWallLavaProtection`)

Prevents lava from generating cobblestone/obsidian when a waterlogged stair (bottom face exposed) is present 1 or 2 blocks above.

- Type: `boolean`
- Default value: `false`
- Categories: `CTA`, `FEATURE`

## Information Display

Use `/cta track <entity>` to track an entity. Information such as the entity's position and velocity will be displayed on the left side of the screen. The command uses the same entity selectors as vanilla Minecraft commands. For example, to track all TNT entities, use:`/cta track @e[type=minecraft:tnt]`

Use `/cta track list` to view the list of all tracked entities.

Use `/cta track clear` to clear the tracking list.

Use `/cta track remove <entity index>` to remove a specific entity from the tracking list. The entity index corresponds to its position in the list and is displayed in the HUD.