package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
//    @Inject(
//            method = "onBreak",
//            at = @At("HEAD")
//    )
//    private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
//        if (world.isClient()) {
//            ExampleMod.addInvisibleBlock(world, pos);
//        }
//    }

    @Inject(
            method = "shouldDrawSide",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        if (ExampleMod.isBlockInvisible(otherPos)) {
            cir.setReturnValue(true);
        }
    }
}
