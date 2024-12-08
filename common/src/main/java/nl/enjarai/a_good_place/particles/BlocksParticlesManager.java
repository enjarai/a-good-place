package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.AGoodPlace;
import nl.enjarai.a_good_place.pack.AnimationParameters;
import nl.enjarai.a_good_place.pack.AnimationsManager;
import org.joml.Matrix4fStack;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

// static class so we have 1 less method call for get instance as isHidden will be called a lot
public class BlocksParticlesManager {

    protected static final Map<BlockPos, PlacingBlockParticle> PARTICLES = new HashMap<>();
    //replace each time so its thread safe. Supposedly faster than a concurrent set
    private static final Set<BlockPos> HIDDEN_BLOCKS = new CopyOnWriteArraySet<>();


    public static void addParticle(BlockState state, BlockPos pos, ClientLevel level, Direction face, Player player, InteractionHand hand) {
        AnimationParameters param = AnimationsManager.getAnimation(state, pos, level);
        /*
        param = new AnimationParameters(null,
                0, null, 4,
                1, -0.7f,
                0.25f, 0.9f,
                0f, 0.1f, 0.1f, -0.08f, false,
                1, 0, .7f);*/

        if (param != null) {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

            if (camera.getPosition().distanceToSqr(pos.getCenter()) <= 1024.0) {
                var p = new ConfiguredPlacingParticle(level, pos, face, player, hand, param);

                var old = BlocksParticlesManager.PARTICLES.put(pos, p);
                if (old != null){
                    old.remove();
                }
                BlocksParticlesManager.hideBlock(pos);


                if (AGoodPlace.RENDER_AS_VANILLA_PARTICLES) {
                    Minecraft.getInstance().particleEngine.add(p);
                }
            }
        }
    }

    public static void hideBlock(BlockPos pos) {
        HIDDEN_BLOCKS.add(pos);
    }

    public static boolean isBlockHidden(BlockPos pos) {
        return HIDDEN_BLOCKS.contains(pos);
    }

    public static void unHideBlock(BlockPos pos) {
        boolean success = HIDDEN_BLOCKS.remove(pos);
        PARTICLES.remove(pos);
        if (success) {
            markBlockForRender(pos);
        }
    }

    private static void markBlockForRender(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockState state = level.getBlockState(pos);
        //this just calls set block dirty which calls set section dirty for neighbors
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }


    //tick manually just to be safe
    public static void tickParticles(ClientLevel level) {
        if (AGoodPlace.RENDER_AS_VANILLA_PARTICLES) return;

        var iterator = PARTICLES.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var p = entry.getValue();
            p.tick();
            if (!p.isAlive()) iterator.remove();
        }

    }

    public static void renderParticles(PoseStack poseStack, float tickDelta) {
        if (AGoodPlace.RENDER_AS_VANILLA_PARTICLES || PARTICLES.isEmpty()) return;

        poseStack.pushPose();

        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        // lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        Matrix4fStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushMatrix();
        poseStack2.mul(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        for (var p : PARTICLES.values()) {
            p.render(null, camera, tickDelta);
        }
        bufferSource.endBatch();


        poseStack2.popMatrix();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        // lightTexture.turnOffLightLayer();

        poseStack.popPose();
    }

    public static void modifyTilePosition(BlockPos pos, PoseStack pose, float partialTicks) {
        // we are on render thread here so we can just check if we are rendering particles
        var particle = PARTICLES.get(pos);
        if (particle != null) {
            particle.applyAnimation(pose, partialTicks);
        }
    }


    public static void clear() {
        PARTICLES.clear();
        HIDDEN_BLOCKS.clear();
    }
}
