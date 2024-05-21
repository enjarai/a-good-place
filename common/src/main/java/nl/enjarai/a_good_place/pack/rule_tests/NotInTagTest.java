package nl.enjarai.a_good_place.pack.rule_tests;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class NotInTagTest extends RuleTest {
    public static final Codec<NotInTagTest> CODEC = TagKey.codec(Registry.BLOCK_REGISTRY)
            .fieldOf("tag").xmap(NotInTagTest::new, t -> t.tag).codec();

    public NotInTagTest(TagKey<Block> tag) {
        this.tag = tag;
    }

    private final TagKey<Block> tag;

    @Override
    public boolean test(BlockState state, RandomSource random) {
        return !state.is(tag);
    }

    @Override
    protected RuleTestType<?> getType() {
        return ModRuleTests.NOT_IN_TAG.get();
    }
}
