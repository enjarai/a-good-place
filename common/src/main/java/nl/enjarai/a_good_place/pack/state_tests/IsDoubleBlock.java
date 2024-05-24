package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class IsDoubleBlock implements BlockStatePredicate {

    public static final IsDoubleBlock INSTANCE = new IsDoubleBlock();
    public static final Codec<IsDoubleBlock> CODEC = Codec.unit(INSTANCE);

    @Override
    public boolean test(BlockState state, BlockPos pos, Level level) {
        return state.is(BlockTags.BEDS) || (state.getBlock() instanceof DoublePlantBlock) ||
                (state.getBlock() instanceof DoorBlock) ||
                (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE);
    }

    @Override
    public BlockStatePredicateType<?> getType() {
        return BlockStatePredicateType.IS_DOUBLE_BLOCK;
    }


}
