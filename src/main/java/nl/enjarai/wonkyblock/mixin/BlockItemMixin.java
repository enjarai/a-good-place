package nl.enjarai.wonkyblock.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "updateBlockStateFromTag",
            at = @At("HEAD")
    )
    private void wonkyblock$startPlaceAnimation(BlockPos pos, Level world, ItemStack stack, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (world.isClientSide) {
            WonkyBlock.getInvisibleBlocks().add(pos);
            world.addAlwaysVisibleParticle(WonkyBlock.PLACING_PARTICLE, true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
    }
}
