package nl.enjarai.a_good_place.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviorMixin {

    /*
    @ModifyReturnValue(
            method = "isSolidRender",
            at = @At("RETURN")
    )
    private boolean wonkyblock$overrideCulling(boolean original, @Local(argsOnly = true) BlockPos pos) {
        if (BlocksParticlesManager.isBlockHidden(pos)) {
            return false;
        }
        return original;
    }*/
}
