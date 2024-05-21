package nl.enjarai.a_good_place.mixins.fabric.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.particles.WonkyBlocksManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {

    @Inject(
            method = "renderModel",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void wonkyblock$hideBlock(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed, RenderType layer, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (WonkyBlocksManager.isBlockHidden(pos)) {
            cir.setReturnValue(false);
        }
    }
}
