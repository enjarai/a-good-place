package nl.enjarai.a_good_place.mixins.neoforge.sodium;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public abstract class BlockRendererMixin {

    @Inject(
            method = "renderModel",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void wonkyblock$hideBlock(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (BlocksParticlesManager.isBlockHidden(pos)) {
            ci.cancel();
        }
    }
}
