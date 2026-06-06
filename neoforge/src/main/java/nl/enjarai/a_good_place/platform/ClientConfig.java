package nl.enjarai.a_good_place.platform;

import net.neoforged.neoforge.common.ModConfigSpec;
import nl.enjarai.a_good_place.AGoodPlace;

public final class ClientConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue RENDER_AS_VANILLA_PARTICLES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        RENDER_AS_VANILLA_PARTICLES = builder
                .comment("Render placing particles using the vanilla particle engine instead of ticking them manually.")
                .define("renderAsVanillaParticles", true);

        SPEC = builder.build();
    }

    private ClientConfig() {
    }

    public static void sync() {
        AGoodPlace.RENDER_AS_VANILLA_PARTICLES = RENDER_AS_VANILLA_PARTICLES.get();
    }
}
