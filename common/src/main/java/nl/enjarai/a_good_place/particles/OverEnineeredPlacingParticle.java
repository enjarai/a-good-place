package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.a_good_place.pack.AnimationParameters;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverEnineeredPlacingParticle extends PlacingBlockParticle {

    private final AnimationParameters settings;

    private final float slideStartX;
    private final float slideStartY;
    private final float slideStartZ;

    protected Direction facing;
    private Vec3 prevRot;
    private Vec3 rot;
    private float step = 0.00275f;

    public OverEnineeredPlacingParticle(ClientLevel world, BlockPos blockPos, Direction face,
                                        Player placer, AnimationParameters params) {
        super(world, blockPos, face);

        settings = params;
        lifetime = 3;//params.duration();
        extraLifeTicks = 2;

        Direction playerFacing = placer.getDirection();

        // slide in animation. We would like to slide in the block so it looks like it comes from the player hand
        Vector3f slideDir = playerFacing.getOpposite().step().rotateY(Mth.HALF_PI / 2);

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

        for(var d : affectedDir){
            if(!emptyDirections.contains(d)){
                //remove component in this dir
                slideDir.sub(d.step().mul(slideDir));
                break;
            }
        }

        if(slideDir.length() == 0 && !emptyDirections.isEmpty()){
            //get nearest direction of the one that are empty
            var nearest = List.of(Direction.orderedByNearest(placer));
            emptyDirections.sort(Comparator.comparingInt(nearest::indexOf));
            slideDir = emptyDirections.get(0).step();
        }

        float slidePow = Mth.randomBetween(this.random, 0.065f, 0.115f);
        slideDir.normalize().mul(slidePow);

        slideStartX = slideDir.x();
        slideStartY = slideDir.y();
        slideStartZ = slideDir.z();


        facing = placer.getDirection();

        float startingAngle = Mth.randomBetween(this.random,
                0.03125f, 0.0635f);

        prevRot = new Vec3(0, 0, 0);

        rot = switch (facing) {
            case EAST -> new Vec3(-startingAngle, 0, -startingAngle);
            case NORTH -> new Vec3(-startingAngle, 0, startingAngle);
            case SOUTH -> new Vec3(startingAngle, 0, -startingAngle);
            case WEST -> new Vec3(startingAngle, 0, startingAngle);
            default -> new Vec3(0, 0, 0);
        };


    }

    @Override
    public void tick() {
        super.tick();

        prevRot = rot;

        rot = switch (facing) {
            case EAST -> rot.add(step, 0, step);
            case NORTH -> rot.add(step, 0, -step);
            case SOUTH -> rot.add(-step, 0, step);
            case WEST -> rot.add(-step, 0, -step);
            default -> new Vec3(0, 0, 0);
        };

        step *= 1.5678982f;
    }


    @Override
    public void applyAnimation(PoseStack poseStack, float time, float partialTicks) {
        var tRot = switch (facing) {
            case EAST -> new Vec3(1, 0, -1);
            case NORTH -> new Vec3(-1, 0, -1);
            case SOUTH -> new Vec3(1, 0, 1);
            case WEST -> new Vec3(-1, 0, 1);
            default -> new Vec3(0, 0, 0);
        };

        float translationAmount;// = Mth.lerp(partialTicks, prevHeight, height);


        // translationAmount = 1 - parabula(time, 2, 0.4f);

        translationAmount = 1 - exponent(time, 0.01f);

        if (translationAmount < 0) {
            translationAmount = 0;
        }

        Vec3 translate = new Vec3(
                slideStartX * translationAmount,
                slideStartY * translationAmount,
                slideStartZ * translationAmount);


        Vec3 smoothRot = prevRot.lerp(rot, partialTicks);

        //anim
        poseStack.translate(tRot.x, tRot.y, tRot.z);

        //  poseStack.mulPose(Axis.YP.rotation((float) smoothRot.x));
        //   poseStack.mulPose(Axis.ZP.rotation((float) smoothRot.z));

        poseStack.translate(-tRot.x, -tRot.y, -tRot.z);

        poseStack.translate(translate.x, translate.y, translate.z);
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
}
