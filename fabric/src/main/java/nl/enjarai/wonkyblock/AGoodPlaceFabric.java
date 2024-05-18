package nl.enjarai.wonkyblock;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import nl.enjarai.wonkyblock.particle.WonkyBlocksManager;


public class AGoodPlaceFabric implements ClientModInitializer {
    public static final String MODID = "a_good_place";

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            WonkyBlocksManager.renderParticles(context.matrixStack(), context.tickDelta());
        });

        ClientTickEvents.END_WORLD_TICK.register((clientWorld) -> {
            WonkyBlocksManager.tickParticles();
        });

        //clear on level change
    }

}
