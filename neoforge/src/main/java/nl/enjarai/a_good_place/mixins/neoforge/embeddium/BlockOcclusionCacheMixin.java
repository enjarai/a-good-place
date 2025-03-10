package nl.enjarai.a_good_place.mixins.neoforge.embeddium;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockOcclusionCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo //won't get loaded if mod isn't there
@Mixin(value = BlockOcclusionCache.class, priority = 600)
public abstract class BlockOcclusionCacheMixin {

    @Inject(
            method = "shouldDrawSide",
            at = @At(value = "INVOKE",
                    remap = true,
                    shift = At.Shift.AFTER,
                    args = {"log=true"},
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"),
            remap = false,
            cancellable = true
    )
    private void wonkyblock$overrideCulling(BlockState selfState, BlockGetter view, BlockPos pos, Direction facing,
                                            CallbackInfoReturnable<Boolean> cir,
                                            @Local(ordinal = 0) BlockPos.MutableBlockPos cpos) {
        if (BlocksParticlesManager.isBlockHidden(cpos)){
            cir.setReturnValue(true);
        }
    }
}
