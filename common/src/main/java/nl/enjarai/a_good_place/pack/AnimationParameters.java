package nl.enjarai.a_good_place.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public record AnimationParameters(HolderSet<Block> targets, RuleTest predicate, int duration) {

    public static final AnimationParameters DEFAULT = new AnimationParameters(null, AlwaysTrueTest.INSTANCE, 7);

    public static final Codec<AnimationParameters> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("targets").forGetter(AnimationParameters::targets),
            StrOpt.of(RuleTest.CODEC, "predicate", AlwaysTrueTest.INSTANCE).forGetter(AnimationParameters::predicate),
            StrOpt.of(Codec.INT, "duration", 7).forGetter(AnimationParameters::duration)
    ).apply(instance, AnimationParameters::new));

    public boolean matches(BlockState blockState, BlockPos pos, RandomSource random) {
        if (targets == null) return true;
        return blockState.is(targets) && predicate.test(blockState, random);
    }
}
