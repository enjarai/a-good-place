package nl.enjarai.a_good_place.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import nl.enjarai.a_good_place.AGoodPlace;

import java.util.function.Function;

public record AnimationParameters(LazyHolderSet<?> targets, int priority,
                                  RuleTest predicate, int duration,
                                  float scaleStart, float scaleCurve,
                                  float translationStart, float translationCurve,
                                  float rotationStart, float rotationCurve,
                                  float heightStart, float heightCurve,
                                  float rightTranslationAngle) {

    private static final Codec<Float> FLOAT_CODEC = floatRangeExclusive(-1, 1);

    public static final Codec<AnimationParameters> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.PASSTHROUGH.xmap(LazyHolderSet::new, a -> a.toDecode).fieldOf("targets").forGetter(a -> null),
            StrOpt.of(Codec.INT, "priority", 0).forGetter(AnimationParameters::priority), // not used
            StrOpt.of(RuleTest.CODEC, "predicate", AlwaysTrueTest.INSTANCE).forGetter(AnimationParameters::predicate),
            StrOpt.of(Codec.intRange(0, 300), "duration", 4).forGetter(AnimationParameters::duration),
            StrOpt.of(Codec.floatRange(0, 10), "scale", 1f).forGetter(AnimationParameters::scaleStart),
            StrOpt.of(FLOAT_CODEC, "scale_curve", 0.5f).forGetter(AnimationParameters::scaleCurve),
            StrOpt.of(Codec.floatRange(-10, 10), "translation", 0f).forGetter(AnimationParameters::translationStart),
            StrOpt.of(FLOAT_CODEC, "translation_curve", 0.5f).forGetter(AnimationParameters::translationCurve),
            StrOpt.of(Codec.floatRange(-Mth.PI, Mth.PI), "rotation", 0f).forGetter(AnimationParameters::rotationStart),
            StrOpt.of(FLOAT_CODEC, "rotation_curve", 0.5f).forGetter(AnimationParameters::rotationCurve),
            StrOpt.of(Codec.floatRange(0, 10), "height", 1f).forGetter(AnimationParameters::heightStart),
            StrOpt.of(FLOAT_CODEC, "height_curve", 0.5f).forGetter(AnimationParameters::heightCurve),
            StrOpt.of(Codec.floatRange(-Mth.PI, Mth.PI), "translation_angle", Mth.HALF_PI / 2).forGetter(AnimationParameters::rightTranslationAngle)
    ).apply(instance, AnimationParameters::new));

    public boolean matches(BlockState blockState, BlockPos pos, RandomSource random) {
        if (targets == null) return true;
        return targets.matches(blockState) && predicate.test(blockState, random);
    }


    static Codec<Float> floatRangeExclusive(final float minExclusive, final float maxExclusive) {
        final Function<Float, DataResult<Float>> checker = checkRange(minExclusive, maxExclusive);
        return Codec.FLOAT.flatXmap(checker, checker);
    }

    // private
    static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRange(final N minExclusive, final N maxExclusive) {
        return value -> {
            if (value.compareTo(minExclusive) > 0 && value.compareTo(maxExclusive) < 0) {
                return DataResult.success(value);
            }
            return DataResult.error(() -> "Value " + value + " outside of range (" + minExclusive + ":" + maxExclusive + ")", value);
        };
    }

    protected static class LazyHolderSet<T> {
        private final Dynamic<T> toDecode;
        private HolderSet<Block> instance;
        private boolean alwaysTrue;

        public LazyHolderSet(Dynamic<T> toDecode) {
            this.toDecode = toDecode;
        }

        public boolean matches(BlockState blockState) {
            return alwaysTrue || (instance != null && blockState.is(instance));
        }

        protected void populate(RegistryAccess registryAccess) {
            alwaysTrue = false;
            instance = null;
            var stringParse = toDecode.getOps().getStringValue(toDecode.getValue());
            if (stringParse.result().isPresent() && stringParse.result().get().equals("*")) {
                alwaysTrue = true;
            } else {
                var res = RegistryCodecs.homogeneousList(Registries.BLOCK)
                        .decode(RegistryOps.create(toDecode.getOps(), registryAccess), toDecode.getValue());
                try {
                    instance = res.getOrThrow(false, s -> {
                        AGoodPlace.LOGGER.error("Could not decode block list for placement animation - error: {}", s);
                    }).getFirst();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
