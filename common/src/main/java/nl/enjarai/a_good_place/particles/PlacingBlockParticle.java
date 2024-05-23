package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.AGoodPlace;

// this ideally could be used for some sort of mod api so mods can create their own particles
// we use a non registered particle because this is a client only mod and we need to render from event anyways
public abstract class PlacingBlockParticle extends Particle {

    protected final BlockPos pos;
    protected final BlockState blockState;

    //for block renderer
    private final BakedModel model;
    private final long seed;
    private final BlockRenderDispatcher renderer;
    protected int extraLifeTicks = 0;


    public PlacingBlockParticle(ClientLevel world, BlockPos blockPos, Direction face) {
        super(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());

        var client = Minecraft.getInstance();

        pos = BlockPos.containing(x, y, z);
        blockState = world.getBlockState(pos);
        model = client.getBlockRenderer().getBlockModel(blockState);
        seed = blockState.getSeed(pos);
        renderer = client.getBlockRenderer();


        hasPhysics = false;
        lifetime = 7;
    }

    @Override
    public void tick() {
        age++;
        if (age >= lifetime + extraLifeTicks) {
            remove();
        }

        if (this.finishedAnimation()) {
            BlocksParticlesManager.unHideBlock(pos);
        }
        if (level.getBlockState(pos) != this.blockState) {
            this.remove();
            BlocksParticlesManager.unHideBlock(pos); //just incase
        }
    }


    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        PoseStack poseStack = new PoseStack();

        var cameraPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        poseStack.translate(px, py, pz);

        applyAnimation(poseStack, partialTicks);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        AGoodPlace.renderBlock(model, seed, poseStack, bufferSource, blockState, level, pos, renderer);

        if (AGoodPlace.RENDER_AS_VANILLA_PARTICLES) Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
    }


    public final void applyAnimation(PoseStack poseStack, float partialTicks) {
        float t = Math.min(1, (age + partialTicks) / (lifetime + 1)); //from 0 to 1
        applyAnimation(poseStack, t, partialTicks);
    }

    protected abstract void applyAnimation(PoseStack poseStack, float animationTime, float partialTicks);

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public boolean finishedAnimation() {
        return age >= lifetime;
    }

}
