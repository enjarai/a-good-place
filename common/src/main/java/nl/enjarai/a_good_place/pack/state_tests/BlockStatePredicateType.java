package nl.enjarai.a_good_place.pack.state_tests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record BlockStatePredicateType<T extends BlockStatePredicate>(MapCodec<T> codec, String name) {

    public static final Codec<BlockStatePredicateType<?>> CODEC = Codec.STRING.flatXmap(
            (name) -> BlockStatePredicateType.get(name).map(DataResult::success).orElseGet(
                    () -> DataResult.error(() -> "Unknown BlockState Predicate: " + name)),
            (t) -> DataResult.success(t.name()));

    private static final Map<String, BlockStatePredicateType<?>> TYPES = new HashMap<>();

    public static <B extends BlockStatePredicate> BlockStatePredicateType<B> register(String name, MapCodec<B> codec) {
        BlockStatePredicateType<B> value = new BlockStatePredicateType<>(codec, name);
        TYPES.put(name, value);
        return value;
    }

    public static Optional<? extends BlockStatePredicateType<? extends BlockStatePredicate>> get(String name) {
        return Optional.ofNullable(TYPES.get(name));
    }


    public static final BlockStatePredicateType<IsDoubleBlock> IS_DOUBLE_BLOCK = register("is_double_block", IsDoubleBlock.CODEC);
    public static final BlockStatePredicateType<HasCollision> HAS_COLLISION = register("has_collision", HasCollision.CODEC);
    public static final BlockStatePredicateType<Not> NOT = register("not", Not.CODEC);
    public static final BlockStatePredicateType<AnyOf> ANY_OF = register("any_of", AnyOf.CODEC);
    public static final BlockStatePredicateType<MatchingBlocks> MATCHING_BLOCKS = register("matching_blocks", MatchingBlocks.CODEC);
    public static final BlockStatePredicateType<MatchingState> MATCHING_STATE = register("matching_state", MatchingState.CODEC);

    public static void init() {
    }
}
