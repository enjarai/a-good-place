package nl.enjarai.wonkyblock.compat.sodium.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer")
public abstract class BlockRendererMixin {
    @Dynamic
    @Inject(
            method = "renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJ)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wonkyblock$hideBlock(BlockRenderView world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffer, boolean cull, long seed, CallbackInfoReturnable<Boolean> cir) {
        if (WonkyBlock.getInvisibleBlocks().contains(pos)) {
            cir.setReturnValue(false);
        }
    }
}
