package nl.enjarai.a_good_place.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public record AnimationParameters(HolderSet<Block> targets, RuleTest predicate, float speed) {

    public static final Codec<AnimationParameters> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("targets").forGetter(AnimationParameters::targets),
            StrOpt.of(RuleTest.CODEC, "predicate", AlwaysTrueTest.INSTANCE).forGetter(AnimationParameters::predicate),
            StrOpt.of(Codec.FLOAT, "speed", 1f).forGetter(AnimationParameters::speed)
    ).apply(instance, AnimationParameters::new));

    public boolean matches(BlockState blockState, BlockPos pos, RandomSource random){
        return blockState.is(targets) && predicate.test(blockState, random);
    }
}
