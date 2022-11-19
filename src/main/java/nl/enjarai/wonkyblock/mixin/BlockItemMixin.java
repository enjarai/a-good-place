package nl.enjarai.wonkyblock.mixin;

import nl.enjarai.wonkyblock.WonkyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "placeFromNbt",
            at = @At("HEAD")
    )
    private void wonkyblock$startPlaceAnimation(BlockPos pos, World world, ItemStack stack, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (world.isClient()) {
            WonkyBlock.getInvisibleBlocks().add(pos);
            world.addImportantParticle(WonkyBlock.PLACING_PARTICLE, true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
    }
}
