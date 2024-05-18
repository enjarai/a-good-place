package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LegoAnimationPlacingParticle extends PlacingBlockParticle{

    protected Direction facing;

    private Vec3 prevRot;
    private Vec3 rot;
    private float step = 0.00275f;

    private float height;
    private float prevHeight;

    public LegoAnimationPlacingParticle(ClientLevel world, BlockPos blockPos, Direction face, Player placer) {
        super(world, blockPos, face);

        facing = placer.getDirection();
        lifetime = 7;

        prevHeight = height = Mth.randomBetween(this.random,
                0.065f, 0.115f);
        float startingAngle =  Mth.randomBetween(this.random,
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

        prevHeight = height;
        prevRot = rot;

        rot = switch (facing) {
            case EAST -> rot.add(step, 0, step);
            case NORTH -> rot.add(step, 0, -step);
            case SOUTH -> rot.add(-step, 0, step);
            case WEST -> rot.add(-step, 0, -step);
            default -> new Vec3(0, 0, 0);
        };

        height -= step * 5f;
        height = Math.max(height, 0);

        //step *= 1.5678982f;
    }


    @Override
    public void applyAnimation(PoseStack poseStack, float partialTicks) {
        var tRot = switch (facing) {
            case EAST -> new Vec3(1, 0, -1);
            case NORTH -> new Vec3(-1, 0, -1);
            case SOUTH -> new Vec3(1, 0, 1);
            case WEST -> new Vec3(-1, 0, 1);
            default -> new Vec3(0, 0, 0);
        };

        float translationAmount = Mth.lerp(partialTicks, prevHeight, height);

        if (translationAmount <= 0)
            translationAmount = 0;

        var translate = switch (facing) {
            case EAST -> new Vec3(-translationAmount, translationAmount, translationAmount);
            case NORTH -> new Vec3(translationAmount, translationAmount, translationAmount);
            case SOUTH -> new Vec3(-translationAmount, translationAmount, -translationAmount);
            case WEST -> new Vec3(translationAmount, translationAmount, -translationAmount);
            default -> new Vec3(0, 0, 0);
        };
        translate =translate.multiply(2,0,2);
        Vec3 smoothRot = prevRot.lerp(rot, partialTicks);

        //anim
        poseStack.translate(tRot.x, tRot.y, tRot.z);

        //  poseStack.mulPose(Axis.YP.rotation((float) smoothRot.x));
        //   poseStack.mulPose(Axis.ZP.rotation((float) smoothRot.z));

        poseStack.translate(-tRot.x, -tRot.y, -tRot.z);

        poseStack.translate(translate.x, translate.y, translate.z);
    }
}
