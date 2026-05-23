package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// we use a non-registered particle because this is a client only mod and we need to render from event anyways
public abstract class PlacingBlockParticle extends Particle {

    protected final BlockPos pos;
    protected final BlockState blockState;
    private final MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
    protected int extraLifeTicks = 0;
    public boolean canRender;


    public PlacingBlockParticle(ClientLevel world, BlockPos blockPos, Direction face) {
        super(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());

        pos = BlockPos.containing(x, y, z);
        blockState = world.getBlockState(pos);

        hasPhysics = false;
        lifetime = 7;

        setSize(1, 1);
    }

    @Override
    public void tick() {
        if (this.removed) {
            return;
        }
        age++;

        if (level.getBlockState(pos) != this.blockState) {
            this.remove();
            BlocksParticlesManager.unHideBlock(pos);
            BlocksParticlesManager.PARTICLES.remove(pos, this);
            return;
        }

        if (this.finishedAnimation()) {
            // un-hide so the chunk starts re-rendering, but keep rendering the particle
            // at its final position (t=1) during extraLifeTicks to cover the re-render delay
            BlocksParticlesManager.unHideBlock(pos);
        }

        if (age >= lifetime + extraLifeTicks) {
            remove();
            BlocksParticlesManager.PARTICLES.remove(pos, this);
        }
    }

    public void submitBlock(PoseStack poseStack, Vec3 cameraPos, SubmitNodeCollector collector, float partialTicks) {
        if (!this.canRender) return;

        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());
        applyAnimation(poseStack, partialTicks);

        movingBlockRenderState.blockState = blockState;
        movingBlockRenderState.blockPos = pos;
        movingBlockRenderState.randomSeedPos = pos;
        movingBlockRenderState.biome = level.getBiome(pos);
        movingBlockRenderState.cardinalLighting = level.cardinalLighting();
        movingBlockRenderState.lightEngine = level.getLightEngine();

        collector.submitMovingBlock(poseStack, movingBlockRenderState);
        poseStack.popPose();
    }


    public final void applyAnimation(PoseStack poseStack, float partialTicks) {
        float t = Math.min(1, (age + partialTicks) / (lifetime + 1)); //from 0 to 1
        applyAnimation(poseStack, t, partialTicks);
    }

    protected abstract void applyAnimation(PoseStack poseStack, float animationTime, float partialTicks);

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    public boolean finishedAnimation() {
        return age >= lifetime;
    }

}
