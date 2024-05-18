package nl.enjarai.a_good_place.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.WonkyBlocksManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public abstract class RebuildTaskMixin {

    @ModifyExpressionValue(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"
            )
    )
    private RenderShape wonkyblock$hideBlock(RenderShape original, @Local(ordinal = 2) BlockPos blockPos) {
        if (original != RenderShape.INVISIBLE && WonkyBlocksManager.isBlockHidden(blockPos)) {
            return RenderShape.INVISIBLE;
        }
        return original;
    }
}
