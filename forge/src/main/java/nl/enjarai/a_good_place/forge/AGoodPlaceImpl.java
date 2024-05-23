package nl.enjarai.a_good_place.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.resource.PathPackResources;
import nl.enjarai.a_good_place.AGoodPlace;
import nl.enjarai.a_good_place.pack.AnimationsManager;
import nl.enjarai.a_good_place.pack.rule_tests.ModRuleTests;
import nl.enjarai.a_good_place.particles.BlocksParticlesManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


@Mod(AGoodPlaceImpl.MOD_ID)
public class AGoodPlaceImpl {
    public static final String MOD_ID = AGoodPlace.MOD_ID;

    private static final DeferredRegister<RuleTestType<?>> RULE_TESTS = DeferredRegister.create(Registries.RULE_TEST, MOD_ID);

    public AGoodPlaceImpl() {
        if (FMLEnvironment.dist == Dist.CLIENT) {

            addClientReloadListener(AnimationsManager::new, AGoodPlace.res("animations"));

            boolean firstInstall = AGoodPlace.copySamplePackIfNotPresent();
            MinecraftForge.EVENT_BUS.register(this);

            registerOptionalTexturePack(AGoodPlace.res("default_animations"),
                    Component.nullToEmpty("Default Place Animations"), firstInstall);

            RULE_TESTS.register(FMLJavaModLoadingContext.get().getModEventBus());

            ModRuleTests.init();
        }
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
    public void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            BlocksParticlesManager.renderParticles(event.getPoseStack(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.LevelTickEvent tickEvent) {
        if (tickEvent.phase == TickEvent.Phase.END && tickEvent.level.isClientSide) {
            BlocksParticlesManager.tickParticles((ClientLevel) tickEvent.level);
        }
    }

    public static void renderBlock(BakedModel model, long seed, PoseStack poseStack, MultiBufferSource buffer, BlockState state,
                                   Level level, BlockPos pos, BlockRenderDispatcher dispatcher) {
        //same as ForgeHooksClient.renderPistonMovedBlocks (what pistons use)
        for (var renderType : model.getRenderTypes(state, RandomSource.create(seed), ModelData.EMPTY)) {
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType));
            dispatcher.getModelRenderer().tesselateBlock(level, model, state, pos, poseStack, vertexConsumer, false,
                    RandomSource.create(), state.getSeed(pos), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
        }
    }

    public static void addClientReloadListener(Supplier<PreparableReloadListener> listener, ResourceLocation location) {
        Consumer<RegisterClientReloadListenersEvent> eventConsumer = (event) -> {
            event.registerReloadListener(listener.get());
        };
        FMLJavaModLoadingContext.get().getModEventBus().addListener(eventConsumer);
    }

    public static void registerOptionalTexturePack(ResourceLocation folderName, Component displayName, boolean defaultEnabled) {
        registerResourcePack(PackType.CLIENT_RESOURCES,
                () -> {
                    IModFile file = ModList.get().getModFileById(folderName.getNamespace()).getFile();
                    try (PathPackResources pack = new PathPackResources(
                            folderName.toString(),
                            true,
                            file.findResource("resourcepacks/" + folderName.getPath()))) {
                        var metadata = Objects.requireNonNull(pack.getMetadataSection(PackMetadataSection.TYPE));
                        return Pack.create(
                                folderName.toString(),
                                displayName,
                                defaultEnabled,
                                (s) -> pack,
                                new Pack.Info(metadata.getDescription(), metadata.getPackFormat(), FeatureFlagSet.of()),
                                PackType.CLIENT_RESOURCES,
                                Pack.Position.TOP,
                                false,
                                PackSource.BUILT_IN);
                    } catch (Exception ee) {
                        if (!DatagenModLoader.isRunningDataGen()) ee.printStackTrace();
                    }
                    return null;
                }
        );
    }

    public static void registerResourcePack(PackType packType, @Nullable Supplier<Pack> packSupplier) {
        if (packSupplier == null) return;
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        Consumer<AddPackFindersEvent> consumer = event -> {
            if (event.getPackType() == packType) {
                var p = packSupplier.get();
                if (p != null) {
                    event.addRepositorySource(infoConsumer -> infoConsumer.accept(packSupplier.get()));
                }
            }
        };
        bus.addListener(consumer);
    }

    public static <T extends RuleTest> Supplier<RuleTestType<T>> registerRuleTest(String id, Codec<T> codec) {
        return RULE_TESTS.register(id, () -> () -> codec);
    }
}
