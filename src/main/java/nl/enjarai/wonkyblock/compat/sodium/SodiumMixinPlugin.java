package nl.enjarai.wonkyblock.compat.sodium;

import nl.enjarai.wonkyblock.compat.CompatMixinPlugin;

import java.util.Set;

public class SodiumMixinPlugin implements CompatMixinPlugin {
    @Override
    public Set<String> getRequiredMods() {
        return Set.of("sodium");
    }
}
