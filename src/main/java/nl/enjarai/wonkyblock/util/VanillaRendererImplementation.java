package nl.enjarai.wonkyblock.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public class VanillaRendererImplementation implements RendererImplementation {
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void renderBlock(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos,
                            MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull,
                            Random random, long seed) {
        client.getBlockRenderManager().getModelRenderer().render(
                world, model, state, pos, matrices, vertexConsumer, cull, random, seed, OverlayTexture.DEFAULT_UV);
    }

    @Override
    public void markBlockForRender(BlockPos pos) {
        var bp1 = pos.add(1, 1, 1);
        var bp2 = pos.add(-1, -1, -1);

        client.worldRenderer.scheduleBlockRenders(
                bp1.getX(), bp1.getY(), bp1.getZ(),
                bp2.getX(), bp2.getY(), bp2.getZ());
    }
}
