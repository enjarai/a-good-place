package nl.enjarai.a_good_place.mixins.fabric;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderInfo.class)
public abstract class BlockRenderInfoMixin {
    @Shadow
    public BlockPos blockPos;

    @Inject(
            method = "shouldDrawSide",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void wonkyblock$overrideCulling(Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (BlocksParticlesManager.isBlockHidden(blockPos)) {
            cir.setReturnValue(false);
        }
    }


}
