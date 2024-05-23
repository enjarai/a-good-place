package nl.enjarai.a_good_place.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviorMixin {

    @Inject(
            method = "isSolidRender",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wonkyblock$overrideCulling(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (BlocksParticlesManager.isBlockHidden(pos)) {
            cir.setReturnValue(false);
        }
    }
}
