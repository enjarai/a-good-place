# A Good Place

Minecraft mod that adds block placement animations.

The mod contains a default animation that will be applied to all suitable blocks (excluding ones like double flowers and bets).
It also works on tile entities!


The mod is fully customizable via Resource Packs.


## Customization
To start just go in your resource packs folder, you'll find a pre made sample pack there.

You'll find a `placement_animations` folder. Here you'll be able to place one or more json files, each of which will target a set of blocks and give them a specific animation. You can name them however you please.

Here below is the syntax for the json file that defines the animations for the placement of the blocks.
All fields except for `targets` are optional.

| name                | default value | explanation                                                                                                                                                             |
|---------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `targets`           | `"*"`         | Affected blocks. Accepts a holder set (tag or list of ids) or `"*"` to affect all blocks`                                                                               |
| `priority`          | `0`           | Priority of this file. Higher ones will be applied over ones with lower priority.                                                                                       |
| `predicate`         | `always_true` | Rule test predicate. Used to filter blocks better.                                                                                                                      |
| `duration`          | `4`           | Duration of the animation in ticks.                                                                                                                                     |
| `scale`             | `1`           | The initial scale of the block when it is placed.                                                                                                                       |
| `scale_curve`       | `0.5`         | Controls the animation of the scale parameter.                                                                                                                          |
| `translation`       | `0`           | The initial translation of the block when it is placed. Translation direction depends on player direction                                                               |
| `translation_curve` | `0.5`         | Controls the animation of the translation parameter.                                                                                                                    |
| `translation_angle` | `0.78`        | Controls the direction of the translation animation by adding an angle to the player look direction.<br/>Default is 45 degrees to look like its coming from player hand |
| `rotation_amount`   | `0`           | The initial rotation amount of the block when it is placed. Rotation direction depends on player direction                                                              |
| `rotation_angle`    | `0`           | The angle of the rotation vector relative to the player direction.                                                                                                      |
| `rotation_curve`    | `0.5`         | Controls the animation of the rotation parameter.                                                                                                                       |
| `rotation_y`        | `0`           | Controls Y component of the rotation. Normally not affected by above ones.                                                                                              |
| `rotate_on_center`  | `false`       | If pivot of the rotation should be center of the block instead of upper left corner.                                                                                    |
| `scale`             | `1`           | The scale of the block when it is placed.                                                                                                                               |

The animation always ends up at the block normal dimension.
For example `scale` is the initial scale of the block while its final scale will obviously be 1.

Regarding `scale`. Internally it is a vector of 3 components.
it will be calculated as follows:
Vec3 rot = player_direction.rotateY(`rotation_angle`) * `rotation_amount`;
rot.y = `rotation_y`;

This rotation will then be either applied with pivot the center of the block or its upper left corner (relative to move direction), depending on config

All angles are in degrees.

Below follows an example of a pop in animation

```json
{
  "targets": "*",
  "scale": 0.2,
  "scale_curve": 0.92
}
```

### About "curve"

All parameter that use the keyword `_curve` control the base of an exponential function used to calculate the curve othe
animation.

- A value of 0 will result in a linear "curve", smoothly interpolating from start to finish.
- A value approaching 1 will make the exponent function have upward concavity, making it steeper and steeper toward the
  end. This results in a slow change at the beginning and a fast one as time goes on. 1 is NOT a valid value.
- A value approaching -1 will make the exponent function have downward concavity, making it steeper and steeper toward
  the beginning. This results in a fast change at the beginning and a slow one as time goes on. -1 is NOT a valid value.
