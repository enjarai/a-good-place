package nl.enjarai.a_good_place.mixins.fabric;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {

    @ModifyExpressionValue(
            method = "compile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"
            )
    )
    private RenderShape aGoodPlace$hideBlock(RenderShape original, @Local(ordinal = 2) BlockPos pos) {
        if (original != RenderShape.INVISIBLE && BlocksParticlesManager.isBlockHidden(pos)) {
            return RenderShape.INVISIBLE;
        }
        return original;
    }

    @ModifyExpressionValue(method = "compile", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"))
    private boolean aGoodPlace$onCompile(boolean isSolid, @Local(ordinal = 2) BlockPos pos) {
        if (isSolid && BlocksParticlesManager.isBlockHidden(pos)) {
            return false;
        }
        // Insert your code here
        return isSolid;
    }
}
