package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record AnyOf(List<BlockStatePredicate> predicates) implements BlockStatePredicate {

    public static final Codec<AnyOf> CODEC = BlockStatePredicate.CODEC.listOf()
            .fieldOf("predicates").xmap(AnyOf::new, AnyOf::predicates).codec();

    @Override
    public BlockStatePredicateType<?> getType() {
        return BlockStatePredicateType.ANY_OF;
    }

    @Override
    public boolean test(BlockState state, BlockPos pos, Level level) {
        for (var p : predicates) {
            if (p.test(state, pos, level)) return true;
        }
        return false;
    }

}
