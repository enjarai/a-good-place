package nl.enjarai.wonkyblock;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import nl.enjarai.wonkyblock.particle.PlacingBlockParticle;
import nl.enjarai.wonkyblock.util.BlockTracker;
import nl.enjarai.wonkyblock.util.RendererImplementation;
import nl.enjarai.wonkyblock.util.VanillaRendererImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WonkyBlock implements ModInitializer, ClientModInitializer, PreLaunchEntrypoint {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MODID = "wonkyblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final SimpleParticleType PLACING_PARTICLE = FabricParticleTypes.simple();

    private static final RendererImplementation renderer = new VanillaRendererImplementation();
    private static final BlockTracker invisibleBlocks = new BlockTracker(renderer);;

    @Override
    public void onInitialize() {
    }

    @Override
    public void onInitializeClient() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("placing_particle"), PLACING_PARTICLE);
        ParticleFactoryRegistry.getInstance().register(PLACING_PARTICLE, new PlacingBlockParticle.Factory());
    }

    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static RendererImplementation getRenderer() {
        return renderer;
    }

    public static BlockTracker getInvisibleBlocks() {
        return invisibleBlocks;
    }
}
