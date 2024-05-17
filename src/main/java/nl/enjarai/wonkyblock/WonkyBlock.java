package nl.enjarai.wonkyblock;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import nl.enjarai.wonkyblock.util.WonkyBlockParticlesManager;
import nl.enjarai.wonkyblock.util.RendererImplementation;
import nl.enjarai.wonkyblock.util.VanillaRendererImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WonkyBlock implements ClientModInitializer, PreLaunchEntrypoint {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MODID = "wonkyblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final SimpleParticleType PLACING_PARTICLE = FabricParticleTypes.simple();

    private static final RendererImplementation renderer = new VanillaRendererImplementation();
    private static final WonkyBlockParticlesManager invisibleBlocks = new WonkyBlockParticlesManager(renderer);;


    @Override
    public void onInitializeClient() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static RendererImplementation getRenderer() {
        return renderer;
    }

    public static WonkyBlockParticlesManager getStuff() {
        return invisibleBlocks;
    }
}
