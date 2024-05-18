package nl.enjarai.a_good_place.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.WonkyBlocksManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo //won't get loaded if mod isn't there
@Mixin(BlockOcclusionCache.class)
public abstract class BlockOcclusionCacheMixin {


    @Shadow @Final private BlockPos.MutableBlockPos cpos;

    @Inject(
            method = "shouldDrawSide",
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"),
            remap = false,
            cancellable = true
    )
    private void wonkyblock$overrideCulling(BlockState selfState, BlockGetter view, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (WonkyBlocksManager.isBlockHidden(cpos)){
            cir.setReturnValue(true);
        }
    }
}
