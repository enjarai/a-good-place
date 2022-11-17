package nl.enjarai.wonkyblock.compat.sodium.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.math.BlockPos;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask")
public abstract class TerrainBuildTaskMixin {
    private BlockPos pos;

    @Dynamic
    @ModifyReturnValue(
            method = "performBuild",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;<init>()V",
                    ordinal = 0
            )
    )
    private BlockPos.Mutable wonkyblock$catchLocals(BlockPos.Mutable blockPos) {
        pos = blockPos;
        return blockPos;
    }

    @Dynamic
    @ModifyReturnValue(
            method = "performBuild",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"
            )
    )
    private BlockRenderType wonkyblock$hideBlock(BlockRenderType renderType) {
        var pos = this.pos;
        this.pos = null;

        if (WonkyBlock.isBlockInvisible(pos)) {
            return BlockRenderType.INVISIBLE;
        }
        return renderType;
    }
}
