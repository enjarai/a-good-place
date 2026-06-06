package nl.enjarai.a_good_place.platform;

import net.neoforged.neoforge.common.ModConfigSpec;
import nl.enjarai.a_good_place.AGoodPlace;

public final class ClientConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue USE_SIMPLE_RENDERER;
    public static final ModConfigSpec.BooleanValue RENDER_AS_VANILLA_PARTICLES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        USE_SIMPLE_RENDERER = builder
                .comment(
                        "Render placing blocks via renderSingleBlock with a uniform packed light",
                        "(max of pos + 6 neighbors) instead of the per-face tesselateBlock path.",
                        "Less accurate (no per-face / AO lighting) but a useful fallback if the",
                        "default renderer interacts badly with another mod's rendering pipeline.")
                .define("useSimpleRenderer", false);

        RENDER_AS_VANILLA_PARTICLES = builder
                .comment("Render placing particles using the vanilla particle engine instead of ticking them manually.")
                .define("renderAsVanillaParticles", true);

        SPEC = builder.build();
    }

    private ClientConfig() {
    }

    public static void sync() {
        AGoodPlace.USE_SIMPLE_RENDERER = USE_SIMPLE_RENDERER.get();
        AGoodPlace.RENDER_AS_VANILLA_PARTICLES = RENDER_AS_VANILLA_PARTICLES.get();
    }
}
