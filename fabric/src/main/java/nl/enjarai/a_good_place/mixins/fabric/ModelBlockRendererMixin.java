package nl.enjarai.a_good_place.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    @WrapOperation(
            method = "tesselateWithoutAO",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean aGoodPlace$overrideCulling(BlockState arg, BlockState arg2, Direction arg3, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        var alwaysRenders = !BlocksParticlesManager.isBlockHidden(pos);
        if (alwaysRenders) return true;
        return original.call(arg, arg2, arg3);
    }

    @WrapOperation(
            method = "tesselateWithAO",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean aGoodPlace$overrideCullingAO(BlockState arg, BlockState arg2, Direction arg3, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        var alwaysRenders = !BlocksParticlesManager.isBlockHidden(pos);
        if (alwaysRenders) return true;
        return original.call(arg, arg2, arg3);
    }


}
