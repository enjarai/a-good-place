package nl.enjarai.wonkyblock.compat.sodium;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import nl.enjarai.wonkyblock.util.RendererImplementation;

public class SodiumRendererImplementation implements RendererImplementation {
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void renderBlock(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, long seed) {

    }

    @Override
    public void markBlockForRender(BlockPos pos) {

    }
}
