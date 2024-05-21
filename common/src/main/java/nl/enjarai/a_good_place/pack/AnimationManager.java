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
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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

        if (true) return new AnimationParameters(List.of(), 0, 4,
                1f, -0.7f,
                new Vec3(0, 0, 4), 0.9f,
                new Vec3(0, 0.07f, 0.07f),
                new Vec3(-0.5, -0.5, -0.5), -0.08f,
                1, 0);

        for (var animation : ANIMATIONS) {
            if (animation.matches(blockState, pos, random)) {
                return animation;
            }
        }
        return null;
    }

    public static void populateTags(RegistryAccess registryAccess) {
    }
}
