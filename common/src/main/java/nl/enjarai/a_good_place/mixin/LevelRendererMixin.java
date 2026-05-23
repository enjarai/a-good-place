package nl.enjarai.a_good_place.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "submitEntities", at = @At("TAIL"))
    private void agoodplace$submitParticles(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector output, CallbackInfo ci) {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        BlocksParticlesManager.submitParticles(poseStack, output, partialTick);
    }
}
