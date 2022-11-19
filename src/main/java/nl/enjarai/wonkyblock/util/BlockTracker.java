package nl.enjarai.wonkyblock.util;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class BlockTracker extends HashSet<BlockPos> {
    private final RendererImplementation renderer;

    public BlockTracker(RendererImplementation renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean remove(Object o) {
        var removed = super.remove(o);
        if (removed) {
            renderer.markBlockForRender((BlockPos) o);
        }
        return removed;
    }

    @Override
    public void clear() {
        var stream = stream().toList();
        super.clear();
        stream.forEach(renderer::markBlockForRender);
    }
}
