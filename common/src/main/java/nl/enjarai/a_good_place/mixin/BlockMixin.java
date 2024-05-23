package nl.enjarai.a_good_place.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(
            method = "shouldRenderFace",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void wonkyblock$overrideCulling(BlockState state, BlockGetter level, BlockPos otherPos, Direction face, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (BlocksParticlesManager.isBlockHidden(pos)) {
            cir.setReturnValue(true);
        }
    }


}
