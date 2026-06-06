package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.AGoodPlace;

// we use a non-registered particle because this is a client only mod and we need to render from event anyways
public abstract class PlacingBlockParticle extends Particle {

    protected final BlockPos pos;
    protected final BlockState blockState;
    private final BlockStateModel model;
    private final long seed;
    private final BlockRenderDispatcher renderer;
    protected int extraLifeTicks = 0;
    public boolean canRender;


    public PlacingBlockParticle(ClientLevel world, BlockPos blockPos, Direction face) {
        super(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());

        pos = BlockPos.containing(x, y, z);
        blockState = world.getBlockState(pos);
        renderer = Minecraft.getInstance().getBlockRenderer();
        model = renderer.getBlockModel(blockState);
        seed = blockState.getSeed(pos);

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

    public void renderBlock(PoseStack poseStack, Camera camera, float partialTicks) {
        if (!this.canRender) return;

        var cameraPos = camera.position();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        poseStack.pushPose();
        poseStack.translate(px, py, pz);

        applyAnimation(poseStack, partialTicks);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        if (AGoodPlace.USE_SIMPLE_RENDERER) {
            renderer.renderSingleBlock(blockState, poseStack, bufferSource, getPackedLight(), OverlayTexture.NO_OVERLAY);
        } else {
            renderer.getModelRenderer().tesselateBlock(
                    level,
                    model.collectParts(RandomSource.create(seed)),
                    blockState, pos, poseStack,
                    bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)),
                    false, OverlayTexture.NO_OVERLAY);
        }
        bufferSource.endBatch();

        poseStack.popPose();
    }

    // Fallback lighting: block at pos is hidden but still present in the world, so light at pos itself is 0 (solid).
    // Take the per-channel max sky/block light from pos + 6 neighbors so the placed block roughly matches its surroundings.
    private int getPackedLight() {
        int self = LevelRenderer.getLightColor(level, pos);
        int maxBlock = LightTexture.block(self);
        int maxSky = LightTexture.sky(self);
        for (Direction dir : Direction.values()) {
            int neighbor = LevelRenderer.getLightColor(level, pos.relative(dir));
            maxBlock = Math.max(maxBlock, LightTexture.block(neighbor));
            maxSky = Math.max(maxSky, LightTexture.sky(neighbor));
        }
        return LightTexture.pack(maxBlock, maxSky);
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
