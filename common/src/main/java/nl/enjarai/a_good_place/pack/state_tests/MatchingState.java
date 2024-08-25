package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public record MatchingState(BlockState blockState) implements BlockStatePredicate {

    public static final MapCodec<MatchingState> CODEC = BlockState.CODEC.fieldOf("block_state")
            .xmap(MatchingState::new, MatchingState::blockState);


    @Override
    public BlockStatePredicateType<?> getType() {
        return BlockStatePredicateType.MATCHING_STATE;
    }

    @Override
    public boolean test(BlockState state, BlockPos pos, Level level) {
        return state == this.blockState;
    }

}
