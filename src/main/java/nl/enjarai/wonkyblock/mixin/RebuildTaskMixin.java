package nl.enjarai.wonkyblock.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public abstract class RebuildTaskMixin {
    @WrapWithCondition(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"
            )
    )
    private boolean wonkyblock$hideBlock(BlockRenderManager blockRenderManager, BlockState blockState,
                                         BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack,
                                         VertexConsumer vertexConsumer, boolean bl, Random random) {
        return !WonkyBlock.getInvisibleBlocks().contains(blockPos);
    }
}
