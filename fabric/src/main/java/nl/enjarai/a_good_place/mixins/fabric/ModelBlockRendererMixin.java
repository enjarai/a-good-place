package nl.enjarai.a_good_place.mixins.fabric;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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

    @WrapWithCondition(
            method = "tesselateWithoutAO",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean aGoodPlace$overrideCulling(BlockState blockState, BlockState blockState2, Direction direction, @Local(argsOnly = true) BlockPos pos) {
        return !BlocksParticlesManager.isBlockHidden(pos);
    }

    @WrapWithCondition(
            method = "tesselateWithAO",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean aGoodPlace$overrideCullingAO(BlockState blockState, BlockState blockState2, Direction direction, @Local(argsOnly = true) BlockPos pos) {
        return !BlocksParticlesManager.isBlockHidden(pos);
    }


}
