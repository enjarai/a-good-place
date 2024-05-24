package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStatePredicate {

    Codec<BlockStatePredicate> CODEC = BlockStatePredicateType.CODEC
            .dispatch("type", BlockStatePredicate::getType, BlockStatePredicateType::codec);

    boolean test(BlockState state, BlockPos pos, Level level);

    BlockStatePredicateType<?> getType();

}
