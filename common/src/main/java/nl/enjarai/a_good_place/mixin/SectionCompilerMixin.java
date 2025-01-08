package nl.enjarai.a_good_place.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {

    @ModifyExpressionValue(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"))
    private boolean onCompile(boolean isSolid, @Local(ordinal = 2) BlockPos pos) {
        if (isSolid && BlocksParticlesManager.isBlockHidden(pos)) {
            return false;
        }
        // Insert your code here
        return isSolid;
    }
}
