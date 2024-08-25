package nl.enjarai.a_good_place.pack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.a_good_place.AGoodPlace;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnimationsManager extends SimpleJsonResourceReloadListener {

    private static final List<AnimationParameters> ANIMATIONS = new ArrayList<>();

    public AnimationsManager() {
        super(new Gson(), "placement_animations");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ANIMATIONS.clear();

        for (var j : jsons.entrySet()) {
            var json = j.getValue();
            var id = j.getKey();

            AnimationParameters effect = AnimationParameters.CODEC.decode(JsonOps.INSTANCE, json)
                    .getOrThrow(errorMsg ->
                            new JsonParseException(
                                    "Could not decode Block Placement Animation with json id " + id + " - error: " + errorMsg + " - json: " + j))
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
    public static AnimationParameters getAnimation(BlockState blockState, BlockPos pos, Level level) {

        //for testing
        if (AGoodPlace.IS_DEV && false) {
            var a = new AnimationParameters(new AnimationParameters.LazyList<>(null, null),
                    0, 3,
                    1f, -0.7f,
                    new Vec3(0.1, 0.2, 0.2), 0.9f,
                    new Vec3(0, -0.1, 0.1),
                    new Vec3(0.5, -0.5, 0.5), -0.08f,
                    1, 0, Optional.empty());
            if (a.matches(blockState, pos, level)) {
                return a;
            } else return null;
        }
        for (var animation : ANIMATIONS) {
            if (animation.matches(blockState, pos, level)) {
                return animation;
            }
        }
        return null;
    }

    public static void populateTags(RegistryAccess registryAccess) {
        for (var v : ANIMATIONS) {
            v.predicates().lazyInit(registryAccess);
        }
    }
}
