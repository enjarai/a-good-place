package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record Not(BlockStatePredicate predicate) implements BlockStatePredicate {

    public static final MapCodec<Not> CODEC = BlockStatePredicate.CODEC
            .fieldOf("predicate").xmap(Not::new, Not::predicate);

    @Override
    public BlockStatePredicateType<?> getType() {
        return BlockStatePredicateType.NOT;
    }

    @Override
    public boolean test(BlockState state, BlockPos pos, Level level) {
        return !predicate.test(state, pos, level);
    }

}
