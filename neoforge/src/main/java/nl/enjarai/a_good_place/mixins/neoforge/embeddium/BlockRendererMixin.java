package nl.enjarai.a_good_place.mixins.neoforge.embeddium;

import net.minecraft.core.BlockPos;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public abstract class BlockRendererMixin {

    @Inject(
            method = "renderModel",
            at = @At("HEAD"),
            remap = false,
            cancellable = true,
            require = 0
    )
    private void wonkyblock$hideBlock(Object ctx, Object buffers, CallbackInfo ci) {
        try {
            BlockPos pos = (BlockPos) ctx.getClass().getMethod("pos").invoke(ctx);
            if (BlocksParticlesManager.isBlockHidden(pos)) {
                ci.cancel();
            }
        } catch (Exception ignored) {}
    }
}
