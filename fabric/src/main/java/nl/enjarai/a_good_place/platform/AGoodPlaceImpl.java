package nl.enjarai.a_good_place.platform;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.enjarai.a_good_place.AGoodPlace;
import nl.enjarai.a_good_place.pack.AnimationsManager;
import nl.enjarai.a_good_place.pack.state_tests.BlockStatePredicateType;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;


public class AGoodPlaceImpl implements ClientModInitializer {

    public static final String MOD_ID = AGoodPlace.MOD_ID;

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            BlocksParticlesManager.renderParticles(context.matrices(),
                    Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(AGoodPlace::onSetup);
        ClientTickEvents.END_WORLD_TICK.register(BlocksParticlesManager::tickParticles);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            AnimationsManager.populateTags(client.getConnection().registryAccess());
        });

        AGoodPlace.copySamplePackIfNotPresent();
        addClientReloadListener(AnimationsManager::new, AGoodPlace.res("animations"));
        registerOptionalTexturePack(AGoodPlace.res("default_animations"),
                Component.nullToEmpty("A Good Place Default Animation"), true);

        BlockStatePredicateType.init();

        AGoodPlace.IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static void renderBlock(PoseStack poseStack, MultiBufferSource buffer, BlockState state, Level level, BlockPos pos, BlockRenderDispatcher blockRenderer, int packedLight) {
        blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    public static void addClientReloadListener(final Supplier<PreparableReloadListener> listener, final Identifier name) {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final Supplier<PreparableReloadListener> inner = Suppliers.memoize(listener::get);

            public Identifier getFabricId() {
                return name;
            }

            @Override
            public CompletableFuture<Void> reload(PreparableReloadListener.SharedState sharedState, Executor executor, PreparableReloadListener.PreparationBarrier barrier, Executor applyExecutor) {
                return this.inner.get().reload(sharedState, executor, barrier, applyExecutor);
            }
        });
    }


    public static void registerOptionalTexturePack(Identifier folderName, Component displayName, boolean defaultEnabled) {
        FabricLoader.getInstance().getModContainer(folderName.getNamespace()).ifPresent(c -> {
            ResourceManagerHelper.registerBuiltinResourcePack(folderName, c,
                    defaultEnabled ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL);
        });
    }

}
