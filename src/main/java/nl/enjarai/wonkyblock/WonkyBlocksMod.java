package nl.enjarai.wonkyblock;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.resources.ResourceLocation;
import nl.enjarai.wonkyblock.particle.WonkyBlocksManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WonkyBlocksMod implements ClientModInitializer {
    public static final String MODID = "wonkyblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            WonkyBlocksManager.renderParticles(context.matrixStack(), context.tickDelta());
        });

        ClientTickEvents.END_WORLD_TICK.register((clientWorld) -> {
            WonkyBlocksManager.tickParticles();
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

}
