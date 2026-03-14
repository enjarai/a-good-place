package nl.enjarai.a_good_place.mixin;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBlockRenderer.class)
public abstract class BlockMixin {
    @Inject(
            method = "shouldRenderFace(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void wonkyblock$overrideCulling(BlockAndTintGetter level, BlockState state, boolean useAO, Direction face, BlockPos neighborPos, CallbackInfoReturnable<Boolean> cir) {
        if (BlocksParticlesManager.isBlockHidden(neighborPos)) {
            cir.setReturnValue(true);
        }
    }
}
