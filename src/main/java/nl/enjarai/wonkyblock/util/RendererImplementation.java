package nl.enjarai.wonkyblock.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface RendererImplementation {
    void renderBlock(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack matrices,
                     VertexConsumer vertexConsumer, boolean cull, RandomSource random, long seed);

    void markBlockForRender(BlockPos pos);
}
