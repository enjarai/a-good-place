package nl.enjarai.a_good_place.mixins.fabric;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {

    @WrapOperation(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState wonkyblock$hideBlock(RenderChunkRegion instance, BlockPos arg, Operation<BlockState> original) {
        if (BlocksParticlesManager.isBlockHidden(arg)) {
            return Blocks.AIR.defaultBlockState();
        }
        return original.call(instance, arg);
    }

    @ModifyExpressionValue(method = "compile", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"))
    private boolean aGoodPlace$onCompile(boolean isSolid, @Local(ordinal = 2) BlockPos pos) {
        if (isSolid && BlocksParticlesManager.isBlockHidden(pos)) {
            return false;
        }
        // Insert your code here
        return isSolid;
    }
}
