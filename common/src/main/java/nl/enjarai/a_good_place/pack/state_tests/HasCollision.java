package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HasCollision implements BlockStatePredicate {

    public static final HasCollision INSTANCE = new HasCollision();
    public static final MapCodec<HasCollision> CODEC = MapCodec.unit(INSTANCE);


    @Override
    public BlockStatePredicateType<?> getType() {
        return BlockStatePredicateType.HAS_COLLISION;
    }

    @Override
    public boolean test(BlockState state, BlockPos pos, Level level) {
        return !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

}
