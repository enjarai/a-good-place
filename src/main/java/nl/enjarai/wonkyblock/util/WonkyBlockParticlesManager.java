package nl.enjarai.wonkyblock.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.wonkyblock.particle.PlacingBlockParticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// static class so we have 1 less method call for get instance as isHidden will be called a lot
public class WonkyBlockParticlesManager {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final List<PlacingBlockParticle> PARTICLES = new ArrayList<>();
    //replace each time so its thread safe. Supposedly faster than a concurrent set
    private static Set<BlockPos> hiddenBlocks = Set.of();


    public static void addParticle(BlockPos pos) {
        PARTICLES.add(new PlacingBlockParticle(MC.level, pos));
        hideBlock(pos);
    }

    public static void hideBlock(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>(hiddenBlocks);
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
        BlockState state = MC.level.getBlockState(pos);
        //this just calls set block dirty which calls set section dirty for neighbors
        MC.level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }

    public static void renderBlock(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack matrices, VertexConsumer vertexConsumer, boolean cull, RandomSource random, long seed) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
                world, model, state, pos, matrices, vertexConsumer,
                cull, random, seed, OverlayTexture.NO_OVERLAY);
    }



}
