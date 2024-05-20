package nl.enjarai.a_good_place.mixins.fabric.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import nl.enjarai.a_good_place.particles.WonkyBlocksManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {

    @Inject(
            method = "renderModel",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void wonkyblock$hideBlock(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        if (WonkyBlocksManager.isBlockHidden(ctx.pos())) {
            ci.cancel();
        }
    }
}
