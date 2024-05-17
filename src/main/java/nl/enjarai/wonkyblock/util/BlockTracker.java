package nl.enjarai.wonkyblock.util;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public class BlockTracker {
    private final RendererImplementation renderer;
    @NotNull
    private Set<BlockPos> map = Set.of(); //replace each time so its thread safe. Supposedly faster than a concurrent set

    public BlockTracker(RendererImplementation renderer) {
        this.renderer = renderer;
    }

    public boolean add(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>(map);
        var success = list.add(pos);
        if (success) {
            map = Set.of(list.toArray(new BlockPos[0]));
        }
        return success;
    }

    public boolean contains(BlockPos pos) {
        return map.contains(pos);
    }

    public boolean remove(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>(map);
        var success = list.remove(pos);
        if (success) {
            map = Set.of(list.toArray(new BlockPos[0]));
            renderer.markBlockForRender(pos);
        }
        return success;
    }

    public void clear() {
        map = Set.of();
    }
}
