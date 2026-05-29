package nl.enjarai.a_good_place.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    @WrapOperation(
            method = "tesselateWithoutAO",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;shouldRenderFace(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean aGoodPlace$overrideCulling(BlockAndTintGetter level, BlockState state, boolean checkSides, Direction face, BlockPos neighborPos, Operation<Boolean> original) {
        if (BlocksParticlesManager.isBlockHidden(neighborPos)) {
            return true;
        }
        return original.call(level, state, checkSides, face, neighborPos);
    }

    @WrapOperation(
            method = "tesselateWithAO",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;shouldRenderFace(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean aGoodPlace$overrideCullingAO(BlockAndTintGetter level, BlockState state, boolean checkSides, Direction face, BlockPos neighborPos, Operation<Boolean> original) {
        if (BlocksParticlesManager.isBlockHidden(neighborPos)) {
            return true;
        }
        return original.call(level, state, checkSides, face, neighborPos);
    }


}
