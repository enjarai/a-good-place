package nl.enjarai.wonkyblock;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import nl.enjarai.wonkyblock.compat.sodium.SodiumRendererImplementation;
import nl.enjarai.wonkyblock.particle.PlacingBlockParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import nl.enjarai.wonkyblock.util.BlockTracker;
import nl.enjarai.wonkyblock.util.RendererImplementation;
import nl.enjarai.wonkyblock.util.VanillaRendererImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class WonkyBlock implements ModInitializer, ClientModInitializer, PreLaunchEntrypoint {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MODID = "wonkyblock";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final DefaultParticleType PLACING_PARTICLE = FabricParticleTypes.simple();

	private static RendererImplementation renderer;
	private static BlockTracker invisibleBlocks;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}

	@Override
	public void onInitializeClient() {
		Registry.register(Registry.PARTICLE_TYPE, id("placing_particle"), PLACING_PARTICLE);
		ParticleFactoryRegistry.getInstance().register(PLACING_PARTICLE, new PlacingBlockParticle.Factory());

		if (FabricLoader.getInstance().isModLoaded("sodium")) {
			renderer = new SodiumRendererImplementation();
		} else {
			renderer = new VanillaRendererImplementation();
		}

		invisibleBlocks = new BlockTracker(renderer);
	}

	@Override
	public void onPreLaunch() {
		MixinExtrasBootstrap.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	public static RendererImplementation getRenderer() {
		return renderer;
	}

	public static BlockTracker getInvisibleBlocks() {
		return invisibleBlocks;
	}
}
