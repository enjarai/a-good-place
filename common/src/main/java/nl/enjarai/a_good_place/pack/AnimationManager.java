package nl.enjarai.a_good_place.pack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import nl.enjarai.a_good_place.AGoodPlace;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimationManager extends SimpleJsonResourceReloadListener {

    private static final List<AnimationParameters> ANIMATIONS = new ArrayList<>();

    public AnimationManager() {
        super(new Gson(), "placement_animations");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ANIMATIONS.clear();

        for (var j : jsons.entrySet()) {
            var json = j.getValue();
            var id = j.getKey();

            AnimationParameters effect = AnimationParameters.CODEC.decode(JsonOps.INSTANCE, json)
                    .getOrThrow(false, errorMsg -> AGoodPlace.LOGGER.warn("Could not decode Biome Special Effect with json id {} - error: {}", id, errorMsg))
                    .getFirst();

            ANIMATIONS.add(effect);
        }
        ANIMATIONS.sort((a, b) -> Integer.compare(b.priority(), a.priority()));

        var level = Minecraft.getInstance().level;
        if (level != null) {
            populateTags(level.registryAccess());
        }
    }


    @Nullable
    public static AnimationParameters getAnimation(BlockState blockState, BlockPos pos, RandomSource random) {
        //early nukes beds and double plants. Not optimal should be moved in the animation
        if(blockState.is(BlockTags.BEDS) || blockState.getBlock() instanceof DoublePlantBlock ||
                (blockState.hasProperty(ChestBlock.TYPE) && blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE)){
            return null;
        }
        for (var animation : ANIMATIONS) {
            if (animation.matches(blockState, pos, random)) {
                return animation;
            }
        }
        return null;
    }

    public static void populateTags(RegistryAccess registryAccess) {
        for (var animation : ANIMATIONS) {
            animation.targets().populate(registryAccess);
        }
    }
}
