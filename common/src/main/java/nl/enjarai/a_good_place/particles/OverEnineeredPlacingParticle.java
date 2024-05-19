package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.a_good_place.pack.AnimationParameters;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverEnineeredPlacingParticle extends PlacingBlockParticle {

    private final AnimationParameters settings;

    // these are all starting values. They all have to end up at 0 (or 1 for scale) since it needs to match the placed block
    private final Vec3 slideStart;
    private final Vec3 rotStart;
    private final float scaleStart;
    private final float heightStart;

    public OverEnineeredPlacingParticle(ClientLevel world, BlockPos blockPos, Direction face,
                                        Player placer, AnimationParameters params) {
        super(world, blockPos, face);

        settings = params;
        lifetime = 4;//params.duration();
        extraLifeTicks = 1;

        //config here
        scaleStart = 0.3f;


        // Slide animation
        // slide in animation. We would like to slide in the block so it looks like it comes from the player hand
        //actually we use the real look dir so we wont snap to directions. Possibly a config here
        Vector3f playerHorizLook = placer.getLookAngle().toVector3f().mul(-1, 0, -1).normalize();
        //another config here. rotate slide toward hand instead of directly toward player
        Vector3f slideDir = playerHorizLook.rotateY(Mth.HALF_PI / 2);
        // also adds a y component
        if (placer.getXRot() > 0) {
            //add back
            slideDir.add(0, 1, 0);
        } else slideDir.add(0, -1, 0);

        slideDir = adjustDirectionBasedOnNeighbors(world, placer, slideDir);

        //config here
        float slidePow = Mth.randomBetween(this.random, 0.1f, 0.14f);
        slideDir.normalize().mul(slidePow);

        slideStart = new Vec3(slideDir);
        heightStart = 0;

        // Rotation animation

        // config here
        float startingAngle = Mth.randomBetween(this.random,
                0.05f, 0.1f);

        //perpendicular vector on y plane
        rotStart = new Vec3(slideStart.z(), 0, -slideStart.x()).normalize()
                .scale(startingAngle);
    }


    @Override
    public void applyAnimation(PoseStack poseStack, float time, float partialTicks) {

        poseStack.translate(0.5, 0.5, 0.5);


        //tralsate toward move direciton on block edge
        Vec3 tRot = slideStart.multiply(1, 0, 1).normalize().scale(0.5f);
        //rotate from up part of the block
        tRot = tRot.add(0, slideStart.y < 0 ? 0.5 : -0.5, 0);


        float translationAmount;// = Mth.lerp(partialTicks, prevHeight, height);


        // translationAmount = 1 - parabula(time, 2, 0.4f);

        translationAmount = 1 - exponent(time, 0.01f);

        if (translationAmount < 0) {
            translationAmount = 0;
        }

        //slide
        Vec3 translate = slideStart.scale(translationAmount);

      //  poseStack.translate(translate.x, translate.y, translate.z);


        // rotate
        Vec3 rotation = rotStart.scale(translationAmount);


        poseStack.translate(tRot.x, tRot.y, tRot.z);

        // original anim also had some y ais rotation...
        //poseStack.mulPose(Axis.YP.rotation((float) rotation.x));
        // another config. determines if they are rotated toward moving dir or opposite
        boolean invert = false;
        if (invert) rotation = rotation.scale(-1);
       // poseStack.mulPose(Axis.ZP.rotation((float) -rotation.z));
       // poseStack.mulPose(Axis.XP.rotation((float) -rotation.x));

        poseStack.translate(-tRot.x, -tRot.y, -tRot.z);


        float a = exponent(time, -1.01f);
        float scale = scaleStart + (1 - scaleStart) * a;
        poseStack.scale(scale, scale, scale);

        poseStack.translate(0,-0.5, 0);
        poseStack.scale(1,scale, 1);
        poseStack.translate(0,0.5f, 0);


        poseStack.translate(-0.5, -0.5, -0.5);



    }


    //parabula from 0 to 1 with a start slope
    private float parabula(float t, float exp, float startSlope) {
        float a = 1 - startSlope;

        return a * (float) Math.pow(t, exp) + (1 - a) * t;
    }

    //just an exponent between 0 and 1
    private float exponent(float t, float base) {
        return (float) (base * Math.pow(1 / base + 1, t) - base);
    }

    // 0 to 1 makes it curve up, -1 to 0 curve down. 0 is a line

    /**
     * An exponent function.
     * @param t time
     * @param curve determines the "curve" of the exponent graph.
     *              0 will be a line
     *              from 0 to 1 will curve with increasing severity (edge cases with vertical line at 1, which is not a valid input)
     *              from 0 to -1 will curve downwards in the same manner
     *              This parameter essentially controls the base of the exponent
     *              0.55 happens to map to a base close to Euler's number
     */
    private float fancyExponent(float t, float curve) {
        if (curve == 0) return t;
        float base;
        if (curve > 0) {
            base = (float) -Math.log(curve);
        } else {
            base = (float) (Math.log(-curve) - 1);
        }
        return exponent(t, base);
    }


    public static List<Direction> getAffectedDirections(float x, float y, float z) {
        List<Direction> list = new ArrayList<>();
        if (z > 0) list.add(Direction.SOUTH);
        if (z < 0) list.add(Direction.NORTH);
        if (x > 0) list.add(Direction.EAST);
        if (x < 0) list.add(Direction.WEST);
        if (y > 0) list.add(Direction.UP);
        if (y < 0) list.add(Direction.DOWN);
        return list;
    }

    // given a slide direction removes all components from direction where neighbors are full blocks.
    // If none is available picks another directon from the available ones. Can return empty vector if this too fails
    @NotNull
    private Vector3f adjustDirectionBasedOnNeighbors(ClientLevel world, Player placer, Vector3f slideDir) {
        // check neighboring blocks to see if they are free
        List<Direction> affectedDir = getAffectedDirections(slideDir.x(), slideDir.y(), slideDir.z());
        List<Direction> emptyDirections = new ArrayList<>();
        for (var d : Direction.values()) {
            BlockPos neighbor = pos.relative(d);
            BlockState state = world.getBlockState(neighbor);
            var collision = state.getCollisionShape(world, neighbor);
            if (collision.isEmpty()) emptyDirections.add(d);
            else {
                if (d.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    if (collision.min(d.getAxis()) > 0.25f) emptyDirections.add(d);
                } else {
                    if (collision.max(d.getAxis()) < 0.75f) emptyDirections.add(d);
                }
            }
        }

        for (var d : affectedDir) {
            if (!emptyDirections.contains(d)) {
                //remove component in this dir. Yes ths crappy code does that
                slideDir.sub(d.step().mul(d.step()).mul(slideDir));
            }
        }

        if (slideDir.length() == 0 && !emptyDirections.isEmpty()) {
            //get nearest direction of the one that are empty
            var nearest = List.of(Direction.orderedByNearest(placer));
            emptyDirections.sort(Comparator.comparingInt(nearest::indexOf));
            slideDir = emptyDirections.get(0).step();
        }
        return slideDir;
    }

}
