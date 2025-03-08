package nl.enjarai.a_good_place.fabric;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
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
            BlocksParticlesManager.renderParticles(context.matrixStack(), context.tickDelta());
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(AGoodPlace::onSetup);
        ClientTickEvents.END_WORLD_TICK.register(BlocksParticlesManager::tickParticles);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            AnimationsManager.populateTags(client.getConnection().registryAccess());
        });

        //todo: clear on level change

        AGoodPlace.copySamplePackIfNotPresent();
        addClientReloadListener(AnimationsManager::new, AGoodPlace.res("animations"));
        registerOptionalTexturePack(AGoodPlace.res("default_animations"),
                Component.nullToEmpty("A Good Place Default Animation"), true);

        BlockStatePredicateType.init();

        AGoodPlace.IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    private static final boolean CONTINUITY = FabricLoader.getInstance().isModLoaded("continuity");

    public static void renderBlock(BakedModel model, long seed, PoseStack poseStack, MultiBufferSource buffer, BlockState state, Level level, BlockPos pos, BlockRenderDispatcher blockRenderer) {
        RenderType movingBlockRenderType = ItemBlockRenderTypes.getMovingBlockRenderType(state);
        //bs because of FABRIC BUG which makes above method not return correct render type, skipping their OWN API
        if (CONTINUITY && movingBlockRenderType != RenderType.translucent()) {
            movingBlockRenderType = RenderType.cutout();
        }
        blockRenderer.getModelRenderer().tesselateBlock(level, blockRenderer.getBlockModel(state), state, pos, poseStack,
                buffer.getBuffer(movingBlockRenderType),
                false, RandomSource.create(), seed, OverlayTexture.NO_OVERLAY);
    }

    public static void addClientReloadListener(final Supplier<PreparableReloadListener> listener, final ResourceLocation name) {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final Supplier<PreparableReloadListener> inner = Suppliers.memoize(listener::get);

            public ResourceLocation getFabricId() {
                return name;
            }

            public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return this.inner.get().reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });
    }


    public static void registerOptionalTexturePack(ResourceLocation folderName, Component displayName, boolean defaultEnabled) {
        FabricLoader.getInstance().getModContainer(folderName.getNamespace()).ifPresent(c -> {
            ResourceManagerHelper.registerBuiltinResourcePack(folderName, c,
                    defaultEnabled ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL);
        });
    }

}
