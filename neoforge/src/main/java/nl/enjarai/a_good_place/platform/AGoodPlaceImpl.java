package nl.enjarai.a_good_place.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.resource.JarContentsPackResources;
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

import javax.annotation.Nullable;
import java.util.Optional;


@Mod(AGoodPlaceImpl.MOD_ID)
public class AGoodPlaceImpl {
    public static final String MOD_ID = AGoodPlace.MOD_ID;
    private final boolean firstInstall;

    public AGoodPlaceImpl(IEventBus bus) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {

            bus.addListener(this::onSetup);
            bus.addListener(this::addClientReloadListener);
            bus.addListener(this::registerResourcePack);

            this.firstInstall = AGoodPlace.copySamplePackIfNotPresent();
            NeoForge.EVENT_BUS.register(this);

            BlockStatePredicateType.init();
            AGoodPlace.IS_DEV = !FMLLoader.getCurrent().isProduction();
        } else {
            this.firstInstall = false;
        }
    }

    public void addClientReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(AGoodPlace.res("animations"), new AnimationsManager());
    }

    public void registerResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {

            event.addRepositorySource(infoConsumer -> {
                Pack optionalPack = createOptionalPack(AGoodPlace.res("default_animations"),
                        Component.nullToEmpty("Default Place Animations"), firstInstall);
                if (optionalPack != null) infoConsumer.accept(optionalPack);
            });
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
        BlocksParticlesManager.renderParticles(event.getPoseStack(),
                Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    @SubscribeEvent
    public void onClientTick(LevelTickEvent.Post tickEvent) {
        if (tickEvent.getLevel().isClientSide()) {
            BlocksParticlesManager.tickParticles((ClientLevel) tickEvent.getLevel());
        }
    }

    public static void renderBlock(PoseStack poseStack, MultiBufferSource buffer, BlockState state,
                                   Level level, BlockPos pos, BlockRenderDispatcher dispatcher, int packedLight) {
        dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, level, pos);
    }

    @Nullable
    public static Pack createOptionalPack(Identifier folderName, Component displayName, boolean defaultEnabled) {
        IModFile file = ModList.get().getModFileById(folderName.getNamespace()).getFile();
        PackLocationInfo locationInfo = new PackLocationInfo(
                folderName.toString(),
                displayName,
                PackSource.BUILT_IN,
                Optional.empty()
        );
        try {
            JarContents contents = file.getContents();
            String prefix = "resourcepacks/" + folderName.getPath();
            return Pack.readMetaAndCreate(
                    locationInfo,
                    new JarContentsPackResources.JarContentsResourcesSupplier(contents, prefix),
                    PackType.CLIENT_RESOURCES,
                    new PackSelectionConfig(
                            defaultEnabled,
                            Pack.Position.TOP,
                            false
                    ));
        } catch (Exception ee) {
            if (!DatagenModLoader.isRunningDataGen()) ee.printStackTrace();
        }
        return null;
    }


}
