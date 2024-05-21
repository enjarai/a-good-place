package nl.enjarai.a_good_place.pack.rule_tests;

import com.mojang.serialization.Codec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class SolidTest extends RuleTest {
    public static final Codec<SolidTest> CODEC = Codec.BOOL
            .fieldOf("solid").xmap(SolidTest::new, t -> t.solid).codec();

    public SolidTest(boolean solid) {
        this.solid = solid;
    }

    private final boolean solid;

    @Override
    public boolean test(BlockState state, RandomSource random) {
        return solid ^ state.getMaterial().isSolid();
    }

    @Override
    protected RuleTestType<?> getType() {
        return ModRuleTests.SOLID.get();
    }
}
