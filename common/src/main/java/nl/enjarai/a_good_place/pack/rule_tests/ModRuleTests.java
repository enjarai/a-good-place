package nl.enjarai.a_good_place.pack.rule_tests;

import com.llamalad7.mixinextras.sugar.impl.SingleIterationList;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import nl.enjarai.a_good_place.AGoodPlace;

import java.util.function.Supplier;

public class ModRuleTests {


    public static void init(){}

    public static final Supplier<RuleTestType<NotInTagTest>> NOT_IN_TAG = AGoodPlace.registerRuleTest("not_in_tag", NotInTagTest.CODEC);
    public static final Supplier<RuleTestType<SolidTest>> SOLID = AGoodPlace.registerRuleTest("solid", SolidTest.CODEC);
    public static final Supplier<RuleTestType<NoDoubleBlocks>> NO_DOUBLE_BLOCKS = AGoodPlace.registerRuleTest("no_double_blocks", NoDoubleBlocks.CODEC);

}
