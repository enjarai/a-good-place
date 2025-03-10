package nl.enjarai.a_good_place.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo //won't get loaded if mod isn't there
@Mixin(value = BlockOcclusionCache.class, priority = 600 ) //for more culling
public abstract class BlockOcclusionCacheMixin {

    //same exact place as more culling with lower priority so its not ambiguous
    @Inject(
            method = "shouldDrawSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(" +
                            "Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void moreculling$useMoreCulling(BlockState selfState, BlockGetter view, BlockPos pos,
                                            Direction facing, CallbackInfoReturnable<Boolean> cir,
                                            @Local BlockPos.MutableBlockPos cpos) {
        if (BlocksParticlesManager.isBlockHidden(cpos)){
            cir.setReturnValue(true);
        }
    }
}
