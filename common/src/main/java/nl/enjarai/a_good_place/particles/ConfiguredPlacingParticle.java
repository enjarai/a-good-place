package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.a_good_place.pack.AnimationParameters;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConfiguredPlacingParticle extends PlacingBlockParticle {

    private final AnimationParameters params;

    // these are all starting values. They all have to end up at 0 (or 1 for scale) since it needs to match the placed block
    private final float yAngle;
    private final Vec2 slideStart; // z and y values. This will be relative to yAngle
    private final Vec3 rotStart;


    public ConfiguredPlacingParticle(ClientLevel level, BlockPos blockPos, Direction face,
                                     Player placer, InteractionHand hand, AnimationParameters settings) {
        super(level, blockPos, face);

        settings.sound().ifPresent(sound -> level.playLocalSound(blockPos,
                sound.value(), SoundSource.PLAYERS, 1, 1, false));

        /*

        settings = new AnimationParameters(null, 0, null, 300,
                0.8f, 0.2f,
                0.5f, -0.9f,
                0, 0, 0, 0, false,
                1, 0,
                0);

        settings = new AnimationParameters(null,
                0, null, 400,
                1.1f, -0.7f,
                0.25f, 0.9f,
                0.08f, 0.2f, 0.2f, -0.08f, false,
                1, 0, 0.7f);

        */


        params = settings;
        lifetime = params.duration();
        extraLifeTicks = 2;


        // Slide animation
        // slide in animation. We would like to slide in the block so it looks like it comes from the player hand
        //actually we use the real look dir so we wont snap to directions. Possibly a config here
        Vector3f playerHorizLook = placer.getLookAngle().toVector3f().mul(-1, 0, -1).normalize();
        //translation relative to player look
        int rightHandMul = placer.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        if (hand != InteractionHand.MAIN_HAND) rightHandMul *= -1;
        Vector3f xVec = new Vector3f(playerHorizLook.z, 0, -playerHorizLook.x).mul(rightHandMul);

        Matrix3f changeOfBasis = new Matrix3f(xVec, new Vector3f(0, 1, 0), playerHorizLook);

        Vector3f tr = params.translation().toVector3f();
        float slidePow = tr.length();
        if (placer.getXRot() > 0) {
            tr.mul(1, 1, 1);
        } else {
            tr.mul(1, -1, 1);
        }
        Vector3f slideDir = tr.mul(changeOfBasis);

        slideDir = adjustDirectionBasedOnNeighbors(level, placer, slideDir);

        //config here
        Vec3 animationDirection = new Vec3(slideDir.normalize());
        //get relative components and angle
        yAngle = (float) Math.atan2(slideDir.x(), slideDir.z());
        var temp = animationDirection.yRot(-yAngle);

        slideStart = new Vec2((float) temp.z, (float) temp.y).scale(slidePow);

        //float startingAngle = addSomeRandom(params.rotationAmount());
        //rotation. Relative to move direction
        rotStart = params.rotation();
    }


    @Override
    public void applyAnimation(PoseStack poseStack, float time, float partialTicks) {

        poseStack.translate(0.5, 0.5, 0.5);
        Vec3 offset = blockState.getOffset(level, pos);

        poseStack.translate(offset.x, offset.y, offset.z);

        //All animations are relative to the move direciton
        poseStack.mulPose(Axis.YP.rotation(yAngle));


        //slide
        {
            float progress = fancyExponent(time, params.translationCurve());
            Vec2 translate = slideStart.scale(1 - progress);

            poseStack.translate(0, translate.y, translate.x);
        }

        // rotate
        {
            float progress = fancyExponent(time, params.rotationCurve());
            Vec3 rotation = rotStart.scale(1 - progress);

            //tralsate toward move direciton on block edge
            Vec3 rotationPivot = params.pivot();
            if (slideStart.y < 0) {
                rotationPivot = rotationPivot.multiply(1, -1, 1);
                rotation = rotation.multiply(-1, 1,-1);
            }

            poseStack.translate(rotationPivot.x, rotationPivot.y, rotationPivot.z);

            //no clue if these arein the right order
            poseStack.mulPose(Axis.YP.rotation((float) rotation.y));
            // another config. determines if they are rotated toward moving dir or opposite
            poseStack.mulPose(Axis.ZP.rotation((float) rotation.z));
            poseStack.mulPose(Axis.XP.rotation((float) rotation.x));

            poseStack.translate(-rotationPivot.x, -rotationPivot.y, -rotationPivot.z);
        }


        // scale
        {
            float progress = fancyExponent(time, params.scaleCurve());
            float scaleStart = params.scaleStart();
            float scale = scaleStart + (0.999f - scaleStart) * progress; //slightly below 1 so we dont have potential clipping when block appears
            poseStack.scale(scale, scale, scale);
        }


        // height scale
        {
            float progress = fancyExponent(time, params.heightCurve());
            float heightStart = params.heightStart();
            float height = heightStart + (1 - heightStart) * progress;
            poseStack.translate(0, -0.5, 0);
            poseStack.scale(1, height, 1);
            poseStack.translate(0, 0.5f, 0);

        }

        //reset so block renders straight
        poseStack.mulPose(Axis.YP.rotation(-yAngle));

        poseStack.translate(-offset.x, -offset.y, -offset.z);

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
     *
     * @param t     time
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

    private float addSomeRandom(float t) {
        return t;//+ Mth.randomBetween(this.random, -random, random);
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
