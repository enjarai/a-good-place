package net.fabricmc.example.mixin;

import net.fabricmc.example.util.MixinHooks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Inject(
            method = "getRenderType",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideBlock(CallbackInfoReturnable<BlockRenderType> cir) {
        if (MixinHooks.invisibleBlockCount > 0) {
            cir.setReturnValue(BlockRenderType.INVISIBLE);
            MixinHooks.invisibleBlockCount--;
        }
    }
}
