package nl.enjarai.a_good_place.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforgespi.locating.IModFile;
import nl.enjarai.a_good_place.AGoodPlace;
import nl.enjarai.a_good_place.pack.AnimationsManager;
import nl.enjarai.a_good_place.pack.state_tests.BlockStatePredicateType;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;


@Mod(AGoodPlaceImpl.MOD_ID)
public class AGoodPlaceImpl {
    public static final String MOD_ID = AGoodPlace.MOD_ID;

    public AGoodPlaceImpl(IEventBus modEventBus) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {

            modEventBus.addListener(this::onSetup);

            addClientReloadListener(AnimationsManager::new, AGoodPlace.res("animations"), modEventBus);

            boolean firstInstall = AGoodPlace.copySamplePackIfNotPresent();
            NeoForge.EVENT_BUS.register(this);

            registerOptionalTexturePack(AGoodPlace.res("default_animations"),
                    Component.nullToEmpty("Default Place Animations"), firstInstall, modEventBus);

            BlockStatePredicateType.init();
            AGoodPlace.IS_DEV = !FMLEnvironment.isProduction();
        }
    }

    public void onSetup(FMLClientSetupEvent event) {
       AGoodPlace.onSetup(null);
    }

    @SubscribeEvent
    public void onLevelLoad(ClientPlayerNetworkEvent.LoggingIn event) {
        AnimationsManager.populateTags(event.getPlayer().level().registryAccess());
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            BlocksParticlesManager.clear();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderLevelStageEvent.AfterEntities event) {
        BlocksParticlesManager.renderParticles(event.getPoseStack(), Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    @SubscribeEvent
    public void onClientTick(LevelTickEvent.Post tickEvent) {
        if (tickEvent.getLevel().isClientSide()) {
            BlocksParticlesManager.tickParticles((ClientLevel) tickEvent.getLevel());
        }
    }

    public static void renderBlock(PoseStack poseStack, MultiBufferSource buffer, BlockState state,
                                   Level level, BlockPos pos, BlockRenderDispatcher dispatcher) {
        dispatcher.renderSingleBlock(state, poseStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }

    public static void addClientReloadListener(Supplier<PreparableReloadListener> listener, Identifier location, IEventBus modEventBus) {
        Consumer<AddClientReloadListenersEvent> eventConsumer = (event) -> {
            event.addListener(location, listener.get());
        };
        modEventBus.addListener(eventConsumer);
    }

    public static void registerOptionalTexturePack(Identifier folderName, Component displayName, boolean defaultEnabled, IEventBus modEventBus) {
        registerResourcePack(PackType.CLIENT_RESOURCES,
                () -> {
                    try {
                        IModFile file = ModList.get().getModFileById(folderName.getNamespace()).getFile();
                        Path contentRoot = file.getContents().getContentRoots().iterator().next();
                        Path packPath = contentRoot.resolve("resourcepacks").resolve(folderName.getPath());
                        PackLocationInfo info = new PackLocationInfo(folderName.toString(), displayName, PackSource.BUILT_IN, Optional.empty());
                        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                            @Override
                            public PackResources openPrimary(PackLocationInfo i) { return new PathPackResources(i, packPath); }
                            @Override
                            public PackResources openFull(PackLocationInfo i, Pack.Metadata m) { return new PathPackResources(i, packPath); }
                        };
                        PackSelectionConfig selectionConfig = new PackSelectionConfig(defaultEnabled, Pack.Position.TOP, false);
                        return Pack.readMetaAndCreate(info, supplier, PackType.CLIENT_RESOURCES, selectionConfig);
                    } catch (Exception ee) {
                        if (!DatagenModLoader.isRunningDataGen()) ee.printStackTrace();
                    }
                    return null;
                },
                modEventBus
        );
    }

    public static void registerResourcePack(PackType packType, @Nullable Supplier<Pack> packSupplier, IEventBus modEventBus) {
        if (packSupplier == null) return;
        Consumer<AddPackFindersEvent> consumer = event -> {
            if (event.getPackType() == packType) {
                var p = packSupplier.get();
                if (p != null) {
                    event.addRepositorySource(infoConsumer -> infoConsumer.accept(packSupplier.get()));
                }
            }
        };
        modEventBus.addListener(consumer);
    }


}
