package nl.enjarai.a_good_place.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public record AnimationParameters(HolderSet<Block> targets, RuleTest predicate, int duration,
                                  float scaleStart, float scaleCurve,
                                  float translationStart, float translationCurve,
                                  float rotationStart, float rotationCurve,
                                  float rightTranslationAngle) {

    public static final Codec<AnimationParameters> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("targets").forGetter(AnimationParameters::targets),
            StrOpt.of(RuleTest.CODEC, "predicate", AlwaysTrueTest.INSTANCE).forGetter(AnimationParameters::predicate),
            StrOpt.of(Codec.intRange(0, 300), "duration", 7).forGetter(AnimationParameters::duration),
            StrOpt.of(Codec.floatRange(0, 10), "initial_scale", 0.4f).forGetter(AnimationParameters::scaleStart),
            StrOpt.of(Codec.floatRange(-1, 1), "initial_scale_curve", 0.5f).forGetter(AnimationParameters::scaleCurve),
            StrOpt.of(Codec.floatRange(-10, 10), "initial_translation", 0.4f).forGetter(AnimationParameters::translationStart),
            StrOpt.of(Codec.floatRange(-1, 1), "initial_translation_curve", 0.5f).forGetter(AnimationParameters::translationCurve),
            StrOpt.of(Codec.floatRange(-Mth.PI, Mth.PI), "initial_rotation", 0.4f).forGetter(AnimationParameters::rotationStart),
            StrOpt.of(Codec.floatRange(-1, 1), "initial_rotation_curve", 0.5f).forGetter(AnimationParameters::rotationCurve),
            StrOpt.of(Codec.floatRange(-Mth.PI, Mth.PI), "right_translation_angle", 0.5f).forGetter(AnimationParameters::rightTranslationAngle)
    ).apply(instance, AnimationParameters::new));

    public boolean matches(BlockState blockState, BlockPos pos, RandomSource random) {
        if (targets == null) return true;
        return blockState.is(targets) && predicate.test(blockState, random);
    }
}
