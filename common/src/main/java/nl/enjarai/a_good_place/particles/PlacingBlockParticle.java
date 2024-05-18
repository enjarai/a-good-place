package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.a_good_place.AGoodPlace;

// we use a non registered particle because this is a client only mod and we need to render from event anyways
public class PlacingBlockParticle extends Particle {

    private final BlockPos pos;
    private final BlockState blockState;
    private Direction facing;

    //for block renderer
    private final BakedModel model;
    private final long seed;
    private final BlockRenderDispatcher renderer;

    private Vec3 prevRot;
    private Vec3 rot;

    private float step = 0.00275f;

    private float height;
    private float prevHeight;

    private boolean destinationReached;

    Minecraft client;

    public PlacingBlockParticle(ClientLevel world, BlockPos blockPos) {
        super(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());

        client = Minecraft.getInstance();

        pos = BlockPos.containing(x, y, z);
        blockState = world.getBlockState(pos);
        model = client.getBlockRenderer().getBlockModel(blockState);
        seed = blockState.getSeed(pos);
        renderer = client.getBlockRenderer();

        facing = client.player.getDirection();

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

        hasPhysics = false;
        lifetime = 7;
    }

    @Override
    public void tick() {
        if (destinationReached) {
            remove();
            return;
        }
        if (age++ >= lifetime) {
            setRemovedNextTick();
        }

        if (removed || client.isPaused())
            return;

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

        step *= 1.5678982f;
    }

    private void setRemovedNextTick() {
        destinationReached = true;
    }


    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {

        PoseStack poseStack = new PoseStack();


        var cameraPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        poseStack.translate(px, py+1, pz);


        applyAnimation(poseStack, partialTicks);

        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

        AGoodPlace. renderBlock(model,seed, poseStack, bufferSource, blockState, level, pos, renderer);


    }


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

        Vec3 smoothRot = prevRot.lerp(rot, partialTicks);

        //anim
        poseStack.translate(tRot.x, tRot.y, tRot.z);

        //  poseStack.mulPose(Axis.YP.rotation((float) smoothRot.x));
        //   poseStack.mulPose(Axis.ZP.rotation((float) smoothRot.z));

        poseStack.translate(-tRot.x, -tRot.y, -tRot.z);

        poseStack.translate(translate.x, translate.y, translate.z);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    public boolean reachedDestination() {
        return destinationReached;
    }

}
