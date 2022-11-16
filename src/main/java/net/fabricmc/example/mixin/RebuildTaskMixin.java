package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public abstract class RebuildTaskMixin {
    private BlockPos pos;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void interceptLocals(
            float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage blockBufferBuilderStorage,
            CallbackInfoReturnable<ChunkBuilder.BuiltChunk.RebuildTask.RenderData> cir,
            ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData, int i, BlockPos blockPos, BlockPos blockPos2,
            ChunkOcclusionDataBuilder chunkOcclusionDataBuilder, ChunkRendererRegion chunkRendererRegion,
            MatrixStack matrixStack, Set set, Random random, BlockRenderManager blockRenderManager,
            Iterator var15, BlockPos blockPos3
    ) {
        pos = blockPos3;
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"
            )
    )
    private BlockRenderType redirectGetRenderType(BlockState blockState) {
        var renderType = blockState.getRenderType();
        if (ExampleMod.isBlockInvisible(pos)) {
            return BlockRenderType.INVISIBLE;
        }

        pos = null;
        return renderType;
    }
}
