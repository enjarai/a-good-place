package nl.enjarai.wonkyblock.compat.sodium.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask")
public abstract class ChunkRenderRebuildTaskMixin {
//    private BlockPos pos;

//    @Dynamic
//    @ModifyVariable(
//            method = "performBuild(Lme/jellysquid/mods/sodium/client/gl/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationSource;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildResult;",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;<init>()V",
//                    ordinal = 1
//            ),
//            index = 15
//    )
//    private BlockPos.Mutable wonkyblock$catchLocal(BlockPos.Mutable blockPos) {
//        pos = blockPos;
//        return blockPos;
//    }
//
//    @Dynamic
//    @ModifyExpressionValue(
//            method = "performBuild(Lme/jellysquid/mods/sodium/client/gl/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationSource;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildResult;",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"
//            )
//    )
//    private BlockRenderType wonkyblock$hideBlock(BlockRenderType renderType) {
//        if (WonkyBlock.isBlockInvisible(pos)) {
//            return BlockRenderType.INVISIBLE;
//        }
//        return renderType;
//    }

    @Dynamic
    @WrapWithCondition(
            method = "performBuild(Lme/jellysquid/mods/sodium/client/gl/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationSource;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z"
            )
    )
    private boolean wonkyblock$hideBlock(BlockRenderView world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed) {
        return !WonkyBlock.getInvisibleBlocks().contains(pos);
    }
}
