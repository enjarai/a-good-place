package nl.enjarai.a_good_place.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.pack.AnimationManager;
import nl.enjarai.a_good_place.pack.AnimationParameters;

import java.util.*;

// static class so we have 1 less method call for get instance as isHidden will be called a lot
public class WonkyBlocksManager {

    private static final Map<BlockPos, PlacingBlockParticle> PARTICLES = new HashMap<>();
    //replace each time so its thread safe. Supposedly faster than a concurrent set
    private static Set<BlockPos> hiddenBlocks = Set.of();


    public static void addParticle(BlockState state, BlockPos pos, Level level, Direction face, Player player) {
        AnimationParameters param = AnimationManager.getAnimation(state, pos, level.random);
        if(param != null) {
            PARTICLES.put(pos, new OverEngineeredPlacingParticle((ClientLevel) level, pos, face, player, param));
            hideBlock(pos);
        }
    }

    public static void hideBlock(BlockPos pos) {
        Set<BlockPos> list = new HashSet<>(hiddenBlocks);
        var success = list.add(pos);
        if (success) {
            hiddenBlocks = Set.of(list.toArray(new BlockPos[0]));
        }
    }

    public static boolean isBlockHidden(BlockPos pos) {
        return hiddenBlocks.contains(pos);
    }

    public static void unHideBlock(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>(hiddenBlocks);
        var success = list.remove(pos);
        if (success) {
            hiddenBlocks = Set.of(list.toArray(new BlockPos[0]));
            markBlockForRender(pos);
        }
    }

    private static void markBlockForRender(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockState state = level.getBlockState(pos);
        //this just calls set block dirty which calls set section dirty for neighbors
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }


    public static void tickParticles(ClientLevel level) {

        var iterator = PARTICLES.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var p = entry.getValue();
            p.tick();
            BlockPos pos = entry.getKey();
            if (p.finishedAnimation()) {
                unHideBlock(pos);
            }
            if (!p.isAlive() || level.getBlockState(pos) != p.blockState) {
                iterator.remove();
                unHideBlock(pos); //just incase
            }
        }

    }

    public static void renderParticles(PoseStack poseStack, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        // lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        PoseStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        for (var p : PARTICLES.values()) {
            p.render(null, camera, tickDelta);
        }
        bufferSource.endBatch();


        poseStack2.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        // lightTexture.turnOffLightLayer();
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
        hiddenBlocks = Set.of();
    }
}
