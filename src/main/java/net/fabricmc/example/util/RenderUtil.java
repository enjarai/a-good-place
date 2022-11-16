package net.fabricmc.example.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class RenderUtil {
    public static void markBlockForRender(BlockPos pos) {
        BlockPos bp1, bp2;
        bp1 = pos.add(1, 1, 1);
        bp2 = pos.add(-1, -1, -1);

        MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(
                bp1.getX(), bp1.getY(), bp1.getZ(),
                bp2.getX(), bp2.getY(), bp2.getZ());
    }
}
