package nl.enjarai.a_good_place.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.a_good_place.AGoodPlace;
import nl.enjarai.a_good_place.pack.state_tests.BlockStatePredicate;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record AnimationParameters(LazyList<?, BlockStatePredicate> predicates, int priority, int duration,
                                  float scaleStart, float scaleCurve,
                                  Vec3 translation, float translationCurve,
                                  Vec3 rotation, Vec3 pivot,
                                  float rotationCurve,
                                  float heightStart, float heightCurve,
                                  Optional<Holder<SoundEvent>> sound
) {

    private static final Codec<Float> FLOAT_CODEC = floatRangeExclusive(-1, 1);
    private static final Codec<Float> DEG_TO_RAD_CODEC = Codec.floatRange(-180, 180)
            .xmap(d -> (float) Math.toRadians(d), r -> (float) Math.toDegrees(r));
    public static final Codec<Vec3> ANGLE_VEC_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            DEG_TO_RAD_CODEC.fieldOf("x").forGetter(o -> (float) o.x()),
            DEG_TO_RAD_CODEC.fieldOf("y").forGetter(o -> (float) o.y()),
            DEG_TO_RAD_CODEC.fieldOf("z").forGetter(o -> (float) o.z())
    ).apply(instance, Vec3::new));
    //normal vector codec is just an array. This is a bit more readable
    public static final Codec<Vec3> VEC_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
    ).apply(instance, Vec3::new));

    public static final Codec<AnimationParameters> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            StrOpt.of(LazyList.codec(BlockStatePredicate.CODEC), "predicates", new LazyList<>(null, null))
                    .forGetter(AnimationParameters::predicates),
            StrOpt.of(Codec.INT, "priority", 0).forGetter(AnimationParameters::priority), // not used
            StrOpt.of(Codec.intRange(0, 300), "duration", 4).forGetter(AnimationParameters::duration),
            StrOpt.of(Codec.floatRange(0, 10), "scale", 1f).forGetter(AnimationParameters::scaleStart),
            StrOpt.of(FLOAT_CODEC, "scale_curve", 0.5f).forGetter(AnimationParameters::scaleCurve),
            StrOpt.of(VEC_CODEC, "translation", Vec3.ZERO).forGetter(AnimationParameters::translation),
            StrOpt.of(FLOAT_CODEC, "translation_curve", 0.5f).forGetter(AnimationParameters::translationCurve),
            StrOpt.of(ANGLE_VEC_CODEC, "rotation", Vec3.ZERO).forGetter(AnimationParameters::rotation),
            StrOpt.of(VEC_CODEC, "rotation_pivot", Vec3.ZERO).forGetter(AnimationParameters::pivot),
            StrOpt.of(FLOAT_CODEC, "rotation_curve", 0.5f).forGetter(AnimationParameters::rotationCurve),
            StrOpt.of(Codec.floatRange(0, 10), "height", 1f).forGetter(AnimationParameters::heightStart),
            StrOpt.of(FLOAT_CODEC, "height_curve", 0.5f).forGetter(AnimationParameters::heightCurve),
            StrOpt.of(SoundEvent.CODEC, "sound").forGetter(AnimationParameters::sound)
    ).apply(instance, AnimationParameters::new));

    public boolean matches(BlockState blockState, BlockPos pos, Level level) {
        var pred = this.predicates.get();
        if (AGoodPlace.isHardcodedBlackList(blockState)) return false;
        return pred.stream().allMatch(p -> p.test(blockState, pos, level));
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

    protected static class LazyList<T, O> {
        private final Codec<O> codec;
        private final Dynamic<T> toDecode;
        private List<O> objects = List.of();

        public LazyList(Dynamic<T> toDecode, Codec<O> codec) {
            this.toDecode = toDecode;
            this.codec = codec;
        }

        public static <H> Codec<LazyList<?, H>> codec(Codec<H> codec) {
            return Codec.PASSTHROUGH.xmap(
                    o -> new LazyList<>(o, codec),
                    o -> o.toDecode
            );
        }

        public List<O> get() {
            return objects;
        }

        protected void lazyInit(RegistryAccess registryAccess) {
            objects = List.of();

            var res = codec.listOf().decode(RegistryOps.create(toDecode.getOps(), registryAccess), toDecode.getValue());
            try {
                objects = res.getOrThrow(false, s -> {
                    AGoodPlace.LOGGER.error("Could not decode block list for placement animation - error: {}", s);
                }).getFirst();
            } catch (Exception ignored) {
            }
        }
    }
}
