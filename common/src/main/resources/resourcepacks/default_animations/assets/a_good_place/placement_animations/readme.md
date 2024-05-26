### Json format syntax:

Here below is the syntax for the json file that defines the animations for the placement of the blocks.
All fields are optional.

In short animations are composed of 4 separate animations: scale, translation, rotation and height scale.

| name                | default value           | explanation                                                                                                                                                                                      |
|---------------------|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `predicates`        | `[]"`                   | A list of BlockStatePredicates used to filter the affected blocks.`                                                                                                                              |
| `priority`          | `0`                     | Priority of this file. Higher ones will be applied over ones with lower priority.                                                                                                                |
| `duration`          | `4`                     | Duration of the animation in ticks.                                                                                                                                                              |
| `scale`             | `1`                     | The initial scale of the block when it is placed.                                                                                                                                                |
| `scale_curve`       | `0.5`                   | Controls the animation of the scale parameter.                                                                                                                                                   |
| `translation`       | `0`                     | A vector of 3 components `x`, `y` and `z` representing the intensity of the translation animation relative to the player direction. If you just set z to 1 it will be exactly toward the player. |
| `translation_curve` | `0.5`                   | Controls the animation of the translation parameter.                                                                                                                                             |
| `rotation`          | `{"x":0, "y":0, "z":0}` | A vector of 3 components `x`, `y` and `z`, each representing the angle in DEGREES relative to the player look direction at which the animation will start.                                       |
| `rotation_curve`    | `0.5`                   | Controls the animation of the rotation parameter.                                                                                                                                                |
| `rotation_pivot`    | `{"x":0, "y":0, "z":0}` | A vector of 3 components `x`, `y` and `z`, each representing the pivot point of the rotation operation, relative to the block center.                                                            |
| `height`            | `1`                     | The Y scale of the block when it is placed.                                                                                                                                                      |
| `height_curve`      | `0.5`                   | Controls the animation of the height parameter.                                                                                                                                                  |
| `sound`             | `<empty>`               | Additional sound to play when the animation starts (ID of sound event).                                                                                                                          |

The animation always ends up at the block normal dimension.
For example `scale` is the initial scale of the block while its final scale will obviously be 1.

Regarding `scale`. Internally it is a vector of 3 components.
it will be calculated as follows:
Vec3 rot = player_direction.rotateY(`rotation_angle`) * `rotation_amount`;
rot.y = `rotation_y`;

This rotation will then be either applied with pivot the center of the block or its upper left corner (relative to move
direction), depending on config

All angles are in degrees.

Below follows an example of a pop in animation

```json
{
  "predicates": [
    {
      "predicate_type": "tag_match",
      "tag": "minecraft:stone"
    }
  ],
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

### About "predicates"

As mentioned above predicate field is a list of block state predicate objects used to filter the blocks to which you
want to apply
your animation.

This is a concept very similar to RuleTests and BlockPredicates, an existing vanilla concept used in worldgen.
Here's a list of the ones that exist:

- `matching_blocks`: Contains a tag or a block list. True if the block matches the list.
- `matching_state`: Contains a blockstate definition. Just matches that specific blockstate.
- `not`: Contains a predicate. True if the predicate is false.
- `any_of`: Contains a list of predicates. True if any of the predicates are true.
- `has_collision`: True if the block has collision.
- `is_double_block`: True if the block is a double block (chest, flowers, beds...). Not exhaustive. You might want to
  add some extra block matches predicates to exclude stuff from tags.

Here's an example using most of these predicates:

```json
{
  "predicates": [
    {
      "predicate_type": "any_of",
      "predicates": [
        {
          "predicate_type": "block_match",
          "blocks": "#minecraft:flowers"
        },
        {
          "predicate_type": "block_match",
          "blocks": [
            "minecraft:sweet_berry_bush"
          ]
        }
      ]
    },
    {
      "predicate_type": "not",
      "predicate": {
        "predicate_type": "is_double_block"
      }
    },
    {
      "predicate_type": "not",
      "predicate": {
        "predicate_type": "has_collision"
      }
    }
  ]
}
```

In this example we are selecting all flowers + sweet berry bush and excluding all blocks that are not double flowers and
have collision