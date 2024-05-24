package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record MatchingBlocks(HolderSet<Block> blocks) implements BlockStatePredicate {

    public static final Codec<MatchingBlocks> CODEC = RegistryCodecs.homogeneousList(Registries.BLOCK)
            .fieldOf("blocks").xmap(MatchingBlocks::new, MatchingBlocks::blocks).codec();


    @Override
    public BlockStatePredicateType<?> getType() {
        return BlockStatePredicateType.MATCHING_BLOCKS;
    }

    @Override
    public boolean test(BlockState state, BlockPos pos, Level level) {
        return state.is(blocks);
    }

}
