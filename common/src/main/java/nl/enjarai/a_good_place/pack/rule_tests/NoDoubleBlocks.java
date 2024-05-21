package nl.enjarai.a_good_place.pack.rule_tests;

import com.mojang.serialization.Codec;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class NoDoubleBlocks extends RuleTest {
    public static final NoDoubleBlocks INSTANCE = new NoDoubleBlocks();
    public static final Codec<NoDoubleBlocks> CODEC = Codec.unit(INSTANCE);

    @Override
    public boolean test(BlockState state, RandomSource random) {
        //early nukes beds and double plants. Not optimal should be moved in the animation
        return !state.is(BlockTags.BEDS) && !(state.getBlock() instanceof DoublePlantBlock) &&
                (!state.hasProperty(ChestBlock.TYPE) || state.getValue(ChestBlock.TYPE) == ChestType.SINGLE);
    }

    @Override
    protected RuleTestType<?> getType() {
        return ModRuleTests.NO_DOUBLE_BLOCKS.get();
    }
}
