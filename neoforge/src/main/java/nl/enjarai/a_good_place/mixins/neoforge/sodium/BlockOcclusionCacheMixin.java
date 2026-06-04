package nl.enjarai.a_good_place.mixins.neoforge.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo //won't get loaded if mod isn't there
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext", remap = false, priority = 600)
public abstract class BlockOcclusionCacheMixin {

    @Inject(
            method = "shouldDrawSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true,
            remap = false
    )
    private void aGoodPlace$overrideCulling(Direction facing, CallbackInfoReturnable<Boolean> cir,
                                            @Local BlockPos.MutableBlockPos neighborPos) {
        if (BlocksParticlesManager.isBlockHidden(neighborPos)) {
            cir.setReturnValue(true);
        }
    }
}
