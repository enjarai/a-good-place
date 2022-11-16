package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.particle.PlacingBlockParticle;
import net.fabricmc.example.util.RenderUtil;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public class ExampleMod implements ModInitializer, ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MODID = "modid";
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final DefaultParticleType PLACING_PARTICLE = FabricParticleTypes.simple();
	public static final HashSet<BlockPos> invisibleBlocks = new HashSet<>();

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
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	public static void addInvisibleBlock(BlockPos pos) {
		invisibleBlocks.add(pos);
	}

	public static boolean isBlockInvisible(BlockPos pos) {
		return invisibleBlocks.contains(pos);
	}

	public static void clearInvisibleBlocks() {
		invisibleBlocks.clear();
	}

	public static void removeInvisibleBlock(BlockPos pos) {
		invisibleBlocks.remove(pos);
		RenderUtil.markBlockForRender(pos);
	}
}
