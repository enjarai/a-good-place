package nl.enjarai.wonkyblock.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public class VanillaRendererImplementation implements RendererImplementation {

    @Override
    public void renderBlock(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack matrices, VertexConsumer vertexConsumer, boolean cull, RandomSource random, long seed) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
                world, model, state, pos, matrices, vertexConsumer,
                cull, random, seed, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void markBlockForRender(BlockPos pos) {
        var client = Minecraft.getInstance();
        BlockState state = client.level.getBlockState(pos);
        //this just calls set block dirty which calls set section dirty for neighbors
        client.level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
}
